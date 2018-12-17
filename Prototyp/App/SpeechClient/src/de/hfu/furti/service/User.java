package de.hfu.furti.service;

public class User {

    private String id;
    private int ttl;
    private String created;
    private int userId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public int getTtl() {
        return ttl;
    }

    public void setTtl(int ttl) {
        this.ttl = ttl;
    }


    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }


    public int getuserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
