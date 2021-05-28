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

    private final ViewPager2 pager;
    private final MeowBottomNavigation meow;
    private FragmentTrackingBinding binding;

    private DialogFragmentAddTheme dialogFragmentAddTheme;
    private DialogFragmentProgressBar dialogFragmentProgressBar;

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

        mUser.clearThemes();
        mUser.fillListThemes();

        if (mUser.getListThemes().size() == 0) binding.layoutNoTrackingTheme.setVisibility(View.VISIBLE);
        else binding.recyclerView.setVisibility(View.VISIBLE);

        adapter = new AdapterTrackingThemes(Arrays.asList(mUser.getThemes().split(";")),
                onDeleteItemClickedListener, onThemeSelectedListener);
        binding.recyclerView.setAdapter(adapter);

        requests = new MakeRequests();

        return binding.getRoot();
    }

    View.OnClickListener btnAddThemeClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dialogFragmentAddTheme = new DialogFragmentAddTheme(btnApplyClickListener);
            dialogFragmentAddTheme.show(getFragmentManager(), "DialogFragmentAddTheme");
        }
    };



    DialogFragmentAddTheme.OnBtnApplyClickListener btnApplyClickListener = new DialogFragmentAddTheme.OnBtnApplyClickListener() {
        @Override
        public void onClick(String theme) {
            dialogFragmentProgressBar = new DialogFragmentProgressBar();
            dialogFragmentProgressBar.show(getFragmentManager(), "FragmentTrackingTheme");
            mUser.setThemes(mUser.getThemes() + ";" + theme);
            requests.new UpdateUserAsync(onUserChangedListener, mUser).execute();
        }
    };

    MakeRequests.OnUserChangedListener onUserChangedListener = new MakeRequests.OnUserChangedListener() {
        @Override
        public void onChanged(String serverResponse) {
            mUser.clearThemes();
            adapter.setThemesList(Arrays.asList(mUser.getThemes().split(";")));
            adapter.notifyDataSetChanged();
            dialogFragmentProgressBar.dismiss();

            mUser.fillListThemes();
            if (mUser.getListThemes().size() == 0){
                binding.recyclerView.setVisibility(View.INVISIBLE);
                binding.layoutNoTrackingTheme.setVisibility(View.VISIBLE);
            }
            else {
                binding.layoutNoTrackingTheme.setVisibility(View.INVISIBLE);
                binding.recyclerView.setVisibility(View.VISIBLE);
            }
            try {
                dialogFragmentAddTheme.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
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

    AdapterTrackingThemes.OnDeleteItemClickedListener onDeleteItemClickedListener = new AdapterTrackingThemes.OnDeleteItemClickedListener() {
        @Override
        public void onDeleteItem(String theme) {
            dialogFragmentProgressBar = new DialogFragmentProgressBar();
            dialogFragmentProgressBar.show(getFragmentManager(), "FragmentTrackingTheme");
            mUser.setThemes(mUser.getThemes().replace(theme, ""));
            mUser.clearThemes();
            requests.new UpdateUserAsync(onUserChangedListener, mUser).execute();
        }
    };
}