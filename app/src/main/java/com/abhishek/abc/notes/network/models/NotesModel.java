package com.abhishek.abc.notes.network.models;

import com.google.gson.annotations.SerializedName;

public class NotesModel extends BaseResponse{

    @SerializedName("id")
    int id;
    @SerializedName("name")
    String note;
    @SerializedName("timestamp")
    String timestamp;

    public int getId() {
        return id;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
