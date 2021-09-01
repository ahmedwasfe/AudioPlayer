package com.oceanforit.audioplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.palette.graphics.Palette;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.oceanforit.audioplayer.adapter.AlbumDeatilsAdapter;
import com.oceanforit.audioplayer.adapter.AudioAdapter;
import com.oceanforit.audioplayer.callBack.ActionPlayingListener;
import com.oceanforit.audioplayer.common.Common;
import com.oceanforit.audioplayer.common.SaveSettings;
import com.oceanforit.audioplayer.favorite.FavDataSource;
import com.oceanforit.audioplayer.favorite.FavoriteAudio;
import com.oceanforit.audioplayer.favorite.FavoriteDataSource;
import com.oceanforit.audioplayer.favorite.FavoriteDatabase;
import com.oceanforit.audioplayer.models.Audio;
import com.oceanforit.audioplayer.service.AudioService;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.oceanforit.audioplayer.HomeActivity.isRepeat;
import static com.oceanforit.audioplayer.HomeActivity.isShuffle;
import static com.oceanforit.audioplayer.common.Common.getRandomAudio;

public class PlayerActivity extends AppCompatActivity implements
        ActionPlayingListener, ServiceConnection {
    private static final String TAG = PlayerActivity.class.getSimpleName();

    @BindView(R.id.img_favorite)
    ImageView imgFavorite;

    @BindView(R.id.txt_audio_name)
    TextView tAudioName;
    @BindView(R.id.txt_artist_name)
    TextView tArtistName;
    @BindView(R.id.txt_duration_played)
    TextView tDurationPlayed;
    @BindView(R.id.txt_duration)
    TextView tDuration;

    @BindView(R.id.iv_cover_art)
    ImageView iCoverArt;
    @BindView(R.id.iv_next)
    ImageView iBtnNext;
    @BindView(R.id.iv_previous)
    ImageView iBtnPrevious;
    @BindView(R.id.iv_shuffle)
    ImageView iBtnShuffle;
    @BindView(R.id.iv_repeat)
    ImageView iBtnRepeat;

    @BindView(R.id.fab_play_pause)
    FloatingActionButton fBtnPlayPause;
    @BindView(R.id.seek_bar)
    SeekBar seekBar;
    @BindView(R.id.seek_bar_volume)
    SeekBar seekBarVolume;

    private FavoriteDataSource favoriteDataSource;

    public static int position = -1;
    public static List<Audio> listAudio = new ArrayList<>();
    private static Uri audioUri;
    ;
    //    private static MediaPlayer mediaPlayer;

    private SaveSettings saveSettings;
    private static AudioService sAudioService;
    private Handler handler = new Handler();
    private Thread playThread, nextThread, nextLongThread, previousThread, previousLongThread;

    @OnClick(R.id.img_favorite)
    void onClickAddToFavorite() {
        addToFavorite(position);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreen();
        setContentView(R.layout.activity_player);

        ButterKnife.bind(this);
        fBtnPlayPause = findViewById(R.id.fab_play_pause);
        saveSettings = new SaveSettings(this);

        initMediaPlayer();
        setupAudioData();
        initUI();
    }

    private void setFullScreen() {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void initUI() {

        favoriteDataSource = new FavDataSource(FavoriteDatabase.getInstance(this).favoriteDAO());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (sAudioService != null && fromUser) {
                    sAudioService.seekTo(progress * 1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (sAudioService != null) {
                    sAudioService.setVolume(0.5f, 0.5f);
                    seekBarVolume.setProgress(50);
                    seekBarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                            if (fromUser)
                                sAudioService.setVolume(progress / 100f, progress / 100f);
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {

                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {

                        }
                    });
                }
                handler.postDelayed(this, 1000);
            }
        });


        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (sAudioService != null) {
                    int currentPosition = sAudioService.getCurrentPosition() / 1000;
                    seekBar.setProgress(currentPosition);
                    tDurationPlayed.setText(formattedTime(currentPosition));
                }
                handler.postDelayed(this, 1000);
            }
        });

        iBtnShuffle.setOnClickListener(v -> {
            if (isShuffle) {
                isShuffle = false;
                iBtnShuffle.setImageResource(R.drawable.ic_shuffle_off);
            } else {
                isShuffle = true;
                iBtnShuffle.setImageResource(R.drawable.ic_shuffle_on);
            }
        });

        iBtnRepeat.setOnClickListener(v -> {
            if (isRepeat) {
                isRepeat = false;
                iBtnRepeat.setImageResource(R.drawable.ic_repeat_off);
            } else {
                isRepeat = true;
                iBtnRepeat.setImageResource(R.drawable.ic_repeat_on);
            }
        });
    }

    private String formattedTime(int currentPosition) {

        String totalOut = "";
        String totalNew = "";
        String secounds = String.valueOf(currentPosition % 60);
        String minutes = String.valueOf(currentPosition / 60);
        totalOut = minutes + ":" + secounds;
        totalNew = minutes + ":" + "0" + secounds;
        if (secounds.length() == 1)
            return totalNew;
        else
            return totalOut;
    }

    private void setupAudioData() {
        tAudioName.setText(listAudio.get(position).getTitle());
        tArtistName.setText(listAudio.get(position).getArtist());
//        if (listAudio == null) {
//            tAudioName.setText(audioName);
//            tArtistName.setText(artistName);
//        } else {
//
//        }
    }

    private void initMediaPlayer() {

        if (getIntent() != null) {
            position = getIntent().getIntExtra(Common.KEY_POSITION, -1);
            Log.d(TAG, "initMediaPlayer: " + position);
            String albumDeatils = getIntent().getStringExtra(Common.KEY_ALBUM_DETAILS);
            if (albumDeatils != null && albumDeatils.equals(Common.ALBUM_DETAILS))
                listAudio = AlbumDeatilsAdapter.listAudio;
            else
                listAudio = AudioAdapter.listAudio;
        }


        if (listAudio != null && listAudio.size() > 0) {
            fBtnPlayPause.setImageResource(R.drawable.ic_pause);
            audioUri = Uri.parse(listAudio.get(position).getPath());
        }

//        if (sAudioService != null) {
//            sAudioService.stop();
//            sAudioService.release();
//            sAudioService.createMediaPlayer(position);
//            sAudioService.start();
//        }
//        sAudioService.createMediaPlayer(position);
//        sAudioService.start();

        Intent serviceIntent = new Intent(this, AudioService.class);
        serviceIntent.putExtra(Common.KEY_SERVICE_POSITION, position);
        startService(serviceIntent);
    }

    private void metaData(Uri uri) {

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(uri.toString());
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "metaData: " + e.getMessage());
            Toast.makeText(this, "File not found", Toast.LENGTH_SHORT).show();
        }

        int duration = Integer.parseInt(listAudio.get(position).getDuration()) / 1000;
        tDuration.setText(formattedTime(duration));
        byte[] art = retriever.getEmbeddedPicture();
        Log.d(TAG, "metaData: " + art);
        Bitmap bitmap;
        if (art != null) {
            iCoverArt.setBackgroundResource(R.drawable.img_bg);
//            Glide.with(this).asBitmap().load(art).into(iCoverArt);

            bitmap = BitmapFactory.decodeByteArray(art, 0, art.length);
            imageAnimation(this, iCoverArt, bitmap);
            Palette.from(bitmap).generate(palette -> {
                Palette.Swatch swatch = palette.getDominantSwatch();
                if (swatch != null) {
                    ImageView gredient = findViewById(R.id.img_gredient);
                    RelativeLayout container = findViewById(R.id.container_player);
                    gredient.setBackgroundResource(R.drawable.gredient_bg);
                    container.setBackgroundResource(R.drawable.bg);
                    GradientDrawable gradientDrawable = new GradientDrawable(
                            GradientDrawable.Orientation.BOTTOM_TOP,
                            new int[]{swatch.getRgb(), 0x00000000});
                    gredient.setBackground(gradientDrawable);
                    GradientDrawable gradientDrawableBg = new GradientDrawable(
                            GradientDrawable.Orientation.BOTTOM_TOP,
                            new int[]{swatch.getRgb(), swatch.getRgb()});
                    container.setBackground(gradientDrawableBg);
                    tAudioName.setTextColor(swatch.getTitleTextColor());
                    tArtistName.setTextColor(swatch.getBodyTextColor());
                } else {
                    ImageView gredient = findViewById(R.id.img_gredient);
                    RelativeLayout container = findViewById(R.id.container);
                    gredient.setBackgroundResource(R.drawable.gredient_bg);
                    container.setBackgroundResource(R.drawable.bg);
                    GradientDrawable gradientDrawable = new GradientDrawable(
                            GradientDrawable.Orientation.BOTTOM_TOP,
                            new int[]{0xff000000, 0x00000000});
                    gredient.setBackground(gradientDrawable);
                    GradientDrawable gradientDrawableBg = new GradientDrawable(
                            GradientDrawable.Orientation.BOTTOM_TOP,
                            new int[]{0xff000000, 0xff000000});
                    container.setBackground(gradientDrawableBg);
                    tAudioName.setTextColor(Color.WHITE);
                    tArtistName.setTextColor(Color.DKGRAY);
                }
            });
        } else {
            iCoverArt.setBackgroundResource(R.drawable.image_player_bg);
            Glide.with(this).asBitmap().load(R.drawable.icon_white).into(iCoverArt);
            ImageView gredient = findViewById(R.id.img_gredient);
            RelativeLayout container = findViewById(R.id.container);
            gredient.setBackgroundResource(R.drawable.gredient_bg);
            try {
                container.setBackgroundResource(R.drawable.bg);
            } catch (Exception e) {
                Log.e(TAG, "metaData: " + e.getMessage());
            }

            tAudioName.setTextColor(Color.WHITE);
            tArtistName.setTextColor(Color.DKGRAY);
        }
    }

    private void addToFavorite(int position) {

        FavoriteAudio favoriteItem = new FavoriteAudio();
        favoriteItem.setId(listAudio.get(position).getId());
        favoriteItem.setPath(listAudio.get(position).getPath());
        favoriteItem.setTitle(listAudio.get(position).getTitle());
        favoriteItem.setAlbum(listAudio.get(position).getAlbum());
        favoriteItem.setArtist(listAudio.get(position).getArtist());
        favoriteItem.setDuration(listAudio.get(position).getDuration());

        favoriteDataSource.getAudioById(listAudio.get(position).getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(favoriteAudio -> {
                    if (favoriteAudio.equals(favoriteItem)) {
                        // audio already in favorite
                        imgFavorite.setImageResource(R.drawable.ic_favorite);
                        favoriteDataSource.deleteFavorite(favoriteItem)
                                .subscribe(integer -> {
                                    imgFavorite.setImageResource(R.drawable.ic_unfavorite);
                                }, throwable -> {
                                    Log.e(TAG, "addToFavorite: " + throwable.getMessage());
                                });
                    } else {
                        favoriteDataSource.addFavorite(favoriteItem)
                                .subscribe(() -> {
                                    imgFavorite.setImageResource(R.drawable.ic_favorite);
                                    Toast.makeText(this, "Add Success", Toast.LENGTH_SHORT).show();
                                }, throwable -> {
                                    imgFavorite.setImageResource(R.drawable.ic_unfavorite);
                                    Log.e(TAG, "addToFavorite: " + throwable.getMessage());
                                });
                    }
                }, throwable -> {
                    imgFavorite.setImageResource(R.drawable.ic_unfavorite);
                    Log.e(TAG, "CheckInFavorite: " + throwable.getMessage());
                });
    }

    @Override
    protected void onResume() {
        Intent serviceIntent = new Intent(this, AudioService.class);
        bindService(serviceIntent, this, BIND_AUTO_CREATE);
        playThreadClick();
        nextThreadClick();
        nextLongThreadClick();
        previousThreadClick();
        previousLongThreadClick();
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(this);
    }

    private void nextLongThreadClick() {
        nextLongThread = new Thread() {
            @Override
            public void run() {
                super.run();
                iBtnNext.setOnLongClickListener(v -> {
//                    Toast.makeText(PlayerActivity.this, "Long Next Click", Toast.LENGTH_SHORT).show();
                    int currentPosition = sAudioService.getCurrentPosition();
                    int duration = sAudioService.getDuration();
                    if (sAudioService.isPlaying() && duration != currentPosition) {
                        currentPosition = currentPosition + 5000;
//                        tDurationPlayed.setText(formattedTime(currentPosition));
                        sAudioService.seekTo(currentPosition);
                    }
                    return true;
                });
            }
        };
        nextLongThread.start();
    }

    private void previousLongThreadClick() {

        previousLongThread = new Thread() {
            @Override
            public void run() {
                super.run();
                iBtnPrevious.setOnLongClickListener(v -> {
//                    Toast.makeText(PlayerActivity.this, "Long Previous Click", Toast.LENGTH_SHORT).show();
                    int currentPosition = sAudioService.getCurrentPosition();
                    if (sAudioService.isPlaying() && currentPosition > 5000) {
                        currentPosition = currentPosition - 5000;
//                        tDurationPlayed.setText(formattedTime(currentPosition));
                        sAudioService.seekTo(currentPosition);
                    }
                    return true;
                });
            }
        };
        previousLongThread.start();
    }

    private void playThreadClick() {
        playThread = new Thread() {
            @Override
            public void run() {
                super.run();
                fBtnPlayPause.setOnClickListener(v -> {
                    playPauseAudio();
                });
            }
        };
        playThread.start();
    }

    @Override
    public void playPauseAudio() {

        if (sAudioService.isPlaying()) {
            fBtnPlayPause.setImageResource(R.drawable.ic_play);
            sAudioService.showNotifcation(R.drawable.ic_play);
            sAudioService.pause();
            seekBar.setMax(sAudioService.getDuration() / 1000);
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (sAudioService != null) {
                        int currentPosition = sAudioService.getCurrentPosition() / 1000;
                        seekBar.setProgress(currentPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });

        } else {
            fBtnPlayPause.setImageResource(R.drawable.ic_pause);
            sAudioService.showNotifcation(R.drawable.ic_pause);
            sAudioService.start();
            seekBar.setMax(sAudioService.getDuration() / 1000);
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (sAudioService != null) {
                        int currentPosition = sAudioService.getCurrentPosition() / 1000;
                        seekBar.setProgress(currentPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
        }
    }

    private void nextThreadClick() {

        nextThread = new Thread() {
            @Override
            public void run() {
                super.run();
                iBtnNext.setOnClickListener(v -> {
                    nextAudio();
                });
            }
        };
        nextThread.start();

    }

    @Override
    public void nextAudio() {
        if (sAudioService.isPlaying()) {
            sAudioService.stop();
            sAudioService.release();
            if (isShuffle && !isRepeat)
                position = getRandomAudio(listAudio.size() - 1);
            else if (!isShuffle && !isRepeat)
                position = ((position + 1) % listAudio.size());
            // else poistion will be position
            audioUri = Uri.parse(listAudio.get(position).getPath());
            sAudioService.createMediaPlayer(position);
            metaData(audioUri);
            setupAudioData();
            seekBar.setMax(sAudioService.getDuration() / 1000);
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (sAudioService != null) {
                        int currentPosition = sAudioService.getCurrentPosition() / 1000;
                        seekBar.setProgress(currentPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
            sAudioService.onCompletion();
            sAudioService.showNotifcation(R.drawable.ic_pause);
            fBtnPlayPause.setBackgroundResource(R.drawable.ic_pause);
            sAudioService.start();
        } else {
            sAudioService.stop();
            sAudioService.release();
            if (isShuffle && !isRepeat)
                position = getRandomAudio(listAudio.size() - 1);
            else if (!isShuffle && !isRepeat)
                position = ((position + 1) % listAudio.size());
            audioUri = Uri.parse(listAudio.get(position).getPath());
            sAudioService.createMediaPlayer(position);
            metaData(audioUri);
            setupAudioData();
            seekBar.setMax(sAudioService.getDuration() / 1000);

            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (sAudioService != null) {
                        int currentPosition = sAudioService.getCurrentPosition() / 1000;
                        seekBar.setProgress(currentPosition);

                    }
                    handler.postDelayed(this, 1000);
                }
            });

            sAudioService.onCompletion();
            sAudioService.showNotifcation(R.drawable.ic_play);
            fBtnPlayPause.setBackgroundResource(R.drawable.ic_play);
        }
    }

    private void previousThreadClick() {

        previousThread = new Thread() {
            @Override
            public void run() {
                super.run();
                iBtnPrevious.setOnClickListener(v -> {
                    previousAudio();
                });
            }
        };
        previousThread.start();
    }

    @Override
    public void previousAudio() {

        if (sAudioService.isPlaying()) {
            sAudioService.stop();
            sAudioService.release();
            if (isShuffle && !isRepeat)
                position = getRandomAudio(listAudio.size() - 1);
            else if (!isShuffle && !isRepeat)
                position = ((position - 1) < 0 ? (listAudio.size() - 1) : (position - 1));
            audioUri = Uri.parse(listAudio.get(position).getPath());
            sAudioService.createMediaPlayer(position);
            metaData(audioUri);
            setupAudioData();
            seekBar.setMax(sAudioService.getDuration() / 1000);
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (sAudioService != null) {
                        int currentPosition = sAudioService.getCurrentPosition() / 1000;
                        seekBar.setProgress(currentPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
            sAudioService.onCompletion();
            sAudioService.showNotifcation(R.drawable.ic_pause);
            fBtnPlayPause.setBackgroundResource(R.drawable.ic_pause);
            sAudioService.start();
        } else {
            sAudioService.stop();
            sAudioService.release();
            if (isShuffle && !isRepeat)
                position = getRandomAudio(listAudio.size() - 1);
            else if (!isShuffle && !isRepeat)
                position = ((position - 1) < 0 ? (listAudio.size() - 1) : (position - 1));
            audioUri = Uri.parse(listAudio.get(position).getPath());
            sAudioService.createMediaPlayer(position);
            metaData(audioUri);
            setupAudioData();
            seekBar.setMax(sAudioService.getDuration() / 1000);
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (sAudioService != null) {
                        int currentPosition = sAudioService.getCurrentPosition() / 1000;
                        seekBar.setProgress(currentPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
            sAudioService.onCompletion();
            sAudioService.showNotifcation(R.drawable.ic_play);
            fBtnPlayPause.setBackgroundResource(R.drawable.ic_play);
        }
    }

    private void imageAnimation(Context context, ImageView imageView, Bitmap bitmap) {
        Animation animOut = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
        Animation animIn = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);

        animOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Glide.with(context).load(bitmap).into(imageView);
                animIn.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                imageView.startAnimation(animIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        imageView.startAnimation(animOut);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        AudioService.MyBinder binder = (AudioService.MyBinder) service;
        sAudioService = binder.getService();
        sAudioService.setCallBack(this);
        Log.d(TAG, "onServiceConnected: " + "Service Connected " + sAudioService);
        // error when back to activity
        seekBar.setMax(sAudioService.getDuration() / 1000);
        metaData(audioUri);
        setupAudioData();
        sAudioService.onCompletion();
        sAudioService.showNotifcation(R.drawable.ic_pause);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        sAudioService = null;
    }

}