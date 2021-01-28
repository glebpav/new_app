package com.example.news_app;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;


public class Top_News extends Fragment {

    MakeRequests requests;
    Top_news_adapter adapter;

    RelativeLayout layout_error;
    ProgressBar progressBar;
    ViewPager view_pager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_top__news, container, false);

        view_pager = view.findViewById(R.id.view_pager);
        progressBar = view.findViewById(R.id.progress_circular);
        layout_error = view.findViewById(R.id.layout_error);

        requests = new MakeRequests("https://analisinf.pythonanywhere.com/");
        MakeRequests.Find_top_news find_top_news = requests.new Find_top_news(this);
        find_top_news.execute();

        return view;
    }
}