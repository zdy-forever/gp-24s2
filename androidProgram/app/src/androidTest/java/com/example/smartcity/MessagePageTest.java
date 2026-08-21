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
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author : Hanjian Jin
 * UID: u7905060
 */
@RunWith(AndroidJUnit4.class)
public class MessagePageTest {

    private static final String TAG = "MessagePageTest";
    private static final String TEST_EMAIL = "testuser@example.com";  // Simulated user email
    private ActivityScenario<MessagePage> scenario;
    private final CountDownLatch latch = new CountDownLatch(1);  // Used to wait for asynchronous operations to complete

    @Rule
    public ActivityTestRule<MessagePage> activityRule =
            new ActivityTestRule<>(MessagePage.class, true, false);

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

        // Launch the MessagePage Activity
        scenario = ActivityScenario.launch(MessagePage.class);
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
        onView(withId(R.id.messagepage_search)).check(matches(isDisplayed()));
        onView(withId(R.id.messagepage_notice)).check(matches(isDisplayed()));
        onView(withId(R.id.messagepage_home)).check(matches(isDisplayed()));
        onView(withId(R.id.messagepage_my)).check(matches(isDisplayed()));
        onView(withId(R.id.messagepage_newmessage)).check(matches(isDisplayed()));
    }

    // Test the search button click
    @Test
    public void testSearchButtonClick() {
        onView(withId(R.id.messagepage_search)).perform(click());
    }

    // Test the notice button click
    @Test
    public void testNoticeButtonClick() {
        onView(withId(R.id.messagepage_notice)).perform(click());
    }

    // Test the home button click
    @Test
    public void testHomeButtonClick() {
        onView(withId(R.id.messagepage_home)).perform(click());
    }

    // Test the add new message button click
    @Test
    public void testAddNewMessageButtonClick() {
        onView(withId(R.id.messagepage_newmessage)).perform(click());
    }
}