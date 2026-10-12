package com.mit.notepad.activities;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.PopupMenu;

import com.mit.notepad.R;
import com.mit.notepad.models.Note;
import com.mit.notepad.repositories.NoteRepository;
import com.mit.notepad.utils.Constants;
import com.mit.notepad.utils.UiUtils;

import java.util.ArrayList;
import java.util.List;

/** The per-note popup menu and note operations shared by every list that shows notes. */
public final class NoteActions {
    private NoteActions() { }

    public static void open(Activity a, Note n) {
        a.startActivity(new Intent(a, NoteViewActivity.class).putExtra(Constants.EXTRA_NOTE_ID, n.id));
    }

    public static void edit(Activity a, Note n) {
        a.startActivity(new Intent(a, NoteEditorActivity.class).putExtra(Constants.EXTRA_NOTE_ID, n.id));
    }

    public static void toggleFavorite(Activity a, Note n, Runnable refresh) {
        new NoteRepository(a).setFavorite(n.id, !n.favorite);
        refresh.run();
    }

    /** Opens the editor for a new note unless the 50-note limit has been reached. */
    public static void createNote(Activity a) {
        if (new NoteRepository(a).isLimitReached()) {
            showLimitDialog(a);
            return;
        }
        a.startActivity(new Intent(a, NoteEditorActivity.class));
    }

    public static void showLimitDialog(Activity a) {
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(a)
                .setTitle(R.string.limit_reached_title)
                .setMessage(a.getString(R.string.limit_reached_message, Constants.MAX_NOTES))
                .setPositiveButton(R.string.ok, null)
                .show();
    }

    public static void confirmDelete(Activity a, Note n, Runnable afterDelete) {
        UiUtils.confirm(a, R.string.delete_note_title,
                a.getString(R.string.delete_note_message, n.title), R.string.delete, () -> {
                    new NoteRepository(a).delete(n.id);
                    UiUtils.toast(a, R.string.note_deleted);
                    afterDelete.run();
                });
    }

    public static void showMenu(Activity a, View anchor, Note n, Runnable refresh) {
        PopupMenu pm = new PopupMenu(a, anchor);
        pm.inflate(R.menu.menu_note_item);
        Menu m = pm.getMenu();
        m.findItem(R.id.action_pin).setTitle(n.pinned ? R.string.unpin : R.string.pin);
        m.findItem(R.id.action_favourite).setTitle(n.favorite ? R.string.remove_favourite : R.string.add_favourite);
        m.findItem(R.id.action_todo).setTitle(n.isTodo() ? R.string.mark_as_note : R.string.mark_as_todo);
        NoteRepository repo = new NoteRepository(a);
        pm.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.action_open) {
                open(a, n);
            } else if (id == R.id.action_edit) {
                edit(a, n);
            } else if (id == R.id.action_pin) {
                repo.setPinned(n.id, !n.pinned);
                refresh.run();
            } else if (id == R.id.action_favourite) {
                repo.setFavorite(n.id, !n.favorite);
                refresh.run();
            } else if (id == R.id.action_tags) {
                List<Long> ids = new ArrayList<>();
                for (com.mit.notepad.models.Tag t : n.tags) ids.add(t.id);
                TagDialogs.pickTags(a, ids, newIds -> {
                    repo.setNoteTags(n.id, newIds);
                    refresh.run();
                });
            } else if (id == R.id.action_todo) {
                repo.setNoteType(n.id, n.isTodo() ? Constants.NOTE_TYPE_NOTE : Constants.NOTE_TYPE_TODO);
                refresh.run();
            } else if (id == R.id.action_alarm) {
                a.startActivity(new Intent(a, AlarmActivity.class).putExtra(Constants.EXTRA_NOTE_ID, n.id));
            } else if (id == R.id.action_delete) {
                confirmDelete(a, n, refresh);
            }
            return true;
        });
        pm.show();
    }
}
