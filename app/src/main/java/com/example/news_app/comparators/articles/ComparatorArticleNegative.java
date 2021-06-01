package com.example.news_app.comparators.articles;

import com.example.news_app.models.News;

import java.util.Comparator;

public class ComparatorArticleNegative implements Comparator<News> {
    @Override
    public int compare(News o1, News o2) {
        return o1.getRating().compareTo(o2.getRating());
    }
}
