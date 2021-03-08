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
import android.text.Html;
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
    private static final String MODE = "Online";
    private static final String PLACEHOLDER_1_TEXT = "Du hast alle Rezepte bewertet und deine Daten wurden erfolgreich hochgeladen.\nDie Abstimmung läuft aktuell noch. Du kannst sie jederzeit durch einen Klick auf 'Abstimmung beenden' schließen.\nDu solltest das jedoch erst dann machen, wenn alle Teilnehmer ihre Stimme abgegeben haben.\nAnschließend werden die Ergebnisse hier veröffentlicht.";
    private SharedPreferences mSharedPreferences;
    private Context mContext;

    //Public
    public static List<Integer> mDislikedIDs;
    public static List<Integer> mLikedIDs;
    public static List<Integer> mStackIDs;
    public static List<Object> mResolvers;

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

    private LinearLayout mLinearLayoutAllergiesSpinner;
    private LinearLayout mLinearLayoutPreparationsSpinner;
    private LinearLayout mLinearLayoutCategoriesSpinner;
    private LinearLayout mLinearLayoutEatingSpinner;
    private List<LinearLayout> mSpinnerLayoutList;

    //Views
    private BottomNavigationView mBottomNavigationView;
    private SwipePlaceHolderView mSwipePlaceHolderView;
    private TextView mTextViewCreateGroupButton;
    private TextView mResultPagePlaceholder1;
    private TextView mResultPagePlaceholder2;
    private TextView mTextViewResultPageHead;
    private TextView mTextViewSwipePageHead;
    private TextView mResultPageButton;
    private ListView mListView;

    //Classes
    private SwipePlaceHolderViewHandlerCreateGroup mSwipePlaceHolderViewHandlerCreateGroup;
    private FilterImageViewHandler mFilterImageViewHandler;
    private FilterTextViewHandler mFilterTextViewHandler;
    private FilterSpinnerHandler mFilterSpinnerHandler;
    private FilterSeekBarHandler mFilterSeekBarHandler;
    private DatabaseHandler mDatabaseHandler;
    private FilterApplier mFilterApplier;
    private SwipeHandler mSwipeHandler;
    private PageHandler mPageHandler;

    //Pages
    private LinearLayout mLoadScreen;
    private LinearLayout mTutorial;
    private LinearLayout mPage1;
    private LinearLayout mPage2;
    private LinearLayout mPage3;

    //Database
    private DatabaseReference mDatabaseReference;
    private DataSnapshot mDataSnapshot;

    //Lists
    private List<Recipe> mAllRecipesList;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        setupEverything();
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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void uploadRatings() {
        String code = mSharedPreferences.getString("GroupCode", "");
        mDatabaseHandler.updateGroupCounter(mDataSnapshot, mLikedIDs, mDatabaseReference, code);
        mDatabaseHandler.updateGroupPeopleNumber(mDataSnapshot, mDatabaseReference, code);
        switchToPage3();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void createGroup() {
        mSwipeHandler.loadSelectedIndices();
        mDatabaseHandler.createGroup(mDatabaseReference, mSwipeHandler.mSelectedIDs, mSharedPreferences.getString("GroupCode", ""));
        switchToPage2();
    }

    private void checkIfGroupIsCompleted() {
        if (mDataSnapshot != null) {
            String code = mSharedPreferences.getString("GroupCode", "");
            String status = (String) mDataSnapshot.child(code).child("group_status").getValue();
            String counter = (String) mDataSnapshot.child(code).child("people_number").getValue();
            mResultPagePlaceholder1.setText(PLACEHOLDER_1_TEXT);
            mResultPagePlaceholder2.setText("Bisherige Stimmenzahl: "+counter);
            if (status != null && status.equals("closed")) {
                mTextViewResultPageHead.setText("Teilnehmer: "+(String) mDataSnapshot.child(code).child("people_number").getValue());
                mResultPageButton.setText("Gruppe löschen");
                mResultPageButton.setOnClickListener(new View.OnClickListener() {
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
        mListView.setVisibility(View.VISIBLE);
    }

    public void closeVoting() {
        mResultPagePlaceholder1.setText("Ergebnisse werden abgerufen...");
        mResultPagePlaceholder2.setVisibility(View.GONE);
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
        mFilterTextViewHandler.resetTextViewSharedPreferences();
        mFilterImageViewHandler.resetImageViewSharedPreferences();
        mFilterSeekBarHandler.resetSeekBarSharedPreferences();
        mFilterSpinnerHandler.resetSpinnerSharedPreferences();
        resetLikeDislikeList();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void resetFilter(View v) {
        mFilterTextViewHandler.resetTextViewStates(mTextViewList);
        mFilterImageViewHandler.resetImageViewStates(mImageViewList);
        mFilterSeekBarHandler.resetSeekBarStates(mSeekBarCalories, mSeekBarTime);
        mFilterSpinnerHandler.resetSpinnerStates(mSpinnerList, mFilterApplier, mTextViewRecipeHitCounter, mSpinnerLayoutList, mTextViewCreateGroupButton);
        resetLikeDislikeList();
        mFilterApplier.applyFilter(mTextViewRecipeHitCounter, mTextViewCreateGroupButton);
        makeToast("Filter wurde zurückgesetzt");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void saveTextViewState(View v)  {
        mFilterTextViewHandler.saveTextViewState(v, mFilterApplier, mTextViewRecipeHitCounter, mTextViewCreateGroupButton);
        resetLikeDislikeList();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void saveImageViewState(View v) {
        mFilterImageViewHandler.saveImageViewState(v, mFilterApplier, mTextViewRecipeHitCounter, mTextViewCreateGroupButton);
        resetLikeDislikeList();
    }

    private void resetLikeDislikeList() {
        mLikedIDs = new ArrayList<>();
        mDislikedIDs = new ArrayList<>();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void resetSpinnerAllergies(View v) {
        mFilterSpinnerHandler.resetSpinnerState(findViewById(R.id.allergies_power_spinner), "AllergiesSpinner", mFilterApplier, mTextViewRecipeHitCounter, mLinearLayoutAllergiesSpinner, mTextViewCreateGroupButton);
        resetLikeDislikeList();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void resetSpinnerPreparationType(View v) {
        mFilterSpinnerHandler.resetSpinnerState(findViewById(R.id.preparation_type_power_spinner), "PreparationTypeSpinner", mFilterApplier, mTextViewRecipeHitCounter, mLinearLayoutPreparationsSpinner, mTextViewCreateGroupButton);
        resetLikeDislikeList();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void resetSpinnerCategory(View v) {
        mFilterSpinnerHandler.resetSpinnerState(findViewById(R.id.category_power_spinner), "CategorySpinner", mFilterApplier, mTextViewRecipeHitCounter, mLinearLayoutCategoriesSpinner, mTextViewCreateGroupButton);
        resetLikeDislikeList();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void resetSpinnerEatingType(View v) {
        mFilterSpinnerHandler.resetSpinnerState(findViewById(R.id.eating_type_power_spinner), "EatingTypeSpinner", mFilterApplier, mTextViewRecipeHitCounter, mLinearLayoutEatingSpinner, mTextViewCreateGroupButton);
        resetLikeDislikeList();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void loadFilter() {
        mFilterTextViewHandler.loadTextViewStates(mTextViewList);
        mFilterImageViewHandler.loadImageViewStates(mImageViewList);
        mFilterSeekBarHandler.loadSeekBarStates(mSeekBarCalories, mSeekBarTime);
        mFilterSpinnerHandler.loadSpinnerStates(mPowerSpinnerAllergies, mPowerSpinnerPreparation, mPowerSpinnerCategories, mPowerSpinnerEating);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void switchToPage2() {
        mPageHandler.savePage(2);
        loadCorrectPage();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void switchToPage3() {
        mPageHandler.savePage(3);
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
            mFilterApplier.applyFilter(mTextViewRecipeHitCounter, mTextViewCreateGroupButton);
        }
        else if (currentPage == 2) {
            mSwipePlaceHolderViewHandlerCreateGroup = new SwipePlaceHolderViewHandlerCreateGroup(mContext);
            setupSwipePlaceholderView();
        }
    }

    private void generateGroupCode() {
        if (mSharedPreferences.getString("GroupCode", "").equals("")) {
            RandomGenerator randomGenerator = new RandomGenerator(mSharedPreferences);
            randomGenerator.generateRandomGroupCode();
        }
        mTextViewResultPageHead.setText(mSharedPreferences.getString("GroupCode", ""));
        mTextViewSwipePageHead.setText(mSharedPreferences.getString("GroupCode", ""));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setupEverything() {
        mSharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        mContext = getApplicationContext();
        //resetSharedPreferences();

        setupPages();
        setupClasses();
        setupViews();
        setupSeekBars();
        setupSpinners();
        setupDatabase();
        setupBottomNavigationBar();
        generateGroupCode();
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

        mDatabaseHandler = new DatabaseHandler(mSharedPreferences);

        mSwipeHandler = new SwipeHandler(mSharedPreferences, MODE);
        mSwipeHandler.loadDislikedIndices();
        mSwipeHandler.loadLikedIndices();
        mDislikedIDs = mSwipeHandler.mDislikedIDs;
        mLikedIDs = mSwipeHandler.mLikedIDs;
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

        mTextViewCreateGroupButton = findViewById(R.id.text_view_create_group_button);
        mTextViewCreateGroupButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                createGroup();
            }
        });

        mTextViewSwipePageHead = findViewById(R.id.swipe_page_head);
        mTextViewResultPageHead = findViewById(R.id.result_page_head);

        mListView = findViewById(R.id.list_view);
        mListView.setVisibility(View.GONE);

        mResultPagePlaceholder1 = findViewById(R.id.result_page_placeholder_1);
        mResultPagePlaceholder2 = findViewById(R.id.result_page_placeholder_2);

        mResultPageButton = findViewById(R.id.result_page_button);
        mResultPageButton.setText("Abstimmung beenden");
        mResultPageButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                closeVoting();
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
            mFilterSeekBarHandler.saveCaloriesSeekBarState(mSeekBarCalories, minValue.intValue(), maxValue.intValue(), mFilterApplier, mTextViewRecipeHitCounter, mTextViewCreateGroupButton);
            resetLikeDislikeList();
        });

        mSeekBarTime.setOnRangeSeekbarChangeListener((minValue, maxValue) -> mTextViewTime.setText(minValue+" - "+maxValue+" min"));

        mSeekBarTime.setOnRangeSeekbarFinalValueListener((minValue, maxValue) -> {
            mFilterSeekBarHandler.saveTimeSeekBarState(mSeekBarTime, minValue.intValue(), maxValue.intValue(), mFilterApplier, mTextViewRecipeHitCounter, mTextViewCreateGroupButton);
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

        mLinearLayoutAllergiesSpinner = findViewById(R.id.allergies_linear_layout);
        mLinearLayoutPreparationsSpinner = findViewById(R.id.preparation_linear_layout);
        mLinearLayoutCategoriesSpinner = findViewById(R.id.category_linear_layout);
        mLinearLayoutEatingSpinner = findViewById(R.id.eating_linear_layout);
        mSpinnerLayoutList = Arrays.asList(mLinearLayoutAllergiesSpinner,mLinearLayoutPreparationsSpinner,mLinearLayoutCategoriesSpinner,mLinearLayoutEatingSpinner);

        mPowerSpinnerEating.setOnSpinnerItemSelectedListener((OnSpinnerItemSelectedListener<String>) (oldIndex, oldItem, newIndex, newItem) -> {
            mFilterSpinnerHandler.saveSpinnerState("EatingTypeSpinner", newItem, newIndex, mLinearLayoutEatingSpinner);
            mFilterApplier.applyFilter(mTextViewRecipeHitCounter, mTextViewCreateGroupButton);
            resetLikeDislikeList();
        });

        mPowerSpinnerCategories.setOnSpinnerItemSelectedListener((OnSpinnerItemSelectedListener<String>) (oldIndex, oldItem, newIndex, newItem) -> {
            mFilterSpinnerHandler.saveSpinnerState("CategorySpinner", newItem, newIndex, mLinearLayoutCategoriesSpinner);
            mFilterApplier.applyFilter(mTextViewRecipeHitCounter, mTextViewCreateGroupButton);
            resetLikeDislikeList();
        });

        mPowerSpinnerAllergies.setOnSpinnerItemSelectedListener((OnSpinnerItemSelectedListener<String>) (oldIndex, oldItem, newIndex, newItem) -> {
            mFilterSpinnerHandler.saveSpinnerState("AllergiesSpinner", newItem, newIndex, mLinearLayoutAllergiesSpinner);
            mFilterApplier.applyFilter(mTextViewRecipeHitCounter, mTextViewCreateGroupButton);
            resetLikeDislikeList();
        });

        mPowerSpinnerPreparation.setOnSpinnerItemSelectedListener((OnSpinnerItemSelectedListener<String>) (oldIndex, oldItem, newIndex, newItem) -> {
            mFilterSpinnerHandler.saveSpinnerState("PreparationTypeSpinner", newItem, newIndex, mLinearLayoutPreparationsSpinner);
            mFilterApplier.applyFilter(mTextViewRecipeHitCounter, mTextViewCreateGroupButton);
            resetLikeDislikeList();
        });
    }

    private void setupDatabase() {
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mDataSnapshot = dataSnapshot;
                checkIfGroupIsCompleted();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("TAG", "DATABASE ERROR: ", databaseError.toException());
            }
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

    private void setupSwipePlaceholderView() {
        mSwipeHandler.loadSelectedIndices();
        mSwipePlaceHolderView = findViewById(R.id.swipeView);
        mSwipePlaceHolderViewHandlerCreateGroup.setSwipePlaceHolderViewBuilder(mSwipePlaceHolderView);
        mSwipePlaceHolderViewHandlerCreateGroup.loadSwipePlaceholderView(mSwipeHandler.mSelectedIDs, mAllRecipesList, mSwipePlaceHolderView);
        mStackIDs = mSwipePlaceHolderViewHandlerCreateGroup.mStackIDs;
        mResolvers = mSwipePlaceHolderViewHandlerCreateGroup.mResolvers;
        mSwipePlaceHolderView.addItemRemoveListener(new ItemRemovedListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onItemRemoved(int count) {
                if (mSwipePlaceHolderView.getAllResolvers().size() == 0) {
                    uploadRatings();
                }
            }
        });
    }

    private void deleteSavedOnlineData() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt("Page"+MODE, 1);
        editor.putString("Selected"+MODE+"IDs", "");
        editor.putString("Liked"+MODE+"IDs", "");
        editor.putString("Disliked"+MODE+"IDs", "");
        editor.putBoolean(MODE+"Tutorial", false);
        editor.commit();
    }

    private void restartActivity() {
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0,0);
    }

    private void makeToast(String text) {
        Toast toast = Toast.makeText(mContext, text, Toast.LENGTH_LONG);
        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
        v.setGravity(Gravity.CENTER);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    private void resetSharedPreferences() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.clear();
        editor.commit();
    }
}