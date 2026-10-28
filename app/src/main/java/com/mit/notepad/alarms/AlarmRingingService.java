// Sets the package name to com.mit.notepad.alarms so this class belongs to the app module namespace.
package com.mit.notepad.alarms;

// Imports android.app.Notification so this file can use that type by its short name.
import android.app.Notification;
// Imports android.app.NotificationChannel so this file can use that type by its short name.
import android.app.NotificationChannel;
// Imports android.app.NotificationManager so this file can use that type by its short name.
import android.app.NotificationManager;
// Imports android.app.PendingIntent so this file can use that type by its short name.
import android.app.PendingIntent;
// Imports android.app.Service so this file can use that type by its short name.
import android.app.Service;
// Imports android.content.Intent so this file can use that type by its short name.
import android.content.Intent;
// Imports android.media.AudioAttributes so this file can use that type by its short name.
import android.media.AudioAttributes;
// Imports android.media.MediaPlayer so this file can use that type by its short name.
import android.media.MediaPlayer;
// Imports android.media.RingtoneManager so this file can use that type by its short name.
import android.media.RingtoneManager;
// Imports android.net.Uri so this file can use that type by its short name.
import android.net.Uri;
// Imports android.os.Build so this file can use that type by its short name.
import android.os.Build;
// Imports android.os.IBinder so this file can use that type by its short name.
import android.os.IBinder;

// Imports androidx.annotation.Nullable so this file can use that type by its short name.
import androidx.annotation.Nullable;
// Imports androidx.core.app.NotificationCompat so this file can use that type by its short name.
import androidx.core.app.NotificationCompat;

// Imports com.mit.notepad.R so this file can use that type by its short name.
import com.mit.notepad.R;
// Imports com.mit.notepad.activities.AlarmRingingActivity so this file can use that type by its short name.
import com.mit.notepad.activities.AlarmRingingActivity;

/** Plays the device alarm tone until the user dismisses it. */
// Declares class AlarmRingingService to group related state and behavior.
public class AlarmRingingService extends Service {
    // Initializes ACTION_START with the text or query fragment shown here.
    public static final String ACTION_START = "com.mit.notepad.action.START_RINGING";
    // Initializes ACTION_STOP with the text or query fragment shown here.
    public static final String ACTION_STOP = "com.mit.notepad.action.STOP_RINGING";
    // Initializes EXTRA_TITLE with the text or query fragment shown here.
    public static final String EXTRA_TITLE = "ringing_title";
    // Initializes EXTRA_MESSAGE with the text or query fragment shown here.
    public static final String EXTRA_MESSAGE = "ringing_message";
    // Initializes CHANNEL_ID with the text or query fragment shown here.
    private static final String CHANNEL_ID = "np_active_alarm";
    // Initializes NOTIFICATION_ID with 8701 so later statements can use the computed value.
    private static final int NOTIFICATION_ID = 8701;

    // Declares player for use by the surrounding method or class.
    private MediaPlayer player;

    // Applies @Override metadata to the declaration that follows.
    @Override
    // Starts foreground alarm playback for the requested alarm.
    public int onStartCommand(Intent intent, int flags, int startId) {
        // If intent != null && ACTION_STOP.equals(intent.getAction()) is true, { immediately.
        if (intent != null && ACTION_STOP.equals(intent.getAction())) {
            // Calls stopSelf with  to perform the operation shown.
            stopSelf();
            // Returns START_NOT_STICKY to the caller.
            return START_NOT_STICKY;
        // Closes the current method, control-flow, or type block.
        }

        // Initializes title with intent == null ? getString(R.string.notif_alarm) so later statements can use the computed value.
        String title = intent == null ? getString(R.string.notif_alarm)
                // Calls intent.getStringExtra with EXTRA_TITLE to perform the operation shown.
                : intent.getStringExtra(EXTRA_TITLE);
        // Initializes message with the text or query fragment shown here.
        String message = intent == null ? "" : intent.getStringExtra(EXTRA_MESSAGE);
        // If title == null is true, title = getString(R.string.notif_alarm) immediately.
        if (title == null) title = getString(R.string.notif_alarm);
        // If message == null is true, message = "" immediately.
        if (message == null) message = "";

        // Calls createChannel with  to perform the operation shown.
        createChannel();
        // Calls startForeground with NOTIFICATION_ID, makeNotification(title, message to perform the operation shown.
        startForeground(NOTIFICATION_ID, makeNotification(title, message));
        // If player == null || !player.isPlaying() is true, startAlarmTone() immediately.
        if (player == null || !player.isPlaying()) startAlarmTone();
        // Returns START_STICKY to the caller.
        return START_STICKY;
    // Closes the current method, control-flow, or type block.
    }

    // Builds notification.
    private Notification makeNotification(String title, String message) {
        // Initializes screen with new Intent(this, AlarmRingingActivity.class) so later statements can use the computed value.
        Intent screen = new Intent(this, AlarmRingingActivity.class)
                // Calls putExtra on the object from the preceding line with the shown arguments.
                .putExtra(EXTRA_TITLE, title)
                // Calls putExtra on the object from the preceding line with the shown arguments.
                .putExtra(EXTRA_MESSAGE, message)
                // Calls addFlags on the object from the preceding line with the shown arguments.
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // Initializes screenPending with PendingIntent.getActivity(this, 0, screen, so later statements can use the computed value.
        PendingIntent screenPending = PendingIntent.getActivity(this, 0, screen,
                // Executes PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE) as a statement.
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Initializes stop with new Intent(this, AlarmRingingService.class).setAction(ACTION_STOP) so later statements can use the computed value.
        Intent stop = new Intent(this, AlarmRingingService.class).setAction(ACTION_STOP);
        // Initializes stopPending with PendingIntent.getService(this, 1, stop, so later statements can use the computed value.
        PendingIntent stopPending = PendingIntent.getService(this, 1, stop,
                // Executes PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE) as a statement.
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Returns new NotificationCompat.Builder(this, CHANNEL_ID) to the caller.
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                // Calls setSmallIcon on the object from the preceding line with the shown arguments.
                .setSmallIcon(R.drawable.ic_notification)
                // Calls setContentTitle on the object from the preceding line with the shown arguments.
                .setContentTitle(title)
                // Calls setContentText on the object from the preceding line with the shown arguments.
                .setContentText(message)
                // Calls setCategory on the object from the preceding line with the shown arguments.
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                // Calls setPriority on the object from the preceding line with the shown arguments.
                .setPriority(NotificationCompat.PRIORITY_MAX)
                // Calls setOngoing on the object from the preceding line with the shown arguments.
                .setOngoing(true)
                // Calls setVisibility on the object from the preceding line with the shown arguments.
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                // Calls setContentIntent on the object from the preceding line with the shown arguments.
                .setContentIntent(screenPending)
                // Calls setFullScreenIntent on the object from the preceding line with the shown arguments.
                .setFullScreenIntent(screenPending, true)
                // Calls addAction on the object from the preceding line with the shown arguments.
                .addAction(R.drawable.ic_close, getString(R.string.dismiss_alarm), stopPending)
                // Calls build on the object from the preceding line with the shown arguments.
                .build();
    // Closes the current method, control-flow, or type block.
    }

    // Creates channel.
    private void createChannel() {
        // If Build.VERSION.SDK_INT < Build.VERSION_CODES.O is true, return immediately.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return;
        // Initializes manager with getSystemService(NotificationManager.class) so later statements can use the computed value.
        NotificationManager manager = getSystemService(NotificationManager.class);
        // If manager == null || manager.getNotificationChannel(CHANNEL_ID) != null is true, return immediately.
        if (manager == null || manager.getNotificationChannel(CHANNEL_ID) != null) return;
        // Initializes channel with new NotificationChannel(CHANNEL_ID, so later statements can use the computed value.
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                // Calls getString with R.string.channel_active_alarm), NotificationManager.IMPORTANCE_HIGH to perform the operation shown.
                getString(R.string.channel_active_alarm), NotificationManager.IMPORTANCE_HIGH);
        // Calls channel.setDescription with getString(R.string.channel_active_alarm_desc to perform the operation shown.
        channel.setDescription(getString(R.string.channel_active_alarm_desc));
        // Calls channel.setSound with null, null to perform the operation shown.
        channel.setSound(null, null); // Audio is played directly by this service.
        // Calls manager.createNotificationChannel with channel to perform the operation shown.
        manager.createNotificationChannel(channel);
    // Closes the current method, control-flow, or type block.
    }

    // Starts alarm tone.
    private void startAlarmTone() {
        // Initializes tone with RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM) so later statements can use the computed value.
        Uri tone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        // If tone == null is true, tone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION) immediately.
        if (tone == null) tone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        // Starts the operation whose failures are handled by the following catch clauses.
        try {
            // Stores the result of new MediaPlayer() in player for use by later logic.
            player = new MediaPlayer();
            // Calls player.setAudioAttributes with new AudioAttributes.Builder( to perform the operation shown.
            player.setAudioAttributes(new AudioAttributes.Builder()
                    // Calls setUsage on the object from the preceding line with the shown arguments.
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    // Calls setContentType on the object from the preceding line with the shown arguments.
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    // Calls build on the object from the preceding line with the shown arguments.
                    .build());
            // Calls player.setDataSource with this, tone to perform the operation shown.
            player.setDataSource(this, tone);
            // Calls player.setLooping with true to perform the operation shown.
            player.setLooping(true);
            // Starts the callback body that handles the event or asynchronous result.
            player.setOnErrorListener((mp, what, extra) -> {
                // Calls stopSelf with  to perform the operation shown.
                stopSelf();
                // Returns true to the caller.
                return true;
            // Executes }) as a statement.
            });
            // Calls player.prepare with  to perform the operation shown.
            player.prepare();
            // Calls player.start with  to perform the operation shown.
            player.start();
        // Calls catch with Exception e) { to perform the operation shown.
        } catch (Exception e) {
            // Calls releasePlayer with  to perform the operation shown.
            releasePlayer();
            // Calls stopSelf with  to perform the operation shown.
            stopSelf();
        // Closes the current method, control-flow, or type block.
        }
    // Closes the current method, control-flow, or type block.
    }

    // Applies @Override metadata to the declaration that follows.
    @Override
    // Declares onDestroy() to implement this part of the component.
    public void onDestroy() {
        // Calls releasePlayer with  to perform the operation shown.
        releasePlayer();
        // Calls stopForeground with true to perform the operation shown.
        stopForeground(true);
        // Calls super.onDestroy with  to perform the operation shown.
        super.onDestroy();
    // Closes the current method, control-flow, or type block.
    }

    // Declares releasePlayer() to implement this part of the component.
    private void releasePlayer() {
        // If player == null is true, return immediately.
        if (player == null) return;
        // Starts the operation whose failures are handled by the following catch clauses.
        try {
            // If player.isPlaying() is true, player.stop() immediately.
            if (player.isPlaying()) player.stop();
        // Calls catch with IllegalStateException ignored) { } to perform the operation shown.
        } catch (IllegalStateException ignored) { }
        // Calls player.release with  to perform the operation shown.
        player.release();
        // Stores the result of null in player for use by later logic.
        player = null;
    // Closes the current method, control-flow, or type block.
    }

    // Applies @Nullable metadata to the declaration that follows.
    @Nullable
    // Applies @Override metadata to the declaration that follows.
    @Override
    // Returns null because this service is not designed for binding.
    public IBinder onBind(Intent intent) { return null; }
// Closes the current method, control-flow, or type block.
}
