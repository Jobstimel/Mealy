package com.example.mealy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.raycoarana.codeinputview.CodeInputView;
import com.raycoarana.codeinputview.OnCodeCompleteListener;

import java.util.ArrayList;
import java.util.List;

public class JoinGroup extends AppCompatActivity {

    //General
    private static final String TAG = "JOIN_LOBBY_ACTIVITY";
    private SharedPreferences mSharedPreferences;
    private Context mContext;

    //Classes
    private JoinGroupCodeInputStatusHandler mJoinGroupCodeInputStatusHandler;

    //Pages
    private LinearLayout mPage1;
    private LinearLayout mPage2;
    private LinearLayout mPage3;

    //Views
    private BottomNavigationView mBottomNavigationView;
    private CodeInputView mCodeInputView;
    private TextView mTextViewCodeInputStatus;
    private TextView mTextViewJoinGroupButton;

    //Database
    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_group);

        setupElements();
    }

    private void checkUserInputCode(String code) {
        mDatabaseReference = mDatabaseReference.child(code);
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Integer> selectedIDs = (ArrayList<Integer>) dataSnapshot.child("selected_ids").getValue();
                if (!(selectedIDs == null)) {
                    mJoinGroupCodeInputStatusHandler.setInputCodeStatusFound(mTextViewCodeInputStatus, mCodeInputView, mTextViewJoinGroupButton);
                    mJoinGroupCodeInputStatusHandler.saveSelectedIDs(selectedIDs);
                }
                else {
                    restartActivity();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("TAG", "DATABASE ERROR: ", databaseError.toException());
            }
        });
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
        setupClasses();
        setupViews();
        setupPages();
        setupBottomNavigationBar();
        loadCorrectPage();
        setupDatabase();
    }

    private void setupClasses() {
        mJoinGroupCodeInputStatusHandler = new JoinGroupCodeInputStatusHandler(mContext, mSharedPreferences);
    }

    private void setupPages() {
        mPage1 = findViewById(R.id.page_1);
        mPage2 = findViewById(R.id.page_2);
        mPage3 = findViewById(R.id.page_3);
    }

    private void setupViews() {
        mBottomNavigationView = findViewById(R.id.bottom_navigation);
        mBottomNavigationView.setSelectedItemId(R.id.join_group);

        mTextViewCodeInputStatus = findViewById(R.id.text_view_code_status);
        mTextViewJoinGroupButton = findViewById(R.id.text_view_join_group);
        mTextViewJoinGroupButton.setClickable(false);

        mCodeInputView = findViewById(R.id.code_input_view);
        mCodeInputView.addOnCompleteListener(code -> checkUserInputCode(code));
    }

    private void setupDatabase() {
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
    }

    private void setupBottomNavigationBar() {
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId()) {
                    case R.id.play_alone:
                        startActivity(new Intent(getApplicationContext(), PlayAlone.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.join_group:
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

    private void restartActivity() {
        Intent intent = getIntent();
        finish();
        overridePendingTransition(0,0);
        startActivity(intent);
    }
}