package de.hfu.furti.service;

import de.hfu.furti.profile.AccessToken;
import de.hfu.furti.login.ResObj;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ProfileService {

    @POST("access_token")
    Call<ResObj> access_token(@Header("Authorization") AccessToken access_token);

    @GET("profile")
    Call<ResponseBody> getProfile();

}
