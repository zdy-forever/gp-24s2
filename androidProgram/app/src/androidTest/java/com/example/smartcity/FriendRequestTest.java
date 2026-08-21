package com.example.smartcity;

import android.Manifest;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import com.example.smartcity.tools.Constants;
import com.example.smartcity.tools.User;
import com.example.smartcity.tools.UserCallback;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.anything;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author : Hanjian Jin
 * UID: u7905060
 */
@RunWith(AndroidJUnit4.class)
public class FriendRequestTest {

    private static final String TAG = "FriendRequestTest";
    private static final String TEST_EMAIL = "test@example.com";  // Simulated user email
    private static final String MOCK_FRIEND_EMAIL = "mockfriend@example.com";  // Mock friend's email
    private ActivityScenario<FriendRequest> scenario;
    private final CountDownLatch latch = new CountDownLatch(1);  // Countdown latch to wait for user loading
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Rule
    public GrantPermissionRule grantPermissionRule = GrantPermissionRule.grant(
            Manifest.permission.POST_NOTIFICATIONS,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
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
                latch.countDown();  // Release the latch when user is loaded
            }
        });

        // Wait for user loading to avoid NullPointerException
        if (!latch.await(5, TimeUnit.SECONDS)) {
            throw new InterruptedException("User loading timeout");
        }

        insertMockFriendRequest();  // Insert a mock friend request for testing
        scenario = ActivityScenario.launch(FriendRequest.class);  // Launch the FriendRequest activity
    }

    @After
    public void tearDown() {
        deleteMockFriendRequest();  // Delete the mock friend request after tests

        if (scenario != null) {
            scenario.close();  // Ensure the activity is properly closed
        }
    }

    private void insertMockFriendRequest() {
        // Create a mock friend request
        Map<String, Object> request = new HashMap<>();
        request.put("fromUser", MOCK_FRIEND_EMAIL);
        request.put("toUser", TEST_EMAIL);
        request.put("status", "pending");

        // Add the request to Firestore
        db.collection(Constants.friendRequest)
                .add(request)
                .addOnSuccessListener(documentReference ->
                        Log.d(TAG, "Mock friend request added: " + documentReference.getId()))
                .addOnFailureListener(e ->
                        Log.e(TAG, "Failed to add mock friend request", e));
    }

    private void deleteMockFriendRequest() {
        // Delete the mock friend request from Firestore
        db.collection(Constants.friendRequest)
                .whereEqualTo("fromUser", MOCK_FRIEND_EMAIL)
                .whereEqualTo("toUser", TEST_EMAIL)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        task.getResult().getDocuments().forEach(document ->
                                db.collection(Constants.friendRequest)
                                        .document(document.getId())
                                        .delete()
                                        .addOnSuccessListener(aVoid ->
                                                Log.d(TAG, "Mock friend request deleted"))
                                        .addOnFailureListener(e ->
                                                Log.e(TAG, "Failed to delete mock friend request", e)));
                    }
                });
    }

    @Test
    public void testClickFriendRequestItem() {
        // Click the first item in the friend request list
        onData(anything()).inAdapterView(withId(R.id.friendrequest_list))
                .atPosition(0).perform(click());
    }

    @Test
    public void testDisplayItem() {
        // Check if the friend request list is displayed
        onView(withId(R.id.friendrequest_list)).check(matches(isDisplayed()));
    }

    @Test
    public void testRejectFriendRequest() {
        // Click the first friend request and reject it
        onData(anything()).inAdapterView(withId(R.id.friendrequest_list))
                .atPosition(0).perform(click());
        onView(withId(R.id.friendrequest_reject)).perform(click());
    }

    @Test
    public void testAcceptFriendRequest() {
        // Click the first friend request and accept it
        onData(anything()).inAdapterView(withId(R.id.friendrequest_list))
                .atPosition(0).perform(click());
        onView(withId(R.id.friendrequest_accept)).perform(click());
    }
}