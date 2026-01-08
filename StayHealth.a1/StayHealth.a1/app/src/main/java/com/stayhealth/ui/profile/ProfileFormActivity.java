package com.stayhealth.ui.profile;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.stayhealth.R;
import com.stayhealth.util.FirebaseService;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

public class ProfileFormActivity extends AppCompatActivity {

    private EditText edtAge, edtWeight, edtHeight, edtChronic, edtKcal;
    private TextView btnSave;

    private DatabaseReference rootRef;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_form);

        edtAge = findViewById(R.id.edtAge);
        edtWeight = findViewById(R.id.edtWeight);
        edtHeight = findViewById(R.id.edtHeight);
        edtChronic = findViewById(R.id.edtChronic);
        edtKcal = findViewById(R.id.edtKcal);
        btnSave = findViewById(R.id.btnSaveProfile);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(this, "Önce giriş yapmalısın.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        rootRef = FirebaseService.getDatabase().getReference();

        prefillFromIntent();
        loadExisting(); // fetch latest from DB

        btnSave.setOnClickListener(v -> saveProfile());
    }

    private void prefillFromIntent() {
        if (getIntent() == null) return;
        setIfPresent(edtAge, "age");
        setIfPresent(edtWeight, "weight");
        setIfPresent(edtHeight, "height");
        setIfPresent(edtChronic, "chronic");
        setIfPresent(edtKcal, "kcal");
    }

    private void setIfPresent(EditText editText, String key) {
        String val = getIntent().getStringExtra(key);
        if (val != null && editText != null) editText.setText(val);
    }

    private void loadExisting() {
        rootRef.child("users").child(uid).get().addOnSuccessListener(snap -> {
            if (!snap.exists()) return;

            Object age = snap.child("age").getValue();
            Object weight = snap.child("weight").getValue();
            Object height = snap.child("height").getValue();
            String chronic = snap.child("chronic").getValue(String.class);
            Object kcal = snap.child("kcal").getValue();

            if (age != null) edtAge.setText(String.valueOf(age));
            if (weight != null) edtWeight.setText(String.valueOf(weight));
            if (height != null) edtHeight.setText(String.valueOf(height));
            if (chronic != null) edtChronic.setText(chronic);
            if (kcal != null) edtKcal.setText(String.valueOf(kcal));
        });
    }

    private void saveProfile() {
        String ageStr = edtAge.getText().toString().trim();
        String weightStr = edtWeight.getText().toString().trim();
        String heightStr = edtHeight.getText().toString().trim();
        String chronicStr = edtChronic.getText().toString().trim();
        String kcalStr = edtKcal.getText().toString().trim();

        if (TextUtils.isEmpty(ageStr) || TextUtils.isEmpty(weightStr) ||
                TextUtils.isEmpty(heightStr) || TextUtils.isEmpty(kcalStr)) {
            Toast.makeText(this, "Age/Weight/Height/kcal boş olamaz.", Toast.LENGTH_SHORT).show();
            return;
        }

        int age = parseIntSafe(ageStr);
        int weight = parseIntSafe(weightStr);
        int height = parseIntSafe(heightStr);
        int kcal = parseIntSafe(kcalStr);

        if (age <= 0 || weight <= 0 || height <= 0 || kcal <= 0) {
            Toast.makeText(this, "Sayılar geçerli olmalı.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(chronicStr)) chronicStr = "No";

        Map<String, Object> map = new HashMap<>();
        map.put("age", age);
        map.put("weight", weight);
        map.put("height", height);
        map.put("chronic", chronicStr);
        map.put("kcal", kcal);
        map.put("updatedAt", System.currentTimeMillis());

        rootRef.child("users").child(uid).updateChildren(map)
            .addOnSuccessListener(unused -> {
                Toast.makeText(this, "Profil kaydedildi ✅", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Hata: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }

    private int parseIntSafe(String s) {
        try { return Integer.parseInt(s); } catch (Exception e) { return -1; }
    }
}
