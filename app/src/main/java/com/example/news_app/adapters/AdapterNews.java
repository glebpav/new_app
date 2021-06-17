package com.example.news_app.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.viewpager.widget.PagerAdapter;

import com.example.news_app.R;
import com.example.news_app.databinding.ItemArticleBinding;
import com.example.news_app.models.News;
import com.example.news_app.network.MakeRequests;

import java.util.ArrayList;

import www.sanju.motiontoast.MotionToast;

import static android.content.Context.MODE_PRIVATE;
import static java.security.AccessController.getContext;

public class AdapterNews extends PagerAdapter {

    private static final String TAG = "ADAPTER_NEWS_SPACE";

    private String toneType;
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
        toneType = getToneFormatFromPref();
        Log.d(TAG, "AdapterNews: news tone - " + toneType);
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

        binding.btnTileMoreInf.setOnClickListener(v -> {
            if (MakeRequests.isInternetAvailable(mContext))
            mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(article.getUrl())));
            else {
                MotionToast.Companion.createColorToast((Activity) mContext, "Нет интернет соединения", "попробуйте перезайти поже",
                        MotionToast.TOAST_ERROR,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(mContext, R.font.helvetica_regular));
            }
        });
        binding.btnShareNews.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, "Sharing URL");
            i.putExtra(Intent.EXTRA_TEXT, article.getUrl());
            mContext.startActivity(Intent.createChooser(i, "Поделиться URL"));
        });

        setRatingValue(rating);
        container.addView(binding.getRoot());
        return binding.getRoot();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    private void setRatingValue(double ratingValue) {
        Log.d(TAG, "setRatingValue: tone - " + toneType);
        if (toneType == null || toneType.equals("REG")) {
            if (ratingValue < 0.35) {
                binding.tvTileRating.setText("Негативно");
            } else if (ratingValue <= 0.65) {
                binding.tvTileRating.setText("Нейтрально");
            } else {
                binding.tvTileRating.setText("Позитивно");
            }
        } else {
            binding.tvTileRating.setText(String.format("%.2f", ratingValue));
        }
    }

    private String getToneFormatFromPref(){
        SharedPreferences toneTypePreferences = mContext.getSharedPreferences(mContext.getResources().
                getString(R.string.tone_format), MODE_PRIVATE);
        return toneTypePreferences.getString(mContext.getResources().
                getString(R.string.tone_format_key), null);
    }

    public void updateToneType(){
        toneType = getToneFormatFromPref();
    }
}
