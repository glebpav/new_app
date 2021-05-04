package com.example.news_app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.news_app.databinding.ItemHistorySrtBinding;
import com.example.news_app.databinding.ItemSettingsBinding;
import com.example.news_app.enums.SettingsPoints;
import com.example.news_app.enums.Sources;
import com.example.news_app.models.User;

import soup.neumorphism.ShapeType;

public class AdapterSourcesTiles extends RecyclerView.Adapter<AdapterSourcesTiles.ViewHolder>{


    private final Context context;
    private User user;
    Boolean isUsed;
    private final OnClickedSourcesItemListener mListener;

    public AdapterSourcesTiles(User user, Context context, OnClickedSourcesItemListener mListener) {
        this.user = user;
        this.context = context;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public AdapterSourcesTiles.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSettingsBinding binding = ItemSettingsBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false);
        return new AdapterSourcesTiles.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Sources point = Sources.values()[position];
        holder.binding.imgTileSettings.setImageResource(point.getIdIc());
        holder.binding.tvTileSettings.setText(context.getResources().getString(point.getIdUnderText()));

        isUsed = false;

        for(String domen : user.getSites().split(";"))
            if (domen.equals(context.getResources().getString(point.getIdDomen()))){
                holder.binding.nuemorphCardSource.setShapeType(ShapeType.PRESSED);
                isUsed = true;
                break;
            }

        holder.binding.layoutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onClicked(point, isUsed);
                isUsed = !isUsed;
                holder.binding.nuemorphCardSource.setShapeType
                        (isUsed?ShapeType.PRESSED:ShapeType.DEFAULT);
            }
        });


    }

    @Override
    public int getItemCount() {
        return Sources.values().length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemSettingsBinding binding;

        public ViewHolder(ItemSettingsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface OnClickedSourcesItemListener {
        void onClicked(Sources point, boolean wasUsed);
    }
}
