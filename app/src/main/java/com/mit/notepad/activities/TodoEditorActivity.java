package com.mit.notepad.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.activity.OnBackPressedCallback;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.mit.notepad.R;
import com.mit.notepad.alarms.AlarmScheduler;
import com.mit.notepad.databinding.ActivityTodoEditorBinding;
import com.mit.notepad.models.Tag;
import com.mit.notepad.models.Todo;
import com.mit.notepad.repositories.TodoRepository;
import com.mit.notepad.utils.Constants;
import com.mit.notepad.utils.DateTimeUtils;
import com.mit.notepad.utils.RepeatLabels;
import com.mit.notepad.utils.UiUtils;
import com.mit.notepad.utils.Validator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/** Screen 7: add / edit a standalone to-do with due date, reminder, repeat and tags. */
public class TodoEditorActivity extends BaseActivity {
    private static final String STATE_DUE = "state_due";
    private static final String STATE_HAS_DUE = "state_has_due";
    private static final String STATE_REPEAT = "state_repeat";
    private static final String STATE_INTERVAL = "state_interval";
    private static final String STATE_TAGS = "state_tags";

    private ActivityTodoEditorBinding b;
    private TodoRepository repo;
    private Todo existing;
    private long todoId;

    private long dueAt;
    private boolean hasDue = true;
    private String repeat = Constants.REPEAT_NONE;
    private int interval = 1;
    private List<Long> tagIds = new ArrayList<>();
    private boolean saved;
    private boolean dirty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityTodoEditorBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        repo = new TodoRepository(this);

        todoId = getIntent().getLongExtra(Constants.EXTRA_TODO_ID, 0);
        if (todoId > 0) {
            existing = repo.get(todoId);
            if (existing == null) {
                UiUtils.toast(this, R.string.item_no_longer_exists);
                finish();
                return;
            }
            if (existing.isLinkedToNote()) { // tasks created from a note are edited as notes
                startActivity(new Intent(this, NoteEditorActivity.class)
                        .putExtra(Constants.EXTRA_NOTE_ID, existing.noteId));
                finish();
                return;
            }
        }
        setupBackHeader(b.header, todoId > 0 ? R.string.edit_todo_title : R.string.add_todo_title,
                R.string.turn_note_into_action);
        b.header.headerAction.setImageResource(R.drawable.ic_check);
        b.header.headerAction.setContentDescription(getString(R.string.save));
        b.header.headerAction.setVisibility(View.VISIBLE);
        b.header.headerAction.setOnClickListener(v -> save());

        if (savedInstanceState == null) {
            dueAt = DateTimeUtils.nextFullHour();
            if (existing != null) {
                b.etTitle.setText(existing.title);
                b.etDesc.setText(existing.description);
                hasDue = existing.dueAt > 0;
                if (hasDue) dueAt = existing.dueAt;
                b.swReminder.setChecked(existing.reminderEnabled);
                repeat = existing.repeatType;
                interval = existing.repeatInterval;
                for (Tag t : existing.tags) tagIds.add(t.id);
            } else {
                b.swReminder.setChecked(true);
            }
        } else {
            dueAt = savedInstanceState.getLong(STATE_DUE);
            hasDue = savedInstanceState.getBoolean(STATE_HAS_DUE, true);
            repeat = savedInstanceState.getString(STATE_REPEAT, Constants.REPEAT_NONE);
            interval = savedInstanceState.getInt(STATE_INTERVAL, 1);
            long[] arr = savedInstanceState.getLongArray(STATE_TAGS);
            if (arr != null) for (long id : arr) tagIds.add(id);
        }

        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) { }
            @Override public void onTextChanged(CharSequence s, int st, int bf, int c) { }
            @Override
            public void afterTextChanged(Editable s) {
                dirty = true;
                updateCounters();
            }
        };
        b.etTitle.addTextChangedListener(watcher);
        b.etDesc.addTextChangedListener(watcher);
        dirty = false;

        b.btnDate.setOnClickListener(v -> pickDate());
        b.btnTime.setOnClickListener(v -> pickTime());
        b.btnClearDue.setOnClickListener(v -> {
            hasDue = false;
            b.swReminder.setChecked(false);
            dirty = true;
            refresh();
        });
        b.rowRepeat.setOnClickListener(v -> RepeatDialogs.pickRepeat(this, repeat, interval, (type, days) -> {
            repeat = type;
            interval = days;
            dirty = true;
            refresh();
        }));
        b.rowTags.setOnClickListener(v -> TagDialogs.pickTags(this, tagIds, ids -> {
            tagIds = ids;
            dirty = true;
            refresh();
        }));
        b.swReminder.setOnCheckedChangeListener((v, checked) -> {
            dirty = true;
            if (checked && !hasDue) {
                hasDue = true;
                dueAt = DateTimeUtils.nextFullHour();
                refresh();
            }
        });
        b.btnSave.setOnClickListener(v -> save());
        b.btnCancel.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        b.tvExactHint.setText(AlarmScheduler.canScheduleExact(this)
                ? R.string.reminder_info : R.string.reminder_info_inexact);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (dirty && !saved) {
                    new MaterialAlertDialogBuilder(TodoEditorActivity.this)
                            .setTitle(R.string.discard_title)
                            .setMessage(R.string.discard_message)
                            .setPositiveButton(R.string.discard, (d, w) -> finish())
                            .setNegativeButton(R.string.keep_editing, null)
                            .show();
                } else {
                    finish();
                }
            }
        });
        updateCounters();
        refresh();
    }

    @Override
    protected void onSaveInstanceState(Bundle out) {
        super.onSaveInstanceState(out);
        out.putLong(STATE_DUE, dueAt);
        out.putBoolean(STATE_HAS_DUE, hasDue);
        out.putString(STATE_REPEAT, repeat);
        out.putInt(STATE_INTERVAL, interval);
        long[] arr = new long[tagIds.size()];
        for (int i = 0; i < arr.length; i++) arr[i] = tagIds.get(i);
        out.putLongArray(STATE_TAGS, arr);
    }

    private void updateCounters() {
        b.tvTitleCounter.setText(getString(R.string.counter, b.etTitle.getText().length(), Constants.MAX_TITLE_LENGTH));
        b.tvDescCounter.setText(getString(R.string.counter, b.etDesc.getText().length(), Constants.MAX_DESCRIPTION_LENGTH));
    }

    private void refresh() {
        b.btnDate.setText(hasDue ? DateTimeUtils.formatDate(dueAt) : getString(R.string.select_date));
        b.btnTime.setText(hasDue ? DateTimeUtils.formatTime(dueAt) : getString(R.string.select_time));
        b.btnClearDue.setVisibility(hasDue ? View.VISIBLE : View.GONE);
        b.tvRepeatValue.setText(RepeatLabels.label(this, repeat, interval));
        b.tvTagsValue.setText(TagDialogs.namesFor(this, tagIds));
    }

    private void pickDate() {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(hasDue ? dueAt : DateTimeUtils.nextFullHour());
        new DatePickerDialog(this, (view, y, m, d) -> {
            Calendar n = Calendar.getInstance();
            n.setTimeInMillis(hasDue ? dueAt : DateTimeUtils.nextFullHour());
            n.set(y, m, d);
            dueAt = n.getTimeInMillis();
            hasDue = true;
            dirty = true;
            refresh();
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void pickTime() {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(hasDue ? dueAt : DateTimeUtils.nextFullHour());
        new TimePickerDialog(this, (view, h, min) -> {
            Calendar n = Calendar.getInstance();
            n.setTimeInMillis(hasDue ? dueAt : DateTimeUtils.nextFullHour());
            n.set(Calendar.HOUR_OF_DAY, h);
            n.set(Calendar.MINUTE, min);
            n.set(Calendar.SECOND, 0);
            n.set(Calendar.MILLISECOND, 0);
            dueAt = n.getTimeInMillis();
            hasDue = true;
            dirty = true;
            refresh();
        }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), false).show();
    }

    private void save() {
        String title = b.etTitle.getText().toString().trim();
        int error = Validator.titleError(title);
        if (error != 0) {
            b.etTitle.setError(getString(error));
            b.etTitle.requestFocus();
            return;
        }
        boolean reminder = b.swReminder.isChecked();
        if (reminder && !hasDue) {
            UiUtils.toast(this, R.string.error_reminder_needs_due);
            return;
        }
        if (reminder && dueAt <= System.currentTimeMillis()) {
            UiUtils.toast(this, R.string.error_reminder_in_past);
            return;
        }
        Todo t = existing != null ? existing : new Todo();
        t.title = title;
        t.description = b.etDesc.getText().toString().trim();
        t.dueAt = hasDue ? dueAt : 0;
        t.reminderEnabled = reminder;
        t.repeatType = repeat;
        t.repeatInterval = interval;
        try {
            repo.save(t, tagIds);
        } catch (RuntimeException e) {
            UiUtils.toast(this, R.string.error_save_failed);
            return;
        }
        saved = true;
        UiUtils.toast(this, R.string.task_saved);
        finish();
    }
}
