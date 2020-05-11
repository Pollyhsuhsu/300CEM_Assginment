package com.example.a300cem_android_assignment.notifications;

import com.example.a300cem_android_assignment.models.ModelChatroom;

public class Data {
    private String user, body, title, sent,chatroom ;
    private Integer icon;

    public Data(String user, String body, String title, String sent, Integer icon, String chatroom) {
        this.user = user;
        this.body = body;
        this.title = title;
        this.sent = sent;
        this.icon = icon;
        this.chatroom = chatroom;
    }

    public Data() {
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSent() {
        return sent;
    }

    public void setSent(String sent) {
        this.sent = sent;
    }

    public Integer getIcon() {
        return icon;
    }

    public void setIcon(Integer icon) {
        this.icon = icon;
    }

    public String getChatroom() {
        return chatroom;
    }

    public void setChatroom(String chatroom) {
        this.chatroom = chatroom;
    }
}
