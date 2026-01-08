package com.stayhealth.model;

/** Simple model for daily calorie totals persisted per user. */
public class DailyCaloriesEntry {
    public String date; // yyyy-MM-dd
    public int calories;

    public DailyCaloriesEntry() { }

    public DailyCaloriesEntry(String date, int calories) {
        this.date = date;
        this.calories = calories;
    }
}
