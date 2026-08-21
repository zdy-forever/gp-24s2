package com.example.smartcity;

import android.Manifest;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.ViewAction;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import com.example.smartcity.tools.User;
import com.example.smartcity.tools.UserCallback;

import org.hamcrest.Matcher;
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
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author : Hanjian Jin
 * UID: u7905060
 */
@RunWith(AndroidJUnit4.class)
public class FindNewFriendTest {

    private static final String TAG = "FindNewFriendTest";
    private static final String TEST_EMAIL = "test@example.com";
    private ActivityScenario<FindNewFriend> scenario;
    private final CountDownLatch latch = new CountDownLatch(1); // Used to wait for user login completion

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
                Log.d(TAG, "User loaded successfully: " + user.getEmail());
                latch.countDown();  // Release the latch, indicating that the user has been loaded
            }
        });

        // Wait for user loading to complete to avoid NullPointerException
        if (!latch.await(5, TimeUnit.SECONDS)) {
            throw new InterruptedException("User loading timeout");
        }

        // Launch the FindNewFriend Activity
        scenario = ActivityScenario.launch(FindNewFriend.class);
    }

    @After
    public void tearDown() {
        if (scenario != null) {
            scenario.close();  // Ensure the activity is properly closed
        }
    }

    @Test
    public void testUIElementsDisplayed() {
        onView(withId(R.id.find_newfriend_input)).check(matches(isDisplayed()));
        onView(withId(R.id.find_newfriend_search)).check(matches(isDisplayed()));
        onView(withId(R.id.find_newfriend_show)).check(matches(isDisplayed()));
        onView(withId(R.id.find_newfriend_age_filter)).check(matches(isDisplayed()));
        onView(withId(R.id.find_newfriend_gender_filter)).check(matches(isDisplayed()));
    }

    @Test
    public void testSearchAndShowResults() {
        // Simulate clicking the search and show results buttons
        onView(withId(R.id.find_newfriend_search)).perform(click());
        onView(withId(R.id.find_newfriend_show)).perform(click());
    }

    @Test
    public void testAgeFilterSelection() {
        // Click the age filter button and check if the filter interface is displayed
        onView(withId(R.id.find_newfriend_age_filter)).perform(click());
        onView(withText("Select an Option")).check(matches(isDisplayed()));
    }

    @Test
    public void testGenderFilterSelection() {
        // Click the gender filter button and check if the filter interface is displayed
        onView(withId(R.id.find_newfriend_gender_filter)).perform(click());
        onView(withText("Select an Option")).check(matches(isDisplayed()));
    }
}