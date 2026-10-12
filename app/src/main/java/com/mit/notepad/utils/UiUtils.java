package com.mit.notepad.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.StringRes;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.mit.notepad.R;
import com.mit.notepad.models.Tag;

import java.util.Locale;

/** Small UI helpers shared across screens. */
public final class UiUtils {
    private UiUtils() { }

    public static void toast(Context ctx, @StringRes int res) {
        Toast.makeText(ctx, res, Toast.LENGTH_SHORT).show();
    }

    public static void toast(Context ctx, String msg) {
        Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
    }

    public static int dp(Context ctx, int dp) {
        return Math.round(dp * ctx.getResources().getDisplayMetrics().density);
    }

    public static void hideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    /** Standard confirmation dialog for destructive actions. */
    public static void confirm(Context ctx, @StringRes int title, CharSequence message,
                               @StringRes int positive, Runnable onConfirm) {
        new MaterialAlertDialogBuilder(ctx)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positive, (d, w) -> onConfirm.run())
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    /** Highlights case-insensitive matches of keyword inside text. */
    public static CharSequence highlight(String text, String keyword, int color) {
        if (text == null) return "";
        if (keyword == null || keyword.trim().isEmpty()) return text;
        SpannableString s = new SpannableString(text);
        String lower = text.toLowerCase(Locale.getDefault());
        String k = keyword.trim().toLowerCase(Locale.getDefault());
        int from = 0;
        while (true) {
            int i = lower.indexOf(k, from);
            if (i < 0) break;
            s.setSpan(new BackgroundColorSpan(color), i, i + k.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            from = i + k.length();
        }
        return s;
    }

    /** Small rounded "#tag" pill coloured with the tag colour. */
    public static TextView tagPill(Context ctx, Tag tag) {
        TextView tv = new TextView(ctx);
        tv.setText(String.format(Locale.getDefault(), "#%s", tag.name));
        tv.setTextSize(11);
        tv.setTextColor(TagStyle.color(tag.color));
        tv.setGravity(Gravity.CENTER);
        tv.setPadding(UiUtils.dp(ctx, 10), UiUtils.dp(ctx, 4), UiUtils.dp(ctx, 10), UiUtils.dp(ctx, 4));
        GradientDrawable bg = new GradientDrawable();
        bg.setCornerRadius(UiUtils.dp(ctx, 12));
        bg.setColor(TagStyle.soft(tag.color));
        tv.setBackground(bg);
        return tv;
    }

    public static int parseColor(String hex, int fallback) {
        try {
            return Color.parseColor(hex);
        } catch (IllegalArgumentException e) {
            return fallback;
        }
    }
}
