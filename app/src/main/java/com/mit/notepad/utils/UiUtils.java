// Sets the package name to com.mit.notepad.utils so this class belongs to the app module namespace.
package com.mit.notepad.utils;

// Imports android.content.Context so this file can use that type by its short name.
import android.content.Context;
// Imports android.graphics.Color so this file can use that type by its short name.
import android.graphics.Color;
// Imports android.graphics.drawable.GradientDrawable so this file can use that type by its short name.
import android.graphics.drawable.GradientDrawable;
// Imports android.text.Spannable so this file can use that type by its short name.
import android.text.Spannable;
// Imports android.text.SpannableString so this file can use that type by its short name.
import android.text.SpannableString;
// Imports android.text.style.BackgroundColorSpan so this file can use that type by its short name.
import android.text.style.BackgroundColorSpan;
// Imports android.view.Gravity so this file can use that type by its short name.
import android.view.Gravity;
// Imports android.view.View so this file can use that type by its short name.
import android.view.View;
// Imports android.view.inputmethod.InputMethodManager so this file can use that type by its short name.
import android.view.inputmethod.InputMethodManager;
// Imports android.widget.TextView so this file can use that type by its short name.
import android.widget.TextView;
// Imports android.widget.Toast so this file can use that type by its short name.
import android.widget.Toast;

// Imports androidx.annotation.StringRes so this file can use that type by its short name.
import androidx.annotation.StringRes;

// Imports com.google.android.material.dialog.MaterialAlertDialogBuilder so this file can use that type by its short name.
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
// Imports com.mit.notepad.R so this file can use that type by its short name.
import com.mit.notepad.R;
// Imports com.mit.notepad.models.Tag so this file can use that type by its short name.
import com.mit.notepad.models.Tag;

// Imports java.util.Locale so this file can use that type by its short name.
import java.util.Locale;

/** Small UI helpers shared across screens. */
// Declares class UiUtils to group related state and behavior.
public final class UiUtils {
    // Declares UiUtils() to implement this part of the component.
    private UiUtils() { }

    // Declares toast() to implement this part of the component.
    public static void toast(Context ctx, @StringRes int res) {
        // Calls Toast.makeText with ctx, res, Toast.LENGTH_SHORT).show( to perform the operation shown.
        Toast.makeText(ctx, res, Toast.LENGTH_SHORT).show();
    // Closes the current method, control-flow, or type block.
    }

    // Declares toast() to implement this part of the component.
    public static void toast(Context ctx, String msg) {
        // Calls Toast.makeText with ctx, msg, Toast.LENGTH_SHORT).show( to perform the operation shown.
        Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
    // Closes the current method, control-flow, or type block.
    }

    // Declares dp() to implement this part of the component.
    public static int dp(Context ctx, int dp) {
        // Returns Math.round(dp * ctx.getResources().getDisplayMetrics().density) to the caller.
        return Math.round(dp * ctx.getResources().getDisplayMetrics().density);
    // Closes the current method, control-flow, or type block.
    }

    // Declares hideKeyboard() to implement this part of the component.
    public static void hideKeyboard(View v) {
        // Initializes imm with (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE) so later statements can use the computed value.
        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        // If imm != null is true, imm.hideSoftInputFromWindow(v.getWindowToken(), 0) immediately.
        if (imm != null) imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    // Closes the current method, control-flow, or type block.
    }

    /** Standard confirmation dialog for destructive actions. */
    // Declares confirm() to implement this part of the component.
    public static void confirm(Context ctx, @StringRes int title, CharSequence message,
                               // Applies @StringRes int positive, Runnable onConfirm) { metadata to the declaration that follows.
                               @StringRes int positive, Runnable onConfirm) {
        // Creates the object described by new MaterialAlertDialogBuilder(ctx).
        new MaterialAlertDialogBuilder(ctx)
                // Calls setTitle on the object from the preceding line with the shown arguments.
                .setTitle(title)
                // Calls setMessage on the object from the preceding line with the shown arguments.
                .setMessage(message)
                // Calls setPositiveButton on the object from the preceding line with the shown arguments.
                .setPositiveButton(positive, (d, w) -> onConfirm.run())
                // Calls setNegativeButton on the object from the preceding line with the shown arguments.
                .setNegativeButton(R.string.cancel, null)
                // Calls show on the object from the preceding line with the shown arguments.
                .show();
    // Closes the current method, control-flow, or type block.
    }

    /** Highlights case-insensitive matches of keyword inside text. */
    // Declares highlight() to implement this part of the component.
    public static CharSequence highlight(String text, String keyword, int color) {
        // If text == null is true, return "" immediately.
        if (text == null) return "";
        // If keyword == null || keyword.trim().isEmpty() is true, return text immediately.
        if (keyword == null || keyword.trim().isEmpty()) return text;
        // Initializes s with new SpannableString(text) so later statements can use the computed value.
        SpannableString s = new SpannableString(text);
        // Initializes lower with text.toLowerCase(Locale.getDefault()) so later statements can use the computed value.
        String lower = text.toLowerCase(Locale.getDefault());
        // Initializes k with keyword.trim().toLowerCase(Locale.getDefault()) so later statements can use the computed value.
        String k = keyword.trim().toLowerCase(Locale.getDefault());
        // Initializes from with 0 so later statements can use the computed value.
        int from = 0;
        // If true is true, { immediately.
        while (true) {
            // Initializes i with lower.indexOf(k, from) so later statements can use the computed value.
            int i = lower.indexOf(k, from);
            // If i < 0 is true, break immediately.
            if (i < 0) break;
            // Calls s.setSpan with new BackgroundColorSpan(color), i, i + k.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE to perform the operation shown.
            s.setSpan(new BackgroundColorSpan(color), i, i + k.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            // Stores the result of i + k.length() in from for use by later logic.
            from = i + k.length();
        // Closes the current method, control-flow, or type block.
        }
        // Returns s to the caller.
        return s;
    // Closes the current method, control-flow, or type block.
    }

    /** Small rounded "#tag" pill coloured with the tag colour. */
    // Declares tagPill() to implement this part of the component.
    public static TextView tagPill(Context ctx, Tag tag) {
        // Initializes tv with new TextView(ctx) so later statements can use the computed value.
        TextView tv = new TextView(ctx);
        // Calls tv.setText with String.format(Locale.getDefault(), "#%s", tag.name to perform the operation shown.
        tv.setText(String.format(Locale.getDefault(), "#%s", tag.name));
        // Calls tv.setTextSize with 11 to perform the operation shown.
        tv.setTextSize(11);
        // Calls tv.setTextColor with TagStyle.color(tag.color to perform the operation shown.
        tv.setTextColor(TagStyle.color(tag.color));
        // Calls tv.setGravity with Gravity.CENTER to perform the operation shown.
        tv.setGravity(Gravity.CENTER);
        // Calls tv.setPadding with UiUtils.dp(ctx, 10), UiUtils.dp(ctx, 4), UiUtils.dp(ctx, 10), UiUtils.dp(ctx, 4 to perform the operation shown.
        tv.setPadding(UiUtils.dp(ctx, 10), UiUtils.dp(ctx, 4), UiUtils.dp(ctx, 10), UiUtils.dp(ctx, 4));
        // Initializes bg with new GradientDrawable() so later statements can use the computed value.
        GradientDrawable bg = new GradientDrawable();
        // Calls bg.setCornerRadius with UiUtils.dp(ctx, 12 to perform the operation shown.
        bg.setCornerRadius(UiUtils.dp(ctx, 12));
        // Calls bg.setColor with TagStyle.soft(tag.color to perform the operation shown.
        bg.setColor(TagStyle.soft(tag.color));
        // Calls tv.setBackground with bg to perform the operation shown.
        tv.setBackground(bg);
        // Returns tv to the caller.
        return tv;
    // Closes the current method, control-flow, or type block.
    }

    // Parses color.
    public static int parseColor(String hex, int fallback) {
        // Starts the operation whose failures are handled by the following catch clauses.
        try {
            // Returns Color.parseColor(hex) to the caller.
            return Color.parseColor(hex);
        // Calls catch with IllegalArgumentException e) { to perform the operation shown.
        } catch (IllegalArgumentException e) {
            // Returns fallback to the caller.
            return fallback;
        // Closes the current method, control-flow, or type block.
        }
    // Closes the current method, control-flow, or type block.
    }
// Closes the current method, control-flow, or type block.
}
