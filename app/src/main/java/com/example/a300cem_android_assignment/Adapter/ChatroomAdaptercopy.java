package com.example.a300cem_android_assignment.Adapter;

public class ChatroomAdaptercopy {
    int chatroom_id;
    String chartroom_name, chatroom_desc, created_at;

    public ChatroomAdaptercopy(int chatroom_id, String chartroom_name, String chatroom_desc, String created_at) {
        this.chatroom_id = chatroom_id;
        this.chartroom_name = chartroom_name;
        this.chatroom_desc = chatroom_desc;
        this.created_at = created_at;

    }

    public int getChatroom_id() {
        return chatroom_id;
    }

    public String getChartroom_name() {
        return chartroom_name;
    }

    public String getChatroom_desc() {
        return chatroom_desc;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setChatroom_id(int chatroom_id) {
        this.chatroom_id = chatroom_id;
    }

    public void setChartroom_name(String chartroom_name) {
        this.chartroom_name = chartroom_name;
    }

    public void setChatroom_desc(String chatroom_desc) {
        this.chatroom_desc = chatroom_desc;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    @Override
    public String toString() {
        return "ChatroomAdapter{" +
                "chatroom_id=" + chatroom_id +
                ", chartroom_name='" + chartroom_name + '\'' +
                ", chatroom_desc='" + chatroom_desc + '\'' +
                ", created_at='" + created_at + '\'' +
                '}';
    }
}
