package com.mit.notepad.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mit.notepad.R;
import com.mit.notepad.alarms.AlarmScheduler;
import com.mit.notepad.database.NotepadDatabase;
import com.mit.notepad.models.Alarm;
import com.mit.notepad.models.Tag;
import com.mit.notepad.models.Todo;
import com.mit.notepad.utils.Constants;
import com.mit.notepad.utils.Repeat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Task data access. Tasks are either standalone or linked to a note (note_id &gt; 0). */
public class TodoRepository {

    /** Filter for task lists. */
    public static class TodoQuery {
        public String status = Constants.STATUS_PENDING;
        public String keyword;
        public long tagId;
        public boolean overdueOnly;
        public boolean reminderOnly;
    }

    private final Context context;
    private final NotepadDatabase helper;
    private final TagRepository tags;

    public TodoRepository(Context context) {
        this(context, NotepadDatabase.getInstance(context));
    }

    public TodoRepository(Context context, NotepadDatabase helper) {
        this.context = context.getApplicationContext();
        this.helper = helper;
        this.tags = new TagRepository(helper);
    }

    private static Todo map(Cursor c) {
        Todo t = new Todo();
        t.id = c.getLong(c.getColumnIndexOrThrow("id"));
        int noteIdx = c.getColumnIndexOrThrow("note_id");
        t.noteId = c.isNull(noteIdx) ? 0 : c.getLong(noteIdx);
        t.title = c.getString(c.getColumnIndexOrThrow("title"));
        t.description = c.getString(c.getColumnIndexOrThrow("description"));
        t.completed = Constants.STATUS_COMPLETED.equals(c.getString(c.getColumnIndexOrThrow("status")));
        int dueIdx = c.getColumnIndexOrThrow("due_at");
        t.dueAt = c.isNull(dueIdx) ? 0 : c.getLong(dueIdx);
        t.reminderEnabled = c.getInt(c.getColumnIndexOrThrow("reminder_enabled")) == 1;
        t.repeatType = c.getString(c.getColumnIndexOrThrow("repeat_type"));
        t.repeatInterval = c.getInt(c.getColumnIndexOrThrow("repeat_interval"));
        t.createdAt = c.getLong(c.getColumnIndexOrThrow("created_at"));
        int doneIdx = c.getColumnIndexOrThrow("completed_at");
        t.completedAt = c.isNull(doneIdx) ? 0 : c.getLong(doneIdx);
        return t;
    }

    public Todo get(long id) {
        try (Cursor c = helper.getReadableDatabase().rawQuery(
                "SELECT * FROM todos WHERE id=?", new String[]{String.valueOf(id)})) {
            if (!c.moveToFirst()) return null;
            Todo t = map(c);
            attachTags(java.util.Collections.singletonList(t));
            return t;
        }
    }

    public Todo getByNote(long noteId) {
        try (Cursor c = helper.getReadableDatabase().rawQuery(
                "SELECT * FROM todos WHERE note_id=?", new String[]{String.valueOf(noteId)})) {
            return c.moveToFirst() ? map(c) : null;
        }
    }

    public int count(String status) {
        try (Cursor c = helper.getReadableDatabase().rawQuery(
                "SELECT COUNT(*) FROM todos WHERE status=?", new String[]{status})) {
            return c.moveToFirst() ? c.getInt(0) : 0;
        }
    }

    public List<Todo> query(TodoQuery q) {
        List<String> args = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT t.* FROM todos t WHERE t.status=?");
        args.add(q.status);
        if (q.keyword != null && !q.keyword.trim().isEmpty()) {
            String like = "%" + q.keyword.trim().replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_") + "%";
            sql.append(" AND (t.title LIKE ? ESCAPE '\\' OR t.description LIKE ? ESCAPE '\\')");
            args.add(like);
            args.add(like);
        }
        if (q.tagId > 0) {
            sql.append(" AND (EXISTS (SELECT 1 FROM todo_tags tt WHERE tt.todo_id=t.id AND tt.tag_id=?)"
                    + " OR EXISTS (SELECT 1 FROM note_tags nt WHERE nt.note_id=t.note_id AND nt.tag_id=?))");
            args.add(String.valueOf(q.tagId));
            args.add(String.valueOf(q.tagId));
        }
        if (q.overdueOnly) {
            sql.append(" AND t.due_at IS NOT NULL AND t.due_at<?");
            args.add(String.valueOf(System.currentTimeMillis()));
        }
        if (q.reminderOnly) sql.append(" AND t.reminder_enabled=1");
        if (Constants.STATUS_COMPLETED.equals(q.status)) {
            sql.append(" ORDER BY t.completed_at DESC");
        } else {
            sql.append(" ORDER BY (t.due_at IS NULL), t.due_at ASC, t.created_at DESC");
        }
        List<Todo> list = new ArrayList<>();
        try (Cursor c = helper.getReadableDatabase().rawQuery(sql.toString(), args.toArray(new String[0]))) {
            while (c.moveToNext()) list.add(map(c));
        }
        attachTags(list);
        return list;
    }

    private void attachTags(List<Todo> list) {
        Map<Long, List<Tag>> byNote = tags.noteTagMap();
        Map<Long, List<Tag>> byTodo = tags.todoTagMap();
        for (Todo t : list) {
            List<Tag> found = t.noteId > 0 ? byNote.get(t.noteId) : byTodo.get(t.id);
            t.tags = found == null ? new ArrayList<Tag>() : found;
        }
    }

    /**
     * Saves a standalone task and (re)schedules or cancels its reminder in one consistent step.
     * A task has at most one alarm row, so editing a task can never create a duplicate reminder.
     */
    public long save(Todo t, List<Long> tagIds) {
        SQLiteDatabase db = helper.getWritableDatabase();
        boolean wantAlarm = t.reminderEnabled && t.dueAt > 0;
        Alarm alarm = null;
        long removedAlarmId = -1;
        db.beginTransaction();
        try {
            ContentValues v = new ContentValues();
            v.put("title", t.title.trim());
            v.put("description", t.description == null ? "" : t.description.trim());
            if (t.dueAt > 0) v.put("due_at", t.dueAt); else v.putNull("due_at");
            v.put("reminder_enabled", wantAlarm ? 1 : 0);
            v.put("repeat_type", t.repeatType);
            v.put("repeat_interval", Math.max(1, t.repeatInterval));
            if (t.id == 0) {
                v.put("status", Constants.STATUS_PENDING);
                v.put("created_at", System.currentTimeMillis());
                t.id = db.insertOrThrow("todos", null, v);
            } else {
                db.update("todos", v, "id=?", new String[]{String.valueOf(t.id)});
            }
            TagRepository.replaceTodoTags(db, t.id, tagIds);
            if (wantAlarm) {
                alarm = AlarmRepository.upsert(db, "todo_id", t.id, t.dueAt, !t.completed,
                        t.repeatType, t.repeatInterval);
            } else {
                removedAlarmId = AlarmRepository.deleteByOwner(db, "todo_id", t.id);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        if (alarm != null) {
            if (alarm.enabled) AlarmScheduler.schedule(context, alarm);
            else AlarmScheduler.cancel(context, alarm.id);
        } else if (removedAlarmId > 0) {
            AlarmScheduler.cancel(context, removedAlarmId);
        }
        return t.id;
    }

    /** Marks a task completed or pending again, pausing/restoring its reminder. */
    public void setCompleted(long id, boolean completed) {
        Todo t = get(id);
        if (t == null) return;
        SQLiteDatabase db = helper.getWritableDatabase();
        String col = t.noteId > 0 ? "note_id" : "todo_id";
        long owner = t.noteId > 0 ? t.noteId : t.id;
        Alarm alarm = AlarmRepository.findByOwner(db, col, owner);
        boolean rearm = false;
        long now = System.currentTimeMillis();
        db.beginTransaction();
        try {
            ContentValues v = new ContentValues();
            v.put("status", completed ? Constants.STATUS_COMPLETED : Constants.STATUS_PENDING);
            if (completed) v.put("completed_at", now); else v.putNull("completed_at");
            db.update("todos", v, "id=?", new String[]{String.valueOf(id)});
            if (alarm != null) {
                if (completed) {
                    db.execSQL("UPDATE alarms SET is_enabled=0 WHERE id=?", new Object[]{alarm.id});
                } else if (t.reminderEnabled) {
                    long at = alarm.scheduledAt;
                    if (at <= now) at = Repeat.nextOccurrence(at, alarm.repeatType, alarm.repeatInterval, now);
                    if (at > now) {
                        db.execSQL("UPDATE alarms SET is_enabled=1, scheduled_at=? WHERE id=?",
                                new Object[]{at, alarm.id});
                        db.execSQL("UPDATE todos SET due_at=? WHERE id=?", new Object[]{at, id});
                        alarm.scheduledAt = at;
                        alarm.enabled = true;
                        rearm = true;
                    }
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        if (alarm != null) {
            if (completed) AlarmScheduler.cancel(context, alarm.id);
            else if (rearm) AlarmScheduler.schedule(context, alarm);
        }
        if (completed) {
            new NotificationRepository(helper).add(context.getString(R.string.notif_task_completed),
                    t.title, Constants.CATEGORY_TODO, t.noteId, t.id);
        }
    }

    /** Deletes a standalone task together with its reminder. */
    public void delete(long id) {
        Todo t = get(id);
        if (t == null) return;
        SQLiteDatabase db = helper.getWritableDatabase();
        long alarmId = -1;
        db.beginTransaction();
        try {
            try (Cursor c = db.rawQuery("SELECT id FROM alarms WHERE todo_id=?", new String[]{String.valueOf(id)})) {
                if (c.moveToFirst()) alarmId = c.getLong(0);
            }
            db.delete("todos", "id=?", new String[]{String.valueOf(id)});
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        if (alarmId > 0) AlarmScheduler.cancel(context, alarmId);
    }
}
