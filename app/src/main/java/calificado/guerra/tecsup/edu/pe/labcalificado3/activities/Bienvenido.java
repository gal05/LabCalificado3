package calificado.guerra.tecsup.edu.pe.labcalificado3.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import calificado.guerra.tecsup.edu.pe.labcalificado3.R;
import calificado.guerra.tecsup.edu.pe.labcalificado3.models.Usuario;
import calificado.guerra.tecsup.edu.pe.labcalificado3.services.ApiService;
import calificado.guerra.tecsup.edu.pe.labcalificado3.services.ApiServiceGenerator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Bienvenido extends AppCompatActivity {

    static TextView ingreseUsuario;
    static TextView ingresePassword;
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bienvenido);


        ingreseUsuario=(TextView)findViewById(R.id.ingrese_usuario);
        ingresePassword=(TextView)findViewById(R.id.ingrese_password);

    }

    public void ingresar(View view){

        final String The_usuario=ingreseUsuario.getText().toString();
         final String The_password=ingresePassword.getText().toString();


        ApiService service = ApiServiceGenerator.createService(ApiService.class);

        Call<List<Usuario>> call = service.getUsuarios();

        call.enqueue(new Callback<List<Usuario>>() {
            @Override
            public void onResponse(Call<List<Usuario>> call, Response<List<Usuario>> response) {
                try {

                    int statusCode = response.code();
                    Log.d(TAG, "HTTP status code: " + statusCode);

                    if (response.isSuccessful()) {

                        List<Usuario> usuarios = response.body();
                        Log.d(TAG, "usuarios: " + usuarios);

                        for (Usuario usuario:usuarios){
                            if(usuario.getNombre()== The_usuario && usuario.getPassword()== The_password){

                            }



                            Log.d("PRODUCTO : ",usuario.getNombre());
                        }

                        /*ProductosAdapter adapter = (ProductosAdapter) productosList.getAdapter();
                        adapter.setProductos(productos);
                        adapter.notifyDataSetChanged();*/

                    } else {
                        Log.e(TAG, "onError: " + response.errorBody().string());
                        throw new Exception("Error en el servicio");
                    }

                } catch (Throwable t) {
                    try {
                        Log.e(TAG, "onThrowable: " + t.toString(), t);
                        Toast.makeText(Bienvenido.this, t.getMessage(), Toast.LENGTH_LONG).show();
                    }catch (Throwable x){}
                }
            }

            @Override
            public void onFailure(Call<List<Usuario>> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.toString());
                Toast.makeText(Bienvenido.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }

        });

       // Toast.makeText(this, "esto " + usuario+ "  y esto "+password, Toast.LENGTH_SHORT).show();

    }
    public  void registrar(View view){

    }


}
