package com.stayhealth.repository;

import android.content.res.AssetManager;

import com.stayhealth.ui.home.FoodItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Handles meal/food loading and filtering.
 */
public class MealRepository {

    public List<FoodItem> loadFoods(AssetManager assets) throws IOException, JSONException {
        String jsonStr = readJsonFromAssets(assets, "food_data.json");
        JSONObject root = new JSONObject(jsonStr);
        if (!root.has("foods")) return new ArrayList<>();

        JSONArray foodsArray = root.getJSONArray("foods");
        List<FoodItem> all = new ArrayList<>();

        for (int i = 0; i < foodsArray.length(); i++) {
            JSONObject obj = foodsArray.getJSONObject(i);
            String id = obj.optString("id", "food_" + i);
            String name = obj.optString("name", "Unknown");
            String category = obj.optString("category", "other");
            String portion = obj.optString("portion", "1 serving");
            double grams = obj.optDouble("grams", 0);
            double calories = obj.optDouble("kcal", obj.optDouble("calories", 0));
            double protein = obj.optDouble("protein_g", obj.optDouble("protein", 0));
            double carbs = obj.optDouble("carb_g", obj.optDouble("carbs", 0));
            double fat = obj.optDouble("fat_g", obj.optDouble("fat", 0));

            List<String> mealTypes = new ArrayList<>();
            JSONArray mealTypesJson = obj.optJSONArray("mealTypes");
            if (mealTypesJson != null) {
                for (int j = 0; j < mealTypesJson.length(); j++) {
                    String mt = mealTypesJson.optString(j);
                    if (mt != null && !mt.trim().isEmpty()) {
                        mealTypes.add(mt.trim());
                    }
                }
            }

            FoodItem item = new FoodItem(id, name, category, portion, grams,
                    calories, protein, carbs, fat, mealTypes);
            all.add(item);
        }
        return all;
    }

    public List<FoodItem> filterByMealType(List<FoodItem> allFoods, String mealType) {
        if (allFoods == null) return new ArrayList<>();
        String mealLower = mealType == null ? "" : mealType.toLowerCase(Locale.ROOT);
        List<FoodItem> filtered = new ArrayList<>();
        for (FoodItem item : allFoods) {
            List<String> mts = item.getMealTypes();
            if (mts == null || mts.isEmpty()) {
                filtered.add(item);
                continue;
            }
            boolean match = false;
            for (String mt : mts) {
                if (mt == null) continue;
                String mtLower = mt.toLowerCase(Locale.ROOT);
                if ("any".equals(mtLower) || mtLower.equals(mealLower)) {
                    match = true;
                    break;
                }
            }
            if (match) filtered.add(item);
        }
        if (filtered.isEmpty()) filtered.addAll(allFoods);
        return filtered;
    }

    public List<FoodItem> search(List<FoodItem> source, String query) {
        if (source == null) return new ArrayList<>();
        String q = query == null ? "" : query.toLowerCase(Locale.ROOT);
        if (q.isEmpty()) return new ArrayList<>(source);
        List<FoodItem> result = new ArrayList<>();
        for (FoodItem item : source) {
            if (item.getName().toLowerCase(Locale.ROOT).contains(q)) {
                result.add(item);
            }
        }
        return result;
    }

    private String readJsonFromAssets(AssetManager assets, String fileName) throws IOException {
        InputStream is = assets.open(fileName);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, StandardCharsets.UTF_8)
        );
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        reader.close();
        is.close();
        return sb.toString();
    }
}
