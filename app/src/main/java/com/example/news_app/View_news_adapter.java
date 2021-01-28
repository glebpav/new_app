package com.example.news_app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;

import soup.neumorphism.NeumorphButton;
import soup.neumorphism.NeumorphCardView;

public class View_news_adapter extends PagerAdapter {

    private Context mContext;
    LayoutInflater layoutInflater;
    ArrayList <News> news_array;
    int position;


    public View_news_adapter(Context context, ArrayList<News> news_array) {
        mContext = context;
        this.news_array = new ArrayList<>();
        this.news_array = news_array;
        Log.d("asd", String.valueOf(news_array.size()));

    }

    @Override
    public int getCount() {
        return news_array.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.article_tile, container, false);

        NeumorphButton btn;
        NeumorphCardView card_view_tile;
        TextView tv_title, tv_description, tv_rating;

        final News article = news_array.get(position);
        this.position = position;
        double rating = Double.valueOf(article.rating);

        tv_title = view.findViewById(R.id.tv_tile_title);
        tv_description = view.findViewById(R.id.tv_tile_description);
        tv_rating = view.findViewById(R.id.tv_tile_rating);
        btn = view.findViewById(R.id.btn_tile_more_inf);
        card_view_tile = view.findViewById(R.id.card_tile);

        tv_title.setText(article.title);
        tv_description.setText(article.description);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(article.url)));
            }
        });

        set_rating_value(rating, tv_rating, card_view_tile, btn);
        container.addView(view, 0);
        return view;
    }

    public void set_rating_value(double rating_value, TextView tv_rating, NeumorphCardView card_view_tile, NeumorphButton btn){

        Log.d("asd", String.valueOf(rating_value));
        if (rating_value < 0.35){
            tv_rating.setText("Негативно");
            Log.d("asd", "pos");
            tv_rating.setTextColor(Color.argb(100,150,0, 0));
            card_view_tile.setShadowColorDark(Color.argb(100,255,0, 0));
            //btn.setShadowColorDark(Color.argb(100,255,0, 0));
            //btn.setShadowColorLight(Color.argb(100,255,0, 0));
        }
        else if (rating_value <= 0.65){
            tv_rating.setText("Нейтрально");
            Log.d("asd", "neut");
            tv_rating.setTextColor(Color.argb(100,0,0, 150));
            card_view_tile.setShadowColorDark(Color.argb(100,0,0, 255));
            //btn.setShadowColorDark(Color.argb(100,0,0, 255));
            //btn.setShadowColorLight(Color.argb(100,0,0, 255));
        }
        else {
            tv_rating.setText("Позитивно");
            Log.d("asd", "neg");
            tv_rating.setTextColor(Color.argb(100,0,150, 0));
            card_view_tile.setShadowColorDark(Color.argb(100,0,255, 0));
            //btn.setShadowColorDark(Color.argb(100,0,255, 0));
            //btn.setShadowColorLight(Color.argb(100,0,255, 0));
        }

    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

}
