// Sets the package name to com.mit.notepad.activities so this class belongs to the app module namespace.
package com.mit.notepad.activities;

// Imports android.content.Intent so this file can use that type by its short name.
import android.content.Intent;
// Imports android.graphics.Typeface so this file can use that type by its short name.
import android.graphics.Typeface;
// Imports android.os.Bundle so this file can use that type by its short name.
import android.os.Bundle;
// Imports android.text.Editable so this file can use that type by its short name.
import android.text.Editable;
// Imports android.text.Spannable so this file can use that type by its short name.
import android.text.Spannable;
// Imports android.text.Spanned so this file can use that type by its short name.
import android.text.Spanned;
// Imports android.text.TextWatcher so this file can use that type by its short name.
import android.text.TextWatcher;
// Imports android.text.style.StyleSpan so this file can use that type by its short name.
import android.text.style.StyleSpan;
// Imports android.text.style.URLSpan so this file can use that type by its short name.
import android.text.style.URLSpan;
// Imports android.text.style.UnderlineSpan so this file can use that type by its short name.
import android.text.style.UnderlineSpan;
// Imports android.util.Patterns so this file can use that type by its short name.
import android.util.Patterns;
// Imports android.view.View so this file can use that type by its short name.
import android.view.View;
// Imports android.widget.EditText so this file can use that type by its short name.
import android.widget.EditText;

// Imports androidx.activity.OnBackPressedCallback so this file can use that type by its short name.
import androidx.activity.OnBackPressedCallback;
// Imports androidx.activity.result.ActivityResultLauncher so this file can use that type by its short name.
import androidx.activity.result.ActivityResultLauncher;
// Imports androidx.activity.result.contract.ActivityResultContracts so this file can use that type by its short name.
import androidx.activity.result.contract.ActivityResultContracts;

// Imports com.google.android.material.dialog.MaterialAlertDialogBuilder so this file can use that type by its short name.
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
// Imports com.mit.notepad.R so this file can use that type by its short name.
import com.mit.notepad.R;
// Imports com.mit.notepad.databinding.ActivityNoteEditorBinding so this file can use that type by its short name.
import com.mit.notepad.databinding.ActivityNoteEditorBinding;
// Imports com.mit.notepad.models.Alarm so this file can use that type by its short name.
import com.mit.notepad.models.Alarm;
// Imports com.mit.notepad.models.Note so this file can use that type by its short name.
import com.mit.notepad.models.Note;
// Imports com.mit.notepad.models.Tag so this file can use that type by its short name.
import com.mit.notepad.models.Tag;
// Imports com.mit.notepad.repositories.AlarmRepository so this file can use that type by its short name.
import com.mit.notepad.repositories.AlarmRepository;
// Imports com.mit.notepad.repositories.NoteRepository so this file can use that type by its short name.
import com.mit.notepad.repositories.NoteRepository;
// Imports com.mit.notepad.utils.Constants so this file can use that type by its short name.
import com.mit.notepad.utils.Constants;
// Imports com.mit.notepad.utils.DateTimeUtils so this file can use that type by its short name.
import com.mit.notepad.utils.DateTimeUtils;
// Imports com.mit.notepad.utils.RepeatLabels so this file can use that type by its short name.
import com.mit.notepad.utils.RepeatLabels;
// Imports com.mit.notepad.utils.RichText so this file can use that type by its short name.
import com.mit.notepad.utils.RichText;
// Imports com.mit.notepad.utils.TextStats so this file can use that type by its short name.
import com.mit.notepad.utils.TextStats;
// Imports com.mit.notepad.utils.UiUtils so this file can use that type by its short name.
import com.mit.notepad.utils.UiUtils;
// Imports com.mit.notepad.utils.Validator so this file can use that type by its short name.
import com.mit.notepad.utils.Validator;

// Imports java.util.ArrayList so this file can use that type by its short name.
import java.util.ArrayList;
// Imports java.util.HashSet so this file can use that type by its short name.
import java.util.HashSet;
// Imports java.util.List so this file can use that type by its short name.
import java.util.List;

/** Screen 3: add / edit a note, with formatting, favourites, to-do, alarm and tags. */
// Declares class NoteEditorActivity to group related state and behavior.
public class NoteEditorActivity extends BaseActivity {
    // Initializes STATE_TAGS with the text or query fragment shown here.
    private static final String STATE_TAGS = "state_tags";
    // Initializes STATE_ALARM_AT with the text or query fragment shown here.
    private static final String STATE_ALARM_AT = "state_alarm_at";
    // Initializes STATE_ALARM_REPEAT with the text or query fragment shown here.
    private static final String STATE_ALARM_REPEAT = "state_alarm_repeat";
    // Initializes STATE_ALARM_INTERVAL with the text or query fragment shown here.
    private static final String STATE_ALARM_INTERVAL = "state_alarm_interval";
    // Initializes STATE_ALARM_ENABLED with the text or query fragment shown here.
    private static final String STATE_ALARM_ENABLED = "state_alarm_enabled";
    // Initializes STATE_ALARM_DIRTY with the text or query fragment shown here.
    private static final String STATE_ALARM_DIRTY = "state_alarm_dirty";
    // Initializes KIND_BOLD with 0 so later statements can use the computed value.
    private static final int KIND_BOLD = 0;
    // Initializes KIND_ITALIC with 1 so later statements can use the computed value.
    private static final int KIND_ITALIC = 1;
    // Initializes KIND_UNDERLINE with 2 so later statements can use the computed value.
    private static final int KIND_UNDERLINE = 2;

    // Declares b for use by the surrounding method or class.
    private ActivityNoteEditorBinding b;
    // Declares notes for use by the surrounding method or class.
    private NoteRepository notes;
    // Declares alarms for use by the surrounding method or class.
    private AlarmRepository alarms;
    // Declares original for use by the surrounding method or class.
    private Note original;
    // Declares noteId for use by the surrounding method or class.
    private long noteId;

    // Initializes tagIds with new ArrayList<>() so later statements can use the computed value.
    private List<Long> tagIds = new ArrayList<>();
    // Initializes initialTagIds with new ArrayList<>() so later statements can use the computed value.
    private List<Long> initialTagIds = new ArrayList<>();
    // Declares alarmAt for use by the surrounding method or class.
    private long alarmAt;
    // Initializes alarmRepeat with Constants.REPEAT_NONE so later statements can use the computed value.
    private String alarmRepeat = Constants.REPEAT_NONE;
    // Initializes alarmInterval with 1 so later statements can use the computed value.
    private int alarmInterval = 1;
    // Initializes alarmEnabled with true so later statements can use the computed value.
    private boolean alarmEnabled = true;
    // Declares alarmDirty for use by the surrounding method or class.
    private boolean alarmDirty;

    // Initializes initialTitle with the text or query fragment shown here.
    private String initialTitle = "";
    // Initializes initialContent with the text or query fragment shown here.
    private String initialContent = "";
    // Initializes initialFormatting with the text or query fragment shown here.
    private String initialFormatting = "[]";
    // Declares initialFavorite for use by the surrounding method or class.
    private boolean initialFavorite;
    // Declares initialTodo for use by the surrounding method or class.
    private boolean initialTodo;
    // Declares saving for use by the surrounding method or class.
    private boolean saving;

    // Initializes alarmLauncher with  so later statements can use the computed value.
    private final ActivityResultLauncher<Intent> alarmLauncher =
            // Starts the callback body that handles the event or asynchronous result.
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                // If result.getResultCode() != RESULT_OK || result.getData() == null is true, return immediately.
                if (result.getResultCode() != RESULT_OK || result.getData() == null) return;
                // Initializes d with result.getData() so later statements can use the computed value.
                Intent d = result.getData();
                // If d.getBooleanExtra(Constants.EXTRA_ALARM_REMOVE, false) is true, { immediately.
                if (d.getBooleanExtra(Constants.EXTRA_ALARM_REMOVE, false)) {
                    // Stores the result of 0 in alarmAt for use by later logic.
                    alarmAt = 0;
                // Runs the fallback branch when the preceding condition did not match.
                } else {
                    // Stores the result of d.getLongExtra(Constants.EXTRA_ALARM_AT, 0) in alarmAt for use by later logic.
                    alarmAt = d.getLongExtra(Constants.EXTRA_ALARM_AT, 0);
                    // Stores the result of d.getStringExtra(Constants.EXTRA_ALARM_REPEAT) in alarmRepeat for use by later logic.
                    alarmRepeat = d.getStringExtra(Constants.EXTRA_ALARM_REPEAT);
                    // Stores the result of d.getIntExtra(Constants.EXTRA_ALARM_INTERVAL, 1) in alarmInterval for use by later logic.
                    alarmInterval = d.getIntExtra(Constants.EXTRA_ALARM_INTERVAL, 1);
                    // Stores the result of d.getBooleanExtra(Constants.EXTRA_ALARM_ENABLED, true) in alarmEnabled for use by later logic.
                    alarmEnabled = d.getBooleanExtra(Constants.EXTRA_ALARM_ENABLED, true);
                // Closes the current method, control-flow, or type block.
                }
                // Stores the result of true in alarmDirty for use by later logic.
                alarmDirty = true;
                // Calls refreshRows with  to perform the operation shown.
                refreshRows();
            // Executes }) as a statement.
            });

    // Applies @Override metadata to the declaration that follows.
    @Override
    // Runs component initialization when Android invokes this lifecycle callback.
    protected void onCreate(Bundle savedInstanceState) {
        // Calls super.onCreate with savedInstanceState to perform the operation shown.
        super.onCreate(savedInstanceState);
        // Stores the result of ActivityNoteEditorBinding.inflate(getLayoutInflater()) in b for use by later logic.
        b = ActivityNoteEditorBinding.inflate(getLayoutInflater());
        // Calls setContentView with b.getRoot( to perform the operation shown.
        setContentView(b.getRoot());
        // Stores the result of new NoteRepository(this) in notes for use by later logic.
        notes = new NoteRepository(this);
        // Stores the result of new AlarmRepository(this) in alarms for use by later logic.
        alarms = new AlarmRepository(this);

        // Stores the result of getIntent().getLongExtra(Constants.EXTRA_NOTE_ID, 0) in noteId for use by later logic.
        noteId = getIntent().getLongExtra(Constants.EXTRA_NOTE_ID, 0);
        // If noteId > 0 is true, { immediately.
        if (noteId > 0) {
            // Stores the result of notes.get(noteId) in original for use by later logic.
            original = notes.get(noteId);
            // If original == null is true, { immediately.
            if (original == null) {
                // Calls UiUtils.toast with this, R.string.item_no_longer_exists to perform the operation shown.
                UiUtils.toast(this, R.string.item_no_longer_exists);
                // Calls finish with  to perform the operation shown.
                finish();
                // Returns control to the caller without a value.
                return;
            // Closes the current method, control-flow, or type block.
            }
        // Closes the current method, control-flow, or type block.
        }

        // Calls setupBackHeader with b.header, noteId > 0 ? R.string.edit_note_title : R.string.add_note_title, to perform the operation shown.
        setupBackHeader(b.header, noteId > 0 ? R.string.edit_note_title : R.string.add_note_title,
                // Executes R.string.capture_thoughts) as a statement.
                R.string.capture_thoughts);
        // Calls b.header.headerAction.setImageResource with R.drawable.ic_check to perform the operation shown.
        b.header.headerAction.setImageResource(R.drawable.ic_check);
        // Calls b.header.headerAction.setContentDescription with getString(R.string.save to perform the operation shown.
        b.header.headerAction.setContentDescription(getString(R.string.save));
        // Calls b.header.headerAction.setVisibility with View.VISIBLE to perform the operation shown.
        b.header.headerAction.setVisibility(View.VISIBLE);
        // Calls b.header.headerAction.setOnClickListener with v -> save( to perform the operation shown.
        b.header.headerAction.setOnClickListener(v -> save());
        // Calls b.btnSave.setOnClickListener with v -> save( to perform the operation shown.
        b.btnSave.setOnClickListener(v -> save());
        // Calls b.btnCancel.setOnClickListener with v -> getOnBackPressedDispatcher().onBackPressed( to perform the operation shown.
        b.btnCancel.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        // Snapshot of what is stored in the database (for unsaved-change detection)
        // If original != null is true, { immediately.
        if (original != null) {
            // Stores the result of original.title in initialTitle for use by later logic.
            initialTitle = original.title;
            // Stores the result of original.content in initialContent for use by later logic.
            initialContent = original.content;
            // Stores the result of RichText.toJson(RichText.fromJson(original.content, original.formatting)) in initialFormatting for use by later logic.
            initialFormatting = RichText.toJson(RichText.fromJson(original.content, original.formatting));
            // Stores the result of original.favorite in initialFavorite for use by later logic.
            initialFavorite = original.favorite;
            // Stores the result of original.isTodo() in initialTodo for use by later logic.
            initialTodo = original.isTodo();
            // Repeats the following block according to the initialization, condition, and update in Tag t : original.tags) initialTagIds.add(t.id.
            for (Tag t : original.tags) initialTagIds.add(t.id);
        // Closes the current method, control-flow, or type block.
        }

        // If savedInstanceState == null is true, { immediately.
        if (savedInstanceState == null) {
            // If original != null is true, { immediately.
            if (original != null) {
                // Calls b.etTitle.setText with original.title to perform the operation shown.
                b.etTitle.setText(original.title);
                // Calls b.etContent.setText with RichText.fromJson(original.content, original.formatting to perform the operation shown.
                b.etContent.setText(RichText.fromJson(original.content, original.formatting));
                // Calls b.swFavorite.setChecked with original.favorite to perform the operation shown.
                b.swFavorite.setChecked(original.favorite);
                // Calls b.swTodo.setChecked with original.isTodo( to perform the operation shown.
                b.swTodo.setChecked(original.isTodo());
                // Stores the result of new ArrayList<>(initialTagIds) in tagIds for use by later logic.
                tagIds = new ArrayList<>(initialTagIds);
                // Initializes a with alarms.getForNote(noteId) so later statements can use the computed value.
                Alarm a = alarms.getForNote(noteId);
                // If a != null is true, { immediately.
                if (a != null) {
                    // Stores the result of a.scheduledAt in alarmAt for use by later logic.
                    alarmAt = a.scheduledAt;
                    // Stores the result of a.repeatType in alarmRepeat for use by later logic.
                    alarmRepeat = a.repeatType;
                    // Stores the result of a.repeatInterval in alarmInterval for use by later logic.
                    alarmInterval = a.repeatInterval;
                    // Stores the result of a.enabled in alarmEnabled for use by later logic.
                    alarmEnabled = a.enabled;
                // Closes the current method, control-flow, or type block.
                }
            // Closes the current method, control-flow, or type block.
            }
        // Runs the fallback branch when the preceding condition did not match.
        } else {
            // Calls savedInstanceState.getLongArray with STATE_TAGS to perform the operation shown.
            long[] saved = savedInstanceState.getLongArray(STATE_TAGS);
            // Stores the result of new ArrayList<>() in tagIds for use by later logic.
            tagIds = new ArrayList<>();
            // If saved != null is true, for (long id : saved) tagIds.add(id) immediately.
            if (saved != null) for (long id : saved) tagIds.add(id);
            // Stores the result of savedInstanceState.getLong(STATE_ALARM_AT) in alarmAt for use by later logic.
            alarmAt = savedInstanceState.getLong(STATE_ALARM_AT);
            // Stores the result of savedInstanceState.getString(STATE_ALARM_REPEAT, Constants.REPEAT_NONE) in alarmRepeat for use by later logic.
            alarmRepeat = savedInstanceState.getString(STATE_ALARM_REPEAT, Constants.REPEAT_NONE);
            // Stores the result of savedInstanceState.getInt(STATE_ALARM_INTERVAL, 1) in alarmInterval for use by later logic.
            alarmInterval = savedInstanceState.getInt(STATE_ALARM_INTERVAL, 1);
            // Stores the result of savedInstanceState.getBoolean(STATE_ALARM_ENABLED, true) in alarmEnabled for use by later logic.
            alarmEnabled = savedInstanceState.getBoolean(STATE_ALARM_ENABLED, true);
            // Stores the result of savedInstanceState.getBoolean(STATE_ALARM_DIRTY) in alarmDirty for use by later logic.
            alarmDirty = savedInstanceState.getBoolean(STATE_ALARM_DIRTY);
        // Closes the current method, control-flow, or type block.
        }

        // Initializes watcher with new SimpleWatcher(this::updateCounts) so later statements can use the computed value.
        TextWatcher watcher = new SimpleWatcher(this::updateCounts);
        // Calls b.etTitle.addTextChangedListener with watcher to perform the operation shown.
        b.etTitle.addTextChangedListener(watcher);
        // Calls b.etContent.addTextChangedListener with watcher to perform the operation shown.
        b.etContent.addTextChangedListener(watcher);
        // Calls updateCounts with  to perform the operation shown.
        updateCounts();
        // Calls refreshRows with  to perform the operation shown.
        refreshRows();

        // Calls b.btnBold.setOnClickListener with v -> toggleStyle(KIND_BOLD to perform the operation shown.
        b.btnBold.setOnClickListener(v -> toggleStyle(KIND_BOLD));
        // Calls b.btnItalic.setOnClickListener with v -> toggleStyle(KIND_ITALIC to perform the operation shown.
        b.btnItalic.setOnClickListener(v -> toggleStyle(KIND_ITALIC));
        // Calls b.btnUnderline.setOnClickListener with v -> toggleStyle(KIND_UNDERLINE to perform the operation shown.
        b.btnUnderline.setOnClickListener(v -> toggleStyle(KIND_UNDERLINE));
        // Calls b.btnBullet.setOnClickListener with v -> toggleBullets( to perform the operation shown.
        b.btnBullet.setOnClickListener(v -> toggleBullets());
        // Calls b.btnLink.setOnClickListener with v -> addLink( to perform the operation shown.
        b.btnLink.setOnClickListener(v -> addLink());
        // Starts the callback body that handles the event or asynchronous result.
        b.rowTags.setOnClickListener(v -> TagDialogs.pickTags(this, tagIds, ids -> {
            // Stores the result of ids in tagIds for use by later logic.
            tagIds = ids;
            // Calls refreshRows with  to perform the operation shown.
            refreshRows();
        // Executes })) as a statement.
        }));
        // Calls b.rowAlarm.setOnClickListener with v -> openAlarmScreen( to perform the operation shown.
        b.rowAlarm.setOnClickListener(v -> openAlarmScreen());

        // Calls getOnBackPressedDispatcher with ).addCallback(this, new OnBackPressedCallback(true) { to perform the operation shown.
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            // Applies @Override metadata to the declaration that follows.
            @Override
            // Closes the drawer, returns to the default tab, or exits the screen on Back.
            public void handleOnBackPressed() {
                // If hasChanges() && !saving is true, { immediately.
                if (hasChanges() && !saving) {
                    // Creates the object described by new MaterialAlertDialogBuilder(NoteEditorActivity.this).
                    new MaterialAlertDialogBuilder(NoteEditorActivity.this)
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
    // Closes the current method, control-flow, or type block.
    }

    // Applies @Override metadata to the declaration that follows.
    @Override
    // Stores transient UI values so Android can restore them later.
    protected void onSaveInstanceState(Bundle out) {
        // Calls super.onSaveInstanceState with out to perform the operation shown.
        super.onSaveInstanceState(out);
        // Calls tagIds.size with )] to perform the operation shown.
        long[] ids = new long[tagIds.size()];
        // Repeats the following block according to the initialization, condition, and update in int i = 0; i < ids.length; i++) ids[i] = tagIds.get(i.
        for (int i = 0; i < ids.length; i++) ids[i] = tagIds.get(i);
        // Calls out.putLongArray with STATE_TAGS, ids to perform the operation shown.
        out.putLongArray(STATE_TAGS, ids);
        // Calls out.putLong with STATE_ALARM_AT, alarmAt to perform the operation shown.
        out.putLong(STATE_ALARM_AT, alarmAt);
        // Calls out.putString with STATE_ALARM_REPEAT, alarmRepeat to perform the operation shown.
        out.putString(STATE_ALARM_REPEAT, alarmRepeat);
        // Calls out.putInt with STATE_ALARM_INTERVAL, alarmInterval to perform the operation shown.
        out.putInt(STATE_ALARM_INTERVAL, alarmInterval);
        // Calls out.putBoolean with STATE_ALARM_ENABLED, alarmEnabled to perform the operation shown.
        out.putBoolean(STATE_ALARM_ENABLED, alarmEnabled);
        // Calls out.putBoolean with STATE_ALARM_DIRTY, alarmDirty to perform the operation shown.
        out.putBoolean(STATE_ALARM_DIRTY, alarmDirty);
    // Closes the current method, control-flow, or type block.
    }

    // Checks whether changes.
    private boolean hasChanges() {
        // Initializes title with b.etTitle.getText().toString().trim() so later statements can use the computed value.
        String title = b.etTitle.getText().toString().trim();
        // Initializes content with b.etContent.getText().toString() so later statements can use the computed value.
        String content = b.etContent.getText().toString();
        // Returns !title.equals(initialTitle.trim()) to the caller.
        return !title.equals(initialTitle.trim())
                // Also accepts the expression !content.equals(initialContent) as a true condition.
                || !content.equals(initialContent)
                // Also accepts the expression !RichText.toJson(b.etContent.getText()).equals(initialFormatting) as a true condition.
                || !RichText.toJson(b.etContent.getText()).equals(initialFormatting)
                // Also accepts the expression b.swFavorite.isChecked() != initialFavorite as a true condition.
                || b.swFavorite.isChecked() != initialFavorite
                // Also accepts the expression b.swTodo.isChecked() != initialTodo as a true condition.
                || b.swTodo.isChecked() != initialTodo
                // Also accepts the expression !new HashSet<>(tagIds).equals(new HashSet<>(initialTagIds)) as a true condition.
                || !new HashSet<>(tagIds).equals(new HashSet<>(initialTagIds))
                // Also accepts the expression alarmDirty as a true condition.
                || alarmDirty;
    // Closes the current method, control-flow, or type block.
    }

    // Updates counts.
    private void updateCounts() {
        // Initializes text with b.etContent.getText().toString() so later statements can use the computed value.
        String text = b.etContent.getText().toString();
        // Calls b.tvCounts.setText with getString(R.string.words_chars, TextStats.wordCount(text), TextStats.charCount(text to perform the operation shown.
        b.tvCounts.setText(getString(R.string.words_chars, TextStats.wordCount(text), TextStats.charCount(text)));
        // Calls b.tvTitleCounter.setText with getString(R.string.counter, b.etTitle.getText().length(), Constants.MAX_TITLE_LENGTH to perform the operation shown.
        b.tvTitleCounter.setText(getString(R.string.counter, b.etTitle.getText().length(), Constants.MAX_TITLE_LENGTH));
    // Closes the current method, control-flow, or type block.
    }

    // Refreshes rows.
    private void refreshRows() {
        // If alarmAt <= 0 is true, { immediately.
        if (alarmAt <= 0) {
            // Calls b.tvAlarmValue.setText with R.string.no_alarm_set to perform the operation shown.
            b.tvAlarmValue.setText(R.string.no_alarm_set);
        // Runs the fallback branch when the preceding condition did not match.
        } else {
            // Initializes text with DateTimeUtils.formatDateTime(alarmAt) so later statements can use the computed value.
            String text = DateTimeUtils.formatDateTime(alarmAt);
            // If !Constants.REPEAT_NONE.equals(alarmRepeat) is true, { immediately.
            if (!Constants.REPEAT_NONE.equals(alarmRepeat)) {
                // Updates text by applying += to " · " + RepeatLabels.label(this, alarmRepeat, alarmInterval).
                text += " · " + RepeatLabels.label(this, alarmRepeat, alarmInterval);
            // Closes the current method, control-flow, or type block.
            }
            // If !alarmEnabled is true, text += " · " + getString(R.string.off) immediately.
            if (!alarmEnabled) text += " · " + getString(R.string.off);
            // Calls b.tvAlarmValue.setText with text to perform the operation shown.
            b.tvAlarmValue.setText(text);
        // Closes the current method, control-flow, or type block.
        }
        // Calls b.tvTagsValue.setText with TagDialogs.namesFor(this, tagIds to perform the operation shown.
        b.tvTagsValue.setText(TagDialogs.namesFor(this, tagIds));
    // Closes the current method, control-flow, or type block.
    }

    // Opens alarm screen.
    private void openAlarmScreen() {
        // Initializes title with b.etTitle.getText().toString().trim() so later statements can use the computed value.
        String title = b.etTitle.getText().toString().trim();
        // Initializes i with new Intent(this, AlarmActivity.class) so later statements can use the computed value.
        Intent i = new Intent(this, AlarmActivity.class)
                // Calls putExtra on the object from the preceding line with the shown arguments.
                .putExtra(Constants.EXTRA_RESULT_MODE, true)
                // Calls putExtra on the object from the preceding line with the shown arguments.
                .putExtra(Constants.EXTRA_TARGET_LABEL, title.isEmpty() ? getString(R.string.untitled) : title);
        // If alarmAt > 0 is true, { immediately.
        if (alarmAt > 0) {
            // Calls i.putExtra with Constants.EXTRA_ALARM_AT, alarmAt to perform the operation shown.
            i.putExtra(Constants.EXTRA_ALARM_AT, alarmAt)
                    // Calls putExtra on the object from the preceding line with the shown arguments.
                    .putExtra(Constants.EXTRA_ALARM_REPEAT, alarmRepeat)
                    // Calls putExtra on the object from the preceding line with the shown arguments.
                    .putExtra(Constants.EXTRA_ALARM_INTERVAL, alarmInterval)
                    // Calls putExtra on the object from the preceding line with the shown arguments.
                    .putExtra(Constants.EXTRA_ALARM_ENABLED, alarmEnabled);
        // Closes the current method, control-flow, or type block.
        }
        // Calls alarmLauncher.launch with i to perform the operation shown.
        alarmLauncher.launch(i);
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
        // If alarmDirty && alarmAt > 0 && alarmEnabled && alarmAt <= System.currentTimeMillis() is true, { immediately.
        if (alarmDirty && alarmAt > 0 && alarmEnabled && alarmAt <= System.currentTimeMillis()) {
            // Calls UiUtils.toast with this, R.string.error_alarm_in_past to perform the operation shown.
            UiUtils.toast(this, R.string.error_alarm_in_past);
            // Returns control to the caller without a value.
            return;
        // Closes the current method, control-flow, or type block.
        }
        // Initializes editable with b.etContent.getText() so later statements can use the computed value.
        Editable editable = b.etContent.getText();
        // Initializes n with original != null ? original : new Note() so later statements can use the computed value.
        Note n = original != null ? original : new Note();
        // Stores the result of title in n.title for use by later logic.
        n.title = title;
        // Stores the result of editable.toString() in n.content for use by later logic.
        n.content = editable.toString();
        // Stores the result of RichText.toJson(editable) in n.formatting for use by later logic.
        n.formatting = RichText.toJson(editable);
        // Stores the result of b.swFavorite.isChecked() in n.favorite for use by later logic.
        n.favorite = b.swFavorite.isChecked();
        // Stores the result of b.swTodo.isChecked() ? Constants.NOTE_TYPE_TODO : Constants.NOTE_TYPE_NOTE in n.noteType for use by later logic.
        n.noteType = b.swTodo.isChecked() ? Constants.NOTE_TYPE_TODO : Constants.NOTE_TYPE_NOTE;
        // Starts the operation whose failures are handled by the following catch clauses.
        try {
            // Initializes id with notes.save(n, tagIds) so later statements can use the computed value.
            long id = notes.save(n, tagIds);
            // If alarmDirty is true, { immediately.
            if (alarmDirty) {
                // If alarmAt > 0 is true, alarms.saveForNote(id, alarmAt, alarmEnabled, alarmRepeat, alarmInterval) immediately.
                if (alarmAt > 0) alarms.saveForNote(id, alarmAt, alarmEnabled, alarmRepeat, alarmInterval);
                // Runs the fallback branch when the preceding condition did not match.
                else alarms.deleteForNote(id);
            // Closes the current method, control-flow, or type block.
            }
        // Calls catch with NoteRepository.NoteLimitException e) { to perform the operation shown.
        } catch (NoteRepository.NoteLimitException e) {
            // Calls NoteActions.showLimitDialog with this to perform the operation shown.
            NoteActions.showLimitDialog(this);
            // Returns control to the caller without a value.
            return;
        // Calls catch with RuntimeException e) { to perform the operation shown.
        } catch (RuntimeException e) {
            // Calls UiUtils.toast with this, R.string.error_save_failed to perform the operation shown.
            UiUtils.toast(this, R.string.error_save_failed);
            // Returns control to the caller without a value.
            return;
        // Closes the current method, control-flow, or type block.
        }
        // Stores the result of true in saving for use by later logic.
        saving = true;
        // Calls UiUtils.toast with this, R.string.note_saved to perform the operation shown.
        UiUtils.toast(this, R.string.note_saved);
        // Calls finish with  to perform the operation shown.
        finish();
    // Closes the current method, control-flow, or type block.
    }

    // ------------------------------------------------------------------ formatting

    // Creates span.
    private Object createSpan(int kind) {
        // If kind == KIND_BOLD is true, return new StyleSpan(Typeface.BOLD) immediately.
        if (kind == KIND_BOLD) return new StyleSpan(Typeface.BOLD);
        // If kind == KIND_ITALIC is true, return new StyleSpan(Typeface.ITALIC) immediately.
        if (kind == KIND_ITALIC) return new StyleSpan(Typeface.ITALIC);
        // Returns new UnderlineSpan() to the caller.
        return new UnderlineSpan();
    // Closes the current method, control-flow, or type block.
    }

    // Declares matches() to implement this part of the component.
    private boolean matches(Object span, int kind) {
        // If kind == KIND_UNDERLINE is true, return span instanceof UnderlineSpan immediately.
        if (kind == KIND_UNDERLINE) return span instanceof UnderlineSpan;
        // If !(span instanceof StyleSpan) is true, return false immediately.
        if (!(span instanceof StyleSpan)) return false;
        // Initializes style with ((StyleSpan) span).getStyle() so later statements can use the computed value.
        int style = ((StyleSpan) span).getStyle();
        // Returns style == (kind == KIND_BOLD ? Typeface.BOLD : Typeface.ITALIC) to the caller.
        return style == (kind == KIND_BOLD ? Typeface.BOLD : Typeface.ITALIC);
    // Closes the current method, control-flow, or type block.
    }

    /** Applies the style to the selection, or removes it if the selection already has it. */
    // Toggles style.
    private void toggleStyle(int kind) {
        // Initializes et with b.etContent so later statements can use the computed value.
        EditText et = b.etContent;
        // Initializes s with Math.min(et.getSelectionStart(), et.getSelectionEnd()) so later statements can use the computed value.
        int s = Math.min(et.getSelectionStart(), et.getSelectionEnd());
        // Initializes e with Math.max(et.getSelectionStart(), et.getSelectionEnd()) so later statements can use the computed value.
        int e = Math.max(et.getSelectionStart(), et.getSelectionEnd());
        // If s < 0 || s == e is true, { immediately.
        if (s < 0 || s == e) {
            // Calls UiUtils.toast with this, R.string.select_text_first to perform the operation shown.
            UiUtils.toast(this, R.string.select_text_first);
            // Returns control to the caller without a value.
            return;
        // Closes the current method, control-flow, or type block.
        }
        // Initializes ed with et.getText() so later statements can use the computed value.
        Editable ed = et.getText();
        // Initializes existing with new ArrayList<>() so later statements can use the computed value.
        List<Object> existing = new ArrayList<>();
        // Repeats the following block according to the initialization, condition, and update in Object o : ed.getSpans(s, e, Object.class).
        for (Object o : ed.getSpans(s, e, Object.class)) {
            // If matches(o, kind) && ed.getSpanStart(o) < e && ed.getSpanEnd(o) > s is true, existing.add(o) immediately.
            if (matches(o, kind) && ed.getSpanStart(o) < e && ed.getSpanEnd(o) > s) existing.add(o);
        // Closes the current method, control-flow, or type block.
        }
        // Initializes flag with Spanned.SPAN_EXCLUSIVE_EXCLUSIVE so later statements can use the computed value.
        int flag = Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;
        // If existing.isEmpty() is true, { immediately.
        if (existing.isEmpty()) {
            // Calls ed.setSpan with createSpan(kind), s, e, flag to perform the operation shown.
            ed.setSpan(createSpan(kind), s, e, flag);
        // Runs the fallback branch when the preceding condition did not match.
        } else {
            // Repeats the following block according to the initialization, condition, and update in Object o : existing.
            for (Object o : existing) {
                // Initializes start with ed.getSpanStart(o) so later statements can use the computed value.
                int start = ed.getSpanStart(o);
                // Initializes end with ed.getSpanEnd(o) so later statements can use the computed value.
                int end = ed.getSpanEnd(o);
                // Calls ed.removeSpan with o to perform the operation shown.
                ed.removeSpan(o);
                // If start < s is true, ed.setSpan(createSpan(kind), start, s, flag) immediately.
                if (start < s) ed.setSpan(createSpan(kind), start, s, flag);
                // If end > e is true, ed.setSpan(createSpan(kind), e, end, flag) immediately.
                if (end > e) ed.setSpan(createSpan(kind), e, end, flag);
            // Closes the current method, control-flow, or type block.
            }
        // Closes the current method, control-flow, or type block.
        }
    // Closes the current method, control-flow, or type block.
    }

    /** Adds or removes a "• " prefix on every line touched by the selection. */
    // Toggles bullets.
    private void toggleBullets() {
        // Initializes et with b.etContent so later statements can use the computed value.
        EditText et = b.etContent;
        // Initializes ed with et.getText() so later statements can use the computed value.
        Editable ed = et.getText();
        // Initializes text with ed.toString() so later statements can use the computed value.
        String text = ed.toString();
        // Initializes s with Math.max(0, Math.min(et.getSelectionStart(), et.getSelectionEnd())) so later statements can use the computed value.
        int s = Math.max(0, Math.min(et.getSelectionStart(), et.getSelectionEnd()));
        // Initializes e with Math.max(0, Math.max(et.getSelectionStart(), et.getSelectionEnd())) so later statements can use the computed value.
        int e = Math.max(0, Math.max(et.getSelectionStart(), et.getSelectionEnd()));
        // Initializes lineStart with s == 0 ? 0 : text.lastIndexOf('\n', s - 1) + 1 so later statements can use the computed value.
        int lineStart = s == 0 ? 0 : text.lastIndexOf('\n', s - 1) + 1;
        // Initializes lineEnd with text.indexOf('\n', e) so later statements can use the computed value.
        int lineEnd = text.indexOf('\n', e);
        // If lineEnd < 0 is true, lineEnd = text.length() immediately.
        if (lineEnd < 0) lineEnd = text.length();

        // Initializes offsets with new ArrayList<>() so later statements can use the computed value.
        List<Integer> offsets = new ArrayList<>();
        // Calls offsets.add with lineStart to perform the operation shown.
        offsets.add(lineStart);
        // Repeats the following block according to the initialization, condition, and update in int i = lineStart; i < lineEnd; i++.
        for (int i = lineStart; i < lineEnd; i++) {
            // If text.charAt(i) == '\n' is true, offsets.add(i + 1) immediately.
            if (text.charAt(i) == '\n') offsets.add(i + 1);
        // Closes the current method, control-flow, or type block.
        }
        // Initializes allBullets with true so later statements can use the computed value.
        boolean allBullets = true;
        // Repeats the following block according to the initialization, condition, and update in int off : offsets.
        for (int off : offsets) {
            // If !text.startsWith("• ", off) is true, { immediately.
            if (!text.startsWith("• ", off)) {
                // Stores the result of false in allBullets for use by later logic.
                allBullets = false;
                // Exits the innermost loop or switch statement.
                break;
            // Closes the current method, control-flow, or type block.
            }
        // Closes the current method, control-flow, or type block.
        }
        // Repeats the following block according to the initialization, condition, and update in int i = offsets.size() - 1; i >= 0; i--.
        for (int i = offsets.size() - 1; i >= 0; i--) { // bottom-up keeps earlier offsets valid
            // Initializes off with offsets.get(i) so later statements can use the computed value.
            int off = offsets.get(i);
            // If allBullets is true, ed.delete(off, off + 2) immediately.
            if (allBullets) ed.delete(off, off + 2);
            // Runs the fallback branch when the preceding condition did not match.
            else ed.insert(off, "• ");
        // Closes the current method, control-flow, or type block.
        }
    // Closes the current method, control-flow, or type block.
    }

    // Adds link.
    private void addLink() {
        // Initializes et with b.etContent so later statements can use the computed value.
        EditText et = b.etContent;
        // Initializes s with Math.min(et.getSelectionStart(), et.getSelectionEnd()) so later statements can use the computed value.
        int s = Math.min(et.getSelectionStart(), et.getSelectionEnd());
        // Initializes e with Math.max(et.getSelectionStart(), et.getSelectionEnd()) so later statements can use the computed value.
        int e = Math.max(et.getSelectionStart(), et.getSelectionEnd());
        // If s < 0 || s == e is true, { immediately.
        if (s < 0 || s == e) {
            // Calls UiUtils.toast with this, R.string.select_text_first to perform the operation shown.
            UiUtils.toast(this, R.string.select_text_first);
            // Returns control to the caller without a value.
            return;
        // Closes the current method, control-flow, or type block.
        }
        // Initializes input with new EditText(this) so later statements can use the computed value.
        EditText input = new EditText(this);
        // Calls input.setHint with R.string.link_hint to perform the operation shown.
        input.setHint(R.string.link_hint);
        // Calls input.setInputType with android.text.InputType.TYPE_TEXT_VARIATION_URI to perform the operation shown.
        input.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_URI);
        // Initializes pad with UiUtils.dp(this, 20) so later statements can use the computed value.
        int pad = UiUtils.dp(this, 20);
        // Calls android.widget.FrameLayout with this to perform the operation shown.
        android.widget.FrameLayout holder = new android.widget.FrameLayout(this);
        // Calls holder.setPadding with pad, UiUtils.dp(this, 8), pad, 0 to perform the operation shown.
        holder.setPadding(pad, UiUtils.dp(this, 8), pad, 0);
        // Calls holder.addView with input to perform the operation shown.
        holder.addView(input);
        // Calls MaterialAlertDialogBuilder with this to perform the operation shown.
        androidx.appcompat.app.AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                // Calls setTitle on the object from the preceding line with the shown arguments.
                .setTitle(R.string.format_link)
                // Calls setView on the object from the preceding line with the shown arguments.
                .setView(holder)
                // Calls setPositiveButton on the object from the preceding line with the shown arguments.
                .setPositiveButton(R.string.ok, null)
                // Calls setNegativeButton on the object from the preceding line with the shown arguments.
                .setNegativeButton(R.string.cancel, null)
                // Calls create on the object from the preceding line with the shown arguments.
                .create();
        // Calls dialog.setOnShowListener with d -> dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE to perform the operation shown.
        dialog.setOnShowListener(d -> dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE)
                // Starts the callback body that handles the event or asynchronous result.
                .setOnClickListener(v -> {
                    // Initializes url with input.getText().toString().trim() so later statements can use the computed value.
                    String url = input.getText().toString().trim();
                    // If !url.contains("://") is true, url = "https://" + url immediately.
                    if (!url.contains("://")) url = "https://" + url;
                    // If !Patterns.WEB_URL.matcher(url).matches() is true, { immediately.
                    if (!Patterns.WEB_URL.matcher(url).matches()) {
                        // Calls input.setError with getString(R.string.error_invalid_link to perform the operation shown.
                        input.setError(getString(R.string.error_invalid_link));
                        // Returns control to the caller without a value.
                        return;
                    // Closes the current method, control-flow, or type block.
                    }
                    // Calls et.getText with ).setSpan(new URLSpan(url), s, e, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE to perform the operation shown.
                    et.getText().setSpan(new URLSpan(url), s, e, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    // Calls dialog.dismiss with  to perform the operation shown.
                    dialog.dismiss();
                // Executes })) as a statement.
                }));
        // Calls dialog.show with  to perform the operation shown.
        dialog.show();
    // Closes the current method, control-flow, or type block.
    }

    /** Minimal TextWatcher that only reacts after the text changed. */
    // Declares class SimpleWatcher to group related state and behavior.
    private static class SimpleWatcher implements TextWatcher {
        // Declares action for use by the surrounding method or class.
        private final Runnable action;

        // Calls SimpleWatcher with Runnable action) { to perform the operation shown.
        SimpleWatcher(Runnable action) {
            // Stores the result of action in this.action for use by later logic.
            this.action = action;
        // Closes the current method, control-flow, or type block.
        }

        // Applies @Override public void beforeTextChanged metadata to the declaration that follows.
        @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) { }

        // Applies @Override public void onTextChanged metadata to the declaration that follows.
        @Override public void onTextChanged(CharSequence s, int st, int bf, int c) { }

        // Applies @Override metadata to the declaration that follows.
        @Override
        // Runs the registered update after the text value changes.
        public void afterTextChanged(Editable s) {
            // Calls action.run with  to perform the operation shown.
            action.run();
        // Closes the current method, control-flow, or type block.
        }
    // Closes the current method, control-flow, or type block.
    }
// Closes the current method, control-flow, or type block.
}
