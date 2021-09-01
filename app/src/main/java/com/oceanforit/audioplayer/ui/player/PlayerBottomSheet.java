package com.oceanforit.audioplayer.ui.player;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.oceanforit.audioplayer.R;

import butterknife.ButterKnife;

public class PlayerBottomSheet extends BottomSheetBehavior {

    private static PlayerBottomSheet instance;
    public static PlayerBottomSheet getInstance(){
        return instance == null ? new PlayerBottomSheet() : instance;
    }
}
