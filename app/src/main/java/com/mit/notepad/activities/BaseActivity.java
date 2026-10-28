// Sets the package name to com.mit.notepad.activities so this class belongs to the app module namespace.
package com.mit.notepad.activities;

// Imports android.os.Bundle so this file can use that type by its short name.
import android.os.Bundle;
// Imports android.view.View so this file can use that type by its short name.
import android.view.View;

// Imports androidx.annotation.StringRes so this file can use that type by its short name.
import androidx.annotation.StringRes;
// Imports androidx.appcompat.app.AppCompatActivity so this file can use that type by its short name.
import androidx.appcompat.app.AppCompatActivity;
// Imports androidx.core.view.WindowCompat so this file can use that type by its short name.
import androidx.core.view.WindowCompat;
// Imports androidx.core.view.WindowInsetsControllerCompat so this file can use that type by its short name.
import androidx.core.view.WindowInsetsControllerCompat;

// Imports com.mit.notepad.R so this file can use that type by its short name.
import com.mit.notepad.R;
// Imports com.mit.notepad.databinding.IncludeHeaderBinding so this file can use that type by its short name.
import com.mit.notepad.databinding.IncludeHeaderBinding;
// Imports com.mit.notepad.repositories.NotificationRepository so this file can use that type by its short name.
import com.mit.notepad.repositories.NotificationRepository;
// Imports com.mit.notepad.utils.Constants so this file can use that type by its short name.
import com.mit.notepad.utils.Constants;
// Imports com.mit.notepad.utils.ThemeUtils so this file can use that type by its short name.
import com.mit.notepad.utils.ThemeUtils;

/** Applies the saved theme, re-applies it when it changes and offers shared screen helpers. */
// Declares class BaseActivity to group related state and behavior.
public abstract class BaseActivity extends AppCompatActivity {
    // Declares appliedTheme for use by the surrounding method or class.
    private String appliedTheme;

    // Applies @Override metadata to the declaration that follows.
    @Override
    // Runs component initialization when Android invokes this lifecycle callback.
    protected void onCreate(Bundle savedInstanceState) {
        // Stores the result of ThemeUtils.apply(this) in appliedTheme for use by later logic.
        appliedTheme = ThemeUtils.apply(this);
        // Calls super.onCreate with savedInstanceState to perform the operation shown.
        super.onCreate(savedInstanceState);
        // Initializes night with (getResources().getConfiguration().uiMode so later statements can use the computed value.
        boolean night = (getResources().getConfiguration().uiMode
                // Continues the expression above with & android.content.res.Configuration.UI_MODE_NIGHT_MASK).
                & android.content.res.Configuration.UI_MODE_NIGHT_MASK)
                // Executes == android.content.res.Configuration.UI_MODE_NIGHT_YES as a statement.
                == android.content.res.Configuration.UI_MODE_NIGHT_YES;
        // Initializes controller with  so later statements can use the computed value.
        WindowInsetsControllerCompat controller =
                // Calls WindowCompat.getInsetsController with getWindow(), getWindow().getDecorView( to perform the operation shown.
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        // Calls controller.setAppearanceLightStatusBars with !night to perform the operation shown.
        controller.setAppearanceLightStatusBars(!night);

        // Opened from a notification: mark that history entry as read.
        // Initializes notificationId with getIntent().getLongExtra(Constants.EXTRA_NOTIFICATION_ID, -1) so later statements can use the computed value.
        long notificationId = getIntent().getLongExtra(Constants.EXTRA_NOTIFICATION_ID, -1);
        // If notificationId > 0 is true, new NotificationRepository(this).markRead(notificationId) immediately.
        if (notificationId > 0) new NotificationRepository(this).markRead(notificationId);
    // Closes the current method, control-flow, or type block.
    }

    // Applies @Override metadata to the declaration that follows.
    @Override
    // Refreshes visible data when this component becomes active.
    protected void onResume() {
        // Calls super.onResume with  to perform the operation shown.
        super.onResume();
        // If !appliedTheme.equals(ThemeUtils.currentKey(this)) is true, recreate() immediately.
        if (!appliedTheme.equals(ThemeUtils.currentKey(this))) recreate();
    // Closes the current method, control-flow, or type block.
    }

    /** Header with a back arrow, title and subtitle; other buttons hidden unless enabled by caller. */
    // Updates up back header.
    protected void setupBackHeader(IncludeHeaderBinding h, @StringRes int title, @StringRes int subtitle) {
        // Calls h.headerNav.setImageResource with R.drawable.ic_arrow_back to perform the operation shown.
        h.headerNav.setImageResource(R.drawable.ic_arrow_back);
        // Calls h.headerNav.setContentDescription with getString(R.string.cd_back to perform the operation shown.
        h.headerNav.setContentDescription(getString(R.string.cd_back));
        // Calls h.headerNav.setOnClickListener with v -> getOnBackPressedDispatcher().onBackPressed( to perform the operation shown.
        h.headerNav.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        // Calls h.headerTitle.setText with title to perform the operation shown.
        h.headerTitle.setText(title);
        // Calls h.headerSubtitle.setText with subtitle to perform the operation shown.
        h.headerSubtitle.setText(subtitle);
        // Calls h.headerAction.setVisibility with View.GONE to perform the operation shown.
        h.headerAction.setVisibility(View.GONE);
        // Calls h.headerSearch.setVisibility with View.GONE to perform the operation shown.
        h.headerSearch.setVisibility(View.GONE);
        // Calls h.headerMore.setVisibility with View.GONE to perform the operation shown.
        h.headerMore.setVisibility(View.GONE);
    // Closes the current method, control-flow, or type block.
    }
// Closes the current method, control-flow, or type block.
}
