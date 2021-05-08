package com.example.news_app.fragments.usualFragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.news_app.adapters.AdapterTopNews;
import com.example.news_app.databinding.FragmentTopNewsBinding;
import com.example.news_app.fragments.dialogFragments.DialogFragmentProgressBar;
import com.example.news_app.models.News;
import com.example.news_app.network.MakeRequests;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class FragmentTopNews extends Fragment {

    private MakeRequests requests;
    private AdapterTopNews adapter;
    private DialogFragmentProgressBar fragmentProgressBar;

    private FragmentTopNewsBinding binding;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTopNewsBinding.inflate(inflater, container, false);

        binding.viewPager.setVisibility(View.INVISIBLE);
        binding.layoutError.setVisibility(View.INVISIBLE);
        fragmentProgressBar = new DialogFragmentProgressBar();
        fragmentProgressBar.show(getFragmentManager(), "FragmentTopNews");

        requests = new MakeRequests();
        MakeRequests.FindTopNews find_topNews = requests.new FindTopNews(onFindTopNewsListener);
        find_topNews.execute();

        return binding.getRoot();
    }

    MakeRequests.OnFindTopNewsListener onFindTopNewsListener = new MakeRequests.OnFindTopNewsListener() {
        @Override
        public void onFind(ArrayList<News> listNews) {
            fragmentProgressBar.dismiss();
            if (listNews != null && listNews.size() != 0) {
                adapter = new AdapterTopNews(getContext(), listNews);
                binding.viewPager.setAdapter(adapter);
                binding.viewPager.setPadding(65, 0, 65, 0);
                binding.viewPager.setVisibility(View.VISIBLE);
            }
            else {
                binding.layoutError.setVisibility(View.VISIBLE);
            }
        }
    };

}