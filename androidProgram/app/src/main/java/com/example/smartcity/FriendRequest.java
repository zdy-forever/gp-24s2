package com.example.smartcity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartcity.tools.Constants;
import com.example.smartcity.tools.User;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Shangyi Shen
 * UID: u7735222
 */
public class FriendRequest extends AppCompatActivity {
    private User userInstance;
    private String friendEmail = null;
    private final Map<String, Boolean> private_info = new HashMap<>();
    String currentUserEmail;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    TextView friendNameShow, friendEmailShow, genderShow, ageShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friendrequest);
        userInstance = User.getInstance();
        currentUserEmail = userInstance.getEmail();

        friendNameShow = findViewById(R.id.friendrequest_username);
        friendEmailShow = findViewById(R.id.friendrequest_email);
        genderShow = findViewById(R.id.friend_request_gender);
        ageShow = findViewById(R.id.friendrequest_age);
        ListView listView = findViewById(R.id.friendrequest_list);
        Button reject = findViewById(R.id.friendrequest_reject);
        Button accept = findViewById(R.id.friendrequest_accept);

        ArrayList<String> friend_result = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, friend_result);
        listView.setAdapter(adapter);
        updatePage();
        // do query and check if private information is switched on for each user in this query
        db.collection(Constants.friendRequest)
                .whereEqualTo("toUser", User.getInstance().getEmail())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String fromUser = document.getString("fromUser");
                            assert fromUser != null;
                            Log.d("privacy1", fromUser);
                            db.collection("users")
                                    .whereEqualTo("email", fromUser).get().addOnCompleteListener(task2 -> {
                                        if (task2.isSuccessful()) {
                                            for (QueryDocumentSnapshot document1 : task2.getResult()) {
                                                Boolean privacy = document1.getBoolean("private_information");
                                                assert privacy != null;
                                                Log.d("privacy", privacy.toString());
                                                private_info.put(fromUser, privacy);
                                            }
                                        }
                                    });
                        }
                    } else {
                        Log.w("Firestore", "Error getting documents: ", task.getException());
                    }
                });


        listView.setOnItemClickListener((parent, view, position, id) -> {
            String friendInformation = (String) parent.getItemAtPosition(position);
            Log.d("FriendName", "Selected friend: " + friendInformation);
            ImageView avatar = findViewById(R.id.friend_avatar);
            avatar.setImageDrawable(null);
            // get Email
            Pattern pattern = Pattern.compile("[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}");
            Matcher matcher = pattern.matcher(friendInformation);
            if (matcher.find()) {
                friendEmail = matcher.group();
                Log.d("ExtractedEmail", "Email: " + friendEmail);
                fetchFriendData(friendEmail);
            } else {
                Log.d("ExtractedEmail", "No email found in the message.");
                Toast.makeText(this, "No valid email found.", Toast.LENGTH_SHORT).show();
            }
        });

        reject.setOnClickListener(v -> {
            // check if already select a friend
            if (friendEmail == null || friendEmail.isEmpty()) {
                Toast.makeText(this, "Please select a person", Toast.LENGTH_SHORT).show();
                return;
            }
            Log.d("reject", "Reject button was clicked");
            // get friend request
            db.collection(Constants.friendRequest)
                    .whereEqualTo(Constants.fromUser, friendEmail)
                    .whereEqualTo(Constants.toUser, currentUserEmail)
                    .get()
                    .addOnCompleteListener(task2 -> {
                        if (!task2.isSuccessful() || task2.getResult().isEmpty()) {
                            handleRejectFailure("No friend request found for the given email", null);
                            return;
                        }
                        Log.d("reject", "Friend request found in Firebase");
                        // get friend request ID and delete it in firestore
                        String requestId = task2.getResult().getDocuments().get(0).getId();
                        deleteFriendRequestForReject(requestId);
                    })
                    .addOnFailureListener(e -> handleRejectFailure("Error fetching friend request", e));
        });

        accept.setOnClickListener(v -> {
            // check if already select a friend
            if (friendEmail == null || friendEmail.isEmpty()) {
                Toast.makeText(this, "Please select a person", Toast.LENGTH_SHORT).show();
                return;
            }
            Log.d("accept", "Accept button was clicked");
            // check if friend request is already exist
            db.collection(Constants.friendRequest)
                    .whereEqualTo(Constants.fromUser, friendEmail)
                    .whereEqualTo(Constants.toUser, currentUserEmail)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful() || task.getResult().isEmpty()) {
                            updatePage();
                            Log.d("Firestore", "No matching friend request found.");
                            return;
                        }
                        Log.d("accept", "Entering friend's profile in Firebase");
                        // get request ID and delete it in firestore
                        String requestId = task.getResult().getDocuments().get(0).getId();
                        deleteFriendRequest(requestId);
                        fetchFriendInfo();
                        Toast.makeText(this, "Successfully added to friend's list!", Toast.LENGTH_SHORT).show();
                    });

        });

    }

    protected void onResume() {
        super.onResume();
        updatePage();
        //update online status
        if (!User.getInstance().isOnline()) {
            User.getInstance().setOnline(true);
        }
        Log.d("isOnline", "Friend request on resume " + User.getInstance().isOnline());
    }

    protected void onPause() {
        super.onPause();
        //update online status
        if (User.getInstance().isOnline()) {
            User.getInstance().setOnline(false);
        }
        Log.d("isOnline", "friend request on pause" + User.getInstance().isOnline());
    }

    private void deleteNotification(String email, String friendId) {
        db.collection(Constants.notifications).document(email).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<Map<String, Object>> notifications =
                                (List<Map<String, Object>>) documentSnapshot.get("notifications");

                        if (notifications != null) {
                            // use stream to filter notifications
                            List<Map<String, Object>> updatedNotifications = notifications.stream().filter(notification ->
                                            !"You have a new friend request".equals(notification.get("text"))
                                                    || !friendId.equals(notification.get("from"))
                                    )
                                    .collect(Collectors.toList());
                            // update database
                            db.collection(Constants.notifications).document(email)
                                    .update(Constants.notifications, updatedNotifications)
                                    .addOnSuccessListener(aVoid ->
                                            Log.d("DeleteNotification", "Notification successfully deleted!"))
                                    .addOnFailureListener(e ->
                                            Log.w("DeleteNotification", "Error deleting notification", e));
                        }
                    } else {
                        Log.w("DeleteNotification", "Document does not exist!");
                    }
                })
                .addOnFailureListener(e ->
                        Log.w("DeleteNotification", "Error getting document", e)
                );
    }

    private void loadAvatar(String urlOrBase64) throws FileNotFoundException {
        ImageView avatar = findViewById(R.id.friend_avatar);
        if (urlOrBase64 != null && urlOrBase64.startsWith("http")) {
            Glide.with(this)
                    .load(urlOrBase64)
                    .placeholder(R.mipmap.default_avatar)
                    .error(R.mipmap.default_avatar)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            Log.e("AccountActivity", "Fail to load avatar", e);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            Log.d("AccountActivity", "Avatar loaded");
                            return false;
                        }
                    })
                    .into(avatar);
        } else if (urlOrBase64 != null && !urlOrBase64.isEmpty()) {
            try {
                byte[] decodedBytes = Base64.decode(urlOrBase64, Base64.DEFAULT);
                Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                avatar.setImageBitmap(decodedBitmap);
            } catch (IllegalArgumentException e) {
                Log.e("AccountActivity", "Invalid Base64 string", e);
                avatar.setImageResource(R.mipmap.default_avatar);
            }
        } else {
            // if string is invalid, then show the default avatar
            avatar.setImageResource(R.mipmap.default_avatar);
        }
    }

    private void updatePage() {
        userInstance = User.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String currentUserEmail = userInstance.getEmail();

        TextView friendNameShow = findViewById(R.id.friendrequest_username);
        TextView friendEmailShow = findViewById(R.id.friendrequest_email);
        TextView genderShow = findViewById(R.id.friend_request_gender);
        TextView ageShow = findViewById(R.id.friendrequest_age);
        ListView listView = findViewById(R.id.friendrequest_list);
        ImageView avatar = findViewById(R.id.friend_avatar);

        ArrayList<String> friend_result = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, friend_result);
        listView.setAdapter(adapter);
        friendNameShow.setText("");
        friendEmailShow.setText("");
        genderShow.setText("");
        ageShow.setText("");
        avatar.setImageBitmap(null);

        //load list of friends request
        db.collection(Constants.friendRequest)
                .whereEqualTo(Constants.toUser, currentUserEmail)
                .whereEqualTo("status", "pending")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // make sure not to append repeatedly
                        friend_result.clear();
                        for (DocumentSnapshot document : task.getResult()) {
                            String fromUser = document.getString("fromUser");
                            if (fromUser != null) {
                                //add friend to list view
                                friend_result.add("Friend request from: " + fromUser);
                            }
                        }
                        // notify adapter and update ListView
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.w("Firestore", "Error getting documents.", task.getException());
                    }
                });
    }

    private void fetchFriendInfo() {
        db.collection(Constants.users)
                .whereEqualTo(Constants.Email, friendEmail)
                .get()
                .addOnCompleteListener(userTask -> {
                    if (!userTask.isSuccessful() || userTask.getResult().isEmpty()) {
                        handleFailure("Failed to retrieve friend's information", null);
                        return;
                    }

                    DocumentSnapshot friendDoc = userTask.getResult().getDocuments().get(0);
                    updateFriendList(friendDoc);

                })
                .addOnFailureListener(e ->
                        handleFailure("Failed to retrieve friend data", e)
                );
    }

    private void deleteFriendRequest(String requestId) {
        db.collection(Constants.friendRequest)
                .document(requestId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    deleteNotification(currentUserEmail, friendEmail);
                    Log.d(Constants.friendRequest, "Friend request approved");

                })
                .addOnFailureListener(e ->
                        handleFailure("Failed to delete friend request", e)
                );
    }

    private void updateFriendList(DocumentSnapshot friendDoc) {
        List<String> friendList = (List<String>) friendDoc.get(Constants.friendList);
        if (friendList == null) {
            friendList = new ArrayList<>();
        }
        Log.d("getFriendListUpdate", friendList.toString());
        // check if friend list has already contain the select new friend
        if (friendList.contains(currentUserEmail)) {
            updatePage();
            Toast.makeText(this, "Already in friend's list.", Toast.LENGTH_SHORT).show();
            return;
        }
        // update friend list
        friendList.add(currentUserEmail);
        userInstance.getFriendList().add(friendEmail);
        userInstance.saveUserToFirestore();
        updatePage();
        db.collection(Constants.users)
                .document(friendDoc.getId())
                .update(Constants.friendList, friendList)
                .addOnSuccessListener(updateVoid -> {
                    Log.d("FriendListUpdate", "Current user email added to friend's friend list successfully.");

                })
                .addOnFailureListener(e ->
                        handleFailure("Failed to update friend's friend list", e)
                );
    }

    @SuppressLint("SetTextI18n")
    private void updateFriendInfo(DocumentSnapshot document) {
        // get user data
        String email = document.getString("email");
        String friendName = document.getString(Constants.userName);
        String friendGender = document.getString(Constants.gender);
        int age = Objects.requireNonNull(document.getLong("age")).intValue();

        friendEmailShow.setText("Email: " + email);
        friendNameShow.setText("Name: " + friendName);

        // show age and gender
        if (private_info.containsKey(email) && Boolean.TRUE.equals(private_info.get(email))) {
            genderShow.setText("Gender: Private");
            ageShow.setText("Age: Private");
        } else {
            genderShow.setText("Gender: " + friendGender);
            ageShow.setText("Age: " + age);
        }
        // load avatar
        String avatarUri = document.getString("avatar");
        if (avatarUri != null && !avatarUri.isEmpty()) {
            try {
                loadAvatar(avatarUri);
            } catch (FileNotFoundException e) {
                Log.e("LoadAvatar", "Avatar file not found", e);
            }
        } else {
            Log.d("Firestore", "No avatar found");
        }
    }

    private void deleteFriendRequestForReject(String requestId) {
        db.collection(Constants.friendRequest)
                .document(requestId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("reject", "Friend request successfully deleted.");
                    Toast.makeText(this, "Friend request rejected successfully.", Toast.LENGTH_SHORT).show();
                    updatePage();
                })
                .addOnFailureListener(e -> {
                    handleRejectFailure("Failed to delete friend request", e);
                });
    }

    private void fetchFriendData(String email) {
        db.collection(Constants.users)
                .whereEqualTo(Constants.Email, email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        updateFriendInfo(document);
                    } else {
                        handleFetchError("No user found with the specified email.", task.getException());
                    }
                })
                .addOnFailureListener(e -> handleFetchError("Error fetching user data", e));
    }

    //handle Failures for different method
    private void handleFailure(String message, Exception e) {
        updatePage();
        if (e != null) {
            Log.e("FriendRequest", message + ": " + e.getMessage());
        } else {
            Log.e("FriendRequest", message);
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void handleRejectFailure(String message, Exception e) {
        updatePage();
        if (e != null) {
            Log.e("reject", message + ": " + e.getMessage());
        } else {
            Log.d("reject", message);
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void handleFetchError(String message, Exception e) {
        Log.e("Firestore", message, e);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}