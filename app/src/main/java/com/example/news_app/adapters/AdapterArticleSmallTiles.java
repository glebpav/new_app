package com.example.news_app.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.news_app.R;
import com.example.news_app.databinding.ItemArticleBinding;
import com.example.news_app.databinding.ItemArticleSmallBinding;
import com.example.news_app.fragments.dialogFragments.DialogFragmentSmallArticle;
import com.example.news_app.models.News;

import java.util.ArrayList;

public class AdapterArticleSmallTiles extends RecyclerView.Adapter<AdapterArticleSmallTiles.ViewHolder> {

    private final Context mContext;
    private ArrayList <News> listNews;

    public void setListNews(ArrayList<News> listNews) {
        this.listNews = listNews;
    }

    public AdapterArticleSmallTiles(ArrayList<News> listNews, Context mContext) {
        this.listNews = listNews;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemArticleSmallBinding binding  = ItemArticleSmallBinding.inflate(LayoutInflater.from
                (parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final News article = listNews.get(position);
        double rating = Double.parseDouble(article.getRating());

        holder.binding.tvArticleTitle.setText(article.getTitle());

        //holder.binding.tvTileTitle.setText(article.getTitle());
        //holder.binding.tvTileDescription.setText(article.getDescription());
        //holder.binding.gradientLineBottom.setBackgroundResource(position%2==0? R.drawable.deep_gradient_bottom1:R.drawable.deep_gradient_bottom2);

        /*holder.binding.btnTileMoreInf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(article.getUrl())));
            }
        });
        holder.binding.btnShareNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, "Sharing URL");
                i.putExtra(Intent.EXTRA_TEXT, article.getUrl());
                mContext.startActivity(Intent.createChooser(i, "Поделиться URL"));
            }
        });*/

        holder.binding.cardViewSmallArticle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragmentSmallArticle dialogFragmentSmallArticle = new
                        DialogFragmentSmallArticle(mContext, article);
                dialogFragmentSmallArticle.show(((AppCompatActivity)mContext).getSupportFragmentManager(),
                        "AdapterSmallArticle");
            }
        });

        setRatingValue(rating, holder);
    }

    @Override
    public int getItemCount() {
        if (listNews == null) return 0;
        return listNews.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ItemArticleSmallBinding binding;

        public ViewHolder(ItemArticleSmallBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public void setRatingValue(double rating_value, final ViewHolder holder) {
        /*if (rating_value < 0.35) {
            holder.binding.tvTileRating.setText("Негативно");
        } else if (rating_value <= 0.65) {
            holder.binding.tvTileRating.setText("Нейтрально");
        } else {
            holder.binding.tvTileRating.setText("Позитивно");
        }*/
    }
}
