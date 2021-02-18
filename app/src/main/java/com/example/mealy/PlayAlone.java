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

import java.util.List;
import java.util.Locale;

public class PlayAlone extends FragmentActivity {

    //General
    private static final String TAG = "PlayAloneActivity";
    private SharedPreferences mSharedPreferences;
    private Context mContext;

    //Class Objects
    private FilterApplier mFilterApplier;

    //Views
    private BottomNavigationView mBottomNavigationView;

    //Lists
    private List<Recipe> mAllRecipesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_alone);

        mContext = getApplicationContext();

        setupElements();

        setupBottomNavigationBar();

    }

    /**
     * Changes the background color of the pressed filter value
     * Calls the applyFilter() method to update the filtered recipes
     * @calls applyFilter()
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void saveFilterValue(View v) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        if (!mSharedPreferences.getBoolean(String.valueOf(v.getTooltipText()+"Offline"),false)) {
            editor.putBoolean(String.valueOf(v.getTooltipText()+"Offline"), true);
            v.setBackgroundColor(ContextCompat.getColor(mContext, R.color.green));
        }
        else {
            editor.putBoolean(String.valueOf(v.getTooltipText()+"Offline"), false);
            v.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
        }
        editor.commit();
        mFilterApplier.applyFilter();
    }

    private void setupElements() {
        mBottomNavigationView = findViewById(R.id.bottom_navigation);
        mBottomNavigationView.setSelectedItemId(R.id.play_alone);
        mSharedPreferences = getSharedPreferences("SavedData", Context.MODE_PRIVATE);
        mAllRecipesList = Loader.loadRecipies(mContext);
        mFilterApplier = new FilterApplier("Offline", mSharedPreferences, mAllRecipesList);

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