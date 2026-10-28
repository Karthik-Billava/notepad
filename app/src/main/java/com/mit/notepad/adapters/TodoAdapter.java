// Sets the package name to com.mit.notepad.adapters so this class belongs to the app module namespace.
package com.mit.notepad.adapters;

// Imports android.content.Context so this file can use that type by its short name.
import android.content.Context;
// Imports android.content.res.ColorStateList so this file can use that type by its short name.
import android.content.res.ColorStateList;
// Imports android.graphics.Paint so this file can use that type by its short name.
import android.graphics.Paint;
// Imports android.view.LayoutInflater so this file can use that type by its short name.
import android.view.LayoutInflater;
// Imports android.view.View so this file can use that type by its short name.
import android.view.View;
// Imports android.view.ViewGroup so this file can use that type by its short name.
import android.view.ViewGroup;
// Imports android.widget.LinearLayout so this file can use that type by its short name.
import android.widget.LinearLayout;

// Imports androidx.annotation.NonNull so this file can use that type by its short name.
import androidx.annotation.NonNull;
// Imports androidx.recyclerview.widget.RecyclerView so this file can use that type by its short name.
import androidx.recyclerview.widget.RecyclerView;

// Imports com.mit.notepad.R so this file can use that type by its short name.
import com.mit.notepad.R;
// Imports com.mit.notepad.databinding.ItemTodoBinding so this file can use that type by its short name.
import com.mit.notepad.databinding.ItemTodoBinding;
// Imports com.mit.notepad.models.Tag so this file can use that type by its short name.
import com.mit.notepad.models.Tag;
// Imports com.mit.notepad.models.Todo so this file can use that type by its short name.
import com.mit.notepad.models.Todo;
// Imports com.mit.notepad.utils.DateTimeUtils so this file can use that type by its short name.
import com.mit.notepad.utils.DateTimeUtils;
// Imports com.mit.notepad.utils.UiUtils so this file can use that type by its short name.
import com.mit.notepad.utils.UiUtils;

// Imports java.util.ArrayList so this file can use that type by its short name.
import java.util.ArrayList;
// Imports java.util.List so this file can use that type by its short name.
import java.util.List;

// Declares class TodoAdapter to group related state and behavior.
public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.Holder> {

    // Declares interface Listener to group related state and behavior.
    public interface Listener {
        // Calls onToggle with Todo todo, boolean completed to perform the operation shown.
        void onToggle(Todo todo, boolean completed);

        // Calls onOpen with Todo todo to perform the operation shown.
        void onOpen(Todo todo);

        // Calls onMenu with Todo todo, View anchor to perform the operation shown.
        void onMenu(Todo todo, View anchor);
    // Closes the current method, control-flow, or type block.
    }

    // Initializes items with new ArrayList<>() so later statements can use the computed value.
    private final List<Todo> items = new ArrayList<>();
    // Declares listener for use by the surrounding method or class.
    private final Listener listener;

    // Declares TodoAdapter() to implement this part of the component.
    public TodoAdapter(Listener listener) {
        // Stores the result of listener in this.listener for use by later logic.
        this.listener = listener;
    // Closes the current method, control-flow, or type block.
    }

    // Updates items.
    public void setItems(List<Todo> todos) {
        // Calls items.clear with  to perform the operation shown.
        items.clear();
        // Calls items.addAll with todos to perform the operation shown.
        items.addAll(todos);
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
        // Returns new Holder(ItemTodoBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false)) to the caller.
        return new Holder(ItemTodoBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    // Closes the current method, control-flow, or type block.
    }

    // Applies @Override metadata to the declaration that follows.
    @Override
    // Fills the RecyclerView row with the item at the requested position.
    public void onBindViewHolder(@NonNull Holder h, int position) {
        // Initializes t with items.get(position) so later statements can use the computed value.
        Todo t = items.get(position);
        // Initializes b with h.b so later statements can use the computed value.
        ItemTodoBinding b = h.b;
        // Initializes ctx with b.getRoot().getContext() so later statements can use the computed value.
        Context ctx = b.getRoot().getContext();

        // Calls b.tvTitle.setText with t.title to perform the operation shown.
        b.tvTitle.setText(t.title);
        // Calls b.tvTitle.setPaintFlags with t.completed ? b.tvTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG to perform the operation shown.
        b.tvTitle.setPaintFlags(t.completed ? b.tvTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG
                // Calls b.tvTitle.getPaintFlags with ) & ~Paint.STRIKE_THRU_TEXT_FLAG to perform the operation shown.
                : b.tvTitle.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
        // Calls b.tvTitle.setAlpha with t.completed ? 0.55f : 1f to perform the operation shown.
        b.tvTitle.setAlpha(t.completed ? 0.55f : 1f);
        // Calls b.tvDesc.setText with t.description to perform the operation shown.
        b.tvDesc.setText(t.description);
        // Calls b.tvDesc.setVisibility with t.description == null || t.description.isEmpty() ? View.GONE : View.VISIBLE to perform the operation shown.
        b.tvDesc.setVisibility(t.description == null || t.description.isEmpty() ? View.GONE : View.VISIBLE);

        // If t.dueAt > 0 is true, { immediately.
        if (t.dueAt > 0) {
            // Calls b.tvDue.setText with ctx.getString(R.string.due_format, to perform the operation shown.
            b.tvDue.setText(ctx.getString(R.string.due_format,
                    // Calls DateTimeUtils.formatDate with t.dueAt), DateTimeUtils.formatTime(t.dueAt to perform the operation shown.
                    DateTimeUtils.formatDate(t.dueAt), DateTimeUtils.formatTime(t.dueAt)));
        // Runs the fallback branch when the preceding condition did not match.
        } else {
            // Calls b.tvDue.setText with R.string.no_due_date to perform the operation shown.
            b.tvDue.setText(R.string.no_due_date);
        // Closes the current method, control-flow, or type block.
        }
        // Initializes overdue with t.isOverdue() so later statements can use the computed value.
        boolean overdue = t.isOverdue();
        // Calls b.tvDue.setTextColor with ctx.getColor(overdue ? R.color.np_danger : R.color.np_text_secondary to perform the operation shown.
        b.tvDue.setTextColor(ctx.getColor(overdue ? R.color.np_danger : R.color.np_text_secondary));
        // Calls b.tvOverdue.setVisibility with overdue ? View.VISIBLE : View.GONE to perform the operation shown.
        b.tvOverdue.setVisibility(overdue ? View.VISIBLE : View.GONE);

        // Initializes reminderOn with t.reminderEnabled && !t.completed so later statements can use the computed value.
        boolean reminderOn = t.reminderEnabled && !t.completed;
        // Calls b.ivBell.setImageTintList with ColorStateList.valueOf(ctx.getColor( to perform the operation shown.
        b.ivBell.setImageTintList(ColorStateList.valueOf(ctx.getColor(
                // Executes reminderOn ? R.color.np_primary : R.color.np_divider))) as a statement.
                reminderOn ? R.color.np_primary : R.color.np_divider)));
        // Calls b.ivBell.setContentDescription with ctx.getString(reminderOn ? R.string.reminder_on : R.string.reminder_off to perform the operation shown.
        b.ivBell.setContentDescription(ctx.getString(reminderOn ? R.string.reminder_on : R.string.reminder_off));

        // Calls b.llTags.removeAllViews with  to perform the operation shown.
        b.llTags.removeAllViews();
        // Initializes shown with 0 so later statements can use the computed value.
        int shown = 0;
        // Repeats the following block according to the initialization, condition, and update in Tag tag : t.tags.
        for (Tag tag : t.tags) {
            // If shown++ == 2 is true, break immediately.
            if (shown++ == 2) break;
            // Initializes lp with new LinearLayout.LayoutParams( so later statements can use the computed value.
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    // Executes ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT) as a statement.
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            // Calls lp.setMarginEnd with UiUtils.dp(ctx, 4 to perform the operation shown.
            lp.setMarginEnd(UiUtils.dp(ctx, 4));
            // Calls b.llTags.addView with UiUtils.tagPill(ctx, tag), lp to perform the operation shown.
            b.llTags.addView(UiUtils.tagPill(ctx, tag), lp);
        // Closes the current method, control-flow, or type block.
        }

        // Calls b.cbDone.setOnCheckedChangeListener with null to perform the operation shown.
        b.cbDone.setOnCheckedChangeListener(null);
        // Calls b.cbDone.setChecked with t.completed to perform the operation shown.
        b.cbDone.setChecked(t.completed);
        // Calls b.cbDone.setOnCheckedChangeListener with (v, checked) -> b.getRoot().post(() -> listener.onToggle(t, checked to perform the operation shown.
        b.cbDone.setOnCheckedChangeListener((v, checked) -> b.getRoot().post(() -> listener.onToggle(t, checked)));
        // Calls b.getRoot with ).setOnClickListener(v -> listener.onOpen(t to perform the operation shown.
        b.getRoot().setOnClickListener(v -> listener.onOpen(t));
        // Calls b.btnMenu.setOnClickListener with v -> listener.onMenu(t, v to perform the operation shown.
        b.btnMenu.setOnClickListener(v -> listener.onMenu(t, v));
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
        final ItemTodoBinding b;

        // Calls Holder with ItemTodoBinding b) { to perform the operation shown.
        Holder(ItemTodoBinding b) {
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
