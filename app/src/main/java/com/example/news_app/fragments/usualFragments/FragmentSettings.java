package com.example.news_app.fragments.usualFragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.news_app.activities.ActivityMain;
import com.example.news_app.R;
import com.example.news_app.adapters.AdapterCurrencyTile;
import com.example.news_app.adapters.AdapterSettingTiles;
import com.example.news_app.databinding.FragmentSettingsBinding;
import com.example.news_app.enums.SettingsPoints;
import com.example.news_app.fileManagers.JsonManager;
import com.example.news_app.fragments.dialogFragments.DialogFragmentChangeName;
import com.example.news_app.fragments.dialogFragments.DialogFragmentHistory;
import com.example.news_app.fragments.dialogFragments.DialogFragmentProgressBar;
import com.example.news_app.fragments.dialogFragments.DialogFragmentSelectCurrency;
import com.example.news_app.fragments.dialogFragments.DialogFragmentSources;
import com.example.news_app.fragments.dialogFragments.DialogFragmentSureToLogOut;
import com.example.news_app.models.CentBankCurrency;
import com.example.news_app.models.SavedData;
import com.example.news_app.models.Weather;
import com.example.news_app.network.MakeRequests;
import com.example.news_app.models.User;
import com.example.news_app.network.ParseCourse;
import com.example.news_app.network.ParseWeather;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import www.sanju.motiontoast.MotionToast;


public class FragmentSettings extends Fragment {

    private static final String TAG = "FRAGMENT_SETTINGS_SPACE";
    private ArrayList<CentBankCurrency> mListCurrency;

    private User mUser;
    private SavedData savedData;
    private MakeRequests requests;
    private JsonManager jsonManager;
    private FragmentSettingsBinding binding;
    private AdapterSettingTiles adapterSettingTiles;
    private AdapterCurrencyTile adapterCurrencyTile;

    private final ViewPager2 pager;
    private final MeowBottomNavigation meow;
    private DialogFragmentHistory fragmentHistory;
    private DialogFragmentSources fragmentSources;
    private DialogFragmentProgressBar fragmentProgress;
    private DialogFragmentChangeName fragmentChangeName;
    private DialogFragmentSureToLogOut fragmentSureToLogOut;
    private DialogFragmentSelectCurrency fragmentSelectCurrency;

    public FragmentSettings(User user, ViewPager2 pager, MeowBottomNavigation meow) {
        this.mUser = user;
        this.meow = meow;
        this.pager = pager;
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);

        requests = new MakeRequests();
        jsonManager = new JsonManager(getContext());
        fragmentProgress = new DialogFragmentProgressBar();

        adapterSettingTiles = new AdapterSettingTiles(getContext(), onClickedSettingsItemListener);
        adapterCurrencyTile = new AdapterCurrencyTile(null);

        binding.progressSyncing.setVisibility(View.INVISIBLE);
        binding.recyclerViewSettings.setAdapter(adapterSettingTiles);
        binding.recyclerViewCurrency.setAdapter(adapterCurrencyTile);
        binding.recyclerViewSettings.setLayoutManager(new GridLayoutManager(getContext(), 2));
        binding.recyclerViewCurrency.setLayoutManager(new GridLayoutManager(getContext(), 2));

        return binding.getRoot();
    }

    private boolean updateSavedData() {

        SavedData newData = new SavedData(mUser);
        newData.setListTopNews(savedData.getListTopNews());
        newData.setListAllCurrency(savedData.getListAllCurrency());

        jsonManager.writeDataToJson(newData);

        return false;
    }

    void logOut() {
        DialogFragmentSureToLogOut.OnLogOutBtnClickedListener onLogOutBtnClickedListener = new DialogFragmentSureToLogOut.OnLogOutBtnClickedListener() {
            @Override
            public void onClicked() {

                // deleting all information about user from json
                jsonManager.writeDataToJson(new SavedData());

                // deleting all information about user from sharedPreferences
                SharedPreferences pref = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
                SharedPreferences.Editor edt = pref.edit();
                edt.putString("login", "");
                edt.putString("password", "");
                edt.apply();

                fragmentSureToLogOut.dismiss();
                Intent intent = new Intent(getActivity(), ActivityMain.class);
                startActivity(intent);
            }
        };

        fragmentSureToLogOut = new DialogFragmentSureToLogOut();
        fragmentSureToLogOut.setLogOutBtnClickedListener(onLogOutBtnClickedListener);
        fragmentSureToLogOut.show(getActivity().getFragmentManager(), "FragmentSetting");
    }

    void changeName() {

        DialogFragmentChangeName.OnBtnChangedClickedListener btnChangedClickedListener = new
                DialogFragmentChangeName.OnBtnChangedClickedListener() {
                    @Override
                    public void onClicked(String name) {
                        Log.d("TAG", "OnBtnChangedClickedListener - onClicked");
                        mUser.setName(name);
                        fragmentProgress.show(getFragmentManager(), "OnBtnChangedClickedListener");
                        requests.new UpdateUserAsync(onUserChangedListener, mUser).execute();
                    }

                    final MakeRequests.OnUserChangedListener onUserChangedListener = new MakeRequests.OnUserChangedListener() {
                        @Override
                        public void onChanged(String serverResponse) {
                            Log.d("TAG", "onUserChangedListener - onChanged");
                            binding.collapsingToolbar.setTitle(mUser.getName());
                            fragmentProgress.dismiss();
                            fragmentChangeName.dismiss();

                            updateSavedData();
                        }
                    };
                };

        fragmentChangeName = new DialogFragmentChangeName(btnChangedClickedListener, mUser.getName());
        fragmentChangeName.show(getFragmentManager(), "FragmentSettings");
    }

    void showHistory() {

        DialogFragmentHistory.OnAdapterTileHistoryClickedListener adapterTileHistoryClickedListener = new
                DialogFragmentHistory.OnAdapterTileHistoryClickedListener() {
                    @Override
                    public void onClicked(int position) {
                        SharedPreferences pref = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
                        SharedPreferences.Editor edt = pref.edit();
                        edt.putString("SearchingTheme", mUser.getHistory().split(";")
                                [(mUser.getHistory().split(";")).length - position - 1]);
                        edt.apply();

                        fragmentHistory.dismiss();
                        pager.setCurrentItem(0);
                        meow.show(0, true);
                    }
                };

        List<String> list = Arrays.asList(mUser.getHistory().split(";"));
        Collections.reverse(list);
        fragmentHistory = new DialogFragmentHistory(list, adapterTileHistoryClickedListener);
        fragmentHistory.show(getFragmentManager(), "FragmentDialog");
    }

    void changeSources() {

        DialogFragmentSources.OnSourcesChangedListener sourcesChangedListener = new
                DialogFragmentSources.OnSourcesChangedListener() {
                    @Override
                    public void onChanged(User user) {
                        mUser.setSites(user.getSites());
                        fragmentProgress = new DialogFragmentProgressBar();
                        assert getFragmentManager() != null;
                        fragmentProgress.show(getFragmentManager(), "OnSourcesChangedListener");
                        requests.new UpdateUserAsync(onUserChangedListener, mUser).execute();
                    }

                    final MakeRequests.OnUserChangedListener onUserChangedListener = new MakeRequests.OnUserChangedListener() {
                        @Override
                        public void onChanged(String serverResponse) {
                            Log.d("TAG", "onUserChangedListener - onChanged");
                            fragmentProgress.dismiss();
                            Toast.makeText(getContext(), getResources().getString(R.string.successful_changed), Toast.LENGTH_SHORT).show();

                            updateSavedData();
                        }
                    };

                };

        mUser.setSites(mUser.getSites().replace("null", "").replace(";;", ";"));
        fragmentSources = new DialogFragmentSources();
        fragmentSources.setUser(mUser);
        fragmentSources.setSourcesChangedListener(sourcesChangedListener);
        fragmentSources.show(getActivity().getFragmentManager(), "FragmentSettings");
    }

    void changeCurrency() {

        DialogFragmentSelectCurrency.OnCurrencySelectedListener currencySelectedListener = new
                DialogFragmentSelectCurrency.OnCurrencySelectedListener() {
                    @Override
                    public void onSelected(CentBankCurrency currency) {
                        Log.d(TAG, "onSelected: " + currency.getName() + " (isHidden - " + currency.isHidden() + ")");

                        if (!currency.isHidden()) {
                            mUser.getListCurrency().remove(currency.getCharCode());
                            mListCurrency.get(mListCurrency.indexOf(currency)).setHidden(true);
                        } else {
                            mUser.getListCurrency().add(currency.getCharCode());
                            mListCurrency.get(mListCurrency.indexOf(currency)).setHidden(false);
                        }

                        for (String str : mUser.getListCurrency())
                            Log.d(TAG, "onSelected: " + str);
                        mUser.reformCurrencyString();
                        Log.d(TAG, "onSelected: " + mListCurrency.get(mListCurrency.indexOf(currency)).getName() + " (isHidden - " + mListCurrency.get(mListCurrency.indexOf(currency)).isHidden() + ")");
                        Log.d(TAG, "onSelected: " + mUser.getCurrency());

                        fragmentProgress = new DialogFragmentProgressBar();
                        fragmentProgress.show(getFragmentManager(), "OnSourcesChangedListener");
                        requests.new UpdateUserAsync(onUserChangedListener, mUser).execute();
                    }

                    final MakeRequests.OnUserChangedListener onUserChangedListener = new MakeRequests.OnUserChangedListener() {
                        @Override
                        public void onChanged(String serverResponse) {
                            Log.d(TAG, "onUserChangedListener - onChanged");
                            ArrayList<CentBankCurrency> outputListCurrency = new ArrayList<>();

                            for (int i = 0; i < mListCurrency.size(); i++) {
                                if (!mListCurrency.get(i).isHidden()) {
                                    outputListCurrency.add(mListCurrency.get(i));
                                    System.out.println(mListCurrency.get(i).getCharCode());
                                }
                            }

                            fragmentSelectCurrency.setListCurrency(mListCurrency);
                            adapterCurrencyTile.setListCurrency(outputListCurrency);
                            adapterCurrencyTile.notifyDataSetChanged();
                            fragmentProgress.dismiss();
                            Toast.makeText(getContext(), getResources().getString(R.string.successful_changed), Toast.LENGTH_SHORT).show();

                            updateSavedData();
                        }
                    };
                };

        Log.d(TAG, "changeCurrency: " + mListCurrency.size());
        fragmentSelectCurrency = new DialogFragmentSelectCurrency(mListCurrency, currencySelectedListener);
        fragmentSelectCurrency.show(getFragmentManager(), "FragmentSettings");
    }

    @Override
    public void onResume() {
        super.onResume();

        ParseWeather.OnFindWeatherListener onFindWeatherListener = weather -> {
            if (weather != null){
                binding.tvTemp.setText(weather.getTemperature());
                binding.tvWeatherDesc.setText(weather.getWeatherDesc());
            }
        };

        final ParseCourse.OnParseCourseListener parseCourseListener = listCurrency -> {
            mUser.fillListCurrency();
            mListCurrency = listCurrency;

            SavedData loadedDataFromInter = new SavedData();
            loadedDataFromInter.prepareToSave(mUser, listCurrency);

            ArrayList<CentBankCurrency> outputListCurrency = new ArrayList<>();
            for (int i = 0; i < listCurrency.size(); i++) {
                for (int j = 0; j < mUser.getListCurrency().size(); j++) {
                    if (listCurrency.get(i).getCharCode().equals(mUser.getListCurrency().get(j))) {
                        outputListCurrency.add(listCurrency.get(i));
                        listCurrency.get(i).setHidden(false);
                        break;
                    }
                }
            }

            if (!loadedDataFromInter.equals(savedData)) {
                Log.d(TAG, "onFound: not equals");
                if (savedData.getListTopNews() != null)
                    loadedDataFromInter.setListTopNews(savedData.getListTopNews());
                jsonManager.writeDataToJson(loadedDataFromInter);

            } else Log.d(TAG, "onFound: equals");

            adapterCurrencyTile.setListCurrency(outputListCurrency);
            adapterCurrencyTile.notifyDataSetChanged();

            YoYo.with(Techniques.FadeOut).duration(500).repeat(1).playOn(binding.progressSyncing);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    binding.progressSyncing.setVisibility(View.INVISIBLE);
                }
            }, 500);
        };

        MakeRequests.OnLoadUserListener loadUserListener = user -> {

            if (user == null) {
                savedData = jsonManager.readUserFromJson();
                user = savedData.getUser();
            }
            mUser = user;
            mUser.clearThemes();
            mUser.fillListHistory();
            mUser.fillListThemes();
            new ParseCourse(parseCourseListener).execute();

            binding.collapsingToolbar.setTitle(user.getName());
            binding.tvCountThemes.setText(String.valueOf(mUser.getListHistory().size()));
            binding.tvCountTracking.setText(String.valueOf(mUser.getListThemes().size()));

            /*
            fragmentProgress.dismiss();
            binding.nestedScrollView.setVisibility(View.VISIBLE);
            binding.appbar.setVisibility(View.VISIBLE);
*/
        };

        if (requests.isInternetAvailable(getContext())) {
            binding.progressSyncing.setVisibility(View.VISIBLE);
            YoYo.with(Techniques.BounceIn).duration(500).repeat(0).playOn(binding.progressSyncing);
            binding.recyclerViewSettings.setClickable(true);
        } else {
            MotionToast.Companion.createColorToast(getActivity(), "Нет интернет соединения", "попробуйте перезайти поже",
                    MotionToast.TOAST_ERROR,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(getContext(), R.font.helvetica_regular));
            binding.recyclerViewSettings.setClickable(false);
        }

        savedData = new SavedData();
        savedData = jsonManager.readUserFromJson();

        if (savedData != null && savedData.getListAllCurrency() != null) {
            ArrayList<CentBankCurrency> outputListCurrency = new ArrayList<>();
            for (int i = 0; i < savedData.getListAllCurrency().size(); i++) {
                for (int j = 0; j < savedData.getListSelectedCurrency().size(); j++) {
                    if (savedData.getListAllCurrency().get(i).getCharCode().equals(savedData
                            .getListSelectedCurrency().get(j))) {
                        outputListCurrency.add(savedData.getListAllCurrency().get(i));
                        //savedData.getListAllCurrency().get(i).setHidden(false);
                        break;
                    }
                }
            }

            adapterCurrencyTile.setListCurrency(outputListCurrency);
            adapterCurrencyTile.notifyDataSetChanged();

            binding.collapsingToolbar.setTitle(savedData.getName());
            binding.tvCountThemes.setText(String.valueOf(savedData.getListHistory().size()));
            binding.tvCountTracking.setText(String.valueOf(savedData.getListThemes().size()));

            //fragmentProgress.dismiss();
            binding.nestedScrollView.setVisibility(View.VISIBLE);
            binding.appbar.setVisibility(View.VISIBLE);
        }

        if (requests.isInternetAvailable(getContext())) {
            new ParseWeather(getContext(), onFindWeatherListener).execute();
            requests.new LoadUser(mUser.getLogin(), mUser.getPassword(), loadUserListener).execute();
        }
    }

    AdapterSettingTiles.OnClickedSettingsItemListener onClickedSettingsItemListener = new
            AdapterSettingTiles.OnClickedSettingsItemListener() {
                @Override
                public void onClicked(SettingsPoints point) {
                    switch (point) {
                        case LOG_OUT:
                            logOut();
                            break;
                        case CHANGE_NAME:
                            changeName();
                            break;
                        case SHOW_HISTORY:
                            showHistory();
                            break;
                        case CHANGE_SOURCES:
                            changeSources();
                            break;
                        case CHANGE_CURRENCY:
                            changeCurrency();
                            break;
                    }
                }
            };

}