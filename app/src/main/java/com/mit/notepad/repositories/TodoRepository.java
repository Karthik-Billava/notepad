// Sets the package name to com.mit.notepad.repositories so this class belongs to the app module namespace.
package com.mit.notepad.repositories;

// Imports android.content.ContentValues so this file can use that type by its short name.
import android.content.ContentValues;
// Imports android.content.Context so this file can use that type by its short name.
import android.content.Context;
// Imports android.database.Cursor so this file can use that type by its short name.
import android.database.Cursor;
// Imports android.database.sqlite.SQLiteDatabase so this file can use that type by its short name.
import android.database.sqlite.SQLiteDatabase;

// Imports com.mit.notepad.R so this file can use that type by its short name.
import com.mit.notepad.R;
// Imports com.mit.notepad.alarms.AlarmScheduler so this file can use that type by its short name.
import com.mit.notepad.alarms.AlarmScheduler;
// Imports com.mit.notepad.database.NotepadDatabase so this file can use that type by its short name.
import com.mit.notepad.database.NotepadDatabase;
// Imports com.mit.notepad.models.Alarm so this file can use that type by its short name.
import com.mit.notepad.models.Alarm;
// Imports com.mit.notepad.models.Tag so this file can use that type by its short name.
import com.mit.notepad.models.Tag;
// Imports com.mit.notepad.models.Todo so this file can use that type by its short name.
import com.mit.notepad.models.Todo;
// Imports com.mit.notepad.utils.Constants so this file can use that type by its short name.
import com.mit.notepad.utils.Constants;
// Imports com.mit.notepad.utils.Repeat so this file can use that type by its short name.
import com.mit.notepad.utils.Repeat;

// Imports java.util.ArrayList so this file can use that type by its short name.
import java.util.ArrayList;
// Imports java.util.List so this file can use that type by its short name.
import java.util.List;
// Imports java.util.Map so this file can use that type by its short name.
import java.util.Map;

/** Task data access. Tasks are either standalone or linked to a note (note_id &gt; 0). */
// Declares class TodoRepository to group related state and behavior.
public class TodoRepository {

    /** Filter for task lists. */
    // Declares class TodoQuery to group related state and behavior.
    public static class TodoQuery {
        // Initializes status with Constants.STATUS_PENDING so later statements can use the computed value.
        public String status = Constants.STATUS_PENDING;
        // Declares keyword for use by the surrounding method or class.
        public String keyword;
        // Declares tagId for use by the surrounding method or class.
        public long tagId;
        // Declares overdueOnly for use by the surrounding method or class.
        public boolean overdueOnly;
        // Declares reminderOnly for use by the surrounding method or class.
        public boolean reminderOnly;
    // Closes the current method, control-flow, or type block.
    }

    // Declares context for use by the surrounding method or class.
    private final Context context;
    // Declares helper for use by the surrounding method or class.
    private final NotepadDatabase helper;
    // Declares tags for use by the surrounding method or class.
    private final TagRepository tags;

    // Declares TodoRepository() to implement this part of the component.
    public TodoRepository(Context context) {
        // Delegates initialization to another constructor with context, NotepadDatabase.getInstance(context)).
        this(context, NotepadDatabase.getInstance(context));
    // Closes the current method, control-flow, or type block.
    }

    // Declares TodoRepository() to implement this part of the component.
    public TodoRepository(Context context, NotepadDatabase helper) {
        // Stores the result of context.getApplicationContext() in this.context for use by later logic.
        this.context = context.getApplicationContext();
        // Stores the result of helper in this.helper for use by later logic.
        this.helper = helper;
        // Stores the result of new TagRepository(helper) in this.tags for use by later logic.
        this.tags = new TagRepository(helper);
    // Closes the current method, control-flow, or type block.
    }

    // Copies the database columns into a model object.
    private static Todo map(Cursor c) {
        // Initializes t with new Todo() so later statements can use the computed value.
        Todo t = new Todo();
        // Stores the result of c.getLong(c.getColumnIndexOrThrow("id")) in t.id for use by later logic.
        t.id = c.getLong(c.getColumnIndexOrThrow("id"));
        // Initializes noteIdx with the text or query fragment shown here.
        int noteIdx = c.getColumnIndexOrThrow("note_id");
        // Stores the result of c.isNull(noteIdx) ? 0 : c.getLong(noteIdx) in t.noteId for use by later logic.
        t.noteId = c.isNull(noteIdx) ? 0 : c.getLong(noteIdx);
        // Stores the result of c.getString(c.getColumnIndexOrThrow("title")) in t.title for use by later logic.
        t.title = c.getString(c.getColumnIndexOrThrow("title"));
        // Stores the result of c.getString(c.getColumnIndexOrThrow("description")) in t.description for use by later logic.
        t.description = c.getString(c.getColumnIndexOrThrow("description"));
        // Stores the result of Constants.STATUS_COMPLETED.equals(c.getString(c.getColumnIndexOrThrow("status"))) in t.completed for use by later logic.
        t.completed = Constants.STATUS_COMPLETED.equals(c.getString(c.getColumnIndexOrThrow("status")));
        // Initializes dueIdx with the text or query fragment shown here.
        int dueIdx = c.getColumnIndexOrThrow("due_at");
        // Stores the result of c.isNull(dueIdx) ? 0 : c.getLong(dueIdx) in t.dueAt for use by later logic.
        t.dueAt = c.isNull(dueIdx) ? 0 : c.getLong(dueIdx);
        // Stores the result of c.getInt(c.getColumnIndexOrThrow("reminder_enabled")) == 1 in t.reminderEnabled for use by later logic.
        t.reminderEnabled = c.getInt(c.getColumnIndexOrThrow("reminder_enabled")) == 1;
        // Stores the result of c.getString(c.getColumnIndexOrThrow("repeat_type")) in t.repeatType for use by later logic.
        t.repeatType = c.getString(c.getColumnIndexOrThrow("repeat_type"));
        // Stores the result of c.getInt(c.getColumnIndexOrThrow("repeat_interval")) in t.repeatInterval for use by later logic.
        t.repeatInterval = c.getInt(c.getColumnIndexOrThrow("repeat_interval"));
        // Stores the result of c.getLong(c.getColumnIndexOrThrow("created_at")) in t.createdAt for use by later logic.
        t.createdAt = c.getLong(c.getColumnIndexOrThrow("created_at"));
        // Initializes doneIdx with the text or query fragment shown here.
        int doneIdx = c.getColumnIndexOrThrow("completed_at");
        // Stores the result of c.isNull(doneIdx) ? 0 : c.getLong(doneIdx) in t.completedAt for use by later logic.
        t.completedAt = c.isNull(doneIdx) ? 0 : c.getLong(doneIdx);
        // Returns t to the caller.
        return t;
    // Closes the current method, control-flow, or type block.
    }

    // Declares get() to implement this part of the component.
    public Todo get(long id) {
        // Runs this operation with automatic cleanup for the resources declared in the try statement.
        try (Cursor c = helper.getReadableDatabase().rawQuery(
                // Calls String.valueOf with id)})) { to perform the operation shown.
                "SELECT * FROM todos WHERE id=?", new String[]{String.valueOf(id)})) {
            // If !c.moveToFirst() is true, return null immediately.
            if (!c.moveToFirst()) return null;
            // Initializes t with map(c) so later statements can use the computed value.
            Todo t = map(c);
            // Calls attachTags with java.util.Collections.singletonList(t to perform the operation shown.
            attachTags(java.util.Collections.singletonList(t));
            // Returns t to the caller.
            return t;
        // Closes the current method, control-flow, or type block.
        }
    // Closes the current method, control-flow, or type block.
    }

    // Returns the requested by note.
    public Todo getByNote(long noteId) {
        // Runs this operation with automatic cleanup for the resources declared in the try statement.
        try (Cursor c = helper.getReadableDatabase().rawQuery(
                // Calls String.valueOf with noteId)})) { to perform the operation shown.
                "SELECT * FROM todos WHERE note_id=?", new String[]{String.valueOf(noteId)})) {
            // Returns c.moveToFirst() ? map(c) : null to the caller.
            return c.moveToFirst() ? map(c) : null;
        // Closes the current method, control-flow, or type block.
        }
    // Closes the current method, control-flow, or type block.
    }

    // Declares count() to implement this part of the component.
    public int count(String status) {
        // Runs this operation with automatic cleanup for the resources declared in the try statement.
        try (Cursor c = helper.getReadableDatabase().rawQuery(
                // Calls COUNT with *) FROM todos WHERE status=?", new String[]{status})) { to perform the operation shown.
                "SELECT COUNT(*) FROM todos WHERE status=?", new String[]{status})) {
            // Returns c.moveToFirst() ? c.getInt(0) : 0 to the caller.
            return c.moveToFirst() ? c.getInt(0) : 0;
        // Closes the current method, control-flow, or type block.
        }
    // Closes the current method, control-flow, or type block.
    }

    // Builds and runs a database query using the supplied filters.
    public List<Todo> query(TodoQuery q) {
        // Initializes args with new ArrayList<>() so later statements can use the computed value.
        List<String> args = new ArrayList<>();
        // Initializes sql with the text or query fragment shown here.
        StringBuilder sql = new StringBuilder("SELECT t.* FROM todos t WHERE t.status=?");
        // Calls args.add with q.status to perform the operation shown.
        args.add(q.status);
        // If q.keyword != null && !q.keyword.trim().isEmpty() is true, { immediately.
        if (q.keyword != null && !q.keyword.trim().isEmpty()) {
            // Initializes like with the text or query fragment shown here.
            String like = "%" + q.keyword.trim().replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_") + "%";
            // Calls sql.append with " AND (t.title LIKE ? ESCAPE '\\' OR t.description LIKE ? ESCAPE '\\')" to perform the operation shown.
            sql.append(" AND (t.title LIKE ? ESCAPE '\\' OR t.description LIKE ? ESCAPE '\\')");
            // Calls args.add with like to perform the operation shown.
            args.add(like);
            // Calls args.add with like to perform the operation shown.
            args.add(like);
        // Closes the current method, control-flow, or type block.
        }
        // If q.tagId > 0 is true, { immediately.
        if (q.tagId > 0) {
            // Calls sql.append with " AND (EXISTS (SELECT 1 FROM todo_tags tt WHERE tt.todo_id=t.id AND tt.tag_id=?)" to perform the operation shown.
            sql.append(" AND (EXISTS (SELECT 1 FROM todo_tags tt WHERE tt.todo_id=t.id AND tt.tag_id=?)"
                    // Appends this literal fragment to the string or SQL query being assembled.
                    + " OR EXISTS (SELECT 1 FROM note_tags nt WHERE nt.note_id=t.note_id AND nt.tag_id=?))");
            // Calls args.add with String.valueOf(q.tagId to perform the operation shown.
            args.add(String.valueOf(q.tagId));
            // Calls args.add with String.valueOf(q.tagId to perform the operation shown.
            args.add(String.valueOf(q.tagId));
        // Closes the current method, control-flow, or type block.
        }
        // If q.overdueOnly is true, { immediately.
        if (q.overdueOnly) {
            // Calls sql.append with " AND t.due_at IS NOT NULL AND t.due_at<?" to perform the operation shown.
            sql.append(" AND t.due_at IS NOT NULL AND t.due_at<?");
            // Calls args.add with String.valueOf(System.currentTimeMillis( to perform the operation shown.
            args.add(String.valueOf(System.currentTimeMillis()));
        // Closes the current method, control-flow, or type block.
        }
        // If q.reminderOnly is true, sql.append(" AND t.reminder_enabled=1") immediately.
        if (q.reminderOnly) sql.append(" AND t.reminder_enabled=1");
        // If Constants.STATUS_COMPLETED.equals(q.status) is true, { immediately.
        if (Constants.STATUS_COMPLETED.equals(q.status)) {
            // Calls sql.append with " ORDER BY t.completed_at DESC" to perform the operation shown.
            sql.append(" ORDER BY t.completed_at DESC");
        // Runs the fallback branch when the preceding condition did not match.
        } else {
            // Calls sql.append with " ORDER BY (t.due_at IS NULL), t.due_at ASC, t.created_at DESC" to perform the operation shown.
            sql.append(" ORDER BY (t.due_at IS NULL), t.due_at ASC, t.created_at DESC");
        // Closes the current method, control-flow, or type block.
        }
        // Initializes list with new ArrayList<>() so later statements can use the computed value.
        List<Todo> list = new ArrayList<>();
        // Runs this operation with automatic cleanup for the resources declared in the try statement.
        try (Cursor c = helper.getReadableDatabase().rawQuery(sql.toString(), args.toArray(new String[0]))) {
            // If c.moveToNext() is true, list.add(map(c)) immediately.
            while (c.moveToNext()) list.add(map(c));
        // Closes the current method, control-flow, or type block.
        }
        // Calls attachTags with list to perform the operation shown.
        attachTags(list);
        // Returns list to the caller.
        return list;
    // Closes the current method, control-flow, or type block.
    }

    // Declares attachTags() to implement this part of the component.
    private void attachTags(List<Todo> list) {
        // Initializes byNote with tags.noteTagMap() so later statements can use the computed value.
        Map<Long, List<Tag>> byNote = tags.noteTagMap();
        // Initializes byTodo with tags.todoTagMap() so later statements can use the computed value.
        Map<Long, List<Tag>> byTodo = tags.todoTagMap();
        // Repeats the following block according to the initialization, condition, and update in Todo t : list.
        for (Todo t : list) {
            // Initializes found with t.noteId > 0 ? byNote.get(t.noteId) : byTodo.get(t.id) so later statements can use the computed value.
            List<Tag> found = t.noteId > 0 ? byNote.get(t.noteId) : byTodo.get(t.id);
            // Stores the result of found == null ? new ArrayList<Tag>() : found in t.tags for use by later logic.
            t.tags = found == null ? new ArrayList<Tag>() : found;
        // Closes the current method, control-flow, or type block.
        }
    // Closes the current method, control-flow, or type block.
    }

    /**
     * Saves a standalone task and (re)schedules or cancels its reminder in one consistent step.
     * A task has at most one alarm row, so editing a task can never create a duplicate reminder.
     */
    // Validates and persists the values currently entered in the form.
    public long save(Todo t, List<Long> tagIds) {
        // Initializes db with helper.getWritableDatabase() so later statements can use the computed value.
        SQLiteDatabase db = helper.getWritableDatabase();
        // Initializes wantAlarm with t.reminderEnabled && t.dueAt > 0 so later statements can use the computed value.
        boolean wantAlarm = t.reminderEnabled && t.dueAt > 0;
        // Initializes alarm with null so later statements can use the computed value.
        Alarm alarm = null;
        // Initializes removedAlarmId with -1 so later statements can use the computed value.
        long removedAlarmId = -1;
        // Calls db.beginTransaction with  to perform the operation shown.
        db.beginTransaction();
        // Starts the operation whose failures are handled by the following catch clauses.
        try {
            // Initializes v with new ContentValues() so later statements can use the computed value.
            ContentValues v = new ContentValues();
            // Calls v.put with "title", t.title.trim( to perform the operation shown.
            v.put("title", t.title.trim());
            // Calls v.put with "description", t.description == null ? "" : t.description.trim( to perform the operation shown.
            v.put("description", t.description == null ? "" : t.description.trim());
            // If t.dueAt > 0 is true, v.put("due_at", t.dueAt); else v.putNull("due_at") immediately.
            if (t.dueAt > 0) v.put("due_at", t.dueAt); else v.putNull("due_at");
            // Calls v.put with "reminder_enabled", wantAlarm ? 1 : 0 to perform the operation shown.
            v.put("reminder_enabled", wantAlarm ? 1 : 0);
            // Calls v.put with "repeat_type", t.repeatType to perform the operation shown.
            v.put("repeat_type", t.repeatType);
            // Calls v.put with "repeat_interval", Math.max(1, t.repeatInterval to perform the operation shown.
            v.put("repeat_interval", Math.max(1, t.repeatInterval));
            // If t.id == 0 is true, { immediately.
            if (t.id == 0) {
                // Calls v.put with "status", Constants.STATUS_PENDING to perform the operation shown.
                v.put("status", Constants.STATUS_PENDING);
                // Calls v.put with "created_at", System.currentTimeMillis( to perform the operation shown.
                v.put("created_at", System.currentTimeMillis());
                // Stores the result of db.insertOrThrow("todos", null, v) in t.id for use by later logic.
                t.id = db.insertOrThrow("todos", null, v);
            // Runs the fallback branch when the preceding condition did not match.
            } else {
                // Calls db.update with "todos", v, "id=?", new String[]{String.valueOf(t.id)} to perform the operation shown.
                db.update("todos", v, "id=?", new String[]{String.valueOf(t.id)});
            // Closes the current method, control-flow, or type block.
            }
            // Calls TagRepository.replaceTodoTags with db, t.id, tagIds to perform the operation shown.
            TagRepository.replaceTodoTags(db, t.id, tagIds);
            // If wantAlarm is true, { immediately.
            if (wantAlarm) {
                // Stores the result of AlarmRepository.upsert(db, "todo_id", t.id, t.dueAt, !t.completed, in alarm for use by later logic.
                alarm = AlarmRepository.upsert(db, "todo_id", t.id, t.dueAt, !t.completed,
                        // Executes t.repeatType, t.repeatInterval) as a statement.
                        t.repeatType, t.repeatInterval);
            // Runs the fallback branch when the preceding condition did not match.
            } else {
                // Stores the result of AlarmRepository.deleteByOwner(db, "todo_id", t.id) in removedAlarmId for use by later logic.
                removedAlarmId = AlarmRepository.deleteByOwner(db, "todo_id", t.id);
            // Closes the current method, control-flow, or type block.
            }
            // Calls db.setTransactionSuccessful with  to perform the operation shown.
            db.setTransactionSuccessful();
        // Continues the expression above with } finally {.
        } finally {
            // Calls db.endTransaction with  to perform the operation shown.
            db.endTransaction();
        // Closes the current method, control-flow, or type block.
        }
        // If alarm != null is true, { immediately.
        if (alarm != null) {
            // If alarm.enabled is true, AlarmScheduler.schedule(context, alarm) immediately.
            if (alarm.enabled) AlarmScheduler.schedule(context, alarm);
            // Runs the fallback branch when the preceding condition did not match.
            else AlarmScheduler.cancel(context, alarm.id);
        // Checks the alternate condition (removedAlarmId > 0) after the earlier branch did not run.
        } else if (removedAlarmId > 0) {
            // Calls AlarmScheduler.cancel with context, removedAlarmId to perform the operation shown.
            AlarmScheduler.cancel(context, removedAlarmId);
        // Closes the current method, control-flow, or type block.
        }
        // Returns t.id to the caller.
        return t.id;
    // Closes the current method, control-flow, or type block.
    }

    /** Marks a task completed or pending again, pausing/restoring its reminder. */
    // Updates completed.
    public void setCompleted(long id, boolean completed) {
        // Initializes t with get(id) so later statements can use the computed value.
        Todo t = get(id);
        // If t == null is true, return immediately.
        if (t == null) return;
        // Initializes db with helper.getWritableDatabase() so later statements can use the computed value.
        SQLiteDatabase db = helper.getWritableDatabase();
        // Initializes col with the text or query fragment shown here.
        String col = t.noteId > 0 ? "note_id" : "todo_id";
        // Initializes owner with t.noteId > 0 ? t.noteId : t.id so later statements can use the computed value.
        long owner = t.noteId > 0 ? t.noteId : t.id;
        // Initializes alarm with AlarmRepository.findByOwner(db, col, owner) so later statements can use the computed value.
        Alarm alarm = AlarmRepository.findByOwner(db, col, owner);
        // Initializes rearm with false so later statements can use the computed value.
        boolean rearm = false;
        // Initializes now with System.currentTimeMillis() so later statements can use the computed value.
        long now = System.currentTimeMillis();
        // Calls db.beginTransaction with  to perform the operation shown.
        db.beginTransaction();
        // Starts the operation whose failures are handled by the following catch clauses.
        try {
            // Initializes v with new ContentValues() so later statements can use the computed value.
            ContentValues v = new ContentValues();
            // Calls v.put with "status", completed ? Constants.STATUS_COMPLETED : Constants.STATUS_PENDING to perform the operation shown.
            v.put("status", completed ? Constants.STATUS_COMPLETED : Constants.STATUS_PENDING);
            // If completed is true, v.put("completed_at", now); else v.putNull("completed_at") immediately.
            if (completed) v.put("completed_at", now); else v.putNull("completed_at");
            // Calls db.update with "todos", v, "id=?", new String[]{String.valueOf(id)} to perform the operation shown.
            db.update("todos", v, "id=?", new String[]{String.valueOf(id)});
            // If alarm != null is true, { immediately.
            if (alarm != null) {
                // If completed is true, { immediately.
                if (completed) {
                    // Calls db.execSQL with "UPDATE alarms SET is_enabled=0 WHERE id=?", new Object[]{alarm.id} to perform the operation shown.
                    db.execSQL("UPDATE alarms SET is_enabled=0 WHERE id=?", new Object[]{alarm.id});
                // Checks the alternate condition (t.reminderEnabled) after the earlier branch did not run.
                } else if (t.reminderEnabled) {
                    // Initializes at with alarm.scheduledAt so later statements can use the computed value.
                    long at = alarm.scheduledAt;
                    // If at <= now is true, at = Repeat.nextOccurrence(at, alarm.repeatType, alarm.repeatInterval, now) immediately.
                    if (at <= now) at = Repeat.nextOccurrence(at, alarm.repeatType, alarm.repeatInterval, now);
                    // If at > now is true, { immediately.
                    if (at > now) {
                        // Calls db.execSQL with "UPDATE alarms SET is_enabled=1, scheduled_at=? WHERE id=?", to perform the operation shown.
                        db.execSQL("UPDATE alarms SET is_enabled=1, scheduled_at=? WHERE id=?",
                                // Creates the object described by new Object[]{at, alarm.id}).
                                new Object[]{at, alarm.id});
                        // Calls db.execSQL with "UPDATE todos SET due_at=? WHERE id=?", new Object[]{at, id} to perform the operation shown.
                        db.execSQL("UPDATE todos SET due_at=? WHERE id=?", new Object[]{at, id});
                        // Stores the result of at in alarm.scheduledAt for use by later logic.
                        alarm.scheduledAt = at;
                        // Stores the result of true in alarm.enabled for use by later logic.
                        alarm.enabled = true;
                        // Stores the result of true in rearm for use by later logic.
                        rearm = true;
                    // Closes the current method, control-flow, or type block.
                    }
                // Closes the current method, control-flow, or type block.
                }
            // Closes the current method, control-flow, or type block.
            }
            // Calls db.setTransactionSuccessful with  to perform the operation shown.
            db.setTransactionSuccessful();
        // Continues the expression above with } finally {.
        } finally {
            // Calls db.endTransaction with  to perform the operation shown.
            db.endTransaction();
        // Closes the current method, control-flow, or type block.
        }
        // If alarm != null is true, { immediately.
        if (alarm != null) {
            // If completed is true, AlarmScheduler.cancel(context, alarm.id) immediately.
            if (completed) AlarmScheduler.cancel(context, alarm.id);
            // Checks the alternate condition (rearm) after the earlier branch did not run.
            else if (rearm) AlarmScheduler.schedule(context, alarm);
        // Closes the current method, control-flow, or type block.
        }
        // If completed is true, { immediately.
        if (completed) {
            // Creates the object described by new NotificationRepository(helper).add(context.getString(R.string.notif_task_completed),.
            new NotificationRepository(helper).add(context.getString(R.string.notif_task_completed),
                    // Executes t.title, Constants.CATEGORY_TODO, t.noteId, t.id) as a statement.
                    t.title, Constants.CATEGORY_TODO, t.noteId, t.id);
        // Closes the current method, control-flow, or type block.
        }
    // Closes the current method, control-flow, or type block.
    }

    // Updates todo tags.
    public void setTodoTags(long todoId, List<Long> tagIds) {
        // Initializes db with helper.getWritableDatabase() so later statements can use the computed value.
        SQLiteDatabase db = helper.getWritableDatabase();
        // Calls db.beginTransaction with  to perform the operation shown.
        db.beginTransaction();
        // Starts the operation whose failures are handled by the following catch clauses.
        try {
            // Calls TagRepository.replaceTodoTags with db, todoId, tagIds to perform the operation shown.
            TagRepository.replaceTodoTags(db, todoId, tagIds);
            // Calls db.setTransactionSuccessful with  to perform the operation shown.
            db.setTransactionSuccessful();
        // Continues the expression above with } finally {.
        } finally {
            // Calls db.endTransaction with  to perform the operation shown.
            db.endTransaction();
        // Closes the current method, control-flow, or type block.
        }
    // Closes the current method, control-flow, or type block.
    }

    /** Deletes a standalone task together with its reminder. */
    // Declares delete() to implement this part of the component.
    public void delete(long id) {
        // Initializes t with get(id) so later statements can use the computed value.
        Todo t = get(id);
        // If t == null is true, return immediately.
        if (t == null) return;
        // Initializes db with helper.getWritableDatabase() so later statements can use the computed value.
        SQLiteDatabase db = helper.getWritableDatabase();
        // Initializes alarmId with -1 so later statements can use the computed value.
        long alarmId = -1;
        // Calls db.beginTransaction with  to perform the operation shown.
        db.beginTransaction();
        // Starts the operation whose failures are handled by the following catch clauses.
        try {
            // Runs this operation with automatic cleanup for the resources declared in the try statement.
            try (Cursor c = db.rawQuery("SELECT id FROM alarms WHERE todo_id=?", new String[]{String.valueOf(id)})) {
                // If c.moveToFirst() is true, alarmId = c.getLong(0) immediately.
                if (c.moveToFirst()) alarmId = c.getLong(0);
            // Closes the current method, control-flow, or type block.
            }
            // Calls db.delete with "todos", "id=?", new String[]{String.valueOf(id)} to perform the operation shown.
            db.delete("todos", "id=?", new String[]{String.valueOf(id)});
            // Calls db.setTransactionSuccessful with  to perform the operation shown.
            db.setTransactionSuccessful();
        // Continues the expression above with } finally {.
        } finally {
            // Calls db.endTransaction with  to perform the operation shown.
            db.endTransaction();
        // Closes the current method, control-flow, or type block.
        }
        // If alarmId > 0 is true, AlarmScheduler.cancel(context, alarmId) immediately.
        if (alarmId > 0) AlarmScheduler.cancel(context, alarmId);
    // Closes the current method, control-flow, or type block.
    }
// Closes the current method, control-flow, or type block.
}
