package com.mit.notepad.activities;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.mit.notepad.R;
import com.mit.notepad.databinding.ActivityNoteViewBinding;
import com.mit.notepad.models.Alarm;
import com.mit.notepad.models.Note;
import com.mit.notepad.models.Tag;
import com.mit.notepad.models.Todo;
import com.mit.notepad.repositories.AlarmRepository;
import com.mit.notepad.repositories.NoteRepository;
import com.mit.notepad.repositories.TodoRepository;
import com.mit.notepad.utils.Constants;
import com.mit.notepad.utils.DateTimeUtils;
import com.mit.notepad.utils.RichText;
import com.mit.notepad.utils.TagStyle;
import com.mit.notepad.utils.UiUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Screen 4: full note view with statistics and quick actions. */
public class NoteViewActivity extends BaseActivity {
    private ActivityNoteViewBinding b;
    private NoteRepository notes;
    private TodoRepository todos;
    private AlarmRepository alarms;
    private long noteId;
    private Note note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityNoteViewBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        notes = new NoteRepository(this);
        todos = new TodoRepository(this);
        alarms = new AlarmRepository(this);
        noteId = getIntent().getLongExtra(Constants.EXTRA_NOTE_ID, 0);

        setupBackHeader(b.header, R.string.title_note, R.string.subtitle_note);
        b.header.headerAction.setVisibility(View.VISIBLE);
        b.header.headerAction.setOnClickListener(v -> {
            if (note != null) NoteActions.edit(this, note);
        });
        b.header.headerSearch.setVisibility(View.VISIBLE);
        b.header.headerSearch.setContentDescription(getString(R.string.toggle_favourite));
        b.header.headerSearch.setOnClickListener(v -> toggleFavorite());
        b.header.headerMore.setVisibility(View.VISIBLE);
        b.header.headerMore.setOnClickListener(this::showMore);

        b.tvContent.setMovementMethod(LinkMovementMethod.getInstance());
        b.actionFav.setOnClickListener(v -> toggleFavorite());
        b.actionTodo.setOnClickListener(v -> {
            if (note == null) return;
            notes.setNoteType(noteId, note.isTodo() ? Constants.NOTE_TYPE_NOTE : Constants.NOTE_TYPE_TODO);
            load();
        });
        b.actionAlarm.setOnClickListener(v ->
                startActivity(new Intent(this, AlarmActivity.class).putExtra(Constants.EXTRA_NOTE_ID, noteId)));
        b.actionTags.setOnClickListener(v -> pickTags());
        b.btnDelete.setOnClickListener(v -> {
            if (note != null) NoteActions.confirmDelete(this, note, this::finish);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        load();
    }

    private void toggleFavorite() {
        if (note == null) return;
        notes.setFavorite(noteId, !note.favorite);
        load();
    }

    private void pickTags() {
        if (note == null) return;
        List<Long> ids = new ArrayList<>();
        for (Tag t : note.tags) ids.add(t.id);
        TagDialogs.pickTags(this, ids, newIds -> {
            notes.setNoteTags(noteId, newIds);
            load();
        });
    }

    private void showMore(View anchor) {
        if (note == null) return;
        PopupMenu pm = new PopupMenu(this, anchor);
        pm.inflate(R.menu.menu_note_view);
        pm.getMenu().findItem(R.id.action_pin).setTitle(note.pinned ? R.string.unpin : R.string.pin);
        pm.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_pin) {
                notes.setPinned(noteId, !note.pinned);
                load();
            } else if (item.getItemId() == R.id.action_delete) {
                NoteActions.confirmDelete(this, note, this::finish);
            }
            return true;
        });
        pm.show();
    }

    private void load() {
        note = notes.get(noteId);
        if (note == null) { // deleted elsewhere (e.g. from a notification)
            UiUtils.toast(this, R.string.item_no_longer_exists);
            finish();
            return;
        }
        b.tvTitle.setText(note.title);
        b.tvDateShort.setText(DateTimeUtils.formatDateTime(note.createdAt));
        b.tvContent.setText(RichText.fromJson(note.content, note.formatting));
        b.tvContent.setVisibility(note.content.isEmpty() ? View.GONE : View.VISIBLE);
        b.tvWords.setText(getString(R.string.label_words, note.wordCount));
        b.tvChars.setText(getString(R.string.label_chars, note.charCount));
        b.ivPinned.setVisibility(note.pinned ? View.VISIBLE : View.GONE);

        int color = note.tags.isEmpty() ? getColor(R.color.np_primary) : TagStyle.color(note.tags.get(0).color);
        b.ivIcon.setImageResource(note.tags.isEmpty()
                ? (note.isTodo() ? R.drawable.ic_todo : R.drawable.ic_doc) : TagStyle.iconRes(note.tags.get(0).icon));
        b.ivIcon.setImageTintList(ColorStateList.valueOf(color));
        b.iconTile.setBackgroundTintList(ColorStateList.valueOf((color & 0x00FFFFFF) | 0x2E000000));

        b.header.headerSearch.setImageResource(note.favorite ? R.drawable.ic_star : R.drawable.ic_star_border);
        b.header.headerSearch.setImageTintList(ColorStateList.valueOf(
                getColor(note.favorite ? R.color.np_star : R.color.np_text_primary)));
        b.ivFav.setImageResource(note.favorite ? R.drawable.ic_star : R.drawable.ic_star_border);
        b.ivFav.setImageTintList(ColorStateList.valueOf(getColor(note.favorite ? R.color.np_star : R.color.np_primary)));
        b.tvFav.setText(note.favorite ? R.string.remove_favourite : R.string.add_favourite);
        b.tvTodo.setText(note.isTodo() ? R.string.mark_as_note : R.string.mark_as_todo);

        Alarm alarm = alarms.getForNote(noteId);
        b.tvAlarm.setText(alarm != null ? R.string.edit_alarm : R.string.set_alarm);

        b.chipTags.removeAllViews();
        for (Tag t : note.tags) b.chipTags.addView(UiUtils.tagPill(this, t));
        TextView add = new TextView(this);
        add.setText(R.string.add_tag_pill);
        add.setTextSize(11);
        add.setTextColor(getColor(R.color.np_primary));
        add.setBackgroundResource(R.drawable.bg_pill);
        add.setPadding(UiUtils.dp(this, 10), UiUtils.dp(this, 4), UiUtils.dp(this, 10), UiUtils.dp(this, 4));
        add.setOnClickListener(v -> pickTags());
        b.chipTags.addView(add);

        Todo todo = note.isTodo() ? todos.getByNote(noteId) : null;
        b.todoRow.setVisibility(todo != null ? View.VISIBLE : View.GONE);
        if (todo != null) {
            b.cbDone.setOnCheckedChangeListener(null);
            b.cbDone.setChecked(todo.completed);
            b.cbDone.setOnCheckedChangeListener((v, checked) -> {
                todos.setCompleted(todo.id, checked);
                b.getRoot().post(this::load);
            });
            b.tvDueInfo.setText(alarm != null
                    ? getString(R.string.due_prefix, DateTimeUtils.formatDateTime(alarm.scheduledAt))
                    : getString(R.string.no_due_date));
        }

        b.tvCreated.setText(DateTimeUtils.formatDateTime(note.createdAt));
        b.tvModified.setText(DateTimeUtils.formatDateTime(note.updatedAt));
        b.tvNoteId.setText(String.format(Locale.US, "#%04d", note.id));
    }
}
