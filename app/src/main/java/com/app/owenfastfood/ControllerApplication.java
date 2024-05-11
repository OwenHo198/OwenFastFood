package com.app.owenfastfood;

import android.app.Application;
import android.content.Context;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.app.owenfastfood.prefs.DataStoreManager;

public class ControllerApplication extends Application {
    private static final String FIREBASE_URL = "https://owenfastfood-224d1-default-rtdb.firebaseio.com";
    private FirebaseDatabase mFirebaseDatabase;
    public static ControllerApplication get(Context context) {
        return (ControllerApplication) context.getApplicationContext();
    }
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        mFirebaseDatabase = FirebaseDatabase.getInstance(FIREBASE_URL);
        DataStoreManager.init(getApplicationContext());
    }
    public DatabaseReference getFoodDatabaseReference() {
        return mFirebaseDatabase.getReference("/food");
    }
    public DatabaseReference getBookingDatabaseReference() {
        return mFirebaseDatabase.getReference("/order");
    }
    public DatabaseReference getFeedbackDatabaseReference() {
        return mFirebaseDatabase.getReference("/feedback");
    }


}