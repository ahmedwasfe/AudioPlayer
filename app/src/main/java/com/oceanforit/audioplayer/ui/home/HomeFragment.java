package com.oceanforit.audioplayer.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.oceanforit.audioplayer.R;
import com.oceanforit.audioplayer.adapter.FragmentsAdapter;
import com.oceanforit.audioplayer.callBack.AudioCallback;
import com.oceanforit.audioplayer.models.AudioModel;
import com.oceanforit.audioplayer.ui.album.AlbumsFragment;
import com.oceanforit.audioplayer.ui.artists.ArtistsFragment;
import com.oceanforit.audioplayer.ui.favorites.FavoritesFragment;
import com.oceanforit.audioplayer.ui.folders.FoldersFragment;
import com.oceanforit.audioplayer.ui.playlists.PlaylistsFragment;
import com.oceanforit.audioplayer.ui.tracks.TracksFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeFragment extends Fragment implements AudioCallback {

    @BindView(R.id.tab_layout)
    TabLayout sTabLayout;
    @BindView(R.id.view_pager)
    ViewPager sViewPager;

    private static HomeFragment instance;
    public static HomeFragment getInstance(){
        return instance == null ? new HomeFragment() : instance;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View layoutView = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, layoutView);
        return layoutView;
    }

    private void initViewPager() {

        sTabLayout.setupWithViewPager(sViewPager);
        FragmentsAdapter fragmentsAdapter = new FragmentsAdapter(getChildFragmentManager(), 0);
//        fragmentsAdapter.addFragment(PlaylistsFragment.getInstance(), getString(R.string.playlists));
        fragmentsAdapter.addFragment(TracksFragment.getInstance(this), getString(R.string.tracks));
        fragmentsAdapter.addFragment(AlbumsFragment.getInstance(), getString(R.string.albums));
        fragmentsAdapter.addFragment(ArtistsFragment.getInstance(), getString(R.string.artists));
        fragmentsAdapter.addFragment(FoldersFragment.getInstance(), getString(R.string.folders));
//        fragmentsAdapter.addFragment(FavoritesFragment.getInstance(), getString(R.string.favorites));
        sViewPager.setAdapter(fragmentsAdapter);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViewPager();
    }

    @Override
    public void onAudioCreate(AudioModel audioModel) {

    }
}
