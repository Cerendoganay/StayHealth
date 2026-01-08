package com.stayhealth.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.stayhealth.R;
import com.stayhealth.util.FirebaseService;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private EditText edtUsername, edtEmail, edtPassword, edtConfirm;

    // ✅ Role seçim butonları (XML’e ekleyeceğiz)
    private TextView btnClient, btnDietician;
    private String selectedRole = "client"; // default

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin); // sende signup layout adı böyle

        mAuth = FirebaseAuth.getInstance();

        ImageView btnBack = findViewById(R.id.btnBack);
        Button btnSignUp = findViewById(R.id.btnSignUp);

        edtUsername = findViewById(R.id.edtUsername);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirm = findViewById(R.id.edtConfirm);

        // ✅ Role UI (XML’de id’leri bu şekilde olmalı)
        btnClient = findViewById(R.id.btnClient);
        btnDietician = findViewById(R.id.btnDietician);

        // ilk görünüm
        applyRoleUI();

        btnClient.setOnClickListener(v -> {
            selectedRole = "client";
            applyRoleUI();
        });

        btnDietician.setOnClickListener(v -> {
            selectedRole = "dietician";
            applyRoleUI();
        });

        // 🔙 BACK → LoginActivity
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        // ▶ SIGN UP
        btnSignUp.setOnClickListener(v -> doSignUp());
    }

    private void applyRoleUI() {
        if (btnClient == null || btnDietician == null) return;

        int selectedText = 0xFF111111;   // koyu
        int unselectedText = 0xFF777777; // gri

        if ("client".equals(selectedRole)) {
            btnClient.setBackgroundResource(R.drawable.bg_toggle_selected);
            btnDietician.setBackgroundResource(R.drawable.bg_toggle_unselected);

            btnClient.setTextColor(selectedText);
            btnDietician.setTextColor(unselectedText);
        } else {
            btnClient.setBackgroundResource(R.drawable.bg_toggle_unselected);
            btnDietician.setBackgroundResource(R.drawable.bg_toggle_selected);

            btnClient.setTextColor(unselectedText);
            btnDietician.setTextColor(selectedText);
        }
    }



    private void doSignUp() {
        String name = edtUsername.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String pass1 = edtPassword.getText().toString().trim();
        String pass2 = edtConfirm.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) ||
                TextUtils.isEmpty(pass1) || TextUtils.isEmpty(pass2)) {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!pass1.equals(pass2)) {
            Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (pass1.length() < 6) {
            Toast.makeText(this, "The password must be at least 6 characters long.", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, pass1).addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Toast.makeText(this,
                        "Registration failed: " + (task.getException() != null ? task.getException().getMessage() : ""),
                        Toast.LENGTH_LONG).show();
                return;
            }

            FirebaseUser user = mAuth.getCurrentUser();
            if (user == null) {
                Toast.makeText(this, "The user was created but a session could not be obtained.", Toast.LENGTH_LONG).show();
                return;
            }

            String uid = user.getUid();

            // ✅ Realtime Database'de profil kaydı
            Map<String, Object> profile = new HashMap<>();
            profile.put("name", name);
            profile.put("email", email);
            profile.put("role", selectedRole); // ✅ "client" / "dietician"
            profile.put("createdAt", System.currentTimeMillis());

                FirebaseService.getDatabase().getReference("users").child(uid).setValue(profile)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Kayıt başarılı! ✅", Toast.LENGTH_SHORT).show();
                        // FIXED: Navigate directly based on role - no email verification
                        navigateByRole(selectedRole);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Profile save error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        // Even if profile save fails, navigate based on role
                        navigateByRole(selectedRole);
                    });
        });
    }

    // FIXED: Navigate directly based on role after signup (no email verification)
    private void navigateByRole(String role) {
        Class<?> targetActivity = "dietician".equalsIgnoreCase(role)
                ? com.stayhealth.ui.consultant.ConsultantListActivity.class
                : com.stayhealth.ui.home.DashboardActivity.class;

        Intent intent = new Intent(SignUpActivity.this, targetActivity);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    /* DISABLED - Email verification removed
    private void sendVerifyEmailAndGo(FirebaseUser user) {
        user.sendEmailVerification()
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Verification email sent. Please check your inbox.", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(SignUpActivity.this, EmailVerifyActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Verification email could not be sent: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(SignUpActivity.this, EmailVerifyActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                });
    }
    */
}
