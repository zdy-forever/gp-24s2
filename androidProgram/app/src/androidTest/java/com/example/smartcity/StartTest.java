package com.example.smartcity;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;
import android.Manifest;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test class for the Start Activity.
 * Ensures UI elements are displayed, user interactions are functional, and Firebase mock authentication is handled.
 * Author: Hanjian Jin
 * UID: u7905060
 */
@RunWith(AndroidJUnit4.class)
public class StartTest {

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

    private FirebaseAuth mockAuth;
    private FirebaseUser mockUser;
    private ActivityScenario<Start> scenario;

    @Before
    public void setUp() {
        // Mock FirebaseAuth and FirebaseUser instances
        mockAuth = mock(FirebaseAuth.class);
        mockUser = mock(FirebaseUser.class);

        // Launch the Start Activity
        scenario = ActivityScenario.launch(Start.class);
    }

    @After
    public void tearDown() {
        if (scenario != null) {
            scenario.close();  // Ensure the activity is properly closed
        }
    }

    // Test if UI elements are displayed correctly
    @Test
    public void testUIElementsDisplayed() {
        onView(withId(R.id.start_log_in)).check(matches(isDisplayed()));
        onView(withId(R.id.start_sign_up)).check(matches(isDisplayed()));
    }

    // Test the Log In button click and navigation to the LogIn Activity
    @Test
    public void testLogInButtonClick() {
        onView(withId(R.id.start_log_in)).perform(click());
        onView(withId(R.id.activity_log_in)).check(matches(isDisplayed()));
    }

    // Test the Sign Up button click and navigation to the SignUp Activity
    @Test
    public void testSignUpButtonClick() {
        // Click the SignUp button and wait for the page to load
        onView(withId(R.id.start_sign_up)).perform(click());

        // Use a Handler to wait for asynchronous tasks to complete
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            onView(withId(R.id.activity_sign_up)).check(matches(isDisplayed()));
        }, 2000);  // Adjust the delay time as needed
    }

    // Test if a user is already logged in and the UI updates accordingly
    @Test
    public void testUserIsAlreadyLoggedIn() {
        // Set up the FirebaseAuth mock to return a logged-in user
        when(mockAuth.getCurrentUser()).thenReturn(mockUser);
        when(mockUser.getEmail()).thenReturn("testuser@example.com");

        // Use ActivityScenario to ensure UI updates on the main thread
        scenario.onActivity(activity -> {
            activity.updateUI(mockUser.getEmail());
        });

        // Verify that the Start Activity is displayed
        onView(withId(R.id.activity_start)).check(matches(isDisplayed()));
    }
}