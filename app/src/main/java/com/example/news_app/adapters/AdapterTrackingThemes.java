package com.example.news_app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.news_app.databinding.ItemTrackingNewsBinding;

import java.util.List;

public class AdapterTrackingThemes extends RecyclerView.Adapter<AdapterTrackingThemes.TrackingThemesHolder> {

    public void setThemesList(List<String> themesList) {
        this.themesList = themesList;
    }

    private List<String> themesList;
    private final OnDeleteItemClickedListener onDeleteItemClickedListener;
    private final OnThemeSelectedListener onThemeSelectedListener;

    public AdapterTrackingThemes(List<String> themesList, OnDeleteItemClickedListener onDeleteItemClickedListener,
                                 OnThemeSelectedListener onThemeSelectedListener) {
        this.themesList = themesList;
        this.onDeleteItemClickedListener = onDeleteItemClickedListener;
        this.onThemeSelectedListener = onThemeSelectedListener;
    }

    @Override
    public TrackingThemesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTrackingNewsBinding binding = ItemTrackingNewsBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false);

        return new TrackingThemesHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TrackingThemesHolder holder, int position) {
        holder.binding.tvTrackingTheme.setText(themesList.get(position));
    }

    @Override
    public int getItemCount() {
        if (themesList == null) return 0;
        return themesList.size();
    }

    class TrackingThemesHolder extends RecyclerView.ViewHolder {

        ItemTrackingNewsBinding binding;

        public TrackingThemesHolder(ItemTrackingNewsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onDeleteItemClickedListener.onDeleteItem(themesList.get(getAdapterPosition()));
                }
            });

            binding.btnOpen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onThemeSelectedListener.onThemeSelected(themesList.get(getAdapterPosition()));
                }
            });
        }
    }

    public interface OnDeleteItemClickedListener {
        void onDeleteItem(String theme);
    }

    public interface OnThemeSelectedListener {
        void onThemeSelected(String theme);
    }

}
