package com.stayhealth.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.stayhealth.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {

    private List<FoodItem> items = new ArrayList<>();
    private Set<String> selectedIds = new HashSet<>();

    public FoodAdapter(List<FoodItem> items) {
        if (items != null) {
            this.items = new ArrayList<>(items); // referans kopyalama
        } else {
            this.items = new ArrayList<>();
        }
    }

    public void updateList(List<FoodItem> newItems) {
        this.items.clear();
        if (newItems != null) {
            this.items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    public Set<String> getSelectedIds() {
        return selectedIds;
    }

    // Dashboard’a gönderirken seçili FoodItem’ları almamız için:
    public List<FoodItem> getSelectedItems() {
        List<FoodItem> selected = new ArrayList<>();
        for (FoodItem item : items) {
            if (selectedIds.contains(item.getId())) {
                selected.add(item);
            }
        }
        return selected;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_food, parent, false);
        return new FoodViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        FoodItem item = items.get(position);

        holder.txtFoodName.setText(item.getName());

        double grams = item.getSelectedGrams();
        if (grams <= 0) grams = item.getGrams();

        double selectedKcal = item.getSelectedCalories();

        // Alt satır: "1 cup (250 ml) · 75 kcal · P:6.3g C:0.6g F:5.3g"
        String detail = item.getPortion()
                + " · " + (int) selectedKcal + " kcal"
                + " · P:" + item.getProtein() + "g"
                + " C:" + item.getCarbs() + "g"
                + " F:" + item.getFat() + "g";
        holder.txtFoodDetail.setText(detail);

        // Gram miktarı
        holder.txtQuantity.setText((int) grams + " g") ;

        // Checkbox state
        boolean isSelected = selectedIds.contains(item.getId());
        holder.chkSelect.setOnCheckedChangeListener(null);
        holder.chkSelect.setChecked(isSelected);

        holder.chkSelect.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedIds.add(item.getId());
            } else {
                selectedIds.remove(item.getId());
            }
        });

        // Satıra tıklayınca da checkbox togglesın:
        holder.itemView.setOnClickListener(v -> {
            boolean currentlySelected = selectedIds.contains(item.getId());
            if (currentlySelected) {
                selectedIds.remove(item.getId());
                holder.chkSelect.setChecked(false);
            } else {
                selectedIds.add(item.getId());
                holder.chkSelect.setChecked(true);
            }
        });

        // Gram -/+ butonları
        holder.btnMinus.setOnClickListener(v -> {
            double g = item.getSelectedGrams();
            if (g <= 0) g = item.getGrams();
            g -= 10;
            if (g < 0) g = 0;
            item.setSelectedGrams(g);
            notifyItemChanged(position);
        });

        holder.btnPlus.setOnClickListener(v -> {
            double g = item.getSelectedGrams();
            if (g <= 0) g = item.getGrams();
            g += 10;
            item.setSelectedGrams(g);
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class FoodViewHolder extends RecyclerView.ViewHolder {

        TextView txtFoodName;
        TextView txtFoodDetail;
        CheckBox chkSelect;
        TextView txtQuantity;     // "250 g"
        ImageButton btnMinus;
        ImageButton btnPlus;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            txtFoodName   = itemView.findViewById(R.id.txtFoodName);
            txtFoodDetail = itemView.findViewById(R.id.txtFoodDetail);
            chkSelect     = itemView.findViewById(R.id.chkSelect);
            txtQuantity   = itemView.findViewById(R.id.txtQuantity);
            btnMinus      = itemView.findViewById(R.id.btnMinus);
            btnPlus       = itemView.findViewById(R.id.btnPlus);
        }
    }
}
