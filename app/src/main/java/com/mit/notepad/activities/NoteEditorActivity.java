package com.mit.notepad.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.mit.notepad.R;
import com.mit.notepad.databinding.ActivityNoteEditorBinding;
import com.mit.notepad.models.Alarm;
import com.mit.notepad.models.Note;
import com.mit.notepad.models.Tag;
import com.mit.notepad.repositories.AlarmRepository;
import com.mit.notepad.repositories.NoteRepository;
import com.mit.notepad.utils.Constants;
import com.mit.notepad.utils.DateTimeUtils;
import com.mit.notepad.utils.RepeatLabels;
import com.mit.notepad.utils.RichText;
import com.mit.notepad.utils.TextStats;
import com.mit.notepad.utils.UiUtils;
import com.mit.notepad.utils.Validator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/** Screen 3: add / edit a note, with formatting, favourites, to-do, alarm and tags. */
public class NoteEditorActivity extends BaseActivity {
    private static final String STATE_TAGS = "state_tags";
    private static final String STATE_ALARM_AT = "state_alarm_at";
    private static final String STATE_ALARM_REPEAT = "state_alarm_repeat";
    private static final String STATE_ALARM_INTERVAL = "state_alarm_interval";
    private static final String STATE_ALARM_ENABLED = "state_alarm_enabled";
    private static final String STATE_ALARM_DIRTY = "state_alarm_dirty";
    private static final int KIND_BOLD = 0;
    private static final int KIND_ITALIC = 1;
    private static final int KIND_UNDERLINE = 2;

    private ActivityNoteEditorBinding b;
    private NoteRepository notes;
    private AlarmRepository alarms;
    private Note original;
    private long noteId;

    private List<Long> tagIds = new ArrayList<>();
    private List<Long> initialTagIds = new ArrayList<>();
    private long alarmAt;
    private String alarmRepeat = Constants.REPEAT_NONE;
    private int alarmInterval = 1;
    private boolean alarmEnabled = true;
    private boolean alarmDirty;

    private String initialTitle = "";
    private String initialContent = "";
    private String initialFormatting = "[]";
    private boolean initialFavorite;
    private boolean initialTodo;
    private boolean saving;

    private final ActivityResultLauncher<Intent> alarmLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() != RESULT_OK || result.getData() == null) return;
                Intent d = result.getData();
                if (d.getBooleanExtra(Constants.EXTRA_ALARM_REMOVE, false)) {
                    alarmAt = 0;
                } else {
                    alarmAt = d.getLongExtra(Constants.EXTRA_ALARM_AT, 0);
                    alarmRepeat = d.getStringExtra(Constants.EXTRA_ALARM_REPEAT);
                    alarmInterval = d.getIntExtra(Constants.EXTRA_ALARM_INTERVAL, 1);
                    alarmEnabled = d.getBooleanExtra(Constants.EXTRA_ALARM_ENABLED, true);
                }
                alarmDirty = true;
                refreshRows();
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityNoteEditorBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        notes = new NoteRepository(this);
        alarms = new AlarmRepository(this);

        noteId = getIntent().getLongExtra(Constants.EXTRA_NOTE_ID, 0);
        if (noteId > 0) {
            original = notes.get(noteId);
            if (original == null) {
                UiUtils.toast(this, R.string.item_no_longer_exists);
                finish();
                return;
            }
        }

        setupBackHeader(b.header, noteId > 0 ? R.string.edit_note_title : R.string.add_note_title,
                R.string.capture_thoughts);
        b.header.headerAction.setImageResource(R.drawable.ic_check);
        b.header.headerAction.setContentDescription(getString(R.string.save));
        b.header.headerAction.setVisibility(View.VISIBLE);
        b.header.headerAction.setOnClickListener(v -> save());
        b.btnSave.setOnClickListener(v -> save());
        b.btnCancel.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        // Snapshot of what is stored in the database (for unsaved-change detection)
        if (original != null) {
            initialTitle = original.title;
            initialContent = original.content;
            initialFormatting = RichText.toJson(RichText.fromJson(original.content, original.formatting));
            initialFavorite = original.favorite;
            initialTodo = original.isTodo();
            for (Tag t : original.tags) initialTagIds.add(t.id);
        }

        if (savedInstanceState == null) {
            if (original != null) {
                b.etTitle.setText(original.title);
                b.etContent.setText(RichText.fromJson(original.content, original.formatting));
                b.swFavorite.setChecked(original.favorite);
                b.swTodo.setChecked(original.isTodo());
                tagIds = new ArrayList<>(initialTagIds);
                Alarm a = alarms.getForNote(noteId);
                if (a != null) {
                    alarmAt = a.scheduledAt;
                    alarmRepeat = a.repeatType;
                    alarmInterval = a.repeatInterval;
                    alarmEnabled = a.enabled;
                }
            }
        } else {
            long[] saved = savedInstanceState.getLongArray(STATE_TAGS);
            tagIds = new ArrayList<>();
            if (saved != null) for (long id : saved) tagIds.add(id);
            alarmAt = savedInstanceState.getLong(STATE_ALARM_AT);
            alarmRepeat = savedInstanceState.getString(STATE_ALARM_REPEAT, Constants.REPEAT_NONE);
            alarmInterval = savedInstanceState.getInt(STATE_ALARM_INTERVAL, 1);
            alarmEnabled = savedInstanceState.getBoolean(STATE_ALARM_ENABLED, true);
            alarmDirty = savedInstanceState.getBoolean(STATE_ALARM_DIRTY);
        }

        TextWatcher watcher = new SimpleWatcher(this::updateCounts);
        b.etTitle.addTextChangedListener(watcher);
        b.etContent.addTextChangedListener(watcher);
        updateCounts();
        refreshRows();

        b.btnBold.setOnClickListener(v -> toggleStyle(KIND_BOLD));
        b.btnItalic.setOnClickListener(v -> toggleStyle(KIND_ITALIC));
        b.btnUnderline.setOnClickListener(v -> toggleStyle(KIND_UNDERLINE));
        b.btnBullet.setOnClickListener(v -> toggleBullets());
        b.btnLink.setOnClickListener(v -> addLink());
        b.rowTags.setOnClickListener(v -> TagDialogs.pickTags(this, tagIds, ids -> {
            tagIds = ids;
            refreshRows();
        }));
        b.rowAlarm.setOnClickListener(v -> openAlarmScreen());

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (hasChanges() && !saving) {
                    new MaterialAlertDialogBuilder(NoteEditorActivity.this)
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
    }

    @Override
    protected void onSaveInstanceState(Bundle out) {
        super.onSaveInstanceState(out);
        long[] ids = new long[tagIds.size()];
        for (int i = 0; i < ids.length; i++) ids[i] = tagIds.get(i);
        out.putLongArray(STATE_TAGS, ids);
        out.putLong(STATE_ALARM_AT, alarmAt);
        out.putString(STATE_ALARM_REPEAT, alarmRepeat);
        out.putInt(STATE_ALARM_INTERVAL, alarmInterval);
        out.putBoolean(STATE_ALARM_ENABLED, alarmEnabled);
        out.putBoolean(STATE_ALARM_DIRTY, alarmDirty);
    }

    private boolean hasChanges() {
        String title = b.etTitle.getText().toString().trim();
        String content = b.etContent.getText().toString();
        return !title.equals(initialTitle.trim())
                || !content.equals(initialContent)
                || !RichText.toJson(b.etContent.getText()).equals(initialFormatting)
                || b.swFavorite.isChecked() != initialFavorite
                || b.swTodo.isChecked() != initialTodo
                || !new HashSet<>(tagIds).equals(new HashSet<>(initialTagIds))
                || alarmDirty;
    }

    private void updateCounts() {
        String text = b.etContent.getText().toString();
        b.tvCounts.setText(getString(R.string.words_chars, TextStats.wordCount(text), TextStats.charCount(text)));
        b.tvTitleCounter.setText(getString(R.string.counter, b.etTitle.getText().length(), Constants.MAX_TITLE_LENGTH));
    }

    private void refreshRows() {
        if (alarmAt <= 0) {
            b.tvAlarmValue.setText(R.string.no_alarm_set);
        } else {
            String text = DateTimeUtils.formatDateTime(alarmAt);
            if (!Constants.REPEAT_NONE.equals(alarmRepeat)) {
                text += " · " + RepeatLabels.label(this, alarmRepeat, alarmInterval);
            }
            if (!alarmEnabled) text += " · " + getString(R.string.off);
            b.tvAlarmValue.setText(text);
        }
        b.tvTagsValue.setText(TagDialogs.namesFor(this, tagIds));
    }

    private void openAlarmScreen() {
        String title = b.etTitle.getText().toString().trim();
        Intent i = new Intent(this, AlarmActivity.class)
                .putExtra(Constants.EXTRA_RESULT_MODE, true)
                .putExtra(Constants.EXTRA_TARGET_LABEL, title.isEmpty() ? getString(R.string.untitled) : title);
        if (alarmAt > 0) {
            i.putExtra(Constants.EXTRA_ALARM_AT, alarmAt)
                    .putExtra(Constants.EXTRA_ALARM_REPEAT, alarmRepeat)
                    .putExtra(Constants.EXTRA_ALARM_INTERVAL, alarmInterval)
                    .putExtra(Constants.EXTRA_ALARM_ENABLED, alarmEnabled);
        }
        alarmLauncher.launch(i);
    }

    private void save() {
        String title = b.etTitle.getText().toString().trim();
        int error = Validator.titleError(title);
        if (error != 0) {
            b.etTitle.setError(getString(error));
            b.etTitle.requestFocus();
            return;
        }
        if (alarmDirty && alarmAt > 0 && alarmEnabled && alarmAt <= System.currentTimeMillis()) {
            UiUtils.toast(this, R.string.error_alarm_in_past);
            return;
        }
        Editable editable = b.etContent.getText();
        Note n = original != null ? original : new Note();
        n.title = title;
        n.content = editable.toString();
        n.formatting = RichText.toJson(editable);
        n.favorite = b.swFavorite.isChecked();
        n.noteType = b.swTodo.isChecked() ? Constants.NOTE_TYPE_TODO : Constants.NOTE_TYPE_NOTE;
        try {
            long id = notes.save(n, tagIds);
            if (alarmDirty) {
                if (alarmAt > 0) alarms.saveForNote(id, alarmAt, alarmEnabled, alarmRepeat, alarmInterval);
                else alarms.deleteForNote(id);
            }
        } catch (NoteRepository.NoteLimitException e) {
            NoteActions.showLimitDialog(this);
            return;
        } catch (RuntimeException e) {
            UiUtils.toast(this, R.string.error_save_failed);
            return;
        }
        saving = true;
        UiUtils.toast(this, R.string.note_saved);
        finish();
    }

    // ------------------------------------------------------------------ formatting

    private Object createSpan(int kind) {
        if (kind == KIND_BOLD) return new StyleSpan(Typeface.BOLD);
        if (kind == KIND_ITALIC) return new StyleSpan(Typeface.ITALIC);
        return new UnderlineSpan();
    }

    private boolean matches(Object span, int kind) {
        if (kind == KIND_UNDERLINE) return span instanceof UnderlineSpan;
        if (!(span instanceof StyleSpan)) return false;
        int style = ((StyleSpan) span).getStyle();
        return style == (kind == KIND_BOLD ? Typeface.BOLD : Typeface.ITALIC);
    }

    /** Applies the style to the selection, or removes it if the selection already has it. */
    private void toggleStyle(int kind) {
        EditText et = b.etContent;
        int s = Math.min(et.getSelectionStart(), et.getSelectionEnd());
        int e = Math.max(et.getSelectionStart(), et.getSelectionEnd());
        if (s < 0 || s == e) {
            UiUtils.toast(this, R.string.select_text_first);
            return;
        }
        Editable ed = et.getText();
        List<Object> existing = new ArrayList<>();
        for (Object o : ed.getSpans(s, e, Object.class)) {
            if (matches(o, kind) && ed.getSpanStart(o) < e && ed.getSpanEnd(o) > s) existing.add(o);
        }
        int flag = Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;
        if (existing.isEmpty()) {
            ed.setSpan(createSpan(kind), s, e, flag);
        } else {
            for (Object o : existing) {
                int start = ed.getSpanStart(o);
                int end = ed.getSpanEnd(o);
                ed.removeSpan(o);
                if (start < s) ed.setSpan(createSpan(kind), start, s, flag);
                if (end > e) ed.setSpan(createSpan(kind), e, end, flag);
            }
        }
    }

    /** Adds or removes a "• " prefix on every line touched by the selection. */
    private void toggleBullets() {
        EditText et = b.etContent;
        Editable ed = et.getText();
        String text = ed.toString();
        int s = Math.max(0, Math.min(et.getSelectionStart(), et.getSelectionEnd()));
        int e = Math.max(0, Math.max(et.getSelectionStart(), et.getSelectionEnd()));
        int lineStart = s == 0 ? 0 : text.lastIndexOf('\n', s - 1) + 1;
        int lineEnd = text.indexOf('\n', e);
        if (lineEnd < 0) lineEnd = text.length();

        List<Integer> offsets = new ArrayList<>();
        offsets.add(lineStart);
        for (int i = lineStart; i < lineEnd; i++) {
            if (text.charAt(i) == '\n') offsets.add(i + 1);
        }
        boolean allBullets = true;
        for (int off : offsets) {
            if (!text.startsWith("• ", off)) {
                allBullets = false;
                break;
            }
        }
        for (int i = offsets.size() - 1; i >= 0; i--) { // bottom-up keeps earlier offsets valid
            int off = offsets.get(i);
            if (allBullets) ed.delete(off, off + 2);
            else ed.insert(off, "• ");
        }
    }

    private void addLink() {
        EditText et = b.etContent;
        int s = Math.min(et.getSelectionStart(), et.getSelectionEnd());
        int e = Math.max(et.getSelectionStart(), et.getSelectionEnd());
        if (s < 0 || s == e) {
            UiUtils.toast(this, R.string.select_text_first);
            return;
        }
        EditText input = new EditText(this);
        input.setHint(R.string.link_hint);
        input.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_URI);
        int pad = UiUtils.dp(this, 20);
        android.widget.FrameLayout holder = new android.widget.FrameLayout(this);
        holder.setPadding(pad, UiUtils.dp(this, 8), pad, 0);
        holder.addView(input);
        androidx.appcompat.app.AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.format_link)
                .setView(holder)
                .setPositiveButton(R.string.ok, null)
                .setNegativeButton(R.string.cancel, null)
                .create();
        dialog.setOnShowListener(d -> dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener(v -> {
                    String url = input.getText().toString().trim();
                    if (!url.contains("://")) url = "https://" + url;
                    if (!Patterns.WEB_URL.matcher(url).matches()) {
                        input.setError(getString(R.string.error_invalid_link));
                        return;
                    }
                    et.getText().setSpan(new URLSpan(url), s, e, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    dialog.dismiss();
                }));
        dialog.show();
    }

    /** Minimal TextWatcher that only reacts after the text changed. */
    private static class SimpleWatcher implements TextWatcher {
        private final Runnable action;

        SimpleWatcher(Runnable action) {
            this.action = action;
        }

        @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) { }

        @Override public void onTextChanged(CharSequence s, int st, int bf, int c) { }

        @Override
        public void afterTextChanged(Editable s) {
            action.run();
        }
    }
}
