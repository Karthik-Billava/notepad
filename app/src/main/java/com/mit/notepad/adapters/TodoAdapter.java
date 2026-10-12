package com.mit.notepad.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mit.notepad.R;
import com.mit.notepad.databinding.ItemTodoBinding;
import com.mit.notepad.models.Tag;
import com.mit.notepad.models.Todo;
import com.mit.notepad.utils.DateTimeUtils;
import com.mit.notepad.utils.UiUtils;

import java.util.ArrayList;
import java.util.List;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.Holder> {

    public interface Listener {
        void onToggle(Todo todo, boolean completed);

        void onOpen(Todo todo);

        void onMenu(Todo todo, View anchor);
    }

    private final List<Todo> items = new ArrayList<>();
    private final Listener listener;

    public TodoAdapter(Listener listener) {
        this.listener = listener;
    }

    public void setItems(List<Todo> todos) {
        items.clear();
        items.addAll(todos);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(ItemTodoBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder h, int position) {
        Todo t = items.get(position);
        ItemTodoBinding b = h.b;
        Context ctx = b.getRoot().getContext();

        b.tvTitle.setText(t.title);
        b.tvTitle.setPaintFlags(t.completed ? b.tvTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG
                : b.tvTitle.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
        b.tvTitle.setAlpha(t.completed ? 0.55f : 1f);
        b.tvDesc.setText(t.description);
        b.tvDesc.setVisibility(t.description == null || t.description.isEmpty() ? View.GONE : View.VISIBLE);

        if (t.dueAt > 0) {
            b.tvDue.setText(ctx.getString(R.string.due_format,
                    DateTimeUtils.formatDate(t.dueAt), DateTimeUtils.formatTime(t.dueAt)));
        } else {
            b.tvDue.setText(R.string.no_due_date);
        }
        boolean overdue = t.isOverdue();
        b.tvDue.setTextColor(ctx.getColor(overdue ? R.color.np_danger : R.color.np_text_secondary));
        b.tvOverdue.setVisibility(overdue ? View.VISIBLE : View.GONE);

        boolean reminderOn = t.reminderEnabled && !t.completed;
        b.ivBell.setImageTintList(ColorStateList.valueOf(ctx.getColor(
                reminderOn ? R.color.np_primary : R.color.np_divider)));
        b.ivBell.setContentDescription(ctx.getString(reminderOn ? R.string.reminder_on : R.string.reminder_off));

        b.llTags.removeAllViews();
        int shown = 0;
        for (Tag tag : t.tags) {
            if (shown++ == 2) break;
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMarginEnd(UiUtils.dp(ctx, 4));
            b.llTags.addView(UiUtils.tagPill(ctx, tag), lp);
        }

        b.cbDone.setOnCheckedChangeListener(null);
        b.cbDone.setChecked(t.completed);
        b.cbDone.setOnCheckedChangeListener((v, checked) -> b.getRoot().post(() -> listener.onToggle(t, checked)));
        b.getRoot().setOnClickListener(v -> listener.onOpen(t));
        b.btnMenu.setOnClickListener(v -> listener.onMenu(t, v));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class Holder extends RecyclerView.ViewHolder {
        final ItemTodoBinding b;

        Holder(ItemTodoBinding b) {
            super(b.getRoot());
            this.b = b;
        }
    }
}
