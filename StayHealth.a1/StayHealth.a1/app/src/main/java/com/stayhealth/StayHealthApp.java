package com.stayhealth;

import android.app.Application;
import android.content.Context;

import com.google.firebase.FirebaseApp;

/**
 * Initializes shared app-level services.
 */
public class StayHealthApp extends Application {

    private static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
        // Ensure Firebase default app is ready early.
        FirebaseApp.initializeApp(this);
    }

    public static Context getContext() {
        return appContext;
    }
}
