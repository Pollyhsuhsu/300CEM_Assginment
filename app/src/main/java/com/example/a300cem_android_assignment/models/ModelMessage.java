package com.example.a300cem_android_assignment.models;

import com.example.a300cem_android_assignment.models.ModelMemberData;

public class ModelMessage {
    private String text; // message body
    private ModelMemberData modelMemberData; // data of the user that sent this message
    private boolean belongsToCurrentUser; // is this message sent by us?

    public ModelMessage(String text, ModelMemberData modelMemberData, boolean belongsToCurrentUser) {
        this.text = text;
        this.modelMemberData = modelMemberData;
        this.belongsToCurrentUser = belongsToCurrentUser;
    }

    public String getText() {
        return text;
    }

    public ModelMemberData getModelMemberData() {
        return modelMemberData;
    }

    public boolean isBelongsToCurrentUser() {
        return belongsToCurrentUser;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setModelMemberData(ModelMemberData modelMemberData) {
        this.modelMemberData = modelMemberData;
    }

    public void setBelongsToCurrentUser(boolean belongsToCurrentUser) {
        this.belongsToCurrentUser = belongsToCurrentUser;
    }
}
