package com.oceanforit.audioplayer.common;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.snackbar.Snackbar;
import com.oceanforit.audioplayer.R;
import com.oceanforit.audioplayer.models.Audio;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Common {

    private static final String TAG = Common.class.getSimpleName();


    public static final int REQUEST_PERMISSION_CODE = 1;
    public static final int DELAY_MILLIS = 1000;

    public static final String PACKAGE_NAME = "com.oceanforit.audioplayer";
    public static final String ACTION_QUIT = PACKAGE_NAME + ".quitservice";
    public static final String EXPAND_PANEL = "expand_panel";
    // Keys
    public static final String KEY_POSITION = "position";
    public static final String KEY_FOLDER_NAME = "folder_name";
    public static final String KEY_AUDIO = "audio";
    public static final String KEY_ALBUM_DETAILS = "albumDetails";
    public static final String ALBUM_DETAILS = "Album_Details";
    public static final String KEY_FOLDER_DETAILS = "folderDetails";
    public static final String FOLDER_DETAILS = "Folder_Details";
    public static final String KEY_SERVICE_POSITION = "servicePosition";

    // Settings
    public static final String KEY_SORT_PREF = "SortOrder";
    public static final String KEY_SORT_BY = "SortBy";
    public static final String SORT_BY_NAME = "sort_name";
    public static final String SORT_BY_DATE = "sort_date";
    public static final String SORT_BY_SIZE = "sort_size";
    public static final String SORT_BY_ARTIST = "sort_artist";
    public static final String AUDIO_CURRENT_PROGRESS = "Current_Progress";
    public static final String NOW_PLAYING_SCREEN = "Playing_Screen";
    public static final String KEY_CURRENT_PROGRESS = "CurrentProgress";
    // Now Playing
    public static final String AUDIO_LAST_PLAYED = "Last_Played";
    public static final String AUDIO_FILE = "Stored_Audio";
    public static final String AUDIO_NAME = "Audio_Name";
    public static final String ARTIST_NAME = "Artist_Name";
    public static final String AUDIO_NOW_POSITION = "Audio_Position";

    // Notificatio & Service
    public static final String CHANNEL_ID_1 = "channel1";
    public static final String CHANNEL_ID_2 = "channel2";

    // Actions
    public static final String ACTION_PREVIOUS = "action_previous";
    public static final String ACTION_PLAY = "action_play";
    public static final String ACTION_NEXT = "action_next";

    public static final String ACTION_PLAYER = "action_player";
    // Brodcast
    public static final String ACTION_NAME = "ActionName";

    public static byte[] getAlbumArt(String uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(uri);
        } catch (Exception e) {
            e.printStackTrace();
        }
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();

        return art;
    }

    public static int getRandomAudio(int i) {
        Random random = new Random();
        return random.nextInt(i + 1);
    }

    public static void showSnackBar(@NonNull Context context, @NonNull int layout, View view, @NonNull String message) {

        Snackbar mSnackBar = Snackbar.make(view, "", Snackbar.LENGTH_LONG);

        View snackView = LayoutInflater.from(context)
                .inflate(layout, null);

        mSnackBar.getView().setBackgroundColor(Color.TRANSPARENT);
        Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) mSnackBar.getView();
        snackbarLayout.setPadding(0,0,0,0);

        try {
            FrameLayout.LayoutParams mFrameParams = (FrameLayout.LayoutParams) mSnackBar.getView().getLayoutParams();
            mFrameParams.gravity = Gravity.BOTTOM;
            mSnackBar.getView().setLayoutParams(mFrameParams);
        }catch (Exception e){
            e.printStackTrace();
        }

        TextView tvMessage = snackView.findViewById(R.id.tv_message);
        tvMessage.setText(message);

        snackbarLayout.addView(snackView,0);
        mSnackBar.show();
    }

}
