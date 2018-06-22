package com.abhishek.abc.notes.network.models;

public class NotesModel {
    int mId;
    String mNote;
    String mTimestamp;

    public int getId() {
        return mId;
    }

    public String getNote() {
        return mNote;
    }

    public void setNote(String note) {
        this.mNote = note;
    }

    public String getTimestamp() {
        return mTimestamp;
    }
}
