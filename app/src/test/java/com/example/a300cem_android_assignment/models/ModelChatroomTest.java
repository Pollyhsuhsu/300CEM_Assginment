package com.example.a300cem_android_assignment.models;

import org.junit.Test;

import static org.junit.Assert.*;

public class ModelChatroomTest {
    int chatroom_id = 1;
    int created_by = 1;
    String chartroom_name = "Test";
    String chatroom_icon = "Test";
    String chatroom_desc = "Test";
    String created_at = "2020-11-11";
    double longitude = 0.1;
    double latitude = 0.1;
    double distance = 0.1;
    ModelChatroom mc = new ModelChatroom(chatroom_id,created_by,chartroom_name,chatroom_icon,chatroom_desc,created_at,longitude,latitude,distance);
    @Test
    public void getDistance() {
        assertEquals(distance, mc.getDistance(),0.1);
    }

    @Test
    public void setDistance() {
        mc.setDistance(this.distance);
        assertEquals(this.distance, mc.getDistance(),0.1);
    }

    @Test
    public void getChatroom_id() {
        assertEquals(chatroom_id, mc.getChatroom_id());
    }

    @Test
    public void setChatroom_id() {
        mc.setChatroom_id(this.chatroom_id);
        assertEquals(this.chatroom_id, mc.getChatroom_id());
    }

    @Test
    public void getCreated_by() {
        assertEquals(created_by, mc.getCreated_by());
    }

    @Test
    public void setCreated_by() {
        mc.setCreated_by(this.created_by);
        assertEquals(this.created_by, mc.getCreated_by());
    }

    @Test
    public void getChartroom_name() {
        assertEquals(chartroom_name, mc.getChartroom_name());
    }

    @Test
    public void setChartroom_name() {
        mc.setChartroom_name(this.chartroom_name);
        assertEquals(this.chartroom_name, mc.getChartroom_name());
    }

    @Test
    public void getChatroom_icon() {
        assertEquals(chatroom_icon, mc.getChatroom_icon());
    }

    @Test
    public void setChatroom_icon() {
        mc.setChatroom_icon(this.chatroom_icon);
        assertEquals(this.chatroom_icon, mc.getChatroom_icon());
    }

    @Test
    public void getChatroom_desc() {
        assertEquals(chatroom_desc, mc.getChatroom_desc());
    }

    @Test
    public void setChatroom_desc() {
        mc.setChatroom_desc(this.chatroom_desc);
        assertEquals(this.chatroom_desc, mc.getChatroom_desc());
    }

    @Test
    public void getCreated_at() {
        assertEquals(created_at, mc.getCreated_at());
    }

    @Test
    public void setCreated_at() {
        mc.setCreated_at(this.created_at);
        assertEquals(this.created_at, mc.getCreated_at());
    }

    @Test
    public void getLongitude() {
        assertEquals(longitude, mc.getLongitude(),0.1);
    }

    @Test
    public void setLongitude() {
        mc.setLongitude(this.longitude);
        assertEquals(this.longitude, mc.getLongitude(),0.1);
    }

    @Test
    public void getLatitude() {
        assertEquals(latitude, mc.getLatitude(),0.1);
    }

    @Test
    public void setLatitude() {
        mc.setLatitude(this.latitude);
        assertEquals(this.latitude, mc.getLatitude(),0.1);
    }

    @Test
    public void testToString() {
         /*
        this test checks that the String at least is
        outputting "u_id=" followed by the first variable
        but the first variable may incorrect 1's and 0's
        */
        String currentChatroomString = mc.toString();
        assertTrue(currentChatroomString.contains("chatroom_id=" + chatroom_id));


        assertFalse(new ModelChatroom().toString().contains("@"));

        assertEquals("ModelChatroom{" +
                "chatroom_id=" + chatroom_id +
                ", created_by=" + created_by +
                ", chartroom_name='" + chartroom_name + '\'' +
                ", chatroom_icon='" + chatroom_icon + '\'' +
                ", chatroom_desc='" + chatroom_desc + '\'' +
                ", created_at='" + created_at + '\'' +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                '}', currentChatroomString.toString());
    }
}