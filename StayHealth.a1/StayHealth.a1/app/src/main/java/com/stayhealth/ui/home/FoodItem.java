package com.stayhealth.ui.home;

import java.io.Serializable;
import java.util.List;

public class FoodItem implements Serializable {

    private String id;
    private String name;
    private String category;
    private String portion;   // "1 cup (250 ml)" gibi
    private double grams;     // JSON’daki temel gram
    private double calories;  // JSON’daki temel kalori (bu gram için)
    private double protein;
    private double carbs;
    private double fat;
    private List<String> mealTypes; // ["Breakfast", "Snack", "Any"] gibi

    // Kullanıcının seçtiği gram (değiştirilebilir)
    private double selectedGrams;

    public FoodItem(String id,
                    String name,
                    String category,
                    String portion,
                    double grams,
                    double calories,
                    double protein,
                    double carbs,
                    double fat,
                    List<String> mealTypes) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.portion = portion;
        this.grams = grams;
        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.fat = fat;
        this.mealTypes = mealTypes;
        this.selectedGrams = grams; // varsayılan: temel gram
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getPortion() {
        return portion;
    }

    public double getGrams() {
        return grams;
    }

    public double getCalories() {
        return calories;
    }

    public double getProtein() {
        return protein;
    }

    public double getCarbs() {
        return carbs;
    }

    public double getFat() {
        return fat;
    }

    public List<String> getMealTypes() {
        return mealTypes;
    }

    public double getSelectedGrams() {
        return selectedGrams;
    }

    public void setSelectedGrams(double selectedGrams) {
        this.selectedGrams = selectedGrams;
    }

    /**
     * Seçilen gram’a göre kalori:
     * scaledCalories = baseCalories * selectedGrams / baseGrams
     */
    public double getSelectedCalories() {
        double baseGrams = (grams <= 0) ? 1 : grams;
        double sg = (selectedGrams <= 0) ? grams : selectedGrams;
        return calories * sg / baseGrams;
    }

}