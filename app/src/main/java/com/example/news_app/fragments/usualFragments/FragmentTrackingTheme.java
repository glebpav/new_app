package com.example.news_app.fragments.usualFragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.news_app.adapters.AdapterTrackingThemes;
import com.example.news_app.fragments.dialogFragments.DialogFragmentAddTheme;
import com.example.news_app.fragments.dialogFragments.DialogFragmentProgressBar;
import com.example.news_app.network.MakeRequests;
import com.example.news_app.databinding.FragmentTrackingBinding;
import com.example.news_app.models.User;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class FragmentTrackingTheme extends Fragment {

    private User mUser;
    private MakeRequests requests;
    private AdapterTrackingThemes adapter;

    private ViewPager2 pager;
    private MeowBottomNavigation meow;
    private FragmentTrackingBinding binding;

    DialogFragmentAddTheme dialogFragmentAddTheme;
    DialogFragmentProgressBar dialogFragmentProgressBar;

    public FragmentTrackingTheme(ViewPager2 pager, User user, MeowBottomNavigation meow) {
        this.mUser = user;
        this.pager = pager;
        this.meow = meow;
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTrackingBinding.inflate(inflater, container, false);
        binding.btnAddTrackingTheme.setOnClickListener(btnAddThemeClicked);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        binding.recyclerView.setLayoutManager(llm);

        clearThemes();
        adapter = new AdapterTrackingThemes(Arrays.asList(mUser.getThemes().split(";")),
                onDeleteItemClickdeListener, onThemeSelectedListener);
        binding.recyclerView.setAdapter(adapter);

        requests = new MakeRequests("https://analisinf.pythonanywhere.com/");

        return binding.getRoot();
    }

    View.OnClickListener btnAddThemeClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dialogFragmentAddTheme = new DialogFragmentAddTheme(btnApplyClickListener);
            dialogFragmentAddTheme.show(getFragmentManager(), "DialogFragmentAddTheme");
        }
    };

    public void clearThemes() {
        if (mUser.getThemes().isEmpty()) return;
        mUser.setThemes(mUser.getThemes().replace(";;", ";"));
        if (mUser.getThemes().charAt(0) == ';') {
            mUser.setThemes(mUser.getThemes().substring(1));
        }
    }

    DialogFragmentAddTheme.OnBtnApplyClickListener btnApplyClickListener = new DialogFragmentAddTheme.OnBtnApplyClickListener() {
        @Override
        public void onClick(String theme) {
            dialogFragmentProgressBar = new DialogFragmentProgressBar();
            dialogFragmentProgressBar.show(getFragmentManager(), "FragmentTrackingTheme");
            requests.new AddTrackingTheme(trackingThemeChenchedListener, mUser, theme).execute();
        }
    };

    MakeRequests.OnAddTrackingThemeListener trackingThemeChenchedListener = new MakeRequests.OnAddTrackingThemeListener() {
        @Override
        public void onClick(User user) {
            if (user != null) {
                mUser = user;
                adapter.setThemes_list(Arrays.asList(mUser.getThemes().split(";")));
                adapter.notifyDataSetChanged();
            }
            dialogFragmentProgressBar.dismiss();
            dialogFragmentAddTheme.dismiss();
        }
    };

    AdapterTrackingThemes.OnThemeSelectedListener onThemeSelectedListener = new AdapterTrackingThemes.
            OnThemeSelectedListener() {
        @Override
        public void onThemeSelected(String theme) {
            SharedPreferences pref = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
            SharedPreferences.Editor edt = pref.edit();
            edt.putString("SearchingTheme", theme);
            edt.apply();
            pager.setCurrentItem(0);
            meow.show(0, true);
        }
    };

    AdapterTrackingThemes.OnDeleteItemClickdeListener onDeleteItemClickdeListener = new AdapterTrackingThemes.OnDeleteItemClickdeListener() {
        @Override
        public void onDeleteItem(String theme) {
            dialogFragmentProgressBar = new DialogFragmentProgressBar();
            dialogFragmentProgressBar.show(getFragmentManager(), "FragmentTrackingTheme");
            requests.new DeleteTheme(theme, mUser, trackingThemeChenchedListener).execute();
        }
    };


}