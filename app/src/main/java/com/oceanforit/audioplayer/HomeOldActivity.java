package com.oceanforit.audioplayer;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.oceanforit.audioplayer.callBack.ActionPlayingListener;
import com.oceanforit.audioplayer.callBack.AudioCallback;
import com.oceanforit.audioplayer.common.Common;
import com.oceanforit.audioplayer.common.SaveSettings;
import com.oceanforit.audioplayer.models.Audio;
import com.oceanforit.audioplayer.models.AudioModel;
import com.oceanforit.audioplayer.service.AudioService;
import com.oceanforit.audioplayer.ui.tracks.TracksFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeOldActivity extends AppCompatActivity implements SearchView.OnQueryTextListener,
        AudioCallback, ServiceConnection, ActionPlayingListener {
    private static final String TAG = HomeOldActivity.class.getSimpleName();

    public static boolean SHOW_MINI_PLAYER = false;
    public static String PATH_NOW_PLAYING = null;
    public static String AUDIO_NAME_NOW_PLAYING = null;
    public static String ARTIST_NAME_NOW_PLAYING = null;
    public static int POSITION_NOW_PLAYING = -1;

    @BindView(R.id.tool_bar)
    Toolbar sToolbar;
    //    @BindView(R.id.player_ui)
//    View playerUI;
//    @BindView(R.id.player_ui1)
//    View playerUI1;
    @BindView(R.id.container_player)
    RelativeLayout relativeLayout;
    //    @BindView(R.id.container_player_ui1)
//    RelativeLayout relativeLayoutUi1;
    @BindView(R.id.bottom_navigation)
    BottomNavigationView sBtnNavView;

    // Player views
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

    private AudioService sAudioService;
    private static SaveSettings saveSettings;
    private BottomSheetBehavior playerBottomSheet;
    private AppBarConfiguration sAppBarConfiguration;
    private NavController sNavController;

    private Handler handler = new Handler();
    private Thread playThread, nextThread, nextLongThread, previousThread, previousLongThread;

    public static List<Audio> listAudios;
    public static List<Audio> listAlbums = new ArrayList<>();
    public static List<Audio> listArtists = new ArrayList<>();
    public static boolean isShuffle = false;
    public static boolean isRepeat = false;


    private static int position = -1;
    private static Uri audioUri;
    public static List<Audio> listPlayerAudio = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: " + "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_old);

        ButterKnife.bind(this);
        initUI();
        initBottomNavigation();
        requestPermissions();
        listPlayerAudio = listAudios;
    }

    private void initBottomNavigation() {

        sAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home,
                R.id.nav_search,
                R.id.nav_favorite
        )
                .build();

        sNavController = Navigation.findNavController(this, R.id.nav_fragments);
        NavigationUI.setupActionBarWithNavController(this, sNavController, sAppBarConfiguration);
        NavigationUI.setupWithNavController(sBtnNavView, sNavController);

        sBtnNavView.setOnNavigationItemSelectedListener(menuItem -> {

            menuItem.setChecked(true);

            switch (menuItem.getItemId()) {
                case R.id.nav_home:
                    Log.d(TAG, "initBottomNavigation: " + "Home " + menuItem.getItemId());
                    Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show();
                    sNavController.navigate(R.id.nav_home);
                    break;
                case R.id.nav_search:
                    Log.d(TAG, "initBottomNavigation: " + "Search " + menuItem.getItemId());
                    Toast.makeText(this, "Search", Toast.LENGTH_SHORT).show();
                    sNavController.navigate(R.id.nav_search);
                    break;
                case R.id.nav_favorite:
                    Log.d(TAG, "initBottomNavigation: " + "Playlist " + menuItem.getItemId());
                    Toast.makeText(this, "Playlist", Toast.LENGTH_SHORT).show();
                    sNavController.navigate(R.id.nav_favorite);
                    break;
            }

            return false;
        });

        sBtnNavView.bringToFront();
    }

    private void requestPermissions() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE}, Common.REQUEST_PERMISSION_CODE);

        } else
            listAudios = getAllAudios(this);
    }

    private void initUI() {

        saveSettings = new SaveSettings(this);
        setSupportActionBar(sToolbar);

        RetroBottomSheetBehavior behavior = new RetroBottomSheetBehavior();
        if (saveSettings.getNowPlayingScreen().equals("screen1")) {
//            playerUI.setVisibility(View.VISIBLE);

            playerBottomSheet = BottomSheetBehavior.from(relativeLayout);
        } else if (saveSettings.getNowPlayingScreen().equals("screen2")) {
//            playerUI.setVisibility(View.VISIBLE);
            playerBottomSheet = BottomSheetBehavior.from(relativeLayout);
        }

        playerBottomSheet.setState(BottomSheetBehavior.STATE_COLLAPSED);
        playerBottomSheet.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_EXPANDED:
                        behavior.setAllowDragging(false);
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        behavior.setAllowDragging(false);
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        behavior.setAllowDragging(true);
                        break;
                    default:
                        behavior.setAllowDragging(true);
                }
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    playerBottomSheet.setState(BottomSheetBehavior.STATE_EXPANDED);
                }

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                ButterKnife.bind(this, bottomSheet);
                if (sAudioService != null) {
                    try {
                        initPlayerUI(bottomSheet);
                    } catch (Exception e) {
                        Log.e(TAG, "onSlide: " + e.getMessage());
                    }

                }

            }
        });


    }

    private void initPlayerUI(View bottomSheet) {

        if (listPlayerAudio.size() > 0) {
            fBtnPlayPause.setImageResource(R.drawable.ic_pause);
            audioUri = Uri.parse(listPlayerAudio.get(position).getPath());
            metaData(audioUri);
        } else
            fBtnPlayPause.setImageResource(R.drawable.ic_play);

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

    public static List<Audio> getAllAudios(Context context) {

        String sortOrder = saveSettings.getSortBy(Common.SORT_BY_NAME);
        List<String> listAlbumsDuplicate = new ArrayList<>();
        List<String> listArtistDuplicate = new ArrayList<>();
        listAlbums.clear();
        listArtists.clear();
        List<Audio> listAudios = new ArrayList<>();
        String order = null;
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        switch (sortOrder) {
            case Common.SORT_BY_NAME:
                order = MediaStore.MediaColumns.DISPLAY_NAME + " ASC";
                break;
            case Common.SORT_BY_DATE:
                order = MediaStore.MediaColumns.DATE_ADDED + " ASC";
                break;
            case Common.SORT_BY_SIZE:
                order = MediaStore.MediaColumns.SIZE + " DESC";
                break;
            case Common.SORT_BY_ARTIST:
                order = MediaStore.MediaColumns.ARTIST + " DESC";
                break;
        }
        String[] projection = {
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA, // For Path
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ARTIST_ID,
                MediaStore.Audio.Media.ALBUM_ARTIST,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.YEAR,

        };

        List<File> listFolders = new ArrayList<>();

//        Cursor cursor = context.getContentResolver().query(uri, projection,
//                MediaStore.Audio.Media.DATA + " like ? ",
//                new String[]{"%" + listFolders + "%"}, order);

        Cursor cursor = context.getContentResolver().query(uri, projection,
                null, null, order);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String album = cursor.getString(0);
                String title = cursor.getString(1);
                String duration = cursor.getString(2);
                String path = cursor.getString(3);
                String artist = cursor.getString(4);
                String artistId = cursor.getString(5);
                String albumId = cursor.getString(6);
                String id = cursor.getString(7);
                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));

                Audio audio = new Audio(id, path, title, artist, artistId, album, albumId, duration);
                Log.d(TAG, "getAllAudios: " + "Path: " + path
                        + " ||  Album: " + album
                        + " ||  AlbumID: " + albumId
                        + " ||  Title: " + title
                        + " ||  Artist: " + artist
                        + " ||  Artist ID: " + artistId
                        + " ||  Duration: " + duration
                        + " ||  Data: " + data);
                listAudios.add(audio);
                if (!listAlbumsDuplicate.contains(album)) {
                    listAlbums.add(audio);
                    listAlbumsDuplicate.add(album);
                }

                if (!listArtistDuplicate.contains(artistId)) {
                    listArtists.add(audio);
                    listArtistDuplicate.add(artistId);
                }
            }
            cursor.close();
        }

        return listAudios;
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

        String lastPlayedPath = saveSettings.getLastPlayed(Common.AUDIO_FILE);
        String audioName = saveSettings.getLastPlayed(Common.AUDIO_NAME);
        String artistName = saveSettings.getLastPlayed(Common.ARTIST_NAME);
        int position = saveSettings.getLastPlayed(Common.AUDIO_NOW_POSITION, -1);

        if (lastPlayedPath != null) {
            SHOW_MINI_PLAYER = true;
            PATH_NOW_PLAYING = lastPlayedPath;
            AUDIO_NAME_NOW_PLAYING = audioName;
            ARTIST_NAME_NOW_PLAYING = artistName;
            POSITION_NOW_PLAYING = position;
        } else {
            SHOW_MINI_PLAYER = false;
            PATH_NOW_PLAYING = null;
            AUDIO_NAME_NOW_PLAYING = null;
            ARTIST_NAME_NOW_PLAYING = null;
            POSITION_NOW_PLAYING = -1;
        }
    }

    private void setupAudioData() {

        tAudioName.setText(listPlayerAudio.get(position).getTitle());
        tArtistName.setText(listPlayerAudio.get(position).getArtist());

    }

    private void initMediaPlayer() {

        if (getIntent() != null) {

            Log.d(TAG, "initMediaPlayer: " + position);
//            String albumDetails = getIntent().getStringExtra(Common.KEY_ALBUM_DETAILS);
//            if (albumDetails != null && albumDetails.equals(Common.ALBUM_DETAILS))
//                listPlayerAudio = AlbumDeatilsAdapter.listAudio;
//            else
//                listPlayerAudio = AudioAdapter.listAudio;
        }

        if (listPlayerAudio != null || listPlayerAudio.size() > 0) {
            fBtnPlayPause.setImageResource(R.drawable.ic_pause);
            audioUri = Uri.parse(listPlayerAudio.get(position).getPath());
            metaData(audioUri);
        }

        Intent serviceIntent = new Intent(this, AudioService.class);
        serviceIntent.putExtra(Common.KEY_SERVICE_POSITION, position);
        startService(serviceIntent);
    }

    private void metaData(Uri uri) {

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri.toString());
        int duration = Integer.parseInt(listPlayerAudio.get(position).getDuration()) / 1000;
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
                    RelativeLayout container = findViewById(R.id.container_player);
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
            RelativeLayout container = findViewById(R.id.container_player);
            gredient.setBackgroundResource(R.drawable.gredient_bg);
            container.setBackgroundResource(R.drawable.bg);
            tAudioName.setTextColor(Color.WHITE);
            tArtistName.setTextColor(Color.DKGRAY);
        }
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

    private int getRandomAudio(int i) {
        Random random = new Random();
        return random.nextInt(i + 1);
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
    protected void onPause() {
        super.onPause();
        unbindService(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Common.REQUEST_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                listAudios = getAllAudios(this);
            else
                finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String inputUser = newText.toLowerCase();
        List<Audio> audios = new ArrayList<>();
        for (Audio audio : listAudios) {
            if (audio.getTitle().toLowerCase().contains(inputUser)) {
                audios.add(audio);
            }
        }
        TracksFragment.audioAdapter.updateList(audios);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_name:
                saveSettings.setSortBy(Common.SORT_BY_NAME);
                this.recreate();
                break;
            case R.id.action_date:
                saveSettings.setSortBy(Common.SORT_BY_DATE);
                this.recreate();
                break;
            case R.id.action_size:
                saveSettings.setSortBy(Common.SORT_BY_SIZE);
                this.recreate();
                break;
            case R.id.action_artist:
                saveSettings.setSortBy(Common.SORT_BY_ARTIST);
                this.recreate();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAudioCreate(AudioModel audioModel) {
        Log.d(TAG, "onAudioCreate: " + "Position: " + audioModel.getPosition() + " Title: " + audioModel.getListAudio().get(audioModel.getPosition()).getTitle());
        position = audioModel.getPosition();
//        listPlayerAudio = audioModel.getListAudio();
        initMediaPlayer();
        setupAudioData();
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        AudioService.MyBinder binder = (AudioService.MyBinder) service;
        sAudioService = binder.getService();
        sAudioService.setCallBack(this);
        Log.d(TAG, "onServiceConnected: " + "Service Connected " + sAudioService);
        try {
            seekBar.setMax(sAudioService.getDuration() / 1000);
            metaData(audioUri);
            setupAudioData();
            sAudioService.onCompletion();
            sAudioService.showNotifcation(R.drawable.ic_pause);
        } catch (Exception e) {
            Log.e(TAG, "onServiceConnected: " + e.getMessage());
        }

    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        sAudioService = null;
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
                    handler.postDelayed(this, Common.DELAY_MILLIS);
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
                    handler.postDelayed(this, Common.DELAY_MILLIS);
                }
            });
        }
    }

    @Override
    public void nextAudio() {

        if (sAudioService.isPlaying()) {
            sAudioService.stop();
            sAudioService.release();
            if (isShuffle && !isRepeat)
                position = getRandomAudio(listPlayerAudio.size() - 1);
            else if (!isShuffle && !isRepeat)
                position = ((position + 1) % listPlayerAudio.size());
            // else poistion will be position
            audioUri = Uri.parse(listPlayerAudio.get(position).getPath());
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
                position = getRandomAudio(listPlayerAudio.size() - 1);
            else if (!isShuffle && !isRepeat)
                position = ((position + 1) % listPlayerAudio.size());
            audioUri = Uri.parse(listPlayerAudio.get(position).getPath());
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

    @Override
    public void previousAudio() {

        if (sAudioService.isPlaying()) {
            sAudioService.stop();
            sAudioService.release();
            if (isShuffle && !isRepeat)
                position = getRandomAudio(listPlayerAudio.size() - 1);
            else if (!isShuffle && !isRepeat)
                position = ((position - 1) < 0 ? (listPlayerAudio.size() - 1) : (position - 1));
            audioUri = Uri.parse(listPlayerAudio.get(position).getPath());
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
                position = getRandomAudio(listPlayerAudio.size() - 1);
            else if (!isShuffle && !isRepeat)
                position = ((position - 1) < 0 ? (listPlayerAudio.size() - 1) : (position - 1));
            audioUri = Uri.parse(listPlayerAudio.get(position).getPath());
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
}