package com.mit.notepad.alarms;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.mit.notepad.R;
import com.mit.notepad.activities.AlarmRingingActivity;

/** Plays the device alarm tone until the user dismisses it. */
public class AlarmRingingService extends Service {
    public static final String ACTION_START = "com.mit.notepad.action.START_RINGING";
    public static final String ACTION_STOP = "com.mit.notepad.action.STOP_RINGING";
    public static final String EXTRA_TITLE = "ringing_title";
    public static final String EXTRA_MESSAGE = "ringing_message";
    private static final String CHANNEL_ID = "np_active_alarm";
    private static final int NOTIFICATION_ID = 8701;

    private MediaPlayer player;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && ACTION_STOP.equals(intent.getAction())) {
            stopSelf();
            return START_NOT_STICKY;
        }

        String title = intent == null ? getString(R.string.notif_alarm)
                : intent.getStringExtra(EXTRA_TITLE);
        String message = intent == null ? "" : intent.getStringExtra(EXTRA_MESSAGE);
        if (title == null) title = getString(R.string.notif_alarm);
        if (message == null) message = "";

        createChannel();
        startForeground(NOTIFICATION_ID, makeNotification(title, message));
        if (player == null || !player.isPlaying()) startAlarmTone();
        return START_STICKY;
    }

    private Notification makeNotification(String title, String message) {
        Intent screen = new Intent(this, AlarmRingingActivity.class)
                .putExtra(EXTRA_TITLE, title)
                .putExtra(EXTRA_MESSAGE, message)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent screenPending = PendingIntent.getActivity(this, 0, screen,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Intent stop = new Intent(this, AlarmRingingService.class).setAction(ACTION_STOP);
        PendingIntent stopPending = PendingIntent.getService(this, 1, stop,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setOngoing(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentIntent(screenPending)
                .setFullScreenIntent(screenPending, true)
                .addAction(R.drawable.ic_close, getString(R.string.dismiss_alarm), stopPending)
                .build();
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return;
        NotificationManager manager = getSystemService(NotificationManager.class);
        if (manager == null || manager.getNotificationChannel(CHANNEL_ID) != null) return;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                getString(R.string.channel_active_alarm), NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription(getString(R.string.channel_active_alarm_desc));
        channel.setSound(null, null); // Audio is played directly by this service.
        manager.createNotificationChannel(channel);
    }

    private void startAlarmTone() {
        Uri tone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (tone == null) tone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        try {
            player = new MediaPlayer();
            player.setAudioAttributes(new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build());
            player.setDataSource(this, tone);
            player.setLooping(true);
            player.setOnErrorListener((mp, what, extra) -> {
                stopSelf();
                return true;
            });
            player.prepare();
            player.start();
        } catch (Exception e) {
            releasePlayer();
            stopSelf();
        }
    }

    @Override
    public void onDestroy() {
        releasePlayer();
        stopForeground(true);
        super.onDestroy();
    }

    private void releasePlayer() {
        if (player == null) return;
        try {
            if (player.isPlaying()) player.stop();
        } catch (IllegalStateException ignored) { }
        player.release();
        player = null;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) { return null; }
}
