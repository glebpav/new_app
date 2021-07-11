package com.example.news_app.fragments.usualFragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
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
import com.example.news_app.databases.DataBaseHelper;
import com.example.news_app.databinding.FragmentSettingsBinding;
import com.example.news_app.fileManagers.JsonManager;
import com.example.news_app.fragments.dialogFragments.DialogFragmentChangeName;
import com.example.news_app.fragments.dialogFragments.DialogFragmentChangeToneFormat;
import com.example.news_app.fragments.dialogFragments.DialogFragmentHistory;
import com.example.news_app.fragments.dialogFragments.DialogFragmentProgressBar;
import com.example.news_app.fragments.dialogFragments.DialogFragmentSelectCurrency;
import com.example.news_app.fragments.dialogFragments.DialogFragmentSources;
import com.example.news_app.fragments.dialogFragments.DialogFragmentSureToLogOut;
import com.example.news_app.models.BookMark;
import com.example.news_app.models.CentBankCurrency;
import com.example.news_app.models.History;
import com.example.news_app.models.SavedData;
import com.example.news_app.models.Weather;
import com.example.news_app.network.MakeRequests;
import com.example.news_app.models.User;
import com.example.news_app.network.ParseCourse;
import com.example.news_app.network.ParseWeather;
import com.example.news_app.utils.SharedPreferencesHelper;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import www.sanju.motiontoast.MotionToast;


public class FragmentSettings extends Fragment {

    private static final String TAG = "FRAGMENT_SETTINGS_SPACE";

    private String selectedSources;
    private ArrayList<CentBankCurrency> mListCurrency;
    private ArrayList<String> mListHistory;

    private User mUser;
    private Weather mWeather;
    private MakeRequests requests;
    private DataBaseHelper dbHelper;
    private FragmentSettingsBinding binding;
    private AdapterSettingTiles adapterSettingTiles;
    private AdapterCurrencyTile adapterCurrencyTile;

    private ViewPager2 pager;
    private MeowBottomNavigation meow;
    private DialogFragmentHistory fragmentHistory;
    private DialogFragmentSources fragmentSources;
    private DialogFragmentProgressBar fragmentProgress;
    private DialogFragmentChangeName fragmentChangeName;
    private DialogFragmentSureToLogOut fragmentSureToLogOut;
    private DialogFragmentSelectCurrency fragmentSelectCurrency;
    private DialogFragmentChangeToneFormat fragmentChangeToneFormat;

    public FragmentSettings(User user, ViewPager2 pager, MeowBottomNavigation meow) {
        this.mUser = user;
        this.meow = meow;
        this.pager = pager;
    }

    public FragmentSettings() {
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);

        mUser = new User();
        mWeather = new Weather();
        requests = new MakeRequests();
        fragmentProgress = new DialogFragmentProgressBar();
        dbHelper = DataBaseHelper.recreateDataBaseHelperInstance(getContext());

        adapterSettingTiles = new AdapterSettingTiles(getContext(), onClickedSettingsItemListener);
        adapterCurrencyTile = new AdapterCurrencyTile(null);

        binding.progressSyncing.setVisibility(View.INVISIBLE);
        binding.recyclerViewSettings.setAdapter(adapterSettingTiles);
        binding.recyclerViewCurrency.setAdapter(adapterCurrencyTile);
        binding.recyclerViewSettings.setLayoutManager(new GridLayoutManager(getContext(), 2));
        binding.recyclerViewCurrency.setLayoutManager(new GridLayoutManager(getContext(), 2));

        Log.d(TAG, "onCreateView: on create");
        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onResume() {
        super.onResume();

        Log.d(TAG, "onResume: is Resuming");
        dbHelper = DataBaseHelper.getDataBaseHelperInstance(getContext());

        dbHelper.new GetCurrencies(onGetCurrencyListener).execute();
        dbHelper.new GetHistoryList(onGetHistoryListener).execute();
        dbHelper.new GetBookMarks(onGetBookMarksListener).execute();

        loadDataFromPref();

        if (mWeather != null) printWeather();

        if (mUser != null && mUser.getName() != null) {
            binding.collapsingToolbar.setTitle(mUser.getName());
        }

        binding.nestedScrollView.setVisibility(View.VISIBLE);
        binding.appbar.setVisibility(View.VISIBLE);

        if (MakeRequests.isInternetAvailable(getContext())) {
            binding.progressSyncing.setVisibility(View.VISIBLE);
            YoYo.with(Techniques.BounceIn).duration(500).repeat(0).playOn(binding.progressSyncing);

            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                new ParseWeather(getContext(), onFindWeatherListener).execute();
            } else {
                binding.layoutWeather.setVisibility(View.INVISIBLE);
            }
            Log.d(TAG, "onResume: out of weather");
            requests.new LoadUser(mUser.getLogin(), mUser.getPassword(), loadUserListener).execute();
        } else {
            String dateOfSaving = SharedPreferencesHelper.readFromPref(getContext().getResources().getString(R.string.time_of_last_save_key), getContext());
            String outputText;
            if (dateOfSaving != null)
                outputText = "последнее сохранение \n" + dateOfSaving;
            else {
                outputText = "попробуйте перезайти поже";
            }
            MotionToast.Companion.createColorToast(getActivity(), "Нет интернет соединения", outputText,
                    MotionToast.TOAST_ERROR,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(getContext(), R.font.helvetica_regular));
        }
    }

    private void logOut() {
        fragmentSureToLogOut = new DialogFragmentSureToLogOut();
        fragmentSureToLogOut.setLogOutBtnClickedListener(onLogOutBtnClickedListener);
        fragmentSureToLogOut.show(getActivity().getFragmentManager(), "FragmentSetting");
    }

    private void changeName() {

        final DialogFragmentChangeName.OnBtnChangedClickedListener btnChangedClickedListener = new
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
                        }
                    };
                };

        fragmentChangeName = new DialogFragmentChangeName(btnChangedClickedListener, mUser.getName());
        fragmentChangeName.show(getFragmentManager(), "FragmentSettings");
    }

    private void showHistory() {
        List<String> list;
        if (!MakeRequests.isInternetAvailable(getContext()))
            list = mListHistory;
        else
            list = Arrays.asList(mUser.getHistory().split(";"));
        Collections.reverse(list);
        fragmentHistory = new DialogFragmentHistory(list, adapterTileHistoryClickedListener);
        fragmentHistory.show(getFragmentManager(), "FragmentDialog");
    }

    private void changeSources() {

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
                        }
                    };

                };

        if (mUser.getSites() == null) mUser.setSites(selectedSources);

        mUser.setSites(mUser.getSites().replace("null", "").replace(";;", ";"));
        fragmentSources = new DialogFragmentSources();
        fragmentSources.setUser(mUser);
        fragmentSources.setSourcesChangedListener(sourcesChangedListener);
        fragmentSources.show(getActivity().getFragmentManager(), "FragmentSettings");
    }

    private void changeCurrency() {
        DialogFragmentSelectCurrency.OnCurrencySelectedListener currencySelectedListener = new
                DialogFragmentSelectCurrency.OnCurrencySelectedListener() {
                    @Override
                    public void onSelected(CentBankCurrency currency) {
                        Log.d(TAG, "onSelected: " + currency.getName() + " (isHidden - " + currency.isHidden() + ")");
/*
                        if (mUser == null)
                            mUser = jsonManager.readUserFromJson().getUser();
*/
                        if (!currency.isHidden()) {
                            mUser.getListCurrency().remove(currency.getCharCode());
                            mListCurrency.get(mListCurrency.indexOf(currency)).setHidden(true);
                        } else {
                            mUser.getListCurrency().add(currency.getCharCode());
                            mListCurrency.get(mListCurrency.indexOf(currency)).setHidden(false);
                        }

                        mUser.reformCurrencyString();
                        mUser.reformCurrencyString();
                        SharedPreferencesHelper.writeToPref(
                                getContext().getResources().getString(R.string.selected_currency_key),
                                mUser.getCurrency(),
                                getContext());

                        Log.d(TAG, "onSelected: " + mListCurrency.get(mListCurrency.indexOf(currency)).getName() + " (isHidden - " + mListCurrency.get(mListCurrency.indexOf(currency)).isHidden() + ")");
                        Log.d(TAG, "onSelected: " + mUser.getCurrency());

                        fragmentProgress = new DialogFragmentProgressBar();
                        fragmentProgress.show(getFragmentManager(), "OnSourcesChangedListener");
                        requests.new UpdateUserAsync(onUserChangedListener, mUser).execute();
                    }

                    final MakeRequests.OnUserChangedListener onUserChangedListener = serverResponse -> {
                        Log.d(TAG, "onUserChangedListener - onChanged");
                        ArrayList<CentBankCurrency> outputListCurrency = new ArrayList<>();

                        for (int i = 0; i < mListCurrency.size(); i++) {
                            if (!mListCurrency.get(i).isHidden()) {
                                outputListCurrency.add(mListCurrency.get(i));
                            }
                        }

                        fragmentSelectCurrency.setListCurrency(mListCurrency);
                        adapterCurrencyTile.setListCurrency(outputListCurrency);
                        adapterCurrencyTile.notifyDataSetChanged();
                        fragmentProgress.dismiss();
                        Toast.makeText(getContext(), getResources().getString(R.string.successful_changed), Toast.LENGTH_SHORT).show();
                    };
                };

        fragmentSelectCurrency = new DialogFragmentSelectCurrency(mListCurrency, getContext(), currencySelectedListener);
        fragmentSelectCurrency.show(getFragmentManager(), "FragmentSettings");
    }

    private void changeToneFormat() {
        fragmentChangeToneFormat = new DialogFragmentChangeToneFormat();
        fragmentChangeToneFormat.show(getParentFragmentManager(), "FragmentSetting");
    }

    private void clearDataInDb() {
        new Thread(() -> {
            dbHelper.getAppDataBase().getHistoryDao().deleteAll();
            dbHelper.getAppDataBase().getBookmarkDao().deleteAll();
        }).start();
    }

    private void clearDataFromPref() {
        SharedPreferencesHelper.writeToPref(
                getContext().getResources().getString(R.string.name_key),
                null,
                getContext());
        SharedPreferencesHelper.writeToPref(
                getContext().getResources().getString(R.string.login_key),
                null,
                getContext());
        SharedPreferencesHelper.writeToPref(
                getContext().getResources().getString(R.string.password_key),
                null,
                getContext());
    }

    void saveCurrencyToDb(ArrayList<CentBankCurrency> listCurrency) {
        new Thread(() -> {
            dbHelper.getAppDataBase().getCurrencyDao().deleteAll();
            dbHelper.getAppDataBase().getCurrencyDao().insertAll(listCurrency.toArray
                    (new CentBankCurrency[listCurrency.size()]));
        }).start();
    }

    void saveHistoryToDb(ArrayList<History> listHistory) {
        new Thread(() -> {
            dbHelper.getAppDataBase().getHistoryDao().deleteAll();
            dbHelper.getAppDataBase().getHistoryDao().insertAll(listHistory.toArray
                    (new History[listHistory.size()]));
        }).start();
    }

    void saveWeatherToPref(Weather weather) {
        SharedPreferencesHelper.writeToPref(
                getContext().getResources().getString(R.string.temperature_key),
                weather.getTemperature(),
                getContext());
        SharedPreferencesHelper.writeToPref(
                getContext().getResources().getString(R.string.weather_url_key),
                weather.getIconUrl(),
                getContext());
        SharedPreferencesHelper.writeToPref(
                getContext().getResources().getString(R.string.weather_desc_key),
                weather.getWeatherDesc(),
                getContext());
    }

    void loadDataFromPref() {
        selectedSources = SharedPreferencesHelper.readFromPref(
                getContext().getResources().getString(R.string.selected_src_key),
                getContext());
        mUser.setName(SharedPreferencesHelper.readFromPref(
                getContext().getResources().getString(R.string.name_key),
                getContext()));
        mUser.setLogin(SharedPreferencesHelper.readFromPref(
                getContext().getResources().getString(R.string.login_key),
                getContext()));
        mUser.setPassword(SharedPreferencesHelper.readFromPref(
                getContext().getResources().getString(R.string.password_key),
                getContext()));
        mWeather.setTemperature(SharedPreferencesHelper.readFromPref(
                getContext().getResources().getString(R.string.temperature_key),
                getContext()));
        mWeather.setWeatherDesc(SharedPreferencesHelper.readFromPref(
                getContext().getResources().getString(R.string.weather_desc_key),
                getContext()));
        mWeather.setIconUrl(SharedPreferencesHelper.readFromPref(
                getContext().getResources().getString(R.string.weather_url_key),
                getContext()));
    }

    String getDate() {
        DateFormat timeFormat = new SimpleDateFormat("HH:mm dd.MM", Locale.getDefault());
        return timeFormat.format(new Date());
    }

    void printWeather() {

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            try {
                int tempInt = (int) Double.parseDouble(mWeather.getTemperature());
                binding.tvTemp.setText(tempInt + "°");
            } catch (Exception e) {
            }

            binding.tvWeatherDesc.setText(mWeather.getWeatherDesc());
            binding.imgWeatherDesc.setOnClickListener(v -> {
                MotionToast.Companion.createColorToast(getActivity(), "Описание погоды", mWeather.getWeatherDesc(),
                        MotionToast.TOAST_INFO,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(getContext(), R.font.helvetica_regular));
            });

            Picasso.with(getContext()).load(mWeather.getIconUrl()).into(binding.imgWeatherDesc);
            YoYo.with(Techniques.BounceIn).duration(500).repeat(0).playOn(binding.imgWeatherDesc);
        } else {
            binding.layoutWeather.setVisibility(View.INVISIBLE);
        }
    }

    private final DialogFragmentHistory.OnAdapterTileHistoryClickedListener adapterTileHistoryClickedListener = position -> {
        Log.d(TAG, "adapterHistory : " + MakeRequests.isInternetAvailable(getContext()));
        if (MakeRequests.isInternetAvailable(getContext())) {
            SharedPreferences pref = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
            SharedPreferences.Editor edt = pref.edit();
            edt.putString("SearchingTheme", mUser.getHistory().split(";")
                    [(mUser.getHistory().split(";")).length - position - 1]);
            edt.apply();

            fragmentHistory.dismiss();
            pager.setCurrentItem(0);
            meow.show(0, true);
        } else {
            MotionToast.Companion.createColorToast(getActivity(), "Нет интернет соединения", "попробуйте перезайти поже",
                    MotionToast.TOAST_ERROR,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(getContext(), R.font.helvetica_regular));
        }
    };

    private final AdapterSettingTiles.OnClickedSettingsItemListener onClickedSettingsItemListener = point -> {
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
            case CHANGE_FORMAT:
                changeToneFormat();
                break;
        }
    };

    private final DialogFragmentSureToLogOut.OnLogOutBtnClickedListener onLogOutBtnClickedListener = () -> {
        // deleting all information about user from json
        //jsonManager.writeDataToJson(new SavedData());

        // deleting all information about user from sharedPreferences
        SharedPreferences pref = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor edt = pref.edit();
        edt.putString("login", "");
        edt.putString("password", "");
        edt.apply();

        clearDataInDb();
        clearDataFromPref();

        fragmentSureToLogOut.dismiss();
        Intent intent = new Intent(getActivity(), ActivityMain.class);
        startActivity(intent);
    };

    private final ParseCourse.OnParseCourseListener parseCourseListener = listCurrency -> {
        mUser.fillListCurrency();
        mListCurrency = listCurrency;

        if (listCurrency != null) {
            saveCurrencyToDb(mListCurrency);
        }

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

    @SuppressLint("SetTextI18n")
    private final ParseWeather.OnFindWeatherListener onFindWeatherListener = weather -> {
        Log.d(TAG, "weather loaded");
        if (weather != null) {
            mWeather = weather;
            Log.d(TAG, "weather : " + weather.getTemperature());
            printWeather();
            saveWeatherToPref(weather);
            SharedPreferencesHelper.writeToPref(
                    getContext().getResources().getString(R.string.time_of_last_save_key),
                    getDate(),
                    getContext());
        }
    };

    private final DataBaseHelper.OnGetCurrencyListener onGetCurrencyListener = listCurrency -> {
        mListCurrency = (ArrayList<CentBankCurrency>) listCurrency;

        ArrayList<String> selectedCurrencyCharCode = new ArrayList<>();
        String currencyStr = SharedPreferencesHelper.readFromPref(
                getContext().getResources().getString(R.string.selected_currency_key),
                getContext());
        if (currencyStr != null) {
            for (String charCode : currencyStr.split(";")) {
                if (charCode != null) selectedCurrencyCharCode.add(charCode);
            }
        }
        Log.d(TAG, "saved cur : " + currencyStr);

        if (mListCurrency == null || currencyStr == null || mListCurrency.size() == 0) return;

        ArrayList<CentBankCurrency> outputListCurrency = new ArrayList<>();
        for (int i = 0; i < mListCurrency.size(); i++) {
            for (int j = 0; j < selectedCurrencyCharCode.size(); j++) {
                if (mListCurrency.get(i).getCharCode().equals(selectedCurrencyCharCode.get(j))) {
                    outputListCurrency.add(mListCurrency.get(i));
                    break;
                }
            }
        }

        adapterCurrencyTile.setListCurrency(outputListCurrency);
        adapterCurrencyTile.notifyDataSetChanged();
    };

    private final DataBaseHelper.OnGetHistoryListener onGetHistoryListener = listHistory -> {
        mListHistory = History.getStringList((ArrayList<History>) listHistory);
        if (mListHistory != null) {
            binding.tvCountThemes.setText(String.valueOf(mListHistory.size()));
        } else {
            binding.tvCountThemes.setText("---");
        }
    };

    private final DataBaseHelper.OnGetBookMarksListener onGetBookMarksListener = listBookMarks -> {
        if (listBookMarks != null) {
            binding.tvCountTracking.setText(String.valueOf(listBookMarks.size()));
        } else {
            binding.tvCountTracking.setText("---");
        }
    };

    private final MakeRequests.OnLoadUserListener loadUserListener = user -> {

        if (user == null) {
            return;
        }
        mUser = user;
        mUser.clearThemes();
        mUser.fillListHistory();
        mUser.fillListThemes();
        new ParseCourse(parseCourseListener).execute();

        SharedPreferencesHelper.writeToPref(
                getContext().getResources().getString(R.string.name_key),
                mUser.getName(),
                getContext());
        Log.d(TAG, "listCurrency : " + user.getCurrency());
        SharedPreferencesHelper.writeToPref(
                getContext().getResources().getString(R.string.selected_currency_key),
                mUser.getCurrency(),
                getContext());

        if (mUser.getListHistory() != null) {
            mListHistory = mUser.getListHistory();
            saveHistoryToDb(History.getListFromStr(mListHistory));
        }
        if (mUser.getSites() != null) selectedSources = mUser.getSites();

        binding.collapsingToolbar.setTitle(mUser.getName());
        binding.tvCountThemes.setText(String.valueOf(mUser.getListHistory().size()));
        binding.tvCountTracking.setText(String.valueOf(mUser.getListThemes().size()));
    };

}