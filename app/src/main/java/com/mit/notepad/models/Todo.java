// Sets the package name to com.mit.notepad.models so this class belongs to the app module namespace.
package com.mit.notepad.models;

// Imports com.mit.notepad.utils.Constants so this file can use that type by its short name.
import com.mit.notepad.utils.Constants;

// Imports java.util.ArrayList so this file can use that type by its short name.
import java.util.ArrayList;
// Imports java.util.List so this file can use that type by its short name.
import java.util.List;

// Declares class Todo to group related state and behavior.
public class Todo {
    // Declares id for use by the surrounding method or class.
    public long id;
    /** Linked note id, or 0 for a standalone task. */
    // Declares noteId for use by the surrounding method or class.
    public long noteId;
    // Initializes title with the text or query fragment shown here.
    public String title = "";
    // Initializes description with the text or query fragment shown here.
    public String description = "";
    // Declares completed for use by the surrounding method or class.
    public boolean completed;
    /** Due date-time in epoch millis, 0 if none. */
    // Declares dueAt for use by the surrounding method or class.
    public long dueAt;
    // Declares reminderEnabled for use by the surrounding method or class.
    public boolean reminderEnabled;
    // Initializes repeatType with Constants.REPEAT_NONE so later statements can use the computed value.
    public String repeatType = Constants.REPEAT_NONE;
    // Initializes repeatInterval with 1 so later statements can use the computed value.
    public int repeatInterval = 1;
    // Declares createdAt for use by the surrounding method or class.
    public long createdAt;
    // Declares completedAt for use by the surrounding method or class.
    public long completedAt;
    // Initializes tags with new ArrayList<>() so later statements can use the computed value.
    public List<Tag> tags = new ArrayList<>();

    // Checks whether linked to note.
    public boolean isLinkedToNote() {
        // Returns noteId > 0 to the caller.
        return noteId > 0;
    // Closes the current method, control-flow, or type block.
    }

    // Checks whether overdue.
    public boolean isOverdue() {
        // Returns !completed && dueAt > 0 && dueAt < System.currentTimeMillis() to the caller.
        return !completed && dueAt > 0 && dueAt < System.currentTimeMillis();
    // Closes the current method, control-flow, or type block.
    }
// Closes the current method, control-flow, or type block.
}
