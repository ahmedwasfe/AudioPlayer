package com.oceanforit.audioplayer.ui.playlists;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.oceanforit.audioplayer.R;

import butterknife.ButterKnife;

public class PlaylistsFragment extends Fragment {


    private static PlaylistsFragment instance;
    public static PlaylistsFragment getInstance(){
        return instance == null ? new PlaylistsFragment() : instance;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View layoutView = inflater.inflate(R.layout.fragment_playlists, container, false);
        ButterKnife.bind(this, layoutView);
        return layoutView;
    }

}
