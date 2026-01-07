package com.stayhealth.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.stayhealth.R;
import com.stayhealth.ui.home.DashboardActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EmailVerifyActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verify);

        mAuth = FirebaseAuth.getInstance();

        ImageView btnBack   = findViewById(R.id.btnBack);
        TextView txtResend  = findViewById(R.id.txtResend);
        // XML'deki buton id'si:
        TextView btnVerify  = findViewById(R.id.btnVerify);

        // 🔙 Geri → Logout + Login'e dön
        btnBack.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(EmailVerifyActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        // 🔁 Resend → tekrar doğrulama maili gönder
        txtResend.setOnClickListener(v -> resendVerificationEmail());

        // ✅ Verify → gerçekten verified mı kontrol et
        btnVerify.setOnClickListener(v -> checkIfVerifiedAndGo());
    }

    private void resendVerificationEmail() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this,
                    "No active session. Please log in again.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        user.sendEmailVerification()
                .addOnSuccessListener(unused ->
                        Toast.makeText(
                                EmailVerifyActivity.this,
                                "Verification email has been resent.",
                                Toast.LENGTH_LONG
                        ).show()
                )
                .addOnFailureListener(e ->
                        Toast.makeText(
                                EmailVerifyActivity.this,
                                "Failed to resend email: " + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show()
                );
    }

    private void checkIfVerifiedAndGo() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this,
                    "User not found. Please log in again.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        // Son durumu almak için reload
        user.reload()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Toast.makeText(this,
                                "Status check failed: " +
                                        (task.getException() != null
                                                ? task.getException().getMessage()
                                                : ""),
                                Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (user.isEmailVerified()) {
                        Toast.makeText(this,
                                "Email verified successfully!",
                                Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(EmailVerifyActivity.this, DashboardActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        Toast.makeText(this,
                                "Email is not verified yet. Please click the link in your email.",
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}
