package com.example.mealy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class CreateLobby extends AppCompatActivity {

    //General
    private static final String TAG = "CreateLobbyActivity";
    private Context mContext;

    //Views
    private BottomNavigationView mBottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_lobby);

        mContext = getApplicationContext();

        setupViews();

        setupBottomNavigationBar();
    }

    private void setupViews() {
        mBottomNavigationView = findViewById(R.id.bottom_navigation);
        mBottomNavigationView.setSelectedItemId(R.id.create_group);
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
                        startActivity(new Intent(getApplicationContext(), JoinLobby.class));
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