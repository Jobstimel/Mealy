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
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
    private static final String MODE = "Offline";
    private SharedPreferences mSharedPreferences;
    private Context mContext;

    //Public
    public static List<Integer> mDislikedIDs = new ArrayList<>();
    public static List<Integer> mLikedIDs = new ArrayList<>();
    public static List<Integer> mStackIDs = new ArrayList<>();
    public static List<Object> mResolvers = new ArrayList<>();

    //Filter
    private TextView mTextViewRecipeHitCounter;

    private TextView mTextViewBreakfast;
    private TextView mTextViewLunch;
    private TextView mTextViewDinner;
    private TextView mTextViewDessert;
    private TextView mTextViewSnack;
    private TextView mTextViewDrink;
    private TextView mTextViewLevel1;
    private TextView mTextViewLevel2;
    private TextView mTextViewLevel3;
    private List<TextView> mTextViewList;

    private ImageView mImageViewGermany;
    private ImageView mImageViewSpain;
    private ImageView mImageViewAsia;
    private ImageView mImageViewItaly;
    private ImageView mImageViewIndia;
    private ImageView mImageViewFrance;
    private ImageView mImageViewGreece;
    private List<ImageView> mImageViewList;

    private CrystalRangeSeekbar mSeekBarCalories;
    private CrystalRangeSeekbar mSeekBarTime;
    private TextView mTextViewCalories;
    private TextView mTextViewTime;

    private PowerSpinnerView mPowerSpinnerPreparation;
    private PowerSpinnerView mPowerSpinnerCategories;
    private PowerSpinnerView mPowerSpinnerAllergies;
    private PowerSpinnerView mPowerSpinnerEating;
    private List<PowerSpinnerView> mSpinnerList;

    //Class Objects
    private SwipePlaceHolderViewHandlerPlayAlone mSwipePlaceHolderViewHandler;
    private FilterImageViewHandler mFilterImageViewHandler;
    private FilterTextViewHandler mFilterTextViewHandler;
    private FilterSpinnerHandler mFilterSpinnerHandler;
    private FilterSeekBarHandler mFilterSeekBarHandler;
    private FilterApplier mFilterApplier;
    private SwipeHandler mSwipeHandler;
    private PageHandler mPageHandler;

    //Views
    private BottomNavigationView mBottomNavigationView;
    private SwipePlaceHolderView mSwipePlaceHolderView;
    private TextView mTextViewRateRecipesButton;
    private TextView mTextViewResultPageHead;
    private TextView mResultPageButton;
    private ListView mListView;

    //Pages
    private LinearLayout mLoadScreen;
    private LinearLayout mTutorial;
    private LinearLayout mPage1;
    private LinearLayout mPage2;
    private LinearLayout mPage3;

    //Lists
    private List<Recipe> mAllRecipesList;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_alone);
        setupElements();
    }

    @Override
    protected void onPause() {
        mSwipeHandler.saveLikedIndices(mLikedIDs);
        mSwipeHandler.saveDislikedIndices(mDislikedIDs);
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        overridePendingTransition(0,0);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void saveTextViewState(View v)  {
        mFilterTextViewHandler.saveTextViewState(v, mFilterApplier, mTextViewRecipeHitCounter, mTextViewRateRecipesButton);
        resetLikeDislikeList();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void saveImageViewState(View v) {
        mFilterImageViewHandler.saveImageViewState(v, mFilterApplier, mTextViewRecipeHitCounter, mTextViewRateRecipesButton);
        resetLikeDislikeList();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void resetFilter(View v) {
        mFilterTextViewHandler.resetTextViewStates(mTextViewList);
        mFilterImageViewHandler.resetImageViewStates(mImageViewList);
        mFilterSeekBarHandler.resetSeekBarStates(mSeekBarCalories, mSeekBarTime);
        mFilterSpinnerHandler.resetSpinnerStates(mSpinnerList, mFilterApplier, mTextViewRecipeHitCounter, mTextViewRateRecipesButton);
        resetLikeDislikeList();
        mFilterApplier.applyFilter(mTextViewRecipeHitCounter, mTextViewRateRecipesButton);
        makeToast("Filter wurde zurückgesetzt");
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void resetSpinnerAllergies(View v) {
        mFilterSpinnerHandler.resetSpinnerState(mPowerSpinnerAllergies, "AllergiesSpinner", mFilterApplier, mTextViewRecipeHitCounter, mTextViewRateRecipesButton);
        resetLikeDislikeList();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void resetSpinnerPreparationType(View v) {
        mFilterSpinnerHandler.resetSpinnerState(mPowerSpinnerPreparation, "PreparationTypeSpinner", mFilterApplier, mTextViewRecipeHitCounter, mTextViewRateRecipesButton);
        resetLikeDislikeList();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void resetSpinnerCategory(View v) {
        mFilterSpinnerHandler.resetSpinnerState(mPowerSpinnerCategories, "CategorySpinner", mFilterApplier, mTextViewRecipeHitCounter, mTextViewRateRecipesButton);
        resetLikeDislikeList();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void resetSpinnerEatingType(View v) {
        mFilterSpinnerHandler.resetSpinnerState(mPowerSpinnerEating, "EatingTypeSpinner", mFilterApplier, mTextViewRecipeHitCounter, mTextViewRateRecipesButton);
        resetLikeDislikeList();
    }

    private void resetLikeDislikeList() {
        mLikedIDs = new ArrayList<>();
        mDislikedIDs = new ArrayList<>();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void switchPage1() {
        deleteSavedData();
        mLikedIDs = new ArrayList<>();
        mDislikedIDs = new ArrayList<>();
        loadCorrectPage();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void switchPage2() {
        mPageHandler.savePage(2);
        loadCorrectPage();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void switchPage3() {
        mPageHandler.savePage(3);
        mSwipeHandler.saveLikedIndices(mLikedIDs);
        mSwipeHandler.saveDislikedIndices(mDislikedIDs);
        loadCorrectPage();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void loadCorrectPage() {
        if (mPageHandler == null) {
            mPageHandler = new PageHandler(mPage1, mPage2, mPage3, mTutorial, mLoadScreen, mSharedPreferences, MODE);
        }
        mPageHandler.loadCorrectPage();
        Integer currentPage = mSharedPreferences.getInt("Page"+MODE, 1);
        if (currentPage == 1) {
            loadFilter();
            mFilterApplier.applyFilter(mTextViewRecipeHitCounter, mTextViewRateRecipesButton);
        }
        else if (currentPage == 2) {
            mSwipePlaceHolderViewHandler = new SwipePlaceHolderViewHandlerPlayAlone(mContext);
            setupSwipePlaceholderView();
        }
        else {
            loadResults();
        }
    }

    private void loadResults() {
        mSwipeHandler.loadOfflineResults(mAllRecipesList);
        RecipeListAdapter adapter = new RecipeListAdapter(this, R.layout.list_view_apdapter_layout, mSwipeHandler.mOfflineResults);
        mListView.setAdapter(adapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void loadFilter() {
        mFilterTextViewHandler.loadTextViewStates(mTextViewList);
        mFilterImageViewHandler.loadImageViewStates(mImageViewList);
        mFilterSeekBarHandler.loadSeekBarStates(mSeekBarCalories, mSeekBarTime);
        mFilterSpinnerHandler.loadSpinnerStates(mPowerSpinnerAllergies, mPowerSpinnerPreparation, mPowerSpinnerCategories, mPowerSpinnerEating);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setupElements() {
        mSharedPreferences = getSharedPreferences("MealyData", Context.MODE_PRIVATE);
        mContext = getApplicationContext();
        //resetSharedPreferences();

        setupPages();
        setupClasses();
        setupViews();
        setupSeekBars();
        setupSpinners();
        setupBottomNavigationBar();
        loadFilter();
        loadCorrectPage();
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
                editor.putBoolean(MODE+"Tutorial", true);
                editor.commit();
            }
        });
    }

    private void setupClasses() {
        mAllRecipesList = JsonLoader.loadRecipies(mContext);

        mFilterTextViewHandler = new FilterTextViewHandler(mSharedPreferences, mContext, MODE);
        mFilterImageViewHandler = new FilterImageViewHandler(mSharedPreferences, mContext, MODE);
        mFilterSeekBarHandler = new FilterSeekBarHandler(mSharedPreferences, mContext, MODE);
        mFilterSpinnerHandler = new FilterSpinnerHandler(mSharedPreferences, mContext, MODE);
        mFilterApplier = new FilterApplier(mSharedPreferences, mAllRecipesList, mContext, MODE);

        mSwipeHandler = new SwipeHandler(mSharedPreferences, MODE);
        mSwipeHandler.loadLikedIndices();
        mSwipeHandler.loadDislikedIndices();
        mLikedIDs = mSwipeHandler.mLikedIDs;
        mDislikedIDs = mSwipeHandler.mDislikedIDs;
    }

    private void setupViews() {
        mTextViewRecipeHitCounter = findViewById(R.id.recipe_count);

        mTextViewBreakfast = findViewById(R.id.breakfast);
        mTextViewLunch = findViewById(R.id.lunch);
        mTextViewDinner = findViewById(R.id.dinner);
        mTextViewDessert = findViewById(R.id.dessert);
        mTextViewSnack = findViewById(R.id.snack);
        mTextViewDrink = findViewById(R.id.drink);
        mTextViewLevel1 = findViewById(R.id.level1);
        mTextViewLevel2 = findViewById(R.id.level2);
        mTextViewLevel3 = findViewById(R.id.level3);
        mTextViewList = Arrays.asList(mTextViewLevel1,mTextViewLevel2,mTextViewLevel3,mTextViewBreakfast,mTextViewLunch,mTextViewDinner,mTextViewDessert,mTextViewSnack,mTextViewDrink);

        mImageViewGermany = findViewById(R.id.germany);
        mImageViewSpain = findViewById(R.id.spain);
        mImageViewAsia = findViewById(R.id.asia);
        mImageViewItaly = findViewById(R.id.italy);
        mImageViewIndia = findViewById(R.id.india);
        mImageViewFrance = findViewById(R.id.france);
        mImageViewGreece = findViewById(R.id.greece);
        mImageViewList = Arrays.asList(mImageViewGermany,mImageViewSpain,mImageViewAsia,mImageViewItaly,mImageViewIndia,mImageViewFrance,mImageViewGreece);

        mTextViewRateRecipesButton = findViewById(R.id.text_view_create_group_button);
        mTextViewRateRecipesButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                switchPage2();
            }
        });

        mTextViewResultPageHead = findViewById(R.id.result_page_head);
        mTextViewResultPageHead.setText("Solomodus");

        mListView = findViewById(R.id.list_view);

        mResultPageButton = findViewById(R.id.result_page_button);
        mResultPageButton.setText("Zurück zum Filter");
        mResultPageButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                switchPage1();
            }
        });
    }

    private void setupSeekBars() {
        mSeekBarTime = findViewById(R.id.time_seek_bar);
        mTextViewTime = findViewById(R.id.time_text_view);

        mSeekBarCalories = findViewById(R.id.calories_seek_bar);
        mTextViewCalories = findViewById(R.id.calories_text_view);

        mSeekBarCalories.setOnRangeSeekbarChangeListener((minValue, maxValue) -> mTextViewCalories.setText(minValue+" - "+maxValue+" kcal"));

        mSeekBarCalories.setOnRangeSeekbarFinalValueListener((minValue, maxValue) -> {
            mFilterSeekBarHandler.saveCaloriesSeekBarState(mSeekBarCalories, minValue.intValue(), maxValue.intValue(), mFilterApplier, mTextViewRecipeHitCounter, mTextViewRateRecipesButton);
            resetLikeDislikeList();
        });

        mSeekBarTime.setOnRangeSeekbarChangeListener((minValue, maxValue) -> mTextViewTime.setText(minValue+" - "+maxValue+" min"));

        mSeekBarTime.setOnRangeSeekbarFinalValueListener((minValue, maxValue) -> {
            mFilterSeekBarHandler.saveTimeSeekBarState(mSeekBarTime, minValue.intValue(), maxValue.intValue(), mFilterApplier, mTextViewRecipeHitCounter, mTextViewRateRecipesButton);
            resetLikeDislikeList();
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setupSpinners() {
        mPowerSpinnerAllergies = findViewById(R.id.allergies_power_spinner);
        mPowerSpinnerPreparation = findViewById(R.id.preparation_type_power_spinner);
        mPowerSpinnerCategories = findViewById(R.id.category_power_spinner);
        mPowerSpinnerEating = findViewById(R.id.eating_type_power_spinner);
        mSpinnerList = Arrays.asList(mPowerSpinnerAllergies, mPowerSpinnerPreparation, mPowerSpinnerCategories, mPowerSpinnerEating);

        mPowerSpinnerEating.setOnSpinnerItemSelectedListener((OnSpinnerItemSelectedListener<String>) (oldIndex, oldItem, newIndex, newItem) -> {
            mFilterSpinnerHandler.saveSpinnerState("EatingTypeSpinner", newItem, newIndex, mPowerSpinnerEating);
            mFilterApplier.applyFilter(mTextViewRecipeHitCounter, mTextViewRateRecipesButton);
            resetLikeDislikeList();
        });

        mPowerSpinnerCategories.setOnSpinnerItemSelectedListener((OnSpinnerItemSelectedListener<String>) (oldIndex, oldItem, newIndex, newItem) -> {
            mFilterSpinnerHandler.saveSpinnerState("CategorySpinner", newItem, newIndex, mPowerSpinnerCategories);
            mFilterApplier.applyFilter(mTextViewRecipeHitCounter, mTextViewRateRecipesButton);
            resetLikeDislikeList();
        });

        mPowerSpinnerAllergies.setOnSpinnerItemSelectedListener((OnSpinnerItemSelectedListener<String>) (oldIndex, oldItem, newIndex, newItem) -> {
            mFilterSpinnerHandler.saveSpinnerState("AllergiesSpinner", newItem, newIndex, mPowerSpinnerAllergies);
            mFilterApplier.applyFilter(mTextViewRecipeHitCounter, mTextViewRateRecipesButton);
            resetLikeDislikeList();
        });

        mPowerSpinnerPreparation.setOnSpinnerItemSelectedListener((OnSpinnerItemSelectedListener<String>) (oldIndex, oldItem, newIndex, newItem) -> {
            mFilterSpinnerHandler.saveSpinnerState("PreparationTypeSpinner", newItem, newIndex, mPowerSpinnerPreparation);
            mFilterApplier.applyFilter(mTextViewRecipeHitCounter, mTextViewRateRecipesButton);
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

    private void setupSwipePlaceholderView() {
        if (mSwipePlaceHolderViewHandler == null) {
            mSwipePlaceHolderViewHandler = new SwipePlaceHolderViewHandlerPlayAlone(mContext);
        }
        mSwipeHandler.loadSelectedIndices();
        mSwipePlaceHolderView = findViewById(R.id.swipeView);
        mSwipePlaceHolderViewHandler.setSwipePlaceHolderViewBuilder(mSwipePlaceHolderView);
        mSwipePlaceHolderViewHandler.loadSwipePlaceholderView(mSwipeHandler.mSelectedIDs, mAllRecipesList, mSwipePlaceHolderView);
        mStackIDs = mSwipePlaceHolderViewHandler.mStackIDs;
        mResolvers = mSwipePlaceHolderViewHandler.mResolvers;
        mSwipePlaceHolderView.addItemRemoveListener(new ItemRemovedListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onItemRemoved(int count) {
                if (mSwipePlaceHolderView.getAllResolvers().size() == 0) {
                    switchPage3();
                }
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
        editor.putInt("Page"+MODE, 1);
        editor.putString("Liked"+MODE+"IDs", "");
        editor.putString("Disliked"+MODE+"IDs", "");
        editor.putBoolean(MODE+"Tutorial", false);
        editor.commit();
    }

    private void makeToast(String text) {
        Toast toast = Toast.makeText(mContext, text, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}