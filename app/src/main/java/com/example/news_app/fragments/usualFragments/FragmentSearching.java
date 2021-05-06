package com.example.news_app.fragments.usualFragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.news_app.adapters.AdapterViewNews;
import com.example.news_app.databinding.FragmentSearchingBinding;
import com.example.news_app.fragments.dialogFragments.DialogFragmentProgressBar;
import com.example.news_app.network.MakeRequests;
import com.example.news_app.models.News;
import com.example.news_app.models.User;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static com.example.news_app.databinding.FragmentSearchingBinding.*;


public class FragmentSearching extends Fragment {

    private User user;
    private MakeRequests requests;

    private AdapterViewNews adapter;
    private FragmentSearchingBinding binding;
    private DialogFragmentProgressBar fragmentProgressBar;

    public FragmentSearching(User user) {

        this.user = user;
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = inflate(inflater, container, false);

        ArrayList<News> newsList = new ArrayList<>();
        adapter = new AdapterViewNews(getContext(), newsList);

        binding.viewPager.setClipToPadding(false);
        binding.viewPager.setPadding(65,0,65,0);
        binding.viewPager.setAdapter(adapter);

        binding.btnFind.setOnClickListener(btnFindClicked);
        requests = new MakeRequests();

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
            findNews(theme);
        }
    };

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences pref = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
        String themeFromPage = pref.getString("SearchingTheme", "");

        if (themeFromPage.length() != 0){
            findNews(themeFromPage);
            binding.etTheme.setText(themeFromPage);
            SharedPreferences.Editor edt = pref.edit();
            edt.putString("SearchingTheme", "");
            edt.apply();
        }
    }

    void findNews(String theme){
        requests.new FindNews(theme, user, onFindNewsListener).execute();
        fragmentProgressBar = new DialogFragmentProgressBar();
        fragmentProgressBar.show(getFragmentManager(), "FragmentSearching");
        binding.imgHello.setVisibility(View.INVISIBLE);
        binding.viewPager.setVisibility(View.INVISIBLE);
        binding.errorLayout.setVisibility(View.INVISIBLE);
    }

    MakeRequests.OnFindNewsListener onFindNewsListener = new MakeRequests.OnFindNewsListener() {
        @Override
        public void onFoundNews(ArrayList<News> listNews) {
            Log.d("onFindNewsListener", "onFoundNews");
            fragmentProgressBar.dismiss();
            if (listNews != null) {
                adapter.setNewsArray(listNews);
                binding.viewPager.setAdapter(adapter);
                binding.viewPager.setVisibility(View.VISIBLE);
            }
            else {
                binding.errorLayout.setVisibility(View.VISIBLE);
            }
        }
    };

}