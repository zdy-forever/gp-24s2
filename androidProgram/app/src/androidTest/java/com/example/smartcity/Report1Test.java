package com.example.smartcity;

import android.Manifest;
import android.content.Intent;
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
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author : Hanjian Jin
 * UID: u7905060
 */

@RunWith(AndroidJUnit4.class)
public class Report1Test {

    private static final String TAG = "Report1Test";
    private static final String TEST_EMAIL = "test@example.com";
    private ActivityScenario<Report1> scenario;
    private final CountDownLatch latch = new CountDownLatch(1);

    @Rule
    public GrantPermissionRule grantPermissionRule = GrantPermissionRule.grant(
            Manifest.permission.INTERNET
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
                Log.d(TAG, "User successfully logged in: " + user.getEmail());
                latch.countDown();  // Release the latch to indicate data loaded
            }
        });

        // Wait for user data to load
        if (!latch.await(5, TimeUnit.SECONDS)) {
            throw new InterruptedException("User loading timeout.");
        }

        // Launch the Report1 Activity
        scenario = ActivityScenario.launch(Report1.class);
    }

    @After
    public void tearDown() {
        if (scenario != null) {
            scenario.close();  // Ensure the activity is closed
        }
    }

    // Test if the UI elements are displayed as expected
    @Test
    public void testUIElementsDisplayed() {
        // Check if the button and input field are displayed
        onView(withId(R.id.reportpage1_button)).check(matches(isDisplayed()));
        onView(withId(R.id.reportpage1_reason)).check(matches(isDisplayed()));

        // Check if all checkboxes are displayed
        int[] checkBoxIds = {
                R.id.reportpage1_checkbox1,
                R.id.reportpage1_checkbox2,
                R.id.reportpage1_checkbox3,
                R.id.reportpage1_checkbox4,
                R.id.reportpage1_checkbox5,
                R.id.reportpage1_checkbox6,
                R.id.reportpage1_checkbox7,
                R.id.reportpage1_checkbox8,
                R.id.reportpage1_checkbox9
        };
        for (int id : checkBoxIds) {
            onView(withId(id)).check(matches(isDisplayed()));
        }
    }

    // Helper method: Test if a CheckBox can be toggled (only checks if it is clickable)
    private void testCheckBoxCanToggle(int checkBoxId) {
        onView(withId(checkBoxId)).perform(click());  // Click to toggle state
        onView(withId(checkBoxId)).perform(click());  // Click again to reset state
    }

    // Test if all checkboxes can be toggled
    @Test
    public void testCheckBoxesCanToggle() {
        int[] checkBoxIds = {
                R.id.reportpage1_checkbox1,
                R.id.reportpage1_checkbox2,
                R.id.reportpage1_checkbox3,
                R.id.reportpage1_checkbox4,
                R.id.reportpage1_checkbox5,
                R.id.reportpage1_checkbox6,
                R.id.reportpage1_checkbox7,
                R.id.reportpage1_checkbox8,
                R.id.reportpage1_checkbox9
        };
        for (int id : checkBoxIds) {
            testCheckBoxCanToggle(id);
        }
    }

    // Test if text can be entered into the reason input field
    @Test
    public void testReasonInput() {
        String reasonText = "This is a test reason";
        onView(withId(R.id.reportpage1_reason)).perform(typeText(reasonText));
        onView(withId(R.id.reportpage1_reason)).check(matches(withText(reasonText)));
    }

    // Test if the button navigates to Report2
    @Test
    public void testButtonNavigationToReport2() {
        String reasonText = "This is a test reason";
        onView(withId(R.id.reportpage1_reason)).perform(typeText(reasonText));

        // Click the first checkbox to satisfy navigation conditions
        onView(withId(R.id.reportpage1_checkbox1)).perform(click());

        // Click the button to navigate to Report2
        onView(withId(R.id.reportpage1_button)).perform(click());

        // Verify if Report2 is launched (assuming an element with ID reportingpage2_radiogroup1_1 exists)
        onView(withId(R.id.reportingpage2_radiogroup1_1)).check(matches(isDisplayed()));
    }
}