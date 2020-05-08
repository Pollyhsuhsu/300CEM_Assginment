package com.example.a300cem_android_assignment.models;


import java.io.Serializable;

public class ModelUser implements Serializable {
    private int u_id;
    private String username,password,email,user_image,created_at,updated_at;

    public ModelUser() { }

    public ModelUser(int u_id, String username, String password, String email, String user_image, String created_at, String updated_at) {
        this.u_id = u_id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.user_image = user_image;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public int getU_id() {
        return u_id;
    }

    public void setU_id(int u_id) {
        this.u_id = u_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUser_image() {
        return user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    @Override
    public String toString() {
        return "ModelUser{" +
                "u_id=" + u_id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", user_image='" + user_image + '\'' +
                ", created_at='" + created_at + '\'' +
                ", updated_at='" + updated_at + '\'' +
                '}';
    }
}
