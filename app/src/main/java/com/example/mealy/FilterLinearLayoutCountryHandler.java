package com.example.mealy;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.example.mealy.FilterApplier;
import com.example.mealy.R;

import java.util.Arrays;
import java.util.List;

public class FilterLinearLayoutCountryHandler {

    public final List<String> TOOLTIPS = Arrays.asList("Deutschland","Spanien","Asien","Italien","Frankreich","Griechenland","Indien");

    private String mMode;
    private SharedPreferences mSharedPreferences;
    private Context mContext;

    public FilterLinearLayoutCountryHandler(String mMode, SharedPreferences mSharedPreferences, Context mContext) {
        this.mMode = mMode;
        this.mSharedPreferences = mSharedPreferences;
        this.mContext = mContext;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void saveCountryFilterValue(View v, FilterApplier filterApplier, TextView textView)  {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        if (!mSharedPreferences.getBoolean(v.getTooltipText() +mMode,false)) {
            editor.putBoolean(v.getTooltipText() +mMode, true);
            v.setBackgroundColor(ContextCompat.getColor(mContext, R.color.green_transparent));
        }
        else {
            editor.putBoolean(v.getTooltipText() +mMode, false);
            v.setBackgroundColor(ContextCompat.getColor(mContext, R.color.background));
        }
        editor.putBoolean("ChangeStatusOffline", true);
        editor.commit();
        filterApplier.applyFilter(textView);
    }

    public void loadCountryFilterLayoutStates(List<LinearLayout> list) {
        for (int i = 0; i < list.size(); i++) {
            if (mSharedPreferences.getBoolean(this.TOOLTIPS.get(i)+mMode, false)) {
                list.get(i).setBackgroundColor(ContextCompat.getColor(mContext, R.color.green_transparent));
            }
        }
    }

    public void resetCountryLayouts(List<LinearLayout> list) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        for (int i = 0; i < list.size(); i++) {
            editor.putBoolean(this.TOOLTIPS.get(i)+mMode, false);
            list.get(i).setBackgroundColor(ContextCompat.getColor(mContext, R.color.background));
        }
        editor.putBoolean("ChangeStatusOffline", true);
        editor.commit();
        loadCountryFilterLayoutStates(list);
    }
}
