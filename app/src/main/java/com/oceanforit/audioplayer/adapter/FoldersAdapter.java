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

import com.oceanforit.audioplayer.R;
import com.oceanforit.audioplayer.callBack.IRecyclerItemCLickListener;
import com.oceanforit.audioplayer.common.Common;
import com.oceanforit.audioplayer.models.Audio;
import com.oceanforit.audioplayer.ui.albumDetails.AlbumDetailsActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FoldersAdapter extends RecyclerView.Adapter<FoldersAdapter.FoldersHolder> {
    private static final String TAG = FoldersAdapter.class.getSimpleName();

    private List<String> listFolder;
    private Context sContext;
    private LayoutInflater inflater;

    public FoldersAdapter(Context sContext, List<String> listFolder) {
        this.listFolder = listFolder;
        this.sContext = sContext;

        inflater = LayoutInflater.from(sContext);
    }

    @NonNull
    @Override
    public FoldersHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FoldersHolder(inflater.inflate(R.layout.row_folder, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FoldersHolder holder, int position) {

        int index = listFolder.get(position).lastIndexOf("/");
        String folderName = listFolder.get(position).substring(index + 1);

        holder.txtFolderName.setText(folderName);
        holder.txtFolderPath.setText(listFolder.get(position));

        holder.setItemCLickListener((view, position1) -> {
            Intent intent = new Intent(sContext, AlbumDetailsActivity.class);
            intent.putExtra(Common.KEY_FOLDER_NAME, listFolder.get(position));
//            intent.putExtra(Common.KEY_FOLDER_DETAILS, Common.FOLDER_DETAILS);
            sContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listFolder.size();
    }

    static class FoldersHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.iv_cover_folder)
        ImageView ivCoverFolder;
        @BindView(R.id.txt_folder_name)
        TextView txtFolderName;
        @BindView(R.id.txt_folder_path)
        TextView txtFolderPath;

        private IRecyclerItemCLickListener itemCLickListener;

        public FoldersHolder(@NonNull View itemView) {
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
