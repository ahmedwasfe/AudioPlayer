package com.oceanforit.audioplayer.ui.tracks;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.oceanforit.audioplayer.HomeActivity;
import com.oceanforit.audioplayer.models.Audio;

import java.util.List;

public class TracksViewModel extends ViewModel {

    private MutableLiveData<List<Audio>> listAudioMutableData;

    public TracksViewModel() {
        if (listAudioMutableData == null)
            listAudioMutableData = new MutableLiveData<>();
    }

    public MutableLiveData<List<Audio>> getListAudioMutableData() {
        if (HomeActivity.listAudios != null)
            listAudioMutableData.setValue(HomeActivity.listAudios);
        return listAudioMutableData;
    }

    public void setListAudioMutableData(MutableLiveData<List<Audio>> listAudioMutableData) {
        this.listAudioMutableData = listAudioMutableData;
    }


}
