package com.mit.notepad.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.mit.notepad.R;
import com.mit.notepad.activities.MainActivity;
import com.mit.notepad.activities.NoteViewActivity;
import com.mit.notepad.activities.TodoEditorActivity;
import com.mit.notepad.adapters.NotificationAdapter;
import com.mit.notepad.databinding.FragmentNotificationsBinding;
import com.mit.notepad.models.AppNotification;
import com.mit.notepad.repositories.NoteRepository;
import com.mit.notepad.repositories.NotificationRepository;
import com.mit.notepad.repositories.TodoRepository;
import com.mit.notepad.utils.Constants;
import com.mit.notepad.utils.UiUtils;

import java.util.List;

/** Screen 11: in-app notification history (All / To-Do / Alarm). */
public class NotificationsFragment extends Fragment implements NotificationAdapter.Listener {
    private FragmentNotificationsBinding b;
    private NotificationRepository repo;
    private NotificationAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle state) {
        b = FragmentNotificationsBinding.inflate(inflater, container, false);
        repo = new NotificationRepository(requireContext());

        b.header.headerTitle.setText(R.string.title_notifications);
        b.header.headerSubtitle.setText(R.string.subtitle_notifications);
        b.header.headerSearch.setVisibility(View.GONE);
        b.header.headerNav.setOnClickListener(v -> ((MainActivity) requireActivity()).openDrawer());
        b.header.headerMore.setOnClickListener(v -> {
            PopupMenu pm = new PopupMenu(requireContext(), v);
            pm.inflate(R.menu.menu_notifications_more);
            pm.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.action_mark_all_read) {
                    repo.markAllRead();
                    load();
                } else if (item.getItemId() == R.id.action_clear_all) {
                    UiUtils.confirm(requireContext(), R.string.clear_all_title,
                            getString(R.string.clear_all_message), R.string.clear_all, () -> {
                                repo.clearAll();
                                load();
                            });
                }
                return true;
            });
            pm.show();
        });

        adapter = new NotificationAdapter(this);
        b.recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        b.recycler.setAdapter(adapter);
        b.chipGroup.setOnCheckedStateChangeListener((g, ids) -> load());
        return b.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        load();
    }

    private void load() {
        if (b == null) return;
        int checked = b.chipGroup.getCheckedChipId();
        String category = checked == R.id.chip_todo ? Constants.CATEGORY_TODO
                : checked == R.id.chip_alarm ? Constants.CATEGORY_ALARM : null;
        List<AppNotification> list = repo.list(category);
        adapter.setItems(list);
        boolean empty = list.isEmpty();
        b.empty.getRoot().setVisibility(empty ? View.VISIBLE : View.GONE);
        b.recycler.setVisibility(empty ? View.GONE : View.VISIBLE);
        b.empty.emptyTitle.setText(R.string.empty_notifications_title);
        b.empty.emptyMessage.setText(R.string.empty_notifications_message);
    }

    /** Opens the related note/task if it still exists. */
    @Override
    public void onOpen(AppNotification n) {
        repo.markRead(n.id);
        if (n.noteId > 0 && new NoteRepository(requireContext()).get(n.noteId) != null) {
            startActivity(new Intent(requireContext(), NoteViewActivity.class)
                    .putExtra(Constants.EXTRA_NOTE_ID, n.noteId));
        } else if (n.todoId > 0 && new TodoRepository(requireContext()).get(n.todoId) != null) {
            startActivity(new Intent(requireContext(), TodoEditorActivity.class)
                    .putExtra(Constants.EXTRA_TODO_ID, n.todoId));
        } else {
            UiUtils.toast(requireContext(), R.string.item_no_longer_exists);
        }
        load();
    }

    @Override
    public void onLongPress(AppNotification n, View anchor) {
        PopupMenu pm = new PopupMenu(requireContext(), anchor);
        pm.inflate(R.menu.menu_notification_item);
        Menu m = pm.getMenu();
        m.findItem(R.id.action_toggle_read).setTitle(n.read ? R.string.mark_unread : R.string.mark_read);
        pm.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_toggle_read) {
                if (n.read) repo.markUnread(n.id); else repo.markRead(n.id);
            } else if (item.getItemId() == R.id.action_delete) {
                repo.delete(n.id);
            }
            load();
            return true;
        });
        pm.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        b = null;
    }
}
