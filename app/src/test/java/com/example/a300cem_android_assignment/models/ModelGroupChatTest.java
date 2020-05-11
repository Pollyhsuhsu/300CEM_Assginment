package com.example.a300cem_android_assignment.models;

import org.junit.Test;

import static org.junit.Assert.*;

public class ModelGroupChatTest {
    String message = "Test";
    String sender_name = "Test";
    String timestamp = "2020-11-11";
    String type = "file";
    int sender_id = 1;
    ModelGroupChat mg = new ModelGroupChat(message,sender_name, timestamp, type, sender_id);

    @Test
    public void getMessage() {
        assertEquals(message, mg.getMessage());
    }

    @Test
    public void setMessage() {
        mg.setMessage(this.message);
        assertEquals(this.message, mg.getMessage());
    }

    @Test
    public void getSender_name() {
        assertEquals(sender_name, mg.getSender_name());
    }

    @Test
    public void setSender_name() {
        mg.setMessage(this.sender_name);
        assertEquals(this.sender_name, mg.getSender_name());
    }

    @Test
    public void getTimestamp() {
        assertEquals(timestamp, mg.getTimestamp());
    }

    @Test
    public void setTimestamp() {
        mg.setTimestamp(this.timestamp);
        assertEquals(this.timestamp, mg.getTimestamp());
    }

    @Test
    public void getType() {
        assertEquals(type, mg.getType());
    }

    @Test
    public void setType() {
        mg.setType(this.type);
        assertEquals(this.type, mg.getType());
    }

    @Test
    public void getSender_id() {
        assertEquals(sender_id, mg.getSender_id());
    }

    @Test
    public void setSender_id() {
        mg.setSender_id(this.sender_id);
        assertEquals(this.sender_id, mg.getSender_id());
    }

    @Test
    public void testToString() {

        /*
        this test checks that the String at least is
        outputting "u_id=" followed by the first variable
        but the first variable may incorrect 1's and 0's
        */
        String groupchat = mg.toString();


        assertFalse(new ModelGroupChat().toString().contains("@"));

        assertEquals("ModelGroupChat{" +
                "message='" + message + '\'' +
                ", sender_name='" + sender_name + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", type='" + type + '\'' +
                ", sender_id=" + sender_id +
                '}', groupchat.toString());
    }
}