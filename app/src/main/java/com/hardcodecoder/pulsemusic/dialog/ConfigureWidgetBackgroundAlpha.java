package com.hardcodecoder.pulsemusic.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.dialog.base.RoundedCustomBottomSheetFragment;
import com.hardcodecoder.pulsemusic.utils.AppSettings;
import com.hardcodecoder.pulsemusic.views.ValueSlider;
import com.hardcodecoder.pulsemusic.widgets.PulseWidgetsHelper;

public class ConfigureWidgetBackgroundAlpha extends RoundedCustomBottomSheetFragment {

    public static final String TAG = ConfigureWidgetBackgroundAlpha.class.getSimpleName();

    @NonNull
    public static ConfigureWidgetBackgroundAlpha getInstance() {
        return new ConfigureWidgetBackgroundAlpha();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bsd_configure_widget_background_alpha, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        final int currentAlphaPercent = AppSettings.getWidgetBackgroundAlpha(requireContext());

        ValueSlider alphaSlider = view.findViewById(R.id.widget_alpha_slider);
        alphaSlider.setSliderValue(currentAlphaPercent);

        view.findViewById(R.id.set_btn).setOnClickListener(v -> {
            AppSettings.setWidgetBackgroundAlpha(requireContext(), alphaSlider.getSliderValue());
            if (AppSettings.isWidgetEnable(requireContext()))
                PulseWidgetsHelper.notifyWidgets(requireContext());
            dismiss();
        });

        view.findViewById(R.id.cancel_btn).setOnClickListener(v -> dismiss());
    }
}