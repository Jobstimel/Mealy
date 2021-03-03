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
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
    public static List<Integer> mDislikedIDs;
    public static List<Integer> mLikedIDs;
    public static List<Integer> mStackIDs;
    public static List<Object> mResolvers;

    //LinearLayouts
    private LinearLayout mLinearLayoutPreparationsSpinner;
    private LinearLayout mLinearLayoutPlaceholderResults1;
    private LinearLayout mLinearLayoutPlaceholderResults2;
    private LinearLayout mLinearLayoutCategoriesSpinner;
    private LinearLayout mLinearLayoutAllergiesSpinner;
    private LinearLayout mLinearLayoutEatingSpinner;
    private LinearLayout mLinearLayoutResultTable;
    private LinearLayout mLinearLayoutBreakfast;
    private LinearLayout mLinearLayoutDessert;
    private LinearLayout mLinearLayoutGermany;
    private LinearLayout mLinearLayoutLevel1;
    private LinearLayout mLinearLayoutLevel2;
    private LinearLayout mLinearLayoutLevel3;
    private LinearLayout mLinearLayoutDinner;
    private LinearLayout mLinearLayoutFrance;
    private LinearLayout mLinearLayoutGreece;
    private LinearLayout mLinearLayoutLunch;
    private LinearLayout mLinearLayoutSnack;
    private LinearLayout mLinearLayoutDrink;
    private LinearLayout mLinearLayoutSpain;
    private LinearLayout mLinearLayoutItaly;
    private LinearLayout mLinearLayoutIndia;
    private LinearLayout mLinearLayoutAsia;
    private LinearLayout mLoadScreen;
    private LinearLayout mTutorial;
    private LinearLayout mPage1;
    private LinearLayout mPage2;
    private LinearLayout mPage3;

    //Views
    private BottomNavigationView mBottomNavigationView;
    private SwipePlaceHolderView mSwipePlaceHolderView;
    private TextView mTextViewCreateGroupButton;
    private TextView mTextViewCloseGroupButton;
    private TextView mTextViewResultPageHeader;
    private TextView mTextViewCompleteCounter;
    private TextView mTextViewRecipeCount;
    private TextView mTextViewGroupCode2;
    private TextView mTextViewGroupCode3;
    private TextView mTextViewCalories;
    private TextView mTextViewTime;
    private ListView mListView;

    //Classes
    private SwipePlaceHolderViewHandlerCreateGroup mSwipePlaceHolderViewHandlerCreateGroup;
    private FilterLinearLayoutCountryHandler mFilterLinearLayoutCountryHandler;
    private FilterLinearLayoutHandler mFilterLinearLayoutHandler;
    private FilterSpinnerHandler mFilterSpinnerHandler;
    private FilterSeekBarHandler mFilterSeekBarHandler;
    private DatabaseHandler mDatabaseHandler;
    private FilterApplier mFilterApplier;
    private SwipeHandler mSwipeHandler;
    private PageHandler mPageHandler;

    //Lists
    private List<LinearLayout> mLinearLayoutCountryList;
    private List<LinearLayout> mSpinnerLayoutList;
    private List<LinearLayout> mLinearLayoutList;
    private List<PowerSpinnerView> mSpinnerList;
    private List<Recipe> mAllRecipesList;

    //Spinners
    private PowerSpinnerView mPowerSpinnerPreparation;
    private PowerSpinnerView mPowerSpinnerCategories;
    private PowerSpinnerView mPowerSpinnerAllergies;
    private PowerSpinnerView mPowerSpinnerEating;

    //SeekBars
    private CrystalRangeSeekbar mSeekBarCalories;
    private CrystalRangeSeekbar mSeekBarTime;

    //Database
    private DatabaseReference mDatabaseReference;
    private DataSnapshot mDataSnapshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        mSharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        mContext = getApplicationContext();

        setupLinearLayouts();
        setupClasses();
        setupSpinners();
        setupViews();
        setupSeekBars();
        setupDatabase();
        generateGroupCode();
        setupLists();
        setupBottomNavigationBar();

        setupVisibilities();
        setupListeners();
        loadCorrectPage();
        loadFilter();
        mFilterApplier.applyFilter(mTextViewRecipeCount, mTextViewCreateGroupButton);
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
                mLinearLayoutResultTable.setVisibility(View.VISIBLE);
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
        mSwipeHandler.loadOnlineResults(mDataSnapshot, mAllRecipesList, "GroupCode");
        RecipeListAdapter adapter = new RecipeListAdapter(this, R.layout.list_view_apdapter_layout, mSwipeHandler.mOnlineResults);
        mListView.setAdapter(adapter);
    }

    public void closeVoting() {
        mDatabaseReference.child(mSharedPreferences.getString("GroupCode", "")).child("group_status").setValue("closed");
    }

    public void deleteGroup() {
        mPageHandler.showLoadScreen();
        mDatabaseHandler.deleteGroup(mDataSnapshot, mDatabaseReference, "GroupCode");
        deleteSavedOnlineData();
        resetSavedFilterData();
        restartActivity();
    }

    public void resetSavedFilterData() {
        mFilterLinearLayoutCountryHandler.resetCountryLayoutsSharedPreferences();
        mFilterLinearLayoutHandler.resetLayoutSharedPreferences();
        mFilterSeekBarHandler.resetSeekBarSharedPreferences();
        mFilterSpinnerHandler.resetSpinnerSharedPreferences();
        resetLikeDislikeList();
    }

    public void resetFilter(View v) {
        mFilterSpinnerHandler.resetSpinnerStates(mSpinnerList, mFilterApplier, mTextViewRecipeCount, mSpinnerLayoutList, mTextViewCreateGroupButton);
        mFilterLinearLayoutCountryHandler.resetCountryLayouts(mLinearLayoutCountryList);
        mFilterSeekBarHandler.resetSeekBarStates(mSeekBarCalories, mSeekBarTime);
        mFilterLinearLayoutHandler.resetLayouts(mLinearLayoutList);
        resetLikeDislikeList();
        makeToast("Filter wurde zurückgesetzt");
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

    private void setupLinearLayouts() {
        mLinearLayoutPlaceholderResults1 = findViewById(R.id.linear_layout_result_page_placeholder1);
        mLinearLayoutPlaceholderResults2 = findViewById(R.id.linear_layout_result_page_placeholder2);
        mLinearLayoutPreparationsSpinner = findViewById(R.id.preparation_linear_layout);
        mLinearLayoutAllergiesSpinner = findViewById(R.id.allergies_linear_layout);
        mLinearLayoutCategoriesSpinner = findViewById(R.id.category_linear_layout);
        mLinearLayoutResultTable = findViewById(R.id.linear_layout_result_table);
        mLinearLayoutEatingSpinner = findViewById(R.id.eating_linear_layout);
        mLinearLayoutBreakfast = findViewById(R.id.breakfast_linear_layout);
        mLinearLayoutDessert = findViewById(R.id.dessert_linear_layout);
        mLinearLayoutGermany = findViewById(R.id.germany_linear_layout);
        mLinearLayoutDinner = findViewById(R.id.dinner_linear_layout);
        mLinearLayoutLevel1 = findViewById(R.id.diff1_linear_layout);
        mLinearLayoutLevel2 = findViewById(R.id.diff2_linear_layout);
        mLinearLayoutLevel3 = findViewById(R.id.diff3_linear_layout);
        mLinearLayoutFrance = findViewById(R.id.france_linear_layout);
        mLinearLayoutGreece = findViewById(R.id.greece_linear_layout);
        mLinearLayoutLunch = findViewById(R.id.lunch_linear_layout);
        mLinearLayoutSnack = findViewById(R.id.snack_linear_layout);
        mLinearLayoutDrink = findViewById(R.id.drink_linear_layout);
        mLinearLayoutSpain = findViewById(R.id.spain_linear_layout);
        mLinearLayoutItaly = findViewById(R.id.italy_linear_layout);
        mLinearLayoutIndia = findViewById(R.id.india_linear_layout);
        mLinearLayoutAsia = findViewById(R.id.asia_linear_layout);
        mLoadScreen = findViewById(R.id.load_screen);
        mTutorial = findViewById(R.id.tutorial);
        mPage1 = findViewById(R.id.page_1);
        mPage2 = findViewById(R.id.page_2);
        mPage3 = findViewById(R.id.page_3);
    }

    private void setupClasses() {
        mAllRecipesList = JsonLoader.loadRecipies(mContext);
        mFilterLinearLayoutCountryHandler = new FilterLinearLayoutCountryHandler("Online", mSharedPreferences, mContext);
        mPageHandler = new PageHandler(mPage1, mPage2, mPage3, mTutorial, mLoadScreen, mSharedPreferences, "Create");
        mFilterLinearLayoutHandler = new FilterLinearLayoutHandler("Online", mSharedPreferences, mContext);
        mFilterApplier = new FilterApplier("Online", mSharedPreferences, mAllRecipesList, mContext);
        mFilterSeekBarHandler = new FilterSeekBarHandler("Online", mSharedPreferences, mContext);
        mFilterSpinnerHandler = new FilterSpinnerHandler("Online", mSharedPreferences, mContext);
        mSwipePlaceHolderViewHandlerCreateGroup = new SwipePlaceHolderViewHandlerCreateGroup(mContext);
        mSwipeHandler = new SwipeHandler("Online", mSharedPreferences);
        mDatabaseHandler = new DatabaseHandler(mSharedPreferences);

        mSwipeHandler.loadDislikedIndices();
        mSwipeHandler.loadLikedIndices();
    }

    private void setupLists() {
        mLinearLayoutList = Arrays.asList(mLinearLayoutLevel1,mLinearLayoutLevel2,mLinearLayoutLevel3,mLinearLayoutBreakfast,mLinearLayoutLunch,mLinearLayoutDinner,mLinearLayoutDessert,mLinearLayoutSnack,mLinearLayoutDrink);
        mLinearLayoutCountryList = Arrays.asList(mLinearLayoutGermany,mLinearLayoutSpain,mLinearLayoutAsia,mLinearLayoutItaly,mLinearLayoutFrance,mLinearLayoutGreece,mLinearLayoutIndia);
        mSpinnerLayoutList = Arrays.asList(mLinearLayoutAllergiesSpinner, mLinearLayoutPreparationsSpinner, mLinearLayoutCategoriesSpinner, mLinearLayoutEatingSpinner);
        mSpinnerList = Arrays.asList(mPowerSpinnerAllergies, mPowerSpinnerPreparation, mPowerSpinnerCategories, mPowerSpinnerEating);
        mDislikedIDs = mSwipeHandler.mDislikedIDs;
        mLikedIDs = mSwipeHandler.mLikedIDs;
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

    private void setupViews() {
        mTextViewCreateGroupButton = findViewById(R.id.text_view_create_group_button);
        mTextViewCompleteCounter = findViewById(R.id.text_view_complete_counter);
        mTextViewResultPageHeader = findViewById(R.id.text_view_result_page_top);
        mTextViewCloseGroupButton = findViewById(R.id.text_view_close_voting);
        mTextViewRecipeCount = findViewById(R.id.text_view_recipe_count);
        mTextViewGroupCode2 = findViewById(R.id.text_view_group_code_2);
        mTextViewGroupCode3 = findViewById(R.id.text_view_group_code_3);
        mListView = findViewById(R.id.list_view);
    }

    private void setupSeekBars() {
        mTextViewCalories = findViewById(R.id.calories_text_view);
        mSeekBarCalories = findViewById(R.id.calories_seek_bar);
        mTextViewTime = findViewById(R.id.time_text_view);
        mSeekBarTime = findViewById(R.id.time_seek_bar);

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
        mPowerSpinnerPreparation = findViewById(R.id.preparation_type_power_spinner);
        mPowerSpinnerAllergies = findViewById(R.id.allergies_power_spinner);
        mPowerSpinnerCategories = findViewById(R.id.category_power_spinner);
        mPowerSpinnerEating = findViewById(R.id.eating_type_power_spinner);

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

    private void setupVisibilities() {
        mLinearLayoutResultTable.setVisibility(View.GONE);
        mTutorial.setVisibility(View.GONE);
    }

    private void setupListeners() {
        mTutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTutorial.setVisibility(View.GONE);
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.putBoolean("CreateTutorial", true);
                editor.commit();
            }
        });

        mTextViewCloseGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeVoting();
            }
        });
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

    private void makeToast(String text) {
        Toast toast = Toast.makeText(mContext, text, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}