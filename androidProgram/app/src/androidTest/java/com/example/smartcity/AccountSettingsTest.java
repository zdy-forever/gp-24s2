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
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author : Hanjian Jin
 * UID: u7905060
 */

/**
 * Test class for the AccountSettings feature.
 */
@RunWith(AndroidJUnit4.class)
public class AccountSettingsTest {

    private static final String TAG = "AccountSettingsTest";
    private static final String TEST_EMAIL = "test@example.com";
    private ActivityScenario<AccountSettings> scenario;
    private final CountDownLatch latch = new CountDownLatch(1);

    @Rule
    public GrantPermissionRule grantPermissionRule = GrantPermissionRule.grant(
            Manifest.permission.POST_NOTIFICATIONS,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_MEDIA_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE
    );

    @Before
    public void setUp() throws InterruptedException {
        Log.d(TAG, "Initializing User instance...");

        // Simulate user login and wait for completion
        User.getInstance(TEST_EMAIL, new UserCallback() {
            @Override
            public void onUserLoaded(@Nullable User user) {
                assertNotNull("User instance should not be null", user);
                assertEquals(TEST_EMAIL, user.getEmail());
                latch.countDown();
            }
        });

        if (!latch.await(5, TimeUnit.SECONDS)) {
            throw new InterruptedException("User loading timeout.");
        }

        // Launch the AccountSettings Activity
        scenario = ActivityScenario.launch(AccountSettings.class);
    }

    @After
    public void tearDown() {
        if (scenario != null) {
            scenario.close();
        }
    }

    @Test
    public void testChangeUsername() {
        // Click the button to change the username
        onView(withId(R.id.account_settings_change_username)).perform(click());
        // Enter the new username
        onView(withId(R.id.account_settings_new_username)).perform(typeText("NewUserName"));
    }

    @Test
    public void testChangeAvatar() {
        // Click the button to set the avatar
        onView(withId(R.id.set_up_avatar)).perform(click());
    }

    @Test
    public void testChangeAge() {
        // Click the button to change age
        onView(withId(R.id.account_settings_age)).perform(click());
    }

    @Test
    public void testChangeGender() {
        // Click the button to change gender
        onView(withId(R.id.account_settings_gender)).perform(click());
    }

    @Test
    public void testChangePassword() {
        // Click the button to change the password
        onView(withId(R.id.account_settings_change_password)).perform(click());
    }

    // Test for deleting the account: Check if the button is clickable
    @Test
    public void testDeleteAccount() {
        // Click the button to delete the account
        onView(withId(R.id.account_settings_delete_account)).perform(click());
    }

}