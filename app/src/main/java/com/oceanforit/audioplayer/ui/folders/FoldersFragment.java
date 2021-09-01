package com.oceanforit.audioplayer.ui.folders;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.oceanforit.audioplayer.HomeActivity;
import com.oceanforit.audioplayer.R;
import com.oceanforit.audioplayer.adapter.FoldersAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FoldersFragment extends Fragment {

    @BindView(R.id.recycler_folders)
    RecyclerView recyclerFolders;

    private static FoldersFragment instance;
    public static FoldersFragment getInstance(){
        return instance == null ? new FoldersFragment() : instance;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View layoutView = inflater.inflate(R.layout.fragment_folders, container, false);
        ButterKnife.bind(this, layoutView);
        recyclerFolders.setHasFixedSize(true);
        recyclerFolders.setLayoutManager(new LinearLayoutManager(getActivity()));
        FoldersAdapter foldersAdapter = new FoldersAdapter(getActivity(), HomeActivity.listFolders);
        recyclerFolders.setAdapter(foldersAdapter);
        return layoutView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
