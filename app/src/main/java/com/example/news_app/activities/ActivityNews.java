package com.example.news_app.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.news_app.R;
import com.example.news_app.fragments.usualFragments.FragmentSearching;
import com.example.news_app.fragments.usualFragments.FragmentSettings;
import com.example.news_app.fragments.usualFragments.FragmentTopNews;
import com.example.news_app.fragments.usualFragments.FragmentTrackingTheme;
import com.example.news_app.models.User;

public class ActivityNews extends AppCompatActivity{

    private final int NUM_PAGES = 4;
    MeowBottomNavigation meow;
    RelativeLayout layout;
    User user;
    ViewPager2 pager;
    ScreenSlidePageAdapter adapter;

    final int SETTING_ID = 3;
    final int TOP_NEWS_ID = 2;
    final int TRACKING_ID = 1;
    final int SEARCH_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_activity);


        meow = findViewById(R.id.meow);
        pager = findViewById(R.id.pager);
        layout = findViewById(R.id.NA);
        adapter = new ScreenSlidePageAdapter(this);

        pager.setAdapter(adapter);
        pager.setCurrentItem(0);
        pager.setUserInputEnabled(false);
        meow.show(SEARCH_ID,true);

        meow.add(new MeowBottomNavigation.Model(SEARCH_ID, R.drawable.ic_baseline_find_replace_24));
        meow.add(new MeowBottomNavigation.Model(TRACKING_ID, R.drawable.ic_baseline_bookmarks_24));
        meow.add(new MeowBottomNavigation.Model(TOP_NEWS_ID, R.drawable.ic_baseline_whatshot_24));
        meow.add(new MeowBottomNavigation.Model(SETTING_ID, R.drawable.ic_baseline_person_24));

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

        meow.setOnClickMenuListener(new MeowBottomNavigation.ClickListener() {
            @Override
            public void onClickItem(MeowBottomNavigation.Model item) {
                switch (item.getId()) {
                    case (SEARCH_ID): {
                        pager.setCurrentItem(SEARCH_ID);
                        break;
                    }
                    case (TRACKING_ID): {
                        pager.setCurrentItem(TRACKING_ID);
                        break;
                    }
                    case (TOP_NEWS_ID): {
                        pager.setCurrentItem(TOP_NEWS_ID);
                        break;
                    }
                    case (SETTING_ID): {
                        pager.setCurrentItem(SETTING_ID);
                        break;
                    }
                    default:
                        pager.setCurrentItem(SEARCH_ID);
                }
            }
        });

        meow.setOnShowListener(new MeowBottomNavigation.ShowListener() {
            @Override
            public void onShowItem(MeowBottomNavigation.Model item) {
                // your codes
            }
        });

        meow.setOnReselectListener(new MeowBottomNavigation.ReselectListener() {
            @Override
            public void onReselectItem(MeowBottomNavigation.Model item) {
                // your codes
            }
        });

    }


    class ScreenSlidePageAdapter extends FragmentStateAdapter {

        public ScreenSlidePageAdapter(FragmentActivity fa) {
            super(fa);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new FragmentSearching(user);
                case 1:
                    return new FragmentTrackingTheme(pager, user, meow);
                case 2:
                    return new FragmentTopNews();
                case 3:
                    return new FragmentSettings(user, pager, meow);
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