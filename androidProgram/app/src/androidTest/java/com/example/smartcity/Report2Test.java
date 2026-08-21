package com.example.smartcity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import com.example.smartcity.tools.User;
import com.example.smartcity.tools.UserCallback;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Test class for Report2 Activity.
 * Simulates user login, ensures data is loaded, and verifies the UI and functionality of Report2.
 * Author: Hanjian Jin
 * UID: u7905060
 */
@RunWith(AndroidJUnit4.class)
public class Report2Test {

    private static final String TAG = "Report2Test";
    private static final String TEST_EMAIL = "test@example.com";
    private ActivityScenario<Report2> scenario;
    private final CountDownLatch latch = new CountDownLatch(1);  // Waits for user data to load

    @Rule
    public GrantPermissionRule grantPermissionRule = GrantPermissionRule.grant(
            Manifest.permission.POST_NOTIFICATIONS,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    );

    @Before
    public void setUp() throws InterruptedException {
        Log.d(TAG, "Simulating user login...");

        // Simulate user login and wait for data to load
        User.getInstance(TEST_EMAIL, new UserCallback() {
            @Override
            public void onUserLoaded(@Nullable User user) {
                assertNotNull("User instance should not be null", user);
                assertEquals(TEST_EMAIL, user.getEmail());
                latch.countDown();  // Release latch to indicate data load completion
            }
        });

        // Wait for user instance to load
        if (!latch.await(5, TimeUnit.SECONDS)) {
            throw new InterruptedException("User loading timeout.");
        }

        // Create an Intent with the correct Activity name and path
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClassName("com.example.smartcity", "com.example.smartcity.Report2");

        // Pass Parcelable data through the intent
        ReportData reportData = new ReportData();
        Bundle bundle = new Bundle();
        bundle.putParcelable("report1Data", reportData);
        intent.putExtras(bundle);

        // Launch the Report2 Activity
        scenario = ActivityScenario.launch(intent);
    }

    @After
    public void tearDown() {
        if (scenario != null) {
            scenario.close();  // Ensure the activity is closed properly
        }
    }

    // Verify if the UI elements are displayed as expected
    @Test
    public void testUIElementsDisplayed() {
        onView(withId(R.id.reportingpage2_radiogroup1)).check(matches(isDisplayed()));
        onView(withId(R.id.reportingpage2_radiogroup2)).check(matches(isDisplayed()));
        onView(withId(R.id.reportpage2_button)).check(matches(isDisplayed()));
    }

    // Test the report button click and navigation to HomePage
    @Test
    public void testReportButtonClick() {
        // Simulate selecting options from RadioGroup1 and RadioGroup2
        onView(withId(R.id.reportingpage2_radiogroup1_1)).perform(click());
        onView(withId(R.id.reportingpage2_radiogroup2_1)).perform(click());

        // Click the report button
        onView(withId(R.id.reportpage2_button)).perform(click());

        // Verify if the navigation to HomePage is successful by checking an element on HomePage
        onView(withId(R.id.osmap_container)).check(matches(isDisplayed()));
    }

    // Test if RadioGroup selections can be successfully made
    @Test
    public void testRadioGroupSelection() {
        // Select options from RadioGroup1 and RadioGroup2
        onView(withId(R.id.reportingpage2_radiogroup1_1)).perform(click());
        onView(withId(R.id.reportingpage2_radiogroup2_1)).perform(click());

        // Click the report button
        onView(withId(R.id.reportpage2_button)).perform(click());
    }
}