package com.oceanforit.audioplayer.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.AudioEffect;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.text.Html;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.oceanforit.audioplayer.HomeActivity;
import com.oceanforit.audioplayer.PlayerActivity;
import com.oceanforit.audioplayer.R;
import com.oceanforit.audioplayer.callBack.ActionPlayingListener;
import com.oceanforit.audioplayer.common.Common;
import com.oceanforit.audioplayer.common.SaveSettings;
import com.oceanforit.audioplayer.models.Audio;

import java.util.ArrayList;
import java.util.List;

public class AudioService extends Service implements MediaPlayer.OnCompletionListener {
    private static final String TAG = AudioService.class.getSimpleName();

    private final int NOTIFICATION_ID = 88;

    IBinder sBinder = new MyBinder();
    public static MediaPlayer mediaPlayer;
    public static List<Audio> listAudios = new ArrayList<>();
    private Uri uri;
    public static int position = -1;

    private AudioManager audioManager;
    private ActionPlayingListener actionPlaying;
    private MediaSessionCompat mediaSessionCompat;
    private SaveSettings saveSettings;

    @Override
    public void onCreate() {
        super.onCreate();
        listAudios = PlayerActivity.listAudio;
        mediaSessionCompat = new MediaSessionCompat(getApplicationContext(), getString(R.string.app_name));
        saveSettings = new SaveSettings(getApplicationContext());

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: " + "onBindMethod");
        return sBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        int servicePostion = intent.getIntExtra(Common.KEY_SERVICE_POSITION, -1);
        Log.e(TAG, "onStartCommand: " + servicePostion);
        String actionName = intent.getStringExtra(Common.ACTION_NAME);
        if (servicePostion != -1)
            playMedia(servicePostion);
        if (actionName != null) {
            switch (actionName) {
                case "playPause":
                    playPauseAudio();
                    break;
                case "next":
                    nextAudio();
                    break;
                case "previous":
                    previousAudio();
                    break;
                case "quit":
//                    quit();
                    break;
            }
        }
        return START_STICKY;
    }

    private void playMedia(int startPosition) {

        listAudios = PlayerActivity.listAudio;

        position = startPosition;
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            if (listAudios != null) {
                createMediaPlayer(position);
                mediaPlayer.start();
            }
        } else {
            createMediaPlayer(position);
            mediaPlayer.start();
        }

    }

    private void quit() {
        pause();
        Intent audioEffectIntent = new Intent(AudioEffect.ACTION_CLOSE_AUDIO_EFFECT_CONTROL_SESSION);
        if (mediaPlayer != null)
            audioEffectIntent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, mediaPlayer.getAudioSessionId());
        audioEffectIntent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, getPackageName());
        sendBroadcast(audioEffectIntent);
        getAudioManager().abandonAudioFocus(audioFocusChangeListener);
        stopSelf();
    }

    private AudioManager getAudioManager() {
        if (audioManager == null)
            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        return audioManager;
    }

    private final AudioManager.OnAudioFocusChangeListener audioFocusChangeListener
            = focusChange -> new Handler().obtainMessage(1, focusChange, 0)
            .sendToTarget();

    public class MyBinder extends Binder {
        public AudioService getService() {
            return AudioService.this;
        }
    }

    public void start() {
        mediaPlayer.start();
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    public void pause() {
        mediaPlayer.pause();
    }

    public void stop() {
        mediaPlayer.stop();
    }

    public void release() {
        mediaPlayer.release();
    }

    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    public void seekTo(int position) {
        mediaPlayer.seekTo(position);
    }

    public void setVolume(float leftVolume, float rightVolume) {
        mediaPlayer.setVolume(leftVolume, rightVolume);
    }

    public void createMediaPlayer(int positionInner) {
        position = positionInner;
        uri = Uri.parse(listAudios.get(position).getPath());
        saveSettings.saveLastPlayed(Common.AUDIO_FILE, uri.toString());
        saveSettings.saveLastPlayed(Common.AUDIO_NAME, listAudios.get(position).getTitle());
        saveSettings.saveLastPlayed(Common.ARTIST_NAME, listAudios.get(position).getArtist());
        saveSettings.saveLastPlayed(Common.AUDIO_NOW_POSITION, position);
        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
    }

    public void onCompletion() {
        mediaPlayer.setOnCompletionListener(this);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (actionPlaying != null) {
            actionPlaying.nextAudio();
            if (mediaPlayer != null) {
                createMediaPlayer(position);
                start();
                onCompletion();
            }
        }

    }

    public void setCallBack(ActionPlayingListener actionPlaying) {
        this.actionPlaying = actionPlaying;
    }

    public void showNotifcation(int btnPlayPause) {

        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra(Common.EXPAND_PANEL, true);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Previous button
        Intent prevIntent = new Intent(this, NotificationReceiver.class)
                .setAction(Common.ACTION_PREVIOUS);
        PendingIntent prevPending = PendingIntent.getBroadcast(this, 0,
                prevIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Play Pause button
        Intent pauseIntent = new Intent(this, NotificationReceiver.class)
                .setAction(Common.ACTION_PLAY);
        PendingIntent pausePending = PendingIntent.getBroadcast(this, 0,
                pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Next button
        Intent nextIntent = new Intent(this, NotificationReceiver.class)
                .setAction(Common.ACTION_NEXT);
        PendingIntent nextPending = PendingIntent.getBroadcast(this, 0,
                nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Delete
        Intent deleteIntent = new Intent().setAction(Common.ACTION_QUIT);
        deleteIntent.setComponent(new ComponentName(this, AudioService.class));
        PendingIntent deletePending = PendingIntent.getService(this, 0, deleteIntent, 0);

        byte[] picture = null;
        picture = Common.getAlbumArt(listAudios.get(position).getPath());
        Bitmap thumb = null;
        if (picture != null)
            thumb = BitmapFactory.decodeByteArray(picture, 0, picture.length);
        else
            thumb = BitmapFactory.decodeResource(getResources(), R.drawable.icon_white);

        int max = Integer.parseInt(listAudios.get(position).getDuration()) / 1000;
        int progress = getCurrentPosition() / 1000;

        Notification notification = new NotificationCompat.Builder(this, Common.CHANNEL_ID_2)
                .setSmallIcon(R.drawable.icon_white)
                .setLargeIcon(thumb)
                .setContentTitle(listAudios.get(position).getTitle())
                .setContentText(listAudios.get(position).getArtist())
                .setSubText(Html.fromHtml("<br>" + listAudios.get(position).getAlbum() + "<br>"))
                .setAutoCancel(true)
                .setNotificationSilent()
                .setOngoing(isPlaying())
                .setShowWhen(false)
                .setProgress(max, progress, false)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .addAction(R.drawable.ic_skip_previous, "Previous", prevPending)
                .addAction(btnPlayPause, "Pause", pausePending)
                .addAction(R.drawable.ic_skip_next, "Next", nextPending)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSessionCompat.getSessionToken()))
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setContentIntent(contentIntent)
                .setOnlyAlertOnce(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .build();

        startForeground(NOTIFICATION_ID, notification);

//        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        manager.notify(1, notification);
    }

    public void playPauseAudio(){
        if (actionPlaying != null)
            actionPlaying.playPauseAudio();
    }

    public void nextAudio() {
        if (actionPlaying != null)
            actionPlaying.nextAudio();
    }

    public void previousAudio(){
        if (actionPlaying != null)
            actionPlaying.previousAudio();
    }
}
