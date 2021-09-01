package com.oceanforit.audioplayer.ui.favorites;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.audiofx.AudioEffect;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.oceanforit.audioplayer.R;
import com.oceanforit.audioplayer.adapter.FavoriteAdapter;
import com.oceanforit.audioplayer.favorite.FavDataSource;
import com.oceanforit.audioplayer.favorite.FavoriteDataSource;
import com.oceanforit.audioplayer.favorite.FavoriteDatabase;
import com.oceanforit.audioplayer.service.AudioService;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavoritesFragment extends Fragment {
    private static final String TAG = FavoritesFragment.class.getSimpleName();

    @BindView(R.id.recycler_favorite)
    RecyclerView recyclerFav;

    private FavoriteDataSource favoriteDataSource;

    private static FavoritesFragment instance;

    public static FavoritesFragment getInstance() {
        return instance == null ? new FavoritesFragment() : instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View layoutView = inflater.inflate(R.layout.fragment_favorites, container, false);
        ButterKnife.bind(this, layoutView);
        initUI();
        getAllFavorites();

        return layoutView;
    }

    private void getAllFavorites() {

        favoriteDataSource.getAllFavoritesAudios()
                .subscribe(favoriteAudios -> {
                    if (favoriteAudios.size() > 0) {
                        FavoriteAdapter favoriteAdapter = new FavoriteAdapter(getActivity(), favoriteAudios);
                        recyclerFav.setAdapter(favoriteAdapter);
                    }
                }, throwable -> {
                    Log.e(TAG, "getAllFavorites: " + throwable.getMessage());
                });
    }

    private void initUI() {

        favoriteDataSource = new FavDataSource(FavoriteDatabase.getInstance(getActivity()).favoriteDAO());

        recyclerFav.setHasFixedSize(true);
        recyclerFav.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.more_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_delete:
                item.setVisible(false);
                break;
            case R.id.action_favorite:
                item.setVisible(false);
                break;
            case R.id.action_remove_favorite:
                showDialogClearFavorite();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showDialogClearFavorite() {

        new AlertDialog.Builder(getActivity())
                .setTitle("Clear Favorites")
                .setMessage("are you sure clear all favorites")
                .setNegativeButton("NO", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("CLEAR", (dialog, which) -> {
                    clearFavorite();
                }).show();
    }

    private void clearFavorite() {

        favoriteDataSource.clearFavorite()
                .subscribe(integer -> {
                    getAllFavorites();
                    Toast.makeText(getActivity(), "Clear success", Toast.LENGTH_SHORT).show();
                }, throwable -> {
                    Log.e(TAG, "clearFavorite: " + throwable.getMessage());
                });
    }
}