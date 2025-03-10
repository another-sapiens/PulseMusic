package com.hardcodecoder.pulsemusic.widgets;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.session.MediaController;
import android.media.session.PlaybackState;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.hardcodecoder.pulsemusic.activities.main.SplashActivity;
import com.hardcodecoder.pulsemusic.playback.PulseController;
import com.hardcodecoder.pulsemusic.playback.PulseController.PulseRemote;
import com.hardcodecoder.pulsemusic.service.PMS;
import com.hardcodecoder.pulsemusic.utils.AppSettings;
import com.hardcodecoder.pulsemusic.utils.LogUtils;
import com.hardcodecoder.pulsemusic.utils.PulseUtil;

public class PulseWidgetControlReceiver extends BroadcastReceiver {

    public static final String TAG = PulseWidgetControlReceiver.class.getSimpleName();
    public static final String ACTION_PLAY_PAUSE = "com.hardcodecoder.pulsemusic.widgets:play_pause";
    public static final String ACTION_SKIP_NEXT = "com.hardcodecoder.pulsemusic.widgets:skip_next";
    public static final String ACTION_SKIP_PREV = "com.hardcodecoder.pulsemusic.widgets:skip_prev";

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        if (intent.getAction() == null) return;
        PulseController pulseController = PulseController.getInstance();
        PulseRemote remote = pulseController.getRemote();
        switch (intent.getAction()) {
            case ACTION_SKIP_NEXT:
                remote.skipToNextTrack();
                break;
            case ACTION_SKIP_PREV:
                remote.skipToPreviousTrack();
                break;
            case ACTION_PLAY_PAUSE:
                MediaController controller = pulseController.getController();
                if (null == controller || null == controller.getPlaybackState()) {
                    if (!PulseUtil.isStoragePermissionGranted(context)) {
                        // We don't have appropriate permissions
                        // Let user grant required permissions
                        Intent launchAppIntent = new Intent(context, SplashActivity.class);
                        launchAppIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        launchAppIntent.setAction(PMS.ACTION_PLAY_CONTINUE);
                        launchAppIntent.putExtra(PMS.KEY_PLAY_CONTINUE, AppSettings.getWidgetPlayAction(context));
                        context.startActivity(launchAppIntent);
                    } else {
                        // We have permissions. Start service
                        Intent serviceIntent = new Intent(context, PMS.class);
                        serviceIntent.setAction(PMS.ACTION_PLAY_CONTINUE);
                        serviceIntent.putExtra(PMS.KEY_PLAY_CONTINUE, AppSettings.getWidgetPlayAction(context));
                        ContextCompat.startForegroundService(context, serviceIntent);
                    }
                    return;
                }
                PlaybackState state = controller.getPlaybackState();
                if (state.getState() == PlaybackState.STATE_PLAYING) remote.pause();
                else remote.play();
                break;
            default:
                LogUtils.logInfo(TAG, "onReceived: " + intent.getAction());
        }
    }
}