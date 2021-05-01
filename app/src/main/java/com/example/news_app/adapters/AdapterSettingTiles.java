package com.example.news_app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.news_app.databinding.ItemSettingsBinding;
import com.example.news_app.enums.SettingsPoints;
import com.example.news_app.models.SettingTile;

import java.util.ArrayList;

public class AdapterSettingTiles extends RecyclerView.Adapter<AdapterSettingTiles.ViewHolder> {

    private Context context;
    private OnClickedSettingsItemListener mListener;

    public AdapterSettingTiles(Context context, OnClickedSettingsItemListener mListener) {
        this.context = context;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSettingsBinding binding = ItemSettingsBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final SettingsPoints point = SettingsPoints.values()[position];
        holder.binding.imgTileSettings.setImageResource(point.getIdIc());
        holder.binding.tvTileSettings.setText(context.getResources().getString(point.getIdUnderText()));

        holder.binding.layoutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onClicked(point);
            }
        });

    }

    @Override
    public int getItemCount() {
        return SettingsPoints.values().length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemSettingsBinding binding;

        public ViewHolder(ItemSettingsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface OnClickedSettingsItemListener {
        void onClicked(SettingsPoints point);
    }

}
