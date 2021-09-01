package com.oceanforit.audioplayer.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.oceanforit.audioplayer.R;
import com.oceanforit.audioplayer.callBack.IRecyclerItemCLickListener;
import com.oceanforit.audioplayer.common.Common;
import com.oceanforit.audioplayer.models.Audio;
import com.oceanforit.audioplayer.ui.albumDetails.AlbumDetailsActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ArtistsAdapter extends RecyclerView.Adapter<ArtistsAdapter.ArtistHolder> {
    private static final String TAG = ArtistsAdapter.class.getSimpleName();

    private Context sContext;
    private List<Audio> listArtist;
    private LayoutInflater inflater;

    public ArtistsAdapter(Context sContext, List<Audio> listArtist) {
        this.sContext = sContext;
        this.listArtist = listArtist;

        inflater = LayoutInflater.from(sContext);
    }

    @NonNull
    @Override
    public ArtistHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ArtistHolder(inflater.inflate(R.layout.row_album, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistHolder holder, int position) {

        Audio audio = listArtist.get(position);

        holder.txtArtistName.setText(audio.getArtist());
        byte[] bytes = Common.getAlbumArt(audio.getPath());

        Log.d(TAG, "onBindViewHolder:  " + bytes);
        if (bytes != null) {
            holder.coverArtist.setBackground(null);
            Glide.with(sContext)
                    .asBitmap()
                    .load(bytes)
                    .into(holder.coverArtist);
        } else {
            holder.coverArtist.setBackgroundResource(R.drawable.image_bg);
            Glide.with(sContext)
                    .load(R.drawable.icon_white)
                    .into(holder.coverArtist);

        }

        holder.setItemCLickListener((view, position1) -> {
            Intent intent = new Intent(sContext, AlbumDetailsActivity.class);
            intent.putExtra(Common.KEY_AUDIO, audio.getArtist());
            sContext.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return listArtist.size();
    }

    static class ArtistHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.iv_cover_album)
        ImageView coverArtist;
        @BindView(R.id.txt_album_name)
        TextView txtArtistName;

        private IRecyclerItemCLickListener itemCLickListener;

        public ArtistHolder(@NonNull View itemView) {
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
