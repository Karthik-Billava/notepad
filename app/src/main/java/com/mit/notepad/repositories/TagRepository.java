package com.mit.notepad.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import com.mit.notepad.database.NotepadDatabase;
import com.mit.notepad.models.Tag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TagRepository {
    public static class DuplicateTagException extends Exception {
        public DuplicateTagException() {
            super("Tag already exists");
        }
    }

    private final NotepadDatabase helper;

    public TagRepository(Context context) {
        this(NotepadDatabase.getInstance(context));
    }

    public TagRepository(NotepadDatabase helper) {
        this.helper = helper;
    }

    private static Tag map(Cursor c) {
        Tag t = new Tag();
        t.id = c.getLong(c.getColumnIndexOrThrow("id"));
        t.name = c.getString(c.getColumnIndexOrThrow("name"));
        t.color = c.getString(c.getColumnIndexOrThrow("color"));
        t.icon = c.getString(c.getColumnIndexOrThrow("icon"));
        t.lastUsedAt = c.getLong(c.getColumnIndexOrThrow("last_used_at"));
        int countIdx = c.getColumnIndex("note_count");
        if (countIdx >= 0) t.noteCount = c.getInt(countIdx);
        return t;
    }

    /** All tags (alphabetical) with the number of notes that use them. */
    public List<Tag> getAll() {
        List<Tag> list = new ArrayList<>();
        try (Cursor c = helper.getReadableDatabase().rawQuery(
                "SELECT t.*, (SELECT COUNT(*) FROM note_tags nt WHERE nt.tag_id=t.id) AS note_count "
                        + "FROM tags t ORDER BY t.name COLLATE NOCASE", null)) {
            while (c.moveToNext()) list.add(map(c));
        }
        return list;
    }

    /** Most recently used tags. */
    public List<Tag> getRecent(int limit) {
        List<Tag> list = new ArrayList<>();
        try (Cursor c = helper.getReadableDatabase().rawQuery(
                "SELECT t.*, (SELECT COUNT(*) FROM note_tags nt WHERE nt.tag_id=t.id) AS note_count "
                        + "FROM tags t WHERE t.last_used_at>0 ORDER BY t.last_used_at DESC LIMIT ?",
                new String[]{String.valueOf(limit)})) {
            while (c.moveToNext()) list.add(map(c));
        }
        return list;
    }

    public Tag get(long id) {
        try (Cursor c = helper.getReadableDatabase().rawQuery(
                "SELECT t.*, (SELECT COUNT(*) FROM note_tags nt WHERE nt.tag_id=t.id) AS note_count "
                        + "FROM tags t WHERE t.id=?", new String[]{String.valueOf(id)})) {
            return c.moveToFirst() ? map(c) : null;
        }
    }

    public long create(String name, String color, String icon) throws DuplicateTagException {
        ContentValues v = new ContentValues();
        v.put("name", name.trim());
        v.put("color", color);
        v.put("icon", icon == null ? "label" : icon);
        try {
            return helper.getWritableDatabase().insertOrThrow("tags", null, v);
        } catch (SQLiteConstraintException e) {
            throw new DuplicateTagException();
        }
    }

    public void update(long id, String name, String color) throws DuplicateTagException {
        ContentValues v = new ContentValues();
        v.put("name", name.trim());
        v.put("color", color);
        try {
            helper.getWritableDatabase().update("tags", v, "id=?", new String[]{String.valueOf(id)});
        } catch (SQLiteConstraintException e) {
            throw new DuplicateTagException();
        }
    }

    /** Deletes the tag and its links only; notes and tasks are untouched. */
    public void delete(long id) {
        helper.getWritableDatabase().delete("tags", "id=?", new String[]{String.valueOf(id)});
    }

    public List<Tag> getForNote(long noteId) {
        List<Tag> tags = noteTagMap().get(noteId);
        return tags == null ? new ArrayList<Tag>() : tags;
    }

    public List<Tag> getForTodo(long todoId) {
        List<Tag> tags = todoTagMap().get(todoId);
        return tags == null ? new ArrayList<Tag>() : tags;
    }

    public Map<Long, List<Tag>> noteTagMap() {
        return loadMap("note_tags", "note_id");
    }

    public Map<Long, List<Tag>> todoTagMap() {
        return loadMap("todo_tags", "todo_id");
    }

    // table/column are internal constants, never user input
    private Map<Long, List<Tag>> loadMap(String table, String ownerCol) {
        Map<Long, List<Tag>> map = new HashMap<>();
        try (Cursor c = helper.getReadableDatabase().rawQuery(
                "SELECT x." + ownerCol + " AS owner_id, t.* FROM " + table + " x "
                        + "JOIN tags t ON t.id=x.tag_id ORDER BY t.name COLLATE NOCASE", null)) {
            int ownerIdx = c.getColumnIndexOrThrow("owner_id");
            while (c.moveToNext()) {
                long owner = c.getLong(ownerIdx);
                List<Tag> list = map.get(owner);
                if (list == null) {
                    list = new ArrayList<>();
                    map.put(owner, list);
                }
                list.add(map(c));
            }
        }
        return map;
    }

    /** Replaces tag links inside an existing transaction. */
    static void replaceNoteTags(SQLiteDatabase db, long noteId, List<Long> tagIds) {
        replaceLinks(db, "note_tags", "note_id", noteId, tagIds);
    }

    static void replaceTodoTags(SQLiteDatabase db, long todoId, List<Long> tagIds) {
        replaceLinks(db, "todo_tags", "todo_id", todoId, tagIds);
    }

    private static void replaceLinks(SQLiteDatabase db, String table, String ownerCol, long ownerId, List<Long> tagIds) {
        db.delete(table, ownerCol + "=?", new String[]{String.valueOf(ownerId)});
        if (tagIds == null) return;
        long now = System.currentTimeMillis();
        for (Long tagId : tagIds) {
            ContentValues v = new ContentValues();
            v.put(ownerCol, ownerId);
            v.put("tag_id", tagId);
            db.insertWithOnConflict(table, null, v, SQLiteDatabase.CONFLICT_IGNORE);
            db.execSQL("UPDATE tags SET last_used_at=? WHERE id=?", new Object[]{now, tagId});
        }
    }
}
