package calificado.guerra.tecsup.edu.pe.labcalificado3.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import calificado.guerra.tecsup.edu.pe.labcalificado3.R;
import calificado.guerra.tecsup.edu.pe.labcalificado3.response.ResponseMessage;
import calificado.guerra.tecsup.edu.pe.labcalificado3.services.ApiService;
import calificado.guerra.tecsup.edu.pe.labcalificado3.services.ApiServiceGenerator;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

public class DenunciaRegister extends AppCompatActivity {

    private Button gps;
    private ImageView imagenPhoto;
    private EditText titulo;
    private EditText descripcion;
    private EditText coordenadas;
    private int usuario_id=Bienvenido.The_codigo;
    private static final String TAG = DenunciaRegister.class.getSimpleName();

    /*
    *GPS
     */
    private LocationManager locationManager;
    private LocationListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_denuncia_register);



        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        imagenPhoto = findViewById(R.id.imagen_preview);
        titulo = findViewById(R.id.titulo_input);
        descripcion = findViewById(R.id.descripcion_input);
        coordenadas = findViewById(R.id.coordenadas_input);
        gps=findViewById(R.id.catchCoordenadas);
        /*
        *GPS
         */
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                coordenadas.setText("Lon: "+location.getLongitude() + "  Lat: " + location.getLatitude());
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };
        configure_button();
    }


    private static final int CAPTURE_IMAGE_REQUEST = 300;

    private Uri mediaFileUri;

    public void takePicture(View view) {
        try {

            if (!permissionsGranted()) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_LIST, PERMISSIONS_REQUEST);
                return;
            }

            // Creando el directorio de imágenes (si no existe)
            File mediaStorageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    throw new Exception("Failed to create directory");
                }
            }

            // Definiendo la ruta destino de la captura (Uri)
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
            mediaFileUri = Uri.fromFile(mediaFile);

            // Iniciando la captura
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mediaFileUri);
            startActivityForResult(intent, CAPTURE_IMAGE_REQUEST);

        } catch (Exception e) {
            Log.e(TAG, e.toString());
            Toast.makeText(this, "Error en captura: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_REQUEST) {
            // Resultado en la captura de la foto
            if (resultCode == RESULT_OK) {
                try {
                    Log.d(TAG, "ResultCode: RESULT_OK");
                    // Toast.makeText(this, "Image saved to: " + mediaFileUri.getPath(), Toast.LENGTH_LONG).show();

                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mediaFileUri);
                    Log.e("guerra","sdasdasdasd"+mediaFileUri);
                    // Reducir la imagen a 800px solo si lo supera
                    bitmap = scaleBitmapDown(bitmap, 800);

                    imagenPhoto.setImageBitmap(bitmap);
                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                    Toast.makeText(this, "Error al procesar imagen: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            } else if (resultCode == RESULT_CANCELED) {
                Log.d(TAG, "ResultCode: RESULT_CANCELED");
            } else {
                Log.d(TAG, "ResultCode: " + resultCode);
            }
        }
    }


    public void callRegister(View view) {

        String titu = titulo.getText().toString();
        String desc = descripcion.getText().toString();
        String coorde = coordenadas.getText().toString();
        String ubica=coordenadas.getText().toString();

        if (titu.isEmpty() || desc.isEmpty()) {
            Toast.makeText(this, "Titulo y Descripcion son campos requeridos", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService service = ApiServiceGenerator.createService(ApiService.class);

        Call<ResponseMessage> call = null;

        if (mediaFileUri == null) {
            // Si no se incluye imagen hacemos un envío POST simple

            call = service.createDenunciaSinPhoto(Bienvenido.The_codigo,titu,ubica, desc, coorde);
        } else {
            // Si se incluye hacemos envió en multiparts

            File file = new File(mediaFileUri.getPath());
            Log.d(TAG, "File: " + file.getPath() + " - exists: " + file.exists());

            // Podemos enviar la imagen con el tamaño original, pero lo mejor será comprimila antes de subir (byteArray)
            // RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file);

            Bitmap bitmap = BitmapFactory.decodeFile(mediaFileUri.getPath());

            // Reducir la imagen a 800px solo si lo supera
            bitmap = scaleBitmapDown(bitmap, 800);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), byteArray);
            MultipartBody.Part imagenPart = MultipartBody.Part.createFormData("foto", file.getName(), requestFile);

            RequestBody usuario_idPart = RequestBody.create(MultipartBody.FORM, String.valueOf(usuario_id));
            RequestBody tituloPart = RequestBody.create(MultipartBody.FORM, titu);
            RequestBody descripcionPart = RequestBody.create(MultipartBody.FORM, desc);
            RequestBody coordenadaPart = RequestBody.create(MultipartBody.FORM, coorde);
            RequestBody estadoPart=RequestBody.create(MultipartBody.FORM,"0");

            call = service.createDenunciaWithImage(usuario_idPart, tituloPart, coordenadaPart,descripcionPart,estadoPart, imagenPart);
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

                        Toast.makeText(DenunciaRegister.this, responseMessage.getMessage(), Toast.LENGTH_LONG).show();
                        finish();

                    } else {
                        Log.e(TAG, "onError: " + response.errorBody().string());
                        throw new Exception("Error en el servicio");
                    }

                } catch (Throwable t) {
                    try {
                        Log.e(TAG, "onThrowable: " + t.toString(), t);
                        Toast.makeText(DenunciaRegister.this, t.getMessage(), Toast.LENGTH_LONG).show();
                    } catch (Throwable x) {
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseMessage> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.toString());
                Toast.makeText(DenunciaRegister.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }

        });
    }
    /**
     * Permissions handler
     */

    private static final int PERMISSIONS_REQUEST = 200;

    private static String[] PERMISSIONS_LIST = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    private boolean permissionsGranted() {
        for (String permission : PERMISSIONS_LIST) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST: {
                for (int i = 0; i < grantResults.length; i++) {
                    Log.d(TAG, "" + grantResults[i]);
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, PERMISSIONS_LIST[i] + " permiso rechazado!", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                Toast.makeText(this, "Permisos concedidos, intente nuevamente.", Toast.LENGTH_LONG).show();
            }
        }
    }

    // Redimensionar una imagen bitmap
    private Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }

    /*
     *GPS
     */

    void configure_button(){
        // first check for permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}
                        , 10);
            }
            return;
        }
        gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //noinspection MissingPermission
                if (ActivityCompat.checkSelfPermission(DenunciaRegister.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(DenunciaRegister.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locationManager.requestLocationUpdates("gps", 5000, 0, listener);
            }
        });
    }






}
