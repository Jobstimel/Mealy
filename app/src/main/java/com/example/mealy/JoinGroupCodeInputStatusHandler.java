package com.example.mealy;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.raycoarana.codeinputview.CodeInputView;

import java.util.ArrayList;
import java.util.List;

public class JoinGroupCodeInputStatusHandler {

    private Context mContext;
    private SharedPreferences mSharedPreferences;
    public List<Integer> mSelectedIDs;

    public JoinGroupCodeInputStatusHandler(Context mContext, SharedPreferences sharedPreferences) {
        this.mContext = mContext;
        this.mSharedPreferences = sharedPreferences;
    }

    public void setInputCodeStatusFound(TextView status, CodeInputView codeInputView, TextView button) {
        status.setText("Status: Gruppe gefunden");
        status.setBackgroundColor(ContextCompat.getColor(mContext, R.color.filter_green));
        codeInputView.setTextColor(ContextCompat.getColor(mContext, R.color.filter_green));
        button.setClickable(true);
    }

    public void saveSelectedIDs(List<Integer> selectedIDs) {
        String tmp = "";
        if (selectedIDs.size() > 0) {
            tmp = String.valueOf(selectedIDs.get(0));
            for (int i = 1; i < selectedIDs.size(); i++) {
                tmp = tmp+ "#" + selectedIDs.get(i);
            }
        }
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("JoinGroupIDs", tmp);
        editor.commit();
    }

    public void saveJoinedGroupCode(String code) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("JoinedGroupCode", code);
        editor.commit();
    }

    public void loadSelectedIDs() {
        mSelectedIDs = new ArrayList<>();
        String tmp1 = mSharedPreferences.getString("JoinGroupIDs", "");
        if (!tmp1.equals("")) {
            String[] tmp2 = tmp1.split("#");
            for (int i = 0; i < tmp2.length; i++) {
                mSelectedIDs.add(Integer.parseInt(tmp2[i]));
            }
        }
    }
}
