package com.mit.notepad.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

import com.mit.notepad.R;
import com.mit.notepad.database.NotepadDatabase;
import com.mit.notepad.databinding.ActivitySplashBinding;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Shows the logo with a progress animation while the database is opened, then enters the app. */
public class SplashActivity extends BaseActivity {
    private static final long DURATION_MS = 1600;

    private ActivitySplashBinding binding;
    private ValueAnimator animator;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private volatile boolean dbReady;
    private boolean animationDone;
    private boolean navigated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SpannableString name = new SpannableString(getString(R.string.app_name));
        name.setSpan(new ForegroundColorSpan(getColor(R.color.np_primary)), 4, name.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        binding.tvSplashName.setText(name);

        // Open (and create/upgrade if needed) the database off the main thread.
        executor.execute(() -> {
            NotepadDatabase.getInstance(getApplicationContext()).getWritableDatabase();
            dbReady = true;
            runOnUiThread(this::proceedIfReady);
        });

        animator = ValueAnimator.ofInt(0, 100);
        animator.setDuration(DURATION_MS);
        animator.addUpdateListener(a -> binding.splashProgress.setProgress((int) a.getAnimatedValue()));
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                animationDone = true;
                proceedIfReady();
            }
        });
        animator.start();
    }

    private void proceedIfReady() {
        if (navigated || !animationDone || !dbReady || isFinishing() || isDestroyed()) return;
        navigated = true;
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        if (animator != null) {
            animator.removeAllListeners();
            animator.cancel();
        }
        executor.shutdown();
        super.onDestroy();
    }
}
