package com.hardcodecoder.pulsemusic.shortcuts;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.hardcodecoder.pulsemusic.service.PMS;
import com.hardcodecoder.pulsemusic.shortcuts.types.LatestShortcutType;
import com.hardcodecoder.pulsemusic.shortcuts.types.ShuffleShortcutType;
import com.hardcodecoder.pulsemusic.shortcuts.types.SuggestedShortcutType;
import com.hardcodecoder.pulsemusic.utils.PulseUtil;

@RequiresApi(api = Build.VERSION_CODES.N_MR1)
public class ShortcutsLauncher extends Activity {

    public static final String KEY_SHORTCUT_TYPE = "com.hardcodecoder.pulsemusic.shortcuts.Type";
    public static final int SHORTCUT_TYPE_NONE = -1;
    public static final int SHORTCUT_TYPE_SHUFFLE = 0;
    public static final int SHORTCUT_TYPE_LATEST = 1;
    public static final int SHORTCUT_TYPE_SUGGESTED = 2;
    private static final String TAG = "ShortcutsLauncher";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPermission();
    }

    private void startShortcutAction() {
        int shortcutType;
        if (null != getIntent() && (shortcutType = getIntent().getIntExtra(KEY_SHORTCUT_TYPE, SHORTCUT_TYPE_NONE)) != SHORTCUT_TYPE_NONE) {
            switch (shortcutType) {
                case SHORTCUT_TYPE_SHUFFLE:
                    AppShortcutsManager.reportShortcutUsed(this, ShuffleShortcutType.getId());
                    startServiceWithAction(PMS.DEFAULT_ACTION_PLAY_SHUFFLE);
                    break;
                case SHORTCUT_TYPE_LATEST:
                    AppShortcutsManager.reportShortcutUsed(this, LatestShortcutType.getId());
                    startServiceWithAction(PMS.DEFAULT_ACTION_PLAY_LATEST);
                    break;
                case SHORTCUT_TYPE_SUGGESTED:
                    AppShortcutsManager.reportShortcutUsed(this, SuggestedShortcutType.getId());
                    startServiceWithAction(PMS.DEFAULT_ACTION_PLAY_SUGGESTED);
                    break;
                default:
                    Log.e(TAG, "Unknown shortcut");
            }
        }
        finish();
    }

    private void startServiceWithAction(int action) {
        Intent intent = new Intent(this, PMS.class);
        intent.setAction(PMS.ACTION_DEFAULT_PLAY);
        intent.putExtra(PMS.KEY_DEFAULT_PLAY, action);
        startService(intent);
    }

    private void getPermission() {
        if (PulseUtil.isStoragePermissionGranted(this)) {
            startShortcutAction();
        } else {
            PulseUtil.getStoragePermission(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PulseUtil.STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.length >= 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startShortcutAction();
            } else {
                // Permission was not granted
                Toast.makeText(this, "App needs to access device storage to work", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }
}