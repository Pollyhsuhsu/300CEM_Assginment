package com.example.a300cem_android_assignment.Adapter;

public class MemberData {
    private String name;
    //private String color;
    private int u_id;

    public MemberData(String name, int u_id) {
        this.name = name;
       // this.color = color;
        this.u_id = u_id;
    }

    public MemberData() {
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

//    public String getColor() {
//        return color;
//    }

//    public void setColor(String color) {
//        this.color = color;
//    }

    public int getU_id() {
        return u_id;
    }

    public void setU_id(int u_id) {
        this.u_id = u_id;
    }

    @Override
    public String toString() {
        return "MemberData{" +
                "name='" + name + '\'' +
                ", u_id=" + u_id +
                '}';
    }
}
