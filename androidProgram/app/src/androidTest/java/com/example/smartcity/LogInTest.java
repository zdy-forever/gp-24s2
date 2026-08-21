package com.example.smartcity;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import com.google.firebase.auth.FirebaseAuth;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.registerIdlingResources;
import static androidx.test.espresso.Espresso.unregisterIdlingResources;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.Mockito.mock;

/**
 * @author : Hanjian Jin
 * UID: u7905060
 */
@RunWith(AndroidJUnit4.class)
public class LogInTest {

    @Rule
    public ActivityTestRule<LogIn> activityRule = new ActivityTestRule<>(LogIn.class, true, false);

    @Rule
    public GrantPermissionRule grantPermissionRule = GrantPermissionRule.grant(
            android.Manifest.permission.POST_NOTIFICATIONS,
            android.Manifest.permission.ACCESS_NETWORK_STATE,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
    );

    private FirebaseAuth mockAuth;
    private SimpleIdlingResource idlingResource;

    @Before
    public void setUp() {
        // Mock FirebaseAuth instance
        mockAuth = mock(FirebaseAuth.class);

        // Initialize and register IdlingResource
        idlingResource = new SimpleIdlingResource();
        registerIdlingResources(idlingResource);

        // Launch the LogIn activity
        activityRule.launchActivity(new Intent());
        Log.d("LogInTest", "Activity launched");
    }

    @After
    public void tearDown() {
        // Unregister IdlingResource and finish the activity
        unregisterIdlingResources(idlingResource);
        activityRule.finishActivity();
    }

    @Test
    public void testForgotPasswordButton() {
        // Click the "Forgot Password" button
        onView(withId(R.id.forgetPassword)).perform(click());

        // Check if the password reset activity is displayed
        onView(withId(R.id.activity_password_reset)).check(matches(isDisplayed()));
    }

    @Test
    public void testNoAccountButton() {
        // Click the "No Account" button
        onView(withId(R.id.login_no_account)).perform(click());

        // Check if the sign-up activity is displayed
        onView(withId(R.id.activity_sign_up)).check(matches(isDisplayed()));
    }

    @Test
    public void testLoginWithEmailPassword() {
        // Ensure input fields are displayed on the screen
        onView(withId(R.id.log_in_address)).check(matches(isDisplayed()));
        onView(withId(R.id.log_in_password)).check(matches(isDisplayed()));

        // Enter email address and verify it
        onView(withId(R.id.log_in_address))
                .perform(click(), typeText("testuser@example.com"), closeSoftKeyboard())
                .check(matches(withText("testuser@example.com")));

        // Enter password and verify it
        onView(withId(R.id.log_in_password))
                .perform(click(), typeText("testpassword"), closeSoftKeyboard())
                .check(matches(withText("testpassword")));

        // Ensure the login button is visible and click it
        onView(withId(R.id.log_in_confirm)).check(matches(isDisplayed())).perform(click());

        // Use IdlingResource to wait for asynchronous operations to complete
        idlingResource.setIdleState(false);
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            idlingResource.setIdleState(true);
        }, 3000);

        // Verify if the user successfully navigates to the HomePage
        onView(withId(R.id.homepage)).check(matches(isDisplayed()));
    }

    @Test
    public void testEmptyInputFields() {
        // Click the login button with empty input fields
        onView(withId(R.id.log_in_confirm)).perform(click());

        // Verify that the user remains on the login page
        onView(withId(R.id.activity_log_in)).check(matches(isDisplayed()));
    }
}