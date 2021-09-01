package com.oceanforit.audioplayer.ui.tracks;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.oceanforit.audioplayer.R;
import com.oceanforit.audioplayer.adapter.AudioAdapter;
import com.oceanforit.audioplayer.callBack.AudioCallback;
import com.oceanforit.audioplayer.callBack.IAudioClickListener;
import com.oceanforit.audioplayer.common.Common;
import com.oceanforit.audioplayer.common.SaveSettings;
import com.oceanforit.audioplayer.models.AudioModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TracksFragment extends Fragment implements IAudioClickListener {
    private static final String TAG = TracksFragment.class.getSimpleName();


    @BindView(R.id.relative_tracks)
    RelativeLayout container;
    @BindView(R.id.recycler_tracks)
    RecyclerView recyclerTracks;

    public static AudioAdapter audioAdapter;
    private SaveSettings saveSettings;
    private TracksViewModel tracksViewModel;
    private AudioCallback audioCallback;

    private static TracksFragment instance;

    public TracksFragment() {

    }

    public TracksFragment(AudioCallback audioCallback) {
        this.audioCallback = audioCallback;
    }

    public static TracksFragment getInstance(AudioCallback audioCallback){
        return instance == null ? new TracksFragment(audioCallback) : instance;
    }

    public static TracksFragment getInstance(){
        return instance == null ? new TracksFragment() : instance;
    }

    @OnClick(R.id.iv_sort)
    void onSortClick(){
        showMenu();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        tracksViewModel = new ViewModelProvider(this).get(TracksViewModel.class);
        View layoutView = inflater.inflate(R.layout.fragment_tracks, container, false);
        ButterKnife.bind(this, layoutView);
        initUI();
        loadTracks();
        return layoutView;
    }

    private void initUI() {

        saveSettings = new SaveSettings(getActivity());
        recyclerTracks.setHasFixedSize(true);
        recyclerTracks.setLayoutManager(new LinearLayoutManager(getActivity()));



    }

    private void loadTracks(){

        tracksViewModel.getListAudioMutableData()
                .observe(getViewLifecycleOwner(), audios -> {
                    if (!(audios.size() < 1)){
                        audioAdapter = new AudioAdapter(getActivity(), audios, this);
                        recyclerTracks.setAdapter(audioAdapter);
                    }
                });

    }

    private void showMenu(){

        PopupMenu menu = new PopupMenu(getActivity(), container);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            menu.setGravity(Gravity.CENTER_HORIZONTAL);
        }
        menu.getMenuInflater().inflate(R.menu.sort_memu, menu.getMenu());
        menu.show();
        menu.setOnMenuItemClickListener(item -> {

            switch (item.getItemId()) {
                case R.id.action_name:
                    saveSettings.setSortBy(Common.SORT_BY_NAME);
                    getActivity().recreate();
                    break;
                case R.id.action_date:
                    saveSettings.setSortBy(Common.SORT_BY_DATE);
                    getActivity().recreate();
                    break;
                case R.id.action_size:
                    saveSettings.setSortBy(Common.SORT_BY_SIZE);
                    getActivity().recreate();
                    break;
                case R.id.action_artist:
                    saveSettings.setSortBy(Common.SORT_BY_ARTIST);
                    getActivity().recreate();
                    break;
            }

            return true;
        });
    }

    @Override
    public void onAudioClickListener(View view, AudioModel audioModel) {
        Log.d(TAG, "onAudioClickListener: " + "Position: " + audioModel.getPosition() + " Title: " + audioModel.getListAudio().get(audioModel.getPosition()).getTitle());
        audioCallback.onAudioCreate(audioModel);
    }
}
