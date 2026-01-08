package com.stayhealth.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseUser;
import com.stayhealth.repository.AuthRepository;

/**
 * MVVM ViewModel for login screen.
 */
public class LoginViewModel extends ViewModel {

    public static class LoginState {
        public final boolean loading;
        public final String error;
        public final String role;
        public final FirebaseUser user;

        public LoginState(boolean loading, String error, String role, FirebaseUser user) {
            this.loading = loading;
            this.error = error;
            this.role = role;
            this.user = user;
        }
    }

    private final AuthRepository repo = new AuthRepository();
    private final MutableLiveData<LoginState> state = new MutableLiveData<>(new LoginState(false, null, null, null));

    public LiveData<LoginState> getState() {
        return state;
    }

    public void logoutPreviousSession() {
        repo.logoutIfLoggedIn();
    }

    public void login(String email, String password) {
        state.setValue(new LoginState(true, null, null, null));
        repo.login(email, password,
                user -> fetchRole(user),
                err -> state.postValue(new LoginState(false, err, null, null)));
    }

    private void fetchRole(FirebaseUser user) {
        repo.fetchRole(user.getUid(),
                role -> state.postValue(new LoginState(false, null, role, user)),
                err -> state.postValue(new LoginState(false, err, null, user)));
    }
}
