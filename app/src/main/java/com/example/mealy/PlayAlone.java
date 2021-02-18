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

    //Views
    private BottomNavigationView mBottomNavigationView;

    //Lists
    private List<Recipe> mAllRecipesList;

    //Spinners
    private PowerSpinnerView mPowerSpinnerAllergies;
    private PowerSpinnerView mPowerSpinnerPreparation;
    private PowerSpinnerView mPowerSpinnerCategories;
    private PowerSpinnerView mPowerSpinnerEating;

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
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        if (!mSharedPreferences.getBoolean(String.valueOf(v.getTooltipText()),false)) {
            editor.putBoolean(String.valueOf(v.getTooltipText()), true);
            v.setBackgroundColor(ContextCompat.getColor(mContext, R.color.green));
        }
        else {
            editor.putBoolean(String.valueOf(v.getTooltipText()), false);
            v.setBackgroundColor(ContextCompat.getColor(mContext, R.color.foreground));
        }
        editor.putBoolean("ChangeStatus", true);
        editor.commit();
        mFilterApplier.applyFilter();
    }

    private void setupElements() {
        mBottomNavigationView = findViewById(R.id.bottom_navigation);
        mBottomNavigationView.setSelectedItemId(R.id.play_alone);
        mSharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        mAllRecipesList = Loader.loadRecipies(mContext);
        mFilterApplier = new FilterApplier("Offline", mSharedPreferences, mAllRecipesList);
        mFilterSpinnerHandler = new FilterSpinnerHandler("Offline", mSharedPreferences, mContext);

        setupSpinners();

        mFilterSpinnerHandler.loadSpinnerStates(mPowerSpinnerAllergies, mPowerSpinnerPreparation, mPowerSpinnerCategories, mPowerSpinnerEating);
    }

    private void setupSpinners() {
        mPowerSpinnerAllergies = findViewById(R.id.allergies_power_spinner);
        mPowerSpinnerPreparation = findViewById(R.id.preparation_type_power_spinner);
        mPowerSpinnerCategories = findViewById(R.id.category_power_spinner);
        mPowerSpinnerEating = findViewById(R.id.eating_type_power_spinner);

        mPowerSpinnerEating.setOnSpinnerItemSelectedListener((OnSpinnerItemSelectedListener<String>) (oldIndex, oldItem, newIndex, newItem) -> {
            mFilterSpinnerHandler.applySpinner(mPowerSpinnerEating, "EatingTypeSpinner", newItem, newIndex);
            mFilterApplier.applyFilter();
        });

        mPowerSpinnerCategories.setOnSpinnerItemSelectedListener((OnSpinnerItemSelectedListener<String>) (oldIndex, oldItem, newIndex, newItem) -> {
            mFilterSpinnerHandler.applySpinner(mPowerSpinnerCategories, "CategorySpinner", newItem, newIndex);
            mFilterApplier.applyFilter();
        });

        mPowerSpinnerAllergies.setOnSpinnerItemSelectedListener((OnSpinnerItemSelectedListener<String>) (oldIndex, oldItem, newIndex, newItem) -> {
            mFilterSpinnerHandler.applySpinner(mPowerSpinnerAllergies, "AllergiesSpinner", newItem, newIndex);
            mFilterApplier.applyFilter();
        });

        mPowerSpinnerPreparation.setOnSpinnerItemSelectedListener((OnSpinnerItemSelectedListener<String>) (oldIndex, oldItem, newIndex, newItem) -> {
            mFilterSpinnerHandler.applySpinner(mPowerSpinnerPreparation, "PreparationTypeSpinner", newItem, newIndex);
            mFilterApplier.applyFilter();
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
        mFilterApplier.applyFilter();
    }

    public void resetSpinnerPreparationType(View v) {
        mFilterSpinnerHandler.resetSpinner(findViewById(R.id.preparation_type_power_spinner), "PreparationTypeSpinner");
        mFilterApplier.applyFilter();
    }

    public void resetSpinnerCategory(View v) {
        mFilterSpinnerHandler.resetSpinner(findViewById(R.id.category_power_spinner), "CategorySpinner");
        mFilterApplier.applyFilter();
    }

    public void resetSpinnerEatingType(View v) {
        mFilterSpinnerHandler.resetSpinner(findViewById(R.id.eating_type_power_spinner), "EatingTypeSpinner");
        mFilterApplier.applyFilter();
    }
}