package com.oceanforit.audioplayer.ui.settings;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.oceanforit.audioplayer.R;
import com.oceanforit.audioplayer.common.SaveSettings;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsFragment extends Fragment {


    private SaveSettings saveSettings;

    @OnClick(R.id.txt_now_playing_screen)
    void onNowPlayingScreenClick() {

        showNowPlayingScreenDialog();
    }

    private void showNowPlayingScreenDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_select_now_playing_screen, null);
        builder.setView(dialogView);

        TextView screen1 = dialogView.findViewById(R.id.txt_playing_screen1);
        TextView screen2 = dialogView.findViewById(R.id.txt_playing_screen2);
        AlertDialog dialog = builder.create();
        // custom dialog
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.CENTER);


        screen1.setOnClickListener(v -> {
            saveSettings.saveNowPlayingScreen("screen1");
        });

        screen2.setOnClickListener(v -> {
            saveSettings.saveNowPlayingScreen("screen2");
        });

        dialog.show();
    }

    private static SettingsFragment instance;

    public static SettingsFragment getInstance() {
        return instance == null ? new SettingsFragment() : instance;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View layoutView = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, layoutView);
        initUI();
        return layoutView;
    }

    private void initUI() {

        saveSettings = new SaveSettings(getActivity());
    }
}
