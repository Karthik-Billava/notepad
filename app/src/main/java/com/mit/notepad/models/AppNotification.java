package com.mit.notepad.models;

public class AppNotification {
    public long id;
    public long noteId;
    public long todoId;
    public String title = "";
    public String message = "";
    public long timestamp;
    public String category = "";
    public boolean read;
}
