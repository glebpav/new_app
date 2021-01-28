package com.example.news_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.adapter.FragmentViewHolder;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.hardware.usb.UsbRequest;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;

import java.util.List;
import java.util.Set;
import java.util.Timer;

public class News_activity extends AppCompatActivity{

    private final int NUM_PAGES = 4;
    MeowBottomNavigation meow;
    RelativeLayout layout;
    User user;
    ViewPager2 pager;
    ScreenSlidePageAdapter adapter;
    long timer_now, timer_last ;
    boolean was_released;


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
        was_released = false;

        meow.add(new MeowBottomNavigation.Model(SEARCH_ID, R.drawable.ic_baseline_find_replace_24));
        meow.add(new MeowBottomNavigation.Model(TRACKING_ID, R.drawable.ic_baseline_bookmarks_24));
        meow.add(new MeowBottomNavigation.Model(TOP_NEWS_ID, R.drawable.ic_baseline_whatshot_24));
        meow.add(new MeowBottomNavigation.Model(SETTING_ID, R.drawable.ic_baseline_person_24));

        Bundle arguments = getIntent().getExtras();
        user = new User();

        if (arguments != null) {
            user.id = arguments.getInt("id");
            user.name = arguments.getString("name");
            user.login = arguments.getString("login");
            user.history = arguments.getString("history");
            user.themes = arguments.getString("themes");
            user.password = arguments.getString("password");
            Log.d("asd", String.valueOf(user));
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
            Log.d("asd", "slide");
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            Log.d("asdf", String.valueOf(position));
            switch (position) {
                case 0:
                    return new Searching(pager, user);
                case 1:
                    return new Tracking(pager, user, meow);
                case 2:
                    return new Top_News();
                case 3:
                    return new Settings(user, pager, meow);
                default:
                    return new Searching(pager, user);
            }
        }

        @Override
        public int getItemCount() {
            return NUM_PAGES;
        }
    }

}