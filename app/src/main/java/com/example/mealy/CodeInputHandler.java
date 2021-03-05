package com.example.mealy;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.raycoarana.codeinputview.CodeInputView;

import java.util.ArrayList;
import java.util.List;

public class CodeInputHandler {

    private Context mContext;
    private SharedPreferences mSharedPreferences;
    public List<Integer> mSelectedIDs;

    public CodeInputHandler(Context mContext, SharedPreferences sharedPreferences) {
        this.mContext = mContext;
        this.mSharedPreferences = sharedPreferences;
    }

    public void checkStatus(String code, DataSnapshot dataSnapshot, TextView status, CodeInputView codeInputView, TextView button) {
        if (dataSnapshot.child(code).exists()) {
            String group_status = (String) dataSnapshot.child(code).child("group_status").getValue();
            List<String> alreadyComplete = (List<String>) dataSnapshot.child(code).child("voting_completed").getValue();
            if (group_status.equals("open")) {
                if (!alreadyComplete.contains(mSharedPreferences.getString("UserID", ""))) {
                    saveSelectedIDs((ArrayList<String>) dataSnapshot.child(code).child("selected_ids").getValue());
                    saveJoinedGroupCode(code);
                    setStatusFound(status, codeInputView, button);
                }
                else {
                    setStatusAlreadyCompleted(status, codeInputView);
                }
            }
            else {
                setStatusClosed(status, codeInputView);
            }
        }
        else {
            setStatusNotFound(status, codeInputView);
        }
    }

    private void setStatusFound(TextView status, CodeInputView codeInputView, TextView button) {
        status.setText("Status: Gruppe gefunden");
        status.setTextColor(ContextCompat.getColor(mContext, R.color.filter_green));
        codeInputView.setTextColor(ContextCompat.getColor(mContext, R.color.filter_green));
        button.setText("Gruppe beitreten");
        button.setClickable(true);
    }

    private void setStatusAlreadyCompleted(TextView status, CodeInputView codeInputView) {
        status.setText("Bereits abgestimmt");
        status.setTextColor(ContextCompat.getColor(mContext, R.color.orange));
        codeInputView.setEditable(true);
    }

    private void setStatusClosed(TextView status, CodeInputView codeInputView) {
        status.setText("Gruppe wurde geschlossen");
        status.setTextColor(ContextCompat.getColor(mContext, R.color.orange));
        codeInputView.setEditable(true);
    }

    private void setStatusNotFound(TextView status, CodeInputView codeInputView) {
        status.setText("Keine Gruppe gefunden");
        status.setTextColor(ContextCompat.getColor(mContext, R.color.red));
        codeInputView.setEditable(true);
    }

    public void saveSelectedIDs(List<String> selectedIDs) {
        String tmp = "";
        if (selectedIDs.size() > 0) {
            tmp = selectedIDs.get(0);
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
        editor.putString("JoinGroupCode", code);
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

    public void deleteSavedOnlineData() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("JoinGroupIDs", "");
        editor.putString("JoinGroupCode", "");
        editor.putString("LikedJoinIDs", "");
        editor.putString("DislikedJoinIDs", "");
        editor.commit();
    }
}
