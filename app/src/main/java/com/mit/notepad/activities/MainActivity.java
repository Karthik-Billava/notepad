// Sets the package name to com.mit.notepad.activities so this class belongs to the app module namespace.
package com.mit.notepad.activities;

// Imports android.Manifest so this file can use that type by its short name.
import android.Manifest;
// Imports android.content.Intent so this file can use that type by its short name.
import android.content.Intent;
// Imports android.content.pm.PackageManager so this file can use that type by its short name.
import android.content.pm.PackageManager;
// Imports android.net.Uri so this file can use that type by its short name.
import android.net.Uri;
// Imports android.os.Build so this file can use that type by its short name.
import android.os.Build;
// Imports android.os.Bundle so this file can use that type by its short name.
import android.os.Bundle;
// Imports android.provider.Settings so this file can use that type by its short name.
import android.provider.Settings;

// Imports androidx.activity.OnBackPressedCallback so this file can use that type by its short name.
import androidx.activity.OnBackPressedCallback;
// Imports androidx.activity.result.ActivityResultLauncher so this file can use that type by its short name.
import androidx.activity.result.ActivityResultLauncher;
// Imports androidx.activity.result.contract.ActivityResultContracts so this file can use that type by its short name.
import androidx.activity.result.contract.ActivityResultContracts;
// Imports androidx.core.content.ContextCompat so this file can use that type by its short name.
import androidx.core.content.ContextCompat;
// Imports androidx.core.view.GravityCompat so this file can use that type by its short name.
import androidx.core.view.GravityCompat;
// Imports androidx.fragment.app.Fragment so this file can use that type by its short name.
import androidx.fragment.app.Fragment;

// Imports com.google.android.material.snackbar.Snackbar so this file can use that type by its short name.
import com.google.android.material.snackbar.Snackbar;
// Imports com.mit.notepad.R so this file can use that type by its short name.
import com.mit.notepad.R;
// Imports com.mit.notepad.databinding.ActivityMainBinding so this file can use that type by its short name.
import com.mit.notepad.databinding.ActivityMainBinding;
// Imports com.mit.notepad.fragments.FavouritesFragment so this file can use that type by its short name.
import com.mit.notepad.fragments.FavouritesFragment;
// Imports com.mit.notepad.fragments.NotesFragment so this file can use that type by its short name.
import com.mit.notepad.fragments.NotesFragment;
// Imports com.mit.notepad.fragments.NotificationsFragment so this file can use that type by its short name.
import com.mit.notepad.fragments.NotificationsFragment;
// Imports com.mit.notepad.fragments.TagsFragment so this file can use that type by its short name.
import com.mit.notepad.fragments.TagsFragment;
// Imports com.mit.notepad.fragments.TodoFragment so this file can use that type by its short name.
import com.mit.notepad.fragments.TodoFragment;
// Imports com.mit.notepad.repositories.SettingsRepository so this file can use that type by its short name.
import com.mit.notepad.repositories.SettingsRepository;
// Imports com.mit.notepad.utils.Constants so this file can use that type by its short name.
import com.mit.notepad.utils.Constants;

/** Hosts the five bottom-navigation tabs and the navigation drawer. */
// Declares class MainActivity to group related state and behavior.
public class MainActivity extends BaseActivity {
    // Declares binding for use by the surrounding method or class.
    private ActivityMainBinding binding;

    // Initializes notificationPermission with  so later statements can use the computed value.
    private final ActivityResultLauncher<String> notificationPermission =
            // Starts the callback body that handles the event or asynchronous result.
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                // If !granted is true, { immediately.
                if (!granted) {
                    // Calls Snackbar.make with binding.getRoot(), R.string.notifications_denied, Snackbar.LENGTH_LONG to perform the operation shown.
                    Snackbar.make(binding.getRoot(), R.string.notifications_denied, Snackbar.LENGTH_LONG)
                            // Calls setAction on the object from the preceding line with the shown arguments.
                            .setAction(R.string.settings, v -> openAppNotificationSettings())
                            // Calls show on the object from the preceding line with the shown arguments.
                            .show();
                // Closes the current method, control-flow, or type block.
                }
            // Executes }) as a statement.
            });

    // Applies @Override metadata to the declaration that follows.
    @Override
    // Runs component initialization when Android invokes this lifecycle callback.
    protected void onCreate(Bundle savedInstanceState) {
        // Calls super.onCreate with savedInstanceState to perform the operation shown.
        super.onCreate(savedInstanceState);
        // Stores the result of ActivityMainBinding.inflate(getLayoutInflater()) in binding for use by later logic.
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        // Calls setContentView with binding.getRoot( to perform the operation shown.
        setContentView(binding.getRoot());

        // Starts the callback body that handles the event or asynchronous result.
        binding.bottomNav.setOnItemSelectedListener(item -> {
            // Calls showTab with item.getItemId( to perform the operation shown.
            showTab(item.getItemId());
            // Returns true to the caller.
            return true;
        // Executes }) as a statement.
        });
        // If savedInstanceState == null is true, { immediately.
        if (savedInstanceState == null) {
            // Calls binding.bottomNav.setSelectedItemId with R.id.nav_notes to perform the operation shown.
            binding.bottomNav.setSelectedItemId(R.id.nav_notes);
        // Closes the current method, control-flow, or type block.
        }

        // Starts the callback body that handles the event or asynchronous result.
        binding.navView.setNavigationItemSelectedListener(item -> {
            // Initializes id with item.getItemId() so later statements can use the computed value.
            int id = item.getItemId();
            // Calls binding.drawerLayout.closeDrawer with GravityCompat.START to perform the operation shown.
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            // If id == R.id.drawer_search is true, { immediately.
            if (id == R.id.drawer_search) {
                // Calls startActivity with new Intent(this, SearchActivity.class to perform the operation shown.
                startActivity(new Intent(this, SearchActivity.class));
            // Checks the alternate condition (id == R.id.drawer_alarms) after the earlier branch did not run.
            } else if (id == R.id.drawer_alarms) {
                // Calls startActivity with new Intent(this, AlarmActivity.class to perform the operation shown.
                startActivity(new Intent(this, AlarmActivity.class));
            // Checks the alternate condition (id == R.id.drawer_settings) after the earlier branch did not run.
            } else if (id == R.id.drawer_settings) {
                // Calls startActivity with new Intent(this, SettingsActivity.class to perform the operation shown.
                startActivity(new Intent(this, SettingsActivity.class));
            // Checks the alternate condition (id == R.id.drawer_exit) after the earlier branch did not run.
            } else if (id == R.id.drawer_exit) {
                // Calls confirmExit with this to perform the operation shown.
                confirmExit(this);
            // Closes the current method, control-flow, or type block.
            }
            // Returns false to the caller.
            return false;
        // Executes }) as a statement.
        });

        // Calls getOnBackPressedDispatcher with ).addCallback(this, new OnBackPressedCallback(true) { to perform the operation shown.
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            // Applies @Override metadata to the declaration that follows.
            @Override
            // Closes the drawer, returns to the default tab, or exits the screen on Back.
            public void handleOnBackPressed() {
                // If binding.drawerLayout.isDrawerOpen(GravityCompat.START) is true, { immediately.
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    // Calls binding.drawerLayout.closeDrawer with GravityCompat.START to perform the operation shown.
                    binding.drawerLayout.closeDrawer(GravityCompat.START);
                // Checks the alternate condition (binding.bottomNav.getSelectedItemId() after the earlier branch did not run.
                } else if (binding.bottomNav.getSelectedItemId() != R.id.nav_notes) {
                    // Calls binding.bottomNav.setSelectedItemId with R.id.nav_notes to perform the operation shown.
                    binding.bottomNav.setSelectedItemId(R.id.nav_notes);
                // Runs the fallback branch when the preceding condition did not match.
                } else {
                    // Calls setEnabled with false to perform the operation shown.
                    setEnabled(false);
                    // Calls getOnBackPressedDispatcher with ).onBackPressed( to perform the operation shown.
                    getOnBackPressedDispatcher().onBackPressed();
                // Closes the current method, control-flow, or type block.
                }
            // Closes the current method, control-flow, or type block.
            }
        // Executes }) as a statement.
        });

        // Calls requestNotificationPermissionOnce with  to perform the operation shown.
        requestNotificationPermissionOnce();
    // Closes the current method, control-flow, or type block.
    }

    // Displays tab.
    private void showTab(int itemId) {
        // Initializes tag with the text or query fragment shown here.
        String tag = "tab_" + itemId;
        // When getSupportFragmentManager().findFragmentByTag(tag is true, immediately != null.
        if (getSupportFragmentManager().findFragmentByTag(tag) != null
                // Requires the additional condition getSupportFragmentManager().findFragmentByTag(tag).isVisible()) { to be true as well.
                && getSupportFragmentManager().findFragmentByTag(tag).isVisible()) {
            // Returns control to the caller without a value.
            return;
        // Closes the current method, control-flow, or type block.
        }
        // Declares fragment for use by the surrounding method or class.
        Fragment fragment;
        // If itemId == R.id.nav_todo is true, fragment = new TodoFragment() immediately.
        if (itemId == R.id.nav_todo) fragment = new TodoFragment();
        // Checks the alternate condition (itemId == R.id.nav_tags) after the earlier branch did not run.
        else if (itemId == R.id.nav_tags) fragment = new TagsFragment();
        // Checks the alternate condition (itemId == R.id.nav_favourites) after the earlier branch did not run.
        else if (itemId == R.id.nav_favourites) fragment = new FavouritesFragment();
        // Checks the alternate condition (itemId == R.id.nav_notifications) after the earlier branch did not run.
        else if (itemId == R.id.nav_notifications) fragment = new NotificationsFragment();
        // Runs the fallback branch when the preceding condition did not match.
        else fragment = new NotesFragment();
        // Calls getSupportFragmentManager with ).beginTransaction( to perform the operation shown.
        getSupportFragmentManager().beginTransaction()
                // Calls replace on the object from the preceding line with the shown arguments.
                .replace(R.id.fragment_container, fragment, tag)
                // Calls commit on the object from the preceding line with the shown arguments.
                .commit();
    // Closes the current method, control-flow, or type block.
    }

    // Opens drawer.
    public void openDrawer() {
        // Calls binding.drawerLayout.openDrawer with GravityCompat.START to perform the operation shown.
        binding.drawerLayout.openDrawer(GravityCompat.START);
    // Closes the current method, control-flow, or type block.
    }

    /** Asks for POST_NOTIFICATIONS once (Android 13+). Denial is handled gracefully. */
    // Requests notification permission once.
    private void requestNotificationPermissionOnce() {
        // If Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU is true, return immediately.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return;
        // Runs the following block only when ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS evaluates to true.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                // Executes == PackageManager.PERMISSION_GRANTED) return as a statement.
                == PackageManager.PERMISSION_GRANTED) return;
        // Initializes settings with new SettingsRepository(this) so later statements can use the computed value.
        SettingsRepository settings = new SettingsRepository(this);
        // If settings.getBool(Constants.SETTING_PERMISSION_ASKED, false) is true, return immediately.
        if (settings.getBool(Constants.SETTING_PERMISSION_ASKED, false)) return;
        // Calls settings.putBool with Constants.SETTING_PERMISSION_ASKED, true to perform the operation shown.
        settings.putBool(Constants.SETTING_PERMISSION_ASKED, true);
        // Calls notificationPermission.launch with Manifest.permission.POST_NOTIFICATIONS to perform the operation shown.
        notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS);
    // Closes the current method, control-flow, or type block.
    }

    // Opens app notification settings.
    private void openAppNotificationSettings() {
        // Initializes intent with new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS) so later statements can use the computed value.
        Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                // Calls putExtra on the object from the preceding line with the shown arguments.
                .putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
        // Calls startActivity with intent to perform the operation shown.
        startActivity(intent);
    // Closes the current method, control-flow, or type block.
    }

    /** Exit confirmation used by the drawer and by Settings. */
    // Confirms exit.
    public static void confirmExit(android.app.Activity activity) {
        // Creates the object described by new com.google.android.material.dialog.MaterialAlertDialogBuilder(activity).
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(activity)
                // Calls setTitle on the object from the preceding line with the shown arguments.
                .setTitle(R.string.exit_title)
                // Calls setMessage on the object from the preceding line with the shown arguments.
                .setMessage(R.string.exit_message)
                // Calls setPositiveButton on the object from the preceding line with the shown arguments.
                .setPositiveButton(R.string.exit_app, (d, w) -> activity.finishAffinity())
                // Calls setNegativeButton on the object from the preceding line with the shown arguments.
                .setNegativeButton(R.string.cancel, null)
                // Calls show on the object from the preceding line with the shown arguments.
                .show();
    // Closes the current method, control-flow, or type block.
    }
// Closes the current method, control-flow, or type block.
}
