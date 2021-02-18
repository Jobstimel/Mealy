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
import android.view.MenuItem;
import android.view.View;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarFinalValueListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener;
import com.skydoves.powerspinner.PowerSpinnerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class PlayAlone extends FragmentActivity {

    //General
    private static final String TAG = "PlayAloneActivity";
    private SharedPreferences mSharedPreferences;
    private Context mContext;

    //Class Objects
    private FilterApplier mFilterApplier;
    private FilterSpinnerHandler mFilterSpinnerHandler;
    private FilterLinearLayoutHandler mFilterLinearLayoutHandler;
    private FilterSeekBarHandler mFilterSeekBarHandler;

    //Views
    private BottomNavigationView mBottomNavigationView;
    private TextView mTextViewRecipeCount;
    private TextView mTextViewCalories;
    private TextView mTextViewTime;

    //Lists
    private List<Recipe> mAllRecipesList;
    private List<LinearLayout> mLinearLayoutList;

    //Spinners
    private PowerSpinnerView mPowerSpinnerAllergies;
    private PowerSpinnerView mPowerSpinnerPreparation;
    private PowerSpinnerView mPowerSpinnerCategories;
    private PowerSpinnerView mPowerSpinnerEating;

    //Seekbars
    private CrystalRangeSeekbar mSeekbarTime;
    private CrystalRangeSeekbar mSeekbarCalories;

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

        mContext = getApplicationContext();

        setupElements();

        setupBottomNavigationBar();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void saveFilterValue(View v)  {
        mFilterLinearLayoutHandler.saveFilterValue(v);
        mFilterApplier.applyFilter(mTextViewRecipeCount);
    }

    private void setupElements() {
        mBottomNavigationView = findViewById(R.id.bottom_navigation);
        mBottomNavigationView.setSelectedItemId(R.id.play_alone);
        mSharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);

        mTextViewRecipeCount = findViewById(R.id.text_view_recipe_count);
        mTextViewCalories = findViewById(R.id.calories_text_view);
        mTextViewTime = findViewById(R.id.time_text_view);

        mAllRecipesList = Loader.loadRecipies(mContext);
        mFilterApplier = new FilterApplier("Offline", mSharedPreferences, mAllRecipesList);
        mFilterSpinnerHandler = new FilterSpinnerHandler("Offline", mSharedPreferences, mContext);
        mFilterLinearLayoutHandler = new FilterLinearLayoutHandler("Offline", mSharedPreferences, mContext);
        mFilterSeekBarHandler = new FilterSeekBarHandler("Offline", mSharedPreferences, mContext);

        setupLinearLayouts();
        setupSpinners();
        setupSeekBars();

        mFilterLinearLayoutHandler.loadFilterLayoutStates(mLinearLayoutList);
        mFilterSpinnerHandler.loadSpinnerStates(mPowerSpinnerAllergies, mPowerSpinnerPreparation, mPowerSpinnerCategories, mPowerSpinnerEating);
        mFilterSeekBarHandler.loadSeekBarStates(mSeekbarTime, "TimeSeekBar", mSeekbarCalories, "CaloriesSeekBar");

        mFilterApplier.applyFilter(mTextViewRecipeCount);
    }

    private void setupSeekBars() {
        mSeekbarTime = findViewById(R.id.time_seek_bar);
        mSeekbarCalories = findViewById(R.id.calories_seek_bar);

        mSeekbarCalories.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue) {
                mTextViewCalories.setText(minValue+" - "+maxValue+" kcal");
            }
        });

        mSeekbarCalories.setOnRangeSeekbarFinalValueListener(new OnRangeSeekbarFinalValueListener() {
            @Override
            public void finalValue(Number minValue, Number maxValue) {
                mFilterSeekBarHandler.applySeekBar(mSeekbarCalories, "CaloriesSeekBar", minValue.intValue(), maxValue.intValue());
                mFilterApplier.applyFilter(mTextViewRecipeCount);
            }
        });

        mSeekbarTime.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue) {
                mTextViewTime.setText(minValue+" - "+maxValue+" min");
            }
        });

        mSeekbarTime.setOnRangeSeekbarFinalValueListener(new OnRangeSeekbarFinalValueListener() {
            @Override
            public void finalValue(Number minValue, Number maxValue) {
                mFilterSeekBarHandler.applySeekBar(mSeekbarTime, "TimeSeekBar", minValue.intValue(), maxValue.intValue());
                mFilterApplier.applyFilter(mTextViewRecipeCount);
            }
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

    public void resetSpinnerAllergies(View v) {
        mFilterSpinnerHandler.resetSpinner(findViewById(R.id.allergies_power_spinner), "AllergiesSpinner");
        mFilterApplier.applyFilter(mTextViewRecipeCount);
    }

    public void resetSpinnerPreparationType(View v) {
        mFilterSpinnerHandler.resetSpinner(findViewById(R.id.preparation_type_power_spinner), "PreparationTypeSpinner");
        mFilterApplier.applyFilter(mTextViewRecipeCount);
    }

    public void resetSpinnerCategory(View v) {
        mFilterSpinnerHandler.resetSpinner(findViewById(R.id.category_power_spinner), "CategorySpinner");
        mFilterApplier.applyFilter(mTextViewRecipeCount);
    }

    public void resetSpinnerEatingType(View v) {
        mFilterSpinnerHandler.resetSpinner(findViewById(R.id.eating_type_power_spinner), "EatingTypeSpinner");
        mFilterApplier.applyFilter(mTextViewRecipeCount);
    }
}