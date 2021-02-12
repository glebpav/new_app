package com.example.news_app;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.databinding.adapters.ViewBindingAdapter;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.util.ArrayList;

import soup.neumorphism.NeumorphFloatingActionButton;


public class Searching extends Fragment {

    EditText et_theme;
    NeumorphFloatingActionButton btn_find;
    ViewPager view_pager;
    ImageView image_view;
    ProgressBar progressBar;
    RelativeLayout layout_error;
    ViewPager2 pager;

    User user;
    Searching fragment_search;
    MakeRequests requests;
    ArrayList<News> news_list;
    View_news_adapter adapter;

    Searching(ViewPager2 pager, User user) {
        this.pager = pager;
        this.user = user;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_searching, container, false);

        et_theme = view.findViewById(R.id.et_theme);
        btn_find = view.findViewById(R.id.btn_find);
        view_pager = view.findViewById(R.id.view_pager);
        progressBar = view.findViewById(R.id.progress_circular);
        image_view = view.findViewById(R.id.img_hello);
        layout_error = view.findViewById(R.id.error_layout);

        news_list = new ArrayList<>();
        adapter = new View_news_adapter(getContext(), news_list);

        view_pager.setClipToPadding(false);
        view_pager.setPadding(65,0,65,0);
        view_pager.setAdapter(adapter);

        btn_find.setOnClickListener(btn_find_clicked);
        requests = new MakeRequests("https://analisinf.pythonanywhere.com/");
        fragment_search = this;

        return view;
    }

    View.OnClickListener btn_find_clicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String theme = et_theme.getText().toString();
            if (theme.length() == 0) {
                Toast.makeText(getActivity(), "Введите тему", Toast.LENGTH_SHORT).show();
                return;
            }
            MakeRequests.Find_news find_news = requests.new Find_news(fragment_search, theme);
            find_news.execute();
        }
    };

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences pref = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
        String theme_from_page = pref.getString("SearchingTheme", "");

        if (theme_from_page.length() != 0){
            MakeRequests.Find_news find_news = requests.new Find_news(fragment_search, theme_from_page);
            find_news.execute();

            et_theme.setText(theme_from_page);
            SharedPreferences.Editor edt = pref.edit();
            edt.putString("SearchingTheme", "");
            edt.commit();
        }
    }
}