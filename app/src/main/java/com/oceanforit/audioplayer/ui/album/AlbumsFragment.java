package com.oceanforit.audioplayer.ui.album;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.oceanforit.audioplayer.HomeActivity;
import com.oceanforit.audioplayer.R;
import com.oceanforit.audioplayer.adapter.AlbumAdapter;
import com.oceanforit.audioplayer.adapter.AudioAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AlbumsFragment extends Fragment {
    private static final String TAG = AlbumsFragment.class.getSimpleName();

    @BindView(R.id.recycler_album)
    RecyclerView recyclerAlbum;

    private AlbumAdapter albumAdapter;

    private static AlbumsFragment instance;
    public static AlbumsFragment getInstance(){
        return instance == null ? new AlbumsFragment() : instance;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View layoutView = inflater.inflate(R.layout.fragment_albums, container, false);
        ButterKnife.bind(this, layoutView);
        initUI();
        loadAlbums();
        return layoutView;
    }

    private void initUI() {

        recyclerAlbum.setHasFixedSize(true);
        recyclerAlbum.setLayoutManager(new GridLayoutManager(getActivity(), 2));
    }

    private void loadAlbums(){

        if (!(HomeActivity.listAlbums.size() < 1)){
            albumAdapter = new AlbumAdapter(getActivity(), HomeActivity.listAlbums);
            recyclerAlbum.setAdapter(albumAdapter);
        }
    }
}
