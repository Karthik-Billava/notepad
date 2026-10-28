// Sets the package name to com.mit.notepad.models so this class belongs to the app module namespace.
package com.mit.notepad.models;

// Declares class AppNotification to group related state and behavior.
public class AppNotification {
    // Declares id for use by the surrounding method or class.
    public long id;
    // Declares noteId for use by the surrounding method or class.
    public long noteId;
    // Declares todoId for use by the surrounding method or class.
    public long todoId;
    // Initializes title with the text or query fragment shown here.
    public String title = "";
    // Initializes message with the text or query fragment shown here.
    public String message = "";
    // Declares timestamp for use by the surrounding method or class.
    public long timestamp;
    // Initializes category with the text or query fragment shown here.
    public String category = "";
    // Declares read for use by the surrounding method or class.
    public boolean read;
// Closes the current method, control-flow, or type block.
}
