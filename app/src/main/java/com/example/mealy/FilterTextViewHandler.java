package com.example.mealy;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import java.util.Arrays;
import java.util.List;

public class FilterTextViewHandler {

    public final List<String> TOOLTIPS = Arrays.asList("Einfach","Mittel","Schwierig","Frühstück","Mittagessen","Abendessen","Dessert","Snack","Getränk","Deutschland","Spanien","Asien","Italien","Frankreich","Griechenland","Indien");

    private SharedPreferences mSharedPreferences;
    private Context mContext;
    private String mMode;


    public FilterTextViewHandler(SharedPreferences sharedPreferences, Context context, String mode) {
        this.mSharedPreferences = sharedPreferences;
        this.mContext = context;
        this.mMode = mode;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void saveTextViewState(View view, FilterApplier filterApplier, TextView recipeCount, TextView button)  {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        if (!mSharedPreferences.getBoolean(view.getTooltipText() + mMode,false)) {
            editor.putBoolean(view.getTooltipText() + mMode, true);
            setViewSelected(view);
        }
        else {
            editor.putBoolean(view.getTooltipText() + mMode, false);
            setViewUnselected(view);
        }
        editor.putBoolean("ChangeStatus"+mMode, true);
        editor.commit();
        filterApplier.applyFilter(recipeCount, button);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void loadTextViewStates(List<TextView> list) {
        for (int i = 0; i < list.size(); i++) {
            if (mSharedPreferences.getBoolean(TOOLTIPS.get(i) + mMode, false)) {
                setTextViewSelected(list.get(i));
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void resetTextViewStates(List<TextView> list) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        for (int i = 0; i < list.size(); i++) {
            editor.putBoolean(TOOLTIPS.get(i) + mMode, false);
            setTextViewUnselected(list.get(i));
        }
        editor.putBoolean("ChangeStatus" + mMode, true);
        editor.commit();
    }

    public void resetTextViewSharedPreferences() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        for (int i = 0; i < TOOLTIPS.size(); i++) {
            editor.putBoolean(TOOLTIPS.get(i)+mMode, false);
        }
        editor.commit();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setTextViewSelected(TextView textView) {
        textView.setBackgroundTintList(ContextCompat.getColorStateList(mContext, R.color.blue));
        textView.setTextColor(ContextCompat.getColor(mContext, R.color.white));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setTextViewUnselected(TextView textView) {
        textView.setBackgroundTintList(ContextCompat.getColorStateList(mContext, R.color.white));
        textView.setTextColor(ContextCompat.getColor(mContext, R.color.black));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setViewSelected(View view) {
        view.setBackgroundTintList(ContextCompat.getColorStateList(mContext, R.color.blue));
        TextView textView = (TextView) view;
        textView.setTextColor(ContextCompat.getColor(mContext, R.color.white));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setViewUnselected(View view) {
        view.setBackgroundTintList(ContextCompat.getColorStateList(mContext, R.color.white));
        TextView textView = (TextView) view;
        textView.setTextColor(ContextCompat.getColor(mContext, R.color.black));
    }
}
