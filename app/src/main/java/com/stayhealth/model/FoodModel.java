package com.stayhealth.model;

public class FoodModel {
    private String name;
    private double kcal_per_gram;

    public FoodModel() {}

    public FoodModel(String name, double kcal_per_gram) {
        this.name = name;
        this.kcal_per_gram = kcal_per_gram;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getKcal_per_gram() { return kcal_per_gram; }
    public void setKcal_per_gram(double kcal_per_gram) { this.kcal_per_gram = kcal_per_gram; }
}
