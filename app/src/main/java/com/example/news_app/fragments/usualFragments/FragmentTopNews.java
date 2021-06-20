package com.example.news_app.fragments.usualFragments;

import android.os.Bundle;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.news_app.R;
import com.example.news_app.adapters.AdapterTopNews;
import com.example.news_app.databases.DataBaseHelper;
import com.example.news_app.databinding.FragmentTopNewsBinding;
import com.example.news_app.fileManagers.JsonManager;
import com.example.news_app.fragments.dialogFragments.DialogFragmentProgressBar;
import com.example.news_app.models.BookMark;
import com.example.news_app.models.News;
import com.example.news_app.models.SavedData;
import com.example.news_app.models.User;
import com.example.news_app.network.MakeRequests;
import com.example.news_app.utils.SharedPreferencesHelper;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import www.sanju.motiontoast.MotionToast;


public class FragmentTopNews extends Fragment {

    private static final String TAG = "FRAGMENT_TOP_NEWS_SPACE";

    private MakeRequests requests;
    private DataBaseHelper dbHelper;
    private AdapterTopNews adapterTopNews;
    private DialogFragmentProgressBar fragmentProgressBar;

    private FragmentTopNewsBinding binding;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTopNewsBinding.inflate(inflater, container, false);

        binding.viewPager.setVisibility(View.INVISIBLE);
        binding.layoutError.setVisibility(View.INVISIBLE);
        binding.progressSyncing.setVisibility(View.INVISIBLE);

        fragmentProgressBar = new DialogFragmentProgressBar();
        adapterTopNews = new AdapterTopNews(getContext(), null);
        requests = new MakeRequests();

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        dbHelper = DataBaseHelper.getDataBaseHelperInstance(getContext());
        dbHelper.new GetHotNews(onGetHotNewsListener).execute();

        adapterTopNews.updateToneType();

        // todo : form here
        /*
        savedData = new SavedData();
        savedData = jsonManager.readUserFromJson();
        Log.d(TAG, "onResume111: " + savedData.getListTopNews());

        // todo : to here

        if (savedData != null && savedData.getListTopNews() != null) {
            adapterTopNews = new AdapterTopNews(getContext(), savedData.getListTopNews());
            binding.viewPager.setAdapter(adapterTopNews);
            binding.viewPager.setPadding(65, 0, 65, 0);
            binding.viewPager.setVisibility(View.VISIBLE);
        } else {
            //fragmentProgressBar.show(getFragmentManager(), "FragmentTopNews");
        }
*/
        if (MakeRequests.isInternetAvailable(getContext())) {
            binding.progressSyncing.setVisibility(View.VISIBLE);
            YoYo.with(Techniques.BounceIn).duration(500).repeat(0).playOn(binding.progressSyncing);

            MakeRequests.FindTopNews find_topNews = requests.new FindTopNews(onFindTopNewsListener);
            find_topNews.execute();
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

    void saveTopNewsToDb(ArrayList<News> listNews) {
        new Thread(() -> {
            dbHelper.getAppDataBase().getHotNewsDao().deleteAll();
            dbHelper.getAppDataBase().getHotNewsDao().insertAll(listNews.toArray
                    (new News[listNews.size()]));
        }).start();
    }

    private final MakeRequests.OnFindTopNewsListener onFindTopNewsListener = listNews -> {
        if (fragmentProgressBar.isVisible())
            fragmentProgressBar.dismiss();

        if (listNews != null) {
            saveTopNewsToDb(listNews);
        } else listNews = null;

        if (listNews != null && listNews.size() != 0) {
            adapterTopNews = new AdapterTopNews(getContext(), listNews);
            binding.viewPager.setAdapter(adapterTopNews);
            binding.viewPager.setPadding(65, 0, 65, 0);
            binding.viewPager.setVisibility(View.VISIBLE);
        } else {
            binding.layoutError.setVisibility(View.VISIBLE);
        }

        YoYo.with(Techniques.FadeOut).duration(500).repeat(1).playOn(binding.progressSyncing);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.progressSyncing.setVisibility(View.INVISIBLE);
            }
        }, 500);
    };

    private final DataBaseHelper.OnGetHotNewsListener onGetHotNewsListener = listNews -> {
        if (listNews == null) {
            Log.d(TAG, "onGetHotNewsListener : news is null");
        } else {
            Log.d(TAG, "onGetBookMarksListener: " + listNews);

            adapterTopNews = new AdapterTopNews(getContext(), (ArrayList) listNews);
            binding.viewPager.setAdapter(adapterTopNews);
            binding.viewPager.setPadding(65, 0, 65, 0);
            binding.viewPager.setVisibility(View.VISIBLE);
        }
    };

}