// Sets the package name to com.mit.notepad.activities so this class belongs to the app module namespace.
package com.mit.notepad.activities;

// Imports android.app.Activity so this file can use that type by its short name.
import android.app.Activity;
// Imports android.content.Intent so this file can use that type by its short name.
import android.content.Intent;
// Imports android.view.Menu so this file can use that type by its short name.
import android.view.Menu;
// Imports android.view.View so this file can use that type by its short name.
import android.view.View;
// Imports android.widget.PopupMenu so this file can use that type by its short name.
import android.widget.PopupMenu;

// Imports com.mit.notepad.R so this file can use that type by its short name.
import com.mit.notepad.R;
// Imports com.mit.notepad.models.Note so this file can use that type by its short name.
import com.mit.notepad.models.Note;
// Imports com.mit.notepad.repositories.NoteRepository so this file can use that type by its short name.
import com.mit.notepad.repositories.NoteRepository;
// Imports com.mit.notepad.utils.Constants so this file can use that type by its short name.
import com.mit.notepad.utils.Constants;
// Imports com.mit.notepad.utils.UiUtils so this file can use that type by its short name.
import com.mit.notepad.utils.UiUtils;

// Imports java.util.ArrayList so this file can use that type by its short name.
import java.util.ArrayList;
// Imports java.util.List so this file can use that type by its short name.
import java.util.List;

/** The per-note popup menu and note operations shared by every list that shows notes. */
// Declares class NoteActions to group related state and behavior.
public final class NoteActions {
    // Declares NoteActions() to implement this part of the component.
    private NoteActions() { }

    // Declares open() to implement this part of the component.
    public static void open(Activity a, Note n) {
        // If n.id < 0 is true, { immediately.
        if (n.id < 0) {
            // Calls a.startActivity with new Intent(a, TodoEditorActivity.class).putExtra(Constants.EXTRA_TODO_ID, -n.id to perform the operation shown.
            a.startActivity(new Intent(a, TodoEditorActivity.class).putExtra(Constants.EXTRA_TODO_ID, -n.id));
        // Runs the fallback branch when the preceding condition did not match.
        } else {
            // Calls a.startActivity with new Intent(a, NoteViewActivity.class).putExtra(Constants.EXTRA_NOTE_ID, n.id to perform the operation shown.
            a.startActivity(new Intent(a, NoteViewActivity.class).putExtra(Constants.EXTRA_NOTE_ID, n.id));
        // Closes the current method, control-flow, or type block.
        }
    // Closes the current method, control-flow, or type block.
    }

    // Declares edit() to implement this part of the component.
    public static void edit(Activity a, Note n) {
        // If n.id < 0 is true, { immediately.
        if (n.id < 0) {
            // Calls a.startActivity with new Intent(a, TodoEditorActivity.class).putExtra(Constants.EXTRA_TODO_ID, -n.id to perform the operation shown.
            a.startActivity(new Intent(a, TodoEditorActivity.class).putExtra(Constants.EXTRA_TODO_ID, -n.id));
        // Runs the fallback branch when the preceding condition did not match.
        } else {
            // Calls a.startActivity with new Intent(a, NoteEditorActivity.class).putExtra(Constants.EXTRA_NOTE_ID, n.id to perform the operation shown.
            a.startActivity(new Intent(a, NoteEditorActivity.class).putExtra(Constants.EXTRA_NOTE_ID, n.id));
        // Closes the current method, control-flow, or type block.
        }
    // Closes the current method, control-flow, or type block.
    }

    // Toggles favorite.
    public static void toggleFavorite(Activity a, Note n, Runnable refresh) {
        // If n.id > 0 is true, { immediately.
        if (n.id > 0) {
            // Creates the object described by new NoteRepository(a).setFavorite(n.id, !n.favorite).
            new NoteRepository(a).setFavorite(n.id, !n.favorite);
            // Calls refresh.run with  to perform the operation shown.
            refresh.run();
        // Closes the current method, control-flow, or type block.
        }
    // Closes the current method, control-flow, or type block.
    }

    /** Opens the editor for a new note unless the 50-note limit has been reached. */
    // Creates note.
    public static void createNote(Activity a) {
        // If new NoteRepository(a).isLimitReached() is true, { immediately.
        if (new NoteRepository(a).isLimitReached()) {
            // Calls showLimitDialog with a to perform the operation shown.
            showLimitDialog(a);
            // Returns control to the caller without a value.
            return;
        // Closes the current method, control-flow, or type block.
        }
        // Calls a.startActivity with new Intent(a, NoteEditorActivity.class to perform the operation shown.
        a.startActivity(new Intent(a, NoteEditorActivity.class));
    // Closes the current method, control-flow, or type block.
    }

    // Displays limit dialog.
    public static void showLimitDialog(Activity a) {
        // Creates the object described by new com.google.android.material.dialog.MaterialAlertDialogBuilder(a).
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(a)
                // Calls setTitle on the object from the preceding line with the shown arguments.
                .setTitle(R.string.limit_reached_title)
                // Calls setMessage on the object from the preceding line with the shown arguments.
                .setMessage(a.getString(R.string.limit_reached_message, Constants.MAX_NOTES))
                // Calls setPositiveButton on the object from the preceding line with the shown arguments.
                .setPositiveButton(R.string.ok, null)
                // Calls show on the object from the preceding line with the shown arguments.
                .show();
    // Closes the current method, control-flow, or type block.
    }

    // Confirms delete.
    public static void confirmDelete(Activity a, Note n, Runnable afterDelete) {
        // Calls UiUtils.confirm with a, R.string.delete_note_title, to perform the operation shown.
        UiUtils.confirm(a, R.string.delete_note_title,
                // Starts the callback body that handles the event or asynchronous result.
                a.getString(R.string.delete_note_message, n.title), R.string.delete, () -> {
                    // If n.id < 0 is true, { immediately.
                    if (n.id < 0) {
                        // Creates the object described by new com.mit.notepad.repositories.TodoRepository(a).delete(-n.id).
                        new com.mit.notepad.repositories.TodoRepository(a).delete(-n.id);
                    // Runs the fallback branch when the preceding condition did not match.
                    } else {
                        // Creates the object described by new NoteRepository(a).delete(n.id).
                        new NoteRepository(a).delete(n.id);
                    // Closes the current method, control-flow, or type block.
                    }
                    // Calls UiUtils.toast with a, R.string.note_deleted to perform the operation shown.
                    UiUtils.toast(a, R.string.note_deleted);
                    // Calls afterDelete.run with  to perform the operation shown.
                    afterDelete.run();
                // Executes }) as a statement.
                });
    // Closes the current method, control-flow, or type block.
    }

    // Displays menu.
    public static void showMenu(Activity a, View anchor, Note n, Runnable refresh) {
        // Initializes pm with new PopupMenu(a, anchor) so later statements can use the computed value.
        PopupMenu pm = new PopupMenu(a, anchor);
        // Calls pm.inflate with R.menu.menu_note_item to perform the operation shown.
        pm.inflate(R.menu.menu_note_item);
        // Initializes m with pm.getMenu() so later statements can use the computed value.
        Menu m = pm.getMenu();
        // If n.id < 0 is true, { immediately.
        if (n.id < 0) {
            // Calls m.findItem with R.id.action_pin).setVisible(false to perform the operation shown.
            m.findItem(R.id.action_pin).setVisible(false);
            // Calls m.findItem with R.id.action_favourite).setVisible(false to perform the operation shown.
            m.findItem(R.id.action_favourite).setVisible(false);
            // Calls m.findItem with R.id.action_todo).setVisible(false to perform the operation shown.
            m.findItem(R.id.action_todo).setVisible(false);
        // Runs the fallback branch when the preceding condition did not match.
        } else {
            // Calls m.findItem with R.id.action_pin).setTitle(n.pinned ? R.string.unpin : R.string.pin to perform the operation shown.
            m.findItem(R.id.action_pin).setTitle(n.pinned ? R.string.unpin : R.string.pin);
            // Calls m.findItem with R.id.action_favourite).setTitle(n.favorite ? R.string.remove_favourite : R.string.add_favourite to perform the operation shown.
            m.findItem(R.id.action_favourite).setTitle(n.favorite ? R.string.remove_favourite : R.string.add_favourite);
            // Calls m.findItem with R.id.action_todo).setTitle(n.isTodo() ? R.string.mark_as_note : R.string.mark_as_todo to perform the operation shown.
            m.findItem(R.id.action_todo).setTitle(n.isTodo() ? R.string.mark_as_note : R.string.mark_as_todo);
        // Closes the current method, control-flow, or type block.
        }
        // Initializes repo with new NoteRepository(a) so later statements can use the computed value.
        NoteRepository repo = new NoteRepository(a);
        // Starts the callback body that handles the event or asynchronous result.
        pm.setOnMenuItemClickListener(item -> {
            // Initializes id with item.getItemId() so later statements can use the computed value.
            int id = item.getItemId();
            // If id == R.id.action_open is true, { immediately.
            if (id == R.id.action_open) {
                // Calls open with a, n to perform the operation shown.
                open(a, n);
            // Checks the alternate condition (id == R.id.action_edit) after the earlier branch did not run.
            } else if (id == R.id.action_edit) {
                // Calls edit with a, n to perform the operation shown.
                edit(a, n);
            // Checks the alternate condition (id == R.id.action_pin) after the earlier branch did not run.
            } else if (id == R.id.action_pin) {
                // If n.id > 0 is true, { immediately.
                if (n.id > 0) {
                    // Calls repo.setPinned with n.id, !n.pinned to perform the operation shown.
                    repo.setPinned(n.id, !n.pinned);
                    // Calls refresh.run with  to perform the operation shown.
                    refresh.run();
                // Closes the current method, control-flow, or type block.
                }
            // Checks the alternate condition (id == R.id.action_favourite) after the earlier branch did not run.
            } else if (id == R.id.action_favourite) {
                // If n.id > 0 is true, { immediately.
                if (n.id > 0) {
                    // Calls repo.setFavorite with n.id, !n.favorite to perform the operation shown.
                    repo.setFavorite(n.id, !n.favorite);
                    // Calls refresh.run with  to perform the operation shown.
                    refresh.run();
                // Closes the current method, control-flow, or type block.
                }
            // Checks the alternate condition (id == R.id.action_tags) after the earlier branch did not run.
            } else if (id == R.id.action_tags) {
                // Initializes ids with new ArrayList<>() so later statements can use the computed value.
                List<Long> ids = new ArrayList<>();
                // Repeats the following block according to the initialization, condition, and update in com.mit.notepad.models.Tag t : n.tags) ids.add(t.id.
                for (com.mit.notepad.models.Tag t : n.tags) ids.add(t.id);
                // Starts the callback body that handles the event or asynchronous result.
                TagDialogs.pickTags(a, ids, newIds -> {
                    // If n.id < 0 is true, { immediately.
                    if (n.id < 0) {
                        // Creates the object described by new com.mit.notepad.repositories.TodoRepository(a).setTodoTags(-n.id, newIds).
                        new com.mit.notepad.repositories.TodoRepository(a).setTodoTags(-n.id, newIds);
                    // Runs the fallback branch when the preceding condition did not match.
                    } else {
                        // Calls repo.setNoteTags with n.id, newIds to perform the operation shown.
                        repo.setNoteTags(n.id, newIds);
                    // Closes the current method, control-flow, or type block.
                    }
                    // Calls refresh.run with  to perform the operation shown.
                    refresh.run();
                // Executes }) as a statement.
                });
            // Checks the alternate condition (id == R.id.action_todo) after the earlier branch did not run.
            } else if (id == R.id.action_todo) {
                // If n.id > 0 is true, { immediately.
                if (n.id > 0) {
                    // Calls repo.setNoteType with n.id, n.isTodo() ? Constants.NOTE_TYPE_NOTE : Constants.NOTE_TYPE_TODO to perform the operation shown.
                    repo.setNoteType(n.id, n.isTodo() ? Constants.NOTE_TYPE_NOTE : Constants.NOTE_TYPE_TODO);
                    // Calls refresh.run with  to perform the operation shown.
                    refresh.run();
                // Closes the current method, control-flow, or type block.
                }
            // Checks the alternate condition (id == R.id.action_alarm) after the earlier branch did not run.
            } else if (id == R.id.action_alarm) {
                // Initializes intent with new Intent(a, AlarmActivity.class) so later statements can use the computed value.
                Intent intent = new Intent(a, AlarmActivity.class);
                // If n.id < 0 is true, { immediately.
                if (n.id < 0) {
                    // Calls intent.putExtra with Constants.EXTRA_TODO_ID, -n.id to perform the operation shown.
                    intent.putExtra(Constants.EXTRA_TODO_ID, -n.id);
                // Runs the fallback branch when the preceding condition did not match.
                } else {
                    // Calls intent.putExtra with Constants.EXTRA_NOTE_ID, n.id to perform the operation shown.
                    intent.putExtra(Constants.EXTRA_NOTE_ID, n.id);
                // Closes the current method, control-flow, or type block.
                }
                // Calls a.startActivity with intent to perform the operation shown.
                a.startActivity(intent);
            // Checks the alternate condition (id == R.id.action_delete) after the earlier branch did not run.
            } else if (id == R.id.action_delete) {
                // Calls confirmDelete with a, n, refresh to perform the operation shown.
                confirmDelete(a, n, refresh);
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
