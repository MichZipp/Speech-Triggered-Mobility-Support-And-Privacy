package de.hfu.furti.service;

public class SessionStorage {
    private int user_id;
    private String session_token;
    private int active_profile;

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

    public int getUserId() {
        return user_id;
    }

    public void setUserId(int user_id) {
        this.user_id = user_id;
    }

    public String getSessionToken() {
        return session_token;
    }

    public void setSessionToken(String session_token) {
        this.session_token = session_token;
    }

    public int getActiveProfile() {
        return active_profile;
    }

    public void setActiveProfile(int active_profile) {
        this.active_profile = active_profile;
    }
}
