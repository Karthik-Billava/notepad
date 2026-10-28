// Sets the package name to com.mit.notepad.activities so this class belongs to the app module namespace.
package com.mit.notepad.activities;

// Imports android.content.Intent so this file can use that type by its short name.
import android.content.Intent;
// Imports android.content.res.ColorStateList so this file can use that type by its short name.
import android.content.res.ColorStateList;
// Imports android.os.Bundle so this file can use that type by its short name.
import android.os.Bundle;
// Imports android.text.method.LinkMovementMethod so this file can use that type by its short name.
import android.text.method.LinkMovementMethod;
// Imports android.view.Menu so this file can use that type by its short name.
import android.view.Menu;
// Imports android.view.View so this file can use that type by its short name.
import android.view.View;
// Imports android.widget.PopupMenu so this file can use that type by its short name.
import android.widget.PopupMenu;
// Imports android.widget.TextView so this file can use that type by its short name.
import android.widget.TextView;

// Imports com.mit.notepad.R so this file can use that type by its short name.
import com.mit.notepad.R;
// Imports com.mit.notepad.databinding.ActivityNoteViewBinding so this file can use that type by its short name.
import com.mit.notepad.databinding.ActivityNoteViewBinding;
// Imports com.mit.notepad.models.Alarm so this file can use that type by its short name.
import com.mit.notepad.models.Alarm;
// Imports com.mit.notepad.models.Note so this file can use that type by its short name.
import com.mit.notepad.models.Note;
// Imports com.mit.notepad.models.Tag so this file can use that type by its short name.
import com.mit.notepad.models.Tag;
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
// Imports com.mit.notepad.utils.RichText so this file can use that type by its short name.
import com.mit.notepad.utils.RichText;
// Imports com.mit.notepad.utils.TagStyle so this file can use that type by its short name.
import com.mit.notepad.utils.TagStyle;
// Imports com.mit.notepad.utils.UiUtils so this file can use that type by its short name.
import com.mit.notepad.utils.UiUtils;

// Imports java.util.ArrayList so this file can use that type by its short name.
import java.util.ArrayList;
// Imports java.util.List so this file can use that type by its short name.
import java.util.List;
// Imports java.util.Locale so this file can use that type by its short name.
import java.util.Locale;

/** Screen 4: full note view with statistics and quick actions. */
// Declares class NoteViewActivity to group related state and behavior.
public class NoteViewActivity extends BaseActivity {
    // Declares b for use by the surrounding method or class.
    private ActivityNoteViewBinding b;
    // Declares notes for use by the surrounding method or class.
    private NoteRepository notes;
    // Declares todos for use by the surrounding method or class.
    private TodoRepository todos;
    // Declares alarms for use by the surrounding method or class.
    private AlarmRepository alarms;
    // Declares noteId for use by the surrounding method or class.
    private long noteId;
    // Declares note for use by the surrounding method or class.
    private Note note;

    // Applies @Override metadata to the declaration that follows.
    @Override
    // Runs component initialization when Android invokes this lifecycle callback.
    protected void onCreate(Bundle savedInstanceState) {
        // Calls super.onCreate with savedInstanceState to perform the operation shown.
        super.onCreate(savedInstanceState);
        // Stores the result of ActivityNoteViewBinding.inflate(getLayoutInflater()) in b for use by later logic.
        b = ActivityNoteViewBinding.inflate(getLayoutInflater());
        // Calls setContentView with b.getRoot( to perform the operation shown.
        setContentView(b.getRoot());
        // Stores the result of new NoteRepository(this) in notes for use by later logic.
        notes = new NoteRepository(this);
        // Stores the result of new TodoRepository(this) in todos for use by later logic.
        todos = new TodoRepository(this);
        // Stores the result of new AlarmRepository(this) in alarms for use by later logic.
        alarms = new AlarmRepository(this);
        // Stores the result of getIntent().getLongExtra(Constants.EXTRA_NOTE_ID, 0) in noteId for use by later logic.
        noteId = getIntent().getLongExtra(Constants.EXTRA_NOTE_ID, 0);

        // Calls setupBackHeader with b.header, R.string.title_note, R.string.subtitle_note to perform the operation shown.
        setupBackHeader(b.header, R.string.title_note, R.string.subtitle_note);
        // Calls b.header.headerAction.setVisibility with View.VISIBLE to perform the operation shown.
        b.header.headerAction.setVisibility(View.VISIBLE);
        // Starts the callback body that handles the event or asynchronous result.
        b.header.headerAction.setOnClickListener(v -> {
            // If note != null is true, NoteActions.edit(this, note) immediately.
            if (note != null) NoteActions.edit(this, note);
        // Executes }) as a statement.
        });
        // Calls b.header.headerSearch.setVisibility with View.VISIBLE to perform the operation shown.
        b.header.headerSearch.setVisibility(View.VISIBLE);
        // Calls b.header.headerSearch.setContentDescription with getString(R.string.toggle_favourite to perform the operation shown.
        b.header.headerSearch.setContentDescription(getString(R.string.toggle_favourite));
        // Calls b.header.headerSearch.setOnClickListener with v -> toggleFavorite( to perform the operation shown.
        b.header.headerSearch.setOnClickListener(v -> toggleFavorite());
        // Calls b.header.headerMore.setVisibility with View.VISIBLE to perform the operation shown.
        b.header.headerMore.setVisibility(View.VISIBLE);
        // Calls b.header.headerMore.setOnClickListener with this::showMore to perform the operation shown.
        b.header.headerMore.setOnClickListener(this::showMore);

        // Calls b.tvContent.setMovementMethod with LinkMovementMethod.getInstance( to perform the operation shown.
        b.tvContent.setMovementMethod(LinkMovementMethod.getInstance());
        // Calls b.actionFav.setOnClickListener with v -> toggleFavorite( to perform the operation shown.
        b.actionFav.setOnClickListener(v -> toggleFavorite());
        // Starts the callback body that handles the event or asynchronous result.
        b.actionTodo.setOnClickListener(v -> {
            // If note == null is true, return immediately.
            if (note == null) return;
            // Calls notes.setNoteType with noteId, note.isTodo() ? Constants.NOTE_TYPE_NOTE : Constants.NOTE_TYPE_TODO to perform the operation shown.
            notes.setNoteType(noteId, note.isTodo() ? Constants.NOTE_TYPE_NOTE : Constants.NOTE_TYPE_TODO);
            // Calls load with  to perform the operation shown.
            load();
        // Executes }) as a statement.
        });
        // Calls b.actionAlarm.setOnClickListener with v -> to perform the operation shown.
        b.actionAlarm.setOnClickListener(v ->
                // Calls startActivity with new Intent(this, AlarmActivity.class).putExtra(Constants.EXTRA_NOTE_ID, noteId to perform the operation shown.
                startActivity(new Intent(this, AlarmActivity.class).putExtra(Constants.EXTRA_NOTE_ID, noteId)));
        // Calls b.actionTags.setOnClickListener with v -> pickTags( to perform the operation shown.
        b.actionTags.setOnClickListener(v -> pickTags());
        // Starts the callback body that handles the event or asynchronous result.
        b.btnDelete.setOnClickListener(v -> {
            // If note != null is true, NoteActions.confirmDelete(this, note, this::finish) immediately.
            if (note != null) NoteActions.confirmDelete(this, note, this::finish);
        // Executes }) as a statement.
        });
    // Closes the current method, control-flow, or type block.
    }

    // Applies @Override metadata to the declaration that follows.
    @Override
    // Refreshes visible data when this component becomes active.
    protected void onResume() {
        // Calls super.onResume with  to perform the operation shown.
        super.onResume();
        // Calls load with  to perform the operation shown.
        load();
    // Closes the current method, control-flow, or type block.
    }

    // Toggles favorite.
    private void toggleFavorite() {
        // If note == null is true, return immediately.
        if (note == null) return;
        // Calls notes.setFavorite with noteId, !note.favorite to perform the operation shown.
        notes.setFavorite(noteId, !note.favorite);
        // Calls load with  to perform the operation shown.
        load();
    // Closes the current method, control-flow, or type block.
    }

    // Prompts the user to choose tags.
    private void pickTags() {
        // If note == null is true, return immediately.
        if (note == null) return;
        // Initializes ids with new ArrayList<>() so later statements can use the computed value.
        List<Long> ids = new ArrayList<>();
        // Repeats the following block according to the initialization, condition, and update in Tag t : note.tags) ids.add(t.id.
        for (Tag t : note.tags) ids.add(t.id);
        // Starts the callback body that handles the event or asynchronous result.
        TagDialogs.pickTags(this, ids, newIds -> {
            // Calls notes.setNoteTags with noteId, newIds to perform the operation shown.
            notes.setNoteTags(noteId, newIds);
            // Calls load with  to perform the operation shown.
            load();
        // Executes }) as a statement.
        });
    // Closes the current method, control-flow, or type block.
    }

    // Displays more.
    private void showMore(View anchor) {
        // If note == null is true, return immediately.
        if (note == null) return;
        // Initializes pm with new PopupMenu(this, anchor) so later statements can use the computed value.
        PopupMenu pm = new PopupMenu(this, anchor);
        // Calls pm.inflate with R.menu.menu_note_view to perform the operation shown.
        pm.inflate(R.menu.menu_note_view);
        // Calls pm.getMenu with ).findItem(R.id.action_pin).setTitle(note.pinned ? R.string.unpin : R.string.pin to perform the operation shown.
        pm.getMenu().findItem(R.id.action_pin).setTitle(note.pinned ? R.string.unpin : R.string.pin);
        // Starts the callback body that handles the event or asynchronous result.
        pm.setOnMenuItemClickListener(item -> {
            // If item.getItemId() == R.id.action_pin is true, { immediately.
            if (item.getItemId() == R.id.action_pin) {
                // Calls notes.setPinned with noteId, !note.pinned to perform the operation shown.
                notes.setPinned(noteId, !note.pinned);
                // Calls load with  to perform the operation shown.
                load();
            // Checks the alternate condition (item.getItemId() after the earlier branch did not run.
            } else if (item.getItemId() == R.id.action_delete) {
                // Calls NoteActions.confirmDelete with this, note, this::finish to perform the operation shown.
                NoteActions.confirmDelete(this, note, this::finish);
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

    // Loads the latest records and refreshes the visible list.
    private void load() {
        // Stores the result of notes.get(noteId) in note for use by later logic.
        note = notes.get(noteId);
        // If note == null is true, { // deleted elsewhere (e.g. from a notification) immediately.
        if (note == null) { // deleted elsewhere (e.g. from a notification)
            // Calls UiUtils.toast with this, R.string.item_no_longer_exists to perform the operation shown.
            UiUtils.toast(this, R.string.item_no_longer_exists);
            // Calls finish with  to perform the operation shown.
            finish();
            // Returns control to the caller without a value.
            return;
        // Closes the current method, control-flow, or type block.
        }
        // Calls b.tvTitle.setText with note.title to perform the operation shown.
        b.tvTitle.setText(note.title);
        // Calls b.tvDateShort.setText with DateTimeUtils.formatDateTime(note.createdAt to perform the operation shown.
        b.tvDateShort.setText(DateTimeUtils.formatDateTime(note.createdAt));
        // Calls b.tvContent.setText with RichText.fromJson(note.content, note.formatting to perform the operation shown.
        b.tvContent.setText(RichText.fromJson(note.content, note.formatting));
        // Calls b.tvContent.setVisibility with note.content.isEmpty() ? View.GONE : View.VISIBLE to perform the operation shown.
        b.tvContent.setVisibility(note.content.isEmpty() ? View.GONE : View.VISIBLE);
        // Calls b.tvWords.setText with getString(R.string.label_words, note.wordCount to perform the operation shown.
        b.tvWords.setText(getString(R.string.label_words, note.wordCount));
        // Calls b.tvChars.setText with getString(R.string.label_chars, note.charCount to perform the operation shown.
        b.tvChars.setText(getString(R.string.label_chars, note.charCount));
        // Calls b.ivPinned.setVisibility with note.pinned ? View.VISIBLE : View.GONE to perform the operation shown.
        b.ivPinned.setVisibility(note.pinned ? View.VISIBLE : View.GONE);

        // Initializes color with note.tags.isEmpty() ? getColor(R.color.np_primary) : TagStyle.color(note.tags.get(0).color) so later statements can use the computed value.
        int color = note.tags.isEmpty() ? getColor(R.color.np_primary) : TagStyle.color(note.tags.get(0).color);
        // Calls b.ivIcon.setImageResource with note.tags.isEmpty( to perform the operation shown.
        b.ivIcon.setImageResource(note.tags.isEmpty()
                // Calls note.isTodo with ) ? R.drawable.ic_todo : R.drawable.ic_doc) : TagStyle.iconRes(note.tags.get(0).icon to perform the operation shown.
                ? (note.isTodo() ? R.drawable.ic_todo : R.drawable.ic_doc) : TagStyle.iconRes(note.tags.get(0).icon));
        // Calls b.ivIcon.setImageTintList with ColorStateList.valueOf(color to perform the operation shown.
        b.ivIcon.setImageTintList(ColorStateList.valueOf(color));
        // Calls b.iconTile.setBackgroundTintList with ColorStateList.valueOf((color & 0x00FFFFFF) | 0x2E000000 to perform the operation shown.
        b.iconTile.setBackgroundTintList(ColorStateList.valueOf((color & 0x00FFFFFF) | 0x2E000000));

        // Calls b.header.headerSearch.setImageResource with note.favorite ? R.drawable.ic_star : R.drawable.ic_star_border to perform the operation shown.
        b.header.headerSearch.setImageResource(note.favorite ? R.drawable.ic_star : R.drawable.ic_star_border);
        // Calls b.header.headerSearch.setImageTintList with ColorStateList.valueOf( to perform the operation shown.
        b.header.headerSearch.setImageTintList(ColorStateList.valueOf(
                // Calls getColor with note.favorite ? R.color.np_star : R.color.np_text_primary to perform the operation shown.
                getColor(note.favorite ? R.color.np_star : R.color.np_text_primary)));
        // Calls b.ivFav.setImageResource with note.favorite ? R.drawable.ic_star : R.drawable.ic_star_border to perform the operation shown.
        b.ivFav.setImageResource(note.favorite ? R.drawable.ic_star : R.drawable.ic_star_border);
        // Calls b.ivFav.setImageTintList with ColorStateList.valueOf(getColor(note.favorite ? R.color.np_star : R.color.np_primary to perform the operation shown.
        b.ivFav.setImageTintList(ColorStateList.valueOf(getColor(note.favorite ? R.color.np_star : R.color.np_primary)));
        // Calls b.tvFav.setText with note.favorite ? R.string.remove_favourite : R.string.add_favourite to perform the operation shown.
        b.tvFav.setText(note.favorite ? R.string.remove_favourite : R.string.add_favourite);
        // Calls b.tvTodo.setText with note.isTodo() ? R.string.mark_as_note : R.string.mark_as_todo to perform the operation shown.
        b.tvTodo.setText(note.isTodo() ? R.string.mark_as_note : R.string.mark_as_todo);

        // Initializes alarm with alarms.getForNote(noteId) so later statements can use the computed value.
        Alarm alarm = alarms.getForNote(noteId);
        // Calls b.tvAlarm.setText with alarm != null ? R.string.edit_alarm : R.string.set_alarm to perform the operation shown.
        b.tvAlarm.setText(alarm != null ? R.string.edit_alarm : R.string.set_alarm);

        // Calls b.chipTags.removeAllViews with  to perform the operation shown.
        b.chipTags.removeAllViews();
        // Repeats the following block according to the initialization, condition, and update in Tag t : note.tags) b.chipTags.addView(UiUtils.tagPill(this, t).
        for (Tag t : note.tags) b.chipTags.addView(UiUtils.tagPill(this, t));
        // Initializes add with new TextView(this) so later statements can use the computed value.
        TextView add = new TextView(this);
        // Calls add.setText with R.string.add_tag_pill to perform the operation shown.
        add.setText(R.string.add_tag_pill);
        // Calls add.setTextSize with 11 to perform the operation shown.
        add.setTextSize(11);
        // Calls add.setTextColor with getColor(R.color.np_primary to perform the operation shown.
        add.setTextColor(getColor(R.color.np_primary));
        // Calls add.setBackgroundResource with R.drawable.bg_pill to perform the operation shown.
        add.setBackgroundResource(R.drawable.bg_pill);
        // Calls add.setPadding with UiUtils.dp(this, 10), UiUtils.dp(this, 4), UiUtils.dp(this, 10), UiUtils.dp(this, 4 to perform the operation shown.
        add.setPadding(UiUtils.dp(this, 10), UiUtils.dp(this, 4), UiUtils.dp(this, 10), UiUtils.dp(this, 4));
        // Calls add.setOnClickListener with v -> pickTags( to perform the operation shown.
        add.setOnClickListener(v -> pickTags());
        // Calls b.chipTags.addView with add to perform the operation shown.
        b.chipTags.addView(add);

        // Initializes todo with note.isTodo() ? todos.getByNote(noteId) : null so later statements can use the computed value.
        Todo todo = note.isTodo() ? todos.getByNote(noteId) : null;
        // Calls b.todoRow.setVisibility with todo != null ? View.VISIBLE : View.GONE to perform the operation shown.
        b.todoRow.setVisibility(todo != null ? View.VISIBLE : View.GONE);
        // If todo != null is true, { immediately.
        if (todo != null) {
            // Calls b.cbDone.setOnCheckedChangeListener with null to perform the operation shown.
            b.cbDone.setOnCheckedChangeListener(null);
            // Calls b.cbDone.setChecked with todo.completed to perform the operation shown.
            b.cbDone.setChecked(todo.completed);
            // Starts the callback body that handles the event or asynchronous result.
            b.cbDone.setOnCheckedChangeListener((v, checked) -> {
                // Calls todos.setCompleted with todo.id, checked to perform the operation shown.
                todos.setCompleted(todo.id, checked);
                // Calls b.getRoot with ).post(this::load to perform the operation shown.
                b.getRoot().post(this::load);
            // Executes }) as a statement.
            });
            // Calls b.tvDueInfo.setText with alarm != null to perform the operation shown.
            b.tvDueInfo.setText(alarm != null
                    // Calls getString with R.string.due_prefix, DateTimeUtils.formatDateTime(alarm.scheduledAt to perform the operation shown.
                    ? getString(R.string.due_prefix, DateTimeUtils.formatDateTime(alarm.scheduledAt))
                    // Calls getString with R.string.no_due_date to perform the operation shown.
                    : getString(R.string.no_due_date));
        // Closes the current method, control-flow, or type block.
        }

        // Calls b.tvCreated.setText with DateTimeUtils.formatDateTime(note.createdAt to perform the operation shown.
        b.tvCreated.setText(DateTimeUtils.formatDateTime(note.createdAt));
        // Calls b.tvModified.setText with DateTimeUtils.formatDateTime(note.updatedAt to perform the operation shown.
        b.tvModified.setText(DateTimeUtils.formatDateTime(note.updatedAt));
        // Calls b.tvNoteId.setText with String.format(Locale.US, "#%04d", note.id to perform the operation shown.
        b.tvNoteId.setText(String.format(Locale.US, "#%04d", note.id));
    // Closes the current method, control-flow, or type block.
    }
// Closes the current method, control-flow, or type block.
}
