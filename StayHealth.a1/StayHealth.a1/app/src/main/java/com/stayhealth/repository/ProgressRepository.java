package com.stayhealth.repository;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.stayhealth.model.DailyCaloriesEntry;
import com.stayhealth.util.FirebaseService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

/**
 * Stores and retrieves per-day calorie totals per user.
 * Path: /users/{uid}/dailyCalories/{date} -> totalCalories
 */
public class ProgressRepository {

    private final FirebaseDatabase db = FirebaseService.getDatabase();
    private final SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    public void saveDailyCalories(@NonNull String uid, @NonNull String dateYmd, int calories) {
        DatabaseReference ref = db.getReference("users").child(uid).child("dailyCalories").child(dateYmd);
        ref.setValue(calories);
    }

    /**
     * Fetch last N days (inclusive of today) totals. Returns empty list if none.
     */
    public void fetchLastNDays(@NonNull String uid, int daysBack,
                               Consumer<List<DailyCaloriesEntry>> onSuccess,
                               Consumer<String> onError) {
        db.getReference("users").child(uid).child("dailyCalories")
                .get()
                .addOnSuccessListener(snapshot -> onSuccess.accept(parseAndFilter(snapshot, daysBack)))
                .addOnFailureListener(e -> onError.accept(e.getMessage()));
    }

    private List<DailyCaloriesEntry> parseAndFilter(DataSnapshot snapshot, int daysBack) {
        List<DailyCaloriesEntry> list = new ArrayList<>();
        Calendar cutoff = Calendar.getInstance();
        cutoff.add(Calendar.DAY_OF_YEAR, -(daysBack - 1)); // inclusive window

        for (DataSnapshot child : snapshot.getChildren()) {
            String dateKey = child.getKey();
            if (dateKey == null) continue;
            long val = 0;
            try { val = child.getValue(Long.class); } catch (Exception ignored) {}
            int calories = (int) val;
            if (calories < 0) calories = 0;

            Calendar entryCal = Calendar.getInstance();
            try {
                entryCal.setTime(ymd.parse(dateKey));
            } catch (ParseException e) {
                continue;
            }
            if (entryCal.before(cutoff)) continue;
            list.add(new DailyCaloriesEntry(dateKey, calories));
        }

        Collections.sort(list, Comparator.comparing(e -> e.date));
        return list;
    }
}
