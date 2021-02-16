package com.hardcodecoder.pulsemusic.helper;

import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.media.session.PlaybackState;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MediaProgressUpdateHelper extends Handler {

    private static final int CMD_REFRESH_PROGRESS = 18;
    private static final long DEFAULT_UPDATE_INTERVAL_MILLS = 1000;
    private final MediaController mController;
    private final Callback mCallback;
    private final long mUpdateIntervalMills;
    private final MediaController.Callback controllerCallback = new MediaController.Callback() {
        @Override
        public void onPlaybackStateChanged(@Nullable PlaybackState state) {
            mCallback.onPlaybackStateChanged(state);
            if (state != null && state.getState() == PlaybackState.STATE_PLAYING) start();
            else stop();
        }

        @Override
        public void onMetadataChanged(@Nullable MediaMetadata metadata) {
            mCallback.onMetadataDataChanged(metadata);
        }
    };

    public MediaProgressUpdateHelper(@NonNull MediaController controller, @NonNull Callback callback, long updateIntervalMills) {
        super(Looper.getMainLooper());
        mController = controller;
        mCallback = callback;
        mUpdateIntervalMills = updateIntervalMills;
        mController.registerCallback(controllerCallback);

        // Make a callbacks so that the receiver initialises itself with current data
        post(() -> mCallback.onMetadataDataChanged(mController.getMetadata()));
        final PlaybackState state = mController.getPlaybackState();
        post(() -> mCallback.onPlaybackStateChanged(state));
        post(() -> {
            if (null != state) {
                mCallback.onProgressValueChanged(state.getPosition());
                if (state.getState() == PlaybackState.STATE_PLAYING)
                    start();
            }
        });
    }

    public MediaProgressUpdateHelper(@NonNull MediaController controller, @NonNull Callback callback) {
        this(controller, callback, DEFAULT_UPDATE_INTERVAL_MILLS);
    }

    private void start() {
        queueNextRefresh(mUpdateIntervalMills);
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        super.handleMessage(msg);
        if (msg.what == CMD_REFRESH_PROGRESS) {
            if (null != mController.getPlaybackState())
                mCallback.onProgressValueChanged(mController.getPlaybackState().getPosition());
            queueNextRefresh(mUpdateIntervalMills);
        }
    }

    private void queueNextRefresh(final long delay) {
        final Message message = obtainMessage(CMD_REFRESH_PROGRESS);
        removeMessages(CMD_REFRESH_PROGRESS);
        sendMessageDelayed(message, delay);
    }

    public void stop() {
        removeMessages(CMD_REFRESH_PROGRESS);
    }

    public void destroy() {
        stop();
        mController.unregisterCallback(controllerCallback);
    }

    public interface Callback {
        void onMetadataDataChanged(MediaMetadata metadata);

        void onPlaybackStateChanged(PlaybackState state);

        void onProgressValueChanged(long progress);
    }
}