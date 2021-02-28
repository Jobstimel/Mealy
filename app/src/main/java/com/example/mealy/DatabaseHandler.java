package com.example.mealy;

import android.content.SharedPreferences;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class DatabaseHandler {

    private SharedPreferences mSharedPreferences;

    public DatabaseHandler(SharedPreferences mSharedPreferences) {
        this.mSharedPreferences = mSharedPreferences;
    }

    public void updateGroupCompletedUserList(DataSnapshot dataSnapshot, DatabaseReference databaseReference, String code) {
        List<String> votingCompleted = (List<String>) dataSnapshot.child(code).child("voting_completed").getValue();

        for (int i = 0; i < votingCompleted.size(); i++) {
            if (votingCompleted.get(i).equals("")) {
                votingCompleted.set(i, mSharedPreferences.getString("UserID", ""));
                break;
            }
        }

        databaseReference.child(code).child("voting_completed").setValue(votingCompleted);
    }

    public void updateGroupCounter(DataSnapshot dataSnapshot, List<Integer> likedIDs, DatabaseReference databaseReference, String code) {
        List<String> counter = (List<String>) dataSnapshot.child(code).child("counter").getValue();
        List<String> selectedIDs = (List<String>) dataSnapshot.child(code).child("selected_ids").getValue();

        for (int i = 0; i < selectedIDs.size(); i++) {
            if (likedIDs.contains(Integer.parseInt(selectedIDs.get(i)))) {
                counter.set(i, String.valueOf(Integer.parseInt(counter.get(i)) + 1));
            }
        }

        databaseReference.child(code).child("counter").setValue(counter);
    }

    public void updateGroupPeopleNumber(DataSnapshot dataSnapshot, DatabaseReference databaseReference, String code) {
        String number = (String) dataSnapshot.child(code).child("people_number").getValue();
        number = String.valueOf(Integer.parseInt(number) + 1);
        databaseReference.child(code).child("people_number").setValue(number);
    }
}
