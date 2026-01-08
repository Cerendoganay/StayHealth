package com.stayhealth.ui.dashboard;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.stayhealth.R;
import com.stayhealth.model.UserProfile;
import com.stayhealth.util.FirebaseService;
import com.stayhealth.viewmodel.UserViewModel;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private EditText edtAge, edtWeight, edtHeight, edtChronic, edtKcal;
    private View btnSaveProfile; // ✅ Button/TextView fark etmez
    private DatabaseReference rootRef;
    private String uid;
    private UserViewModel userViewModel;
    private boolean observingUser = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        edtAge = view.findViewById(R.id.edtAge);
        edtWeight = view.findViewById(R.id.edtWeight);
        edtHeight = view.findViewById(R.id.edtHeight);
        edtChronic = view.findViewById(R.id.edtChronic);
        edtKcal = view.findViewById(R.id.edtKcal);
        btnSaveProfile = view.findViewById(R.id.btnSaveProfile);

        if (btnSaveProfile == null) {
            Toast.makeText(getContext(), "HATA: btnSaveProfile id bulunamadı!", Toast.LENGTH_LONG).show();
            return;
        }

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(getContext(), "Please login first.", Toast.LENGTH_SHORT).show();
            return;
        }

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        rootRef = FirebaseService.getDatabase().getReference();
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        btnSaveProfile.setOnClickListener(v -> saveProfile());
        observeUserOnce(view);
    }

    @Override
    public void onStart() {
        super.onStart();
        userViewModel.start(uid);
    }

    @Override
    public void onStop() {
        super.onStop();
        userViewModel.stop();
    }

    private void observeUserOnce(View view) {
        if (observingUser) return;
        observingUser = true;
        userViewModel.getUser().observe(getViewLifecycleOwner(), this::applyProfile);
    }

    private void applyProfile(UserProfile profile) {
        if (profile == null) return;
        if (profile.age != null && edtAge != null) edtAge.setText(String.valueOf(profile.age));
        if (profile.weight != null && edtWeight != null) edtWeight.setText(String.valueOf(profile.weight));
        if (profile.height != null && edtHeight != null) edtHeight.setText(String.valueOf(profile.height));
        if (profile.chronic != null && edtChronic != null) edtChronic.setText(profile.chronic);
        if (profile.kcal != null && edtKcal != null) edtKcal.setText(String.valueOf(profile.kcal));
    }

    private void saveProfile() {
        String ageStr = edtAge.getText().toString().trim();
        String weightStr = edtWeight.getText().toString().trim();
        String heightStr = edtHeight.getText().toString().trim();
        String chronicStr = edtChronic.getText().toString().trim();
        String kcalStr = edtKcal.getText().toString().trim();

        if (TextUtils.isEmpty(ageStr) || TextUtils.isEmpty(weightStr)
                || TextUtils.isEmpty(heightStr) || TextUtils.isEmpty(kcalStr)) {
            Toast.makeText(getContext(), "Age/Weight/Height/Kcal boş olamaz.", Toast.LENGTH_SHORT).show();
            return;
        }

        int age = parseIntSafe(ageStr);
        int weight = parseIntSafe(weightStr);
        int height = parseIntSafe(heightStr);
        int kcal = parseIntSafe(kcalStr);

        if (age <= 0 || weight <= 0 || height <= 0 || kcal <= 0) {
            Toast.makeText(getContext(), "Değerler geçerli sayı olmalı.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(chronicStr)) chronicStr = "No";

        Map<String, Object> profile = new HashMap<>();
        profile.put("age", age);
        profile.put("weight", weight);
        profile.put("height", height);
        profile.put("chronic", chronicStr);
        profile.put("kcal", kcal);
        profile.put("updatedAt", System.currentTimeMillis());

        rootRef.child("users").child(uid).updateChildren(profile)
                .addOnSuccessListener(unused ->
                        Toast.makeText(getContext(), "Profile saved ✅", Toast.LENGTH_SHORT).show()
                )
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Save error: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }

    private int parseIntSafe(String s) {
        try { return Integer.parseInt(s); } catch (Exception e) { return -1; }
    }
}
