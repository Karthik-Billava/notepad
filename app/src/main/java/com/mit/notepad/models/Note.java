// Sets the package name to com.mit.notepad.models so this class belongs to the app module namespace.
package com.mit.notepad.models;

// Imports com.mit.notepad.utils.Constants so this file can use that type by its short name.
import com.mit.notepad.utils.Constants;

// Imports java.util.ArrayList so this file can use that type by its short name.
import java.util.ArrayList;
// Imports java.util.List so this file can use that type by its short name.
import java.util.List;

// Declares class Note to group related state and behavior.
public class Note {
    // Declares id for use by the surrounding method or class.
    public long id;
    // Initializes title with the text or query fragment shown here.
    public String title = "";
    /** Plain text content (used for search and counts). */
    // Initializes content with the text or query fragment shown here.
    public String content = "";
    /** JSON describing bold/italic/underline/link spans, see RichText. */
    // Initializes formatting with the text or query fragment shown here.
    public String formatting = "[]";
    // Initializes noteType with Constants.NOTE_TYPE_NOTE so later statements can use the computed value.
    public String noteType = Constants.NOTE_TYPE_NOTE;
    // Declares createdAt for use by the surrounding method or class.
    public long createdAt;
    // Declares updatedAt for use by the surrounding method or class.
    public long updatedAt;
    // Declares wordCount for use by the surrounding method or class.
    public int wordCount;
    // Declares charCount for use by the surrounding method or class.
    public int charCount;
    // Declares favorite for use by the surrounding method or class.
    public boolean favorite;
    // Declares pinned for use by the surrounding method or class.
    public boolean pinned;
    // Initializes tags with new ArrayList<>() so later statements can use the computed value.
    public List<Tag> tags = new ArrayList<>();

    // Checks whether todo.
    public boolean isTodo() {
        // Returns Constants.NOTE_TYPE_TODO.equals(noteType) to the caller.
        return Constants.NOTE_TYPE_TODO.equals(noteType);
    // Closes the current method, control-flow, or type block.
    }
// Closes the current method, control-flow, or type block.
}
