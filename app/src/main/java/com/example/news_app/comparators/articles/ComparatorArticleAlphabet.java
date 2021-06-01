package com.example.news_app.comparators.articles;

import com.example.news_app.models.News;

import java.util.Comparator;

public class ComparatorArticleAlphabet implements Comparator<News> {

    boolean stage;

    public ComparatorArticleAlphabet() {
        stage = true;
    }

    @Override
    public int compare(News o1, News o2) {
        if (stage) return o1.getTitle().compareTo(o2.getTitle());
        else return -1 * o1.getTitle().compareTo(o2.getTitle());
    }

    public void nextStage() {
        stage = !stage;
    }
}
