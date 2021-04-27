package com.example.news_app.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.PagerAdapter;

import com.example.news_app.R;
import com.example.news_app.models.News;

import java.util.ArrayList;

import soup.neumorphism.NeumorphImageButton;

public class AdapterViewNews extends PagerAdapter {

    private Context mContext;
    private LayoutInflater layoutInflater;

    public ArrayList<News> getNewsArray() {
        return newsArray;
    }

    public void setNewsArray(ArrayList<News> newsArray) {
        this.newsArray = newsArray;
    }

    ArrayList<News> newsArray;
    int position;


    public AdapterViewNews(Context context, ArrayList<News> newsArray) {
        mContext = context;
        this.newsArray = new ArrayList<>();
        this.newsArray = newsArray;
        Log.d("asd", String.valueOf(newsArray.size()));
    }

    @Override
    public int getCount() {
        return newsArray.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.article_tile, container, false);

        NeumorphImageButton btnReadMore, btnShareUrl;
        //NeumorphCardView card_view_tile;
        CardView card_view_tile;
        TextView tv_title, tv_description, tv_rating;

        final News article = newsArray.get(position);
        this.position = position;
        double rating = Double.valueOf(article.getRating());

        tv_title = view.findViewById(R.id.tv_tile_title);
        tv_description = view.findViewById(R.id.tv_tile_description);
        tv_rating = view.findViewById(R.id.tv_tile_rating);
        btnReadMore = view.findViewById(R.id.btn_tile_more_inf);
        btnShareUrl = view.findViewById(R.id.btn_share_news);
        //card_view_tile = view.findViewById(R.id.card_tile);
        card_view_tile = view.findViewById(R.id.card_tile);

        tv_title.setText(article.getTitle());
        tv_description.setText(article.getDescription());
        btnReadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(article.getUrl())));
            }
        });
        btnShareUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, "Sharing URL");
                i.putExtra(Intent.EXTRA_TEXT, article.getUrl());
                mContext.startActivity(Intent.createChooser(i, "Поделиться URL"));
            }
        });

        set_rating_value(rating, tv_rating, card_view_tile, btnReadMore);
        container.addView(view, 0);
        return view;
    }

    public void set_rating_value(double rating_value, TextView tv_rating, CardView card_view_tile, View btn) {

        Log.d("asd", String.valueOf(rating_value));
        if (rating_value < 0.35) {
            tv_rating.setText("Негативно");
            Log.d("asd", "pos");
            //tv_rating.setTextColor(Color.argb(100,150,0, 0));
            //card_view_tile.setShadowColorDark(Color.argb(100,255,0, 0));
            //btn.setShadowColorDark(Color.argb(100,255,0, 0));
            //btn.setShadowColorLight(Color.argb(100,255,0, 0));
        } else if (rating_value <= 0.65) {
            tv_rating.setText("Нейтрально");
            Log.d("asd", "neut");
            //tv_rating.setTextColor(Color.argb(100,0,0, 150));
            //card_view_tile.setShadowColorDark(Color.argb(100,0,0, 255));
            //btn.setShadowColorDark(Color.argb(100,0,0, 255));
            //btn.setShadowColorLight(Color.argb(100,0,0, 255));
        } else {
            tv_rating.setText("Позитивно");
            Log.d("asd", "neg");
            //tv_rating.setTextColor(Color.argb(100,0,150, 0));
            //card_view_tile.setShadowColorDark(Color.argb(100,0,255, 0));
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
