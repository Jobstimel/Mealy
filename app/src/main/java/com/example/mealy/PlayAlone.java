package com.example.mealy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class PlayAlone extends AppCompatActivity {

    //General
    private static final String TAG = "PlayAloneActivity";
    private Context mContext;

    //Views
    private BottomNavigationView mBottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_alone);

        mContext = getApplicationContext();

        setupViews();

        setupBottomNavigationBar();
    }

    private void setupViews() {
        mBottomNavigationView = findViewById(R.id.bottom_navigation);
        mBottomNavigationView.setSelectedItemId(R.id.play_alone);
    }

    private void setupBottomNavigationBar() {
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId()) {
                    case R.id.play_alone:
                        return true;
                    case R.id.join_group:
                        startActivity(new Intent(getApplicationContext(), JoinLobby.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.create_group:
                        startActivity(new Intent(getApplicationContext(), CreateLobby.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });
    }
}