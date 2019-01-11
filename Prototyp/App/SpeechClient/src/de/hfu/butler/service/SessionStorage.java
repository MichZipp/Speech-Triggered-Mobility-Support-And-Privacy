package de.hfu.butler.service;

public class SessionStorage {
    private String user_id;
    private String session_token;
    private int profile_type = 0;
    private String profile_id;

    // static variable single_instance of type Singleton
    private static SessionStorage single_instance = null;

    // variable of type String
    public String s;

    // static method to create instance of Singleton class
    public static SessionStorage getInstance()
    {
        if (single_instance == null)
            single_instance = new SessionStorage();

        return single_instance;
    }

    public String getUserId() {
        return user_id;
    }

    public void setUserId(String user_id) {
        this.user_id = user_id;
    }

    public String getSessionToken() {
        return session_token;
    }

    public void setSessionToken(String session_token) {
        this.session_token = session_token;
    }

    public int getProfileType() {
        return profile_type;
    }

    public void setProfileType(int profile_type) {
        this.profile_type = profile_type;
    }

    public String getProfileId() {
        return profile_id;
    }

    public void setProfileId(String profile_id) {
        this.profile_id = profile_id;
    }
}
