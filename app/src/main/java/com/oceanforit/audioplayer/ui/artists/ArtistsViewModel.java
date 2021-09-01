package com.oceanforit.audioplayer.ui.artists;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.oceanforit.audioplayer.HomeActivity;
import com.oceanforit.audioplayer.callBack.LoadAudiosListener;
import com.oceanforit.audioplayer.models.Audio;

import java.util.List;

public class ArtistsViewModel extends ViewModel implements LoadAudiosListener {

    private MutableLiveData<List<Audio>> listAudioMutableData;
    private MutableLiveData<String> errorMutableData;
    private LoadAudiosListener loadAudiosListener;

    public ArtistsViewModel() {
        if (listAudioMutableData == null)
            listAudioMutableData = new MutableLiveData<>();

        loadAudiosListener = this;
    }

    public MutableLiveData<List<Audio>> getListAudioMutableData() {
        loadArtists();
        return listAudioMutableData;
    }

    private void loadArtists(){
        if (!(HomeActivity.listArtists.size() < 1))
            loadAudiosListener.onLoadAudiosSuccess(HomeActivity.listArtists);
    }

    @Override
    public void onLoadAudiosSuccess(List<Audio> listAudios) {
        listAudioMutableData.setValue(listAudios);
    }

    @Override
    public void onLoadAudiosFailed(String messageError) {
        errorMutableData.setValue(messageError);
    }
}
