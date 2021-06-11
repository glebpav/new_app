package com.example.news_app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.news_app.databinding.ItemHistorySrtBinding;

import java.util.List;

public class AdapterHistoryTiles extends RecyclerView.Adapter<AdapterHistoryTiles.ViewHolder> {


    List<String> listHistory;
    OnClickHistoryItemListener clickHistoryItemListener;

    public AdapterHistoryTiles(List<String> listHistory, OnClickHistoryItemListener clickHistoryItemListener) {
        this.listHistory = listHistory;
        this.clickHistoryItemListener = clickHistoryItemListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemHistorySrtBinding binding = ItemHistorySrtBinding.inflate(LayoutInflater.from
                (parent.getContext()),parent,false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.binding.tvHistoryTile.setText(listHistory.get(position));

        holder.binding.tvHistoryTile.setOnClickListener(v -> clickHistoryItemListener.onClick(position));
    }

    @Override
    public int getItemCount() {
        return listHistory.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ItemHistorySrtBinding binding;

        public ViewHolder(ItemHistorySrtBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface OnClickHistoryItemListener{
        void onClick(int position);
    }

}
