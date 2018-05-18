package calificado.guerra.tecsup.edu.pe.labcalificado3.services;

import java.util.List;
import java.util.Map;

import calificado.guerra.tecsup.edu.pe.labcalificado3.models.Denuncia;
import calificado.guerra.tecsup.edu.pe.labcalificado3.models.Usuario;
import calificado.guerra.tecsup.edu.pe.labcalificado3.response.ResponseMessage;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Alumno on 11/05/2018.
 */

public interface ApiService {


    String API_BASE_URL = "https://laravel-55-android-gal05.c9users.io/";

    @GET("api/denuncia/usuarios")
    Call<List<Usuario>> getUsuarios();





    @FormUrlEncoded
    @POST("/api/denuncia/usuarios")
    Call<ResponseMessage> createUsuario(@Field("password") String password,
                                        @Field("dni") String dni,
                                        @Field("nombre") String nombre,
                                        @Field("tipo") int tipo);








/*    @Multipart
    @POST("/api/v1/productos")
    Call<ResponseMessage> createProductoWithImage(
            @Part("nombre") RequestBody nombre,
            @Part("precio") RequestBody precio,
            @Part("detalles") RequestBody detalles,
            @Part MultipartBody.Part imagen
    );
*/

    @GET("api/denuncia/denuncias")
    Call<List<Denuncia>> getDenuncias();

    @GET("/api/denuncia/denuncias/{id_usuario}")
    Call<List<Denuncia>> denuncias_x_usuario(@Path("id_usuario") int id_usuario);



    @FormUrlEncoded
    @POST("/api/denuncia/denuncias")
    Call<ResponseMessage> createDenunciaSinPhoto(@Field("usuario_id") int usuario_id,
                                                 @Field("titulo") String precio,
                                                 @Field("ubicacion") String detalles,
                                                 @Field("descripcion") String descripcion,
                                                 @Field("estado") String ubicacion);


    @Multipart
    @POST("/api/denuncia/denuncias")
    Call<ResponseMessage> createDenunciaWithImage(
            @Part("usuario_id") RequestBody usuario_id,
            @Part("titulo") RequestBody titulo,
            @Part("ubicacion") RequestBody ubicacion,
            @Part("descripcion") RequestBody descripcion,
            @Part("estado") RequestBody estado,
            @Part MultipartBody.Part foto
    );



    /*@GET("/api/denuncia/denuncias/{otp}")
    Call<List<Denuncia>> denuncias_por_usuario(@Path("otp") int otp);
*/




}
