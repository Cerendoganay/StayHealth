package com.stayhealth.ui.consultant;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.stayhealth.R;
import com.stayhealth.util.FirebaseService;
import com.google.firebase.database.DatabaseReference;

public class ConsultantDefinitionActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextView txtName;

    private TextView txtAgeValue, txtWeightValue, txtHeightValue, txtChronicValue, txtKcalValue;

    private TextView btnGoDietList, btnDelete, btnAddComment;

    private String clientUid;
    private DatabaseReference rootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultant_definition);

        btnBack = findViewById(R.id.btnBack);
        txtName = findViewById(R.id.txtName);

        // row value id’leri (aşağıdaki row xml’lerle uyumlu)
        txtAgeValue = findViewById(R.id.txtAgeValue);
        txtWeightValue = findViewById(R.id.txtWeightValue);
        txtHeightValue = findViewById(R.id.txtHeightValue);
        txtChronicValue = findViewById(R.id.txtChronicValue);
        txtKcalValue = findViewById(R.id.txtKcalValue);

        btnGoDietList = findViewById(R.id.btnGoDietList);
        btnDelete = findViewById(R.id.btnDelete);
        btnAddComment = findViewById(R.id.btnAddComment);

        rootRef = FirebaseService.getDatabase().getReference();

        clientUid = getIntent().getStringExtra("client_uid");
        if (TextUtils.isEmpty(clientUid)) {
            Toast.makeText(this, "client_uid gelmedi.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        btnBack.setOnClickListener(v -> finish());

        loadClientInfo(); // users/{uid} içinden dolduracağız

        btnGoDietList.setOnClickListener(v -> {
            // Buradan Diet List ekranına gideceksin (senin activity adına göre değiştir)
            // Intent i = new Intent(this, DietListActivity.class);
            // i.putExtra("client_uid", clientUid);
            // startActivity(i);
            Toast.makeText(this, "Diet List (yakında)", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadClientInfo() {
        rootRef.child("users").child(clientUid).get()
                .addOnSuccessListener(snap -> {
                    String name = snap.child("name").getValue(String.class);
                    if (name == null) name = "Consultant";
                    txtName.setText(name);

                    // Bunları users içine yazacağız:
                    // age, weight, height, chronic, kcal
                    String age = String.valueOf(snap.child("age").getValue());
                    String weight = String.valueOf(snap.child("weight").getValue());
                    String height = String.valueOf(snap.child("height").getValue());
                    String chronic = String.valueOf(snap.child("chronic").getValue());
                    String kcal = String.valueOf(snap.child("kcal").getValue());

                    txtAgeValue.setText(clean(age));
                    txtWeightValue.setText(clean(weight) + " kg");
                    txtHeightValue.setText(clean(height) + " cm");
                    txtChronicValue.setText(clean(chronic));
                    txtKcalValue.setText(clean(kcal) + " kcal");
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "User okunamadı: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }

    private String clean(String v) {
        if (v == null || "null".equals(v)) return "-";
        return v;
    }
}
