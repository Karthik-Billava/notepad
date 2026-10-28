// Sets the package name to com.mit.notepad.activities so this class belongs to the app module namespace.
package com.mit.notepad.activities;

// Imports android.app.DatePickerDialog so this file can use that type by its short name.
import android.app.DatePickerDialog;
// Imports android.app.TimePickerDialog so this file can use that type by its short name.
import android.app.TimePickerDialog;
// Imports android.content.Intent so this file can use that type by its short name.
import android.content.Intent;
// Imports android.os.Bundle so this file can use that type by its short name.
import android.os.Bundle;
// Imports android.text.Editable so this file can use that type by its short name.
import android.text.Editable;
// Imports android.text.TextWatcher so this file can use that type by its short name.
import android.text.TextWatcher;
// Imports android.view.View so this file can use that type by its short name.
import android.view.View;

// Imports androidx.activity.OnBackPressedCallback so this file can use that type by its short name.
import androidx.activity.OnBackPressedCallback;

// Imports com.google.android.material.dialog.MaterialAlertDialogBuilder so this file can use that type by its short name.
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
// Imports com.mit.notepad.R so this file can use that type by its short name.
import com.mit.notepad.R;
// Imports com.mit.notepad.alarms.AlarmScheduler so this file can use that type by its short name.
import com.mit.notepad.alarms.AlarmScheduler;
// Imports com.mit.notepad.databinding.ActivityTodoEditorBinding so this file can use that type by its short name.
import com.mit.notepad.databinding.ActivityTodoEditorBinding;
// Imports com.mit.notepad.models.Tag so this file can use that type by its short name.
import com.mit.notepad.models.Tag;
// Imports com.mit.notepad.models.Todo so this file can use that type by its short name.
import com.mit.notepad.models.Todo;
// Imports com.mit.notepad.repositories.TodoRepository so this file can use that type by its short name.
import com.mit.notepad.repositories.TodoRepository;
// Imports com.mit.notepad.utils.Constants so this file can use that type by its short name.
import com.mit.notepad.utils.Constants;
// Imports com.mit.notepad.utils.DateTimeUtils so this file can use that type by its short name.
import com.mit.notepad.utils.DateTimeUtils;
// Imports com.mit.notepad.utils.RepeatLabels so this file can use that type by its short name.
import com.mit.notepad.utils.RepeatLabels;
// Imports com.mit.notepad.utils.UiUtils so this file can use that type by its short name.
import com.mit.notepad.utils.UiUtils;
// Imports com.mit.notepad.utils.Validator so this file can use that type by its short name.
import com.mit.notepad.utils.Validator;

// Imports java.util.ArrayList so this file can use that type by its short name.
import java.util.ArrayList;
// Imports java.util.Calendar so this file can use that type by its short name.
import java.util.Calendar;
// Imports java.util.List so this file can use that type by its short name.
import java.util.List;

/** Screen 7: add / edit a standalone to-do with due date, reminder, repeat and tags. */
// Declares class TodoEditorActivity to group related state and behavior.
public class TodoEditorActivity extends BaseActivity {
    // Initializes STATE_DUE with the text or query fragment shown here.
    private static final String STATE_DUE = "state_due";
    // Initializes STATE_HAS_DUE with the text or query fragment shown here.
    private static final String STATE_HAS_DUE = "state_has_due";
    // Initializes STATE_REPEAT with the text or query fragment shown here.
    private static final String STATE_REPEAT = "state_repeat";
    // Initializes STATE_INTERVAL with the text or query fragment shown here.
    private static final String STATE_INTERVAL = "state_interval";
    // Initializes STATE_TAGS with the text or query fragment shown here.
    private static final String STATE_TAGS = "state_tags";

    // Declares b for use by the surrounding method or class.
    private ActivityTodoEditorBinding b;
    // Declares repo for use by the surrounding method or class.
    private TodoRepository repo;
    // Declares existing for use by the surrounding method or class.
    private Todo existing;
    // Declares todoId for use by the surrounding method or class.
    private long todoId;

    // Declares dueAt for use by the surrounding method or class.
    private long dueAt;
    // Initializes hasDue with true so later statements can use the computed value.
    private boolean hasDue = true;
    // Initializes repeat with Constants.REPEAT_NONE so later statements can use the computed value.
    private String repeat = Constants.REPEAT_NONE;
    // Initializes interval with 1 so later statements can use the computed value.
    private int interval = 1;
    // Initializes tagIds with new ArrayList<>() so later statements can use the computed value.
    private List<Long> tagIds = new ArrayList<>();
    // Declares saved for use by the surrounding method or class.
    private boolean saved;
    // Declares dirty for use by the surrounding method or class.
    private boolean dirty;

    // Applies @Override metadata to the declaration that follows.
    @Override
    // Runs component initialization when Android invokes this lifecycle callback.
    protected void onCreate(Bundle savedInstanceState) {
        // Calls super.onCreate with savedInstanceState to perform the operation shown.
        super.onCreate(savedInstanceState);
        // Stores the result of ActivityTodoEditorBinding.inflate(getLayoutInflater()) in b for use by later logic.
        b = ActivityTodoEditorBinding.inflate(getLayoutInflater());
        // Calls setContentView with b.getRoot( to perform the operation shown.
        setContentView(b.getRoot());
        // Stores the result of new TodoRepository(this) in repo for use by later logic.
        repo = new TodoRepository(this);

        // Stores the result of getIntent().getLongExtra(Constants.EXTRA_TODO_ID, 0) in todoId for use by later logic.
        todoId = getIntent().getLongExtra(Constants.EXTRA_TODO_ID, 0);
        // If todoId > 0 is true, { immediately.
        if (todoId > 0) {
            // Stores the result of repo.get(todoId) in existing for use by later logic.
            existing = repo.get(todoId);
            // If existing == null is true, { immediately.
            if (existing == null) {
                // Calls UiUtils.toast with this, R.string.item_no_longer_exists to perform the operation shown.
                UiUtils.toast(this, R.string.item_no_longer_exists);
                // Calls finish with  to perform the operation shown.
                finish();
                // Returns control to the caller without a value.
                return;
            // Closes the current method, control-flow, or type block.
            }
            // If existing.isLinkedToNote() is true, { // tasks created from a note are edited as notes immediately.
            if (existing.isLinkedToNote()) { // tasks created from a note are edited as notes
                // Calls startActivity with new Intent(this, NoteEditorActivity.class to perform the operation shown.
                startActivity(new Intent(this, NoteEditorActivity.class)
                        // Calls putExtra on the object from the preceding line with the shown arguments.
                        .putExtra(Constants.EXTRA_NOTE_ID, existing.noteId));
                // Calls finish with  to perform the operation shown.
                finish();
                // Returns control to the caller without a value.
                return;
            // Closes the current method, control-flow, or type block.
            }
        // Closes the current method, control-flow, or type block.
        }
        // Calls setupBackHeader with b.header, todoId > 0 ? R.string.edit_todo_title : R.string.add_todo_title, to perform the operation shown.
        setupBackHeader(b.header, todoId > 0 ? R.string.edit_todo_title : R.string.add_todo_title,
                // Executes R.string.turn_note_into_action) as a statement.
                R.string.turn_note_into_action);
        // Calls b.header.headerAction.setImageResource with R.drawable.ic_check to perform the operation shown.
        b.header.headerAction.setImageResource(R.drawable.ic_check);
        // Calls b.header.headerAction.setContentDescription with getString(R.string.save to perform the operation shown.
        b.header.headerAction.setContentDescription(getString(R.string.save));
        // Calls b.header.headerAction.setVisibility with View.VISIBLE to perform the operation shown.
        b.header.headerAction.setVisibility(View.VISIBLE);
        // Calls b.header.headerAction.setOnClickListener with v -> save( to perform the operation shown.
        b.header.headerAction.setOnClickListener(v -> save());

        // If savedInstanceState == null is true, { immediately.
        if (savedInstanceState == null) {
            // Stores the result of DateTimeUtils.nextFullHour() in dueAt for use by later logic.
            dueAt = DateTimeUtils.nextFullHour();
            // If existing != null is true, { immediately.
            if (existing != null) {
                // Calls b.etTitle.setText with existing.title to perform the operation shown.
                b.etTitle.setText(existing.title);
                // Calls b.etDesc.setText with existing.description to perform the operation shown.
                b.etDesc.setText(existing.description);
                // Stores the result of existing.dueAt > 0 in hasDue for use by later logic.
                hasDue = existing.dueAt > 0;
                // If hasDue is true, dueAt = existing.dueAt immediately.
                if (hasDue) dueAt = existing.dueAt;
                // Calls b.swReminder.setChecked with existing.reminderEnabled to perform the operation shown.
                b.swReminder.setChecked(existing.reminderEnabled);
                // Stores the result of existing.repeatType in repeat for use by later logic.
                repeat = existing.repeatType;
                // Stores the result of existing.repeatInterval in interval for use by later logic.
                interval = existing.repeatInterval;
                // Repeats the following block according to the initialization, condition, and update in Tag t : existing.tags) tagIds.add(t.id.
                for (Tag t : existing.tags) tagIds.add(t.id);
            // Runs the fallback branch when the preceding condition did not match.
            } else {
                // Calls b.swReminder.setChecked with true to perform the operation shown.
                b.swReminder.setChecked(true);
            // Closes the current method, control-flow, or type block.
            }
        // Runs the fallback branch when the preceding condition did not match.
        } else {
            // Stores the result of savedInstanceState.getLong(STATE_DUE) in dueAt for use by later logic.
            dueAt = savedInstanceState.getLong(STATE_DUE);
            // Stores the result of savedInstanceState.getBoolean(STATE_HAS_DUE, true) in hasDue for use by later logic.
            hasDue = savedInstanceState.getBoolean(STATE_HAS_DUE, true);
            // Stores the result of savedInstanceState.getString(STATE_REPEAT, Constants.REPEAT_NONE) in repeat for use by later logic.
            repeat = savedInstanceState.getString(STATE_REPEAT, Constants.REPEAT_NONE);
            // Stores the result of savedInstanceState.getInt(STATE_INTERVAL, 1) in interval for use by later logic.
            interval = savedInstanceState.getInt(STATE_INTERVAL, 1);
            // Calls savedInstanceState.getLongArray with STATE_TAGS to perform the operation shown.
            long[] arr = savedInstanceState.getLongArray(STATE_TAGS);
            // If arr != null is true, for (long id : arr) tagIds.add(id) immediately.
            if (arr != null) for (long id : arr) tagIds.add(id);
        // Closes the current method, control-flow, or type block.
        }

        // Initializes watcher with new TextWatcher() { so later statements can use the computed value.
        TextWatcher watcher = new TextWatcher() {
            // Applies @Override public void beforeTextChanged metadata to the declaration that follows.
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) { }
            // Applies @Override public void onTextChanged metadata to the declaration that follows.
            @Override public void onTextChanged(CharSequence s, int st, int bf, int c) { }
            // Applies @Override metadata to the declaration that follows.
            @Override
            // Runs the registered update after the text value changes.
            public void afterTextChanged(Editable s) {
                // Stores the result of true in dirty for use by later logic.
                dirty = true;
                // Calls updateCounters with  to perform the operation shown.
                updateCounters();
            // Closes the current method, control-flow, or type block.
            }
        // Opens the block whose statements implement the declaration or branch above.
        };
        // Calls b.etTitle.addTextChangedListener with watcher to perform the operation shown.
        b.etTitle.addTextChangedListener(watcher);
        // Calls b.etDesc.addTextChangedListener with watcher to perform the operation shown.
        b.etDesc.addTextChangedListener(watcher);
        // Stores the result of false in dirty for use by later logic.
        dirty = false;

        // Calls b.btnDate.setOnClickListener with v -> pickDate( to perform the operation shown.
        b.btnDate.setOnClickListener(v -> pickDate());
        // Calls b.btnTime.setOnClickListener with v -> pickTime( to perform the operation shown.
        b.btnTime.setOnClickListener(v -> pickTime());
        // Starts the callback body that handles the event or asynchronous result.
        b.btnClearDue.setOnClickListener(v -> {
            // Stores the result of false in hasDue for use by later logic.
            hasDue = false;
            // Calls b.swReminder.setChecked with false to perform the operation shown.
            b.swReminder.setChecked(false);
            // Stores the result of true in dirty for use by later logic.
            dirty = true;
            // Calls refresh with  to perform the operation shown.
            refresh();
        // Executes }) as a statement.
        });
        // Starts the callback body that handles the event or asynchronous result.
        b.rowRepeat.setOnClickListener(v -> RepeatDialogs.pickRepeat(this, repeat, interval, (type, days) -> {
            // Stores the result of type in repeat for use by later logic.
            repeat = type;
            // Stores the result of days in interval for use by later logic.
            interval = days;
            // Stores the result of true in dirty for use by later logic.
            dirty = true;
            // Calls refresh with  to perform the operation shown.
            refresh();
        // Executes })) as a statement.
        }));
        // Starts the callback body that handles the event or asynchronous result.
        b.rowTags.setOnClickListener(v -> TagDialogs.pickTags(this, tagIds, ids -> {
            // Stores the result of ids in tagIds for use by later logic.
            tagIds = ids;
            // Stores the result of true in dirty for use by later logic.
            dirty = true;
            // Calls refresh with  to perform the operation shown.
            refresh();
        // Executes })) as a statement.
        }));
        // Starts the callback body that handles the event or asynchronous result.
        b.swReminder.setOnCheckedChangeListener((v, checked) -> {
            // Stores the result of true in dirty for use by later logic.
            dirty = true;
            // If checked && !hasDue is true, { immediately.
            if (checked && !hasDue) {
                // Stores the result of true in hasDue for use by later logic.
                hasDue = true;
                // Stores the result of DateTimeUtils.nextFullHour() in dueAt for use by later logic.
                dueAt = DateTimeUtils.nextFullHour();
                // Calls refresh with  to perform the operation shown.
                refresh();
            // Closes the current method, control-flow, or type block.
            }
        // Executes }) as a statement.
        });
        // Calls b.btnSave.setOnClickListener with v -> save( to perform the operation shown.
        b.btnSave.setOnClickListener(v -> save());
        // Calls b.btnCancel.setOnClickListener with v -> getOnBackPressedDispatcher().onBackPressed( to perform the operation shown.
        b.btnCancel.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        // Calls b.tvExactHint.setText with AlarmScheduler.canScheduleExact(this to perform the operation shown.
        b.tvExactHint.setText(AlarmScheduler.canScheduleExact(this)
                // Executes ? R.string.reminder_info : R.string.reminder_info_inexact) as a statement.
                ? R.string.reminder_info : R.string.reminder_info_inexact);

        // Calls getOnBackPressedDispatcher with ).addCallback(this, new OnBackPressedCallback(true) { to perform the operation shown.
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            // Applies @Override metadata to the declaration that follows.
            @Override
            // Closes the drawer, returns to the default tab, or exits the screen on Back.
            public void handleOnBackPressed() {
                // If dirty && !saved is true, { immediately.
                if (dirty && !saved) {
                    // Creates the object described by new MaterialAlertDialogBuilder(TodoEditorActivity.this).
                    new MaterialAlertDialogBuilder(TodoEditorActivity.this)
                            // Calls setTitle on the object from the preceding line with the shown arguments.
                            .setTitle(R.string.discard_title)
                            // Calls setMessage on the object from the preceding line with the shown arguments.
                            .setMessage(R.string.discard_message)
                            // Calls setPositiveButton on the object from the preceding line with the shown arguments.
                            .setPositiveButton(R.string.discard, (d, w) -> finish())
                            // Calls setNegativeButton on the object from the preceding line with the shown arguments.
                            .setNegativeButton(R.string.keep_editing, null)
                            // Calls show on the object from the preceding line with the shown arguments.
                            .show();
                // Runs the fallback branch when the preceding condition did not match.
                } else {
                    // Calls finish with  to perform the operation shown.
                    finish();
                // Closes the current method, control-flow, or type block.
                }
            // Closes the current method, control-flow, or type block.
            }
        // Executes }) as a statement.
        });
        // Calls updateCounters with  to perform the operation shown.
        updateCounters();
        // Calls refresh with  to perform the operation shown.
        refresh();
    // Closes the current method, control-flow, or type block.
    }

    // Applies @Override metadata to the declaration that follows.
    @Override
    // Stores transient UI values so Android can restore them later.
    protected void onSaveInstanceState(Bundle out) {
        // Calls super.onSaveInstanceState with out to perform the operation shown.
        super.onSaveInstanceState(out);
        // Calls out.putLong with STATE_DUE, dueAt to perform the operation shown.
        out.putLong(STATE_DUE, dueAt);
        // Calls out.putBoolean with STATE_HAS_DUE, hasDue to perform the operation shown.
        out.putBoolean(STATE_HAS_DUE, hasDue);
        // Calls out.putString with STATE_REPEAT, repeat to perform the operation shown.
        out.putString(STATE_REPEAT, repeat);
        // Calls out.putInt with STATE_INTERVAL, interval to perform the operation shown.
        out.putInt(STATE_INTERVAL, interval);
        // Calls tagIds.size with )] to perform the operation shown.
        long[] arr = new long[tagIds.size()];
        // Repeats the following block according to the initialization, condition, and update in int i = 0; i < arr.length; i++) arr[i] = tagIds.get(i.
        for (int i = 0; i < arr.length; i++) arr[i] = tagIds.get(i);
        // Calls out.putLongArray with STATE_TAGS, arr to perform the operation shown.
        out.putLongArray(STATE_TAGS, arr);
    // Closes the current method, control-flow, or type block.
    }

    // Updates counters.
    private void updateCounters() {
        // Calls b.tvTitleCounter.setText with getString(R.string.counter, b.etTitle.getText().length(), Constants.MAX_TITLE_LENGTH to perform the operation shown.
        b.tvTitleCounter.setText(getString(R.string.counter, b.etTitle.getText().length(), Constants.MAX_TITLE_LENGTH));
        // Calls b.tvDescCounter.setText with getString(R.string.counter, b.etDesc.getText().length(), Constants.MAX_DESCRIPTION_LENGTH to perform the operation shown.
        b.tvDescCounter.setText(getString(R.string.counter, b.etDesc.getText().length(), Constants.MAX_DESCRIPTION_LENGTH));
    // Closes the current method, control-flow, or type block.
    }

    // Declares refresh() to implement this part of the component.
    private void refresh() {
        // Calls b.btnDate.setText with hasDue ? DateTimeUtils.formatDate(dueAt) : getString(R.string.select_date to perform the operation shown.
        b.btnDate.setText(hasDue ? DateTimeUtils.formatDate(dueAt) : getString(R.string.select_date));
        // Calls b.btnTime.setText with hasDue ? DateTimeUtils.formatTime(dueAt) : getString(R.string.select_time to perform the operation shown.
        b.btnTime.setText(hasDue ? DateTimeUtils.formatTime(dueAt) : getString(R.string.select_time));
        // Calls b.btnClearDue.setVisibility with hasDue ? View.VISIBLE : View.GONE to perform the operation shown.
        b.btnClearDue.setVisibility(hasDue ? View.VISIBLE : View.GONE);
        // Calls b.tvRepeatValue.setText with RepeatLabels.label(this, repeat, interval to perform the operation shown.
        b.tvRepeatValue.setText(RepeatLabels.label(this, repeat, interval));
        // Calls b.tvTagsValue.setText with TagDialogs.namesFor(this, tagIds to perform the operation shown.
        b.tvTagsValue.setText(TagDialogs.namesFor(this, tagIds));
    // Closes the current method, control-flow, or type block.
    }

    // Prompts the user to choose date.
    private void pickDate() {
        // Initializes c with Calendar.getInstance() so later statements can use the computed value.
        Calendar c = Calendar.getInstance();
        // Calls c.setTimeInMillis with hasDue ? dueAt : DateTimeUtils.nextFullHour( to perform the operation shown.
        c.setTimeInMillis(hasDue ? dueAt : DateTimeUtils.nextFullHour());
        // Creates the object described by new DatePickerDialog(this, (view, y, m, d) -> {.
        new DatePickerDialog(this, (view, y, m, d) -> {
            // Initializes n with Calendar.getInstance() so later statements can use the computed value.
            Calendar n = Calendar.getInstance();
            // Calls n.setTimeInMillis with hasDue ? dueAt : DateTimeUtils.nextFullHour( to perform the operation shown.
            n.setTimeInMillis(hasDue ? dueAt : DateTimeUtils.nextFullHour());
            // Calls n.set with y, m, d to perform the operation shown.
            n.set(y, m, d);
            // Stores the result of n.getTimeInMillis() in dueAt for use by later logic.
            dueAt = n.getTimeInMillis();
            // Stores the result of true in hasDue for use by later logic.
            hasDue = true;
            // Stores the result of true in dirty for use by later logic.
            dirty = true;
            // Calls refresh with  to perform the operation shown.
            refresh();
        // Calls c.get with Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show( to perform the operation shown.
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    // Closes the current method, control-flow, or type block.
    }

    // Prompts the user to choose time.
    private void pickTime() {
        // Initializes c with Calendar.getInstance() so later statements can use the computed value.
        Calendar c = Calendar.getInstance();
        // Calls c.setTimeInMillis with hasDue ? dueAt : DateTimeUtils.nextFullHour( to perform the operation shown.
        c.setTimeInMillis(hasDue ? dueAt : DateTimeUtils.nextFullHour());
        // Creates the object described by new TimePickerDialog(this, (view, h, min) -> {.
        new TimePickerDialog(this, (view, h, min) -> {
            // Initializes n with Calendar.getInstance() so later statements can use the computed value.
            Calendar n = Calendar.getInstance();
            // Calls n.setTimeInMillis with hasDue ? dueAt : DateTimeUtils.nextFullHour( to perform the operation shown.
            n.setTimeInMillis(hasDue ? dueAt : DateTimeUtils.nextFullHour());
            // Calls n.set with Calendar.HOUR_OF_DAY, h to perform the operation shown.
            n.set(Calendar.HOUR_OF_DAY, h);
            // Calls n.set with Calendar.MINUTE, min to perform the operation shown.
            n.set(Calendar.MINUTE, min);
            // Calls n.set with Calendar.SECOND, 0 to perform the operation shown.
            n.set(Calendar.SECOND, 0);
            // Calls n.set with Calendar.MILLISECOND, 0 to perform the operation shown.
            n.set(Calendar.MILLISECOND, 0);
            // Stores the result of n.getTimeInMillis() in dueAt for use by later logic.
            dueAt = n.getTimeInMillis();
            // Stores the result of true in hasDue for use by later logic.
            hasDue = true;
            // Stores the result of true in dirty for use by later logic.
            dirty = true;
            // Calls refresh with  to perform the operation shown.
            refresh();
        // Calls c.get with Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), false).show( to perform the operation shown.
        }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), false).show();
    // Closes the current method, control-flow, or type block.
    }

    // Validates and persists the values currently entered in the form.
    private void save() {
        // Initializes title with b.etTitle.getText().toString().trim() so later statements can use the computed value.
        String title = b.etTitle.getText().toString().trim();
        // Initializes error with Validator.titleError(title) so later statements can use the computed value.
        int error = Validator.titleError(title);
        // If error != 0 is true, { immediately.
        if (error != 0) {
            // Calls b.etTitle.setError with getString(error to perform the operation shown.
            b.etTitle.setError(getString(error));
            // Calls b.etTitle.requestFocus with  to perform the operation shown.
            b.etTitle.requestFocus();
            // Returns control to the caller without a value.
            return;
        // Closes the current method, control-flow, or type block.
        }
        // Initializes reminder with b.swReminder.isChecked() so later statements can use the computed value.
        boolean reminder = b.swReminder.isChecked();
        // If reminder && !hasDue is true, { immediately.
        if (reminder && !hasDue) {
            // Calls UiUtils.toast with this, R.string.error_reminder_needs_due to perform the operation shown.
            UiUtils.toast(this, R.string.error_reminder_needs_due);
            // Returns control to the caller without a value.
            return;
        // Closes the current method, control-flow, or type block.
        }
        // If reminder && dueAt <= System.currentTimeMillis() is true, { immediately.
        if (reminder && dueAt <= System.currentTimeMillis()) {
            // Calls UiUtils.toast with this, R.string.error_reminder_in_past to perform the operation shown.
            UiUtils.toast(this, R.string.error_reminder_in_past);
            // Returns control to the caller without a value.
            return;
        // Closes the current method, control-flow, or type block.
        }
        // Initializes t with existing != null ? existing : new Todo() so later statements can use the computed value.
        Todo t = existing != null ? existing : new Todo();
        // Stores the result of title in t.title for use by later logic.
        t.title = title;
        // Stores the result of b.etDesc.getText().toString().trim() in t.description for use by later logic.
        t.description = b.etDesc.getText().toString().trim();
        // Stores the result of hasDue ? dueAt : 0 in t.dueAt for use by later logic.
        t.dueAt = hasDue ? dueAt : 0;
        // Stores the result of reminder in t.reminderEnabled for use by later logic.
        t.reminderEnabled = reminder;
        // Stores the result of repeat in t.repeatType for use by later logic.
        t.repeatType = repeat;
        // Stores the result of interval in t.repeatInterval for use by later logic.
        t.repeatInterval = interval;
        // Starts the operation whose failures are handled by the following catch clauses.
        try {
            // Calls repo.save with t, tagIds to perform the operation shown.
            repo.save(t, tagIds);
        // Calls catch with RuntimeException e) { to perform the operation shown.
        } catch (RuntimeException e) {
            // Calls UiUtils.toast with this, R.string.error_save_failed to perform the operation shown.
            UiUtils.toast(this, R.string.error_save_failed);
            // Returns control to the caller without a value.
            return;
        // Closes the current method, control-flow, or type block.
        }
        // Stores the result of true in saved for use by later logic.
        saved = true;
        // Calls UiUtils.toast with this, R.string.task_saved to perform the operation shown.
        UiUtils.toast(this, R.string.task_saved);
        // Calls finish with  to perform the operation shown.
        finish();
    // Closes the current method, control-flow, or type block.
    }
// Closes the current method, control-flow, or type block.
}
