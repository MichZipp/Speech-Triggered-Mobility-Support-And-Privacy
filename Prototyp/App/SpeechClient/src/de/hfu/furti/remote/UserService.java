package de.hfu.furti.remote;

import de.hfu.furti.model.ResObj;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface UserService {

    @GET("login/{email}/{password}")
    Call<ResObj> login(@Path("email") String email, @Path("password") String password);

}
