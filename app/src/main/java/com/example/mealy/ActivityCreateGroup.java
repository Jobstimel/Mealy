package com.example.mealy;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener;
import com.skydoves.powerspinner.PowerSpinnerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ActivityCreateGroup extends AppCompatActivity {

    //General
    private static final String TAG = "CREATE_GROUP_ACTIVITY";
    private SharedPreferences mSharedPreferences;
    private Context mContext;

    //Public
    public static List<Integer> mLikedIDs;
    public static List<Integer> mDislikedIDs;

    //Classes
    private FilterSpinnerHandler mFilterSpinnerHandler;
    private FilterApplier mFilterApplier;
    private FilterLinearLayoutHandler mFilterLinearLayoutHandler;
    private FilterLinearLayoutCountryHandler mFilterLinearLayoutCountryHandler;
    private FilterSeekBarHandler mFilterSeekBarHandler;
    private SwipeHandler mSwipeHandler;

    //Views
    private BottomNavigationView mBottomNavigationView;
    private TextView mTextViewRecipeCount;
    private TextView mTextViewCreateGroupButton;

    //Lists
    private List<Recipe> mAllRecipesList;
    private List<PowerSpinnerView> mSpinnerList;

    //SeekBars
    private CrystalRangeSeekbar mSeekBarTime;
    private CrystalRangeSeekbar mSeekBarCalories;

    private TextView mTextViewCalories;
    private TextView mTextViewTime;

    //Spinners
    private PowerSpinnerView mPowerSpinnerAllergies;
    private PowerSpinnerView mPowerSpinnerPreparation;
    private PowerSpinnerView mPowerSpinnerCategories;
    private PowerSpinnerView mPowerSpinnerEating;

    private LinearLayout mLinearLayoutAllergiesSpinner;
    private LinearLayout mLinearLayoutPreparationsSpinner;
    private LinearLayout mLinearLayoutCategoriesSpinner;
    private LinearLayout mLinearLayoutEatingSpinner;

    private List<LinearLayout> mSpinnerLayoutList;

    //Layouts
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

    private List<LinearLayout> mLinearLayoutList;
    private List<LinearLayout> mLinearLayoutCountryList;

    //Database
    private DatabaseReference mDatabaseReference;
    private DataSnapshot mDataSnapshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        setupElements();
    }

    public void createGroup(View v) {
        mSwipeHandler.loadSelectedIndices();
        RandomGenerator randomGenerator = new RandomGenerator(mSharedPreferences);
        randomGenerator.generateRandomGroupCode();
        List<String> counter = new ArrayList<String>(Collections.nCopies(FilterApplier.MAX_RECIPES, "0"));
        List<String> voting_completed = new ArrayList<String>(Collections.nCopies(20, ""));
        List<String> selectedIndices = new ArrayList<>();
        for (int i = 0; i < mSwipeHandler.mSelectedIDs.size(); i++) {
            selectedIndices.add(String.valueOf(mSwipeHandler.mSelectedIDs.get(i)));
        }
        mDatabaseReference.child(mSharedPreferences.getString("GroupCode", "")).child("selected_ids").setValue(selectedIndices);
        mDatabaseReference.child(mSharedPreferences.getString("GroupCode", "")).child("counter").setValue(counter);
        mDatabaseReference.child(mSharedPreferences.getString("GroupCode", "")).child("voting_completed").setValue(voting_completed);
        mDatabaseReference.child(mSharedPreferences.getString("GroupCode", "")).child("people_number").setValue("0");
        mDatabaseReference.child(mSharedPreferences.getString("GroupCode", "")).child("group_status").setValue("open");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void saveFilterValue(View v)  {
        mFilterLinearLayoutHandler.saveFilterValue(v, mFilterApplier, mTextViewRecipeCount, mTextViewCreateGroupButton);
        resetLikeDislikeList();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void saveFilterCountryValue(View v) {
        mFilterLinearLayoutCountryHandler.saveCountryFilterValue(v, mFilterApplier, mTextViewRecipeCount, mTextViewCreateGroupButton);
        resetLikeDislikeList();
    }

    private void resetLikeDislikeList() {
        mLikedIDs = new ArrayList<>();
        mDislikedIDs = new ArrayList<>();
    }

    public void resetSpinnerAllergies(View v) {
        mFilterSpinnerHandler.resetSpinner(findViewById(R.id.allergies_power_spinner), "AllergiesSpinner", mFilterApplier, mTextViewRecipeCount, mLinearLayoutAllergiesSpinner, mTextViewCreateGroupButton);
        resetLikeDislikeList();
    }

    public void resetSpinnerPreparationType(View v) {
        mFilterSpinnerHandler.resetSpinner(findViewById(R.id.preparation_type_power_spinner), "PreparationTypeSpinner", mFilterApplier, mTextViewRecipeCount, mLinearLayoutPreparationsSpinner, mTextViewCreateGroupButton);
        resetLikeDislikeList();
    }

    public void resetSpinnerCategory(View v) {
        mFilterSpinnerHandler.resetSpinner(findViewById(R.id.category_power_spinner), "CategorySpinner", mFilterApplier, mTextViewRecipeCount, mLinearLayoutCategoriesSpinner, mTextViewCreateGroupButton);
        resetLikeDislikeList();
    }

    public void resetSpinnerEatingType(View v) {
        mFilterSpinnerHandler.resetSpinner(findViewById(R.id.eating_type_power_spinner), "EatingTypeSpinner", mFilterApplier, mTextViewRecipeCount, mLinearLayoutEatingSpinner, mTextViewCreateGroupButton);
        resetLikeDislikeList();
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

        setupLists();
        setupClasses();
        setupViews();
        setupSeekBars();
        setupSpinners();
        setupLayouts();
        setupDatabase();
        setupBottomNavigationBar();
        loadFilter();
        mFilterApplier.applyFilter(mTextViewRecipeCount, mTextViewCreateGroupButton);
    }

    private void setupClasses() {
        mFilterSpinnerHandler = new FilterSpinnerHandler("Online", mSharedPreferences, mContext);
        mFilterApplier = new FilterApplier("Online", mSharedPreferences, mAllRecipesList, mContext);
        mFilterLinearLayoutHandler = new FilterLinearLayoutHandler("Online", mSharedPreferences, mContext);
        mFilterLinearLayoutCountryHandler = new FilterLinearLayoutCountryHandler("Online", mSharedPreferences, mContext);
        mFilterSeekBarHandler = new FilterSeekBarHandler("Online", mSharedPreferences, mContext);
        mSwipeHandler = new SwipeHandler("Online", mSharedPreferences);
    }

    private void setupDatabase() {
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mDataSnapshot = dataSnapshot;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("TAG", "DATABASE ERROR: ", databaseError.toException());
            }
        });
    }

    private void setupLists() {
        mAllRecipesList = JsonLoader.loadRecipies(mContext);
        mLikedIDs = new ArrayList<>();
        mDislikedIDs = new ArrayList<>();
    }

    private void setupViews() {
        mTextViewRecipeCount = findViewById(R.id.text_view_recipe_count);
        mTextViewCreateGroupButton = findViewById(R.id.text_view_create_group_button);
    }

    private void setupSeekBars() {
        mSeekBarTime = findViewById(R.id.time_seek_bar);
        mSeekBarCalories = findViewById(R.id.calories_seek_bar);
        mTextViewCalories = findViewById(R.id.calories_text_view);
        mTextViewTime = findViewById(R.id.time_text_view);

        mSeekBarCalories.setOnRangeSeekbarChangeListener((minValue, maxValue) -> mTextViewCalories.setText(minValue+" - "+maxValue+" kcal"));

        mSeekBarCalories.setOnRangeSeekbarFinalValueListener((minValue, maxValue) -> {
            mFilterSeekBarHandler.applyCaloriesSeekBar(mSeekBarCalories, minValue.intValue(), maxValue.intValue(), mFilterApplier, mTextViewRecipeCount, mTextViewCreateGroupButton);
            resetLikeDislikeList();
        });

        mSeekBarTime.setOnRangeSeekbarChangeListener((minValue, maxValue) -> mTextViewTime.setText(minValue+" - "+maxValue+" min"));

        mSeekBarTime.setOnRangeSeekbarFinalValueListener((minValue, maxValue) -> {
            mFilterSeekBarHandler.applyTimeSeekBar(mSeekBarTime, minValue.intValue(), maxValue.intValue(), mFilterApplier, mTextViewRecipeCount, mTextViewCreateGroupButton);
            resetLikeDislikeList();
        });
    }

    private void setupSpinners() {
        mPowerSpinnerAllergies = findViewById(R.id.allergies_power_spinner);
        mPowerSpinnerPreparation = findViewById(R.id.preparation_type_power_spinner);
        mPowerSpinnerCategories = findViewById(R.id.category_power_spinner);
        mPowerSpinnerEating = findViewById(R.id.eating_type_power_spinner);

        mSpinnerList = Arrays.asList(mPowerSpinnerAllergies, mPowerSpinnerPreparation, mPowerSpinnerCategories, mPowerSpinnerEating);

        mPowerSpinnerEating.setOnSpinnerItemSelectedListener((OnSpinnerItemSelectedListener<String>) (oldIndex, oldItem, newIndex, newItem) -> {
            mFilterSpinnerHandler.applySpinner("EatingTypeSpinner", newItem, newIndex, mLinearLayoutEatingSpinner);
            mFilterApplier.applyFilter(mTextViewRecipeCount, mTextViewCreateGroupButton);
            resetLikeDislikeList();
        });

        mPowerSpinnerCategories.setOnSpinnerItemSelectedListener((OnSpinnerItemSelectedListener<String>) (oldIndex, oldItem, newIndex, newItem) -> {
            mFilterSpinnerHandler.applySpinner("CategorySpinner", newItem, newIndex, mLinearLayoutCategoriesSpinner);
            mFilterApplier.applyFilter(mTextViewRecipeCount, mTextViewCreateGroupButton);
            resetLikeDislikeList();
        });

        mPowerSpinnerAllergies.setOnSpinnerItemSelectedListener((OnSpinnerItemSelectedListener<String>) (oldIndex, oldItem, newIndex, newItem) -> {
            mFilterSpinnerHandler.applySpinner("AllergiesSpinner", newItem, newIndex, mLinearLayoutAllergiesSpinner);
            mFilterApplier.applyFilter(mTextViewRecipeCount, mTextViewCreateGroupButton);
            resetLikeDislikeList();
        });

        mPowerSpinnerPreparation.setOnSpinnerItemSelectedListener((OnSpinnerItemSelectedListener<String>) (oldIndex, oldItem, newIndex, newItem) -> {
            mFilterSpinnerHandler.applySpinner("PreparationTypeSpinner", newItem, newIndex, mLinearLayoutPreparationsSpinner);
            mFilterApplier.applyFilter(mTextViewRecipeCount, mTextViewCreateGroupButton);
            resetLikeDislikeList();
        });

        mLinearLayoutAllergiesSpinner = findViewById(R.id.allergies_linear_layout);
        mLinearLayoutPreparationsSpinner = findViewById(R.id.preparation_linear_layout);
        mLinearLayoutCategoriesSpinner = findViewById(R.id.category_linear_layout);
        mLinearLayoutEatingSpinner = findViewById(R.id.eating_linear_layout);

        mSpinnerLayoutList = Arrays.asList(mLinearLayoutAllergiesSpinner, mLinearLayoutPreparationsSpinner, mLinearLayoutCategoriesSpinner, mLinearLayoutEatingSpinner);
    }

    private void setupLayouts() {
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

        mLinearLayoutList = Arrays.asList(mLinearLayoutLevel1,mLinearLayoutLevel2,mLinearLayoutLevel3,mLinearLayoutBreakfast,mLinearLayoutLunch,mLinearLayoutDinner,mLinearLayoutDessert,mLinearLayoutSnack,mLinearLayoutDrink);
        mLinearLayoutCountryList = Arrays.asList(mLinearLayoutGermany,mLinearLayoutSpain,mLinearLayoutAsia,mLinearLayoutItaly,mLinearLayoutFrance,mLinearLayoutGreece,mLinearLayoutIndia);
    }

    private void setupBottomNavigationBar() {
        mBottomNavigationView = findViewById(R.id.bottom_navigation);
        mBottomNavigationView.setSelectedItemId(R.id.create_group);
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId()) {
                    case R.id.play_alone:
                        startActivity(new Intent(getApplicationContext(), ActivityPlayAlone.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.join_group:
                        startActivity(new Intent(getApplicationContext(), ActivityJoinGroup.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.create_group:
                        return true;
                }
                return false;
            }
        });
    }
}