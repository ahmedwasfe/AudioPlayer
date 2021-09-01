package com.oceanforit.audioplayer.models;

import java.util.List;

public class AudioModel {

    private List<Audio> listAudio;
    private int position;

    public AudioModel() {
    }

    public List<Audio> getListAudio() {
        return listAudio;
    }

    public void setListAudio(List<Audio> listAudio) {
        this.listAudio = listAudio;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
