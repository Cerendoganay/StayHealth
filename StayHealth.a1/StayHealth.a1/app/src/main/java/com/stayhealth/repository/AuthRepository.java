package com.stayhealth.repository;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.stayhealth.util.FirebaseService;

import java.util.function.Consumer;

/**
 * Auth-related data operations.
 */
public class AuthRepository {

    private final FirebaseAuth auth;
    private final FirebaseDatabase db;

    public AuthRepository() {
        this.auth = FirebaseAuth.getInstance();
        this.db = FirebaseService.getDatabase();
    }

    public void logoutIfLoggedIn() {
        if (auth.getCurrentUser() != null) {
            auth.signOut();
        }
    }

    public void login(String email, String password,
                      Consumer<FirebaseUser> onSuccess,
                      Consumer<String> onError) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && auth.getCurrentUser() != null) {
                        onSuccess.accept(auth.getCurrentUser());
                    } else {
                        String msg = task.getException() != null
                                ? task.getException().getMessage()
                                : "Unknown error";
                        onError.accept(msg);
                    }
                });
    }

    public void fetchRole(@NonNull String uid,
                          Consumer<String> onSuccess,
                          Consumer<String> onError) {
        db.getReference("users").child(uid).child("role")
                .get()
                .addOnSuccessListener(snapshot -> onSuccess.accept(parseRole(snapshot)))
                .addOnFailureListener(e -> onError.accept(e.getMessage()));
    }

    private String parseRole(DataSnapshot snapshot) {
        String role = snapshot.getValue(String.class);
        return role == null ? "client" : role;
    }
}
