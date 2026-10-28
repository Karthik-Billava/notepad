// Sets the package name to com.mit.notepad.fragments so this class belongs to the app module namespace.
package com.mit.notepad.fragments;

// Imports android.content.Intent so this file can use that type by its short name.
import android.content.Intent;
// Imports android.os.Bundle so this file can use that type by its short name.
import android.os.Bundle;
// Imports android.view.LayoutInflater so this file can use that type by its short name.
import android.view.LayoutInflater;
// Imports android.view.Menu so this file can use that type by its short name.
import android.view.Menu;
// Imports android.view.View so this file can use that type by its short name.
import android.view.View;
// Imports android.view.ViewGroup so this file can use that type by its short name.
import android.view.ViewGroup;
// Imports android.widget.PopupMenu so this file can use that type by its short name.
import android.widget.PopupMenu;

// Imports androidx.annotation.NonNull so this file can use that type by its short name.
import androidx.annotation.NonNull;
// Imports androidx.annotation.Nullable so this file can use that type by its short name.
import androidx.annotation.Nullable;
// Imports androidx.fragment.app.Fragment so this file can use that type by its short name.
import androidx.fragment.app.Fragment;
// Imports androidx.recyclerview.widget.LinearLayoutManager so this file can use that type by its short name.
import androidx.recyclerview.widget.LinearLayoutManager;

// Imports com.mit.notepad.R so this file can use that type by its short name.
import com.mit.notepad.R;
// Imports com.mit.notepad.activities.MainActivity so this file can use that type by its short name.
import com.mit.notepad.activities.MainActivity;
// Imports com.mit.notepad.activities.NoteViewActivity so this file can use that type by its short name.
import com.mit.notepad.activities.NoteViewActivity;
// Imports com.mit.notepad.activities.TodoEditorActivity so this file can use that type by its short name.
import com.mit.notepad.activities.TodoEditorActivity;
// Imports com.mit.notepad.adapters.NotificationAdapter so this file can use that type by its short name.
import com.mit.notepad.adapters.NotificationAdapter;
// Imports com.mit.notepad.databinding.FragmentNotificationsBinding so this file can use that type by its short name.
import com.mit.notepad.databinding.FragmentNotificationsBinding;
// Imports com.mit.notepad.models.AppNotification so this file can use that type by its short name.
import com.mit.notepad.models.AppNotification;
// Imports com.mit.notepad.repositories.NoteRepository so this file can use that type by its short name.
import com.mit.notepad.repositories.NoteRepository;
// Imports com.mit.notepad.repositories.NotificationRepository so this file can use that type by its short name.
import com.mit.notepad.repositories.NotificationRepository;
// Imports com.mit.notepad.repositories.TodoRepository so this file can use that type by its short name.
import com.mit.notepad.repositories.TodoRepository;
// Imports com.mit.notepad.utils.Constants so this file can use that type by its short name.
import com.mit.notepad.utils.Constants;
// Imports com.mit.notepad.utils.UiUtils so this file can use that type by its short name.
import com.mit.notepad.utils.UiUtils;

// Imports java.util.List so this file can use that type by its short name.
import java.util.List;

/** Screen 11: in-app notification history (All / To-Do / Alarm). */
// Declares class NotificationsFragment to group related state and behavior.
public class NotificationsFragment extends Fragment implements NotificationAdapter.Listener {
    // Declares b for use by the surrounding method or class.
    private FragmentNotificationsBinding b;
    // Declares repo for use by the surrounding method or class.
    private NotificationRepository repo;
    // Declares adapter for use by the surrounding method or class.
    private NotificationAdapter adapter;

    // Applies @Nullable metadata to the declaration that follows.
    @Nullable
    // Applies @Override metadata to the declaration that follows.
    @Override
    // Builds and returns the fragment view hierarchy.
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle state) {
        // Stores the result of FragmentNotificationsBinding.inflate(inflater, container, false) in b for use by later logic.
        b = FragmentNotificationsBinding.inflate(inflater, container, false);
        // Stores the result of new NotificationRepository(requireContext()) in repo for use by later logic.
        repo = new NotificationRepository(requireContext());

        // Calls b.header.headerTitle.setText with R.string.title_notifications to perform the operation shown.
        b.header.headerTitle.setText(R.string.title_notifications);
        // Calls b.header.headerSubtitle.setText with R.string.subtitle_notifications to perform the operation shown.
        b.header.headerSubtitle.setText(R.string.subtitle_notifications);
        // Calls b.header.headerSearch.setVisibility with View.GONE to perform the operation shown.
        b.header.headerSearch.setVisibility(View.GONE);
        // Calls b.header.headerNav.setOnClickListener with v -> ((MainActivity) requireActivity()).openDrawer( to perform the operation shown.
        b.header.headerNav.setOnClickListener(v -> ((MainActivity) requireActivity()).openDrawer());
        // Starts the callback body that handles the event or asynchronous result.
        b.header.headerMore.setOnClickListener(v -> {
            // Initializes pm with new PopupMenu(requireContext(), v) so later statements can use the computed value.
            PopupMenu pm = new PopupMenu(requireContext(), v);
            // Calls pm.inflate with R.menu.menu_notifications_more to perform the operation shown.
            pm.inflate(R.menu.menu_notifications_more);
            // Starts the callback body that handles the event or asynchronous result.
            pm.setOnMenuItemClickListener(item -> {
                // If item.getItemId() == R.id.action_mark_all_read is true, { immediately.
                if (item.getItemId() == R.id.action_mark_all_read) {
                    // Calls repo.markAllRead with  to perform the operation shown.
                    repo.markAllRead();
                    // Calls load with  to perform the operation shown.
                    load();
                // Checks the alternate condition (item.getItemId() after the earlier branch did not run.
                } else if (item.getItemId() == R.id.action_clear_all) {
                    // Calls UiUtils.confirm with requireContext(), R.string.clear_all_title, to perform the operation shown.
                    UiUtils.confirm(requireContext(), R.string.clear_all_title,
                            // Starts the callback body that handles the event or asynchronous result.
                            getString(R.string.clear_all_message), R.string.clear_all, () -> {
                                // Calls repo.clearAll with  to perform the operation shown.
                                repo.clearAll();
                                // Calls load with  to perform the operation shown.
                                load();
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
        // Executes }) as a statement.
        });

        // Stores the result of new NotificationAdapter(this) in adapter for use by later logic.
        adapter = new NotificationAdapter(this);
        // Calls b.recycler.setLayoutManager with new LinearLayoutManager(requireContext( to perform the operation shown.
        b.recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        // Calls b.recycler.setAdapter with adapter to perform the operation shown.
        b.recycler.setAdapter(adapter);
        // Calls b.chipGroup.setOnCheckedStateChangeListener with (g, ids) -> load( to perform the operation shown.
        b.chipGroup.setOnCheckedStateChangeListener((g, ids) -> load());
        // Returns b.getRoot() to the caller.
        return b.getRoot();
    // Closes the current method, control-flow, or type block.
    }

    // Applies @Override metadata to the declaration that follows.
    @Override
    // Refreshes visible data when this component becomes active.
    public void onResume() {
        // Calls super.onResume with  to perform the operation shown.
        super.onResume();
        // Calls load with  to perform the operation shown.
        load();
    // Closes the current method, control-flow, or type block.
    }

    // Loads the latest records and refreshes the visible list.
    private void load() {
        // If b == null is true, return immediately.
        if (b == null) return;
        // Initializes checked with b.chipGroup.getCheckedChipId() so later statements can use the computed value.
        int checked = b.chipGroup.getCheckedChipId();
        // Initializes category with checked == R.id.chip_todo ? Constants.CATEGORY_TODO so later statements can use the computed value.
        String category = checked == R.id.chip_todo ? Constants.CATEGORY_TODO
                // Executes : checked == R.id.chip_alarm ? Constants.CATEGORY_ALARM : null as a statement.
                : checked == R.id.chip_alarm ? Constants.CATEGORY_ALARM : null;
        // Initializes list with repo.list(category) so later statements can use the computed value.
        List<AppNotification> list = repo.list(category);
        // Calls adapter.setItems with list to perform the operation shown.
        adapter.setItems(list);
        // Initializes empty with list.isEmpty() so later statements can use the computed value.
        boolean empty = list.isEmpty();
        // Calls b.empty.getRoot with ).setVisibility(empty ? View.VISIBLE : View.GONE to perform the operation shown.
        b.empty.getRoot().setVisibility(empty ? View.VISIBLE : View.GONE);
        // Calls b.recycler.setVisibility with empty ? View.GONE : View.VISIBLE to perform the operation shown.
        b.recycler.setVisibility(empty ? View.GONE : View.VISIBLE);
        // Calls b.empty.emptyTitle.setText with R.string.empty_notifications_title to perform the operation shown.
        b.empty.emptyTitle.setText(R.string.empty_notifications_title);
        // Calls b.empty.emptyMessage.setText with R.string.empty_notifications_message to perform the operation shown.
        b.empty.emptyMessage.setText(R.string.empty_notifications_message);
    // Closes the current method, control-flow, or type block.
    }

    /** Opens the related note/task if it still exists. */
    // Applies @Override metadata to the declaration that follows.
    @Override
    // Declares onOpen() to implement this part of the component.
    public void onOpen(AppNotification n) {
        // Calls repo.markRead with n.id to perform the operation shown.
        repo.markRead(n.id);
        // If n.noteId > 0 && new NoteRepository(requireContext()).get(n.noteId) != null is true, { immediately.
        if (n.noteId > 0 && new NoteRepository(requireContext()).get(n.noteId) != null) {
            // Calls startActivity with new Intent(requireContext(), NoteViewActivity.class to perform the operation shown.
            startActivity(new Intent(requireContext(), NoteViewActivity.class)
                    // Calls putExtra on the object from the preceding line with the shown arguments.
                    .putExtra(Constants.EXTRA_NOTE_ID, n.noteId));
        // Checks the alternate condition (n.todoId > 0 && new TodoRepository(requireContext() after the earlier branch did not run.
        } else if (n.todoId > 0 && new TodoRepository(requireContext()).get(n.todoId) != null) {
            // Calls startActivity with new Intent(requireContext(), TodoEditorActivity.class to perform the operation shown.
            startActivity(new Intent(requireContext(), TodoEditorActivity.class)
                    // Calls putExtra on the object from the preceding line with the shown arguments.
                    .putExtra(Constants.EXTRA_TODO_ID, n.todoId));
        // Runs the fallback branch when the preceding condition did not match.
        } else {
            // Calls UiUtils.toast with requireContext(), R.string.item_no_longer_exists to perform the operation shown.
            UiUtils.toast(requireContext(), R.string.item_no_longer_exists);
        // Closes the current method, control-flow, or type block.
        }
        // Calls load with  to perform the operation shown.
        load();
    // Closes the current method, control-flow, or type block.
    }

    // Applies @Override metadata to the declaration that follows.
    @Override
    // Declares onLongPress() to implement this part of the component.
    public void onLongPress(AppNotification n, View anchor) {
        // Initializes pm with new PopupMenu(requireContext(), anchor) so later statements can use the computed value.
        PopupMenu pm = new PopupMenu(requireContext(), anchor);
        // Calls pm.inflate with R.menu.menu_notification_item to perform the operation shown.
        pm.inflate(R.menu.menu_notification_item);
        // Initializes m with pm.getMenu() so later statements can use the computed value.
        Menu m = pm.getMenu();
        // Calls m.findItem with R.id.action_toggle_read).setTitle(n.read ? R.string.mark_unread : R.string.mark_read to perform the operation shown.
        m.findItem(R.id.action_toggle_read).setTitle(n.read ? R.string.mark_unread : R.string.mark_read);
        // Starts the callback body that handles the event or asynchronous result.
        pm.setOnMenuItemClickListener(item -> {
            // If item.getItemId() == R.id.action_toggle_read is true, { immediately.
            if (item.getItemId() == R.id.action_toggle_read) {
                // If n.read is true, repo.markUnread(n.id); else repo.markRead(n.id) immediately.
                if (n.read) repo.markUnread(n.id); else repo.markRead(n.id);
            // Checks the alternate condition (item.getItemId() after the earlier branch did not run.
            } else if (item.getItemId() == R.id.action_delete) {
                // Calls repo.delete with n.id to perform the operation shown.
                repo.delete(n.id);
            // Closes the current method, control-flow, or type block.
            }
            // Calls load with  to perform the operation shown.
            load();
            // Returns true to the caller.
            return true;
        // Executes }) as a statement.
        });
        // Calls pm.show with  to perform the operation shown.
        pm.show();
    // Closes the current method, control-flow, or type block.
    }

    // Applies @Override metadata to the declaration that follows.
    @Override
    // Releases references tied to the fragment view.
    public void onDestroyView() {
        // Calls super.onDestroyView with  to perform the operation shown.
        super.onDestroyView();
        // Stores the result of null in b for use by later logic.
        b = null;
    // Closes the current method, control-flow, or type block.
    }
// Closes the current method, control-flow, or type block.
}
