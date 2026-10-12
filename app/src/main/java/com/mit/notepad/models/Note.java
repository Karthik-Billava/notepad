package com.mit.notepad.models;

import com.mit.notepad.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class Note {
    public long id;
    public String title = "";
    /** Plain text content (used for search and counts). */
    public String content = "";
    /** JSON describing bold/italic/underline/link spans, see RichText. */
    public String formatting = "[]";
    public String noteType = Constants.NOTE_TYPE_NOTE;
    public long createdAt;
    public long updatedAt;
    public int wordCount;
    public int charCount;
    public boolean favorite;
    public boolean pinned;
    public List<Tag> tags = new ArrayList<>();

    public boolean isTodo() {
        return Constants.NOTE_TYPE_TODO.equals(noteType);
    }
}
