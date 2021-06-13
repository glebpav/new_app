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

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
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

@RequiresApi(api = Build.VERSION_CODES.N)
public class FragmentSearching extends Fragment {

    private static final String TAG = "FRAGMENT_SEARCHING_SPACE";

    private User user;
    private MakeRequests requests;
    private ArrayList<News> mListNews;
    private ArrayList<News> showingNews;

    // this var says what model of article is showing (short or full)
    private int showingModel;

    // (true) - show / (false) - don't show
    // 0-st position - positive chip
    // 1-nd position - neutral chip
    // 2-nd position - negative chip
    private ArrayList<Boolean> chipArray;

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
        binding.btnAddSortingTiles.setOnClickListener(btnAddSortingTilesClicked);

        binding.chipPos.setOnClickListener(chipPosClicked);
        binding.chipNeut.setOnClickListener(chipNeutClicked);
        binding.chipNeg.setOnClickListener(chipNegClicked);

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
        initChipArray();

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (adapterArticleBigTiles != null){
            adapterArticleBigTiles.updateToneType();
            binding.viewPager.setAdapter(adapterArticleBigTiles);
        }

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
            binding.btnFind.setOnClickListener(v -> MotionToast.Companion.createColorToast(getActivity(), "Нет интернет соединения", "попробуйте перезайти поже",
                    MotionToast.TOAST_ERROR,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(getContext(), R.font.helvetica_regular)));
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

    private void initChipArray() {
        chipArray = new ArrayList<>();
        for (int i = 0; i < 3; i++) chipArray.add(true);
    }

    private void onChipArrayChanged() {
        showingNews = new ArrayList<>();
        for (News article : mListNews) {
            double rating = Double.parseDouble(article.getRating());

            if (rating < 0.35) {
                if (chipArray.get(2))
                    showingNews.add(article);
            } else if (rating <= 0.65) {
                if (chipArray.get(1))
                    showingNews.add(article);
            } else if (chipArray.get(0))
                showingNews.add(article);
        }
        adapterArticleBigTiles.setListNews(showingNews);
        adapterArticleSmallTiles.setListNews(showingNews);
        adapterArticleSmallTiles.notifyDataSetChanged();
        binding.viewPager.setAdapter(adapterArticleBigTiles);
    }

    @SuppressLint("LongLogTag")
    private final View.OnClickListener imgSortAlphabetClicked = v -> {
        Log.d(TAG, "onClick: " + mListNews.size());

        showingNews.sort(comparatorArticleAlphabet);
        adapterArticleBigTiles.setListNews(showingNews);
        binding.viewPager.setAdapter(adapterArticleBigTiles);
        adapterArticleSmallTiles.setListNews(showingNews);
        adapterArticleSmallTiles.notifyDataSetChanged();
        comparatorArticleAlphabet.nextStage();
    };

    private final View.OnClickListener imgSortPositiveClicked = v -> {
        showingNews.sort(comparatorArticlePositive);
        adapterArticleBigTiles.setListNews(showingNews);
        adapterArticleSmallTiles.setListNews(showingNews);
        adapterArticleSmallTiles.notifyDataSetChanged();
        binding.viewPager.setAdapter(adapterArticleBigTiles);
    };

    private final View.OnClickListener imgSortNegativeClicked = v -> {
        showingNews.sort(comparatorArticleNegative);
        adapterArticleBigTiles.setListNews(showingNews);
        adapterArticleSmallTiles.setListNews(showingNews);
        adapterArticleSmallTiles.notifyDataSetChanged();
        binding.viewPager.setAdapter(adapterArticleBigTiles);
    };

    private final View.OnClickListener imgViewModelGreedClicked = v -> {
        //adapterArticleSmallTiles.setListNews(showingNews);
        //adapterArticleSmallTiles.notifyDataSetChanged();
        binding.viewPager.setVisibility(View.INVISIBLE);
        binding.recyclerViewNews.setVisibility(View.VISIBLE);
        showingModel = 2;
    };

    private final View.OnClickListener imgViewModelCarouselClicked = v -> {
        //adapterArticleBigTiles.setListNews(showingNews);
        //binding.viewPager.setAdapter(adapterArticleBigTiles);
        binding.recyclerViewNews.setVisibility(View.INVISIBLE);
        binding.viewPager.setVisibility(View.VISIBLE);
        showingModel = 1;
    };

    private final View.OnClickListener btnAddSortingTilesClicked = v -> {
        if (binding.layoutSortingPictures.getVisibility() == View.GONE) {
            binding.layoutSortingPictures.setVisibility(View.VISIBLE);
            YoYo.with(Techniques.BounceIn).duration(500).repeat(0).playOn(binding.layoutSortingPictures);
            binding.btnAddSortingTiles.setImageResource(R.drawable.ic_baseline_close_24);
        }
        else {
            YoYo.with(Techniques.FadeOut).duration(500).repeat(1).playOn(binding.layoutSortingPictures);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    binding.layoutSortingPictures.setVisibility(View.GONE);
                }
            }, 500);
            binding.btnAddSortingTiles.setImageResource(R.drawable.ic_baseline_add_24);
        }
    };

    private final View.OnClickListener btnFindClicked = v -> {
        String theme = binding.etTheme.getText().toString();
        if (theme.length() == 0) {
            Toast.makeText(getActivity(), "Введите тему", Toast.LENGTH_SHORT).show();
            return;
        }
        findNews(theme);
    };

    private final View.OnClickListener chipPosClicked = v -> {
        chipArray.set(0, binding.chipPos.isChecked());
        onChipArrayChanged();
    };

    private final View.OnClickListener chipNeutClicked = v -> {
        chipArray.set(1, binding.chipNeut.isChecked());
        onChipArrayChanged();
    };

    private final View.OnClickListener chipNegClicked = v -> {
        chipArray.set(2, binding.chipNeg.isChecked());
        onChipArrayChanged();
    };

    private final MakeRequests.OnFindNewsListener onFindNewsListener = listNews -> {
        Log.d("onFindNewsListener", "onFoundNews");
        fragmentProgressBar.dismiss();
        if (listNews != null) {
            mListNews = listNews;

            adapterArticleBigTiles.setListNews(listNews);
            adapterArticleSmallTiles.setListNews(listNews);
            adapterArticleSmallTiles.notifyDataSetChanged();
            binding.viewPager.setAdapter(adapterArticleBigTiles);

            switch (showingModel) {
                case 1:
                    binding.viewPager.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    binding.recyclerViewNews.setVisibility(View.VISIBLE);
                    break;
            }

            binding.layoutSortingTop.setVisibility(View.VISIBLE);
        } else {
            binding.errorLayout.setVisibility(View.VISIBLE);
        }
    };

}