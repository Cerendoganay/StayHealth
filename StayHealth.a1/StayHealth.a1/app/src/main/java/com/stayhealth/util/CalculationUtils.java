package com.stayhealth.util;

/**
 * Small pure-Java utility helpers for calculations and validations.
 */
public final class CalculationUtils {

    private CalculationUtils() { }

    /**
     * Calculates BMI using kg and cm.
     * Returns NaN if inputs are non-positive.
     */
    public static double calculateBmi(double weightKg, double heightCm) {
        if (weightKg <= 0 || heightCm <= 0) {
            return Double.NaN;
        }
        double heightM = heightCm / 100.0;
        return weightKg / (heightM * heightM);
    }

    /**
     * Simple text validation: non-null, non-empty after trimming.
     */
    public static boolean isNonEmpty(String text) {
        return text != null && !text.trim().isEmpty();
    }
}
