// Sets the package name to com.mit.notepad.models so this class belongs to the app module namespace.
package com.mit.notepad.models;

// Declares class Tag to group related state and behavior.
public class Tag {
    // Declares id for use by the surrounding method or class.
    public long id;
    // Initializes name with the text or query fragment shown here.
    public String name = "";
    // Initializes color with the text or query fragment shown here.
    public String color = "#7C6CF0";
    // Initializes icon with the text or query fragment shown here.
    public String icon = "label";
    // Declares lastUsedAt for use by the surrounding method or class.
    public long lastUsedAt;
    /** Number of notes using this tag (filled by TagRepository.getAll()). */
    // Declares noteCount for use by the surrounding method or class.
    public int noteCount;
// Closes the current method, control-flow, or type block.
}
