package com.example.news_app.fragments.dialogFragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.news_app.R;
import com.example.news_app.adapters.AdapterArticleSmallTiles;
import com.example.news_app.databinding.DialogFragmentSmallArticleBinding;
import com.example.news_app.models.News;

public class DialogFragmentSmallArticle extends DialogFragment {

    private DialogFragmentSmallArticleBinding binding;
    private Context mContext;
    private News article;

    public DialogFragmentSmallArticle(Context mContext, News article) {
        this.mContext = mContext;
        this.article = article;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogFragmentSmallArticleBinding.inflate(inflater, container, false);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }

        binding.tvTileTitle.setText(article.getTitle());
        binding.tvTileDescription.setText(article.getDescription());
        binding.gradientLineBottom.setBackgroundResource(R.drawable.deep_gradient_bottom1);

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

        binding.btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        setRatingValue(Double.parseDouble(article.getRating()));
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
}
