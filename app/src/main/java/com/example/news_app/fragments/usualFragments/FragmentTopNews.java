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
import com.example.news_app.databinding.FragmentTopNewsBinding;
import com.example.news_app.fileManagers.JsonManager;
import com.example.news_app.fragments.dialogFragments.DialogFragmentProgressBar;
import com.example.news_app.models.News;
import com.example.news_app.models.SavedData;
import com.example.news_app.models.User;
import com.example.news_app.network.MakeRequests;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import www.sanju.motiontoast.MotionToast;


public class FragmentTopNews extends Fragment {

    private static final String TAG = "FRAGMENT_TOP_NEWS_SPACE";

    private MakeRequests requests;
    private AdapterTopNews adapterTopNews;
    private DialogFragmentProgressBar fragmentProgressBar;

    private SavedData savedData;
    private JsonManager jsonManager;
    private FragmentTopNewsBinding binding;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTopNewsBinding.inflate(inflater, container, false);

        binding.viewPager.setVisibility(View.INVISIBLE);
        binding.layoutError.setVisibility(View.INVISIBLE);
        binding.progressSyncing.setVisibility(View.INVISIBLE);

        fragmentProgressBar = new DialogFragmentProgressBar();
        adapterTopNews = new AdapterTopNews(getContext(), null);

        savedData = new SavedData();
        jsonManager = new JsonManager(getContext());
        requests = new MakeRequests();

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        savedData = new SavedData();
        savedData = jsonManager.readUserFromJson();

        if (savedData != null && savedData.getListTopNews() != null) {
            adapterTopNews = new AdapterTopNews(getContext(), savedData.getListTopNews());
            binding.viewPager.setAdapter(adapterTopNews);
            binding.viewPager.setPadding(65, 0, 65, 0);
            binding.viewPager.setVisibility(View.VISIBLE);
        } else {
            //fragmentProgressBar.show(getFragmentManager(), "FragmentTopNews");
        }

        if (requests.isInternetAvailable(getContext())) {
            binding.progressSyncing.setVisibility(View.VISIBLE);
            YoYo.with(Techniques.BounceIn).duration(500).repeat(0).playOn(binding.progressSyncing);

            MakeRequests.FindTopNews find_topNews = requests.new FindTopNews(onFindTopNewsListener);
            find_topNews.execute();
        }
        else {
            MotionToast.Companion.createColorToast(getActivity(), "Нет интернет соединения", "попробуйте перезайти поже",
                    MotionToast.TOAST_ERROR,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(getContext(), R.font.helvetica_regular));
        }
    }

    MakeRequests.OnFindTopNewsListener onFindTopNewsListener = new MakeRequests.OnFindTopNewsListener() {
        @Override
        public void onFind(ArrayList<News> listNews) {
            if (fragmentProgressBar.isVisible())
                fragmentProgressBar.dismiss();

            boolean isEquals = true;

            if (listNews != null) {

                if (savedData != null && savedData.getListTopNews() != null) {
                    if (listNews.size() == savedData.getListTopNews().size()) {
                        for (int i = 0; i < listNews.size(); i++) {
                            if (!listNews.get(i).getTitle().equals(savedData.getListTopNews().get(i).getTitle())) {
                                isEquals = false;
                                break;
                            }
                        }
                    } else isEquals = false;
                } else isEquals = false;

                if (!isEquals) {
                    Log.d(TAG, "onFind: not equals");
                    savedData.setListTopNews(listNews);
                    jsonManager.writeOnlyTopNewsToJson(listNews);
                } else Log.d(TAG, "onFind: equals");
            } else listNews = savedData.getListTopNews();

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
        }
    };

}