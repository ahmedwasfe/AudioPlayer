package com.oceanforit.audioplayer.common;

import android.content.Context;
import android.content.SharedPreferences;

public class SaveSettings {

    private SharedPreferences prefSort;
    private SharedPreferences prefLastPlayed;
    private SharedPreferences prefCurrentProgress;
    private SharedPreferences prefNowPlayingScreen;

    public SaveSettings(Context context){
        prefSort = context.getSharedPreferences(Common.KEY_SORT_PREF, Context.MODE_PRIVATE);
        prefLastPlayed = context.getSharedPreferences(Common.AUDIO_LAST_PLAYED, Context.MODE_PRIVATE);
        prefCurrentProgress = context.getSharedPreferences(Common.AUDIO_CURRENT_PROGRESS, Context.MODE_PRIVATE);
        prefNowPlayingScreen = context.getSharedPreferences(Common.NOW_PLAYING_SCREEN, Context.MODE_PRIVATE);
    }

    // save sort
    public void setSortBy(String sortBy){
        SharedPreferences.Editor editor = prefSort.edit();
        editor.putString(Common.KEY_SORT_BY, sortBy);
        editor.apply();
        editor.commit();
    }

    // get sort
    public String getSortBy(String sortBy){
        return prefSort.getString(Common.KEY_SORT_BY, sortBy);
    }

    // save last paly
    public void saveLastPlayed(String key, String path){
        SharedPreferences.Editor editor = prefLastPlayed.edit();
        editor.putString(key, path);
        editor.apply();
        editor.commit();
    }

    // save last paly
    public void saveLastPlayed(String key, int position){
        SharedPreferences.Editor editor = prefLastPlayed.edit();
        editor.putInt(key, position);
        editor.apply();
        editor.commit();
    }

    // save last paly
    public String getLastPlayed(String key){
        return prefLastPlayed.getString(key, "");
    }

    public int getLastPlayed(String key, int defValue){
        return prefLastPlayed.getInt(key, defValue);
    }

    // save current progress
    public void saveCurrentProgress(String key, int progress){
        SharedPreferences.Editor editor = prefCurrentProgress.edit();
        editor.putInt(key, progress);
        editor.apply();
        editor.commit();
    }

    // get current porogess
    public int getCurrentProgress(String key){
        return prefCurrentProgress.getInt(key, -1);
    }

    public void saveNowPlayingScreen(String screen){

        SharedPreferences.Editor editor = prefNowPlayingScreen.edit();
        editor.putString("PlayingScreen", screen);
        editor.apply();
        editor.commit();
    }

    public String getNowPlayingScreen(){
        return prefNowPlayingScreen.getString("PlayingScreen", "screen1");
    }

}
