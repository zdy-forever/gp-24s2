package com.example.smartcity;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

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
 * @author : Hanjian Jin
 * UID: u7905060
 */
@RunWith(AndroidJUnit4.class)
public class ManageBookingTest {

    private static final String TAG = "ManageBookingTest";
    private static final String TEST_EMAIL = "testuser@example.com";  // Simulated user email
    private ActivityScenario<ManageBooking> scenario;
    private final CountDownLatch latch = new CountDownLatch(1);  // Used to wait for asynchronous operations to complete

    @Rule
    public ActivityTestRule<ManageBooking> activityRule =
            new ActivityTestRule<>(ManageBooking.class, true, false);

    @Before
    public void setUp() throws InterruptedException {
        Log.d(TAG, "Initializing User instance...");

        // Simulate user login and wait for the asynchronous operation to complete
        User.getInstance(TEST_EMAIL, new UserCallback() {
            @Override
            public void onUserLoaded(@Nullable User user) {
                assertNotNull("User instance should not be null", user);
                assertEquals(TEST_EMAIL, user.getEmail());
                Log.d(TAG, "User loaded successfully: " + user.getEmail());
                latch.countDown();  // Release the latch to indicate user loading is complete
            }
        });

        // Wait for user loading to complete to avoid NullPointerException
        if (!latch.await(5, TimeUnit.SECONDS)) {
            throw new InterruptedException("User loading timeout");
        }

        // Launch the ManageBooking Activity
        scenario = ActivityScenario.launch(ManageBooking.class);
    }

    @After
    public void tearDown() {
        if (scenario != null) {
            scenario.close();  // Ensure the activity is properly closed
        }
    }

    // Test if the UI elements are displayed
    @Test
    public void testUIElementsDisplayed() {
        onView(withId(R.id.managebooking_info)).check(matches(isDisplayed()));
        onView(withId(R.id.managebooking_cancel_booking)).check(matches(isDisplayed()));
        onView(withId(R.id.managebooking_mark_as_done)).check(matches(isDisplayed()));
    }

    // Test if the cancel button is clickable
    @Test
    public void testCancelButtonClickable() {
        onView(withId(R.id.managebooking_cancel_booking)).perform(click());
    }

    // Test if the mark as done button is clickable
    @Test
    public void testDoneButtonClickable() {
        onView(withId(R.id.managebooking_mark_as_done)).perform(click());
    }
}