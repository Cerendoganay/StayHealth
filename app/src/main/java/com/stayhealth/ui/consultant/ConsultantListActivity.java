package com.stayhealth.ui.consultant;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.stayhealth.R;
import com.stayhealth.ui.auth.SignUpActivity;

import java.util.ArrayList;
import java.util.List;

public class ConsultantListActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private ConsultantAdapter adapter;
    private List<Consultant> consultantList = new ArrayList<>();

    private Button btnFilterAll, btnFilterActive, btnFilterPast;
    private EditText edtSearch;
    private TextView txtAddConsultant;
    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultant_list);

        db = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.recyclerConsultants);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ConsultantAdapter(this, consultantList);
        recyclerView.setAdapter(adapter);

        btnFilterAll    = findViewById(R.id.btnFilterAll);
        btnFilterActive = findViewById(R.id.btnFilterActive);
        btnFilterPast   = findViewById(R.id.btnFilterPast);
        edtSearch       = findViewById(R.id.edtSearch);
        txtAddConsultant= findViewById(R.id.txtAddConsultant);
        btnBack         = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        btnFilterAll.setOnClickListener(v -> adapter.filterByStatus(null));
        btnFilterActive.setOnClickListener(v -> adapter.filterByStatus("active"));
        btnFilterPast.setOnClickListener(v -> adapter.filterByStatus("past"));

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s,int start,int count,int after){}
            @Override public void onTextChanged(CharSequence s,int start,int before,int count){
                adapter.filterByName(s.toString());
            }
            @Override public void afterTextChanged(Editable s){}
        });

        txtAddConsultant.setOnClickListener(v -> {
            Intent i = new Intent(this, SignUpActivity.class);
            i.putExtra("user_type", "client");
            startActivity(i);
        });

        loadConsultants();
    }

    private void loadConsultants() {
        db.collection("users")
                .whereEqualTo("role", "client")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    consultantList.clear();

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        String id = doc.getId();
                        String name = doc.getString("name");
                        String email = doc.getString("email");
                        String status = doc.getString("status");
                        if (status == null) status = "active";

                        Consultant c = new Consultant(id, name, email, status);

                        Long age = doc.getLong("age");
                        Double weight = doc.getDouble("weight");
                        Double height = doc.getDouble("height");
                        String chronic = doc.getString("chronicIllness");
                        Long kcal = doc.getLong("kcal");

                        if (age != null) c.setAge(age.intValue());
                        if (weight != null) c.setWeight(weight);
                        if (height != null) c.setHeight(height);
                        if (chronic != null) c.setChronicIllness(chronic);
                        if (kcal != null) c.setKcal(kcal.intValue());

                        consultantList.add(c);
                    }

                    adapter.updateData(consultantList);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to load consultants: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}
