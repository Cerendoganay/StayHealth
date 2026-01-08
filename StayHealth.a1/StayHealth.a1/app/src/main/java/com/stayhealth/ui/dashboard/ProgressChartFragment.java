package com.stayhealth.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.stayhealth.R;
import com.stayhealth.model.DailyCaloriesEntry;
import com.stayhealth.viewmodel.ProgressChartViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class ProgressChartFragment extends Fragment {

    private Spinner spinnerRange;
    private View barMon, barTue, barWed, barThu, barFri, barSat, barSun;
    private TextView lblMon, lblTue, lblWed, lblThu, lblFri, lblSat, lblSun;
    private TextView txtAvgKcal, txtBestDay, txtWorstDay, txtEmptyChart;
    private ProgressChartViewModel viewModel;
    private int selectedWeekOffset = 0; // 0=this week, 1=last week, etc.

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_progress_chart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ProgressChartViewModel.class);

        ImageView btnBack = view.findViewById(R.id.btnBack);
        if (btnBack != null) btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

        spinnerRange = view.findViewById(R.id.spinnerRange);

        barMon = view.findViewById(R.id.barMon);
        barTue = view.findViewById(R.id.barTue);
        barWed = view.findViewById(R.id.barWed);
        barThu = view.findViewById(R.id.barThu);
        barFri = view.findViewById(R.id.barFri);
        barSat = view.findViewById(R.id.barSat);
        barSun = view.findViewById(R.id.barSun);

        lblMon = view.findViewById(R.id.lblMon);
        lblTue = view.findViewById(R.id.lblTue);
        lblWed = view.findViewById(R.id.lblWed);
        lblThu = view.findViewById(R.id.lblThu);
        lblFri = view.findViewById(R.id.lblFri);
        lblSat = view.findViewById(R.id.lblSat);
        lblSun = view.findViewById(R.id.lblSun);

        txtAvgKcal = view.findViewById(R.id.txtAvgKcal);
        txtBestDay = view.findViewById(R.id.txtBestDay);
        txtWorstDay = view.findViewById(R.id.txtWorstDay);
        txtEmptyChart = view.findViewById(R.id.txtEmptyChart);

        ArrayList<String> weekItems = buildLast4WeeksLabels();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.item_spinner_week_selected, weekItems);
        adapter.setDropDownViewResource(R.layout.item_spinner_week_dropdown);
        spinnerRange.setAdapter(adapter);

        loadWeekData(0);

        viewModel.getState().observe(getViewLifecycleOwner(), state -> {
            if (state == null) return;
            if (state.entries == null || state.entries.isEmpty()) {
                showEmptyState();
            } else {
                hideEmptyState();
                updateFromEntries(state.entries);
            }
        });

        spinnerRange.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                loadWeekData(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private ArrayList<String> buildLast4WeeksLabels() {
        ArrayList<String> items = new ArrayList<>();
        SimpleDateFormat fmt = new SimpleDateFormat("MMM d", Locale.ENGLISH);

        for (int i = 0; i < 4; i++) {
            Calendar start = startOfWeek();
            start.add(Calendar.WEEK_OF_YEAR, -i);

            Calendar end = (Calendar) start.clone();
            end.add(Calendar.DAY_OF_YEAR, 6);

            String title;
            if (i == 0) title = "This Week";
            else if (i == 1) title = "Last Week";
            else title = i + " Weeks Ago";

            items.add(title + " (" + fmt.format(start.getTime()) + " - " + fmt.format(end.getTime()) + ")");
        }
        return items;
    }

    private Calendar startOfWeek() {
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }

    private void loadWeekData(int weekIndex) {
        selectedWeekOffset = weekIndex;
        String uid = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;
        int daysBack = 7 * (weekIndex + 1);
        viewModel.load(uid, daysBack);
    }

    private void updateFromEntries(java.util.List<DailyCaloriesEntry> entries) {
        int[] week = new int[7];
        Calendar start = startOfWeek();
        start.add(Calendar.WEEK_OF_YEAR, -selectedWeekOffset);
        Calendar end = (Calendar) start.clone();
        end.add(Calendar.DAY_OF_YEAR, 6);

        for (DailyCaloriesEntry entry : entries) {
            Calendar c = Calendar.getInstance();
            try {
                java.text.SimpleDateFormat fmt = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US);
                c.setTime(fmt.parse(entry.date));
            } catch (Exception e) {
                continue;
            }
            if (c.before(start) || c.after(end)) continue;
            int dow = c.get(Calendar.DAY_OF_WEEK);
            int idx = (dow == Calendar.SUNDAY) ? 6 : dow - Calendar.MONDAY;
            if (idx >= 0 && idx < 7) week[idx] += entry.calories;
        }

        updateBars(week);
        updateSummary(week);
    }

    private void updateBars(int[] weekData) {
        if (weekData == null || weekData.length != 7) return;

        int max = 0;
        for (int v : weekData) max = Math.max(max, v);
        int maxReference = Math.max(max, 2000);
        int maxHeightPx = dpToPx(160);

        View[] bars = {barMon, barTue, barWed, barThu, barFri, barSat, barSun};
        TextView[] labels = {lblMon, lblTue, lblWed, lblThu, lblFri, lblSat, lblSun};
        int bestIdx = indexOfMax(weekData);

        for (int i = 0; i < bars.length; i++) {
            View bar = bars[i];
            if (bar == null) continue;
            float ratio = maxReference == 0 ? 0 : Math.min(1f, weekData[i] / (float) maxReference);
            int h = (int) Math.max(dpToPx(12), ratio * maxHeightPx);
            bar.getLayoutParams().height = h;
            bar.requestLayout();
            bar.setBackgroundColor(requireContext().getColor(i == bestIdx ? R.color.brand_primary : R.color.brand_primary_light));
        }

        for (int i = 0; i < labels.length; i++) {
            TextView tv = labels[i];
            if (tv == null) continue;
            tv.setTypeface(null, i == bestIdx ? android.graphics.Typeface.BOLD : android.graphics.Typeface.NORMAL);
            tv.setTextColor(requireContext().getColor(i == bestIdx ? R.color.text_primary : R.color.text_secondary));
        }
    }

    private void updateSummary(int[] weekData) {
        if (weekData == null || weekData.length == 0) return;
        int sum = 0; int maxIdx = 0; int minIdx = 0;
        for (int i = 0; i < weekData.length; i++) {
            sum += weekData[i];
            if (weekData[i] > weekData[maxIdx]) maxIdx = i;
            if (weekData[i] < weekData[minIdx]) minIdx = i;
        }
        int avg = weekData.length == 0 ? 0 : sum / weekData.length;
        if (txtAvgKcal != null) txtAvgKcal.setText(avg + " kcal");
        if (txtBestDay != null) txtBestDay.setText(dayName(maxIdx));
        if (txtWorstDay != null) txtWorstDay.setText(dayName(minIdx));
    }

    private int indexOfMax(int[] arr) {
        int idx = 0;
        for (int i = 1; i < arr.length; i++) if (arr[i] > arr[idx]) idx = i;
        return idx;
    }

    private String dayName(int idx) {
        String[] names = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        if (idx < 0 || idx >= names.length) return "";
        return names[idx];
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return (int) (dp * density);
    }

    private void showEmptyState() {
        if (txtEmptyChart != null) txtEmptyChart.setVisibility(View.VISIBLE);
    }

    private void hideEmptyState() {
        if (txtEmptyChart != null) txtEmptyChart.setVisibility(View.GONE);
    }
}
