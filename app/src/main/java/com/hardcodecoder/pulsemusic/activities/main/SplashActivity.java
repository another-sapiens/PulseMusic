package com.hardcodecoder.pulsemusic.activities.main;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.hardcodecoder.pulsemusic.Preferences;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.TaskRunner;
import com.hardcodecoder.pulsemusic.activities.base.ThemeActivity;
import com.hardcodecoder.pulsemusic.providers.ProviderManager;
import com.hardcodecoder.pulsemusic.service.PMS;
import com.hardcodecoder.pulsemusic.shortcuts.AppShortcutsManager;
import com.hardcodecoder.pulsemusic.themes.TintHelper;
import com.hardcodecoder.pulsemusic.utils.AppSettings;
import com.hardcodecoder.pulsemusic.utils.PulseUtil;

public class SplashActivity extends ThemeActivity {

    private final Handler mHandler = TaskRunner.getMainHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ProviderManager.init(this);
        TintHelper.setAccentTintTo((ImageView) findViewById(R.id.splash_logo));
        getPermission();
    }

    private void getPermission() {
        if (PulseUtil.isStoragePermissionGranted(this)) {
            doStartUpInitialization();
            startPulse();
        } else {
            PulseUtil.getStoragePermission(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PulseUtil.STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.length >= 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                doStartUpInitialization();
                startPulse();
            } else {
                // Permission was not granted
                Toast.makeText(this, getString(R.string.toast_requires_storage_access), Toast.LENGTH_LONG).show();
                mHandler.postDelayed(this::finish, 1500);
            }
        }
    }

    private void doStartUpInitialization() {
        TaskRunner.executeAsync(() -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                // Initialize app shortcuts
                AppShortcutsManager manager = new AppShortcutsManager(getApplicationContext());
                manager.initDynamicShortcuts(false);
            }

            if (AppSettings.isFirstRun(this)) {
                AppSettings.setPlaylistSectionEnabled(this, Preferences.KEY_HOME_PLAYLIST_TOP_ALBUMS, true);
                AppSettings.setPlaylistSectionEnabled(this, Preferences.KEY_HOME_PLAYLIST_FOR_YOU, true);
                AppSettings.setPlaylistSectionEnabled(this, Preferences.KEY_HOME_PLAYLIST_NEW_IN_LIBRARY, true);
                AppSettings.setFirstRun(this, false);
            }
        });
    }

    private void startPulse() {
        mHandler.postDelayed(() -> {
            Intent intent = getIntent();

            Intent mainActivityIntent = new Intent(SplashActivity.this, MainContentActivity.class);
            Intent serviceIntent = new Intent(this, PMS.class);
            Uri uri = intent.getData();
            if (uri != null) {
                mainActivityIntent.setAction(MainContentActivity.ACTION_PLAY_FROM_URI);
                mainActivityIntent.putExtra(MainContentActivity.TRACK_URI, uri.toString());
            } else if (intent.getAction().equals(PMS.ACTION_PLAY_CONTINUE)) {
                serviceIntent.setAction(PMS.ACTION_PLAY_CONTINUE);
                serviceIntent.putExtra(PMS.KEY_PLAY_CONTINUE, intent.getIntExtra(PMS.KEY_PLAY_CONTINUE, PMS.DEFAULT_ACTION_PLAY_NONE));

            }
            startActivity(mainActivityIntent);
            startService(serviceIntent);
            finish();
        }, 400);
    }
}