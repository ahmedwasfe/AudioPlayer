package com.oceanforit.audioplayer.adapter;

import android.content.Context;
import android.content.Intent;
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
import com.oceanforit.audioplayer.callBack.IRecyclerItemCLickListener;
import com.oceanforit.audioplayer.common.Common;
import com.oceanforit.audioplayer.favorite.FavDataSource;
import com.oceanforit.audioplayer.favorite.FavoriteAudio;
import com.oceanforit.audioplayer.favorite.FavoriteDataSource;
import com.oceanforit.audioplayer.favorite.FavoriteDatabase;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteHolder> {
    private static final String TAG = FavoriteAdapter.class.getSimpleName();

    private Context context;
    private List<FavoriteAudio> listFavorites;
    private LayoutInflater inflater;

    private FavoriteDataSource favoriteDataSource;

    public FavoriteAdapter(Context context, List<FavoriteAudio> listFavorites) {
        this.context = context;
        this.listFavorites = listFavorites;
        inflater = LayoutInflater.from(context);

        favoriteDataSource = new FavDataSource(FavoriteDatabase.getInstance(context).favoriteDAO());
    }

    @NonNull
    @Override
    public FavoriteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FavoriteHolder(inflater.inflate(R.layout.row_audio, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteHolder holder, int position) {
        FavoriteAudio favorite = listFavorites.get(position);

        holder.txtTitle.setText(favorite.getTitle());
        holder.txtArtist.setText(favorite.getArtist());

        byte[] bytes = Common.getAlbumArt(favorite.getPath());

        Log.d(TAG, "onBindViewHolder:  " + bytes);
        if (bytes != null) {
            holder.imageAudio.setBackgroundResource(R.drawable.img_bg);
            Glide.with(context)
                    .asBitmap()
                    .load(bytes)
                    .into(holder.imageAudio);
        } else {
            holder.imageAudio.setBackgroundResource(R.drawable.image_bg);
            Glide.with(context)
                    .load(R.drawable.icon_white)
                    .into(holder.imageAudio);
        }

        holder.setItemCLickListener((view, position1) -> {
            Intent intent = new Intent(context, PlayerActivity.class);
            intent.putExtra(Common.KEY_POSITION, position);
            context.startActivity(intent);
        });

        holder.moreOptions.setOnClickListener(v -> {

            PopupMenu menu = new PopupMenu(context, v);
            menu.getMenuInflater().inflate(R.menu.more_menu, menu.getMenu());
            menu.show();
            menu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.action_delete:
                        item.setVisible(false);
                        break;
                    case R.id.action_favorite:
                        item.setVisible(false);
                        break;
                    case R.id.action_remove_favorite:
                        removeFromFavorite(favorite, position);
                        break;
                }
                return true;
            });
        });
    }

    private void removeFromFavorite(FavoriteAudio favorite, int position) {

        favoriteDataSource.deleteFavorite(favorite)
                .subscribe(integer -> {
                    listFavorites.remove(position);
                    notifyItemRemoved(position);
                    notifyDataSetChanged();
                    Toast.makeText(context, "Remove success", Toast.LENGTH_SHORT).show();
                }, throwable -> {
                    Log.e(TAG, "removeFromFavorite: " + throwable.getMessage());
                });
    }

    @Override
    public int getItemCount() {
        return listFavorites.size();
    }

    class FavoriteHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.iv_audio)
        ImageView imageAudio;
        @BindView(R.id.iv_more)
        ImageView moreOptions;
        @BindView(R.id.txt_title)
        TextView txtTitle;
        @BindView(R.id.txt_artist)
        TextView txtArtist;

        private IRecyclerItemCLickListener itemCLickListener;

        public FavoriteHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
        }

        public void setItemCLickListener(IRecyclerItemCLickListener itemCLickListener) {
            this.itemCLickListener = itemCLickListener;
        }

        @Override
        public void onClick(View v) {
            itemCLickListener.onItemClickListener(v, getAbsoluteAdapterPosition());
        }
    }
}
