package com.esprit.goga.bean;

import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

/**
 * Created by khmai on 08/03/2018.
 */

public class User {


    @SerializedName("id")
    String id;
    @SerializedName("username")
    String username;
    @SerializedName("email")
    String email;
    @SerializedName("token")
    String token;
    @SerializedName("profilepicture")
    String profilePicture;
    @SerializedName("firstname")
    String firtsname;
    @SerializedName("lastname")
    String lastname;


    public User(JSONObject jsonObject){
        try {
            this.id = jsonObject.getString("id");
            this.username = jsonObject.getString("username");
            this.profilePicture = "https://goga-api.herokuapp.com/api/attachments/images/download/"+jsonObject.getString("profilepicture");

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public User() {
    }

    public String getEmail() {
        return email;
    }

    public String getFirtsname() {
        return firtsname;
    }

    public void setFirtsname(String firtsname) {
        this.firtsname = firtsname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User(String id, String username, String profilePicture) {
        this.id = id;
        this.username = username;
        this.profilePicture = profilePicture;
    }

    public User(String username, String profilePicture) {
        this.username = username;
        this.profilePicture = profilePicture;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfilePicture() {
        return "https://goga-api.herokuapp.com/api/attachments/profilepicture/download/"+profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
}
