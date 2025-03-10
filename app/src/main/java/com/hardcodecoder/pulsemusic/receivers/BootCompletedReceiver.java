package com.hardcodecoder.pulsemusic.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.hardcodecoder.pulsemusic.service.AudioDeviceService;
import com.hardcodecoder.pulsemusic.utils.AppSettings;

public class BootCompletedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent eventIntent) {
        String action = eventIntent.getAction();
        if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
            boolean startService = AppSettings.isBluetoothAutoPlayEnabled(context);
            if (startService)
                ContextCompat.startForegroundService(context, new Intent(context, AudioDeviceService.class));
        }
    }
}