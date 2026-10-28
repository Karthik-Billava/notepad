// Sets the package name to com.mit.notepad.adapters so this class belongs to the app module namespace.
package com.mit.notepad.adapters;

// Imports android.content.Context so this file can use that type by its short name.
import android.content.Context;
// Imports android.content.res.ColorStateList so this file can use that type by its short name.
import android.content.res.ColorStateList;
// Imports android.view.LayoutInflater so this file can use that type by its short name.
import android.view.LayoutInflater;
// Imports android.view.View so this file can use that type by its short name.
import android.view.View;
// Imports android.view.ViewGroup so this file can use that type by its short name.
import android.view.ViewGroup;

// Imports androidx.annotation.NonNull so this file can use that type by its short name.
import androidx.annotation.NonNull;
// Imports androidx.recyclerview.widget.RecyclerView so this file can use that type by its short name.
import androidx.recyclerview.widget.RecyclerView;

// Imports com.mit.notepad.R so this file can use that type by its short name.
import com.mit.notepad.R;
// Imports com.mit.notepad.databinding.ItemAlarmBinding so this file can use that type by its short name.
import com.mit.notepad.databinding.ItemAlarmBinding;
// Imports com.mit.notepad.models.Alarm so this file can use that type by its short name.
import com.mit.notepad.models.Alarm;
// Imports com.mit.notepad.utils.DateTimeUtils so this file can use that type by its short name.
import com.mit.notepad.utils.DateTimeUtils;
// Imports com.mit.notepad.utils.RepeatLabels so this file can use that type by its short name.
import com.mit.notepad.utils.RepeatLabels;

// Imports java.util.ArrayList so this file can use that type by its short name.
import java.util.ArrayList;
// Imports java.util.List so this file can use that type by its short name.
import java.util.List;

// Declares class AlarmAdapter to group related state and behavior.
public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.Holder> {

    // Declares interface Listener to group related state and behavior.
    public interface Listener {
        // Calls onToggle with Alarm alarm, boolean enabled to perform the operation shown.
        void onToggle(Alarm alarm, boolean enabled);

        // Calls onMenu with Alarm alarm, View anchor to perform the operation shown.
        void onMenu(Alarm alarm, View anchor);
    // Closes the current method, control-flow, or type block.
    }

    // Initializes items with new ArrayList<>() so later statements can use the computed value.
    private final List<Alarm> items = new ArrayList<>();
    // Declares listener for use by the surrounding method or class.
    private final Listener listener;

    // Declares AlarmAdapter() to implement this part of the component.
    public AlarmAdapter(Listener listener) {
        // Stores the result of listener in this.listener for use by later logic.
        this.listener = listener;
    // Closes the current method, control-flow, or type block.
    }

    // Updates items.
    public void setItems(List<Alarm> alarms) {
        // Calls items.clear with  to perform the operation shown.
        items.clear();
        // Calls items.addAll with alarms to perform the operation shown.
        items.addAll(alarms);
        // Calls notifyDataSetChanged with  to perform the operation shown.
        notifyDataSetChanged();
    // Closes the current method, control-flow, or type block.
    }

    // Applies @NonNull metadata to the declaration that follows.
    @NonNull
    // Applies @Override metadata to the declaration that follows.
    @Override
    // Inflates and wraps a RecyclerView row layout.
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Returns new Holder(ItemAlarmBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false)) to the caller.
        return new Holder(ItemAlarmBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    // Closes the current method, control-flow, or type block.
    }

    // Applies @Override metadata to the declaration that follows.
    @Override
    // Fills the RecyclerView row with the item at the requested position.
    public void onBindViewHolder(@NonNull Holder h, int position) {
        // Initializes a with items.get(position) so later statements can use the computed value.
        Alarm a = items.get(position);
        // Initializes b with h.b so later statements can use the computed value.
        ItemAlarmBinding b = h.b;
        // Initializes ctx with b.getRoot().getContext() so later statements can use the computed value.
        Context ctx = b.getRoot().getContext();
        // Calls b.tvTitle.setText with a.ownerTitle to perform the operation shown.
        b.tvTitle.setText(a.ownerTitle);
        // Calls b.tvTime.setText with DateTimeUtils.formatDateTime(a.scheduledAt to perform the operation shown.
        b.tvTime.setText(DateTimeUtils.formatDateTime(a.scheduledAt));
        // Calls b.tvRepeat.setText with RepeatLabels.label(ctx, a.repeatType, a.repeatInterval to perform the operation shown.
        b.tvRepeat.setText(RepeatLabels.label(ctx, a.repeatType, a.repeatInterval));
        // Initializes color with ctx.getColor(a.enabled ? R.color.np_primary : R.color.np_text_secondary) so later statements can use the computed value.
        int color = ctx.getColor(a.enabled ? R.color.np_primary : R.color.np_text_secondary);
        // Calls b.ivIcon.setImageTintList with ColorStateList.valueOf(color to perform the operation shown.
        b.ivIcon.setImageTintList(ColorStateList.valueOf(color));
        // Calls b.iconTile.setBackgroundTintList with ColorStateList.valueOf( to perform the operation shown.
        b.iconTile.setBackgroundTintList(ColorStateList.valueOf(
                // Calls ctx.getColor with a.enabled ? R.color.np_primary_light : R.color.np_surface_variant to perform the operation shown.
                ctx.getColor(a.enabled ? R.color.np_primary_light : R.color.np_surface_variant)));
        // Calls b.swEnabled.setOnCheckedChangeListener with null to perform the operation shown.
        b.swEnabled.setOnCheckedChangeListener(null);
        // Calls b.swEnabled.setChecked with a.enabled to perform the operation shown.
        b.swEnabled.setChecked(a.enabled);
        // Calls b.swEnabled.setOnCheckedChangeListener with (v, checked) -> listener.onToggle(a, checked to perform the operation shown.
        b.swEnabled.setOnCheckedChangeListener((v, checked) -> listener.onToggle(a, checked));
        // Calls b.btnMenu.setOnClickListener with v -> listener.onMenu(a, v to perform the operation shown.
        b.btnMenu.setOnClickListener(v -> listener.onMenu(a, v));
    // Closes the current method, control-flow, or type block.
    }

    // Applies @Override metadata to the declaration that follows.
    @Override
    // Returns the requested item count.
    public int getItemCount() {
        // Returns items.size() to the caller.
        return items.size();
    // Closes the current method, control-flow, or type block.
    }

    // Declares class Holder to group related state and behavior.
    static class Holder extends RecyclerView.ViewHolder {
        // Declares b for use by the surrounding method or class.
        final ItemAlarmBinding b;

        // Calls Holder with ItemAlarmBinding b) { to perform the operation shown.
        Holder(ItemAlarmBinding b) {
            // Initializes the parent class by passing b.getRoot()) to its constructor.
            super(b.getRoot());
            // Stores the result of b in this.b for use by later logic.
            this.b = b;
        // Closes the current method, control-flow, or type block.
        }
    // Closes the current method, control-flow, or type block.
    }
// Closes the current method, control-flow, or type block.
}
