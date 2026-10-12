package com.mit.notepad.models;

import com.mit.notepad.utils.Constants;

public class Alarm {
    public long id;
    /** Exactly one of noteId / todoId is greater than zero. */
    public long noteId;
    public long todoId;
    public long scheduledAt;
    public boolean enabled;
    public String repeatType = Constants.REPEAT_NONE;
    public int repeatInterval = 1;
    public long createdAt;
    /** Title of the note/task that owns this alarm (joined for display). */
    public String ownerTitle = "";
    public boolean isTask;
}
