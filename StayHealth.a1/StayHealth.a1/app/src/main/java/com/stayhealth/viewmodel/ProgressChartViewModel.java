package com.stayhealth.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.stayhealth.model.DailyCaloriesEntry;
import com.stayhealth.repository.ProgressRepository;

import java.util.ArrayList;
import java.util.List;

public class ProgressChartViewModel extends ViewModel {

    public static class ProgressState {
        public final boolean loading;
        public final String error;
        public final List<DailyCaloriesEntry> entries;

        public ProgressState(boolean loading, String error, List<DailyCaloriesEntry> entries) {
            this.loading = loading;
            this.error = error;
            this.entries = entries;
        }
    }

    private final ProgressRepository repo = new ProgressRepository();
    private final MutableLiveData<ProgressState> state = new MutableLiveData<>(new ProgressState(false, null, new ArrayList<>()));

    public LiveData<ProgressState> getState() {
        return state;
    }

    public void load(String uid, int daysBack) {
        if (uid == null || uid.isEmpty()) {
            state.setValue(new ProgressState(false, "User not logged in", new ArrayList<>()));
            return;
        }
        state.setValue(new ProgressState(true, null, currentEntries()));
        repo.fetchLastNDays(uid, daysBack,
                list -> state.postValue(new ProgressState(false, null, list)),
                err -> state.postValue(new ProgressState(false, err, new ArrayList<>())));
    }

    private List<DailyCaloriesEntry> currentEntries() {
        ProgressState cur = state.getValue();
        return cur == null || cur.entries == null ? new ArrayList<>() : cur.entries;
    }
}
