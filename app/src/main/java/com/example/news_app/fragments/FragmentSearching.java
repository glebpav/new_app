package com.example.news_app.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.news_app.adapters.AdapterViewNews;
import com.example.news_app.databinding.FragmentSearchingBinding;
import com.example.news_app.network.MakeRequests;
import com.example.news_app.R;
import com.example.news_app.models.News;
import com.example.news_app.models.User;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import soup.neumorphism.NeumorphFloatingActionButton;

import static com.example.news_app.databinding.FragmentSearchingBinding.*;


public class FragmentSearching extends Fragment {

    public ViewPager2 pager;

    public User user;
    public FragmentSearching fragment_search;
    public MakeRequests requests;
    public ArrayList<News> news_list;
    public AdapterViewNews adapter;
    public FragmentSearchingBinding binding;

    public FragmentSearching(ViewPager2 pager, User user) {
        this.pager = pager;
        this.user = user;
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = inflate(inflater, container, false);

        news_list = new ArrayList<>();
        adapter = new AdapterViewNews(getContext(), news_list);

        binding.viewPager.setClipToPadding(false);
        binding.viewPager.setPadding(65,0,65,0);
        binding.viewPager.setAdapter(adapter);

        binding.btnFind.setOnClickListener(btn_find_clicked);
        requests = new MakeRequests("https://analisinf.pythonanywhere.com/");
        fragment_search = this;

        return binding.getRoot();
    }

    View.OnClickListener btn_find_clicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String theme = binding.etTheme.getText().toString();
            if (theme.length() == 0) {
                Toast.makeText(getActivity(), "Введите тему", Toast.LENGTH_SHORT).show();
                return;
            }
            MakeRequests.FindNews find_news = requests.new FindNews(fragment_search, theme);
            find_news.execute();
        }
    };

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences pref = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
        String theme_from_page = pref.getString("SearchingTheme", "");

        if (theme_from_page.length() != 0){
            MakeRequests.FindNews find_news = requests.new FindNews(fragment_search, theme_from_page);
            find_news.execute();

            binding.etTheme.setText(theme_from_page);
            SharedPreferences.Editor edt = pref.edit();
            edt.putString("SearchingTheme", "");
            edt.apply();
        }
    }
}