// Sets the package name to com.mit.notepad.repositories so this class belongs to the app module namespace.
package com.mit.notepad.repositories;

// Imports android.content.ContentValues so this file can use that type by its short name.
import android.content.ContentValues;
// Imports android.content.Context so this file can use that type by its short name.
import android.content.Context;
// Imports android.database.Cursor so this file can use that type by its short name.
import android.database.Cursor;
// Imports android.database.sqlite.SQLiteConstraintException so this file can use that type by its short name.
import android.database.sqlite.SQLiteConstraintException;
// Imports android.database.sqlite.SQLiteDatabase so this file can use that type by its short name.
import android.database.sqlite.SQLiteDatabase;

// Imports com.mit.notepad.database.NotepadDatabase so this file can use that type by its short name.
import com.mit.notepad.database.NotepadDatabase;
// Imports com.mit.notepad.models.Tag so this file can use that type by its short name.
import com.mit.notepad.models.Tag;

// Imports java.util.ArrayList so this file can use that type by its short name.
import java.util.ArrayList;
// Imports java.util.HashMap so this file can use that type by its short name.
import java.util.HashMap;
// Imports java.util.List so this file can use that type by its short name.
import java.util.List;
// Imports java.util.Map so this file can use that type by its short name.
import java.util.Map;

// Declares class TagRepository to group related state and behavior.
public class TagRepository {
    // Declares class DuplicateTagException to group related state and behavior.
    public static class DuplicateTagException extends Exception {
        // Declares DuplicateTagException() to implement this part of the component.
        public DuplicateTagException() {
            // Initializes the parent class by passing "Tag already exists") to its constructor.
            super("Tag already exists");
        // Closes the current method, control-flow, or type block.
        }
    // Closes the current method, control-flow, or type block.
    }

    // Declares helper for use by the surrounding method or class.
    private final NotepadDatabase helper;

    // Declares TagRepository() to implement this part of the component.
    public TagRepository(Context context) {
        // Delegates initialization to another constructor with NotepadDatabase.getInstance(context)).
        this(NotepadDatabase.getInstance(context));
    // Closes the current method, control-flow, or type block.
    }

    // Declares TagRepository() to implement this part of the component.
    public TagRepository(NotepadDatabase helper) {
        // Stores the result of helper in this.helper for use by later logic.
        this.helper = helper;
    // Closes the current method, control-flow, or type block.
    }

    // Copies the database columns into a model object.
    private static Tag map(Cursor c) {
        // Initializes t with new Tag() so later statements can use the computed value.
        Tag t = new Tag();
        // Stores the result of c.getLong(c.getColumnIndexOrThrow("id")) in t.id for use by later logic.
        t.id = c.getLong(c.getColumnIndexOrThrow("id"));
        // Stores the result of c.getString(c.getColumnIndexOrThrow("name")) in t.name for use by later logic.
        t.name = c.getString(c.getColumnIndexOrThrow("name"));
        // Stores the result of c.getString(c.getColumnIndexOrThrow("color")) in t.color for use by later logic.
        t.color = c.getString(c.getColumnIndexOrThrow("color"));
        // Stores the result of c.getString(c.getColumnIndexOrThrow("icon")) in t.icon for use by later logic.
        t.icon = c.getString(c.getColumnIndexOrThrow("icon"));
        // Stores the result of c.getLong(c.getColumnIndexOrThrow("last_used_at")) in t.lastUsedAt for use by later logic.
        t.lastUsedAt = c.getLong(c.getColumnIndexOrThrow("last_used_at"));
        // Initializes countIdx with the text or query fragment shown here.
        int countIdx = c.getColumnIndex("note_count");
        // If countIdx >= 0 is true, t.noteCount = c.getInt(countIdx) immediately.
        if (countIdx >= 0) t.noteCount = c.getInt(countIdx);
        // Returns t to the caller.
        return t;
    // Closes the current method, control-flow, or type block.
    }

    // Initializes TAG_SELECT_SQL with  so later statements can use the computed value.
    private static final String TAG_SELECT_SQL =
            // Calls COUNT with *) FROM note_tags nt WHERE nt.tag_id=t.id) + " to perform the operation shown.
            "SELECT t.*, ((SELECT COUNT(*) FROM note_tags nt WHERE nt.tag_id=t.id) + "
                    // Appends this literal fragment to the string or SQL query being assembled.
                    + "(SELECT COUNT(*) FROM todo_tags tt JOIN todos td ON td.id=tt.todo_id "
                    // Appends this literal fragment to the string or SQL query being assembled.
                    + "WHERE tt.tag_id=t.id AND td.note_id IS NULL)) AS note_count FROM tags t ";

    /** All tags (alphabetical) with the number of notes that use them. */
    // Returns the requested all.
    public List<Tag> getAll() {
        // Initializes list with new ArrayList<>() so later statements can use the computed value.
        List<Tag> list = new ArrayList<>();
        // Runs this operation with automatic cleanup for the resources declared in the try statement.
        try (Cursor c = helper.getReadableDatabase().rawQuery(
                // Continues the expression above with TAG_SELECT_SQL + "ORDER BY t.name COLLATE NOCASE", null)) {.
                TAG_SELECT_SQL + "ORDER BY t.name COLLATE NOCASE", null)) {
            // If c.moveToNext() is true, list.add(map(c)) immediately.
            while (c.moveToNext()) list.add(map(c));
        // Closes the current method, control-flow, or type block.
        }
        // Returns list to the caller.
        return list;
    // Closes the current method, control-flow, or type block.
    }

    /** Most recently used tags. */
    // Returns the requested recent.
    public List<Tag> getRecent(int limit) {
        // Initializes list with new ArrayList<>() so later statements can use the computed value.
        List<Tag> list = new ArrayList<>();
        // Runs this operation with automatic cleanup for the resources declared in the try statement.
        try (Cursor c = helper.getReadableDatabase().rawQuery(
                // Supplies TAG_SELECT_SQL + "WHERE t.last_used_at>0 ORDER BY t.last_used_at DESC LIMIT ?" as the next argument to the call above.
                TAG_SELECT_SQL + "WHERE t.last_used_at>0 ORDER BY t.last_used_at DESC LIMIT ?",
                // Creates the object described by new String[]{String.valueOf(limit)})) {.
                new String[]{String.valueOf(limit)})) {
            // If c.moveToNext() is true, list.add(map(c)) immediately.
            while (c.moveToNext()) list.add(map(c));
        // Closes the current method, control-flow, or type block.
        }
        // Returns list to the caller.
        return list;
    // Closes the current method, control-flow, or type block.
    }

    // Declares get() to implement this part of the component.
    public Tag get(long id) {
        // Runs this operation with automatic cleanup for the resources declared in the try statement.
        try (Cursor c = helper.getReadableDatabase().rawQuery(
                // Calls String.valueOf with id)})) { to perform the operation shown.
                TAG_SELECT_SQL + "WHERE t.id=?", new String[]{String.valueOf(id)})) {
            // Returns c.moveToFirst() ? map(c) : null to the caller.
            return c.moveToFirst() ? map(c) : null;
        // Closes the current method, control-flow, or type block.
        }
    // Closes the current method, control-flow, or type block.
    }

    // Declares create() to implement this part of the component.
    public long create(String name, String color, String icon) throws DuplicateTagException {
        // Initializes v with new ContentValues() so later statements can use the computed value.
        ContentValues v = new ContentValues();
        // Calls v.put with "name", name.trim( to perform the operation shown.
        v.put("name", name.trim());
        // Calls v.put with "color", color to perform the operation shown.
        v.put("color", color);
        // Calls v.put with "icon", icon == null ? "label" : icon to perform the operation shown.
        v.put("icon", icon == null ? "label" : icon);
        // Starts the operation whose failures are handled by the following catch clauses.
        try {
            // Returns helper.getWritableDatabase().insertOrThrow("tags", null, v) to the caller.
            return helper.getWritableDatabase().insertOrThrow("tags", null, v);
        // Calls catch with SQLiteConstraintException e) { to perform the operation shown.
        } catch (SQLiteConstraintException e) {
            // Raises new DuplicateTagException() so the caller can handle this failure.
            throw new DuplicateTagException();
        // Closes the current method, control-flow, or type block.
        }
    // Closes the current method, control-flow, or type block.
    }

    // Declares update() to implement this part of the component.
    public void update(long id, String name, String color) throws DuplicateTagException {
        // Initializes v with new ContentValues() so later statements can use the computed value.
        ContentValues v = new ContentValues();
        // Calls v.put with "name", name.trim( to perform the operation shown.
        v.put("name", name.trim());
        // Calls v.put with "color", color to perform the operation shown.
        v.put("color", color);
        // Starts the operation whose failures are handled by the following catch clauses.
        try {
            // Calls helper.getWritableDatabase with ).update("tags", v, "id=?", new String[]{String.valueOf(id)} to perform the operation shown.
            helper.getWritableDatabase().update("tags", v, "id=?", new String[]{String.valueOf(id)});
        // Calls catch with SQLiteConstraintException e) { to perform the operation shown.
        } catch (SQLiteConstraintException e) {
            // Raises new DuplicateTagException() so the caller can handle this failure.
            throw new DuplicateTagException();
        // Closes the current method, control-flow, or type block.
        }
    // Closes the current method, control-flow, or type block.
    }

    /** Deletes the tag and its links only; notes and tasks are untouched. */
    // Declares delete() to implement this part of the component.
    public void delete(long id) {
        // Calls helper.getWritableDatabase with ).delete("tags", "id=?", new String[]{String.valueOf(id)} to perform the operation shown.
        helper.getWritableDatabase().delete("tags", "id=?", new String[]{String.valueOf(id)});
    // Closes the current method, control-flow, or type block.
    }

    // Returns the requested for note.
    public List<Tag> getForNote(long noteId) {
        // Initializes tags with noteTagMap().get(noteId) so later statements can use the computed value.
        List<Tag> tags = noteTagMap().get(noteId);
        // Returns tags == null ? new ArrayList<Tag>() : tags to the caller.
        return tags == null ? new ArrayList<Tag>() : tags;
    // Closes the current method, control-flow, or type block.
    }

    // Returns the requested for todo.
    public List<Tag> getForTodo(long todoId) {
        // Initializes tags with todoTagMap().get(todoId) so later statements can use the computed value.
        List<Tag> tags = todoTagMap().get(todoId);
        // Returns tags == null ? new ArrayList<Tag>() : tags to the caller.
        return tags == null ? new ArrayList<Tag>() : tags;
    // Closes the current method, control-flow, or type block.
    }

    // Declares noteTagMap() to implement this part of the component.
    public Map<Long, List<Tag>> noteTagMap() {
        // Returns loadMap("note_tags", "note_id") to the caller.
        return loadMap("note_tags", "note_id");
    // Closes the current method, control-flow, or type block.
    }

    // Declares todoTagMap() to implement this part of the component.
    public Map<Long, List<Tag>> todoTagMap() {
        // Returns loadMap("todo_tags", "todo_id") to the caller.
        return loadMap("todo_tags", "todo_id");
    // Closes the current method, control-flow, or type block.
    }

    // table/column are internal constants, never user input
    // Declares loadMap() to implement this part of the component.
    private Map<Long, List<Tag>> loadMap(String table, String ownerCol) {
        // Initializes map with new HashMap<>() so later statements can use the computed value.
        Map<Long, List<Tag>> map = new HashMap<>();
        // Runs this operation with automatic cleanup for the resources declared in the try statement.
        try (Cursor c = helper.getReadableDatabase().rawQuery(
                // Continues the expression above with "SELECT x." + ownerCol + " AS owner_id, t.* FROM " + table + " x ".
                "SELECT x." + ownerCol + " AS owner_id, t.* FROM " + table + " x "
                        // Appends this literal fragment to the string or SQL query being assembled.
                        + "JOIN tags t ON t.id=x.tag_id ORDER BY t.name COLLATE NOCASE", null)) {
            // Initializes ownerIdx with the text or query fragment shown here.
            int ownerIdx = c.getColumnIndexOrThrow("owner_id");
            // If c.moveToNext() is true, { immediately.
            while (c.moveToNext()) {
                // Initializes owner with c.getLong(ownerIdx) so later statements can use the computed value.
                long owner = c.getLong(ownerIdx);
                // Initializes list with map.get(owner) so later statements can use the computed value.
                List<Tag> list = map.get(owner);
                // If list == null is true, { immediately.
                if (list == null) {
                    // Stores the result of new ArrayList<>() in list for use by later logic.
                    list = new ArrayList<>();
                    // Calls map.put with owner, list to perform the operation shown.
                    map.put(owner, list);
                // Closes the current method, control-flow, or type block.
                }
                // Calls list.add with map(c to perform the operation shown.
                list.add(map(c));
            // Closes the current method, control-flow, or type block.
            }
        // Closes the current method, control-flow, or type block.
        }
        // Returns map to the caller.
        return map;
    // Closes the current method, control-flow, or type block.
    }

    /** Replaces tag links inside an existing transaction. */
    // Declares replaceNoteTags() to implement this part of the component.
    static void replaceNoteTags(SQLiteDatabase db, long noteId, List<Long> tagIds) {
        // Calls replaceLinks with db, "note_tags", "note_id", noteId, tagIds to perform the operation shown.
        replaceLinks(db, "note_tags", "note_id", noteId, tagIds);
    // Closes the current method, control-flow, or type block.
    }

    // Declares replaceTodoTags() to implement this part of the component.
    static void replaceTodoTags(SQLiteDatabase db, long todoId, List<Long> tagIds) {
        // Calls replaceLinks with db, "todo_tags", "todo_id", todoId, tagIds to perform the operation shown.
        replaceLinks(db, "todo_tags", "todo_id", todoId, tagIds);
    // Closes the current method, control-flow, or type block.
    }

    // Declares replaceLinks() to implement this part of the component.
    private static void replaceLinks(SQLiteDatabase db, String table, String ownerCol, long ownerId, List<Long> tagIds) {
        // Calls db.delete with table, ownerCol + "=?", new String[]{String.valueOf(ownerId)} to perform the operation shown.
        db.delete(table, ownerCol + "=?", new String[]{String.valueOf(ownerId)});
        // If tagIds == null is true, return immediately.
        if (tagIds == null) return;
        // Initializes now with System.currentTimeMillis() so later statements can use the computed value.
        long now = System.currentTimeMillis();
        // Repeats the following block according to the initialization, condition, and update in Long tagId : tagIds.
        for (Long tagId : tagIds) {
            // Initializes v with new ContentValues() so later statements can use the computed value.
            ContentValues v = new ContentValues();
            // Calls v.put with ownerCol, ownerId to perform the operation shown.
            v.put(ownerCol, ownerId);
            // Calls v.put with "tag_id", tagId to perform the operation shown.
            v.put("tag_id", tagId);
            // Calls db.insertWithOnConflict with table, null, v, SQLiteDatabase.CONFLICT_IGNORE to perform the operation shown.
            db.insertWithOnConflict(table, null, v, SQLiteDatabase.CONFLICT_IGNORE);
            // Calls db.execSQL with "UPDATE tags SET last_used_at=? WHERE id=?", new Object[]{now, tagId} to perform the operation shown.
            db.execSQL("UPDATE tags SET last_used_at=? WHERE id=?", new Object[]{now, tagId});
        // Closes the current method, control-flow, or type block.
        }
    // Closes the current method, control-flow, or type block.
    }
// Closes the current method, control-flow, or type block.
}
