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
 * @author : Hanjian Jin
 * UID: u7905060
 */

@RunWith(AndroidJUnit4.class)
public class PasswordResetTest {

    private static final String TAG = "PasswordResetTest";
    private static final String TEST_EMAIL = "test@example.com";
    private ActivityScenario<PasswordReset> scenario;
    private final CountDownLatch latch = new CountDownLatch(1);

    @Rule
    public GrantPermissionRule grantPermissionRule = GrantPermissionRule.grant(
            android.Manifest.permission.INTERNET
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
                latch.countDown();  // Release the latch indicating user data loaded successfully
            }
        });

        // Wait for user data to load
        if (!latch.await(5, TimeUnit.SECONDS)) {
            throw new InterruptedException("User loading timeout.");
        }

        // Launch the PasswordReset Activity
        scenario = ActivityScenario.launch(PasswordReset.class);
    }

    @After
    public void tearDown() {
        if (scenario != null) {
            scenario.close();  // Ensure the activity is properly closed
        }
    }

    // Test if the PasswordReset page layout is correctly loaded
    @Test
    public void testPasswordResetLayout() {
        // Verify if the main UI elements are displayed as expected
        onView(withId(R.id.password_reset_email)).check(matches(isDisplayed()));
        onView(withId(R.id.password_reset_new_password)).check(matches(isDisplayed()));
        onView(withId(R.id.password_reset_confirm_password)).check(matches(isDisplayed()));
        onView(withId(R.id.password_reset_apply)).check(matches(isDisplayed()));
        onView(withId(R.id.password_reset_by_email)).check(matches(isDisplayed()));
    }

    // Test if the input fields accept text
    @Test
    public void testInputFieldsAcceptText() {
        // Enter text in the email input field
        onView(withId(R.id.password_reset_email)).perform(typeText("test@example.com"));
        // Enter text in the new password input field
        onView(withId(R.id.password_reset_new_password)).perform(typeText("Password123!"));
        // Enter text in the confirm password input field
        onView(withId(R.id.password_reset_confirm_password)).perform(typeText("Password123!"));
    }

    // Test if the buttons are clickable
    @Test
    public void testButtonsAreClickable() {
        // Click the "Apply" button
        onView(withId(R.id.password_reset_apply)).perform(click());

        // Click the "Reset via Email" button
        onView(withId(R.id.password_reset_by_email)).perform(click());
    }
}