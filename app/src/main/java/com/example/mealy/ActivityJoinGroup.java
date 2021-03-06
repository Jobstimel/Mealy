package com.example.mealy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.mindorks.placeholderview.listeners.ItemRemovedListener;
import com.raycoarana.codeinputview.CodeInputView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ActivityJoinGroup extends AppCompatActivity {

    //General
    private static final String TAG = "JOIN_LOBBY_ACTIVITY";
    private static final String MODE = "Join";
    private SharedPreferences mSharedPreferences;
    private Context mContext;

    //Public
    public static List<Integer> mDislikedIDs;
    public static List<Integer> mStackIDs;
    public static List<Object> mResolvers;
    public static List<Integer> mLikedIDs;

    //Views
    private BottomNavigationView mBottomNavigationView;
    private SwipePlaceHolderView mSwipePlaceHolderView;
    private TextView mTextViewResultPlaceholder;
    private TextView mTextViewLeaveGroupButton;
    private TextView mTextViewResultPageHeader;
    private TextView mTextViewCodeInputStatus;
    private TextView mTextViewJoinGroupButton;
    private CodeInputView mCodeInputView;

    //LinearLayouts
    private LinearLayout mLoadScreen;
    private LinearLayout mTutorial;
    private LinearLayout mPage1;
    private LinearLayout mPage2;
    private LinearLayout mPage3;

    //Classes
    private SwipePlaceHolderViewHandlerJoinGroup mSwipePlaceHolderViewHandlerJoinGroup;
    private CodeInputHandler mCodeInputHandler;
    private SwipeHandler mSwipeHandler;
    private PageHandler mPageHandler;

    //Database
    private DatabaseReference mDatabaseReference;
    private DataSnapshot mDataSnapshot;

    //Lists
    private List<Recipe> mAllRecipesList;

    //ListView
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_group);

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

    private void generateUserID() {
        if (mSharedPreferences.getString("UserID", "").equals("")) {
            RandomGenerator randomStringBuilder = new RandomGenerator(mSharedPreferences);
            randomStringBuilder.generateRandomUserID(30);
        }
    }

    private void uploadRatings() {
        String code = mSharedPreferences.getString("JoinGroupCode", "");
        DatabaseHandler mDatabaseHandler = new DatabaseHandler(mSharedPreferences);
        mDatabaseHandler.updateGroupCounter(mDataSnapshot, mLikedIDs, mDatabaseReference, code);
        mDatabaseHandler.updateGroupCompletedUserList(mDataSnapshot, mDatabaseReference, code);
        mDatabaseHandler.updateGroupPeopleNumber(mDataSnapshot, mDatabaseReference, code);
        switchToPage3();
    }

    private void checkUserInputCode(String code) {
        mCodeInputHandler.checkStatus(code, mDataSnapshot, mTextViewCodeInputStatus, mCodeInputView, mTextViewJoinGroupButton);
    }

    private void checkIfGroupIsCompleted() {
        if (mDataSnapshot != null) {
            String code = mSharedPreferences.getString("JoinGroupCode", "");
            String status = (String) mDataSnapshot.child(code).child("group_status").getValue();
            if (status != null && status.equals("closed")) {
                mListView.setVisibility(View.VISIBLE);
                mTextViewResultPageHeader.setText("Teilnehmer: "+String.valueOf(mDataSnapshot.child(code).child("people_number").getValue()));
                loadResults();
            }
        }
    }

    private void loadResults() {
        if (mAllRecipesList == null) {
            mAllRecipesList = JsonLoader.loadRecipies(mContext);
        }
        mTextViewResultPlaceholder.setVisibility(View.GONE);
        mSwipeHandler.loadOnlineResults(mDataSnapshot, mAllRecipesList, MODE+"GroupCode");
        RecipeListAdapter adapter = new RecipeListAdapter(this, R.layout.list_view_apdapter_layout, mSwipeHandler.mOnlineResults);
        mListView.setAdapter(adapter);
        mTextViewLeaveGroupButton.setText("Gruppe verlassen");
        mTextViewLeaveGroupButton.setClickable(true);
    }

    public void leaveGroup() {
        deleteSavedOnlineData();
        restartActivity();
    }

    public void switchToPage2(View v) {
        mPageHandler.savePage(2);
        loadCorrectPage();
    }

    private void switchToPage3() {
        mPageHandler.savePage(3);
        loadCorrectPage();
    }

    private void loadCorrectPage() {
        mPageHandler.loadCorrectPage();
        Integer currentPage = mSharedPreferences.getInt("PageJoin", 1);
        if (currentPage == 2) {
            setupLists();
            setupSwipePlaceholderView();
        }
        else if (currentPage == 3) {
            checkIfGroupIsCompleted();
        }
    }

    private void setupElements() {
        mSharedPreferences = getSharedPreferences("MealyData", Context.MODE_PRIVATE);
        mContext = getApplicationContext();
        //resetSharedPreferences();

        setupPages();
        setupClasses();
        setupViews();
        setupDatabase();
        setupBottomNavigationBar();
        generateUserID();
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
                editor.putBoolean("JoinTutorial", true);
                editor.commit();
            }
        });
    }

    private void setupClasses() {
        mCodeInputHandler = new CodeInputHandler(mContext, mSharedPreferences);
        mSwipePlaceHolderViewHandlerJoinGroup = new SwipePlaceHolderViewHandlerJoinGroup(mContext);
        mSwipeHandler = new SwipeHandler(mSharedPreferences, MODE);
        mPageHandler = new PageHandler(mPage1, mPage2, mPage3, mTutorial, mLoadScreen, mSharedPreferences, MODE);
    }

    private void setupSwipePlaceholderView() {
        mCodeInputHandler.loadSelectedIDs();
        mSwipePlaceHolderView = findViewById(R.id.swipeView);
        mSwipePlaceHolderViewHandlerJoinGroup.setSwipePlaceHolderViewBuilder(mSwipePlaceHolderView);
        mSwipePlaceHolderViewHandlerJoinGroup.loadSwipePlaceholderView(mCodeInputHandler.mSelectedIDs, mAllRecipesList, mSwipePlaceHolderView);
        mStackIDs = mSwipePlaceHolderViewHandlerJoinGroup.mStackIDs;
        mResolvers = mSwipePlaceHolderViewHandlerJoinGroup.mResolvers;
        mSwipePlaceHolderView.addItemRemoveListener(new ItemRemovedListener() {
            @Override
            public void onItemRemoved(int count) {
                if (mSwipePlaceHolderView.getAllResolvers().size() == 0) {
                    uploadRatings();
                }
            }
        });
    }

    private void setupLists() {
        mSwipeHandler.loadLikedIndices();
        mSwipeHandler.loadDislikedIndices();
        mAllRecipesList = JsonLoader.loadRecipies(mContext);
        mLikedIDs = mSwipeHandler.mLikedIDs;
        mDislikedIDs = mSwipeHandler.mDislikedIDs;
    }

    private void setupViews() {
        mListView = findViewById(R.id.list_view);
        mListView.setVisibility(View.GONE);
        mBottomNavigationView = findViewById(R.id.bottom_navigation);
        mBottomNavigationView.setSelectedItemId(R.id.join_group);

        mTextViewCodeInputStatus = findViewById(R.id.text_view_code_status);
        mTextViewJoinGroupButton = findViewById(R.id.text_view_join_group);
        mTextViewJoinGroupButton.setClickable(false);

        mTextViewLeaveGroupButton = findViewById(R.id.result_page_button);
        mTextViewLeaveGroupButton.setText("Gruppe verlassen");
        mTextViewLeaveGroupButton.setClickable(false);
        mTextViewLeaveGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leaveGroup();
            }
        });

        mTextViewResultPageHeader = findViewById(R.id.result_page_head);
        mTextViewResultPageHeader.setText(mSharedPreferences.getString(MODE+"GroupCode", ""));

        mCodeInputView = findViewById(R.id.code_input_view);
        mCodeInputView.addOnCompleteListener(code -> checkUserInputCode(code));

        mTextViewResultPlaceholder = findViewById(R.id.result_page_placeholder_1);
        mTextViewResultPlaceholder.setText("Die Abstimmung läuft noch, sobald der Host die Abstimmung beendet hat werden die Ergebnisse hier angezeigt.");
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
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId()) {
                    case R.id.play_alone:
                        mPageHandler.showLoadScreen();
                        overridePendingTransition(0,0);
                        startActivity(new Intent(getApplicationContext(), ActivityPlayAlone.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.join_group:
                        return true;
                    case R.id.create_group:
                        mPageHandler.showLoadScreen();
                        overridePendingTransition(0,0);
                        startActivity(new Intent(getApplicationContext(), ActivityCreateGroup.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });
    }

    private void restartActivity() {
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0,0);
    }

    private void resetSharedPreferences() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.clear();
        editor.commit();
    }

    private void deleteSavedOnlineData() {
        mLikedIDs = new ArrayList<>();
        mDislikedIDs = new ArrayList<>();
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt("PageJoin", 1);
        editor.putString("JoinGroupIDs", "");
        editor.putString("JoinGroupCode", "");
        editor.putString("LikedJoinIDs", "");
        editor.putString("DislikedJoinIDs", "");
        editor.putBoolean("JoinTutorial", false);
        editor.commit();
    }
}