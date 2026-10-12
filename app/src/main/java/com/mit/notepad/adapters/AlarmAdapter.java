package com.mit.notepad.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mit.notepad.R;
import com.mit.notepad.databinding.ItemAlarmBinding;
import com.mit.notepad.models.Alarm;
import com.mit.notepad.utils.DateTimeUtils;
import com.mit.notepad.utils.RepeatLabels;

import java.util.ArrayList;
import java.util.List;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.Holder> {

    public interface Listener {
        void onToggle(Alarm alarm, boolean enabled);

        void onMenu(Alarm alarm, View anchor);
    }

    private final List<Alarm> items = new ArrayList<>();
    private final Listener listener;

    public AlarmAdapter(Listener listener) {
        this.listener = listener;
    }

    public void setItems(List<Alarm> alarms) {
        items.clear();
        items.addAll(alarms);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(ItemAlarmBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder h, int position) {
        Alarm a = items.get(position);
        ItemAlarmBinding b = h.b;
        Context ctx = b.getRoot().getContext();
        b.tvTitle.setText(a.ownerTitle);
        b.tvTime.setText(DateTimeUtils.formatDateTime(a.scheduledAt));
        b.tvRepeat.setText(RepeatLabels.label(ctx, a.repeatType, a.repeatInterval));
        int color = ctx.getColor(a.enabled ? R.color.np_primary : R.color.np_text_secondary);
        b.ivIcon.setImageTintList(ColorStateList.valueOf(color));
        b.iconTile.setBackgroundTintList(ColorStateList.valueOf(
                ctx.getColor(a.enabled ? R.color.np_primary_light : R.color.np_surface_variant)));
        b.swEnabled.setOnCheckedChangeListener(null);
        b.swEnabled.setChecked(a.enabled);
        b.swEnabled.setOnCheckedChangeListener((v, checked) -> listener.onToggle(a, checked));
        b.btnMenu.setOnClickListener(v -> listener.onMenu(a, v));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class Holder extends RecyclerView.ViewHolder {
        final ItemAlarmBinding b;

        Holder(ItemAlarmBinding b) {
            super(b.getRoot());
            this.b = b;
        }
    }
}
