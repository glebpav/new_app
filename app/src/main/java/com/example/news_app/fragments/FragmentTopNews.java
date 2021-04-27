package com.example.news_app.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.news_app.adapters.AdapterTopNews;
import com.example.news_app.databinding.FragmentTopNewsBinding;
import com.example.news_app.network.MakeRequests;

import org.jetbrains.annotations.NotNull;


public class FragmentTopNews extends Fragment {

    public MakeRequests requests;
    public AdapterTopNews adapter;

    public FragmentTopNewsBinding binding;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTopNewsBinding.inflate(inflater, container, false);

        requests = new MakeRequests("https://analisinf.pythonanywhere.com/");
        MakeRequests.FindTopNews find_topNews = requests.new FindTopNews(this);
        find_topNews.execute();

        return binding.getRoot();
    }
}