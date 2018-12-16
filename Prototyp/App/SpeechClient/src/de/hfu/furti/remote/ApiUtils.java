package de.hfu.furti.remote;

public class ApiUtils {

    public static final String BASE_URL = "http://192.52.33.31:3000/api/users/login/";

    public static UserService getUserService() {
        return  RetrofitClient.getClient(BASE_URL).create(UserService.class);
    }
}
