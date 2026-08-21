package com.example.smartcity;

import android.Manifest;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.UiController;
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
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
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
public class BookingPageTest {

    private static final String TAG = "BookingPageTest";
    private static final String TEST_EMAIL = "test@example.com";
    private ActivityScenario<BookingPage> scenario;
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

        // Simulate user login and wait for the asynchronous operation to complete
        User.getInstance(TEST_EMAIL, new UserCallback() {
            @Override
            public void onUserLoaded(@Nullable User user) {
                assertNotNull("User instance should not be null", user);
                assertEquals(TEST_EMAIL, user.getEmail());
                Log.d(TAG, "User loaded successfully: " + user.getEmail());
                latch.countDown();  // Release the latch, indicating that the user has been loaded
            }
        });

        // Wait for the user to be loaded to avoid a NullPointerException
        if (!latch.await(5, TimeUnit.SECONDS)) {
            throw new InterruptedException("User loading timeout");
        }

        // Launch the BookingPage Activity
        scenario = ActivityScenario.launch(BookingPage.class);
    }

    @After
    public void tearDown() {
        if (scenario != null) {
            scenario.close();  // Ensure the activity is properly closed
        }
    }

    @Test
    public void testUIElementsDisplayed() {
        onView(withId(R.id.bookingpage_date_picker)).check(matches(isDisplayed()));
        onView(withId(R.id.bookingpage_startime)).check(matches(isDisplayed()));
        onView(withId(R.id.bookingpage_endtime)).check(matches(isDisplayed()));
        onView(withId(R.id.bookingpage_confirm)).check(matches(isDisplayed()));
    }

    @Test
    public void testDatePickerDisplayed() {
        onView(withId(R.id.bookingpage_date_picker)).perform(click());
        onView(withText("OK")).check(matches(isDisplayed()));
    }

    @Test
    public void testTimePickersDisplayed() {
        onView(withId(R.id.bookingpage_startime)).check(matches(isDisplayed()));
        onView(withId(R.id.bookingpage_endtime)).check(matches(isDisplayed()));
    }

    // Complete booking flow test
    @Test
    public void testCompleteBookingFlow() {
        // Click the date picker and confirm the date
        onView(withId(R.id.bookingpage_date_picker)).perform(click());
        onView(withText("OK")).check(matches(isDisplayed())).perform(click());

        // Set the start time to 10:00
        onView(withId(R.id.bookingpage_startime)).perform(setTime(10, 0));

        // Set the end time to 11:00
        onView(withId(R.id.bookingpage_endtime)).perform(setTime(11, 0));

        // Click the confirm button
        onView(withId(R.id.bookingpage_confirm)).perform(click());
        onView(withText("YES")).check(matches(isDisplayed()));
    }

    // Custom method: Set the time on a TimePicker
    private ViewAction setTime(final int hour, final int minute) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(TimePicker.class);  // Only works with TimePicker
            }

            @Override
            public String getDescription() {
                return "Set the time on a TimePicker";
            }

            @Override
            public void perform(UiController uiController, View view) {
                TimePicker timePicker = (TimePicker) view;
                timePicker.setHour(hour);
                timePicker.setMinute(minute);
            }
        };
    }
}