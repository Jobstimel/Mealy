package com.example.mealy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class JoinGroup extends AppCompatActivity {

    //General
    private static final String TAG = "JoinLobbyActivity";
    private SharedPreferences mSharedPreferences;
    private Context mContext;

    //Pages
    private LinearLayout mPage1;
    private LinearLayout mPage2;
    private LinearLayout mPage3;

    //Views
    private BottomNavigationView mBottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_group);

        setupElements();
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
        setupViews();
        setupPages();
        setupBottomNavigationBar();
        loadCorrectPage();
    }

    private void setupPages() {
        mPage1 = findViewById(R.id.page_1);
        mPage2 = findViewById(R.id.page_2);
        mPage3 = findViewById(R.id.page_3);
    }

    private void setupViews() {
        mBottomNavigationView = findViewById(R.id.bottom_navigation);
        mBottomNavigationView.setSelectedItemId(R.id.join_group);
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
}