package com.example.news_app.fragments.usualFragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.news_app.R;
import com.example.news_app.adapters.AdapterTrackingThemes;
import com.example.news_app.databases.DataBaseHelper;
import com.example.news_app.models.BookMark;
import com.example.news_app.fileManagers.JsonManager;
import com.example.news_app.fragments.dialogFragments.DialogFragmentAddTheme;
import com.example.news_app.fragments.dialogFragments.DialogFragmentProgressBar;
import com.example.news_app.network.MakeRequests;
import com.example.news_app.databinding.FragmentTrackingBinding;
import com.example.news_app.models.User;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

import www.sanju.motiontoast.MotionToast;

public class FragmentTrackingTheme extends Fragment {

    final static String TAG = "FRAGMENT_TRACKING_SPACE";

    private User mUser;
    private MakeRequests requests;
    private DataBaseHelper dbHelper;
    private JsonManager jsonManager;
    private AdapterTrackingThemes adapter;

    private ViewPager2 pager;
    private MeowBottomNavigation meow;
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

        binding.progressSyncing.setVisibility(View.INVISIBLE);
        binding.layoutNoTrackingTheme.setVisibility(View.INVISIBLE);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        binding.recyclerView.setLayoutManager(llm);

        mUser.clearThemes();
        mUser.fillListThemes();

        adapter = new AdapterTrackingThemes(Arrays.asList(mUser.getThemes().split(";")), getContext(),
                onDeleteItemClickedListener, onThemeSelectedListener);
        binding.recyclerView.setAdapter(adapter);

        requests = new MakeRequests();
        jsonManager = new JsonManager(getContext());

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        dbHelper = DataBaseHelper.getDataBaseHelperInstance(getContext());
        Log.d(TAG, "onResume: getBookMarks from db");
        dbHelper.new GetBookMarks(onGetBookMarksListener).execute();

        if (MakeRequests.isInternetAvailable(getContext())) {
            binding.progressSyncing.setVisibility(View.VISIBLE);
            YoYo.with(Techniques.BounceIn).duration(500).repeat(0).playOn(binding.progressSyncing);
            requests.new LoadUser(mUser.getLogin(), mUser.getPassword(), loadUserListener).execute();
        } else {
            String dateOfSaving = jsonManager.readSavedDate();
            if (dateOfSaving != null) {
                MotionToast.Companion.createColorToast(getActivity(), "Нет интернет соединения", "последнее сохранение \n" + dateOfSaving,
                        MotionToast.TOAST_ERROR,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(getContext(), R.font.helvetica_regular));
            } else {
                MotionToast.Companion.createColorToast(getActivity(), "Нет интернет соединения", "попробуйте перезайти поже",
                        MotionToast.TOAST_ERROR,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(getContext(), R.font.helvetica_regular));
            }
        }
    }

    void printThemes(ArrayList<String> list) {
        adapter.setThemesList(list);
        adapter.notifyDataSetChanged();

        if (list.size() == 0) {
            binding.recyclerView.setVisibility(View.INVISIBLE);
            binding.layoutNoTrackingTheme.setVisibility(View.VISIBLE);
        } else {
            binding.recyclerView.setVisibility(View.VISIBLE);
            binding.layoutNoTrackingTheme.setVisibility(View.INVISIBLE);
        }
    }

    void saveBookMarkToDb(ArrayList<String> listBookMarks) {
        new Thread(() -> {
            dbHelper.getAppDataBase().getBookmarkDao().deleteAll();
            dbHelper.getAppDataBase().getBookmarkDao().insertAll(BookMark
                    .getFromStringBookMarksList(listBookMarks)
                    .toArray(new BookMark[listBookMarks.size()]));
        }).start();
    }

    private final MakeRequests.OnLoadUserListener loadUserListener = user -> {
        if (user != null)
            mUser = user;
        //mUser.clearThemes();
        //mUser.fillListHistory();
        mUser.fillListThemes();
        Log.d(TAG, "onResultsLoaded: " + mUser.getListThemes());

        saveBookMarkToDb(mUser.getListThemes());

        printThemes(mUser.getListThemes());

        YoYo.with(Techniques.FadeOut).duration(500).repeat(1).playOn(binding.progressSyncing);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.progressSyncing.setVisibility(View.INVISIBLE);
            }
        }, 500);
    };

    private final AdapterTrackingThemes.OnThemeSelectedListener onThemeSelectedListener = theme -> {
        SharedPreferences pref = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor edt = pref.edit();
        edt.putString("SearchingTheme", theme);
        edt.apply();
        pager.setCurrentItem(0);
        meow.show(0, true);
    };

    private final MakeRequests.OnUserChangedListener onUserChangedListener = serverResponse -> {
        mUser.clearThemes();
        mUser.fillListThemes();
        Log.d(TAG, "onChangedLoaded: " + mUser.getListThemes());

        saveBookMarkToDb(mUser.getListThemes());
        printThemes(mUser.getListThemes());
        dialogFragmentProgressBar.dismiss();
        try {
            dialogFragmentAddTheme.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    };

    private final DialogFragmentAddTheme.OnBtnApplyClickListener btnApplyClickListener = theme -> {
        dialogFragmentProgressBar = new DialogFragmentProgressBar();
        dialogFragmentProgressBar.show(getFragmentManager(), "FragmentTrackingTheme");
        mUser.setThemes(mUser.getThemes() + ";" + theme);

        Log.d(TAG, "onClick: " + new Gson().toJson(mUser));

        requests.new UpdateUserAsync(onUserChangedListener, mUser).execute();
    };

    private final AdapterTrackingThemes.OnDeleteItemClickedListener onDeleteItemClickedListener = theme -> {
        dialogFragmentProgressBar = new DialogFragmentProgressBar();
        dialogFragmentProgressBar.show(getFragmentManager(), "FragmentTrackingTheme");
        mUser.setThemes(mUser.getThemes().replace(theme, ""));
        mUser.clearThemes();
        requests.new UpdateUserAsync(onUserChangedListener, mUser).execute();
    };

    private final DataBaseHelper.OnGetBookMarksListener onGetBookMarksListener = listBookMarks -> {
        if (listBookMarks == null) {
            Log.d(TAG, "onGetBookMarksListener : bookMarks is null");
        } else {
            Log.d(TAG, "onGetBookMarksListener: " + listBookMarks);
            printThemes(BookMark.getStringBookMarksList(listBookMarks));
            mUser.setListThemes(BookMark.getStringBookMarksList(listBookMarks));
        }
    };

    private final View.OnClickListener btnAddThemeClicked = v -> {
        if (MakeRequests.isInternetAvailable(getContext())) {
            dialogFragmentAddTheme = new DialogFragmentAddTheme(btnApplyClickListener);
            dialogFragmentAddTheme.show(getFragmentManager(), "DialogFragmentAddTheme");
        } else {
            MotionToast.Companion.createColorToast(getActivity(), "Нет интернет соединения", "попробуйте перезайти поже",
                    MotionToast.TOAST_ERROR,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(getContext(), R.font.helvetica_regular));
        }
    };
}