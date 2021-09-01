package com.oceanforit.audioplayer.ui.artists;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.oceanforit.audioplayer.HomeActivity;
import com.oceanforit.audioplayer.R;
import com.oceanforit.audioplayer.adapter.ArtistsAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ArtistsFragment extends Fragment {

    @BindView(R.id.recycler_artists)
    RecyclerView recyclerArtists;

    private ArtistsViewModel artistsViewModel;
    private ArtistsAdapter artistsAdapter;

    private static ArtistsFragment instance;

    public static ArtistsFragment getInstance() {
        return instance == null ? new ArtistsFragment() : instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        artistsViewModel = new ViewModelProvider(this).get(ArtistsViewModel.class);
        View layoutView = inflater.inflate(R.layout.fragment_artists, container, false);
        ButterKnife.bind(this, layoutView);
        initUI();
        loadArtists();
        return layoutView;
    }

    private void initUI() {
        loadGridSize(2);
    }

    private void loadGridSize(int spanCount) {
        recyclerArtists.setHasFixedSize(true);
        recyclerArtists.setLayoutManager(new GridLayoutManager(getActivity(), spanCount));
    }

    private void loadArtists() {

        artistsViewModel.getListAudioMutableData()
                .observe(getViewLifecycleOwner(), audios -> {

                        artistsAdapter = new ArtistsAdapter(getActivity(), audios);
                        recyclerArtists.setAdapter(artistsAdapter);

                });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.artists_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_grid_size1:
                loadGridSize(1);
                break;
            case R.id.action_grid_size2:
                loadGridSize(2);
                break;
            case R.id.action_grid_size3:
                loadGridSize(3);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
