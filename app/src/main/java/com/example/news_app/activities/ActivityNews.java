package com.example.news_app.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import android.os.Build;
import android.os.Bundle;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.news_app.R;
import com.example.news_app.databinding.ActivityNewsBinding;
import com.example.news_app.fragments.usualFragments.FragmentSearching;
import com.example.news_app.fragments.usualFragments.FragmentSettings;
import com.example.news_app.fragments.usualFragments.FragmentTopNews;
import com.example.news_app.fragments.usualFragments.FragmentTrackingTheme;
import com.example.news_app.models.User;

public class ActivityNews extends AppCompatActivity{

    private final int NUM_PAGES = 4;
    User user;
    ScreenSlidePageAdapter adapter;
    ActivityNewsBinding binding;

    final int SETTING_ID = 3;
    final int TOP_NEWS_ID = 2;
    final int TRACKING_ID = 1;
    final int SEARCH_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        adapter = new ScreenSlidePageAdapter(this);
        binding.pager.setAdapter(adapter);
        binding.pager.setCurrentItem(0);
        binding.pager.setUserInputEnabled(false);
        binding.meow.show(SEARCH_ID,true);

        binding.meow.add(new MeowBottomNavigation.Model(SEARCH_ID, R.drawable.ic_baseline_find_replace_24));
        binding.meow.add(new MeowBottomNavigation.Model(TRACKING_ID, R.drawable.ic_baseline_bookmarks_24));
        binding.meow.add(new MeowBottomNavigation.Model(TOP_NEWS_ID, R.drawable.ic_baseline_whatshot_24));
        binding.meow.add(new MeowBottomNavigation.Model(SETTING_ID, R.drawable.ic_baseline_person_24));

        Bundle arguments = getIntent().getExtras();
        user = new User();

        if (arguments != null) {
            user.setId(arguments.getInt("id"));
            user.setName(arguments.getString("name"));
            user.setLogin(arguments.getString("login"));
            user.setHistory(arguments.getString("history"));
            user.setThemes(arguments.getString("themes"));
            user.setPassword(arguments.getString("password"));
            user.setSites(arguments.getString("sites"));
            user.setCurrency(arguments.getString("currency"));
        }

        binding.meow.setOnClickMenuListener(item -> {
            switch (item.getId()) {
                case (SEARCH_ID): {
                    binding.pager.setCurrentItem(SEARCH_ID);
                    break;
                }
                case (TRACKING_ID): {
                    binding.pager.setCurrentItem(TRACKING_ID);
                    break;
                }
                case (TOP_NEWS_ID): {
                    binding.pager.setCurrentItem(TOP_NEWS_ID);
                    break;
                }
                case (SETTING_ID): {
                    binding.pager.setCurrentItem(SETTING_ID);
                    break;
                }
                default:
                    binding.pager.setCurrentItem(SEARCH_ID);
            }
        });

        binding.meow.setOnShowListener(item -> {
            // your codes
        });

        binding.meow.setOnReselectListener(item -> {
            // your codes
        });
    }


    class ScreenSlidePageAdapter extends FragmentStateAdapter {

        public ScreenSlidePageAdapter(FragmentActivity fa) {
            super(fa);
        }



        @RequiresApi(api = Build.VERSION_CODES.N)
        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new FragmentSearching(user);
                case 1:
                    return new FragmentTrackingTheme(binding.pager, user, binding.meow);
                case 2:
                    return new FragmentTopNews();
                case 3:
                    return new FragmentSettings(user, binding.pager, binding.meow);
                default:
                    return new FragmentSearching(user);
            }
        }
        @Override
        public int getItemCount() {
            return NUM_PAGES;
        }
    }
}