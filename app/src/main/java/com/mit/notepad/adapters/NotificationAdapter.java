// Sets the package name to com.mit.notepad.adapters so this class belongs to the app module namespace.
package com.mit.notepad.adapters;

// Imports android.content.Context so this file can use that type by its short name.
import android.content.Context;
// Imports android.content.res.ColorStateList so this file can use that type by its short name.
import android.content.res.ColorStateList;
// Imports android.graphics.Typeface so this file can use that type by its short name.
import android.graphics.Typeface;
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
// Imports com.mit.notepad.databinding.ItemNotificationBinding so this file can use that type by its short name.
import com.mit.notepad.databinding.ItemNotificationBinding;
// Imports com.mit.notepad.models.AppNotification so this file can use that type by its short name.
import com.mit.notepad.models.AppNotification;
// Imports com.mit.notepad.utils.Constants so this file can use that type by its short name.
import com.mit.notepad.utils.Constants;
// Imports com.mit.notepad.utils.DateTimeUtils so this file can use that type by its short name.
import com.mit.notepad.utils.DateTimeUtils;

// Imports java.util.ArrayList so this file can use that type by its short name.
import java.util.ArrayList;
// Imports java.util.List so this file can use that type by its short name.
import java.util.List;

// Declares class NotificationAdapter to group related state and behavior.
public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.Holder> {

    // Declares interface Listener to group related state and behavior.
    public interface Listener {
        // Calls onOpen with AppNotification n to perform the operation shown.
        void onOpen(AppNotification n);

        // Calls onLongPress with AppNotification n, View anchor to perform the operation shown.
        void onLongPress(AppNotification n, View anchor);
    // Closes the current method, control-flow, or type block.
    }

    // Initializes items with new ArrayList<>() so later statements can use the computed value.
    private final List<AppNotification> items = new ArrayList<>();
    // Declares listener for use by the surrounding method or class.
    private final Listener listener;

    // Declares NotificationAdapter() to implement this part of the component.
    public NotificationAdapter(Listener listener) {
        // Stores the result of listener in this.listener for use by later logic.
        this.listener = listener;
    // Closes the current method, control-flow, or type block.
    }

    // Updates items.
    public void setItems(List<AppNotification> list) {
        // Calls items.clear with  to perform the operation shown.
        items.clear();
        // Calls items.addAll with list to perform the operation shown.
        items.addAll(list);
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
        // Returns new Holder(ItemNotificationBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false)) to the caller.
        return new Holder(ItemNotificationBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    // Closes the current method, control-flow, or type block.
    }

    // Applies @Override metadata to the declaration that follows.
    @Override
    // Fills the RecyclerView row with the item at the requested position.
    public void onBindViewHolder(@NonNull Holder h, int position) {
        // Initializes n with items.get(position) so later statements can use the computed value.
        AppNotification n = items.get(position);
        // Initializes b with h.b so later statements can use the computed value.
        ItemNotificationBinding b = h.b;
        // Initializes ctx with b.getRoot().getContext() so later statements can use the computed value.
        Context ctx = b.getRoot().getContext();

        // Declares color for use by the surrounding method or class.
        int color;
        // If Constants.CATEGORY_ALARM.equals(n.category) is true, color = ctx.getColor(R.color.np_alarm_blue) immediately.
        if (Constants.CATEGORY_ALARM.equals(n.category)) color = ctx.getColor(R.color.np_alarm_blue);
        // Checks the alternate condition (Constants.CATEGORY_NOTE.equals(n.category) after the earlier branch did not run.
        else if (Constants.CATEGORY_NOTE.equals(n.category)) color = ctx.getColor(R.color.np_note_amber);
        // Runs the fallback branch when the preceding condition did not match.
        else color = ctx.getColor(R.color.np_primary);
        // Calls b.ivIcon.setImageTintList with ColorStateList.valueOf(color to perform the operation shown.
        b.ivIcon.setImageTintList(ColorStateList.valueOf(color));
        // Calls b.iconTile.setBackgroundTintList with ColorStateList.valueOf( to perform the operation shown.
        b.iconTile.setBackgroundTintList(ColorStateList.valueOf(
                // Executes (color & 0x00FFFFFF) | 0x2E000000)) as a statement.
                (color & 0x00FFFFFF) | 0x2E000000));

        // Calls b.tvTitle.setText with n.title to perform the operation shown.
        b.tvTitle.setText(n.title);
        // Calls b.tvTitle.setTypeface with null, n.read ? Typeface.NORMAL : Typeface.BOLD to perform the operation shown.
        b.tvTitle.setTypeface(null, n.read ? Typeface.NORMAL : Typeface.BOLD);
        // Calls b.tvMessage.setText with n.message to perform the operation shown.
        b.tvMessage.setText(n.message);
        // Calls b.tvFullTime.setText with DateTimeUtils.formatDateTime(n.timestamp to perform the operation shown.
        b.tvFullTime.setText(DateTimeUtils.formatDateTime(n.timestamp));
        // Calls b.tvTime.setText with DateTimeUtils.listStamp(ctx, n.timestamp to perform the operation shown.
        b.tvTime.setText(DateTimeUtils.listStamp(ctx, n.timestamp));
        // Calls b.viewUnread.setVisibility with n.read ? View.INVISIBLE : View.VISIBLE to perform the operation shown.
        b.viewUnread.setVisibility(n.read ? View.INVISIBLE : View.VISIBLE);
        // Calls b.getRoot with ).setAlpha(n.read ? 0.8f : 1f to perform the operation shown.
        b.getRoot().setAlpha(n.read ? 0.8f : 1f);

        // Calls b.getRoot with ).setOnClickListener(v -> listener.onOpen(n to perform the operation shown.
        b.getRoot().setOnClickListener(v -> listener.onOpen(n));
        // Starts the callback body that handles the event or asynchronous result.
        b.getRoot().setOnLongClickListener(v -> {
            // Calls listener.onLongPress with n, v to perform the operation shown.
            listener.onLongPress(n, v);
            // Returns true to the caller.
            return true;
        // Executes }) as a statement.
        });
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
        final ItemNotificationBinding b;

        // Calls Holder with ItemNotificationBinding b) { to perform the operation shown.
        Holder(ItemNotificationBinding b) {
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
