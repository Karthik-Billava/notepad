package com.mit.notepad.activities;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.mit.notepad.R;
import com.mit.notepad.databinding.IncludeHeaderBinding;
import com.mit.notepad.repositories.NotificationRepository;
import com.mit.notepad.utils.Constants;
import com.mit.notepad.utils.ThemeUtils;

/** Applies the saved theme, re-applies it when it changes and offers shared screen helpers. */
public abstract class BaseActivity extends AppCompatActivity {
    private String appliedTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        appliedTheme = ThemeUtils.apply(this);
        super.onCreate(savedInstanceState);
        boolean night = (getResources().getConfiguration().uiMode
                & android.content.res.Configuration.UI_MODE_NIGHT_MASK)
                == android.content.res.Configuration.UI_MODE_NIGHT_YES;
        WindowInsetsControllerCompat controller =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        controller.setAppearanceLightStatusBars(!night);

        // Opened from a notification: mark that history entry as read.
        long notificationId = getIntent().getLongExtra(Constants.EXTRA_NOTIFICATION_ID, -1);
        if (notificationId > 0) new NotificationRepository(this).markRead(notificationId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!appliedTheme.equals(ThemeUtils.currentKey(this))) recreate();
    }

    /** Header with a back arrow, title and subtitle; other buttons hidden unless enabled by caller. */
    protected void setupBackHeader(IncludeHeaderBinding h, @StringRes int title, @StringRes int subtitle) {
        h.headerNav.setImageResource(R.drawable.ic_arrow_back);
        h.headerNav.setContentDescription(getString(R.string.cd_back));
        h.headerNav.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        h.headerTitle.setText(title);
        h.headerSubtitle.setText(subtitle);
        h.headerAction.setVisibility(View.GONE);
        h.headerSearch.setVisibility(View.GONE);
        h.headerMore.setVisibility(View.GONE);
    }
}
