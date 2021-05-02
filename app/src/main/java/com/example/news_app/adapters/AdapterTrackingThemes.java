package com.example.news_app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.news_app.R;

import java.util.List;

import soup.neumorphism.NeumorphFloatingActionButton;

public class AdapterTrackingThemes extends RecyclerView.Adapter<AdapterTrackingThemes.Tracking_themes_Holder> {

    public void setThemes_list(List<String> themes_list) {
        this.themes_list = themes_list;
    }

    private List<String> themes_list;
    private final OnDeleteItemClickdeListener onDeleteItemClickdeListener;
    private final OnThemeSelectedListener onThemeSelectedListener;

    public AdapterTrackingThemes(List<String> themes_list, OnDeleteItemClickdeListener onDeleteItemClickdeListener, OnThemeSelectedListener onThemeSelectedListener) {
        this.themes_list = themes_list;
        this.onDeleteItemClickdeListener = onDeleteItemClickdeListener;
        this.onThemeSelectedListener = onThemeSelectedListener;
    }

    @Override
    public Tracking_themes_Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.tracking_tile, parent, false);

        Tracking_themes_Holder tracking_themes_holder = new Tracking_themes_Holder(view);

        return tracking_themes_holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Tracking_themes_Holder holder, int position) {
        holder.bind(themes_list.get(position));
    }

    @Override
    public int getItemCount() {
        return themes_list.size();
    }

    class Tracking_themes_Holder extends RecyclerView.ViewHolder {

        TextView tv_tracking_theme;
        NeumorphFloatingActionButton btn_delete, btn_open;

        public Tracking_themes_Holder(@NonNull View itemView) {
            super(itemView);

            tv_tracking_theme = itemView.findViewById(R.id.tv_tracking_theme);
            btn_delete = itemView.findViewById(R.id.btn_delete);
            btn_open = itemView.findViewById(R.id.btn_open);

            btn_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onDeleteItemClickdeListener.onDeleteItem(themes_list.get(getAdapterPosition()));
                }
            });

            btn_open.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onThemeSelectedListener.onThemeSelected(themes_list.get(getAdapterPosition()));
                }
            });
        }

        void bind(String theme) {
            tv_tracking_theme.setText(theme);
        }
    }

    public interface OnDeleteItemClickdeListener {
        void onDeleteItem(String theme);
    }

    public interface OnThemeSelectedListener {
        void onThemeSelected(String theme);
    }

}
