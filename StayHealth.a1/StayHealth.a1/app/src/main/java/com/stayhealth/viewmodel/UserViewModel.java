package com.stayhealth.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.stayhealth.model.UserProfile;
import com.stayhealth.repository.UserRepository;

public class UserViewModel extends ViewModel {

    private final UserRepository repo = new UserRepository();

    public LiveData<UserProfile> getUser() {
        return repo.getUserLiveData();
    }

    public void start(String uid) {
        repo.startListening(uid);
    }

    public void stop() {
        repo.stopListening();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        repo.stopListening();
    }
}
