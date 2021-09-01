package com.oceanforit.audioplayer.callBack;

import com.oceanforit.audioplayer.models.Audio;

import java.util.List;

public interface LoadAudiosListener {

    void onLoadAudiosSuccess(List<Audio> listAudios);
    void onLoadAudiosFailed(String messageError);
}
