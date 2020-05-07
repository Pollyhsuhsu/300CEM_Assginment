package com.example.a300cem_android_assignment.models;

public class ModelGroupChat {
    String message, sender_name, timestamp, type;
    int sender_id;

    public ModelGroupChat() {
    }

    public ModelGroupChat(String message, String sender_name, String timestamp, String type, int sender_id) {
        this.message = message;
        this.sender_name = sender_name;
        this.timestamp = timestamp;
        this.type = type;
        this.sender_id = sender_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender_name() {
        return sender_name;
    }

    public void setSender_name(String sender_name) {
        this.sender_name = sender_name;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getSender_id() {
        return sender_id;
    }

    public void setSender_id(int sender_id) {
        this.sender_id = sender_id;
    }

    @Override
    public String toString() {
        return "ModelGroupChat{" +
                "message='" + message + '\'' +
                ", sender_name='" + sender_name + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", type='" + type + '\'' +
                ", sender_id=" + sender_id +
                '}';
    }
}
