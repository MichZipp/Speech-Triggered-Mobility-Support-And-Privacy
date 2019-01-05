package de.hfu.furti.service;

import de.hfu.furti.login.Login;
import de.hfu.furti.login.ResObj;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface UserService {

    @POST("login")
    Call<ResObj> login(@Body Login login);

    @GET("access_token")
    Call<ResponseBody> getToken(@Header("Authorization") String access_token);

}
