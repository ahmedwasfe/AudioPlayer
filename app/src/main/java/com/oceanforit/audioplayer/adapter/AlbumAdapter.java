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
import androidx.cardview.widget.CardView;
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

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumHolder> {
    private static final String TAG = AlbumAdapter.class.getSimpleName();

    private Context sContext;
    private List<Audio> listAlbum;
    private LayoutInflater inflater;

    public AlbumAdapter(Context sContext, List<Audio> listAlbum) {
        this.sContext = sContext;
        this.listAlbum = listAlbum;

        inflater = LayoutInflater.from(sContext);
    }

    @NonNull
    @Override
    public AlbumHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AlbumHolder(inflater.inflate(R.layout.row_album, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumHolder holder, int position) {

//        if(position == 0){
//            CardView.LayoutParams layoutParams = new CardView.LayoutParams(CardView.LayoutParams.WRAP_CONTENT,
//                    CardView.LayoutParams.WRAP_CONTENT);
//            layoutParams.setMargins(5,80,5,5);
//            holder.coverAlbum.setLayoutParams(layoutParams);
//        }


        holder.txtAlbumName.setText(listAlbum.get(position).getAlbum());


        byte[] bytes = Common.getAlbumArt(listAlbum.get(position).getPath());

        Log.d(TAG, "onBindViewHolder:  " + bytes);
        if (bytes != null) {
            holder.coverAlbum.setBackground(null);
            Glide.with(sContext)
                    .asBitmap()
                    .load(bytes)
                    .into(holder.coverAlbum);
        } else {
            holder.coverAlbum.setBackgroundResource(R.drawable.image_bg);
            Glide.with(sContext)
                    .load(R.drawable.icon_white)
                    .into(holder.coverAlbum);

        }

        holder.setItemCLickListener((view, position1) -> {
            Intent intent = new Intent(sContext, AlbumDetailsActivity.class);
            intent.putExtra(Common.KEY_AUDIO, listAlbum.get(position).getAlbum());
            sContext.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return listAlbum.size();
    }

    static class AlbumHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.card_album)
        CardView cardView;
        @BindView(R.id.iv_cover_album)
        ImageView coverAlbum;
        @BindView(R.id.txt_album_name)
        TextView txtAlbumName;

        private IRecyclerItemCLickListener itemCLickListener;

        public AlbumHolder(@NonNull View itemView) {
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
