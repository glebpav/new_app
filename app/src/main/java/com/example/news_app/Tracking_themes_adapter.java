package com.example.news_app;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;

import java.util.ArrayList;
import java.util.Set;

import soup.neumorphism.NeumorphFloatingActionButton;

class Tracking_themes_adapter extends RecyclerView.Adapter<Tracking_themes_adapter.Tracking_themes_Holder> {

    ArrayList <String> themes_list;
    Tracking fragment;
    ViewPager2 pager;
    MeowBottomNavigation meow;

    Tracking_themes_adapter(ArrayList <String> themes_list, Tracking fragment, ViewPager2 pager, MeowBottomNavigation meow){
        this.themes_list = themes_list;
        this.fragment = fragment;
        this.pager = pager;
        this.meow = meow;
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

            btn_delete.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    MakeRequests.Delete_theme delete_theme = fragment.requests.new Delete_theme(fragment, themes_list.get(getAdapterPosition()));
                    delete_theme.execute();
                }
            });

            btn_open.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences pref = fragment.getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
                    SharedPreferences.Editor edt = pref.edit();
                    edt.putString("SearchingTheme", themes_list.get(getAdapterPosition()));
                    edt.commit();

                    pager.setCurrentItem(0);
                    meow.show(0, true);
                }
            });
        }

        void bind(String theme){
            tv_tracking_theme.setText(theme);
        }
    }
}
