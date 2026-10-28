// Sets the package name to com.mit.notepad.activities so this class belongs to the app module namespace.
package com.mit.notepad.activities;

// Imports android.app.DatePickerDialog so this file can use that type by its short name.
import android.app.DatePickerDialog;
// Imports android.app.TimePickerDialog so this file can use that type by its short name.
import android.app.TimePickerDialog;
// Imports android.content.Intent so this file can use that type by its short name.
import android.content.Intent;
// Imports android.net.Uri so this file can use that type by its short name.
import android.net.Uri;
// Imports android.os.Build so this file can use that type by its short name.
import android.os.Build;
// Imports android.os.Bundle so this file can use that type by its short name.
import android.os.Bundle;
// Imports android.provider.Settings so this file can use that type by its short name.
import android.provider.Settings;
// Imports android.util.Log so this file can use that type by its short name.
import android.util.Log;
// Imports android.view.View so this file can use that type by its short name.
import android.view.View;
// Imports android.widget.PopupMenu so this file can use that type by its short name.
import android.widget.PopupMenu;

// Imports androidx.recyclerview.widget.LinearLayoutManager so this file can use that type by its short name.
import androidx.recyclerview.widget.LinearLayoutManager;

// Imports com.google.android.material.dialog.MaterialAlertDialogBuilder so this file can use that type by its short name.
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
// Imports com.mit.notepad.R so this file can use that type by its short name.
import com.mit.notepad.R;
// Imports com.mit.notepad.adapters.AlarmAdapter so this file can use that type by its short name.
import com.mit.notepad.adapters.AlarmAdapter;
// Imports com.mit.notepad.alarms.AlarmScheduler so this file can use that type by its short name.
import com.mit.notepad.alarms.AlarmScheduler;
// Imports com.mit.notepad.databinding.ActivityAlarmBinding so this file can use that type by its short name.
import com.mit.notepad.databinding.ActivityAlarmBinding;
// Imports com.mit.notepad.models.Alarm so this file can use that type by its short name.
import com.mit.notepad.models.Alarm;
// Imports com.mit.notepad.models.Note so this file can use that type by its short name.
import com.mit.notepad.models.Note;
// Imports com.mit.notepad.models.Todo so this file can use that type by its short name.
import com.mit.notepad.models.Todo;
// Imports com.mit.notepad.repositories.AlarmRepository so this file can use that type by its short name.
import com.mit.notepad.repositories.AlarmRepository;
// Imports com.mit.notepad.repositories.NoteRepository so this file can use that type by its short name.
import com.mit.notepad.repositories.NoteRepository;
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

// Imports java.util.ArrayList so this file can use that type by its short name.
import java.util.ArrayList;
// Imports java.util.Calendar so this file can use that type by its short name.
import java.util.Calendar;
// Imports java.util.List so this file can use that type by its short name.
import java.util.List;

/**
 * Screen 8: create/edit an alarm for a note or task and manage all alarms.
 *
 * Modes:
 *  - direct: persists alarms through AlarmRepository (opened from the drawer, a note or a task);
 *  - result mode: used by the note editor, returns the chosen alarm settings without saving
 *    (the note may not exist yet) - the editor stores them when the note is saved.
 */
// Declares class AlarmActivity to group related state and behavior.
public class AlarmActivity extends BaseActivity implements AlarmAdapter.Listener {
    // Declares b for use by the surrounding method or class.
    private ActivityAlarmBinding b;
    // Declares alarms for use by the surrounding method or class.
    private AlarmRepository alarms;
    // Declares notes for use by the surrounding method or class.
    private NoteRepository notes;
    // Declares todos for use by the surrounding method or class.
    private TodoRepository todos;
    // Declares adapter for use by the surrounding method or class.
    private AlarmAdapter adapter;

    // Declares resultMode for use by the surrounding method or class.
    private boolean resultMode;
    // Declares launchedWithTarget for use by the surrounding method or class.
    private boolean launchedWithTarget;
    // Declares targetNoteId for use by the surrounding method or class.
    private long targetNoteId;
    // Declares targetTodoId for use by the surrounding method or class.
    private long targetTodoId;
    // Initializes targetLabel with the text or query fragment shown here.
    private String targetLabel = "";
    // Declares hasExisting for use by the surrounding method or class.
    private boolean hasExisting;

    // Declares alarmAt for use by the surrounding method or class.
    private long alarmAt;
    // Initializes repeat with Constants.REPEAT_NONE so later statements can use the computed value.
    private String repeat = Constants.REPEAT_NONE;
    // Initializes interval with 1 so later statements can use the computed value.
    private int interval = 1;
    // Declares updatingChips for use by the surrounding method or class.
    private boolean updatingChips;

    // Applies @Override metadata to the declaration that follows.
    @Override
    // Runs component initialization when Android invokes this lifecycle callback.
    protected void onCreate(Bundle savedInstanceState) {
        // Calls super.onCreate with savedInstanceState to perform the operation shown.
        super.onCreate(savedInstanceState);
        // Stores the result of ActivityAlarmBinding.inflate(getLayoutInflater()) in b for use by later logic.
        b = ActivityAlarmBinding.inflate(getLayoutInflater());
        // Calls setContentView with b.getRoot( to perform the operation shown.
        setContentView(b.getRoot());
        // Stores the result of new AlarmRepository(this) in alarms for use by later logic.
        alarms = new AlarmRepository(this);
        // Stores the result of new NoteRepository(this) in notes for use by later logic.
        notes = new NoteRepository(this);
        // Stores the result of new TodoRepository(this) in todos for use by later logic.
        todos = new TodoRepository(this);

        // Calls setupBackHeader with b.header, R.string.title_set_alarm, R.string.subtitle_set_alarm to perform the operation shown.
        setupBackHeader(b.header, R.string.title_set_alarm, R.string.subtitle_set_alarm);
        // Calls b.header.headerAction.setImageResource with R.drawable.ic_info to perform the operation shown.
        b.header.headerAction.setImageResource(R.drawable.ic_info);
        // Calls b.header.headerAction.setContentDescription with getString(R.string.alarm_info_title to perform the operation shown.
        b.header.headerAction.setContentDescription(getString(R.string.alarm_info_title));
        // Calls b.header.headerAction.setVisibility with View.VISIBLE to perform the operation shown.
        b.header.headerAction.setVisibility(View.VISIBLE);
        // Calls b.header.headerAction.setOnClickListener with v -> new MaterialAlertDialogBuilder(this to perform the operation shown.
        b.header.headerAction.setOnClickListener(v -> new MaterialAlertDialogBuilder(this)
                // Calls setTitle on the object from the preceding line with the shown arguments.
                .setTitle(R.string.alarm_info_title)
                // Calls setMessage on the object from the preceding line with the shown arguments.
                .setMessage(R.string.alarm_info_message)
                // Calls setPositiveButton on the object from the preceding line with the shown arguments.
                .setPositiveButton(R.string.ok, null).show());

        // Initializes in with getIntent() so later statements can use the computed value.
        Intent in = getIntent();
        // Stores the result of in.getBooleanExtra(Constants.EXTRA_RESULT_MODE, false) in resultMode for use by later logic.
        resultMode = in.getBooleanExtra(Constants.EXTRA_RESULT_MODE, false);
        // Stores the result of in.getLongExtra(Constants.EXTRA_NOTE_ID, 0) in targetNoteId for use by later logic.
        targetNoteId = in.getLongExtra(Constants.EXTRA_NOTE_ID, 0);
        // Stores the result of in.getLongExtra(Constants.EXTRA_TODO_ID, 0) in targetTodoId for use by later logic.
        targetTodoId = in.getLongExtra(Constants.EXTRA_TODO_ID, 0);
        // Stores the result of resultMode || targetNoteId > 0 || targetTodoId > 0 in launchedWithTarget for use by later logic.
        launchedWithTarget = resultMode || targetNoteId > 0 || targetTodoId > 0;

        // Stores the result of new AlarmAdapter(this) in adapter for use by later logic.
        adapter = new AlarmAdapter(this);
        // Calls b.recycler.setLayoutManager with new LinearLayoutManager(this to perform the operation shown.
        b.recycler.setLayoutManager(new LinearLayoutManager(this));
        // Calls b.recycler.setAdapter with adapter to perform the operation shown.
        b.recycler.setAdapter(adapter);
        // Calls b.sectionActive.setVisibility with resultMode ? View.GONE : View.VISIBLE to perform the operation shown.
        b.sectionActive.setVisibility(resultMode ? View.GONE : View.VISIBLE);
        // Calls b.btnChange.setVisibility with launchedWithTarget ? View.GONE : View.VISIBLE to perform the operation shown.
        b.btnChange.setVisibility(launchedWithTarget ? View.GONE : View.VISIBLE);

        // If savedInstanceState == null is true, { immediately.
        if (savedInstanceState == null) {
            // Calls resetForm with  to perform the operation shown.
            resetForm();
            // If resultMode is true, { immediately.
            if (resultMode) {
                // Stores the result of in.getStringExtra(Constants.EXTRA_TARGET_LABEL) in targetLabel for use by later logic.
                targetLabel = in.getStringExtra(Constants.EXTRA_TARGET_LABEL);
                // Initializes at with in.getLongExtra(Constants.EXTRA_ALARM_AT, 0) so later statements can use the computed value.
                long at = in.getLongExtra(Constants.EXTRA_ALARM_AT, 0);
                // If at > 0 is true, { immediately.
                if (at > 0) {
                    // Stores the result of at in alarmAt for use by later logic.
                    alarmAt = at;
                    // Stores the result of in.getStringExtra(Constants.EXTRA_ALARM_REPEAT) in repeat for use by later logic.
                    repeat = in.getStringExtra(Constants.EXTRA_ALARM_REPEAT);
                    // Stores the result of in.getIntExtra(Constants.EXTRA_ALARM_INTERVAL, 1) in interval for use by later logic.
                    interval = in.getIntExtra(Constants.EXTRA_ALARM_INTERVAL, 1);
                    // Calls b.swEnable.setChecked with in.getBooleanExtra(Constants.EXTRA_ALARM_ENABLED, true to perform the operation shown.
                    b.swEnable.setChecked(in.getBooleanExtra(Constants.EXTRA_ALARM_ENABLED, true));
                    // Stores the result of true in hasExisting for use by later logic.
                    hasExisting = true;
                // Closes the current method, control-flow, or type block.
                }
            // Checks the alternate condition (targetNoteId > 0 || targetTodoId > 0) after the earlier branch did not run.
            } else if (targetNoteId > 0 || targetTodoId > 0) {
                // Calls setTarget with targetNoteId, targetTodoId to perform the operation shown.
                setTarget(targetNoteId, targetTodoId);
            // Closes the current method, control-flow, or type block.
            }
        // Runs the fallback branch when the preceding condition did not match.
        } else {
            // Stores the result of savedInstanceState.getLong("at") in alarmAt for use by later logic.
            alarmAt = savedInstanceState.getLong("at");
            // Stores the result of savedInstanceState.getString("repeat", Constants.REPEAT_NONE) in repeat for use by later logic.
            repeat = savedInstanceState.getString("repeat", Constants.REPEAT_NONE);
            // Stores the result of savedInstanceState.getInt("interval", 1) in interval for use by later logic.
            interval = savedInstanceState.getInt("interval", 1);
            // Stores the result of savedInstanceState.getString("label", "") in targetLabel for use by later logic.
            targetLabel = savedInstanceState.getString("label", "");
            // Stores the result of savedInstanceState.getBoolean("existing") in hasExisting for use by later logic.
            hasExisting = savedInstanceState.getBoolean("existing");
            // Stores the result of savedInstanceState.getLong("note") in targetNoteId for use by later logic.
            targetNoteId = savedInstanceState.getLong("note");
            // Stores the result of savedInstanceState.getLong("todo") in targetTodoId for use by later logic.
            targetTodoId = savedInstanceState.getLong("todo");
        // Closes the current method, control-flow, or type block.
        }

        // Calls b.btnDate.setOnClickListener with v -> pickDate( to perform the operation shown.
        b.btnDate.setOnClickListener(v -> pickDate());
        // Calls b.btnTime.setOnClickListener with v -> pickTime( to perform the operation shown.
        b.btnTime.setOnClickListener(v -> pickTime());
        // Calls b.chipGroup.setOnCheckedStateChangeListener with (g, ids) -> onRepeatChipChanged( to perform the operation shown.
        b.chipGroup.setOnCheckedStateChangeListener((g, ids) -> onRepeatChipChanged());
        // Calls b.btnChange.setOnClickListener with v -> chooseTarget( to perform the operation shown.
        b.btnChange.setOnClickListener(v -> chooseTarget());
        // Calls b.btnSet.setOnClickListener with v -> saveAlarm( to perform the operation shown.
        b.btnSet.setOnClickListener(v -> saveAlarm());
        // Calls b.btnCancel.setOnClickListener with v -> finish( to perform the operation shown.
        b.btnCancel.setOnClickListener(v -> finish());
        // Calls b.btnRemove.setOnClickListener with v -> removeAlarm( to perform the operation shown.
        b.btnRemove.setOnClickListener(v -> removeAlarm());
        // Calls b.btnAllowExact.setOnClickListener with v -> openExactAlarmSettings( to perform the operation shown.
        b.btnAllowExact.setOnClickListener(v -> openExactAlarmSettings());
        // Calls refreshForm with  to perform the operation shown.
        refreshForm();
    // Closes the current method, control-flow, or type block.
    }

    // Applies @Override metadata to the declaration that follows.
    @Override
    // Stores transient UI values so Android can restore them later.
    protected void onSaveInstanceState(Bundle out) {
        // Calls super.onSaveInstanceState with out to perform the operation shown.
        super.onSaveInstanceState(out);
        // Calls out.putLong with "at", alarmAt to perform the operation shown.
        out.putLong("at", alarmAt);
        // Calls out.putString with "repeat", repeat to perform the operation shown.
        out.putString("repeat", repeat);
        // Calls out.putInt with "interval", interval to perform the operation shown.
        out.putInt("interval", interval);
        // Calls out.putString with "label", targetLabel to perform the operation shown.
        out.putString("label", targetLabel);
        // Calls out.putBoolean with "existing", hasExisting to perform the operation shown.
        out.putBoolean("existing", hasExisting);
        // Calls out.putLong with "note", targetNoteId to perform the operation shown.
        out.putLong("note", targetNoteId);
        // Calls out.putLong with "todo", targetTodoId to perform the operation shown.
        out.putLong("todo", targetTodoId);
    // Closes the current method, control-flow, or type block.
    }

    // Applies @Override metadata to the declaration that follows.
    @Override
    // Refreshes visible data when this component becomes active.
    protected void onResume() {
        // Calls super.onResume with  to perform the operation shown.
        super.onResume();
        // Calls b.bannerExact.setVisibility with AlarmScheduler.canScheduleExact(this) ? View.GONE : View.VISIBLE to perform the operation shown.
        b.bannerExact.setVisibility(AlarmScheduler.canScheduleExact(this) ? View.GONE : View.VISIBLE);
        // Calls loadList with  to perform the operation shown.
        loadList();
    // Closes the current method, control-flow, or type block.
    }

    // ------------------------------------------------------------------ form state

    // Resets form.
    private void resetForm() {
        // Stores the result of DateTimeUtils.nextFullHour() in alarmAt for use by later logic.
        alarmAt = DateTimeUtils.nextFullHour();
        // Stores the result of Constants.REPEAT_NONE in repeat for use by later logic.
        repeat = Constants.REPEAT_NONE;
        // Stores the result of 1 in interval for use by later logic.
        interval = 1;
        // Stores the result of false in hasExisting for use by later logic.
        hasExisting = false;
        // Calls b.swEnable.setChecked with true to perform the operation shown.
        b.swEnable.setChecked(true);
    // Closes the current method, control-flow, or type block.
    }

    // Updates target.
    private void setTarget(long noteId, long todoId) {
        // Stores the result of noteId in targetNoteId for use by later logic.
        targetNoteId = noteId;
        // Stores the result of todoId in targetTodoId for use by later logic.
        targetTodoId = todoId;
        // Calls resetForm with  to perform the operation shown.
        resetForm();
        // Initializes existing with null so later statements can use the computed value.
        Alarm existing = null;
        // If noteId > 0 is true, { immediately.
        if (noteId > 0) {
            // Initializes n with notes.get(noteId) so later statements can use the computed value.
            Note n = notes.get(noteId);
            // If n == null is true, { immediately.
            if (n == null) {
                // Stores the result of 0 in targetNoteId for use by later logic.
                targetNoteId = 0;
                // Stores the result of "" in targetLabel for use by later logic.
                targetLabel = "";
            // Runs the fallback branch when the preceding condition did not match.
            } else {
                // Stores the result of n.title in targetLabel for use by later logic.
                targetLabel = n.title;
                // Stores the result of alarms.getForNote(noteId) in existing for use by later logic.
                existing = alarms.getForNote(noteId);
            // Closes the current method, control-flow, or type block.
            }
        // Checks the alternate condition (todoId > 0) after the earlier branch did not run.
        } else if (todoId > 0) {
            // Initializes t with todos.get(todoId) so later statements can use the computed value.
            Todo t = todos.get(todoId);
            // If t == null is true, { immediately.
            if (t == null) {
                // Stores the result of 0 in targetTodoId for use by later logic.
                targetTodoId = 0;
                // Stores the result of "" in targetLabel for use by later logic.
                targetLabel = "";
            // Runs the fallback branch when the preceding condition did not match.
            } else {
                // Stores the result of t.title in targetLabel for use by later logic.
                targetLabel = t.title;
                // Stores the result of alarms.getForTodo(todoId) in existing for use by later logic.
                existing = alarms.getForTodo(todoId);
            // Closes the current method, control-flow, or type block.
            }
        // Runs the fallback branch when the preceding condition did not match.
        } else {
            // Stores the result of "" in targetLabel for use by later logic.
            targetLabel = "";
        // Closes the current method, control-flow, or type block.
        }
        // If existing != null is true, { immediately.
        if (existing != null) {
            // Stores the result of existing.scheduledAt in alarmAt for use by later logic.
            alarmAt = existing.scheduledAt;
            // Stores the result of existing.repeatType in repeat for use by later logic.
            repeat = existing.repeatType;
            // Stores the result of existing.repeatInterval in interval for use by later logic.
            interval = existing.repeatInterval;
            // Calls b.swEnable.setChecked with existing.enabled to perform the operation shown.
            b.swEnable.setChecked(existing.enabled);
            // Stores the result of true in hasExisting for use by later logic.
            hasExisting = true;
        // Closes the current method, control-flow, or type block.
        }
        // Calls refreshForm with  to perform the operation shown.
        refreshForm();
    // Closes the current method, control-flow, or type block.
    }

    // Refreshes form.
    private void refreshForm() {
        // Initializes hasTarget with resultMode || targetNoteId > 0 || targetTodoId > 0 so later statements can use the computed value.
        boolean hasTarget = resultMode || targetNoteId > 0 || targetTodoId > 0;
        // Calls b.tvTargetTitle.setText with hasTarget ? targetLabel : getString(R.string.no_target_selected to perform the operation shown.
        b.tvTargetTitle.setText(hasTarget ? targetLabel : getString(R.string.no_target_selected));
        // Calls b.tvTargetHint.setText with hasTarget ? getString(targetTodoId > 0 ? R.string.target_task : R.string.target_note to perform the operation shown.
        b.tvTargetHint.setText(hasTarget ? getString(targetTodoId > 0 ? R.string.target_task : R.string.target_note)
                // Calls getString with R.string.choose_target_hint to perform the operation shown.
                : getString(R.string.choose_target_hint));
        // Calls b.btnDate.setText with DateTimeUtils.formatDate(alarmAt to perform the operation shown.
        b.btnDate.setText(DateTimeUtils.formatDate(alarmAt));
        // Calls b.btnTime.setText with DateTimeUtils.formatTime(alarmAt to perform the operation shown.
        b.btnTime.setText(DateTimeUtils.formatTime(alarmAt));
        // Stores the result of true in updatingChips for use by later logic.
        updatingChips = true;
        // Initializes chip with Constants.REPEAT_DAILY.equals(repeat) ? R.id.chip_daily so later statements can use the computed value.
        int chip = Constants.REPEAT_DAILY.equals(repeat) ? R.id.chip_daily
                // Calls Constants.REPEAT_WEEKLY.equals with repeat) ? R.id.chip_weekly to perform the operation shown.
                : Constants.REPEAT_WEEKLY.equals(repeat) ? R.id.chip_weekly
                // Calls Constants.REPEAT_CUSTOM.equals with repeat) ? R.id.chip_custom : R.id.chip_none to perform the operation shown.
                : Constants.REPEAT_CUSTOM.equals(repeat) ? R.id.chip_custom : R.id.chip_none;
        // Calls b.chipGroup.check with chip to perform the operation shown.
        b.chipGroup.check(chip);
        // Calls b.chipCustom.setText with Constants.REPEAT_CUSTOM.equals(repeat to perform the operation shown.
        b.chipCustom.setText(Constants.REPEAT_CUSTOM.equals(repeat)
                // Calls RepeatLabels.label with this, repeat, interval) : getString(R.string.repeat_custom to perform the operation shown.
                ? RepeatLabels.label(this, repeat, interval) : getString(R.string.repeat_custom));
        // Stores the result of false in updatingChips for use by later logic.
        updatingChips = false;
        // Calls b.tvRepeatHint.setText with Constants.REPEAT_NONE.equals(repeat) ? getString(R.string.repeat_hint_none to perform the operation shown.
        b.tvRepeatHint.setText(Constants.REPEAT_NONE.equals(repeat) ? getString(R.string.repeat_hint_none)
                // Calls getString with R.string.repeat_hint, RepeatLabels.label(this, repeat, interval to perform the operation shown.
                : getString(R.string.repeat_hint, RepeatLabels.label(this, repeat, interval)));
        // Calls b.btnRemove.setVisibility with hasExisting ? View.VISIBLE : View.GONE to perform the operation shown.
        b.btnRemove.setVisibility(hasExisting ? View.VISIBLE : View.GONE);
        // Calls b.btnSet.setText with hasExisting ? R.string.update_alarm : R.string.set_alarm to perform the operation shown.
        b.btnSet.setText(hasExisting ? R.string.update_alarm : R.string.set_alarm);
    // Closes the current method, control-flow, or type block.
    }

    // Declares onRepeatChipChanged() to implement this part of the component.
    private void onRepeatChipChanged() {
        // If updatingChips is true, return immediately.
        if (updatingChips) return;
        // Initializes id with b.chipGroup.getCheckedChipId() so later statements can use the computed value.
        int id = b.chipGroup.getCheckedChipId();
        // If id == R.id.chip_daily is true, { immediately.
        if (id == R.id.chip_daily) {
            // Stores the result of Constants.REPEAT_DAILY in repeat for use by later logic.
            repeat = Constants.REPEAT_DAILY;
        // Checks the alternate condition (id == R.id.chip_weekly) after the earlier branch did not run.
        } else if (id == R.id.chip_weekly) {
            // Stores the result of Constants.REPEAT_WEEKLY in repeat for use by later logic.
            repeat = Constants.REPEAT_WEEKLY;
        // Checks the alternate condition (id == R.id.chip_custom) after the earlier branch did not run.
        } else if (id == R.id.chip_custom) {
            // Calls RepeatDialogs.askInterval with this, interval, new RepeatDialogs.IntervalCallback() { to perform the operation shown.
            RepeatDialogs.askInterval(this, interval, new RepeatDialogs.IntervalCallback() {
                // Applies @Override metadata to the declaration that follows.
                @Override
                // Declares onInterval() to implement this part of the component.
                public void onInterval(int days) {
                    // Stores the result of Constants.REPEAT_CUSTOM in repeat for use by later logic.
                    repeat = Constants.REPEAT_CUSTOM;
                    // Stores the result of days in interval for use by later logic.
                    interval = days;
                    // Calls refreshForm with  to perform the operation shown.
                    refreshForm();
                // Closes the current method, control-flow, or type block.
                }

                // Applies @Override metadata to the declaration that follows.
                @Override
                // Declares onCancel() to implement this part of the component.
                public void onCancel() {
                    // Calls refreshForm with  to perform the operation shown.
                    refreshForm(); // restore previous selection
                // Closes the current method, control-flow, or type block.
                }
            // Executes }) as a statement.
            });
            // Returns control to the caller without a value.
            return;
        // Runs the fallback branch when the preceding condition did not match.
        } else {
            // Stores the result of Constants.REPEAT_NONE in repeat for use by later logic.
            repeat = Constants.REPEAT_NONE;
        // Closes the current method, control-flow, or type block.
        }
        // Calls refreshForm with  to perform the operation shown.
        refreshForm();
    // Closes the current method, control-flow, or type block.
    }

    // Prompts the user to choose date.
    private void pickDate() {
        // Initializes c with Calendar.getInstance() so later statements can use the computed value.
        Calendar c = Calendar.getInstance();
        // Calls c.setTimeInMillis with alarmAt to perform the operation shown.
        c.setTimeInMillis(alarmAt);
        // Creates the object described by new DatePickerDialog(this, (view, y, m, d) -> {.
        new DatePickerDialog(this, (view, y, m, d) -> {
            // Initializes n with Calendar.getInstance() so later statements can use the computed value.
            Calendar n = Calendar.getInstance();
            // Calls n.setTimeInMillis with alarmAt to perform the operation shown.
            n.setTimeInMillis(alarmAt);
            // Calls n.set with y, m, d to perform the operation shown.
            n.set(y, m, d);
            // Stores the result of n.getTimeInMillis() in alarmAt for use by later logic.
            alarmAt = n.getTimeInMillis();
            // Calls refreshForm with  to perform the operation shown.
            refreshForm();
        // Calls c.get with Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show( to perform the operation shown.
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    // Closes the current method, control-flow, or type block.
    }

    // Prompts the user to choose time.
    private void pickTime() {
        // Initializes c with Calendar.getInstance() so later statements can use the computed value.
        Calendar c = Calendar.getInstance();
        // Calls c.setTimeInMillis with alarmAt to perform the operation shown.
        c.setTimeInMillis(alarmAt);
        // Creates the object described by new TimePickerDialog(this, (view, h, min) -> {.
        new TimePickerDialog(this, (view, h, min) -> {
            // Initializes n with Calendar.getInstance() so later statements can use the computed value.
            Calendar n = Calendar.getInstance();
            // Calls n.setTimeInMillis with alarmAt to perform the operation shown.
            n.setTimeInMillis(alarmAt);
            // Calls n.set with Calendar.HOUR_OF_DAY, h to perform the operation shown.
            n.set(Calendar.HOUR_OF_DAY, h);
            // Calls n.set with Calendar.MINUTE, min to perform the operation shown.
            n.set(Calendar.MINUTE, min);
            // Calls n.set with Calendar.SECOND, 0 to perform the operation shown.
            n.set(Calendar.SECOND, 0);
            // Calls n.set with Calendar.MILLISECOND, 0 to perform the operation shown.
            n.set(Calendar.MILLISECOND, 0);
            // Stores the result of n.getTimeInMillis() in alarmAt for use by later logic.
            alarmAt = n.getTimeInMillis();
            // Calls refreshForm with  to perform the operation shown.
            refreshForm();
        // Calls c.get with Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), false).show( to perform the operation shown.
        }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), false).show();
    // Closes the current method, control-flow, or type block.
    }

    /** Lets the user pick any note or pending standalone task as the alarm target. */
    // Prompts the user to choose target.
    private void chooseTarget() {
        // Initializes labels with new ArrayList<>() so later statements can use the computed value.
        List<String> labels = new ArrayList<>();
        // Initializes ids with new ArrayList<>() so later statements can use the computed value.
        List<long[]> ids = new ArrayList<>(); // {noteId, todoId}
        // Repeats the following block according to the initialization, condition, and update in Note n : notes.getAll().
        for (Note n : notes.getAll()) {
            // Calls labels.add with getString(R.string.target_note_item, n.title to perform the operation shown.
            labels.add(getString(R.string.target_note_item, n.title));
            // Calls ids.add with new long[]{n.id, 0} to perform the operation shown.
            ids.add(new long[]{n.id, 0});
        // Closes the current method, control-flow, or type block.
        }
        // Initializes q with new TodoRepository.TodoQuery() so later statements can use the computed value.
        TodoRepository.TodoQuery q = new TodoRepository.TodoQuery();
        // Repeats the following block according to the initialization, condition, and update in Todo t : todos.query(q).
        for (Todo t : todos.query(q)) {
            // If t.isLinkedToNote() is true, continue; // linked tasks are reached through their note immediately.
            if (t.isLinkedToNote()) continue; // linked tasks are reached through their note
            // Calls labels.add with getString(R.string.target_task_item, t.title to perform the operation shown.
            labels.add(getString(R.string.target_task_item, t.title));
            // Calls ids.add with new long[]{0, t.id} to perform the operation shown.
            ids.add(new long[]{0, t.id});
        // Closes the current method, control-flow, or type block.
        }
        // If labels.isEmpty() is true, { immediately.
        if (labels.isEmpty()) {
            // Calls UiUtils.toast with this, R.string.nothing_to_select to perform the operation shown.
            UiUtils.toast(this, R.string.nothing_to_select);
            // Returns control to the caller without a value.
            return;
        // Closes the current method, control-flow, or type block.
        }
        // Creates the object described by new MaterialAlertDialogBuilder(this).
        new MaterialAlertDialogBuilder(this)
                // Calls setTitle on the object from the preceding line with the shown arguments.
                .setTitle(R.string.choose_target)
                // Calls setItems on the object from the preceding line with the shown arguments.
                .setItems(labels.toArray(new String[0]), (d, which) -> setTarget(ids.get(which)[0], ids.get(which)[1]))
                // Calls setNegativeButton on the object from the preceding line with the shown arguments.
                .setNegativeButton(R.string.cancel, null)
                // Calls show on the object from the preceding line with the shown arguments.
                .show();
    // Closes the current method, control-flow, or type block.
    }

    // ------------------------------------------------------------------ actions

    // Persists alarm.
    private void saveAlarm() {
        // Initializes enabled with b.swEnable.isChecked() so later statements can use the computed value.
        boolean enabled = b.swEnable.isChecked();
        // If !resultMode && targetNoteId == 0 && targetTodoId == 0 is true, { immediately.
        if (!resultMode && targetNoteId == 0 && targetTodoId == 0) {
            // Calls UiUtils.toast with this, R.string.error_select_target to perform the operation shown.
            UiUtils.toast(this, R.string.error_select_target);
            // Returns control to the caller without a value.
            return;
        // Closes the current method, control-flow, or type block.
        }
        // If enabled && alarmAt <= System.currentTimeMillis() is true, { immediately.
        if (enabled && alarmAt <= System.currentTimeMillis()) {
            // Calls UiUtils.toast with this, R.string.error_alarm_in_past to perform the operation shown.
            UiUtils.toast(this, R.string.error_alarm_in_past);
            // Returns control to the caller without a value.
            return;
        // Closes the current method, control-flow, or type block.
        }
        // If resultMode is true, { immediately.
        if (resultMode) {
            // Initializes result with new Intent() so later statements can use the computed value.
            Intent result = new Intent()
                    // Calls putExtra on the object from the preceding line with the shown arguments.
                    .putExtra(Constants.EXTRA_ALARM_AT, alarmAt)
                    // Calls putExtra on the object from the preceding line with the shown arguments.
                    .putExtra(Constants.EXTRA_ALARM_REPEAT, repeat)
                    // Calls putExtra on the object from the preceding line with the shown arguments.
                    .putExtra(Constants.EXTRA_ALARM_INTERVAL, interval)
                    // Calls putExtra on the object from the preceding line with the shown arguments.
                    .putExtra(Constants.EXTRA_ALARM_ENABLED, enabled);
            // Calls setResult with RESULT_OK, result to perform the operation shown.
            setResult(RESULT_OK, result);
            // Calls finish with  to perform the operation shown.
            finish();
            // Returns control to the caller without a value.
            return;
        // Closes the current method, control-flow, or type block.
        }
        // Starts the operation whose failures are handled by the following catch clauses.
        try {
            // If targetNoteId > 0 is true, alarms.saveForNote(targetNoteId, alarmAt, enabled, repeat, interval) immediately.
            if (targetNoteId > 0) alarms.saveForNote(targetNoteId, alarmAt, enabled, repeat, interval);
            // Runs the fallback branch when the preceding condition did not match.
            else alarms.saveForTodo(targetTodoId, alarmAt, enabled, repeat, interval);
        // Calls catch with RuntimeException e) { to perform the operation shown.
        } catch (RuntimeException e) {
            // Calls Log.e with "AlarmActivity", "Failed to persist alarm (noteId=" + targetNoteId to perform the operation shown.
            Log.e("AlarmActivity", "Failed to persist alarm (noteId=" + targetNoteId
                    // Appends this literal fragment to the string or SQL query being assembled.
                    + ", todoId=" + targetTodoId + ", at=" + alarmAt + ")", e);
            // Calls UiUtils.toast with this, R.string.error_save_failed to perform the operation shown.
            UiUtils.toast(this, R.string.error_save_failed);
            // Returns control to the caller without a value.
            return;
        // Closes the current method, control-flow, or type block.
        }
        // Calls UiUtils.toast with this, R.string.alarm_saved to perform the operation shown.
        UiUtils.toast(this, R.string.alarm_saved);
        // If launchedWithTarget is true, { immediately.
        if (launchedWithTarget) {
            // Calls finish with  to perform the operation shown.
            finish();
        // Runs the fallback branch when the preceding condition did not match.
        } else {
            // Stores the result of 0 in targetNoteId for use by later logic.
            targetNoteId = 0;
            // Stores the result of 0 in targetTodoId for use by later logic.
            targetTodoId = 0;
            // Stores the result of "" in targetLabel for use by later logic.
            targetLabel = "";
            // Calls resetForm with  to perform the operation shown.
            resetForm();
            // Calls refreshForm with  to perform the operation shown.
            refreshForm();
            // Calls loadList with  to perform the operation shown.
            loadList();
        // Closes the current method, control-flow, or type block.
        }
    // Closes the current method, control-flow, or type block.
    }

    // Removes alarm.
    private void removeAlarm() {
        // If resultMode is true, { immediately.
        if (resultMode) {
            // Calls setResult with RESULT_OK, new Intent().putExtra(Constants.EXTRA_ALARM_REMOVE, true to perform the operation shown.
            setResult(RESULT_OK, new Intent().putExtra(Constants.EXTRA_ALARM_REMOVE, true));
            // Calls finish with  to perform the operation shown.
            finish();
            // Returns control to the caller without a value.
            return;
        // Closes the current method, control-flow, or type block.
        }
        // Calls UiUtils.confirm with this, R.string.remove_alarm_title, getString(R.string.remove_alarm_message, targetLabel), to perform the operation shown.
        UiUtils.confirm(this, R.string.remove_alarm_title, getString(R.string.remove_alarm_message, targetLabel),
                // Starts the callback body that handles the event or asynchronous result.
                R.string.remove, () -> {
                    // If targetNoteId > 0 is true, alarms.deleteForNote(targetNoteId) immediately.
                    if (targetNoteId > 0) alarms.deleteForNote(targetNoteId);
                    // Runs the fallback branch when the preceding condition did not match.
                    else alarms.deleteForTodo(targetTodoId);
                    // Calls UiUtils.toast with this, R.string.alarm_removed to perform the operation shown.
                    UiUtils.toast(this, R.string.alarm_removed);
                    // If launchedWithTarget is true, { immediately.
                    if (launchedWithTarget) {
                        // Calls finish with  to perform the operation shown.
                        finish();
                    // Runs the fallback branch when the preceding condition did not match.
                    } else {
                        // Calls resetForm with  to perform the operation shown.
                        resetForm();
                        // Calls refreshForm with  to perform the operation shown.
                        refreshForm();
                        // Calls loadList with  to perform the operation shown.
                        loadList();
                    // Closes the current method, control-flow, or type block.
                    }
                // Executes }) as a statement.
                });
    // Closes the current method, control-flow, or type block.
    }

    // Opens exact alarm settings.
    private void openExactAlarmSettings() {
        // If Build.VERSION.SDK_INT >= Build.VERSION_CODES.S is true, { immediately.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Calls startActivity with new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM, to perform the operation shown.
            startActivity(new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                    // Calls Uri.parse with "package:" + getPackageName( to perform the operation shown.
                    Uri.parse("package:" + getPackageName())));
        // Closes the current method, control-flow, or type block.
        }
    // Closes the current method, control-flow, or type block.
    }

    // ------------------------------------------------------------------ active alarms list

    // Declares loadList() to implement this part of the component.
    private void loadList() {
        // Initializes list with alarms.getAll() so later statements can use the computed value.
        List<Alarm> list = alarms.getAll();
        // Calls adapter.setItems with list to perform the operation shown.
        adapter.setItems(list);
        // Calls b.tvAlarmCount.setText with getResources().getQuantityString(R.plurals.alarms_count, list.size(), list.size( to perform the operation shown.
        b.tvAlarmCount.setText(getResources().getQuantityString(R.plurals.alarms_count, list.size(), list.size()));
        // Initializes empty with list.isEmpty() so later statements can use the computed value.
        boolean empty = list.isEmpty();
        // Calls b.empty.getRoot with ).setVisibility(empty ? View.VISIBLE : View.GONE to perform the operation shown.
        b.empty.getRoot().setVisibility(empty ? View.VISIBLE : View.GONE);
        // Calls b.recycler.setVisibility with empty ? View.GONE : View.VISIBLE to perform the operation shown.
        b.recycler.setVisibility(empty ? View.GONE : View.VISIBLE);
        // Calls b.empty.emptyTitle.setText with R.string.empty_alarms_title to perform the operation shown.
        b.empty.emptyTitle.setText(R.string.empty_alarms_title);
        // Calls b.empty.emptyMessage.setText with R.string.empty_alarms_message to perform the operation shown.
        b.empty.emptyMessage.setText(R.string.empty_alarms_message);
    // Closes the current method, control-flow, or type block.
    }

    // Applies @Override metadata to the declaration that follows.
    @Override
    // Declares onToggle() to implement this part of the component.
    public void onToggle(Alarm alarm, boolean enabled) {
        // Initializes result with alarms.setEnabled(alarm.id, enabled) so later statements can use the computed value.
        int result = alarms.setEnabled(alarm.id, enabled);
        // If result == AlarmRepository.PAST is true, UiUtils.toast(this, R.string.error_alarm_passed_edit) immediately.
        if (result == AlarmRepository.PAST) UiUtils.toast(this, R.string.error_alarm_passed_edit);
        // Calls b.getRoot with ).post(this::loadList to perform the operation shown.
        b.getRoot().post(this::loadList);
    // Closes the current method, control-flow, or type block.
    }

    // Applies @Override metadata to the declaration that follows.
    @Override
    // Declares onMenu() to implement this part of the component.
    public void onMenu(Alarm alarm, View anchor) {
        // Initializes pm with new PopupMenu(this, anchor) so later statements can use the computed value.
        PopupMenu pm = new PopupMenu(this, anchor);
        // Calls pm.inflate with R.menu.menu_alarm_item to perform the operation shown.
        pm.inflate(R.menu.menu_alarm_item);
        // Starts the callback body that handles the event or asynchronous result.
        pm.setOnMenuItemClickListener(item -> {
            // If item.getItemId() == R.id.action_edit is true, { immediately.
            if (item.getItemId() == R.id.action_edit) {
                // Calls setTarget with alarm.noteId, alarm.todoId to perform the operation shown.
                setTarget(alarm.noteId, alarm.todoId);
                // Calls b.scroll.smoothScrollTo with 0, 0 to perform the operation shown.
                b.scroll.smoothScrollTo(0, 0);
            // Checks the alternate condition (item.getItemId() after the earlier branch did not run.
            } else if (item.getItemId() == R.id.action_delete) {
                // Calls UiUtils.confirm with this, R.string.remove_alarm_title, to perform the operation shown.
                UiUtils.confirm(this, R.string.remove_alarm_title,
                        // Starts the callback body that handles the event or asynchronous result.
                        getString(R.string.remove_alarm_message, alarm.ownerTitle), R.string.delete, () -> {
                            // Calls alarms.delete with alarm.id to perform the operation shown.
                            alarms.delete(alarm.id);
                            // Calls loadList with  to perform the operation shown.
                            loadList();
                        // Executes }) as a statement.
                        });
            // Closes the current method, control-flow, or type block.
            }
            // Returns true to the caller.
            return true;
        // Executes }) as a statement.
        });
        // Calls pm.show with  to perform the operation shown.
        pm.show();
    // Closes the current method, control-flow, or type block.
    }
// Closes the current method, control-flow, or type block.
}
