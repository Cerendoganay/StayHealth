package com.stayhealth.viewmodel;

import android.content.res.AssetManager;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.stayhealth.repository.MealRepository;
import com.stayhealth.ui.home.FoodItem;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * MVVM ViewModel for meal selection screen.
 */
public class MealSelectionViewModel extends ViewModel {

    public static class MealState {
        public final boolean loading;
        public final String error;
        public final List<FoodItem> items;

        public MealState(boolean loading, String error, List<FoodItem> items) {
            this.loading = loading;
            this.error = error;
            this.items = items;
        }
    }

    private final MealRepository repo = new MealRepository();
    private final MutableLiveData<MealState> state = new MutableLiveData<>(new MealState(false, null, new ArrayList<>()));

    private List<FoodItem> allFoods = new ArrayList<>();
    private List<FoodItem> mealFiltered = new ArrayList<>();
    private String currentMealType = "Breakfast";

    public LiveData<MealState> getState() {
        return state;
    }

    public void setMealType(String mealType) {
        this.currentMealType = mealType == null ? "Breakfast" : mealType;
        applyMealFilter(null);
    }

    public void loadFoods(AssetManager assets, String mealType, String initialQuery) {
        state.setValue(new MealState(true, null, state.getValue() != null ? state.getValue().items : new ArrayList<>()));
        currentMealType = mealType == null ? "Breakfast" : mealType;
        try {
            allFoods = repo.loadFoods(assets);
            mealFiltered = repo.filterByMealType(allFoods, currentMealType);
            List<FoodItem> display = repo.search(mealFiltered, initialQuery);
            state.postValue(new MealState(false, null, display));
        } catch (IOException | JSONException e) {
            state.postValue(new MealState(false, e.getMessage(), new ArrayList<>()));
        }
    }

    public void applySearch(String query) {
        applyMealFilter(query);
    }

    private void applyMealFilter(String query) {
        mealFiltered = repo.filterByMealType(allFoods, currentMealType);
        List<FoodItem> display = repo.search(mealFiltered, query == null ? "" : query);
        state.postValue(new MealState(false, null, display));
    }
}
