package com.example.news_app.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.news_app.R;
import com.example.news_app.databinding.ItemSettingsBinding;
import com.example.news_app.enums.SettingsPoints;
import com.example.news_app.network.MakeRequests;

import www.sanju.motiontoast.MotionToast;

public class AdapterSettingTiles extends RecyclerView.Adapter<AdapterSettingTiles.ViewHolder> {

    private final Context context;
    private final OnClickedSettingsItemListener mListener;

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

        holder.binding.layoutItem.setOnClickListener(v -> {
            if (MakeRequests.isInternetAvailable(context)
                    || point == SettingsPoints.CHANGE_CURRENCY
                    || point == SettingsPoints.SHOW_HISTORY
                    || point == SettingsPoints.CHANGE_FORMAT)
                mListener.onClicked(point);
            else {
                MotionToast.Companion.createColorToast((Activity) context,
                        "Нет интернет соединения",
                        "попробуйте перезайти поже",
                        MotionToast.TOAST_ERROR,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(context, R.font.helvetica_regular));
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
