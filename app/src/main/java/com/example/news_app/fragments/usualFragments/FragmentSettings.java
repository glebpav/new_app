package com.example.news_app.fragments.usualFragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.news_app.activities.ActivityMain;
import com.example.news_app.R;
import com.example.news_app.adapters.AdapterCurrencyTile;
import com.example.news_app.adapters.AdapterSettingTiles;
import com.example.news_app.databinding.FragmentSettingsBinding;
import com.example.news_app.enums.SettingsPoints;
import com.example.news_app.fragments.dialogFragments.DialogFragmentChangeName;
import com.example.news_app.fragments.dialogFragments.DialogFragmentHistory;
import com.example.news_app.fragments.dialogFragments.DialogFragmentProgressBar;
import com.example.news_app.fragments.dialogFragments.DialogFragmentSources;
import com.example.news_app.fragments.dialogFragments.DialogFragmentSureToLogOut;
import com.example.news_app.models.CentBankCurrency;
import com.example.news_app.network.MakeRequests;
import com.example.news_app.models.User;
import com.example.news_app.network.course.ParseCourse;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


public class FragmentSettings extends Fragment {

    private User mUser;
    private MakeRequests requests;
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

    public FragmentSettings(User user, ViewPager2 pager, MeowBottomNavigation meow) {
        this.mUser = user;
        this.meow = meow;
        this.pager = pager;
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);

        requests = new MakeRequests();

        adapterSettingTiles = new AdapterSettingTiles(getContext(), onClickedSettingsItemListener);
        adapterCurrencyTile = new AdapterCurrencyTile(null);
        binding.recyclerViewSettings.setAdapter(adapterSettingTiles);
        binding.recyclerViewCurrency.setAdapter(adapterCurrencyTile);
        binding.recyclerViewSettings.setLayoutManager(new GridLayoutManager(getContext(), 2));
        binding.recyclerViewCurrency.setLayoutManager(new GridLayoutManager(getContext(), 2));
        fragmentProgress = new DialogFragmentProgressBar();

        return binding.getRoot();
    }

    private void initFragment() {
        fragmentProgress.show(getFragmentManager(), "FragmentSettings");
        binding.appbar.setVisibility(View.INVISIBLE);
        binding.nestedScrollView.setVisibility(View.INVISIBLE);
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
                    }
                }
            };

    void logOut() {
        DialogFragmentSureToLogOut.OnLogOutBtnClickedListener onLogOutBtnClickedListener = new DialogFragmentSureToLogOut.OnLogOutBtnClickedListener() {
            @Override
            public void onClicked() {
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
        fragmentChangeName = new DialogFragmentChangeName(btnChangedClickedListener, mUser.getName());
        fragmentChangeName.show(getFragmentManager(), "FragmentSettings");
    }

    void showHistory() {
        List<String> list = Arrays.asList(mUser.getHistory().split(";"));
        Collections.reverse(list);
        fragmentHistory = new DialogFragmentHistory(list, adapterTileHistoryClickedListener);
        fragmentHistory.show(getFragmentManager(), "FragmentDialog");
    }

    void changeSources() {
        mUser.setSites(mUser.getSites().replace("null", "").replace(";;", ";"));
        fragmentSources = new DialogFragmentSources();
        fragmentSources.setUser(mUser);
        fragmentSources.setSourcesChangedListener(sourcesChangedListener);
        fragmentSources.show(getActivity().getFragmentManager(), "FragmentSettings");
    }

    @Override
    public void onResume() {
        super.onResume();
        initFragment();

        final ParseCourse.OnParseCourseListener parseCourseListener = new ParseCourse.OnParseCourseListener() {
            @Override
            public void onFound(ArrayList<CentBankCurrency> listCurrency) {
                mUser.fillListCurrency();
                ArrayList<CentBankCurrency> outputListCurrency = new ArrayList<>();
                for (int i = 0; i < listCurrency.size(); i++) {
                    for (int j = 0; j < mUser.getListCurrency().size(); j++) {
                        if (listCurrency.get(i).getCharCode().equals(mUser.getListCurrency().get(j))){
                            outputListCurrency.add(listCurrency.get(i));
                            break;
                        }
                    }
                }

                adapterCurrencyTile.setListCurrency(outputListCurrency);
                adapterCurrencyTile.notifyDataSetChanged();
            }
        };

        MakeRequests.OnLoadUserListener loadUserListener = new MakeRequests.OnLoadUserListener() {
            @Override
            public void onResults(User user) {
                mUser = user;
                new ParseCourse(parseCourseListener).execute();
                mUser.clearThemes();
                mUser.fillListHistory();
                mUser.fillListThemes();

                binding.collapsingToolbar.setTitle(user.getName());
                binding.tvCountThemes.setText(String.valueOf(mUser.getListHistory().size()));
                binding.tvCountTracking.setText(String.valueOf(mUser.getListThemes().size()));

                fragmentProgress.dismiss();
                binding.nestedScrollView.setVisibility(View.VISIBLE);
                binding.appbar.setVisibility(View.VISIBLE);

            }
        };


        requests.new LoadUser(mUser.getLogin(), mUser.getPassword(), loadUserListener).execute();
    }

    DialogFragmentHistory.OnAdapterTileHistoryClickedListener adapterTileHistoryClickedListener =
            new DialogFragmentHistory.OnAdapterTileHistoryClickedListener() {
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

    DialogFragmentChangeName.OnBtnChangedClickedListener btnChangedClickedListener = new
            DialogFragmentChangeName.OnBtnChangedClickedListener() {
                @Override
                public void onClicked(String name) {
                    Log.d("TAG", "OnBtnChangedClickedListener - onClicked");
                    mUser.setName(name);
                    fragmentProgress = new DialogFragmentProgressBar();
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

    DialogFragmentSources.OnSourcesChangedListener sourcesChangedListener = new DialogFragmentSources.OnSourcesChangedListener() {
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


}