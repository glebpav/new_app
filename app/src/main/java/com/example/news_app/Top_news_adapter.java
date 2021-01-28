package com.example.news_app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;

import soup.neumorphism.NeumorphButton;
import soup.neumorphism.NeumorphCardView;

public class Top_news_adapter extends PagerAdapter {

    Context mContext;
    ArrayList <News> list_news;
    LayoutInflater layoutInflater;

    public Top_news_adapter(Context mContext, ArrayList<News> list_news) {
        this.mContext = mContext;
        this.list_news = list_news;
    }

    @Override
    public int getCount() {
        return list_news.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.hot_article_tile, container, false);

        final News article = list_news.get(position);
        double rating = Double.valueOf(article.rating);

        NeumorphButton btn;
        NeumorphCardView card_view_tile;
        TextView tv_title, tv_rating;

        tv_title = view.findViewById(R.id.tv_tile_title);
        tv_rating = view.findViewById(R.id.tv_tile_rating);
        btn = view.findViewById(R.id.btn_tile_more_inf);
        card_view_tile = view.findViewById(R.id.card_tile);

        tv_title.setText(article.title);
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
