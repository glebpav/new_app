package com.example.news_app.comparators.articles;

import com.example.news_app.models.News;

import java.util.Comparator;

public class ComparatorArticlePositive implements Comparator<News> {
    @Override
    public int compare(News o1, News o2) {
        return -1 * o1.getRating().compareTo(o2.getRating());
    }
}
