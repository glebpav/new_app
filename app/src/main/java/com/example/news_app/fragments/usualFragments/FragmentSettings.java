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
import com.example.news_app.ActivityMain;
import com.example.news_app.R;
import com.example.news_app.adapters.AdapterSettingTiles;
import com.example.news_app.databinding.DialogFragmentSureToLogOutBinding;
import com.example.news_app.databinding.FragmentSettingsBinding;
import com.example.news_app.enums.SettingsPoints;
import com.example.news_app.fragments.dialogFragments.DialogFragmentChangeName;
import com.example.news_app.fragments.dialogFragments.DialogFragmentHistory;
import com.example.news_app.fragments.dialogFragments.DialogFragmentProgressBar;
import com.example.news_app.fragments.dialogFragments.DialogFragmentSources;
import com.example.news_app.fragments.dialogFragments.DialogFragmentSureToLogOut;
import com.example.news_app.network.MakeRequests;
import com.example.news_app.models.User;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


public class FragmentSettings extends Fragment {

    private User mUser;
    private MakeRequests requests;
    private FragmentSettingsBinding binding;
    private AdapterSettingTiles adapterSettingTiles;

    private ListView listHistory;
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
        binding.recyclerViewSettings.setAdapter(adapterSettingTiles);
        binding.recyclerViewSettings.setLayoutManager(new GridLayoutManager(getContext(), 2));
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

    void change_fields(final User user) {
        binding.tvCountTracking.setText(String.valueOf(user.getThemes().split(";").length));
        binding.tvCountThemes.setText(String.valueOf(user.getHistory().split(";").length));
        //if (user.history.equals(";"))user.history = null;
        List<String> list = Arrays.asList(user.getHistory().split(";"));
        System.out.println(list);
        Collections.reverse(list);
        System.out.println(list);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_list_item_1, list.toArray(new String[0]));
        listHistory.setAdapter(adapter);

        listHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences pref = Objects.requireNonNull(getActivity()).
                        getSharedPreferences("pref", Context.MODE_PRIVATE);
                SharedPreferences.Editor edt = pref.edit();
                edt.putString("SearchingTheme", user.getHistory().split(";")
                        [(user.getHistory().split(";")).length - position - 1]);
                edt.apply();

                pager.setCurrentItem(0);
                meow.show(0, true);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        initFragment();

        MakeRequests.OnLoadUserListener loadUserListener = new MakeRequests.OnLoadUserListener() {
            @Override
            public void onResults(User user) {
                binding.collapsingToolbar.setTitle(user.getName());
                binding.tvCountThemes.setText((!user.getHistory().isEmpty()) ?
                        String.valueOf(user.getHistory().split(";").length) : "0");
                binding.tvCountTracking.setText((!user.getThemes().isEmpty()) ?
                        String.valueOf(user.getThemes().split(";").length) : "0");
                mUser = user;

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
                Toast.makeText(getContext(), getResources().getString(R.string.succesfull_changed), Toast.LENGTH_SHORT).show();
            }
        };

    };


}