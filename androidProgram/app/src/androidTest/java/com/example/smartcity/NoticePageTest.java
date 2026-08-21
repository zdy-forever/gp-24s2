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
public class NoticePageTest {

    private static final String TAG = "NoticePageTest";
    private static final String TEST_EMAIL = "test@example.com";
    private ActivityScenario<NoticePage> scenario;
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
        Log.d(TAG, "Launching NoticePage Activity...");

        // Simulate user login and wait for asynchronous loading to complete
        User.getInstance(TEST_EMAIL, new UserCallback() {
            @Override
            public void onUserLoaded(@Nullable User user) {
                assertNotNull("User instance should not be null", user);
                assertEquals(TEST_EMAIL, user.getEmail());
                Log.d(TAG, "User loaded successfully: " + user.getEmail());
                latch.countDown();  // Release the latch to indicate user loading is complete
            }
        });

        // Wait for user loading to complete
        if (!latch.await(5, TimeUnit.SECONDS)) {
            throw new InterruptedException("User loading timeout");
        }

        // Launch the NoticePage Activity
        scenario = ActivityScenario.launch(NoticePage.class);
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
        onView(withId(R.id.noticepage_search)).check(matches(isDisplayed()));
        onView(withId(R.id.noticepage_home)).check(matches(isDisplayed()));
        onView(withId(R.id.noticepage_message)).check(matches(isDisplayed()));
        onView(withId(R.id.noticepage_my)).check(matches(isDisplayed()));
        onView(withId(R.id.noticepage_nodification)).check(matches(isDisplayed()));
    }

    // Test navigation to the Search page
    @Test
    public void testSearchPageNavigation() {
        onView(withId(R.id.noticepage_search)).perform(click());
        onView(withId(R.id.searchpage)).check(matches(isDisplayed()));
    }

    // Test navigation to the Home page
    @Test
    public void testHomePageNavigation() {
        onView(withId(R.id.noticepage_home)).perform(click());
        onView(withId(R.id.homepage)).check(matches(isDisplayed()));
    }

    // Test navigation to the Message page
    @Test
    public void testMessagePageNavigation() {
        onView(withId(R.id.noticepage_message)).perform(click());
        onView(withId(R.id.select_friend_result)).check(matches(isDisplayed()));
    }

    // Test ListView item click
    @Test
    public void testListViewItemClick() {
        // Click the first ListView item (assuming there is data)
        onView(withId(R.id.noticepage_nodification)).perform(click());
    }

    // Verify notifications are loaded
    @Test
    public void testNotificationsLoaded() {
        // Verify if the notifications list is displayed
        onView(withId(R.id.noticepage_nodification)).check(matches(isDisplayed()));
    }
}