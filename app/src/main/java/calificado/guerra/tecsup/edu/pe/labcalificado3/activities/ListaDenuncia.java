package calificado.guerra.tecsup.edu.pe.labcalificado3.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import calificado.guerra.tecsup.edu.pe.labcalificado3.R;
import calificado.guerra.tecsup.edu.pe.labcalificado3.adapters.DenunciaAdapter;
import calificado.guerra.tecsup.edu.pe.labcalificado3.models.Denuncia;
import calificado.guerra.tecsup.edu.pe.labcalificado3.services.ApiService;
import calificado.guerra.tecsup.edu.pe.labcalificado3.services.ApiServiceGenerator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListaDenuncia extends AppCompatActivity {

    private  static  final  String TAG=ListaDenuncia.class.getSimpleName();

    private RecyclerView denunciasList;

    private static final int REGISTER_FORM_REQUEST=100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_denuncia);

        denunciasList = findViewById(R.id.recyclerview);
        denunciasList.setLayoutManager(new LinearLayoutManager(this));

        denunciasList.setAdapter(new DenunciaAdapter());


        initialize();
    }

    private void initialize() {

        ApiService service = ApiServiceGenerator.createService(ApiService.class);

        Call<List<Denuncia>> call = service.getDenuncias();

        call.enqueue(new Callback<List<Denuncia>>() {
            @Override
            public void onResponse(Call<List<Denuncia>> call, Response<List<Denuncia>> response) {
                try {

                    int statusCode = response.code();
                    Log.d(TAG, "HTTP status code: " + statusCode);

                    if (response.isSuccessful()) {

                        List<Denuncia> denuncias = response.body();
                        Log.d(TAG, "productos: " + denuncias);

                        DenunciaAdapter adapter = (DenunciaAdapter) denunciasList.getAdapter();
                        adapter.setDenuncias(denuncias);
                        adapter.notifyDataSetChanged();

                    } else {
                        Log.e(TAG, "onError: " + response.errorBody().string());
                        throw new Exception("Error en el servicio");
                    }

                } catch (Throwable t) {
                    try {
                        Log.e(TAG, "onThrowable: " + t.toString(), t);
                        Toast.makeText(ListaDenuncia.this, t.getMessage(), Toast.LENGTH_LONG).show();
                    }catch (Throwable x){}
                }
            }

            @Override
            public void onFailure(Call<List<Denuncia>> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.toString());
                Toast.makeText(ListaDenuncia.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }

        });
    }

    public void showRegiste(View view){
        startActivityForResult(new Intent(this, DenunciaRegister.class), REGISTER_FORM_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REGISTER_FORM_REQUEST) {
            // refresh data
            initialize();
        }
    }

}
