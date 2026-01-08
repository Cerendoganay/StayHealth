package com.stayhealth.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.stayhealth.R;
import com.stayhealth.ui.home.ProgressChartActivity;
import com.stayhealth.ui.home.DashboardActivity;
import com.stayhealth.ui.auth.LoginActivity;
import com.stayhealth.util.FirebaseService;
import com.stayhealth.viewmodel.UserViewModel;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

        private TextView txtName, txtEmail;
        private DatabaseReference rootRef;
        private String uid;
        private Map<String, Object> cachedProfile = new HashMap<>();
        private UserViewModel userViewModel;
        private boolean observingUser = false;

        private final ActivityResultLauncher<Intent> editProfileLauncher =
                        registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                                if (result.getResultCode() == RESULT_OK) {
                                        loadProfile();
                                }
                        });

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_profile);

                if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                        Toast.makeText(this, "Please login first.", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                }

                uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                rootRef = FirebaseService.getDatabase().getReference();
                userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

                // Back
                ImageView btnBack = findViewById(R.id.btnBack);
                if (btnBack != null) btnBack.setOnClickListener(v -> finish());

                txtName = findViewById(R.id.txtName);
                txtEmail = findViewById(R.id.txtEmail);

                findViewById(R.id.cardEditProfile).setOnClickListener(v -> openEditProfile());

                findViewById(R.id.cardSettings).setOnClickListener(v ->
                                Toast.makeText(this, "Settings (coming soon)", Toast.LENGTH_SHORT).show()
                );

                findViewById(R.id.cardLogout).setOnClickListener(v -> {
                        FirebaseAuth.getInstance().signOut();
                        Intent i = new Intent(this, LoginActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                        finishAffinity();
                });

                // Bottom nav
                ImageView navChart = findViewById(R.id.navChart);
                ImageView navChecklist = findViewById(R.id.navChecklist);
                ImageView navProfile = findViewById(R.id.navProfile);

                navChecklist.setOnClickListener(v -> startActivity(new Intent(this, DashboardActivity.class)));
                navChart.setOnClickListener(v -> startActivity(new Intent(this, ProgressChartActivity.class)));
                navProfile.setOnClickListener(v -> { /* already here */ });

                loadProfile();
                observeUserLiveDataOnce();
        }

        @Override
        protected void onStart() {
                super.onStart();
                observeUser();
        }

        @Override
        protected void onStop() {
                super.onStop();
                if (userViewModel != null) userViewModel.stop();
        }

        private void observeUser() {
                userViewModel.start(uid);
        }

        private void observeUserLiveDataOnce() {
                if (observingUser) return;
                observingUser = true;
                userViewModel.getUser().observe(this, profile -> {
                        if (profile == null) return;
                        cachedProfile.clear();
                        cachedProfile.put("name", profile.name);
                        cachedProfile.put("email", profile.email);
                        cachedProfile.put("age", profile.age);
                        cachedProfile.put("weight", profile.weight);
                        cachedProfile.put("height", profile.height);
                        cachedProfile.put("chronic", profile.chronic);
                        cachedProfile.put("kcal", profile.kcal);

                        if (txtName != null) {
                                if (profile.name != null && !profile.name.isEmpty()) txtName.setText(profile.name);
                                else if (FirebaseAuth.getInstance().getCurrentUser() != null && FirebaseAuth.getInstance().getCurrentUser().getDisplayName() != null)
                                        txtName.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                        }
                        if (txtEmail != null) {
                                if (profile.email != null && !profile.email.isEmpty()) txtEmail.setText(profile.email);
                                else if (FirebaseAuth.getInstance().getCurrentUser() != null) txtEmail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                        }
                });
        }

        private void openEditProfile() {
                Intent intent = new Intent(this, ProfileFormActivity.class);
                // pass current profile for fast prefill; DB fetch in form will also refresh
                for (Map.Entry<String, Object> entry : cachedProfile.entrySet()) {
                        if (entry.getValue() != null)
                                intent.putExtra(entry.getKey(), String.valueOf(entry.getValue()));
                }
                editProfileLauncher.launch(intent);
        }

        private void loadProfile() {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                        if (txtName != null) txtName.setText(user.getDisplayName() != null ? user.getDisplayName() : "Profile");
                        if (txtEmail != null) txtEmail.setText(user.getEmail());
                }

                rootRef.child("users").child(uid).get().addOnSuccessListener(snap -> {
                        cachedProfile.clear();
                        if (snap.exists()) {
                                for (String key : new String[]{"age", "weight", "height", "chronic", "kcal", "name"}) {
                                        cachedProfile.put(key, snap.child(key).getValue());
                                }
                                Object name = snap.child("name").getValue();
                                if (txtName != null && name != null) txtName.setText(String.valueOf(name));
                        }
                });
        }

        @Override
        protected void onDestroy() {
                super.onDestroy();
                if (userViewModel != null) userViewModel.stop();
        }
}
