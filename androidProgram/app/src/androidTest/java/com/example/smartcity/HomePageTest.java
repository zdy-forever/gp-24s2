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
public class HomePageTest {

    private static final String TAG = "HomePageTest";
    private static final String TEST_EMAIL = "test@example.com";  // Simulated user email
    private ActivityScenario<HomePage> scenario;
    private final CountDownLatch latch = new CountDownLatch(1);  // Used to wait for asynchronous operations to complete

    @Rule
    public GrantPermissionRule grantPermissionRule = GrantPermissionRule.grant(
            Manifest.permission.POST_NOTIFICATIONS,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_MEDIA_LOCATION,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.MODIFY_AUDIO_SETTINGS,
            Manifest.permission.READ_EXTERNAL_STORAGE
    );

    @Before
    public void setup() throws InterruptedException {
        Log.d(TAG, "Launching HomePage Activity...");

        // Initialize user and wait for asynchronous operation using CountDownLatch
        User.getInstance(TEST_EMAIL, new UserCallback() {
            @Override
            public void onUserLoaded(@Nullable User user) {
                assertNotNull("User instance should not be null", user);
                assertEquals(TEST_EMAIL, user.getEmail());
                Log.d(TAG, "User loaded successfully: " + user.getEmail());
                latch.countDown();  // Release the latch, indicating user loading is complete
            }
        });

        // Wait for user loading to complete
        if (!latch.await(5, TimeUnit.SECONDS)) {
            throw new InterruptedException("User loading timeout");
        }

        scenario = ActivityScenario.launch(HomePage.class);  // Launch HomePage Activity
    }

    @After
    public void tearDown() {
        if (scenario != null) {
            scenario.close();  // Ensure the Activity is properly closed
        }
    }

    // Check if UI elements are displayed
    @Test
    public void testUIElementsDisplayed() {
        onView(withId(R.id.homepage_search)).check(matches(isDisplayed()));
        onView(withId(R.id.homepage_notice)).check(matches(isDisplayed()));
        onView(withId(R.id.homepage_message)).check(matches(isDisplayed()));
        onView(withId(R.id.homepage_my)).check(matches(isDisplayed()));
        onView(withId(R.id.homepage_report)).check(matches(isDisplayed()));
        onView(withId(R.id.homepage_book)).check(matches(isDisplayed()));
    }

    // Test if the Search button is clickable
    @Test
    public void testSearchButtonClickable() {
        onView(withId(R.id.homepage_search)).perform(click());
    }

    // Test if the Notice button is clickable
    @Test
    public void testNoticeButtonClickable() {
        onView(withId(R.id.homepage_notice)).perform(click());
    }

    // Test if the Message button is clickable
    @Test
    public void testMessageButtonClickable() {
        onView(withId(R.id.homepage_message)).perform(click());
    }

    // Test if the Report button is clickable
    @Test
    public void testReportButtonClickable() {
        onView(withId(R.id.homepage_report)).perform(click());
    }

    // Test if the Book button is clickable
    @Test
    public void testBookButtonClickable() {
        onView(withId(R.id.homepage_book)).perform(click());
    }
}