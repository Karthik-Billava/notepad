// Sets the package name to com.mit.notepad.activities so this class belongs to the app module namespace.
package com.mit.notepad.activities;

// Imports android.animation.Animator so this file can use that type by its short name.
import android.animation.Animator;
// Imports android.animation.AnimatorListenerAdapter so this file can use that type by its short name.
import android.animation.AnimatorListenerAdapter;
// Imports android.animation.ValueAnimator so this file can use that type by its short name.
import android.animation.ValueAnimator;
// Imports android.content.Intent so this file can use that type by its short name.
import android.content.Intent;
// Imports android.os.Bundle so this file can use that type by its short name.
import android.os.Bundle;
// Imports android.text.SpannableString so this file can use that type by its short name.
import android.text.SpannableString;
// Imports android.text.Spanned so this file can use that type by its short name.
import android.text.Spanned;
// Imports android.text.style.ForegroundColorSpan so this file can use that type by its short name.
import android.text.style.ForegroundColorSpan;

// Imports com.mit.notepad.R so this file can use that type by its short name.
import com.mit.notepad.R;
// Imports com.mit.notepad.database.NotepadDatabase so this file can use that type by its short name.
import com.mit.notepad.database.NotepadDatabase;
// Imports com.mit.notepad.databinding.ActivitySplashBinding so this file can use that type by its short name.
import com.mit.notepad.databinding.ActivitySplashBinding;

// Imports java.util.concurrent.ExecutorService so this file can use that type by its short name.
import java.util.concurrent.ExecutorService;
// Imports java.util.concurrent.Executors so this file can use that type by its short name.
import java.util.concurrent.Executors;

/** Shows the logo with a progress animation while the database is opened, then enters the app. */
// Declares class SplashActivity to group related state and behavior.
public class SplashActivity extends BaseActivity {
    // Initializes DURATION_MS with 1600 so later statements can use the computed value.
    private static final long DURATION_MS = 1600;

    // Declares binding for use by the surrounding method or class.
    private ActivitySplashBinding binding;
    // Declares animator for use by the surrounding method or class.
    private ValueAnimator animator;
    // Initializes executor with Executors.newSingleThreadExecutor() so later statements can use the computed value.
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    // Declares dbReady for use by the surrounding method or class.
    private volatile boolean dbReady;
    // Declares animationDone for use by the surrounding method or class.
    private boolean animationDone;
    // Declares navigated for use by the surrounding method or class.
    private boolean navigated;

    // Applies @Override metadata to the declaration that follows.
    @Override
    // Runs component initialization when Android invokes this lifecycle callback.
    protected void onCreate(Bundle savedInstanceState) {
        // Calls super.onCreate with savedInstanceState to perform the operation shown.
        super.onCreate(savedInstanceState);
        // Stores the result of ActivitySplashBinding.inflate(getLayoutInflater()) in binding for use by later logic.
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        // Calls setContentView with binding.getRoot( to perform the operation shown.
        setContentView(binding.getRoot());

        // Initializes name with new SpannableString(getString(R.string.app_name)) so later statements can use the computed value.
        SpannableString name = new SpannableString(getString(R.string.app_name));
        // Calls name.setSpan with new ForegroundColorSpan(getColor(R.color.np_primary)), 4, name.length(), to perform the operation shown.
        name.setSpan(new ForegroundColorSpan(getColor(R.color.np_primary)), 4, name.length(),
                // Executes Spanned.SPAN_EXCLUSIVE_EXCLUSIVE) as a statement.
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // Calls binding.tvSplashName.setText with name to perform the operation shown.
        binding.tvSplashName.setText(name);

        // Open (and create/upgrade if needed) the database off the main thread.
        // Starts the callback body that handles the event or asynchronous result.
        executor.execute(() -> {
            // Calls NotepadDatabase.getInstance with getApplicationContext()).getWritableDatabase( to perform the operation shown.
            NotepadDatabase.getInstance(getApplicationContext()).getWritableDatabase();
            // Stores the result of true in dbReady for use by later logic.
            dbReady = true;
            // Calls runOnUiThread with this::proceedIfReady to perform the operation shown.
            runOnUiThread(this::proceedIfReady);
        // Executes }) as a statement.
        });

        // Stores the result of ValueAnimator.ofInt(0, 100) in animator for use by later logic.
        animator = ValueAnimator.ofInt(0, 100);
        // Calls animator.setDuration with DURATION_MS to perform the operation shown.
        animator.setDuration(DURATION_MS);
        // Calls animator.addUpdateListener with a -> binding.splashProgress.setProgress((int) a.getAnimatedValue( to perform the operation shown.
        animator.addUpdateListener(a -> binding.splashProgress.setProgress((int) a.getAnimatedValue()));
        // Calls animator.addListener with new AnimatorListenerAdapter() { to perform the operation shown.
        animator.addListener(new AnimatorListenerAdapter() {
            // Applies @Override metadata to the declaration that follows.
            @Override
            // Declares onAnimationEnd() to implement this part of the component.
            public void onAnimationEnd(Animator animation) {
                // Stores the result of true in animationDone for use by later logic.
                animationDone = true;
                // Calls proceedIfReady with  to perform the operation shown.
                proceedIfReady();
            // Closes the current method, control-flow, or type block.
            }
        // Executes }) as a statement.
        });
        // Calls animator.start with  to perform the operation shown.
        animator.start();
    // Closes the current method, control-flow, or type block.
    }

    // Declares proceedIfReady() to implement this part of the component.
    private void proceedIfReady() {
        // If navigated || !animationDone || !dbReady || isFinishing() || isDestroyed() is true, return immediately.
        if (navigated || !animationDone || !dbReady || isFinishing() || isDestroyed()) return;
        // Stores the result of true in navigated for use by later logic.
        navigated = true;
        // Calls startActivity with new Intent(this, MainActivity.class to perform the operation shown.
        startActivity(new Intent(this, MainActivity.class));
        // Calls finish with  to perform the operation shown.
        finish();
    // Closes the current method, control-flow, or type block.
    }

    // Applies @Override metadata to the declaration that follows.
    @Override
    // Declares onDestroy() to implement this part of the component.
    protected void onDestroy() {
        // If animator != null is true, { immediately.
        if (animator != null) {
            // Calls animator.removeAllListeners with  to perform the operation shown.
            animator.removeAllListeners();
            // Calls animator.cancel with  to perform the operation shown.
            animator.cancel();
        // Closes the current method, control-flow, or type block.
        }
        // Calls executor.shutdown with  to perform the operation shown.
        executor.shutdown();
        // Calls super.onDestroy with  to perform the operation shown.
        super.onDestroy();
    // Closes the current method, control-flow, or type block.
    }
// Closes the current method, control-flow, or type block.
}
