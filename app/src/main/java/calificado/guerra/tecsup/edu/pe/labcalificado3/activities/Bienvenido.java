package calificado.guerra.tecsup.edu.pe.labcalificado3.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Currency;
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
    public boolean verdad;
    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int REGISTER_FORM_REQUEST=100;


    public static String The_usuario;
    public static String The_password;

    public  static  int The_codigo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bienvenido);


        ingreseUsuario=(TextView)findViewById(R.id.ingrese_usuario);
        ingresePassword=(TextView)findViewById(R.id.ingrese_password);

    }

    public void ingresar(View view){

        The_usuario=ingreseUsuario.getText().toString().trim();
        The_password=ingresePassword.getText().toString().trim();

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
                            if(usuario.getNombre().equals(The_usuario) && usuario.getPassword().equals(The_password)){
                                The_codigo= usuario.getId();
                                 verdad=true;

                            }
                            Log.d("PRODUCTO : ",usuario.getNombre()+"  "+ usuario.getPassword());
                        }

                        if (verdad==true) {
                            Intent intent = new Intent(Bienvenido.this, ListaDenuncia.class);

                            Bienvenido.this.startActivity(intent);
                            finish();

                        }else{
                            Toast.makeText(Bienvenido.this, "Credenciales no validas"+The_password+" "+The_usuario, Toast.LENGTH_LONG).show();

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
    public  void registrarUser(View view){
        startActivityForResult(new Intent(this,RegistroUsuario.class),REGISTER_FORM_REQUEST);
    }


}
