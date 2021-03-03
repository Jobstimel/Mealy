package com.example.mealy;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.mindorks.placeholderview.listeners.ItemRemovedListener;
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener;
import com.skydoves.powerspinner.PowerSpinnerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ActivityPlayAlone extends FragmentActivity {

    //General
    private static final String TAG = "PLAY_ALONE_ACTIVITY";
    private SharedPreferences mSharedPreferences;
    private Context mContext;

    //Public
    public static List<Integer> mStackIDs;
    public static List<Integer> mLikedIDs;
    public static List<Integer> mDislikedIDs;
    public static List<Object> mResolvers;

    //Class Objects
    private SwipeHandler mSwipeHandler;
    private FilterApplier mFilterApplier;
    private FilterSpinnerHandler mFilterSpinnerHandler;
    private FilterLinearLayoutHandler mFilterLinearLayoutHandler;
    private FilterLinearLayoutCountryHandler mFilterLinearLayoutCountryHandler;
    private FilterSeekBarHandler mFilterSeekBarHandler;
    private SwipePlaceHolderViewHandlerPlayAlone mSwipePlaceHolderViewHandler;
    private PageHandler mPageHandler;

    //Result
    private ImageView mRecipePoster1;
    private ImageView mRecipePoster2;
    private ImageView mRecipePoster3;
    private TextView mRecipeScore1;
    private TextView mRecipeScore2;
    private TextView mRecipeScore3;
    private List<ImageView> mPosterList;
    private List<TextView> mScoreList;

    //Views
    private BottomNavigationView mBottomNavigationView;
    private SwipePlaceHolderView mSwipePlaceHolderView;
    private TextView mTextViewRateRecipesButton;
    private TextView mTextViewRecipeCount;
    private TextView mTextViewCalories;
    private TextView mTextViewTime;

    //Lists
    private List<Recipe> mAllRecipesList;
    private List<LinearLayout> mLinearLayoutList;
    private List<LinearLayout> mLinearLayoutCountryList;
    private List<PowerSpinnerView> mSpinnerList;
    private List<LinearLayout> mSpinnerLayoutList;

    //Spinners
    private PowerSpinnerView mPowerSpinnerAllergies;
    private PowerSpinnerView mPowerSpinnerPreparation;
    private PowerSpinnerView mPowerSpinnerCategories;
    private PowerSpinnerView mPowerSpinnerEating;

    //SeekBars
    private CrystalRangeSeekbar mSeekBarTime;
    private CrystalRangeSeekbar mSeekBarCalories;

    //Pages
    private LinearLayout mPage1;
    private LinearLayout mPage2;
    private LinearLayout mPage3;
    private LinearLayout mLoadScreen;
    private LinearLayout mTutorial;

    //LinearLayouts
    private LinearLayout mLinearLayoutLevel1;
    private LinearLayout mLinearLayoutLevel2;
    private LinearLayout mLinearLayoutLevel3;
    private LinearLayout mLinearLayoutBreakfast;
    private LinearLayout mLinearLayoutLunch;
    private LinearLayout mLinearLayoutDinner;
    private LinearLayout mLinearLayoutDessert;
    private LinearLayout mLinearLayoutSnack;
    private LinearLayout mLinearLayoutDrink;
    private LinearLayout mLinearLayoutGermany;
    private LinearLayout mLinearLayoutSpain;
    private LinearLayout mLinearLayoutAsia;
    private LinearLayout mLinearLayoutItaly;
    private LinearLayout mLinearLayoutFrance;
    private LinearLayout mLinearLayoutGreece;
    private LinearLayout mLinearLayoutIndia;

    private LinearLayout mLinearLayoutAllergiesSpinner;
    private LinearLayout mLinearLayoutPreparationsSpinner;
    private LinearLayout mLinearLayoutCategoriesSpinner;
    private LinearLayout mLinearLayoutEatingSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_alone);
        setupElements();
    }

    protected void onPause() {
        mSwipeHandler.saveLikedIndices(mLikedIDs);
        mSwipeHandler.saveDislikedIndices(mDislikedIDs);
        super.onPause();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void saveFilterValue(View v)  {
        mFilterLinearLayoutHandler.saveFilterValue(v, mFilterApplier, mTextViewRecipeCount, mTextViewRateRecipesButton);
        resetLikeDislikeList();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void saveFilterCountryValue(View v) {
        mFilterLinearLayoutCountryHandler.saveCountryFilterValue(v, mFilterApplier, mTextViewRecipeCount, mTextViewRateRecipesButton);
        resetLikeDislikeList();
    }

    public void resetFilter(View v) {
        mFilterLinearLayoutHandler.resetLayouts(mLinearLayoutList);
        mFilterLinearLayoutCountryHandler.resetCountryLayouts(mLinearLayoutCountryList);
        mFilterSeekBarHandler.resetSeekBarStates(mSeekBarCalories, mSeekBarTime);
        mFilterSpinnerHandler.resetSpinnerStates(mSpinnerList, mFilterApplier, mTextViewRecipeCount, mSpinnerLayoutList, mTextViewRateRecipesButton);
        resetLikeDislikeList();
    }

    public void resetSpinnerAllergies(View v) {
        mFilterSpinnerHandler.resetSpinner(findViewById(R.id.allergies_power_spinner), "AllergiesSpinner", mFilterApplier, mTextViewRecipeCount, mLinearLayoutAllergiesSpinner, mTextViewRateRecipesButton);
        resetLikeDislikeList();
    }

    public void resetSpinnerPreparationType(View v) {
        mFilterSpinnerHandler.resetSpinner(findViewById(R.id.preparation_type_power_spinner), "PreparationTypeSpinner", mFilterApplier, mTextViewRecipeCount, mLinearLayoutPreparationsSpinner, mTextViewRateRecipesButton);
        resetLikeDislikeList();
    }

    public void resetSpinnerCategory(View v) {
        mFilterSpinnerHandler.resetSpinner(findViewById(R.id.category_power_spinner), "CategorySpinner", mFilterApplier, mTextViewRecipeCount, mLinearLayoutCategoriesSpinner, mTextViewRateRecipesButton);
        resetLikeDislikeList();
    }

    public void resetSpinnerEatingType(View v) {
        mFilterSpinnerHandler.resetSpinner(findViewById(R.id.eating_type_power_spinner), "EatingTypeSpinner", mFilterApplier, mTextViewRecipeCount, mLinearLayoutEatingSpinner, mTextViewRateRecipesButton);
        resetLikeDislikeList();
    }

    private void resetLikeDislikeList() {
        mLikedIDs = new ArrayList<>();
        mDislikedIDs = new ArrayList<>();
    }

    public void switchPage1(View v) {
        deleteSavedData();
        mLikedIDs = new ArrayList<>();
        mDislikedIDs = new ArrayList<>();
        loadCorrectPage();
    }

    public void switchPage2(View v) {
        mPageHandler.savePage(2);
        loadCorrectPage();
    }

    public void switchPage3() {
        mPageHandler.savePage(3);
        loadCorrectPage();
    }

    private void loadCorrectPage() {
        mPageHandler.loadCorrectPage();
        Integer currentPage = mSharedPreferences.getInt("PageOffline", 1);
        if (currentPage == 1) {
            loadFilter();
        }
        else if (currentPage == 2) {
            setupSwipePlaceholderView();
        }
        else {
            loadResults();
        }
    }

    private void loadResults() {
        setupResultPage();
        mSwipeHandler.loadOfflineResults(mAllRecipesList, mLikedIDs, mDislikedIDs);
        ResultLoader mResultLoader = new ResultLoader(mContext);
        mResultLoader.loadResults(mSwipeHandler.mOfflineResults, mPosterList, mScoreList);
    }

    private void loadFilter() {
        mFilterLinearLayoutHandler.loadFilterLayoutStates(mLinearLayoutList);
        mFilterLinearLayoutCountryHandler.loadCountryFilterLayoutStates(mLinearLayoutCountryList);
        mFilterSeekBarHandler.loadSeekBarStates(mSeekBarCalories, mSeekBarTime);
        mFilterSpinnerHandler.loadSpinnerStates(mPowerSpinnerAllergies, mPowerSpinnerPreparation, mPowerSpinnerCategories, mPowerSpinnerEating);
    }

    private void setupElements() {
        mSharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        mContext = getApplicationContext();
        //resetSharedPreferences();

        setupPages();
        setupLists();
        setupClasses();
        setupViews();
        setupLinearLayouts();
        setupSpinners();
        setupSeekBars();
        setupVariables();
        loadFilter();
        setupBottomNavigationBar();
        loadCorrectPage();
        mFilterApplier.applyFilter(mTextViewRecipeCount, mTextViewRateRecipesButton);
    }

    private void setupResultPage() {
        mRecipePoster1 = findViewById(R.id.recipe_image_1);
        mRecipePoster2 = findViewById(R.id.recipe_image_2);
        mRecipePoster3 = findViewById(R.id.recipe_image_3);
        mRecipeScore1 = findViewById(R.id.recipe_score_1);
        mRecipeScore2 = findViewById(R.id.recipe_score_2);
        mRecipeScore3 = findViewById(R.id.recipe_score_3);
        mPosterList = Arrays.asList(mRecipePoster1,mRecipePoster2,mRecipePoster3);
        mScoreList = Arrays.asList(mRecipeScore1,mRecipeScore2,mRecipeScore3);
    }

    private void setupSwipePlaceholderView() {
        mSwipeHandler.loadSelectedIndices();
        mSwipePlaceHolderView = findViewById(R.id.swipeView);
        mSwipePlaceHolderViewHandler.setSwipePlaceHolderViewBuilder(mSwipePlaceHolderView);
        mSwipePlaceHolderViewHandler.loadSwipePlaceholderView(mSwipeHandler.mSelectedIDs, mAllRecipesList, mSwipePlaceHolderView);
        mStackIDs = mSwipePlaceHolderViewHandler.mStackIDs;
        mResolvers = mSwipePlaceHolderViewHandler.mResolvers;
        mSwipePlaceHolderView.addItemRemoveListener(new ItemRemovedListener() {
            @Override
            public void onItemRemoved(int count) {
                if (mSwipePlaceHolderView.getAllResolvers().size() == 0) {
                    switchPage3();
                }
            }
        });
    }

    private void setupVariables() {
        mLikedIDs = mSwipeHandler.mLikedIDs;
        mDislikedIDs = mSwipeHandler.mDislikedIDs;
    }

    private void setupLists() {
        mAllRecipesList = JsonLoader.loadRecipies(mContext);
        mLikedIDs = new ArrayList<>();
        mDislikedIDs = new ArrayList<>();
        mStackIDs = new ArrayList<>();
        mResolvers = new ArrayList<>();
    }

    private void setupPages() {
        mPage1 = findViewById(R.id.page_1);
        mPage2 = findViewById(R.id.page_2);
        mPage3 = findViewById(R.id.page_3);
        mLoadScreen = findViewById(R.id.load_screen);
        mTutorial = findViewById(R.id.tutorial);
        mTutorial.setVisibility(View.GONE);
        mTutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTutorial.setVisibility(View.GONE);
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.putBoolean("OfflineTutorial", true);
                editor.commit();
            }
        });
    }

    private void setupViews() {
        mTextViewRateRecipesButton = findViewById(R.id.text_view_create_group_button);
        mTextViewRecipeCount = findViewById(R.id.text_view_recipe_count);
        mTextViewCalories = findViewById(R.id.calories_text_view);
        mTextViewTime = findViewById(R.id.time_text_view);
    }

    private void setupClasses() {
        mFilterApplier = new FilterApplier("Offline", mSharedPreferences, mAllRecipesList, mContext);
        mFilterSpinnerHandler = new FilterSpinnerHandler("Offline", mSharedPreferences, mContext);
        mFilterLinearLayoutHandler = new FilterLinearLayoutHandler("Offline", mSharedPreferences, mContext);
        mFilterLinearLayoutCountryHandler = new FilterLinearLayoutCountryHandler("Offline", mSharedPreferences, mContext);
        mFilterSeekBarHandler = new FilterSeekBarHandler("Offline", mSharedPreferences, mContext);
        mSwipeHandler = new SwipeHandler("Offline", mSharedPreferences);
        mSwipePlaceHolderViewHandler = new SwipePlaceHolderViewHandlerPlayAlone(mContext);
        mPageHandler = new PageHandler(mPage1, mPage2, mPage3, mTutorial, mLoadScreen, mSharedPreferences, "Offline");

        mSwipeHandler.loadLikedIndices();
        mSwipeHandler.loadDislikedIndices();
    }

    private void setupSeekBars() {
        mSeekBarTime = findViewById(R.id.time_seek_bar);
        mSeekBarCalories = findViewById(R.id.calories_seek_bar);

        mSeekBarCalories.setOnRangeSeekbarChangeListener((minValue, maxValue) -> mTextViewCalories.setText(minValue+" - "+maxValue+" kcal"));

        mSeekBarCalories.setOnRangeSeekbarFinalValueListener((minValue, maxValue) -> {
            mFilterSeekBarHandler.applyCaloriesSeekBar(mSeekBarCalories, minValue.intValue(), maxValue.intValue(), mFilterApplier, mTextViewRecipeCount, mTextViewRateRecipesButton);
            resetLikeDislikeList();
        });

        mSeekBarTime.setOnRangeSeekbarChangeListener((minValue, maxValue) -> mTextViewTime.setText(minValue+" - "+maxValue+" min"));

        mSeekBarTime.setOnRangeSeekbarFinalValueListener((minValue, maxValue) -> {
            mFilterSeekBarHandler.applyTimeSeekBar(mSeekBarTime, minValue.intValue(), maxValue.intValue(), mFilterApplier, mTextViewRecipeCount, mTextViewRateRecipesButton);
            resetLikeDislikeList();
        });
    }

    private void setupLinearLayouts() {
        mLinearLayoutLevel1 = findViewById(R.id.diff1_linear_layout);
        mLinearLayoutLevel2 = findViewById(R.id.diff2_linear_layout);
        mLinearLayoutLevel3 = findViewById(R.id.diff3_linear_layout);
        mLinearLayoutBreakfast = findViewById(R.id.breakfast_linear_layout);
        mLinearLayoutLunch = findViewById(R.id.lunch_linear_layout);
        mLinearLayoutDinner = findViewById(R.id.dinner_linear_layout);
        mLinearLayoutDessert = findViewById(R.id.dessert_linear_layout);
        mLinearLayoutSnack = findViewById(R.id.snack_linear_layout);
        mLinearLayoutDrink = findViewById(R.id.drink_linear_layout);
        mLinearLayoutGermany = findViewById(R.id.germany_linear_layout);
        mLinearLayoutSpain = findViewById(R.id.spain_linear_layout);
        mLinearLayoutAsia = findViewById(R.id.asia_linear_layout);
        mLinearLayoutItaly = findViewById(R.id.italy_linear_layout);
        mLinearLayoutFrance = findViewById(R.id.france_linear_layout);
        mLinearLayoutGreece = findViewById(R.id.greece_linear_layout);
        mLinearLayoutIndia = findViewById(R.id.india_linear_layout);

        mLinearLayoutAllergiesSpinner = findViewById(R.id.allergies_linear_layout);
        mLinearLayoutPreparationsSpinner = findViewById(R.id.preparation_linear_layout);
        mLinearLayoutCategoriesSpinner = findViewById(R.id.category_linear_layout);
        mLinearLayoutEatingSpinner = findViewById(R.id.eating_linear_layout);

        mLinearLayoutList = Arrays.asList(mLinearLayoutLevel1,mLinearLayoutLevel2,mLinearLayoutLevel3,mLinearLayoutBreakfast,mLinearLayoutLunch,mLinearLayoutDinner,mLinearLayoutDessert,mLinearLayoutSnack,mLinearLayoutDrink);
        mLinearLayoutCountryList = Arrays.asList(mLinearLayoutGermany,mLinearLayoutSpain,mLinearLayoutAsia,mLinearLayoutItaly,mLinearLayoutFrance,mLinearLayoutGreece,mLinearLayoutIndia);
        mSpinnerLayoutList = Arrays.asList(mLinearLayoutAllergiesSpinner, mLinearLayoutPreparationsSpinner, mLinearLayoutCategoriesSpinner, mLinearLayoutEatingSpinner);
    }

    private void setupSpinners() {
        mPowerSpinnerAllergies = findViewById(R.id.allergies_power_spinner);
        mPowerSpinnerPreparation = findViewById(R.id.preparation_type_power_spinner);
        mPowerSpinnerCategories = findViewById(R.id.category_power_spinner);
        mPowerSpinnerEating = findViewById(R.id.eating_type_power_spinner);

        mSpinnerList = Arrays.asList(mPowerSpinnerAllergies, mPowerSpinnerPreparation, mPowerSpinnerCategories, mPowerSpinnerEating);

        mPowerSpinnerEating.setOnSpinnerItemSelectedListener((OnSpinnerItemSelectedListener<String>) (oldIndex, oldItem, newIndex, newItem) -> {
            mFilterSpinnerHandler.applySpinner("EatingTypeSpinner", newItem, newIndex, mLinearLayoutEatingSpinner);
            mFilterApplier.applyFilter(mTextViewRecipeCount, mTextViewRateRecipesButton);
            resetLikeDislikeList();
        });

        mPowerSpinnerCategories.setOnSpinnerItemSelectedListener((OnSpinnerItemSelectedListener<String>) (oldIndex, oldItem, newIndex, newItem) -> {
            mFilterSpinnerHandler.applySpinner("CategorySpinner", newItem, newIndex, mLinearLayoutCategoriesSpinner);
            mFilterApplier.applyFilter(mTextViewRecipeCount, mTextViewRateRecipesButton);
            resetLikeDislikeList();
        });

        mPowerSpinnerAllergies.setOnSpinnerItemSelectedListener((OnSpinnerItemSelectedListener<String>) (oldIndex, oldItem, newIndex, newItem) -> {
            mFilterSpinnerHandler.applySpinner("AllergiesSpinner", newItem, newIndex, mLinearLayoutAllergiesSpinner);
            mFilterApplier.applyFilter(mTextViewRecipeCount, mTextViewRateRecipesButton);
            resetLikeDislikeList();
        });

        mPowerSpinnerPreparation.setOnSpinnerItemSelectedListener((OnSpinnerItemSelectedListener<String>) (oldIndex, oldItem, newIndex, newItem) -> {
            mFilterSpinnerHandler.applySpinner("PreparationTypeSpinner", newItem, newIndex, mLinearLayoutPreparationsSpinner);
            mFilterApplier.applyFilter(mTextViewRecipeCount, mTextViewRateRecipesButton);
            resetLikeDislikeList();
        });
    }

    private void setupBottomNavigationBar() {
        mBottomNavigationView = findViewById(R.id.bottom_navigation);
        mBottomNavigationView.setSelectedItemId(R.id.play_alone);
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId()) {
                    case R.id.play_alone:
                        return true;
                    case R.id.join_group:
                        mPageHandler.showLoadScreen();
                        startActivity(new Intent(getApplicationContext(), ActivityJoinGroup.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.create_group:
                        mPageHandler.showLoadScreen();
                        startActivity(new Intent(getApplicationContext(), ActivityCreateGroup.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });
    }

    private void resetSharedPreferences() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.clear();
        editor.commit();
    }

    private void deleteSavedData() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt("PageOffline", 1);
        editor.putString("LikedOfflineIDs", "");
        editor.putString("DislikedOfflineIDs", "");
        editor.putBoolean("OfflineTutorial", false);
        editor.commit();
    }
}