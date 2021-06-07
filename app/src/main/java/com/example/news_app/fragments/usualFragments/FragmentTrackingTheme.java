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
import com.example.news_app.fileManagers.JsonManager;
import com.example.news_app.fragments.dialogFragments.DialogFragmentAddTheme;
import com.example.news_app.fragments.dialogFragments.DialogFragmentProgressBar;
import com.example.news_app.models.SavedData;
import com.example.news_app.network.MakeRequests;
import com.example.news_app.databinding.FragmentTrackingBinding;
import com.example.news_app.models.User;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

import www.sanju.motiontoast.MotionToast;

// Todo : disable buttons in adapter Themes when internet is'n available

public class FragmentTrackingTheme extends Fragment {

    final static String TAG = "FRAGMENT_TRACKING_SPACE";

    private User mUser;
    private MakeRequests requests;
    private JsonManager jsonManager;
    private SavedData savedData;
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

        binding.btnAddTrackingTheme.setOnClickListener(btnAddThemeClicked);
        binding.progressSyncing.setVisibility(View.INVISIBLE);
        binding.layoutNoTrackingTheme.setVisibility(View.INVISIBLE);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        binding.recyclerView.setLayoutManager(llm);

        mUser.clearThemes();
        mUser.fillListThemes();

        adapter = new AdapterTrackingThemes(Arrays.asList(mUser.getThemes().split(";")),
                onDeleteItemClickedListener, onThemeSelectedListener);
        binding.recyclerView.setAdapter(adapter);

        requests = new MakeRequests();
        jsonManager = new JsonManager(getContext());

        return binding.getRoot();
    }

    void printThemes (ArrayList <String> list){
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

    View.OnClickListener btnAddThemeClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DialogFragmentAddTheme.OnBtnApplyClickListener btnApplyClickListener = new DialogFragmentAddTheme.OnBtnApplyClickListener() {
                @Override
                public void onClick(String theme) {
                    dialogFragmentProgressBar = new DialogFragmentProgressBar();
                    dialogFragmentProgressBar.show(getFragmentManager(), "FragmentTrackingTheme");
                    mUser.setThemes(mUser.getThemes() + ";" + theme);

                    Log.d(TAG, "onClick: " + new Gson().toJson(mUser));

                    requests.new UpdateUserAsync(onUserChangedListener, mUser).execute();
                }
            };

            dialogFragmentAddTheme = new DialogFragmentAddTheme(btnApplyClickListener);
            dialogFragmentAddTheme.show(getFragmentManager(), "DialogFragmentAddTheme");
        }
    };

    @Override
    public void onResume() {
        super.onResume();

        MakeRequests.OnLoadUserListener loadUserListener = new MakeRequests.OnLoadUserListener() {
            @Override
            public void onResults(User user) {
                if (user != null)
                    mUser = user;
                //mUser.clearThemes();
                //mUser.fillListHistory();
                mUser.fillListThemes();
                Log.d(TAG, "onResultsLoaded: " + mUser.getListThemes());

                SavedData loadedDataFromInter = new SavedData();
                loadedDataFromInter.prepareToSave(mUser, savedData.getListAllCurrency());

                printThemes(mUser.getListThemes());

                YoYo.with(Techniques.FadeOut).duration(500).repeat(1).playOn(binding.progressSyncing);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        binding.progressSyncing.setVisibility(View.INVISIBLE);
                    }
                }, 500);

                jsonManager.writeDataToJson(loadedDataFromInter);
            }
        };

        savedData = new SavedData();
        savedData = jsonManager.readUserFromJson();

        if (savedData != null && savedData.getListThemes() != null) {
            Log.d(TAG, "onResumeSaved: " + savedData.getListThemes());
            printThemes(savedData.getListThemes());
        }
        if (requests.isInternetAvailable(getContext())) {
            binding.btnAddTrackingTheme.setOnClickListener(btnAddThemeClicked);
            binding.progressSyncing.setVisibility(View.VISIBLE);
            YoYo.with(Techniques.BounceIn).duration(500).repeat(0).playOn(binding.progressSyncing);
            requests.new LoadUser(mUser.getLogin(), mUser.getPassword(), loadUserListener).execute();
        } else {
            MotionToast.Companion.createColorToast(getActivity(), "Нет интернет соединения", "попробуйте перезайти поже",
                    MotionToast.TOAST_ERROR,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(getContext(), R.font.helvetica_regular));
            binding.btnAddTrackingTheme.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MotionToast.Companion.createColorToast(getActivity(), "Нет интернет соединения", "попробуйте перезайти поже",
                            MotionToast.TOAST_ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(getContext(), R.font.helvetica_regular));
                }
            });
        }
    }

    private final MakeRequests.OnUserChangedListener onUserChangedListener = new MakeRequests.OnUserChangedListener() {
        @Override
        public void onChanged(String serverResponse) {
            mUser.clearThemes();
            mUser.fillListThemes();

            Log.d(TAG, "onChangedLoaded: " + mUser.getListThemes());

            savedData = jsonManager.readUserFromJson();
            savedData.setListThemes(mUser.getListThemes());
            jsonManager.writeDataToJson(savedData);

            Log.d(TAG, "onChangedSaved: " + savedData.getListThemes());

            printThemes(mUser.getListThemes());

            dialogFragmentProgressBar.dismiss();
            try {
                dialogFragmentAddTheme.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private final AdapterTrackingThemes.OnDeleteItemClickedListener onDeleteItemClickedListener = new AdapterTrackingThemes.OnDeleteItemClickedListener() {
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