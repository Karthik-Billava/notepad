package com.mit.notepad.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.mit.notepad.database.NotepadDatabase;
import com.mit.notepad.models.AppNotification;

import java.util.ArrayList;
import java.util.List;

/** In-app notification history. */
public class NotificationRepository {
    private final NotepadDatabase helper;

    public NotificationRepository(Context context) {
        this(NotepadDatabase.getInstance(context));
    }

    public NotificationRepository(NotepadDatabase helper) {
        this.helper = helper;
    }

    private static AppNotification map(Cursor c) {
        AppNotification n = new AppNotification();
        n.id = c.getLong(c.getColumnIndexOrThrow("id"));
        int noteIdx = c.getColumnIndexOrThrow("note_id");
        int todoIdx = c.getColumnIndexOrThrow("todo_id");
        n.noteId = c.isNull(noteIdx) ? 0 : c.getLong(noteIdx);
        n.todoId = c.isNull(todoIdx) ? 0 : c.getLong(todoIdx);
        n.title = c.getString(c.getColumnIndexOrThrow("title"));
        n.message = c.getString(c.getColumnIndexOrThrow("message"));
        n.timestamp = c.getLong(c.getColumnIndexOrThrow("timestamp"));
        n.category = c.getString(c.getColumnIndexOrThrow("category"));
        n.read = c.getInt(c.getColumnIndexOrThrow("is_read")) == 1;
        return n;
    }

    public long add(String title, String message, String category, long noteId, long todoId) {
        ContentValues v = new ContentValues();
        v.put("title", title);
        v.put("message", message == null ? "" : message);
        v.put("timestamp", System.currentTimeMillis());
        v.put("category", category);
        v.put("is_read", 0);
        if (noteId > 0) v.put("note_id", noteId);
        if (todoId > 0) v.put("todo_id", todoId);
        return helper.getWritableDatabase().insertOrThrow("notifications", null, v);
    }

    /** @param category null for all categories */
    public List<AppNotification> list(String category) {
        List<AppNotification> list = new ArrayList<>();
        String sql = "SELECT * FROM notifications" + (category == null ? "" : " WHERE category=?")
                + " ORDER BY timestamp DESC, id DESC";
        try (Cursor c = helper.getReadableDatabase().rawQuery(sql,
                category == null ? null : new String[]{category})) {
            while (c.moveToNext()) list.add(map(c));
        }
        return list;
    }

    public int unreadCount() {
        try (Cursor c = helper.getReadableDatabase().rawQuery(
                "SELECT COUNT(*) FROM notifications WHERE is_read=0", null)) {
            return c.moveToFirst() ? c.getInt(0) : 0;
        }
    }

    public void markRead(long id) {
        helper.getWritableDatabase().execSQL("UPDATE notifications SET is_read=1 WHERE id=?", new Object[]{id});
    }

    public void markUnread(long id) {
        helper.getWritableDatabase().execSQL("UPDATE notifications SET is_read=0 WHERE id=?", new Object[]{id});
    }

    public void markAllRead() {
        helper.getWritableDatabase().execSQL("UPDATE notifications SET is_read=1");
    }

    public void delete(long id) {
        helper.getWritableDatabase().delete("notifications", "id=?", new String[]{String.valueOf(id)});
    }

    public void clearAll() {
        helper.getWritableDatabase().delete("notifications", null, null);
    }
}
