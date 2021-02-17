package com.example.mealy;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class PlayAlone extends FragmentActivity {

    //General
    private static final String TAG = "PlayAloneActivity";
    private SharedPreferences mSharedPreferences;
    private Context mContext;

    //Views
    private BottomNavigationView mBottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_alone);

        mContext = getApplicationContext();

        setupElements();

        setupBottomNavigationBar();

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void saveFilterValue(View v) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        if (!mSharedPreferences.getBoolean(String.valueOf(v.getTooltipText()),false)) {
            editor.putBoolean(String.valueOf(v.getTooltipText()), true);
            v.setBackgroundColor(ContextCompat.getColor(mContext, R.color.green));
        }
        else {
            editor.putBoolean(String.valueOf(v.getTooltipText()), false);
            v.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
        }
        editor.putBoolean("ChangeStatus", true);
        editor.commit();
        //applyFilter(v);
    }

    private void setupElements() {
        mBottomNavigationView = findViewById(R.id.bottom_navigation);
        mBottomNavigationView.setSelectedItemId(R.id.play_alone);
        mSharedPreferences = getSharedPreferences("SavedData", Context.MODE_PRIVATE);

        Fragment newFragment = new OfflineFilter();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.add(R.id.fragment_container, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void setupBottomNavigationBar() {
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId()) {
                    case R.id.play_alone:
                        return true;
                    case R.id.join_group:
                        startActivity(new Intent(getApplicationContext(), JoinGroup.class));
                        overridePendingTransition(0,0);
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