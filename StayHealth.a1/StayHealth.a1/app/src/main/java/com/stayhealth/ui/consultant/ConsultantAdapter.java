package com.stayhealth.ui.consultant;

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

public class ConsultantAdapter extends RecyclerView.Adapter<ConsultantAdapter.VH> {

    public interface OnItemClickListener {
        void onClick(ConsultantModel model);
    }

    private final List<ConsultantModel> items = new ArrayList<>();
    private final OnItemClickListener listener;

    public ConsultantAdapter(List<ConsultantModel> initial, OnItemClickListener listener) {
        if (initial != null) items.addAll(initial);
        this.listener = listener;
    }

    public void submitList(List<ConsultantModel> newItems) {
        items.clear();
        if (newItems != null) items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_consultant, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        ConsultantModel m = items.get(position);

        h.txtName.setText(m.getName() != null ? m.getName() : "Unknown");
        h.imgAvatar.setImageResource(m.getAvatarRes());

        String status = m.getStatus() != null ? m.getStatus().toLowerCase() : "active";
        if ("past".equals(status)) {
            h.txtStatus.setText("Past");
            h.txtStatus.setBackgroundResource(R.drawable.bg_status_past);
        } else {
            h.txtStatus.setText("Active");
            h.txtStatus.setBackgroundResource(R.drawable.bg_status_active);
        }

        h.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(m);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView imgAvatar;
        TextView txtName, txtStatus;

        VH(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            txtName = itemView.findViewById(R.id.txtName);
            txtStatus = itemView.findViewById(R.id.txtStatus);
        }
    }
}
