package com.oceanforit.audioplayer.adapter;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.oceanforit.audioplayer.HomeActivity;
import com.oceanforit.audioplayer.PlayerActivity;
import com.oceanforit.audioplayer.R;
import com.oceanforit.audioplayer.callBack.IAudioClickListener;
import com.oceanforit.audioplayer.common.Common;
import com.oceanforit.audioplayer.favorite.FavDataSource;
import com.oceanforit.audioplayer.favorite.FavoriteAudio;
import com.oceanforit.audioplayer.favorite.FavoriteDataSource;
import com.oceanforit.audioplayer.favorite.FavoriteDatabase;
import com.oceanforit.audioplayer.models.Audio;
import com.oceanforit.audioplayer.models.AudioModel;

import java.io.File;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AlbumDeatilsAdapter extends RecyclerView.Adapter<AudioAdapter.AudioHolder> {
    private static final String TAG = AlbumDeatilsAdapter.class.getSimpleName();

    private Context sContext;
    public static List<Audio> listAudio;
    private LayoutInflater inflater;
    private static IAudioClickListener audioClickListener;

    private FavoriteDataSource favoriteDataSource;

    public AlbumDeatilsAdapter(Context sContext, List<Audio> listAudio) {
        this.sContext = sContext;
        this.listAudio = listAudio;

        inflater = LayoutInflater.from(sContext);

        favoriteDataSource = new FavDataSource(FavoriteDatabase.getInstance(sContext).favoriteDAO());
    }

    public AlbumDeatilsAdapter(Context sContext, List<Audio> listAudio, IAudioClickListener audioClickListener) {
        this.sContext = sContext;
        this.listAudio = listAudio;
        this.audioClickListener = audioClickListener;

        inflater = LayoutInflater.from(sContext);
    }


    @NonNull
    @Override
    public AudioAdapter.AudioHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AudioAdapter.AudioHolder(inflater.inflate(R.layout.row_audio, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AudioAdapter.AudioHolder holder, int position) {

        holder.txtTitle.setText(listAudio.get(position).getTitle());
        holder.txtArtist.setText(listAudio.get(position).getArtist());


        byte[] bytes = Common.getAlbumArt(listAudio.get(position).getPath());

        Log.d(TAG, "onBindViewHolder:  " + bytes);
        if (bytes != null) {
            holder.imageAudio.setBackgroundResource(R.drawable.img_bg);
            Glide.with(sContext)
                    .asBitmap()
                    .load(bytes)
                    .into(holder.imageAudio);
        } else {
            holder.imageAudio.setBackgroundResource(R.drawable.image_bg);
            Glide.with(sContext)
                    .load(R.drawable.icon_white)
                    .into(holder.imageAudio);
        }

        holder.itemView.setOnClickListener(view -> {
//            AudioModel audioModel = new AudioModel();
//            audioModel.setListAudio(listAudio);
//            audioModel.setPosition(position);
//            audioClickListener.onAudioClickListener(view, audioModel);
            Intent intent = new Intent(sContext, PlayerActivity.class);
            intent.putExtra(Common.KEY_ALBUM_DETAILS, Common.ALBUM_DETAILS);
            intent.putExtra(Common.KEY_POSITION, position);
            sContext.startActivity(intent);
        });

        holder.moreOptions.setOnClickListener(v -> {

            PopupMenu menu = new PopupMenu(sContext, v);
            menu.getMenuInflater().inflate(R.menu.more_menu, menu.getMenu());
            menu.show();
            menu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.action_delete:
                        deleteAudio(position, v);
                        break;
                    case R.id.action_favorite:
                        addToFavorite(position);
                        break;
                    case R.id.action_remove_favorite:
                        item.setVisible(false);
                        break;
                }
                return true;
            });
        });
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
                    if (favoriteAudio.equals(favoriteItem))
                        // audio already in favorite
                        Toast.makeText(sContext, "This audio already in favorite", Toast.LENGTH_SHORT).show();
                    else {
                        favoriteDataSource.addFavorite(favoriteItem)
                                .subscribe(() -> {
                                    Toast.makeText(sContext, "Add Success", Toast.LENGTH_SHORT).show();
                                }, throwable -> {
                                    Log.e(TAG, "addToFavorite: " + throwable.getMessage());
                                });
                    }
                }, throwable -> {
                    Log.e(TAG, "CheckInFavorite: " + throwable.getMessage());
                });
    }

    @Override
    public int getItemCount() {
        return listAudio.size();
    }

    public void deleteAudio(int position, View view) {
        Uri contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                Long.parseLong(listAudio.get(position).getId()));
        File file = new File(listAudio.get(position).getPath());
        boolean isDelete = file.delete();
        if (isDelete) {
            try {
                sContext.getContentResolver().delete(contentUri, null, null);
            } catch (SecurityException se) {
                Log.e(TAG, "deleteAudio: " + se.getMessage());
            }
            listAudio.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, listAudio.size());
            Common.showSnackBar(sContext, R.layout.snack_success_layout, view,
                    sContext.getString(R.string.delete_audio_success));
        } else
            Common.showSnackBar(sContext, R.layout.snack_error_layout, view,
                    sContext.getString(R.string.error_delete_audio));
    }
}
