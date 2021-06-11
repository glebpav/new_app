package com.example.news_app.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.news_app.R;
import com.example.news_app.databinding.ItemTrackingNewsBinding;
import com.example.news_app.network.MakeRequests;

import java.util.List;

import www.sanju.motiontoast.MotionToast;

public class AdapterTrackingThemes extends RecyclerView.Adapter<AdapterTrackingThemes.TrackingThemesHolder> {

    public void setThemesList(List<String> themesList) {
        this.themesList = themesList;
    }

    private List<String> themesList;
    private final Context context;
    private final OnDeleteItemClickedListener onDeleteItemClickedListener;
    private final OnThemeSelectedListener onThemeSelectedListener;

    public AdapterTrackingThemes(List<String> themesList, Context context, OnDeleteItemClickedListener onDeleteItemClickedListener,
                                 OnThemeSelectedListener onThemeSelectedListener) {
        this.context = context;
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
            binding.btnDelete.setOnClickListener(v -> {
                if (MakeRequests.isInternetAvailable(context))
                onDeleteItemClickedListener.onDeleteItem(themesList.get(getAdapterPosition()));
                else {
                    MotionToast.Companion.createColorToast((Activity) context, "Нет интернет соединения", "попробуйте перезайти поже",
                            MotionToast.TOAST_ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(context, R.font.helvetica_regular));
                }
            });

            binding.btnOpen.setOnClickListener(v -> {
                if (MakeRequests.isInternetAvailable(context))
                    onThemeSelectedListener.onThemeSelected(themesList.get(getAdapterPosition()));
                else {
                    MotionToast.Companion.createColorToast((Activity) context, "Нет интернет соединения", "попробуйте перезайти поже",
                            MotionToast.TOAST_ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(context, R.font.helvetica_regular));
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
