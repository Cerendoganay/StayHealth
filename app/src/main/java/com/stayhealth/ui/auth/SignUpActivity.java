package com.stayhealth.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.stayhealth.R;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private EditText edtUsername, edtEmail, edtPassword, edtConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();

        ImageView btnBack = findViewById(R.id.btnBack);
        Button btnSignUp  = findViewById(R.id.btnSignUp);

        edtUsername = findViewById(R.id.edtUsername);
        edtEmail    = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirm  = findViewById(R.id.edtConfirm);

        // 🔙 BACK → LoginActivity
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        // ▶ SIGN UP → Firebase Auth + Realtime DB + verify mail + EmailVerifyActivity
        btnSignUp.setOnClickListener(v -> doSignUp());
    }

    private void doSignUp() {
        String name  = edtUsername.getText().toString().trim();
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

        mAuth.createUserWithEmailAndPassword(email, pass1)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Toast.makeText(this,
                                "Registration failed: " +
                                        (task.getException() != null
                                                ? task.getException().getMessage()
                                                : ""),
                                Toast.LENGTH_LONG).show();
                        return;
                    }

                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user == null) {
                        Toast.makeText(this,
                                "The user was created but a session could not be obtained.",
                                Toast.LENGTH_LONG).show();
                        return;
                    }

                    String uid = user.getUid();

                    // Realtime Database'de profil kaydı
                    Map<String, Object> profile = new HashMap<>();
                    profile.put("name", name);
                    profile.put("email", email);
                    profile.put("role", "patient"); // ileride dietitian/patient ayrımı yapabilirsin
                    profile.put("createdAt", System.currentTimeMillis());

                    FirebaseDatabase.getInstance()
                            .getReference("users")
                            .child(uid)
                            .setValue(profile)
                            .addOnSuccessListener(aVoid -> {
                                // Profil kaydı başarılı → doğrulama maili gönder ve verify ekrana git
                                sendVerifyEmailAndGo(user);
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this,
                                        "Profile save error: " + e.getMessage(),
                                        Toast.LENGTH_LONG).show();
                                // Yine de user oluşturulmuş durumda, ama verify ekranına geçmek mantıklı
                                sendVerifyEmailAndGo(user);
                            });
                });
    }

    private void sendVerifyEmailAndGo(FirebaseUser user) {
        user.sendEmailVerification()
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this,
                            "Verification email sent. Please check your inbox.",
                            Toast.LENGTH_LONG).show();

                    // EmailVerifyActivity'e git
                    Intent intent = new Intent(SignUpActivity.this, EmailVerifyActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this,
                            "Verification email could not be sent: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();

                    // Yine de verify ekranına yönlendirebiliriz
                    Intent intent = new Intent(SignUpActivity.this, EmailVerifyActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                });
    }
}
