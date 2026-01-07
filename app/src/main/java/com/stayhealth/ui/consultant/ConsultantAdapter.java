package com.stayhealth.ui.consultant;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.stayhealth.R;

import java.util.ArrayList;
import java.util.List;

public class ConsultantAdapter extends RecyclerView.Adapter<ConsultantAdapter.ViewHolder> {

    private Context context;
    private List<Consultant> consultantList;   // ekranda görünen liste
    private List<Consultant> fullList;         // asıl liste (search/filter için)

    public ConsultantAdapter(Context context, List<Consultant> consultantList) {
        this.context = context;
        this.consultantList = consultantList;
        this.fullList = new ArrayList<>(consultantList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_consultant, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Consultant c = consultantList.get(position);

        holder.txtName.setText(c.getName() != null ? c.getName() : "Unknown");
        String status = c.getStatus() != null ? c.getStatus() : "active";
        holder.txtStatus.setText(status.equals("past") ? "Past" : "Active");

        if (status.equals("past")) {
            holder.statusPill.setBackgroundResource(R.drawable.bg_status_past);
        } else {
            holder.statusPill.setBackgroundResource(R.drawable.bg_status_active);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ConsultantDetailActivity.class);
            intent.putExtra("id", c.getId());
            intent.putExtra("name", c.getName());
            intent.putExtra("age", c.getAge());
            intent.putExtra("weight", c.getWeight());
            intent.putExtra("height", c.getHeight());
            intent.putExtra("chronic", c.getChronicIllness());
            intent.putExtra("kcal", c.getKcal());
            intent.putExtra("email", c.getEmail());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return consultantList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgAvatar;
        TextView txtName, txtStatus;
        View statusPill;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar  = itemView.findViewById(R.id.imgAvatar);
            txtName    = itemView.findViewById(R.id.txtName);
            txtStatus  = itemView.findViewById(R.id.txtStatusLabel);
            statusPill = itemView.findViewById(R.id.statusPill);
        }
    }

    // 🔄 Firestore’dan veri yenilenince
    public void updateData(List<Consultant> newList) {
        consultantList.clear();
        consultantList.addAll(newList);

        fullList.clear();
        fullList.addAll(newList);

        notifyDataSetChanged();
    }

    // 🔍 Search
    public void filterByName(String text) {
        consultantList.clear();

        if (text == null || text.isEmpty()) {
            consultantList.addAll(fullList);
        } else {
            text = text.toLowerCase();
            for (Consultant c : fullList) {
                if (c.getName() != null &&
                        c.getName().toLowerCase().contains(text)) {
                    consultantList.add(c);
                } else if (c.getEmail() != null &&
                        c.getEmail().toLowerCase().contains(text)) {
                    consultantList.add(c);
                }
            }
        }
        notifyDataSetChanged();
    }

    // 🟢 Status filter
    public void filterByStatus(String status) {
        consultantList.clear();

        if (status == null) { // All
            consultantList.addAll(fullList);
        } else {
            for (Consultant c : fullList) {
                if (status.equalsIgnoreCase(c.getStatus())) {
                    consultantList.add(c);
                }
            }
        }
        notifyDataSetChanged();
    }
}
