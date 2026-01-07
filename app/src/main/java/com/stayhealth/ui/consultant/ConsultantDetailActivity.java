package com.stayhealth.ui.consultant;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.stayhealth.R;

public class ConsultantDetailActivity extends AppCompatActivity {

    private String consultantId;

    private TextView txtNameDetail, txtAge, txtWeight, txtHeight, txtChronic, txtKcal;
    private Button btnGoDietList, btnDelete, btnAddComment;
    private ImageView btnBack;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultant_detail);

        db = FirebaseFirestore.getInstance();

        consultantId = getIntent().getStringExtra("id");

        txtNameDetail = findViewById(R.id.txtNameDetail);
        txtAge   = findViewById(R.id.txtAge);
        txtWeight= findViewById(R.id.txtWeight);
        txtHeight= findViewById(R.id.txtHeight);
        txtChronic = findViewById(R.id.txtChronic);
        txtKcal = findViewById(R.id.txtKcal);
        btnGoDietList = findViewById(R.id.btnGoDietList);
        btnDelete = findViewById(R.id.btnDelete);
        btnAddComment = findViewById(R.id.btnAddComment);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        // Intent ile gelen verileri göster (Firestore’dan da güncelleyebiliriz)
        String name   = getIntent().getStringExtra("name");
        String status = getIntent().getStringExtra("status");
        int age       = getIntent().getIntExtra("age", -1);
        double weight = getIntent().getDoubleExtra("weight", -1);
        double height = getIntent().getDoubleExtra("height", -1);
        String chronic= getIntent().getStringExtra("chronic");
        int kcal      = getIntent().getIntExtra("kcal", -1);

        if (name != null) txtNameDetail.setText(name);

        txtAge.setText(age > 0 ? String.valueOf(age) : "-");
        txtWeight.setText(weight > 0 ? weight + " kg" : "-");
        txtHeight.setText(height > 0 ? (int)height + " cm" : "-");
        txtChronic.setText(chronic != null ? chronic : "-");
        txtKcal.setText(kcal > 0 ? kcal + " kcal" : "-");

        btnGoDietList.setOnClickListener(v ->
                Toast.makeText(this, "Diet list ekranını sonra bağlarız", Toast.LENGTH_SHORT).show()
        );

        btnAddComment.setOnClickListener(v ->
                Toast.makeText(this, "Yorum ekleme ekranı daha sonra", Toast.LENGTH_SHORT).show()
        );

        btnDelete.setOnClickListener(v -> deleteConsultant());
    }

    private void deleteConsultant() {
        if (consultantId == null) {
            Toast.makeText(this, "Unknown consultant", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("users").document(consultantId)
                .delete()
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Consultant deleted", Toast.LENGTH_SHORT).show();
                    finish(); // listeye geri dön
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Delete failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}
