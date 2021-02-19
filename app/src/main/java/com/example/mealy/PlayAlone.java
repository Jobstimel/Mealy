package com.example.mealy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarFinalValueListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mindorks.placeholderview.SwipeDecor;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener;
import com.skydoves.powerspinner.PowerSpinnerView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class PlayAlone extends FragmentActivity {

    //General
    private static final String TAG = "PlayAloneActivity";
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
    private FilterSeekBarHandler mFilterSeekBarHandler;
    private SwipePlaceHolderViewHandler mSwipePlaceHolderViewHandler;

    //Views
    private BottomNavigationView mBottomNavigationView;
    private SwipePlaceHolderView mSwipePlaceHolderView;
    private TextView mTextViewRecipeCount;
    private TextView mTextViewCalories;
    private TextView mTextViewTime;
    private ListView mResultListView;

    //Lists
    private List<Recipe> mAllRecipesList;
    private List<LinearLayout> mLinearLayoutList;
    private List<PowerSpinnerView> mSpinnerList;

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

    private void loadFilterStates() {
        mFilterLinearLayoutHandler.loadFilterLayoutStates(mLinearLayoutList);
        mFilterSpinnerHandler.loadSpinnerStates(mPowerSpinnerAllergies, mPowerSpinnerPreparation, mPowerSpinnerCategories, mPowerSpinnerEating);
        mFilterSeekBarHandler.loadSeekBarStates(mSeekBarTime, "TimeSeekBar", mSeekBarCalories, "CaloriesSeekBar");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void saveFilterValue(View v)  {
        mFilterLinearLayoutHandler.saveFilterValue(v);
        setChangeStatusTrue();
        mFilterApplier.applyFilter(mTextViewRecipeCount);
    }

    public void resetFilter(View v) {
        mFilterLinearLayoutHandler.resetLayouts(mLinearLayoutList);
        mFilterSeekBarHandler.resetSeekBarStates(mSeekBarCalories, mSeekBarTime);
        mFilterSpinnerHandler.resetSpinnerStates(mSpinnerList);
        loadFilterStates();
        setChangeStatusTrue();
        mFilterApplier.applyFilter(mTextViewRecipeCount);
        makeToast("Filter wurde zurückgesetzt");
    }

    public void resetSpinnerAllergies(View v) {
        mFilterSpinnerHandler.resetSpinner(findViewById(R.id.allergies_power_spinner), "AllergiesSpinner");
        setChangeStatusTrue();
        mFilterApplier.applyFilter(mTextViewRecipeCount);
    }

    public void resetSpinnerPreparationType(View v) {
        mFilterSpinnerHandler.resetSpinner(findViewById(R.id.preparation_type_power_spinner), "PreparationTypeSpinner");
        setChangeStatusTrue();
        mFilterApplier.applyFilter(mTextViewRecipeCount);
    }

    public void resetSpinnerCategory(View v) {
        mFilterSpinnerHandler.resetSpinner(findViewById(R.id.category_power_spinner), "CategorySpinner");
        setChangeStatusTrue();
        mFilterApplier.applyFilter(mTextViewRecipeCount);
    }

    public void resetSpinnerEatingType(View v) {
        mFilterSpinnerHandler.resetSpinner(findViewById(R.id.eating_type_power_spinner), "EatingTypeSpinner");
        setChangeStatusTrue();
        mFilterApplier.applyFilter(mTextViewRecipeCount);
    }

    private void makeToast(String text) {
        Toast toast = Toast.makeText(mContext,text, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public void switchPage1(View v) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt("PageOffline", 1);
        editor.commit();
        loadCorrectPage();
    }

    public void switchPage2(View v) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt("PageOffline", 2);
        editor.commit();
        loadCorrectPage();
    }

    public void switchPage3(View v) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt("PageOffline", 3);
        editor.commit();
        loadCorrectPage();
    }

    private void loadResults() {
        mSwipeHandler.loadOfflineResults(mAllRecipesList, mLikedIDs);
        ListViewAdapter adapter = new ListViewAdapter(this, R.layout.list_view_apdapter_layout, mSwipeHandler.mOfflineResults, "Offline");
        mResultListView.setAdapter(adapter);
    }

    private void loadCorrectPage() {
        setupPages();
        Integer mPage = mSharedPreferences.getInt("PageOffline", 1);
        if (mPage == 1) {
            mPage1.setVisibility(View.VISIBLE);
            mPage2.setVisibility(View.GONE);
            mPage3.setVisibility(View.GONE);
            mSwipeHandler.saveLikedIndices(mLikedIDs);
            mSwipeHandler.saveDislikedIndices(mDislikedIDs);
            setupViews();
            setupClasses();
            setupLinearLayouts();
            setupSpinners();
            setupSeekBars();
            loadFilterStates();
            mFilterApplier.applyFilter(mTextViewRecipeCount);
        }
        else if (mPage == 2) {
            mPage1.setVisibility(View.GONE);
            mPage2.setVisibility(View.VISIBLE);
            mPage3.setVisibility(View.GONE);
            setupSwipePlaceholderView();
        }
        else if (mPage == 3) {
            mPage1.setVisibility(View.GONE);
            mPage2.setVisibility(View.GONE);
            mPage3.setVisibility(View.VISIBLE);
            loadResults();
        }
    }

    private void setupElements() {
        mSharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        mContext = getApplicationContext();
        setupLists();
        setupClasses();
        mSwipeHandler.loadLikedIndices();
        mSwipeHandler.loadDislikedIndices();
        mLikedIDs = mSwipeHandler.mLikedIDs;
        mDislikedIDs = mSwipeHandler.mDislikedIDs;
        mResultListView = findViewById(R.id.list_view_result);
        loadCorrectPage();
        setupBottomNavigationBar();
    }

    private void setupSwipePlaceholderView() {
        mSwipeHandler.loadSelectedIndices();
        mSwipePlaceHolderView = findViewById(R.id.swipeView);
        mSwipePlaceHolderViewHandler.setSwipePlaceHolderViewBuilder(mSwipePlaceHolderView);
        mSwipePlaceHolderViewHandler.loadSwipePlaceholderView(mSwipeHandler.mSelectedIDs, mAllRecipesList, mSwipePlaceHolderView);
        mStackIDs = mSwipePlaceHolderViewHandler.mStackIDs;
        mResolvers = mSwipePlaceHolderViewHandler.mResolvers;
    }

    private void setupLists() {
        mAllRecipesList = Loader.loadRecipies(mContext);
        mLikedIDs = new ArrayList<>();
        mDislikedIDs = new ArrayList<>();
        mStackIDs = new ArrayList<>();
        mResolvers = new ArrayList<>();
    }

    private void setupPages() {
        mPage1 = findViewById(R.id.page_1);
        mPage2 = findViewById(R.id.page_2);
        mPage3 = findViewById(R.id.page_3);
    }

    private void setupViews() {
        mTextViewRecipeCount = findViewById(R.id.text_view_recipe_count);
        mTextViewCalories = findViewById(R.id.calories_text_view);
        mTextViewTime = findViewById(R.id.time_text_view);
    }

    private void setupClasses() {
        mFilterApplier = new FilterApplier("Offline", mSharedPreferences, mAllRecipesList);
        mFilterSpinnerHandler = new FilterSpinnerHandler("Offline", mSharedPreferences, mContext);
        mFilterLinearLayoutHandler = new FilterLinearLayoutHandler("Offline", mSharedPreferences, mContext);
        mFilterSeekBarHandler = new FilterSeekBarHandler("Offline", mSharedPreferences, mContext);
        mSwipeHandler = new SwipeHandler("Offline", mSharedPreferences, mContext);
        mSwipePlaceHolderViewHandler = new SwipePlaceHolderViewHandler("Offline", mSharedPreferences, mContext);
    }

    private void setupSeekBars() {
        mSeekBarTime = findViewById(R.id.time_seek_bar);
        mSeekBarCalories = findViewById(R.id.calories_seek_bar);

        mSeekBarCalories.setOnRangeSeekbarChangeListener((minValue, maxValue) -> mTextViewCalories.setText(minValue+" - "+maxValue+" kcal"));

        mSeekBarCalories.setOnRangeSeekbarFinalValueListener((minValue, maxValue) -> {
            mFilterSeekBarHandler.applySeekBar(mSeekBarCalories, "CaloriesSeekBar", minValue.intValue(), maxValue.intValue());
            mFilterApplier.applyFilter(mTextViewRecipeCount);
        });

        mSeekBarTime.setOnRangeSeekbarChangeListener((minValue, maxValue) -> mTextViewTime.setText(minValue+" - "+maxValue+" min"));

        mSeekBarTime.setOnRangeSeekbarFinalValueListener((minValue, maxValue) -> {
            mFilterSeekBarHandler.applySeekBar(mSeekBarTime, "TimeSeekBar", minValue.intValue(), maxValue.intValue());
            mFilterApplier.applyFilter(mTextViewRecipeCount);
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

        mLinearLayoutList = Arrays.asList(mLinearLayoutLevel1,mLinearLayoutLevel2,mLinearLayoutLevel3,mLinearLayoutBreakfast,mLinearLayoutLunch,mLinearLayoutDinner,mLinearLayoutDessert,mLinearLayoutSnack,mLinearLayoutDrink,mLinearLayoutGermany,mLinearLayoutSpain,mLinearLayoutAsia,mLinearLayoutItaly,mLinearLayoutFrance,mLinearLayoutGreece,mLinearLayoutIndia);
    }

    private void setupSpinners() {
        mPowerSpinnerAllergies = findViewById(R.id.allergies_power_spinner);
        mPowerSpinnerPreparation = findViewById(R.id.preparation_type_power_spinner);
        mPowerSpinnerCategories = findViewById(R.id.category_power_spinner);
        mPowerSpinnerEating = findViewById(R.id.eating_type_power_spinner);

        mSpinnerList = Arrays.asList(mPowerSpinnerAllergies, mPowerSpinnerPreparation, mPowerSpinnerCategories, mPowerSpinnerEating);

        mPowerSpinnerEating.setOnSpinnerItemSelectedListener((OnSpinnerItemSelectedListener<String>) (oldIndex, oldItem, newIndex, newItem) -> {
            mFilterSpinnerHandler.applySpinner(mPowerSpinnerEating, "EatingTypeSpinner", newItem, newIndex);
            mFilterApplier.applyFilter(mTextViewRecipeCount);
        });

        mPowerSpinnerCategories.setOnSpinnerItemSelectedListener((OnSpinnerItemSelectedListener<String>) (oldIndex, oldItem, newIndex, newItem) -> {
            mFilterSpinnerHandler.applySpinner(mPowerSpinnerCategories, "CategorySpinner", newItem, newIndex);
            mFilterApplier.applyFilter(mTextViewRecipeCount);
        });

        mPowerSpinnerAllergies.setOnSpinnerItemSelectedListener((OnSpinnerItemSelectedListener<String>) (oldIndex, oldItem, newIndex, newItem) -> {
            mFilterSpinnerHandler.applySpinner(mPowerSpinnerAllergies, "AllergiesSpinner", newItem, newIndex);
            mFilterApplier.applyFilter(mTextViewRecipeCount);
        });

        mPowerSpinnerPreparation.setOnSpinnerItemSelectedListener((OnSpinnerItemSelectedListener<String>) (oldIndex, oldItem, newIndex, newItem) -> {
            mFilterSpinnerHandler.applySpinner(mPowerSpinnerPreparation, "PreparationTypeSpinner", newItem, newIndex);
            mFilterApplier.applyFilter(mTextViewRecipeCount);
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
                        startActivity(new Intent(getApplicationContext(), JoinGroup.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.create_group:
                        startActivity(new Intent(getApplicationContext(), CreateGroup.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });
    }

    private void setChangeStatusTrue() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean("ChangeStatusOffline", true);
        editor.commit();
    }
}