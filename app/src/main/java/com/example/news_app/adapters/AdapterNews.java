package com.example.news_app.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.news_app.R;
import com.example.news_app.databinding.ItemArticleBinding;
import com.example.news_app.models.News;

import java.util.ArrayList;

public class AdapterNews extends PagerAdapter {

    private final Context mContext;
    private ArrayList<News> listNews;
    private ItemArticleBinding binding;


    public void setListNews(ArrayList<News> listNews) {
        this.listNews = listNews;
    }

    public AdapterNews(Context context, ArrayList<News> listNews) {
        mContext = context;
        this.listNews = new ArrayList<>();
        this.listNews = listNews;
    }

    @Override
    public int getCount() {
        if (listNews == null) return 0;
        return listNews.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        binding = ItemArticleBinding.inflate(layoutInflater, container, false);

        final News article = listNews.get(position);
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

        setRatingValue(rating);
        container.addView(binding.getRoot());
        return binding.getRoot();
    }

    public void setRatingValue(double rating_value) {
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
