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
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.oceanforit.audioplayer.PlayerActivity;
import com.oceanforit.audioplayer.R;
import com.oceanforit.audioplayer.callBack.IAudioClickListener;
import com.oceanforit.audioplayer.callBack.IRecyclerItemCLickListener;
import com.oceanforit.audioplayer.common.Common;
import com.oceanforit.audioplayer.favorite.FavDataSource;
import com.oceanforit.audioplayer.favorite.FavoriteAudio;
import com.oceanforit.audioplayer.favorite.FavoriteDataSource;
import com.oceanforit.audioplayer.favorite.FavoriteDatabase;
import com.oceanforit.audioplayer.models.Audio;
import com.oceanforit.audioplayer.models.AudioModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.AudioHolder> {
    private static final String TAG = AudioAdapter.class.getSimpleName();

    private Context sContext;
    public static List<Audio> listAudio;
    private LayoutInflater inflater;
    private static IAudioClickListener audioClickListener;

    private FavoriteDataSource favoriteDataSource;

    public AudioAdapter(Context sContext, List<Audio> listAudio, IAudioClickListener audioClickListener) {
        this.sContext = sContext;
        this.listAudio = listAudio;
        this.audioClickListener = audioClickListener;

        inflater = LayoutInflater.from(sContext);

        favoriteDataSource = new FavDataSource(FavoriteDatabase.getInstance(sContext).favoriteDAO());
    }

    @NonNull
    @Override
    public AudioHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AudioHolder(inflater.inflate(R.layout.row_audio, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AudioHolder holder, int position) {

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

//        holder.itemView.setOnClickListener(v -> {
//            AudioModel audioModel = new AudioModel();
//            audioModel.setListAudio(listAudio);
//            audioModel.setPosition(position);
//            audioClickListener.onAudioClickListener(v, audioModel);
//        });
        holder.setItemCLickListener((view, position1) -> {
            Intent intent = new Intent(sContext, PlayerActivity.class);
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
                    favoriteDataSource.addFavorite(favoriteItem)
                            .subscribe(() -> {
                                Toast.makeText(sContext, "Add Success", Toast.LENGTH_SHORT).show();
                            }, throwable1 -> {
                                Log.e(TAG, "addToFavorite: " + throwable1.getMessage());
                            });
                    Log.e(TAG, "CheckInFavorite: " + throwable.getMessage());
                });
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

    public void updateList(List<Audio> audios) {
        listAudio = new ArrayList<>();
        listAudio.addAll(audios);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return listAudio.size();
    }

    public static class AudioHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.iv_audio)
        ImageView imageAudio;
        @BindView(R.id.iv_more)
        ImageView moreOptions;
        @BindView(R.id.txt_title)
        TextView txtTitle;
        @BindView(R.id.txt_artist)
        TextView txtArtist;

        private IRecyclerItemCLickListener itemCLickListener;

        public AudioHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
        }

        public void setItemCLickListener(IRecyclerItemCLickListener itemCLickListener) {
            this.itemCLickListener = itemCLickListener;
        }

        @Override
        public void onClick(View v) {
            itemCLickListener.onItemClickListener(v, getAdapterPosition());
        }
    }
}
