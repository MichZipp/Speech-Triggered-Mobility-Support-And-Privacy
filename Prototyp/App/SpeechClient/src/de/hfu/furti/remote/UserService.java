package de.hfu.furti.remote;

import de.hfu.furti.model.ResObj;
import de.hfu.furti.service.Login;
import de.hfu.furti.service.User;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UserService {

//    @GET("login/{id}/{token}")
//    Call<ResObj> login(@Path("id") String id, @Path("token") String token);

    @POST("login")
    Call<ResObj> login(@Body Login login);

    @GET("access_token")
    Call<ResponseBody> getToken(@Header("Authorization")String access_token);

}
