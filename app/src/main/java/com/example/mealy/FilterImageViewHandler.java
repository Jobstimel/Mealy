package com.example.mealy;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import java.util.Arrays;
import java.util.List;

public class FilterImageViewHandler {

    public final List<String> TOOLTIPS = Arrays.asList("Deutschland","Spanien","Asien","Italien","Indien","Frankreich","Griechenland");

    private SharedPreferences mSharedPreferences;
    private Context mContext;
    private String mMode;

    public FilterImageViewHandler(SharedPreferences sharedPreferences, Context context, String mode) {
        this.mSharedPreferences = sharedPreferences;
        this.mContext = context;
        this.mMode = mode;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void saveImageViewState(View view, FilterApplier filterApplier, TextView textView, TextView button)  {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        if (!mSharedPreferences.getBoolean(view.getTooltipText() + mMode,false)) {
            editor.putBoolean(view.getTooltipText() + mMode, true);
            setViewSelected(view);
        }
        else {
            editor.putBoolean(view.getTooltipText() + mMode, false);
            setViewUnselected(view);
        }
        editor.putBoolean("ChangeStatus" + mMode, true);
        editor.commit();
        filterApplier.applyFilter(textView, button);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void loadImageViewStates(List<ImageView> list) {
        for (int i = 0; i < list.size(); i++) {
            if (mSharedPreferences.getBoolean(TOOLTIPS.get(i) + mMode, false)) {
                setImageViewSelected(list.get(i));
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void resetImageViewStates(List<ImageView> list) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        for (int i = 0; i < list.size(); i++) {
            editor.putBoolean(TOOLTIPS.get(i) + mMode, false);
            setImageViewUnselected(list.get(i));
        }
        editor.putBoolean("ChangeStatus" + mMode, true);
        editor.commit();
    }

    public void resetImageViewSharedPreferences() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        for (int i = 0; i < TOOLTIPS.size(); i++) {
            editor.putBoolean(TOOLTIPS.get(i) + mMode, false);
        }
        editor.commit();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setImageViewSelected(ImageView imageView) {
        imageView.setBackgroundTintList(ContextCompat.getColorStateList(mContext, R.color.blue));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setImageViewUnselected(ImageView imageView) {
        imageView.setBackgroundTintList(ContextCompat.getColorStateList(mContext, R.color.white));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setViewSelected(View view) {
        view.setBackgroundTintList(ContextCompat.getColorStateList(mContext, R.color.blue));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setViewUnselected(View view) {
        view.setBackgroundTintList(ContextCompat.getColorStateList(mContext, R.color.white));
    }
}
