package com.stayhealth.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.stayhealth.model.UserProfile;
import com.stayhealth.util.FirebaseService;

/**
 * Real-time user profile source of truth (path: /users/{uid}).
 */
public class UserRepository {

    private final MutableLiveData<UserProfile> userLiveData = new MutableLiveData<>();
    private DatabaseReference userRef;
    private ValueEventListener listener;

    public LiveData<UserProfile> getUserLiveData() {
        return userLiveData;
    }

    public void startListening(@NonNull String uid) {
        stopListening();
        userRef = FirebaseService.getDatabase().getReference("users").child(uid);
        listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserProfile profile = snapshot.getValue(UserProfile.class);
                if (profile == null) profile = new UserProfile();
                userLiveData.postValue(profile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Keep last known data; could expose error channel if needed.
            }
        };
        userRef.addValueEventListener(listener);
    }

    public void stopListening() {
        if (userRef != null && listener != null) {
            userRef.removeEventListener(listener);
        }
        listener = null;
        userRef = null;
    }
}
