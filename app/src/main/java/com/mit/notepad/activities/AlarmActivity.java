package com.mit.notepad.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.PopupMenu;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.mit.notepad.R;
import com.mit.notepad.adapters.AlarmAdapter;
import com.mit.notepad.alarms.AlarmScheduler;
import com.mit.notepad.databinding.ActivityAlarmBinding;
import com.mit.notepad.models.Alarm;
import com.mit.notepad.models.Note;
import com.mit.notepad.models.Todo;
import com.mit.notepad.repositories.AlarmRepository;
import com.mit.notepad.repositories.NoteRepository;
import com.mit.notepad.repositories.TodoRepository;
import com.mit.notepad.utils.Constants;
import com.mit.notepad.utils.DateTimeUtils;
import com.mit.notepad.utils.RepeatLabels;
import com.mit.notepad.utils.UiUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Screen 8: create/edit an alarm for a note or task and manage all alarms.
 *
 * Modes:
 *  - direct: persists alarms through AlarmRepository (opened from the drawer, a note or a task);
 *  - result mode: used by the note editor, returns the chosen alarm settings without saving
 *    (the note may not exist yet) - the editor stores them when the note is saved.
 */
public class AlarmActivity extends BaseActivity implements AlarmAdapter.Listener {
    private ActivityAlarmBinding b;
    private AlarmRepository alarms;
    private NoteRepository notes;
    private TodoRepository todos;
    private AlarmAdapter adapter;

    private boolean resultMode;
    private boolean launchedWithTarget;
    private long targetNoteId;
    private long targetTodoId;
    private String targetLabel = "";
    private boolean hasExisting;

    private long alarmAt;
    private String repeat = Constants.REPEAT_NONE;
    private int interval = 1;
    private boolean updatingChips;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityAlarmBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        alarms = new AlarmRepository(this);
        notes = new NoteRepository(this);
        todos = new TodoRepository(this);

        setupBackHeader(b.header, R.string.title_set_alarm, R.string.subtitle_set_alarm);
        b.header.headerAction.setImageResource(R.drawable.ic_info);
        b.header.headerAction.setContentDescription(getString(R.string.alarm_info_title));
        b.header.headerAction.setVisibility(View.VISIBLE);
        b.header.headerAction.setOnClickListener(v -> new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.alarm_info_title)
                .setMessage(R.string.alarm_info_message)
                .setPositiveButton(R.string.ok, null).show());

        Intent in = getIntent();
        resultMode = in.getBooleanExtra(Constants.EXTRA_RESULT_MODE, false);
        targetNoteId = in.getLongExtra(Constants.EXTRA_NOTE_ID, 0);
        targetTodoId = in.getLongExtra(Constants.EXTRA_TODO_ID, 0);
        launchedWithTarget = resultMode || targetNoteId > 0 || targetTodoId > 0;

        adapter = new AlarmAdapter(this);
        b.recycler.setLayoutManager(new LinearLayoutManager(this));
        b.recycler.setAdapter(adapter);
        b.sectionActive.setVisibility(resultMode ? View.GONE : View.VISIBLE);
        b.btnChange.setVisibility(launchedWithTarget ? View.GONE : View.VISIBLE);

        if (savedInstanceState == null) {
            resetForm();
            if (resultMode) {
                targetLabel = in.getStringExtra(Constants.EXTRA_TARGET_LABEL);
                long at = in.getLongExtra(Constants.EXTRA_ALARM_AT, 0);
                if (at > 0) {
                    alarmAt = at;
                    repeat = in.getStringExtra(Constants.EXTRA_ALARM_REPEAT);
                    interval = in.getIntExtra(Constants.EXTRA_ALARM_INTERVAL, 1);
                    b.swEnable.setChecked(in.getBooleanExtra(Constants.EXTRA_ALARM_ENABLED, true));
                    hasExisting = true;
                }
            } else if (targetNoteId > 0 || targetTodoId > 0) {
                setTarget(targetNoteId, targetTodoId);
            }
        } else {
            alarmAt = savedInstanceState.getLong("at");
            repeat = savedInstanceState.getString("repeat", Constants.REPEAT_NONE);
            interval = savedInstanceState.getInt("interval", 1);
            targetLabel = savedInstanceState.getString("label", "");
            hasExisting = savedInstanceState.getBoolean("existing");
            targetNoteId = savedInstanceState.getLong("note");
            targetTodoId = savedInstanceState.getLong("todo");
        }

        b.btnDate.setOnClickListener(v -> pickDate());
        b.btnTime.setOnClickListener(v -> pickTime());
        b.chipGroup.setOnCheckedStateChangeListener((g, ids) -> onRepeatChipChanged());
        b.btnChange.setOnClickListener(v -> chooseTarget());
        b.btnSet.setOnClickListener(v -> saveAlarm());
        b.btnCancel.setOnClickListener(v -> finish());
        b.btnRemove.setOnClickListener(v -> removeAlarm());
        b.btnAllowExact.setOnClickListener(v -> openExactAlarmSettings());
        refreshForm();
    }

    @Override
    protected void onSaveInstanceState(Bundle out) {
        super.onSaveInstanceState(out);
        out.putLong("at", alarmAt);
        out.putString("repeat", repeat);
        out.putInt("interval", interval);
        out.putString("label", targetLabel);
        out.putBoolean("existing", hasExisting);
        out.putLong("note", targetNoteId);
        out.putLong("todo", targetTodoId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        b.bannerExact.setVisibility(AlarmScheduler.canScheduleExact(this) ? View.GONE : View.VISIBLE);
        loadList();
    }

    // ------------------------------------------------------------------ form state

    private void resetForm() {
        alarmAt = DateTimeUtils.nextFullHour();
        repeat = Constants.REPEAT_NONE;
        interval = 1;
        hasExisting = false;
        b.swEnable.setChecked(true);
    }

    private void setTarget(long noteId, long todoId) {
        targetNoteId = noteId;
        targetTodoId = todoId;
        resetForm();
        Alarm existing = null;
        if (noteId > 0) {
            Note n = notes.get(noteId);
            if (n == null) {
                targetNoteId = 0;
                targetLabel = "";
            } else {
                targetLabel = n.title;
                existing = alarms.getForNote(noteId);
            }
        } else if (todoId > 0) {
            Todo t = todos.get(todoId);
            if (t == null) {
                targetTodoId = 0;
                targetLabel = "";
            } else {
                targetLabel = t.title;
                existing = alarms.getForTodo(todoId);
            }
        } else {
            targetLabel = "";
        }
        if (existing != null) {
            alarmAt = existing.scheduledAt;
            repeat = existing.repeatType;
            interval = existing.repeatInterval;
            b.swEnable.setChecked(existing.enabled);
            hasExisting = true;
        }
        refreshForm();
    }

    private void refreshForm() {
        boolean hasTarget = resultMode || targetNoteId > 0 || targetTodoId > 0;
        b.tvTargetTitle.setText(hasTarget ? targetLabel : getString(R.string.no_target_selected));
        b.tvTargetHint.setText(hasTarget ? getString(targetTodoId > 0 ? R.string.target_task : R.string.target_note)
                : getString(R.string.choose_target_hint));
        b.btnDate.setText(DateTimeUtils.formatDate(alarmAt));
        b.btnTime.setText(DateTimeUtils.formatTime(alarmAt));
        updatingChips = true;
        int chip = Constants.REPEAT_DAILY.equals(repeat) ? R.id.chip_daily
                : Constants.REPEAT_WEEKLY.equals(repeat) ? R.id.chip_weekly
                : Constants.REPEAT_CUSTOM.equals(repeat) ? R.id.chip_custom : R.id.chip_none;
        b.chipGroup.check(chip);
        b.chipCustom.setText(Constants.REPEAT_CUSTOM.equals(repeat)
                ? RepeatLabels.label(this, repeat, interval) : getString(R.string.repeat_custom));
        updatingChips = false;
        b.tvRepeatHint.setText(Constants.REPEAT_NONE.equals(repeat) ? getString(R.string.repeat_hint_none)
                : getString(R.string.repeat_hint, RepeatLabels.label(this, repeat, interval)));
        b.btnRemove.setVisibility(hasExisting ? View.VISIBLE : View.GONE);
        b.btnSet.setText(hasExisting ? R.string.update_alarm : R.string.set_alarm);
    }

    private void onRepeatChipChanged() {
        if (updatingChips) return;
        int id = b.chipGroup.getCheckedChipId();
        if (id == R.id.chip_daily) {
            repeat = Constants.REPEAT_DAILY;
        } else if (id == R.id.chip_weekly) {
            repeat = Constants.REPEAT_WEEKLY;
        } else if (id == R.id.chip_custom) {
            RepeatDialogs.askInterval(this, interval, new RepeatDialogs.IntervalCallback() {
                @Override
                public void onInterval(int days) {
                    repeat = Constants.REPEAT_CUSTOM;
                    interval = days;
                    refreshForm();
                }

                @Override
                public void onCancel() {
                    refreshForm(); // restore previous selection
                }
            });
            return;
        } else {
            repeat = Constants.REPEAT_NONE;
        }
        refreshForm();
    }

    private void pickDate() {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(alarmAt);
        new DatePickerDialog(this, (view, y, m, d) -> {
            Calendar n = Calendar.getInstance();
            n.setTimeInMillis(alarmAt);
            n.set(y, m, d);
            alarmAt = n.getTimeInMillis();
            refreshForm();
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void pickTime() {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(alarmAt);
        new TimePickerDialog(this, (view, h, min) -> {
            Calendar n = Calendar.getInstance();
            n.setTimeInMillis(alarmAt);
            n.set(Calendar.HOUR_OF_DAY, h);
            n.set(Calendar.MINUTE, min);
            n.set(Calendar.SECOND, 0);
            n.set(Calendar.MILLISECOND, 0);
            alarmAt = n.getTimeInMillis();
            refreshForm();
        }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), false).show();
    }

    /** Lets the user pick any note or pending standalone task as the alarm target. */
    private void chooseTarget() {
        List<String> labels = new ArrayList<>();
        List<long[]> ids = new ArrayList<>(); // {noteId, todoId}
        for (Note n : notes.getAll()) {
            labels.add(getString(R.string.target_note_item, n.title));
            ids.add(new long[]{n.id, 0});
        }
        TodoRepository.TodoQuery q = new TodoRepository.TodoQuery();
        for (Todo t : todos.query(q)) {
            if (t.isLinkedToNote()) continue; // linked tasks are reached through their note
            labels.add(getString(R.string.target_task_item, t.title));
            ids.add(new long[]{0, t.id});
        }
        if (labels.isEmpty()) {
            UiUtils.toast(this, R.string.nothing_to_select);
            return;
        }
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.choose_target)
                .setItems(labels.toArray(new String[0]), (d, which) -> setTarget(ids.get(which)[0], ids.get(which)[1]))
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    // ------------------------------------------------------------------ actions

    private void saveAlarm() {
        boolean enabled = b.swEnable.isChecked();
        if (!resultMode && targetNoteId == 0 && targetTodoId == 0) {
            UiUtils.toast(this, R.string.error_select_target);
            return;
        }
        if (enabled && alarmAt <= System.currentTimeMillis()) {
            UiUtils.toast(this, R.string.error_alarm_in_past);
            return;
        }
        if (resultMode) {
            Intent result = new Intent()
                    .putExtra(Constants.EXTRA_ALARM_AT, alarmAt)
                    .putExtra(Constants.EXTRA_ALARM_REPEAT, repeat)
                    .putExtra(Constants.EXTRA_ALARM_INTERVAL, interval)
                    .putExtra(Constants.EXTRA_ALARM_ENABLED, enabled);
            setResult(RESULT_OK, result);
            finish();
            return;
        }
        try {
            if (targetNoteId > 0) alarms.saveForNote(targetNoteId, alarmAt, enabled, repeat, interval);
            else alarms.saveForTodo(targetTodoId, alarmAt, enabled, repeat, interval);
        } catch (RuntimeException e) {
            Log.e("AlarmActivity", "Failed to persist alarm (noteId=" + targetNoteId
                    + ", todoId=" + targetTodoId + ", at=" + alarmAt + ")", e);
            UiUtils.toast(this, R.string.error_save_failed);
            return;
        }
        UiUtils.toast(this, R.string.alarm_saved);
        if (launchedWithTarget) {
            finish();
        } else {
            targetNoteId = 0;
            targetTodoId = 0;
            targetLabel = "";
            resetForm();
            refreshForm();
            loadList();
        }
    }

    private void removeAlarm() {
        if (resultMode) {
            setResult(RESULT_OK, new Intent().putExtra(Constants.EXTRA_ALARM_REMOVE, true));
            finish();
            return;
        }
        UiUtils.confirm(this, R.string.remove_alarm_title, getString(R.string.remove_alarm_message, targetLabel),
                R.string.remove, () -> {
                    if (targetNoteId > 0) alarms.deleteForNote(targetNoteId);
                    else alarms.deleteForTodo(targetTodoId);
                    UiUtils.toast(this, R.string.alarm_removed);
                    if (launchedWithTarget) {
                        finish();
                    } else {
                        resetForm();
                        refreshForm();
                        loadList();
                    }
                });
    }

    private void openExactAlarmSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            startActivity(new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                    Uri.parse("package:" + getPackageName())));
        }
    }

    // ------------------------------------------------------------------ active alarms list

    private void loadList() {
        List<Alarm> list = alarms.getAll();
        adapter.setItems(list);
        b.tvAlarmCount.setText(getResources().getQuantityString(R.plurals.alarms_count, list.size(), list.size()));
        boolean empty = list.isEmpty();
        b.empty.getRoot().setVisibility(empty ? View.VISIBLE : View.GONE);
        b.recycler.setVisibility(empty ? View.GONE : View.VISIBLE);
        b.empty.emptyTitle.setText(R.string.empty_alarms_title);
        b.empty.emptyMessage.setText(R.string.empty_alarms_message);
    }

    @Override
    public void onToggle(Alarm alarm, boolean enabled) {
        int result = alarms.setEnabled(alarm.id, enabled);
        if (result == AlarmRepository.PAST) UiUtils.toast(this, R.string.error_alarm_passed_edit);
        b.getRoot().post(this::loadList);
    }

    @Override
    public void onMenu(Alarm alarm, View anchor) {
        PopupMenu pm = new PopupMenu(this, anchor);
        pm.inflate(R.menu.menu_alarm_item);
        pm.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_edit) {
                setTarget(alarm.noteId, alarm.todoId);
                b.scroll.smoothScrollTo(0, 0);
            } else if (item.getItemId() == R.id.action_delete) {
                UiUtils.confirm(this, R.string.remove_alarm_title,
                        getString(R.string.remove_alarm_message, alarm.ownerTitle), R.string.delete, () -> {
                            alarms.delete(alarm.id);
                            loadList();
                        });
            }
            return true;
        });
        pm.show();
    }
}
