package com.oceanforit.audioplayer.ui.albumDetails;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.oceanforit.audioplayer.HomeActivity;
import com.oceanforit.audioplayer.PlayerActivity;
import com.oceanforit.audioplayer.R;
import com.oceanforit.audioplayer.adapter.AlbumDeatilsAdapter;
import com.oceanforit.audioplayer.callBack.IAudioClickListener;
import com.oceanforit.audioplayer.common.Common;
import com.oceanforit.audioplayer.models.Audio;
import com.oceanforit.audioplayer.models.AudioModel;
import com.oceanforit.audioplayer.ui.home.HomeFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.oceanforit.audioplayer.HomeActivity.listAudios;


public class AlbumDetailsActivity extends AppCompatActivity implements IAudioClickListener {
    private static final String TAG = AlbumDetailsActivity.class.getSimpleName();

    @BindView(R.id.tool_bar)
    Toolbar toolbar;
    @BindView(R.id.img_album_cover)
    ImageView iAlbumCover;
    @BindView(R.id.txt_album_name)
    TextView tAlbumName;
    @BindView(R.id.txt_artist_name)
    TextView tArtistName;
    @BindView(R.id.recycler_album_details)
    RecyclerView recyclerAlbum;

    private String audioName;
    private List<Audio> listAlbum = new ArrayList<>();

    private AlbumDeatilsAdapter albumDeatilsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_details);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        if (getIntent() != null) {
            audioName = getIntent().getStringExtra(Common.KEY_AUDIO);
            String folderName = getIntent().getStringExtra(Common.KEY_FOLDER_NAME);

            if (folderName != null) {
                listAlbum = getAudios(this, folderName);
                getAlbumDetails(listAlbum);
                getSupportActionBar().setTitle(folderName);
                tAlbumName.setText(folderName);
                tArtistName.setVisibility(View.INVISIBLE);

            } else {
                listAlbum = getAudios();
                getAlbumDetails(listAlbum);
            }
        }


        initUI();
//        getAudios();
    }


    private void initUI() {



        recyclerAlbum.setHasFixedSize(true);
        recyclerAlbum.setLayoutManager(new LinearLayoutManager(this));

        if (listAlbum.size() > 0) {
            albumDeatilsAdapter = new AlbumDeatilsAdapter(this, listAlbum);
            recyclerAlbum.setAdapter(albumDeatilsAdapter);
        }
    }

    private List<Audio> getAudios() {

        List<Audio> listAudio = new ArrayList<>();
        try {
            int x = 0;
            for (int i = 0; i < listAudios.size(); i++) {
                if (audioName.equals(listAudios.get(i).getAlbum())) {
                    listAudio.add(x, listAudios.get(i));
                    x++;
                } else if (audioName.equals(listAudios.get(i).getArtist())) {
                    listAudio.add(x, listAudios.get(i));
                    x++;
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "getAudios: " + e.getMessage());
        }


        return listAudio;
    }

    void getAlbumDetails(List<Audio> listAlbum){

        try {


            Log.d(TAG, "getAudios Albume: " + listAlbum.get(0).getAlbum());
            getSupportActionBar().setTitle(listAlbum.get(0).getAlbum());
            tAlbumName.setText(listAlbum.get(0).getAlbum());
            tArtistName.setText(listAlbum.get(0).getArtist());
            byte[] art = Common.getAlbumArt(listAlbum.get(0).getPath());
            if (art != null) {
                iAlbumCover.setBackgroundResource(R.drawable.img_bg);
                Glide.with(this)
                        .asBitmap()
                        .load(art)
                        .into(iAlbumCover);
            } else {
                iAlbumCover.setBackgroundResource(R.drawable.image_bg);
                Glide.with(this)
                        .load(R.drawable.icon_white)
                        .into(iAlbumCover);
            }

        }catch(Exception e){
            Log.d(TAG, "getAlbumDetails: " + e.getMessage());
        }
    }

    public static List<Audio> getAudios(Context context, String folderName) {


        List<Audio> listAudios = new ArrayList<>();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

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

//        Cursor cursor = context.getContentResolver().query(uri, projection,
//                MediaStore.Audio.Media.DATA + " like ? ",
//                new String[]{"%" + listFolders + "%"}, order);

        String selection = MediaStore.Video.Media.DATA + " like?";
        String[] selectionArgs = new String[]{"%" + folderName + "%"};
        Cursor cursor = context.getContentResolver().query(uri, projection,
                selection, selectionArgs, null);

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

            }
            cursor.close();
        }

        return listAudios;
    }

    @Override
    public void onAudioClickListener(View view, AudioModel audioModel) {
//        Intent intent = new Intent(this, PlayerActivity.class);
//        intent.putExtra(Common.KEY_ALBUM_DETAILS, Common.ALBUM_DETAILS);
//        intent.putExtra(Common.KEY_POSITION, audioModel.getPosition());
//        startActivity(intent);

    }
}