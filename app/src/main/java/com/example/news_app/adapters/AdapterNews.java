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
import com.example.news_app.databinding.ItemArticleBinding;
import com.example.news_app.databinding.ItemTrackingNewsBinding;
import com.example.news_app.models.News;

import java.util.ArrayList;

import soup.neumorphism.NeumorphImageButton;

public class AdapterNews extends PagerAdapter {

    private final Context mContext;
    private ArrayList<News> newsArray;
    private ItemArticleBinding binding;


    public void setNewsArray(ArrayList<News> newsArray) {
        this.newsArray = newsArray;
    }

    public AdapterNews(Context context, ArrayList<News> newsArray) {
        mContext = context;
        this.newsArray = new ArrayList<>();
        this.newsArray = newsArray;
    }

    @Override
    public int getCount() {
        return newsArray.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        binding = ItemArticleBinding.inflate(layoutInflater, container, false);

        final News article = newsArray.get(position);
        double rating = Double.parseDouble(article.getRating());

        binding.tvTileTitle.setText(article.getTitle());
        binding.tvTileDescription.setText(article.getDescription());
        binding.gradientLineBottom.setBackgroundResource(position%2==0?R.drawable.deep_gradient_bottom1:R.drawable.deep_gradient_bottom2);

        binding.btnTileMoreInf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(article.getUrl())));
            }
        });
        binding.btnShareNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, "Sharing URL");
                i.putExtra(Intent.EXTRA_TEXT, article.getUrl());
                mContext.startActivity(Intent.createChooser(i, "Поделиться URL"));
            }
        });

        set_rating_value(rating);
        container.addView(binding.getRoot());
        return binding.getRoot();
    }

    public void set_rating_value(double rating_value) {
        if (rating_value < 0.35) {
            binding.tvTileRating.setText("Негативно");
        } else if (rating_value <= 0.65) {
            binding.tvTileRating.setText("Нейтрально");
        } else {
            binding.tvTileRating.setText("Позитивно");
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
