package de.hfu.furti.login;

import de.hfu.furti.service.ProfileService;
import de.hfu.furti.service.UserService;

public class ApiUtils {

    public static final String BASE_URL = "http://192.52.33.31:3000/api/users/";
    public static final String BASE_URL1 = "http://192.52.33.31:3000/api/profiles/";


    public static UserService getUserService() {
        return  RetrofitClient.getClient(BASE_URL).create(UserService.class);
    }

    public static ProfileService getProfileService() {
        return  RetrofitClient.getClient(BASE_URL1).create(ProfileService.class);
    }
}