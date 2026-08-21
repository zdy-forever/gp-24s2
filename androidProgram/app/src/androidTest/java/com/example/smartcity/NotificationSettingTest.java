package com.example.smartcity;

import android.Manifest;
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
 * @author : Hanjian Jin
 * UID: u7905060
 */
@RunWith(AndroidJUnit4.class)
public class NotificationSettingTest {

    private static final String TAG = "NotificationSettingTest";
    private static final String TEST_EMAIL = "test@example.com";
    private ActivityScenario<NotificationSetting> scenario;
    private final CountDownLatch latch = new CountDownLatch(1);  // Used to wait for user data loading

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

        // Simulate user login and wait for user data to load
        User.getInstance(TEST_EMAIL, new UserCallback() {
            @Override
            public void onUserLoaded(@Nullable User user) {
                assertNotNull("User instance should not be null", user);
                assertEquals(TEST_EMAIL, user.getEmail());
                Log.d(TAG, "User successfully logged in: " + user.getEmail());
                latch.countDown();  // Release the latch, indicating user data loaded successfully
            }
        });

        // Wait for user data to load
        if (!latch.await(5, TimeUnit.SECONDS)) {
            throw new InterruptedException("User loading timeout.");
        }

        // Launch the NotificationSetting Activity
        scenario = ActivityScenario.launch(NotificationSetting.class);
    }

    @After
    public void tearDown() {
        if (scenario != null) {
            scenario.close();  // Ensure the activity is closed properly
        }
    }

    // Test if the UI elements are displayed as expected
    @Test
    public void testUIElementsDisplayed() {
        int[] switchIds = {
                R.id.notificationsetting_friend_request,
                R.id.notificationsetting_friend_message,
                R.id.notificationsetting_booking_success,
                R.id.notificationsetting_booking_near,
                R.id.notificationsetting_report_seccess,
                R.id.notificationsetting_report_feedback,
                R.id.notificationsetting_low_time_limit
        };

        for (int id : switchIds) {
            onView(withId(id)).check(matches(isDisplayed()));
        }
    }

    // Helper method: Test if a specific switch can toggle (clickable, regardless of state)
    private void testSwitchCanToggle(int switchId) {
        // Click the switch to toggle its state
        onView(withId(switchId)).perform(click());

        // Click again to toggle back to the original state
        onView(withId(switchId)).perform(click());
    }

    // Test 'Friend Request' notification setting
    @Test
    public void testFriendRequestSettingToggle() {
        testSwitchCanToggle(R.id.notificationsetting_friend_request);
    }

    // Test 'Friend Message' notification setting
    @Test
    public void testFriendMessageSettingToggle() {
        testSwitchCanToggle(R.id.notificationsetting_friend_message);
    }

    // Test 'Booking Success' notification setting
    @Test
    public void testBookingSuccessSettingToggle() {
        testSwitchCanToggle(R.id.notificationsetting_booking_success);
    }

    // Test 'Booking Near' notification setting
    @Test
    public void testBookingNearSettingToggle() {
        testSwitchCanToggle(R.id.notificationsetting_booking_near);
    }

    // Test 'Report Success' notification setting
    @Test
    public void testReportSuccessSettingToggle() {
        testSwitchCanToggle(R.id.notificationsetting_report_seccess);
    }

    // Test 'Report Feedback' notification setting
    @Test
    public void testReportFeedbackSettingToggle() {
        testSwitchCanToggle(R.id.notificationsetting_report_feedback);
    }

    // Test 'Low Time Limit' notification setting
    @Test
    public void testLowTimeLimitSettingToggle() {
        testSwitchCanToggle(R.id.notificationsetting_low_time_limit);
    }
}