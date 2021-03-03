package com.example.mealy;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.mindorks.placeholderview.listeners.ItemRemovedListener;
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
    public static List<Integer> mStackIDs;
    public static List<Object> mResolvers;

    //Classes
    private SwipePlaceHolderViewHandlerCreateGroup mSwipePlaceHolderViewHandlerCreateGroup;
    private FilterSpinnerHandler mFilterSpinnerHandler;
    private FilterApplier mFilterApplier;
    private FilterLinearLayoutHandler mFilterLinearLayoutHandler;
    private FilterLinearLayoutCountryHandler mFilterLinearLayoutCountryHandler;
    private FilterSeekBarHandler mFilterSeekBarHandler;
    private SwipeHandler mSwipeHandler;
    private DatabaseHandler mDatabaseHandler;
    private PageHandler mPageHandler;

    //Result
    private ScrollView mScrollViewResults;
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
    private TextView mTextViewRecipeCount;
    private TextView mTextViewCreateGroupButton;
    private TextView mTextViewGroupCode2;
    private TextView mTextViewGroupCode3;
    private TextView mTextViewCompleteCounter;
    private TextView mTextViewCloseGroupButton;
    private TextView mTextViewResultPageHeader;

    //Pages
    private LinearLayout mPage1;
    private LinearLayout mPage2;
    private LinearLayout mPage3;
    private LinearLayout mLoadScreen;
    private LinearLayout mTutorial;

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

    private LinearLayout mLinearLayoutPlaceholderResults1;
    private LinearLayout mLinearLayoutPlaceholderResults2;

    //Database
    private DatabaseReference mDatabaseReference;
    private DataSnapshot mDataSnapshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

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

    private void uploadRatings() {
        String code = mSharedPreferences.getString("GroupCode", "");
        mDatabaseHandler.updateGroupCounter(mDataSnapshot, mLikedIDs, mDatabaseReference, code);
        mDatabaseHandler.updateGroupPeopleNumber(mDataSnapshot, mDatabaseReference, code);
        switchToPage3();
    }

    public void createGroup(View v) {
        mSwipeHandler.loadSelectedIndices();
        mDatabaseHandler.createGroup(mDatabaseReference, mSwipeHandler.mSelectedIDs, mSharedPreferences.getString("GroupCode", ""));
        switchToPage2();
    }

    private void checkIfGroupIsCompleted() {
        if (mDataSnapshot != null) {
            String code = mSharedPreferences.getString("GroupCode", "");
            String status = (String) mDataSnapshot.child(code).child("group_status").getValue();
            if (status != null && status.equals("closed")) {
                mLinearLayoutPlaceholderResults1.setVisibility(View.GONE);
                mLinearLayoutPlaceholderResults2.setVisibility(View.GONE);
                mScrollViewResults.setVisibility(View.VISIBLE);
                mTextViewResultPageHeader.setText("Teilnehmer: "+String.valueOf(mDataSnapshot.child(code).child("people_number").getValue()));
                mTextViewCloseGroupButton.setText("Gruppe löschen");
                mTextViewCloseGroupButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteGroup();
                    }
                });
                loadResults();
            }
        }
    }

    private void loadResults() {
        if (mAllRecipesList == null) {
            mAllRecipesList = JsonLoader.loadRecipies(mContext);
        }
        setupResultPage();
        mSwipeHandler.loadOnlineResults(mDataSnapshot, mAllRecipesList, "GroupCode");
        ResultLoader mResultLoader = new ResultLoader(mContext);
        mResultLoader.loadResults(mSwipeHandler.mOnlineWinners, mPosterList, mScoreList);
    }

    public void closeVoting() {
        mDatabaseReference.child(mSharedPreferences.getString("GroupCode", "")).child("group_status").setValue("closed");
    }

    public void deleteGroup() {
        mPageHandler.showLoadScreen();
        mDatabaseReference.child(mSharedPreferences.getString("GroupCode", "")).removeValue();
        resetSavedFilterData();
        deleteSavedOnlineData();
        restartActivity();
    }

    public void resetSavedFilterData() {
        mFilterLinearLayoutHandler.resetLayoutSharedPreferences();
        mFilterLinearLayoutCountryHandler.resetCountryLayoutsSharedPreferences();
        mFilterSeekBarHandler.resetSeekBarSharedPreferences();
        mFilterSpinnerHandler.resetSpinnerSharedPreferences();
        resetLikeDislikeList();
    }

    public void resetFilter(View v) {
        mFilterLinearLayoutHandler.resetLayouts(mLinearLayoutList);
        mFilterLinearLayoutCountryHandler.resetCountryLayouts(mLinearLayoutCountryList);
        mFilterSeekBarHandler.resetSeekBarStates(mSeekBarCalories, mSeekBarTime);
        mFilterSpinnerHandler.resetSpinnerStates(mSpinnerList, mFilterApplier, mTextViewRecipeCount, mSpinnerLayoutList, null);
        resetLikeDislikeList();
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

    public void switchToPage2() {
        mPageHandler.savePage(2);
        loadCorrectPage();
    }

    public void switchToPage3() {
        mPageHandler.savePage(3);
        loadCorrectPage();
    }

    private void loadCorrectPage() {
        mPageHandler.loadCorrectPage();
        if (mSharedPreferences.getInt("PageCreate", 1) == 2) {
            setupLists();
            setupSwipePlaceholderView();
        }
    }

    private void generateGroupCode() {
        if (mSharedPreferences.getString("GroupCode", "").equals("")) {
            RandomGenerator randomGenerator = new RandomGenerator(mSharedPreferences);
            randomGenerator.generateRandomGroupCode();
        }
        mTextViewGroupCode2.setText("Zugangscode: "+mSharedPreferences.getString("GroupCode", ""));
        mTextViewGroupCode3.setText("Zugangscode: "+mSharedPreferences.getString("GroupCode", ""));
    }

    private void setupElements() {
        mSharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        mContext = getApplicationContext();
        //resetSharedPreferences();

        setupPages();
        setupClasses();
        setupLists();
        setupViews();
        setupSeekBars();
        setupSpinners();
        setupLayouts();
        setupDatabase();
        generateGroupCode();
        setupBottomNavigationBar();
        loadCorrectPage();
        loadFilter();
        mFilterApplier.applyFilter(mTextViewRecipeCount, mTextViewCreateGroupButton);
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

    private void setupClasses() {
        mAllRecipesList = JsonLoader.loadRecipies(mContext);
        mFilterSpinnerHandler = new FilterSpinnerHandler("Online", mSharedPreferences, mContext);
        mFilterApplier = new FilterApplier("Online", mSharedPreferences, mAllRecipesList, mContext);
        mFilterLinearLayoutHandler = new FilterLinearLayoutHandler("Online", mSharedPreferences, mContext);
        mFilterLinearLayoutCountryHandler = new FilterLinearLayoutCountryHandler("Online", mSharedPreferences, mContext);
        mFilterSeekBarHandler = new FilterSeekBarHandler("Online", mSharedPreferences, mContext);
        mSwipeHandler = new SwipeHandler("Online", mSharedPreferences);
        mSwipePlaceHolderViewHandlerCreateGroup = new SwipePlaceHolderViewHandlerCreateGroup(mContext);
        mDatabaseHandler = new DatabaseHandler(mSharedPreferences);
        mPageHandler = new PageHandler(mPage1, mPage2, mPage3, mTutorial, mLoadScreen, mSharedPreferences, "Create");
    }

    private void setupSwipePlaceholderView() {
        mSwipeHandler.loadSelectedIndices();
        mSwipePlaceHolderView = findViewById(R.id.swipeView);
        mSwipePlaceHolderViewHandlerCreateGroup.setSwipePlaceHolderViewBuilder(mSwipePlaceHolderView);
        mSwipePlaceHolderViewHandlerCreateGroup.loadSwipePlaceholderView(mSwipeHandler.mSelectedIDs, mAllRecipesList, mSwipePlaceHolderView);
        mStackIDs = mSwipePlaceHolderViewHandlerCreateGroup.mStackIDs;
        mResolvers = mSwipePlaceHolderViewHandlerCreateGroup.mResolvers;
        mSwipePlaceHolderView.addItemRemoveListener(new ItemRemovedListener() {
            @Override
            public void onItemRemoved(int count) {
                if (mSwipePlaceHolderView.getAllResolvers().size() == 0) {
                    uploadRatings();
                }
            }
        });
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
                editor.putBoolean("CreateTutorial", true);
                editor.commit();
            }
        });
    }

    private void setupDatabase() {
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mDataSnapshot = dataSnapshot;
                checkIfGroupIsCompleted();
                mTextViewCompleteCounter.setText("Bisherige Stimmenanzahl: "+(String) dataSnapshot.child(mSharedPreferences.getString("GroupCode", "")).child("people_number").getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("TAG", "DATABASE ERROR: ", databaseError.toException());
            }
        });
    }

    private void setupLists() {
        mSwipeHandler.loadLikedIndices();
        mSwipeHandler.loadDislikedIndices();
        mLikedIDs = mSwipeHandler.mLikedIDs;
        mDislikedIDs = mSwipeHandler.mDislikedIDs;
    }

    private void setupViews() {
        mTextViewRecipeCount = findViewById(R.id.text_view_recipe_count);
        mTextViewCreateGroupButton = findViewById(R.id.text_view_create_group_button);
        mTextViewGroupCode2 = findViewById(R.id.text_view_group_code_2);
        mTextViewGroupCode3 = findViewById(R.id.text_view_group_code_3);
        mTextViewCompleteCounter = findViewById(R.id.text_view_complete_counter);
        mTextViewResultPageHeader = findViewById(R.id.text_view_result_page_top);
        mTextViewCloseGroupButton = findViewById(R.id.text_view_close_voting);
        mTextViewCloseGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeVoting();
            }
        });
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

        mLinearLayoutPlaceholderResults1 = findViewById(R.id.linear_layout_result_page_placeholder1);
        mLinearLayoutPlaceholderResults2 = findViewById(R.id.linear_layout_result_page_placeholder2);
        mScrollViewResults = findViewById(R.id.scroll_view);
        mScrollViewResults.setVisibility(View.GONE);

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
                        mPageHandler.showLoadScreen();
                        startActivity(new Intent(getApplicationContext(), ActivityPlayAlone.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.join_group:
                        mPageHandler.showLoadScreen();
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

    private void resetSharedPreferences() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.clear();
        editor.commit();
    }

    private void deleteSavedOnlineData() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt("PageCreate", 1);
        editor.putString("SelectedOnlineIDs", "");
        editor.putString("LikedCreateIDs", "");
        editor.putString("DislikedCreateIDs", "");
        editor.putBoolean("CreateTutorial", false);
        editor.commit();
    }

    private void restartActivity() {
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0,0);
    }
}