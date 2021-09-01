package com.oceanforit.audioplayer.ui.nowPlaying;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.oceanforit.audioplayer.PlayerActivity;
import com.oceanforit.audioplayer.R;
import com.oceanforit.audioplayer.callBack.ActionPlayingListener;
import com.oceanforit.audioplayer.common.Common;
import com.oceanforit.audioplayer.service.AudioService;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.oceanforit.audioplayer.HomeActivity.ARTIST_NAME_NOW_PLAYING;
import static com.oceanforit.audioplayer.HomeActivity.AUDIO_NAME_NOW_PLAYING;
import static com.oceanforit.audioplayer.HomeActivity.PATH_NOW_PLAYING;
import static com.oceanforit.audioplayer.HomeActivity.POSITION_NOW_PLAYING;
import static com.oceanforit.audioplayer.HomeActivity.SHOW_MINI_PLAYER;

public class NowPlayingFragment extends Fragment implements ServiceConnection {
    private static final String TAG = NowPlayingFragment.class.getSimpleName();


    private View layoutView;
    private int position;

    @BindView(R.id.iv_cover_playing)
    ImageView iCoverAudio;
    @BindView(R.id.iv_next_playing)
    ImageView iNext;

    @BindView(R.id.txt_audio_name_playing)
    TextView tAudioName;
    @BindView(R.id.txt_artist_name_playing)
    TextView tArtistName;
    @BindView(R.id.fab_play_pause_playing)
    FloatingActionButton fBtnPlayPause;

    private AudioService sAudioService;

    private static NowPlayingFragment instant;

    public static NowPlayingFragment getInstant() {
        return instant == null ? new NowPlayingFragment() : instant;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        layoutView = inflater.inflate(R.layout.fragment_now_playing, container, false);
        ButterKnife.bind(this, layoutView);

        iNext.setOnClickListener(v -> {
            if (sAudioService != null)
                sAudioService.nextAudio();
//                PlayerActivity.next();
        });

        fBtnPlayPause.setOnClickListener(v -> {
            if (sAudioService != null)
                sAudioService.playPauseAudio();
//                PlayerActivity.play();
        });

        return layoutView;
    }

    @Override
    public void onResume() {
        super.onResume();

        Intent serviceIntent = new Intent(getActivity(), AudioService.class);
        getActivity().bindService(serviceIntent, this, Context.BIND_AUTO_CREATE);

//
//        if (sAudioService != null) {
//            if (sAudioService.getListAudios().size() > 0 && sAudioService.getListAudios() != null) {
//
//                tAudioName.setText(sAudioService.getListAudios().get(sAudioService.getPosition()).getTitle());
//                tArtistName.setText(sAudioService.getListAudios().get(sAudioService.getPosition()).getArtist());
//
//                byte[] art = Common.getAlbumArt(sAudioService.getListAudios().get(sAudioService.getPosition()).getPath());
//                if (art != null) {
//                    iCoverAudio.setBackgroundResource(R.drawable.img_bg);
//                    Glide.with(getActivity()).load(art).into(iCoverAudio);
//                } else {
//                    iCoverAudio.setBackgroundResource(R.drawable.image_bg);
//                    Glide.with(getActivity()).load(R.drawable.icon_white).into(iCoverAudio);
//                }
//
//                layoutView.setOnClickListener(v -> {
//
//                    Intent intent = new Intent(getActivity(), PlayerActivity.class);
////                intent.putExtra("audioName", PlayerActivity.listAudio.get(PlayerActivity.position).getTitle());
////                intent.putExtra("artistName", PlayerActivity.listAudio.get(PlayerActivity.position).getTitle());
//                    intent.putExtra(Common.KEY_POSITION, sAudioService.getPosition());
//                    startActivity(intent);
//
//                });
//
//            } else {
//
//                /**
//                 *
//                 if (SHOW_MINI_PLAYER) {
//                 if (PATH_NOW_PLAYING != null) {
//                 tAudioName.setText(AUDIO_NAME_NOW_PLAYING);
//                 tArtistName.setText(ARTIST_NAME_NOW_PLAYING);
//                 position = POSITION_NOW_PLAYING;
//                 Log.d(TAG, "onResume Position: " + POSITION_NOW_PLAYING);
//                 byte[] art = Common.getAlbumArt(PATH_NOW_PLAYING);
//                 if (art != null) {
//                 iCoverAudio.setBackgroundResource(R.drawable.img_bg);
//                 Glide.with(getActivity()).load(art).into(iCoverAudio);
//                 } else {
//                 iCoverAudio.setBackgroundResource(R.drawable.image_bg);
//                 Glide.with(getActivity()).load(R.drawable.icon_white).into(iCoverAudio);
//                 }
//
//                 layoutView.setOnClickListener(v -> {
//
//                 Intent intent = new Intent(getActivity(), PlayerActivity.class);
//                 intent.putExtra("audioName", AUDIO_NAME_NOW_PLAYING);
//                 intent.putExtra("artistName", ARTIST_NAME_NOW_PLAYING);
//                 intent.putExtra(Common.KEY_POSITION, position);
//                 startActivity(intent);
//
//                 });
//                 }
//                 }
//                 */
//            }
//        }else {
//            Toast.makeText(sAudioService, "Error in Service", Toast.LENGTH_SHORT).show();
//        }

    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unbindService(this);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {

        AudioService.MyBinder binder = (AudioService.MyBinder) service;
        sAudioService = binder.getService();
        Log.d(TAG, "onServiceConnected: " + "Service Connected " + sAudioService);
        Toast.makeText(sAudioService, "Service Connected " + sAudioService, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        sAudioService = null;
    }
}
