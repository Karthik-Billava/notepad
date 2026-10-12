package com.mit.notepad.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mit.notepad.R;
import com.mit.notepad.databinding.ItemNotificationBinding;
import com.mit.notepad.models.AppNotification;
import com.mit.notepad.utils.Constants;
import com.mit.notepad.utils.DateTimeUtils;

import java.util.ArrayList;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.Holder> {

    public interface Listener {
        void onOpen(AppNotification n);

        void onLongPress(AppNotification n, View anchor);
    }

    private final List<AppNotification> items = new ArrayList<>();
    private final Listener listener;

    public NotificationAdapter(Listener listener) {
        this.listener = listener;
    }

    public void setItems(List<AppNotification> list) {
        items.clear();
        items.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(ItemNotificationBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder h, int position) {
        AppNotification n = items.get(position);
        ItemNotificationBinding b = h.b;
        Context ctx = b.getRoot().getContext();

        int color;
        if (Constants.CATEGORY_ALARM.equals(n.category)) color = ctx.getColor(R.color.np_alarm_blue);
        else if (Constants.CATEGORY_NOTE.equals(n.category)) color = ctx.getColor(R.color.np_note_amber);
        else color = ctx.getColor(R.color.np_primary);
        b.ivIcon.setImageTintList(ColorStateList.valueOf(color));
        b.iconTile.setBackgroundTintList(ColorStateList.valueOf(
                (color & 0x00FFFFFF) | 0x2E000000));

        b.tvTitle.setText(n.title);
        b.tvTitle.setTypeface(null, n.read ? Typeface.NORMAL : Typeface.BOLD);
        b.tvMessage.setText(n.message);
        b.tvFullTime.setText(DateTimeUtils.formatDateTime(n.timestamp));
        b.tvTime.setText(DateTimeUtils.listStamp(ctx, n.timestamp));
        b.viewUnread.setVisibility(n.read ? View.INVISIBLE : View.VISIBLE);
        b.getRoot().setAlpha(n.read ? 0.8f : 1f);

        b.getRoot().setOnClickListener(v -> listener.onOpen(n));
        b.getRoot().setOnLongClickListener(v -> {
            listener.onLongPress(n, v);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class Holder extends RecyclerView.ViewHolder {
        final ItemNotificationBinding b;

        Holder(ItemNotificationBinding b) {
            super(b.getRoot());
            this.b = b;
        }
    }
}
