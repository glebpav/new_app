package com.example.news_app.fragments.dialogFragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.news_app.R;
import com.example.news_app.adapters.AdapterHistoryTiles;
import com.example.news_app.databinding.DialogFragmentHistoryBinding;

import java.util.List;

public class DialogFragmentHistory extends DialogFragment {

    DialogFragmentHistoryBinding binding;
    List<String> listHistory;
    AdapterHistoryTiles adapterHistoryTiles;
    OnAdapterTileHistoryClickedListener adapterTileHistoryClicked;

    public DialogFragmentHistory(List<String> listHistory, OnAdapterTileHistoryClickedListener
            adapterTileHistoryClicked) {
        this.listHistory = listHistory;
        this.adapterTileHistoryClicked = adapterTileHistoryClicked;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        inflater.inflate(R.layout.dialog_fragment_history, container, false);
        binding = DialogFragmentHistoryBinding.inflate(inflater, container, false);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }

        adapterHistoryTiles = new AdapterHistoryTiles(listHistory, historyItemListener);
        binding.recyclerView.setAdapter(adapterHistoryTiles);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return binding.getRoot();
    }

    AdapterHistoryTiles.OnClickHistoryItemListener historyItemListener = new AdapterHistoryTiles.OnClickHistoryItemListener() {
        @Override
        public void onClick(int position) {
            adapterTileHistoryClicked.onClicked(position);
        }
    };

    public interface OnAdapterTileHistoryClickedListener {
        void onClicked(int position);
    }
}
