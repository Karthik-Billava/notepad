package com.mit.notepad.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.mit.notepad.R;
import com.mit.notepad.databinding.ActivityMainBinding;
import com.mit.notepad.fragments.FavouritesFragment;
import com.mit.notepad.fragments.NotesFragment;
import com.mit.notepad.fragments.NotificationsFragment;
import com.mit.notepad.fragments.TagsFragment;
import com.mit.notepad.fragments.TodoFragment;
import com.mit.notepad.repositories.SettingsRepository;
import com.mit.notepad.utils.Constants;

/** Hosts the five bottom-navigation tabs and the navigation drawer. */
public class MainActivity extends BaseActivity {
    private ActivityMainBinding binding;

    private final ActivityResultLauncher<String> notificationPermission =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (!granted) {
                    Snackbar.make(binding.getRoot(), R.string.notifications_denied, Snackbar.LENGTH_LONG)
                            .setAction(R.string.settings, v -> openAppNotificationSettings())
                            .show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.bottomNav.setOnItemSelectedListener(item -> {
            showTab(item.getItemId());
            return true;
        });
        if (savedInstanceState == null) {
            binding.bottomNav.setSelectedItemId(R.id.nav_notes);
        }

        binding.navView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            if (id == R.id.drawer_search) {
                startActivity(new Intent(this, SearchActivity.class));
            } else if (id == R.id.drawer_alarms) {
                startActivity(new Intent(this, AlarmActivity.class));
            } else if (id == R.id.drawer_settings) {
                startActivity(new Intent(this, SettingsActivity.class));
            } else if (id == R.id.drawer_exit) {
                confirmExit(this);
            }
            return false;
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START);
                } else if (binding.bottomNav.getSelectedItemId() != R.id.nav_notes) {
                    binding.bottomNav.setSelectedItemId(R.id.nav_notes);
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });

        requestNotificationPermissionOnce();
    }

    private void showTab(int itemId) {
        String tag = "tab_" + itemId;
        if (getSupportFragmentManager().findFragmentByTag(tag) != null
                && getSupportFragmentManager().findFragmentByTag(tag).isVisible()) {
            return;
        }
        Fragment fragment;
        if (itemId == R.id.nav_todo) fragment = new TodoFragment();
        else if (itemId == R.id.nav_tags) fragment = new TagsFragment();
        else if (itemId == R.id.nav_favourites) fragment = new FavouritesFragment();
        else if (itemId == R.id.nav_notifications) fragment = new NotificationsFragment();
        else fragment = new NotesFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment, tag)
                .commit();
    }

    public void openDrawer() {
        binding.drawerLayout.openDrawer(GravityCompat.START);
    }

    /** Asks for POST_NOTIFICATIONS once (Android 13+). Denial is handled gracefully. */
    private void requestNotificationPermissionOnce() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) return;
        SettingsRepository settings = new SettingsRepository(this);
        if (settings.getBool(Constants.SETTING_PERMISSION_ASKED, false)) return;
        settings.putBool(Constants.SETTING_PERMISSION_ASKED, true);
        notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS);
    }

    private void openAppNotificationSettings() {
        Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                .putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
        startActivity(intent);
    }

    /** Exit confirmation used by the drawer and by Settings. */
    public static void confirmExit(android.app.Activity activity) {
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(activity)
                .setTitle(R.string.exit_title)
                .setMessage(R.string.exit_message)
                .setPositiveButton(R.string.exit_app, (d, w) -> activity.finishAffinity())
                .setNegativeButton(R.string.cancel, null)
                .show();
    }
}
