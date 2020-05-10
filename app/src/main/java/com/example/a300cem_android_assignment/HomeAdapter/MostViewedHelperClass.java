package com.example.a300cem_android_assignment.HomeAdapter;

public class MostViewedHelperClass {
    String image;
    String title, description;
    int id,participants;

    public MostViewedHelperClass(String image, String title, String description, int participants,int id) {
        this.id = id;
        this.image = image;
        this.title = title;
        this.description = description;
        this.participants = participants;
    }

    public String getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getParticipants() {
        return participants;
    }

    public void setParticipants(int participants) {
        this.participants = participants;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

