package com.stayhealth;

import com.stayhealth.util.CalculationUtils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CalculationUtilsTest {

    @Test
    public void calculateBmi_validInputs_returnsExpectedValue() {
        double bmi = CalculationUtils.calculateBmi(70, 175); // 70kg, 1.75m -> 22.86
        assertEquals(22.86, bmi, 0.01);
    }

    @Test
    public void calculateBmi_zeroOrNegative_returnsNaN() {
        assertTrue(Double.isNaN(CalculationUtils.calculateBmi(0, 175)));
        assertTrue(Double.isNaN(CalculationUtils.calculateBmi(70, 0)));
        assertTrue(Double.isNaN(CalculationUtils.calculateBmi(-5, 180)));
    }

    @Test
    public void isNonEmpty_handlesNormalText() {
        assertTrue(CalculationUtils.isNonEmpty("Hello"));
        assertTrue(CalculationUtils.isNonEmpty("  spaced  "));
    }

    @Test
    public void isNonEmpty_handlesEmptyOrNull() {
        assertFalse(CalculationUtils.isNonEmpty(""));
        assertFalse(CalculationUtils.isNonEmpty("   "));
        assertFalse(CalculationUtils.isNonEmpty(null));
    }
}
