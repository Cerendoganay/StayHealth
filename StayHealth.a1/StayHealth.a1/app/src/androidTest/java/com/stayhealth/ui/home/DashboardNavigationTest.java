package com.stayhealth.ui.home;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.stayhealth.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

/**
 * Verifies that the bottom navigation in DashboardActivity swaps fragments
 * and renders the expected UI for each destination. Uses only local UI
 * interactions (no network/mock dependencies).
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class DashboardNavigationTest {

    @Rule
    public ActivityScenarioRule<DashboardActivity> activityRule =
            new ActivityScenarioRule<>(DashboardActivity.class);

    @Test
    public void bottomNav_switchesFragmentsAndShowsContent() {
        // Default launch should show the home fragment (ClientHomeFragment)
        onView(withId(R.id.txtDailyKcal)).check(matches(isDisplayed()));

        // Navigate to Progress/Chart and verify its unique view
        onView(withId(R.id.navChart)).perform(click());
        onView(withId(R.id.txtProgressTitle)).check(matches(isDisplayed()));

        // Navigate to Profile and verify a form field is visible
        onView(withId(R.id.navProfile)).perform(click());
        onView(withId(R.id.edtAge)).check(matches(isDisplayed()));

        // Navigate back to Home to ensure we can return
        onView(withId(R.id.navChecklist)).perform(click());
        onView(withId(R.id.txtDailyKcal)).check(matches(isDisplayed()));
    }
}
