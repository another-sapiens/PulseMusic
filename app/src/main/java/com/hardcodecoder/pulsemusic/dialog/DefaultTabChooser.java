package com.hardcodecoder.pulsemusic.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hardcodecoder.pulsemusic.Preferences;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.dialog.base.RoundedCustomBottomSheetFragment;
import com.hardcodecoder.pulsemusic.utils.AppSettings;

public class DefaultTabChooser extends RoundedCustomBottomSheetFragment {

    public static final String TAG = DefaultTabChooser.class.getSimpleName();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bsd_choose_default_tab, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        RadioGroup radioGroup = view.findViewById(R.id.radio_button_group);
        int currentDefaultTab = AppSettings.getDefaultTabId(requireContext());

        switch (currentDefaultTab) {
            case Preferences.TAB_HOME:
                ((RadioButton) radioGroup.findViewById(R.id.tab_home)).setChecked(true);
                break;
            case Preferences.TAB_LIBRARY:
                ((RadioButton) radioGroup.findViewById(R.id.tab_library)).setChecked(true);
                break;
            case Preferences.TAB_ALBUMS:
                ((RadioButton) radioGroup.findViewById(R.id.tab_albums)).setChecked(true);
                break;
            case Preferences.TAB_ARTISTS:
                ((RadioButton) radioGroup.findViewById(R.id.tab_artists)).setChecked(true);
                break;
            case Preferences.TAB_PLAYLIST:
                ((RadioButton) radioGroup.findViewById(R.id.tab_playlist)).setChecked(true);
                break;
        }

        view.findViewById(R.id.default_tab_set_btn).setOnClickListener(v1 -> {
            final int checkId = radioGroup.getCheckedRadioButtonId();
            int defaultTabId;
            if (checkId == R.id.tab_library) defaultTabId = Preferences.TAB_LIBRARY;
            else if (checkId == R.id.tab_albums) defaultTabId = Preferences.TAB_ALBUMS;
            else if (checkId == R.id.tab_artists) defaultTabId = Preferences.TAB_ARTISTS;
            else if (checkId == R.id.tab_playlist) defaultTabId = Preferences.TAB_PLAYLIST;
            else defaultTabId = Preferences.TAB_HOME;

            AppSettings.setDefaultTab(requireContext(), defaultTabId);
            dismiss();
        });

        view.findViewById(R.id.default_tab_cancel_btn).setOnClickListener(v -> dismiss());
    }
}