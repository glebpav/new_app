package com.example.news_app.fragments.usualFragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.news_app.R;
import com.example.news_app.adapters.AdapterArticleSmallTiles;
import com.example.news_app.adapters.AdapterNews;
import com.example.news_app.comparators.articles.ComparatorArticleAlphabet;
import com.example.news_app.comparators.articles.ComparatorArticleNegative;
import com.example.news_app.comparators.articles.ComparatorArticlePositive;
import com.example.news_app.databinding.FragmentSearchingBinding;
import com.example.news_app.fragments.dialogFragments.DialogFragmentProgressBar;
import com.example.news_app.network.MakeRequests;
import com.example.news_app.models.News;
import com.example.news_app.models.User;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import www.sanju.motiontoast.MotionToast;

import static com.example.news_app.databinding.FragmentSearchingBinding.*;


public class FragmentSearching extends Fragment {

    private static final String TAG = "FRAGMENT_SEARCHING_SPACE";

    private int showingModel;
    private ArrayList<News> mListNews;
    private User user;
    private MakeRequests requests;

    private FragmentSearchingBinding binding;
    private AdapterNews adapterArticleBigTiles;
    private AdapterArticleSmallTiles adapterArticleSmallTiles;
    private DialogFragmentProgressBar fragmentProgressBar;
    private ComparatorArticleAlphabet comparatorArticleAlphabet;
    private ComparatorArticlePositive comparatorArticlePositive;
    private ComparatorArticleNegative comparatorArticleNegative;

    public FragmentSearching(User user) {
        this.user = user;
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = inflate(inflater, container, false);

        adapterArticleBigTiles = new AdapterNews(getContext(), mListNews);
        adapterArticleSmallTiles = new AdapterArticleSmallTiles(mListNews, getContext());

        binding.viewPager.setClipToPadding(false);
        binding.viewPager.setPadding(65, 0, 65, 0);
        binding.recyclerViewNews.setLayoutManager(new GridLayoutManager(getContext(), 2));

        binding.viewPager.setAdapter(adapterArticleBigTiles);
        binding.recyclerViewNews.setAdapter(adapterArticleSmallTiles);

        binding.btnFind.setOnClickListener(btnFindClicked);
        binding.imgSortAlphabet.setOnClickListener(imgSortAlphabetClicked);
        binding.imgSortPositive.setOnClickListener(imgSortPositiveClicked);
        binding.imgSortNegative.setOnClickListener(imgSortNegativeClicked);
        binding.imgViewModelCarousel.setOnClickListener(imgViewModelCarouselClicked);
        binding.imgViewModelGreed.setOnClickListener(imgViewModelGreedClicked);

        binding.layoutSortingTop.setVisibility(View.INVISIBLE);
        binding.recyclerViewNews.setVisibility(View.INVISIBLE);
        binding.viewPager.setVisibility(View.INVISIBLE);

        requests = new MakeRequests();
        comparatorArticleAlphabet = new ComparatorArticleAlphabet();
        comparatorArticlePositive = new ComparatorArticlePositive();
        comparatorArticleNegative = new ComparatorArticleNegative();

        showingModel = 1;

        return binding.getRoot();
    }

    View.OnClickListener btnFindClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String theme = binding.etTheme.getText().toString();
            if (theme.length() == 0) {
                Toast.makeText(getActivity(), "Введите тему", Toast.LENGTH_SHORT).show();
                return;
            }
            findNews(theme);
        }
    };

    @Override
    public void onResume() {
        super.onResume();

        if (requests.isInternetAvailable(getContext())) {
            SharedPreferences pref = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
            String themeFromPage = pref.getString("SearchingTheme", "");

            if (themeFromPage.length() != 0) {
                findNews(themeFromPage);
                binding.etTheme.setText(themeFromPage);
                SharedPreferences.Editor edt = pref.edit();
                edt.putString("SearchingTheme", "");
                edt.apply();
            }
            binding.btnFind.setOnClickListener(btnFindClicked);
        } else {
            MotionToast.Companion.createColorToast(getActivity(), "Нет интернет соединения", "попробуйте перезайти поже",
                    MotionToast.TOAST_ERROR,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(getContext(), R.font.helvetica_regular));
            binding.btnFind.setOnClickListener(new View.OnClickListener() {
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

    void findNews(String theme) {
        requests.new FindNews(theme, user, onFindNewsListener).execute();
        fragmentProgressBar = new DialogFragmentProgressBar();
        fragmentProgressBar.show(getFragmentManager(), "FragmentSearching");
        binding.imgHello.setVisibility(View.INVISIBLE);
        binding.viewPager.setVisibility(View.INVISIBLE);
        binding.errorLayout.setVisibility(View.INVISIBLE);
        binding.layoutSortingTop.setVisibility(View.INVISIBLE);
    }

    View.OnClickListener imgSortAlphabetClicked = new View.OnClickListener() {
        @SuppressLint("LongLogTag")
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onClick(View v) {
            Log.d(TAG, "onClick: " + mListNews.size());
            mListNews.sort(comparatorArticleAlphabet);
            adapterArticleBigTiles.setListNews(mListNews);
            binding.viewPager.setAdapter(adapterArticleBigTiles);
            adapterArticleSmallTiles.setListNews(mListNews);
            adapterArticleSmallTiles.notifyDataSetChanged();
            comparatorArticleAlphabet.nextStage();
        }
    };

    View.OnClickListener imgSortPositiveClicked = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onClick(View v) {
            mListNews.sort(comparatorArticlePositive);
            adapterArticleBigTiles.setListNews(mListNews);
            adapterArticleSmallTiles.setListNews(mListNews);
            adapterArticleSmallTiles.notifyDataSetChanged();
            binding.viewPager.setAdapter(adapterArticleBigTiles);
        }
    };

    View.OnClickListener imgSortNegativeClicked = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onClick(View v) {
            mListNews.sort(comparatorArticleNegative);
            adapterArticleBigTiles.setListNews(mListNews);
            adapterArticleSmallTiles.setListNews(mListNews);
            adapterArticleSmallTiles.notifyDataSetChanged();
            binding.viewPager.setAdapter(adapterArticleBigTiles);
        }
    };

    View.OnClickListener imgViewModelCarouselClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            adapterArticleBigTiles.setListNews(mListNews);
            binding.viewPager.setAdapter(adapterArticleBigTiles);
            binding.recyclerViewNews.setVisibility(View.INVISIBLE);
            binding.viewPager.setVisibility(View.VISIBLE);
            showingModel = 1;
        }
    };

    View.OnClickListener imgViewModelGreedClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            adapterArticleSmallTiles.setListNews(mListNews);
            adapterArticleSmallTiles.notifyDataSetChanged();
            binding.viewPager.setVisibility(View.INVISIBLE);
            binding.recyclerViewNews.setVisibility(View.VISIBLE);
            showingModel = 2;
        }
    };

    MakeRequests.OnFindNewsListener onFindNewsListener = new MakeRequests.OnFindNewsListener() {
        @Override
        public void onFoundNews(ArrayList<News> listNews) {
            Log.d("onFindNewsListener", "onFoundNews");
            fragmentProgressBar.dismiss();
            if (listNews != null) {
                mListNews = listNews;

                switch (showingModel) {
                    case 1:
                        adapterArticleBigTiles.setListNews(listNews);
                        binding.viewPager.setAdapter(adapterArticleBigTiles);
                        binding.viewPager.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        adapterArticleSmallTiles.setListNews(listNews);
                        adapterArticleSmallTiles.notifyDataSetChanged();
                        binding.recyclerViewNews.setVisibility(View.VISIBLE);
                        break;
                }

                binding.layoutSortingTop.setVisibility(View.VISIBLE);
            } else {
                binding.errorLayout.setVisibility(View.VISIBLE);
            }
        }
    };

}