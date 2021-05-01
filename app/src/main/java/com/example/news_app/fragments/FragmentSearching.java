package com.example.news_app.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.news_app.adapters.AdapterViewNews;
import com.example.news_app.databinding.FragmentSearchingBinding;
import com.example.news_app.network.MakeRequests;
import com.example.news_app.models.News;
import com.example.news_app.models.User;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static com.example.news_app.databinding.FragmentSearchingBinding.*;


public class FragmentSearching extends Fragment {

    public ViewPager2 pager;

    private User user;
    private FragmentSearching fragmentSearch;
    private MakeRequests requests;
    private ArrayList<News> newsList;

    public AdapterViewNews getAdapter() {
        return adapter;
    }
    public User getUser() {
        return user;
    }
    public FragmentSearchingBinding getBinding () {
        return binding;
    }

    public void setUser(User user) {
        this.user = user;
    }

    private AdapterViewNews adapter;
    private FragmentSearchingBinding binding;

    public FragmentSearching(ViewPager2 pager, User user) {
        this.pager = pager;
        this.user = user;
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = inflate(inflater, container, false);

        newsList = new ArrayList<>();
        adapter = new AdapterViewNews(getContext(), newsList);

        binding.viewPager.setClipToPadding(false);
        binding.viewPager.setPadding(65,0,65,0);
        binding.viewPager.setAdapter(adapter);

        binding.btnFind.setOnClickListener(btnFindClicked);
        requests = new MakeRequests("https://analisinf.pythonanywhere.com/");
        fragmentSearch = this;

        return binding.getRoot();
    }

    View.OnClickListener btnFindClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String theme = binding.etTheme.getText().toString();
            if (theme.length() == 0) {
                Toast.makeText(getActivity(), "Введите тему", Toast.LENGTH_SHORT).show();
                return;
            }
            MakeRequests.FindNews findNews = requests.new FindNews(fragmentSearch, theme);
            findNews.execute();
        }
    };

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences pref = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
        String themeFromPage = pref.getString("SearchingTheme", "");

        if (themeFromPage.length() != 0){
            MakeRequests.FindNews find_news = requests.new FindNews(fragmentSearch, themeFromPage);
            find_news.execute();

            binding.etTheme.setText(themeFromPage);
            SharedPreferences.Editor edt = pref.edit();
            edt.putString("SearchingTheme", "");
            edt.apply();
        }
    }
}