// Sets the package name to com.mit.notepad.repositories so this class belongs to the app module namespace.
package com.mit.notepad.repositories;

// Imports android.content.ContentValues so this file can use that type by its short name.
import android.content.ContentValues;
// Imports android.content.Context so this file can use that type by its short name.
import android.content.Context;
// Imports android.database.Cursor so this file can use that type by its short name.
import android.database.Cursor;

// Imports com.mit.notepad.database.NotepadDatabase so this file can use that type by its short name.
import com.mit.notepad.database.NotepadDatabase;
// Imports com.mit.notepad.models.AppNotification so this file can use that type by its short name.
import com.mit.notepad.models.AppNotification;

// Imports java.util.ArrayList so this file can use that type by its short name.
import java.util.ArrayList;
// Imports java.util.List so this file can use that type by its short name.
import java.util.List;

/** In-app notification history. */
// Declares class NotificationRepository to group related state and behavior.
public class NotificationRepository {
    // Declares helper for use by the surrounding method or class.
    private final NotepadDatabase helper;

    // Declares NotificationRepository() to implement this part of the component.
    public NotificationRepository(Context context) {
        // Delegates initialization to another constructor with NotepadDatabase.getInstance(context)).
        this(NotepadDatabase.getInstance(context));
    // Closes the current method, control-flow, or type block.
    }

    // Declares NotificationRepository() to implement this part of the component.
    public NotificationRepository(NotepadDatabase helper) {
        // Stores the result of helper in this.helper for use by later logic.
        this.helper = helper;
    // Closes the current method, control-flow, or type block.
    }

    // Copies the database columns into a model object.
    private static AppNotification map(Cursor c) {
        // Initializes n with new AppNotification() so later statements can use the computed value.
        AppNotification n = new AppNotification();
        // Stores the result of c.getLong(c.getColumnIndexOrThrow("id")) in n.id for use by later logic.
        n.id = c.getLong(c.getColumnIndexOrThrow("id"));
        // Initializes noteIdx with the text or query fragment shown here.
        int noteIdx = c.getColumnIndexOrThrow("note_id");
        // Initializes todoIdx with the text or query fragment shown here.
        int todoIdx = c.getColumnIndexOrThrow("todo_id");
        // Stores the result of c.isNull(noteIdx) ? 0 : c.getLong(noteIdx) in n.noteId for use by later logic.
        n.noteId = c.isNull(noteIdx) ? 0 : c.getLong(noteIdx);
        // Stores the result of c.isNull(todoIdx) ? 0 : c.getLong(todoIdx) in n.todoId for use by later logic.
        n.todoId = c.isNull(todoIdx) ? 0 : c.getLong(todoIdx);
        // Stores the result of c.getString(c.getColumnIndexOrThrow("title")) in n.title for use by later logic.
        n.title = c.getString(c.getColumnIndexOrThrow("title"));
        // Stores the result of c.getString(c.getColumnIndexOrThrow("message")) in n.message for use by later logic.
        n.message = c.getString(c.getColumnIndexOrThrow("message"));
        // Stores the result of c.getLong(c.getColumnIndexOrThrow("timestamp")) in n.timestamp for use by later logic.
        n.timestamp = c.getLong(c.getColumnIndexOrThrow("timestamp"));
        // Stores the result of c.getString(c.getColumnIndexOrThrow("category")) in n.category for use by later logic.
        n.category = c.getString(c.getColumnIndexOrThrow("category"));
        // Stores the result of c.getInt(c.getColumnIndexOrThrow("is_read")) == 1 in n.read for use by later logic.
        n.read = c.getInt(c.getColumnIndexOrThrow("is_read")) == 1;
        // Returns n to the caller.
        return n;
    // Closes the current method, control-flow, or type block.
    }

    // Declares add() to implement this part of the component.
    public long add(String title, String message, String category, long noteId, long todoId) {
        // Initializes v with new ContentValues() so later statements can use the computed value.
        ContentValues v = new ContentValues();
        // Calls v.put with "title", title to perform the operation shown.
        v.put("title", title);
        // Calls v.put with "message", message == null ? "" : message to perform the operation shown.
        v.put("message", message == null ? "" : message);
        // Calls v.put with "timestamp", System.currentTimeMillis( to perform the operation shown.
        v.put("timestamp", System.currentTimeMillis());
        // Calls v.put with "category", category to perform the operation shown.
        v.put("category", category);
        // Calls v.put with "is_read", 0 to perform the operation shown.
        v.put("is_read", 0);
        // If noteId > 0 is true, v.put("note_id", noteId) immediately.
        if (noteId > 0) v.put("note_id", noteId);
        // If todoId > 0 is true, v.put("todo_id", todoId) immediately.
        if (todoId > 0) v.put("todo_id", todoId);
        // Returns helper.getWritableDatabase().insertOrThrow("notifications", null, v) to the caller.
        return helper.getWritableDatabase().insertOrThrow("notifications", null, v);
    // Closes the current method, control-flow, or type block.
    }

    /** @param category null for all categories */
    // Declares list() to implement this part of the component.
    public List<AppNotification> list(String category) {
        // Initializes list with new ArrayList<>() so later statements can use the computed value.
        List<AppNotification> list = new ArrayList<>();
        // Initializes sql with the text or query fragment shown here.
        String sql = "SELECT * FROM notifications" + (category == null ? "" : " WHERE category=?")
                // Appends this literal fragment to the string or SQL query being assembled.
                + " ORDER BY timestamp DESC, id DESC";
        // Runs this operation with automatic cleanup for the resources declared in the try statement.
        try (Cursor c = helper.getReadableDatabase().rawQuery(sql,
                // Stores the result of = null ? null : new String[]{category})) { in category for use by later logic.
                category == null ? null : new String[]{category})) {
            // If c.moveToNext() is true, list.add(map(c)) immediately.
            while (c.moveToNext()) list.add(map(c));
        // Closes the current method, control-flow, or type block.
        }
        // Returns list to the caller.
        return list;
    // Closes the current method, control-flow, or type block.
    }

    // Declares unreadCount() to implement this part of the component.
    public int unreadCount() {
        // Runs this operation with automatic cleanup for the resources declared in the try statement.
        try (Cursor c = helper.getReadableDatabase().rawQuery(
                // Calls COUNT with *) FROM notifications WHERE is_read=0", null)) { to perform the operation shown.
                "SELECT COUNT(*) FROM notifications WHERE is_read=0", null)) {
            // Returns c.moveToFirst() ? c.getInt(0) : 0 to the caller.
            return c.moveToFirst() ? c.getInt(0) : 0;
        // Closes the current method, control-flow, or type block.
        }
    // Closes the current method, control-flow, or type block.
    }

    // Updates read.
    public void markRead(long id) {
        // Calls helper.getWritableDatabase with ).execSQL("UPDATE notifications SET is_read=1 WHERE id=?", new Object[]{id} to perform the operation shown.
        helper.getWritableDatabase().execSQL("UPDATE notifications SET is_read=1 WHERE id=?", new Object[]{id});
    // Closes the current method, control-flow, or type block.
    }

    // Updates unread.
    public void markUnread(long id) {
        // Calls helper.getWritableDatabase with ).execSQL("UPDATE notifications SET is_read=0 WHERE id=?", new Object[]{id} to perform the operation shown.
        helper.getWritableDatabase().execSQL("UPDATE notifications SET is_read=0 WHERE id=?", new Object[]{id});
    // Closes the current method, control-flow, or type block.
    }

    // Updates all read.
    public void markAllRead() {
        // Calls helper.getWritableDatabase with ).execSQL("UPDATE notifications SET is_read=1" to perform the operation shown.
        helper.getWritableDatabase().execSQL("UPDATE notifications SET is_read=1");
    // Closes the current method, control-flow, or type block.
    }

    // Declares delete() to implement this part of the component.
    public void delete(long id) {
        // Calls helper.getWritableDatabase with ).delete("notifications", "id=?", new String[]{String.valueOf(id)} to perform the operation shown.
        helper.getWritableDatabase().delete("notifications", "id=?", new String[]{String.valueOf(id)});
    // Closes the current method, control-flow, or type block.
    }

    // Clears all.
    public void clearAll() {
        // Calls helper.getWritableDatabase with ).delete("notifications", null, null to perform the operation shown.
        helper.getWritableDatabase().delete("notifications", null, null);
    // Closes the current method, control-flow, or type block.
    }
// Closes the current method, control-flow, or type block.
}
