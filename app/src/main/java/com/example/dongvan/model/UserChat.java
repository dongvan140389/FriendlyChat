package com.example.dongvan.model;

/**
 * Created by VoNga on 26-Feb-17.
 */

public class UserChat {
    private String name;
    private String email;
    private String UID;

    public UserChat() {
    }

    public UserChat(String name, String email, String UID) {
        this.name = name;
        this.email = email;
        this.UID = UID;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
