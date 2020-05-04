package com.example.a300cem_android_assignment.models;

import java.io.Serializable;

public class ModelChatroom  implements Serializable {
    int chatroom_id,created_by;
    String chartroom_name, chatroom_icon, chatroom_desc, created_at;
    double longitude,latitude;
    double distance;

    public ModelChatroom() {
    }

    public ModelChatroom(int chatroom_id, int created_by, String chartroom_name, String chatroom_icon, String chatroom_desc, String created_at, double longitude, double latitude, double distance) {
        this.chatroom_id = chatroom_id;
        this.created_by = created_by;
        this.chartroom_name = chartroom_name;
        this.chatroom_icon = chatroom_icon;
        this.chatroom_desc = chatroom_desc;
        this.created_at = created_at;
        this.longitude = longitude;
        this.latitude = latitude;
        this.distance = distance;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getChatroom_id() {
        return chatroom_id;
    }

    public void setChatroom_id(int chatroom_id) {
        this.chatroom_id = chatroom_id;
    }

    public int getCreated_by() {
        return created_by;
    }

    public void setCreated_by(int created_by) {
        this.created_by = created_by;
    }

    public String getChartroom_name() {
        return chartroom_name;
    }

    public void setChartroom_name(String chartroom_name) {
        this.chartroom_name = chartroom_name;
    }

    public String getChatroom_icon() {
        return chatroom_icon;
    }

    public void setChatroom_icon(String chatroom_icon) {
        this.chatroom_icon = chatroom_icon;
    }

    public String getChatroom_desc() {
        return chatroom_desc;
    }

    public void setChatroom_desc(String chatroom_desc) {
        this.chatroom_desc = chatroom_desc;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    @Override
    public String toString() {
        return "ModelChatroom{" +
                "chatroom_id=" + chatroom_id +
                ", created_by=" + created_by +
                ", chartroom_name='" + chartroom_name + '\'' +
                ", chatroom_icon='" + chatroom_icon + '\'' +
                ", chatroom_desc='" + chatroom_desc + '\'' +
                ", created_at='" + created_at + '\'' +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                '}';
    }
}
