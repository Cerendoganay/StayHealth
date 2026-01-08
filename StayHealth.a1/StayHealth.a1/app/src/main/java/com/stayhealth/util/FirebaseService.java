package com.stayhealth.util;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Single source of truth for Firebase Realtime Database instance.
 */
public final class FirebaseService {

    private static FirebaseDatabase database;

    private FirebaseService() {}

    /**
     * Returns the EU-region FirebaseDatabase instance used across the app.
     */
    public static synchronized FirebaseDatabase getDatabase() {
        if (database == null) {
            // Ensure default app is ready (defensive, normally already initialized by google-services).
            FirebaseApp.initializeApp(com.stayhealth.StayHealthApp.getContext());
            database = FirebaseDatabase.getInstance(Constants.FIREBASE_DB_URL);
        }
        return database;
    }
}
