package com.mit.notepad.models;

import com.mit.notepad.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class Todo {
    public long id;
    /** Linked note id, or 0 for a standalone task. */
    public long noteId;
    public String title = "";
    public String description = "";
    public boolean completed;
    /** Due date-time in epoch millis, 0 if none. */
    public long dueAt;
    public boolean reminderEnabled;
    public String repeatType = Constants.REPEAT_NONE;
    public int repeatInterval = 1;
    public long createdAt;
    public long completedAt;
    public List<Tag> tags = new ArrayList<>();

    public boolean isLinkedToNote() {
        return noteId > 0;
    }

    public boolean isOverdue() {
        return !completed && dueAt > 0 && dueAt < System.currentTimeMillis();
    }
}
