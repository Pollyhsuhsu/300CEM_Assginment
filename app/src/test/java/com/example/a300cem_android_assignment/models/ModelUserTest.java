package com.example.a300cem_android_assignment.models;

import org.junit.Test;

import static org.junit.Assert.*;

public class ModelUserTest {

    private int u_id = 1;
    private String username = "Test";
    private String password = "Test";
    private String email = "Test@gmail.com";
    private String user_image = "user_image";
    private String created_at = "2020-11-11";
    private String updated_at = "2020-11-11";
    private ModelUser currentUser = new ModelUser(u_id,username,password,email,user_image,created_at,updated_at);
    @Test
    public void getU_id() {
        assertEquals(u_id, currentUser.getU_id());
    }

    @Test
    public void getUsername() {
        assertEquals(username, currentUser.getUsername());
    }

    @Test
    public void getPassword() {
        assertEquals(password, currentUser.getPassword());
    }

    @Test
    public void getEmail() {
        assertEquals(email, currentUser.getEmail());
    }

    @Test
    public void getCreated_at() {
        assertEquals(created_at, currentUser.getCreated_at());
    }

    @Test
    public void getUpdated_at() {
        assertEquals(updated_at, currentUser.getUpdated_at());
    }

    @Test
    public void setU_id() {
        currentUser.setU_id(this.u_id);
        assertEquals(this.u_id, currentUser.getU_id());
    }

    @Test
    public void setUsername() {
        currentUser.setUsername(this.username);
        assertEquals(this.username, currentUser.getUsername());
    }

    @Test
    public void setPassword() {
        currentUser.setPassword(this.password);
        assertEquals(this.password, currentUser.getPassword());
    }

    @Test
    public void setEmail() {
        currentUser.setEmail(this.email);
        assertEquals(this.email, currentUser.getEmail());
    }

    @Test
    public void setCreated_at() {
        currentUser.setCreated_at(this.created_at);
        assertEquals(this.created_at, currentUser.getCreated_at());
    }

    @Test
    public void setUpdated_at() {
        currentUser.setUpdated_at(this.user_image);
        assertEquals(this.user_image, currentUser.getUser_image());
    }

    @Test
    public void getUser_image() {
        assertEquals(user_image, currentUser.getUser_image());
    }

    @Test
    public void setUser_image() {
        currentUser.setUpdated_at(this.updated_at);
        assertEquals(this.updated_at, currentUser.getUpdated_at());
    }
    @Test
    public void testToString() {


        /*
        this test checks that the String at least is
        outputting "u_id=" followed by the first variable
        but the first variable may incorrect 1's and 0's
        */
        String currentUserString = currentUser.toString();
        assertTrue(currentUserString.contains("u_id=" + u_id));


        assertFalse(new ModelUser().toString().contains("@"));

        assertEquals("ModelUser{" +
                    "u_id=" + u_id +
                    ", username='" + username + '\'' +
                    ", password='" + password + '\'' +
                    ", email='" + email + '\'' +
                    ", user_image='" + user_image + '\'' +
                    ", created_at='" + created_at + '\'' +
                    ", updated_at='" + updated_at + '\'' +
                    '}', currentUser.toString());
    }
}