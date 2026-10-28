// Sets the package name to com.mit.notepad.models so this class belongs to the app module namespace.
package com.mit.notepad.models;

// Imports com.mit.notepad.utils.Constants so this file can use that type by its short name.
import com.mit.notepad.utils.Constants;

// Declares class Alarm to group related state and behavior.
public class Alarm {
    // Declares id for use by the surrounding method or class.
    public long id;
    /** Exactly one of noteId / todoId is greater than zero. */
    // Declares noteId for use by the surrounding method or class.
    public long noteId;
    // Declares todoId for use by the surrounding method or class.
    public long todoId;
    // Declares scheduledAt for use by the surrounding method or class.
    public long scheduledAt;
    // Declares enabled for use by the surrounding method or class.
    public boolean enabled;
    // Initializes repeatType with Constants.REPEAT_NONE so later statements can use the computed value.
    public String repeatType = Constants.REPEAT_NONE;
    // Initializes repeatInterval with 1 so later statements can use the computed value.
    public int repeatInterval = 1;
    // Declares createdAt for use by the surrounding method or class.
    public long createdAt;
    /** Title of the note/task that owns this alarm (joined for display). */
    // Initializes ownerTitle with the text or query fragment shown here.
    public String ownerTitle = "";
    // Declares isTask for use by the surrounding method or class.
    public boolean isTask;
// Closes the current method, control-flow, or type block.
}
