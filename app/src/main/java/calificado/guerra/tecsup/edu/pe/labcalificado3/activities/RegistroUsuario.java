package calificado.guerra.tecsup.edu.pe.labcalificado3.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import calificado.guerra.tecsup.edu.pe.labcalificado3.R;
import calificado.guerra.tecsup.edu.pe.labcalificado3.response.ResponseMessage;
import calificado.guerra.tecsup.edu.pe.labcalificado3.services.ApiService;
import calificado.guerra.tecsup.edu.pe.labcalificado3.services.ApiServiceGenerator;
import retrofit2.Call;
import retrofit2.Callback;

public class RegistroUsuario extends AppCompatActivity {

    private static final String TAG=RegistroUsuario.class.getSimpleName();
    static EditText name_user;
    static EditText dni_user;
    static  EditText password1;
    static  EditText password2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_usuario);

        name_user=(EditText)findViewById(R.id.ingrese_nombre_u);
        dni_user=(EditText)findViewById(R.id.ingrese_dni_u);
        password1=(EditText)findViewById(R.id.ingrese_password_u);
        password2=(EditText)findViewById(R.id.ingrese_password_u2);


    }



    public void registrar(View view){
        String user=name_user.getText().toString();
        String dni=dni_user.getText().toString();
        String pass1=password1.getText().toString();
        String pass2=password2.getText().toString();
        int tipo=0;

        if (user.isEmpty()|| dni.isEmpty()||pass1.isEmpty()||pass2.isEmpty()){
            Toast.makeText(this,"Todos los campos son requeridos",Toast.LENGTH_SHORT);
            return;
        }

        ApiService service= ApiServiceGenerator.createService(ApiService.class);

        Call<ResponseMessage> call=null;

        if(!pass1.equals(pass2)){
            Toast.makeText(this,"No coinciden los passwords",Toast.LENGTH_SHORT);
            return;
        }else{
            call=service.createUsuario(pass1,dni,user,tipo);
        }
        call.enqueue(new Callback<ResponseMessage>() {

            @Override
            public void onResponse(Call<ResponseMessage> call, retrofit2.Response<ResponseMessage> response) {
                try {

                    int statusCode = response.code();
                    Log.d(TAG, "HTTP status code: " + statusCode);

                    if (response.isSuccessful()) {

                        ResponseMessage responseMessage = response.body();
                        Log.d(TAG, "responseMessage: " + responseMessage);

                        Toast.makeText(RegistroUsuario.this, responseMessage.getMessage(), Toast.LENGTH_LONG).show();
                        finish();

                    } else {
                        Log.e(TAG, "onError: " + response.errorBody().string());
                        throw new Exception("Error en el servicio");
                    }

                } catch (Throwable t) {
                    try {
                        Log.e(TAG, "onThrowable: " + t.toString(), t);
                        Toast.makeText(RegistroUsuario.this, t.getMessage(), Toast.LENGTH_LONG).show();
                    } catch (Throwable x) {
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseMessage> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.toString());
                Toast.makeText(RegistroUsuario.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }

        });








    }



    /**
     * Permissions handler
     */

    private static final int PERMISSIONS_REQUEST=200;
    private static String[]PERMISSIONS_LIST=new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    private boolean permissionsGranted(){
        for (String permissions:PERMISSIONS_LIST){
            if (ContextCompat.checkSelfPermission(this,permissions)!= PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST: {
                for (int i = 0; i < grantResults.length; i++) {
                    Log.d(TAG, "" + grantResults[i]);
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, PERMISSIONS_LIST[i] + " permiso rechazado", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                Toast.makeText(this, "Permisos concedidos, intente nuevamente.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
