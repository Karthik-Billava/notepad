package com.mit.notepad.models;

public class Tag {
    public long id;
    public String name = "";
    public String color = "#7C6CF0";
    public String icon = "label";
    public long lastUsedAt;
    /** Number of notes using this tag (filled by TagRepository.getAll()). */
    public int noteCount;
}
