package com.stayhealth.ui.auth;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.stayhealth.R;

public class ForgotPasswordActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText edtEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mAuth = FirebaseAuth.getInstance();

        ImageView btnBack         = findViewById(R.id.btnBack);
        Button btnSendCode        = findViewById(R.id.btnSendEmail);
        TextView txtBackToLogin   = findViewById(R.id.txtBackToLogin);
        edtEmail                  = findViewById(R.id.edtEmail);

        //  Login'a geri dön
        btnBack.setOnClickListener(v -> finish());
        txtBackToLogin.setOnClickListener(v -> finish());

        //  Reset Mail Gönder
        btnSendCode.setOnClickListener(v -> sendResetEmail());
    }

    private void sendResetEmail() {
        String email = edtEmail.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter your email address.", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(
                            ForgotPasswordActivity.this,
                            "Password reset link sent. Check your inbox.",
                            Toast.LENGTH_LONG
                    ).show();

                    // ✅ EmailVerifyActivity'ye geç
                    android.content.Intent intent =
                            new android.content.Intent(ForgotPasswordActivity.this, EmailVerifyActivity.class);
                    intent.putExtra("mode", "reset_password"); // istersen ekranda farklı yazı gösterirsin
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(
                        ForgotPasswordActivity.this,
                        "Operation failed: " + e.getMessage(),
                        Toast.LENGTH_LONG
                ).show());
    }


}