package com.stayhealth.ui.consultant;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.stayhealth.R;
import com.stayhealth.util.FirebaseService;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

public class ConsultantListActivity extends AppCompatActivity {

    // ✅ değişkenler tanımlı
    private ImageView btnBack;
    private TextView btnAll, btnActive, btnPast, btnAdd;
    private AppCompatEditText edtSearch;
    private RecyclerView recyclerConsultants;

    private ConsultantAdapter adapter;
    private final List<ConsultantModel> allConsultants = new ArrayList<>();
    private String currentFilter = "all"; // all | active | past

    private DatabaseReference rootRef;
    private String dieticianUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultant_list);

        // ✅ id’leri XML’de aynen olmalı
        btnBack = findViewById(R.id.btnBack);
        btnAll = findViewById(R.id.btnAll);
        btnActive = findViewById(R.id.btnActive);
        btnPast = findViewById(R.id.btnPast);
        btnAdd = findViewById(R.id.btnAdd);
        edtSearch = findViewById(R.id.edtSearch);
        recyclerConsultants = findViewById(R.id.recyclerConsultants);

        recyclerConsultants.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ConsultantAdapter(new ArrayList<>(), model -> {
            Intent i = new Intent(this, ConsultantDefinitionActivity.class);
            i.putExtra("client_uid", model.getUid());
            startActivity(i);
        });
        recyclerConsultants.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());
        btnAdd.setOnClickListener(v ->
                Toast.makeText(this, "+ Add consultant (yakında)", Toast.LENGTH_SHORT).show()
        );

        btnAll.setOnClickListener(v -> { currentFilter = "all"; applyFilterUI(); applyFilters(); });
        btnActive.setOnClickListener(v -> { currentFilter = "active"; applyFilterUI(); applyFilters(); });
        btnPast.setOnClickListener(v -> { currentFilter = "past"; applyFilterUI(); applyFilters(); });

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { applyFilters(); }
            @Override public void afterTextChanged(Editable s) {}
        });

        rootRef = FirebaseService.getDatabase().getReference();

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(this, "Önce giriş yapmalısın.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        dieticianUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        applyFilterUI();
        loadConsultantsFromFirebase();
    }

    private void loadConsultantsFromFirebase() {
        allConsultants.clear();
        adapter.submitList(new ArrayList<>());

        rootRef.child("dieticians")
                .child(dieticianUid)
                .child("consultants")
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (!snapshot.exists()) {
                        Toast.makeText(this, "Henüz danışanın yok.", Toast.LENGTH_SHORT).show();
                        applyFilters();
                        return;
                    }

                    List<String> clientUids = new ArrayList<>();
                    for (DataSnapshot child : snapshot.getChildren()) {
                        clientUids.add(child.getKey());
                    }

                    for (String clientUid : clientUids) {
                        rootRef.child("users").child(clientUid).get()
                                .addOnSuccessListener(userSnap -> {
                                    String name = userSnap.child("name").getValue(String.class);
                                    String status = userSnap.child("status").getValue(String.class);

                                    if (name == null) name = "Unknown";
                                    if (status == null) status = "active";

                                    allConsultants.add(new ConsultantModel(
                                            clientUid, name, status, R.drawable.ic_profile
                                    ));
                                    applyFilters();
                                });
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Danışanlar okunamadı: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }

    private void applyFilters() {
        String q = edtSearch.getText() != null ? edtSearch.getText().toString().trim().toLowerCase() : "";

        List<ConsultantModel> filtered = new ArrayList<>();
        for (ConsultantModel c : allConsultants) {

            if (!"all".equals(currentFilter)) {
                if (c.getStatus() == null || !currentFilter.equalsIgnoreCase(c.getStatus())) continue;
            }

            if (!q.isEmpty()) {
                if (c.getName() == null || !c.getName().toLowerCase().contains(q)) continue;
            }

            filtered.add(c);
        }

        adapter.submitList(filtered);
    }

    private void applyFilterUI() {
        // ✅ drawable’lara mutlaka R.drawable ile eriş
        int sel = R.drawable.bg_filter_selected;
        int unsel = R.drawable.bg_filter_unselected;

        btnAll.setBackgroundResource("all".equals(currentFilter) ? sel : unsel);
        btnActive.setBackgroundResource("active".equals(currentFilter) ? sel : unsel);
        btnPast.setBackgroundResource("past".equals(currentFilter) ? sel : unsel);
    }
}
