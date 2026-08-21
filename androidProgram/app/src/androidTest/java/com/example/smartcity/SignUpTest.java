package com.example.smartcity;

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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Test class for SignUp Activity.
 * Ensures the UI is displayed correctly and functionality works as expected.
 * Author: Hanjian Jin
 * UID: u7905060
 */
@RunWith(AndroidJUnit4.class)
public class SignUpTest {

    private static final String TAG = "SignUpTest";
    private static final String TEST_EMAIL = "testuser@example.com";
    private static final String TEST_PASSWORD = "Password1!";
    private ActivityScenario<SignUp> scenario;
    private final CountDownLatch latch = new CountDownLatch(1);  // Used to wait for user data to load

    @Rule
    public GrantPermissionRule grantPermissionRule = GrantPermissionRule.grant(
            android.Manifest.permission.INTERNET
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
                latch.countDown();  // Release the latch to indicate data load completion
            }
        });

        // Wait for the user data to load
        if (!latch.await(5, TimeUnit.SECONDS)) {
            throw new InterruptedException("User loading timeout.");
        }

        // Launch the SignUp Activity
        scenario = ActivityScenario.launch(SignUp.class);
    }

    @After
    public void tearDown() {
        if (scenario != null) {
            scenario.close();  // Ensure the activity is properly closed
        }
    }

    // Test if the UI elements are displayed correctly
    @Test
    public void testUIElementsDisplayed() {
        onView(withId(R.id.sign_up_address)).check(matches(isDisplayed()));
        onView(withId(R.id.sign_up_password)).check(matches(isDisplayed()));
        onView(withId(R.id.sign_up_confirm_password)).check(matches(isDisplayed()));
        onView(withId(R.id.sign_up_apply)).check(matches(isDisplayed()));
        onView(withId(R.id.sign_up_have_account)).check(matches(isDisplayed()));
    }

    // Test if the text fields accept input
    @Test
    public void testTextInput() {
        onView(withId(R.id.sign_up_address)).perform(typeText(TEST_EMAIL));
        onView(withId(R.id.sign_up_password)).perform(typeText(TEST_PASSWORD));
        onView(withId(R.id.sign_up_confirm_password)).perform(typeText(TEST_PASSWORD));
    }

    // Test if the buttons are clickable
    @Test
    public void testButtonsClickable() {
        onView(withId(R.id.sign_up_apply)).perform(click());
        onView(withId(R.id.sign_up_have_account)).perform(click());
    }

    // Test successful sign-up process
    @Test
    public void testSuccessfulSignUp() {
        onView(withId(R.id.sign_up_address)).perform(typeText(TEST_EMAIL));
        onView(withId(R.id.sign_up_password)).perform(typeText(TEST_PASSWORD));
        onView(withId(R.id.sign_up_confirm_password)).perform(typeText(TEST_PASSWORD));
        onView(withId(R.id.sign_up_apply)).perform(click());

        // Verify if navigation to HomePage is successful by checking an element on HomePage
        // Assume that HomePage contains an element with ID imageView2
        onView(withId(R.id.imageView2)).check(matches(isDisplayed()));
    }
}