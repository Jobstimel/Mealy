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
import android.widget.LinearLayout;
import android.widget.ListView;
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
import java.util.List;

public class ActivityJoinGroup extends AppCompatActivity {

    //General
    private static final String TAG = "JOIN_LOBBY_ACTIVITY";
    private SharedPreferences mSharedPreferences;
    private Context mContext;

    //Public
    public static List<Integer> mStackIDs;
    public static List<Integer> mLikedIDs;
    public static List<Integer> mDislikedIDs;
    public static List<Object> mResolvers;

    //Classes
    private JoinGroupCodeInputStatusHandler mJoinGroupCodeInputStatusHandler;
    private SwipePlaceHolderViewHandlerJoinGroup mSwipePlaceHolderViewHandlerJoinGroup;
    private SwipeHandler mSwipeHandler;

    //Pages
    private LinearLayout mPage1;
    private LinearLayout mPage2;
    private LinearLayout mPage3;

    //Lists
    private List<Recipe> mAllRecipesList;

    //Views
    private BottomNavigationView mBottomNavigationView;
    private SwipePlaceHolderView mSwipePlaceHolderView;
    private CodeInputView mCodeInputView;
    private TextView mTextViewCodeInputStatus;
    private TextView mTextViewJoinGroupButton;
    private TextView mTextViewLeaveGroupButton;
    private ListView mResultListView;

    //Database
    private DatabaseReference mDatabaseReference;
    private DataSnapshot mDataSnapshot;

    //Layouts
    private LinearLayout mLinearLayoutPlaceholderResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_group);

        setupElements();
    }

    protected void onPause() {
        mSwipeHandler.saveLikedIndices(mLikedIDs);
        mSwipeHandler.saveDislikedIndices(mDislikedIDs);
        super.onPause();
    }

    private void generateUserID() {
        if (mSharedPreferences.getString("UserID", "").equals("")) {
            UserIDGenerator randomStringBuilder = new UserIDGenerator(mSharedPreferences);
            randomStringBuilder.generateRandomID(30);
        }
    }

    private void uploadRatings() {
        DatabaseHandlerJoinGroup  mDatabaseHandlerJoinGroup = new DatabaseHandlerJoinGroup(mSharedPreferences);
        mDatabaseHandlerJoinGroup.updateGroupCounter(mDataSnapshot, mLikedIDs, mDatabaseReference);
        mDatabaseHandlerJoinGroup.updateGroupCompletedUserList(mDataSnapshot, mDatabaseReference);
        mDatabaseHandlerJoinGroup.updateGroupPeopleNumber(mDataSnapshot, mDatabaseReference);
        switchToPage3();
    }

    private void checkUserInputCode(String code) {
        mJoinGroupCodeInputStatusHandler.checkStatus(code, mDataSnapshot, mTextViewCodeInputStatus, mCodeInputView, mTextViewJoinGroupButton);
    }

    private void checkIfGroupIsCompleted() {
        String code = mSharedPreferences.getString("JoinGroupCode", "");
        String status = (String) mDataSnapshot.child(code).child("group_status").getValue();
        if (status != null && status.equals("closed")) {
            mLinearLayoutPlaceholderResults.setVisibility(View.GONE);
            loadResults();
        }
    }

    private void loadResults() {
        if (mAllRecipesList == null) {
            mAllRecipesList = Loader.loadRecipies(mContext);
        }
        mSwipeHandler.loadOnlineResults(mDataSnapshot, mAllRecipesList);
        mLinearLayoutPlaceholderResults.setVisibility(View.GONE);
        ListViewAdapter adapter = new ListViewAdapter(this, R.layout.list_view_apdapter_layout, mSwipeHandler.mOnlineResults, "Online");
        mResultListView.setAdapter(adapter);
        mTextViewLeaveGroupButton.setText("Gruppe verlassen");
        mTextViewLeaveGroupButton.setClickable(true);
    }

    public void leaveGroup(View v) {
        savePage(1);
        mJoinGroupCodeInputStatusHandler.deleteSavedOnlineData();
        loadCorrectPage();
    }

    public void switchToPage2(View v) {
        savePage(2);
        loadCorrectPage();
    }

    private void switchToPage3() {
        savePage(3);
        loadCorrectPage();
    }

    private void savePage(int page) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt("PageJoin", page);
        editor.commit();
    }

    private void loadCorrectPage() {
        Integer currentPage = mSharedPreferences.getInt("PageJoin", 1);
        if (currentPage == 1) {
            mPage1.setVisibility(View.VISIBLE);
            mPage2.setVisibility(View.GONE);
            mPage3.setVisibility(View.GONE);
        }
        else if (currentPage == 2) {
            mPage1.setVisibility(View.GONE);
            mPage2.setVisibility(View.VISIBLE);
            mPage3.setVisibility(View.GONE);
            setupLists();
            setupSwipePlaceholderView();
        }
        else {
            mPage1.setVisibility(View.GONE);
            mPage2.setVisibility(View.GONE);
            mPage3.setVisibility(View.VISIBLE);
        }
    }

    private void setupElements() {
        mSharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        mContext = getApplicationContext();
        //resetSharedPreferences();
        generateUserID();
        setupClasses();
        setupViews();
        setupPages();
        setupLayouts();
        setupBottomNavigationBar();
        loadCorrectPage();
        setupDatabase();
    }

    private void setupLayouts() {
        mLinearLayoutPlaceholderResults = findViewById(R.id.linear_layout_result_page_placeholder);
    }

    private void setupSwipePlaceholderView() {
        mJoinGroupCodeInputStatusHandler.loadSelectedIDs();
        mSwipePlaceHolderView = findViewById(R.id.swipeView);
        mSwipePlaceHolderViewHandlerJoinGroup.setSwipePlaceHolderViewBuilder(mSwipePlaceHolderView);
        mSwipePlaceHolderViewHandlerJoinGroup.loadSwipePlaceholderView(mJoinGroupCodeInputStatusHandler.mSelectedIDs, mAllRecipesList, mSwipePlaceHolderView);
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

    private void setupClasses() {
        mJoinGroupCodeInputStatusHandler = new JoinGroupCodeInputStatusHandler(mContext, mSharedPreferences);
        mSwipePlaceHolderViewHandlerJoinGroup = new SwipePlaceHolderViewHandlerJoinGroup(mContext);
        mSwipeHandler = new SwipeHandler("Join", mSharedPreferences);
    }

    private void setupLists() {
        mSwipeHandler.loadLikedIndices();
        mSwipeHandler.loadDislikedIndices();
        mAllRecipesList = Loader.loadRecipies(mContext);
        mLikedIDs = mSwipeHandler.mLikedIDs;
        mDislikedIDs = mSwipeHandler.mDislikedIDs;
    }

    private void setupPages() {
        mPage1 = findViewById(R.id.page_1);
        mPage2 = findViewById(R.id.page_2);
        mPage3 = findViewById(R.id.page_3);
    }

    private void setupViews() {
        mBottomNavigationView = findViewById(R.id.bottom_navigation);
        mBottomNavigationView.setSelectedItemId(R.id.join_group);
        mResultListView = findViewById(R.id.list_view_result);

        mTextViewCodeInputStatus = findViewById(R.id.text_view_code_status);
        mTextViewJoinGroupButton = findViewById(R.id.text_view_join_group);
        mTextViewJoinGroupButton.setClickable(false);

        mTextViewLeaveGroupButton = findViewById(R.id.text_view_leave_group);
        mTextViewLeaveGroupButton.setClickable(false);

        mCodeInputView = findViewById(R.id.code_input_view);
        mCodeInputView.addOnCompleteListener(code -> checkUserInputCode(code));
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
                        startActivity(new Intent(getApplicationContext(), ActivityPlayAlone.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.join_group:
                        return true;
                    case R.id.create_group:
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
}