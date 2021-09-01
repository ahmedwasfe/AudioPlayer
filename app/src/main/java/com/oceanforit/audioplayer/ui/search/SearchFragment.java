package com.oceanforit.audioplayer.ui.search;

import static com.oceanforit.audioplayer.HomeActivity.listAudios;
import static com.oceanforit.audioplayer.ui.tracks.TracksFragment.audioAdapter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.oceanforit.audioplayer.R;
import com.oceanforit.audioplayer.adapter.AudioAdapter;
import com.oceanforit.audioplayer.models.Audio;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchFragment extends Fragment {

    @BindView(R.id._search_view)
    SearchView searchView;
    @BindView(R.id.recycler_search)
    RecyclerView recyclerPlaylist;

    private static SearchFragment instance;

    public static SearchFragment getInstance() {
        return instance == null ? new SearchFragment() : instance;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View layoutView = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.bind(this, layoutView);

        recyclerPlaylist.setLayoutManager(new LinearLayoutManager(getActivity()));
        audioAdapter = new AudioAdapter(getActivity(), listAudios, null);
        recyclerPlaylist.setAdapter(audioAdapter);
        searchView.setFocusable(false);
        searchView.setOnQueryTextListener(new SearchQueryListener());

        return layoutView;
    }

    class SearchQueryListener implements SearchView.OnQueryTextListener {


        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            String inputUser = newText.toLowerCase();
            List<Audio> audios = new ArrayList<>();
            for (Audio audio : listAudios) {
                if (audio.getTitle().toLowerCase().contains(inputUser)) {
                    audios.add(audio);
                }
            }
            audioAdapter.updateList(audios);
            audioAdapter = new AudioAdapter(getActivity(), audios, null);
            recyclerPlaylist.setAdapter(audioAdapter);
            return false;
        }

    }
}
