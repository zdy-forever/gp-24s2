package com.example.smartcity;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartcity.tools.Constants;
import com.example.smartcity.tools.expParser.FinalExp;
import com.example.smartcity.tools.expParser.ParserForFindFriend;
import com.example.smartcity.tools.expParser.SearchExe;
import com.example.smartcity.tools.User;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author : Daoyan Zhu
 * UID: u7782042
 * @author : Shangyi Shen
 * UID: u7735222
 */
public class FindNewFriend extends AppCompatActivity {
    public static ArrayList<String> find_friend_result = new ArrayList<>();
    public static ArrayList<String> find_friend_email = new ArrayList<>();
    public static ArrayList<String> find_friend_name = new ArrayList<>();
    public static ArrayList<String> find_friend_online = new ArrayList<>();
    private static int search_times = 0;
    private User userInstance;
    private static Boolean private_account = true;
    private ArrayList<String> private_email = new ArrayList<>();
    private boolean show_click = false;
    private boolean search_click = false;
    String currentInput = "";
    String lastInput = "";
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();

        // initialize the variables
        find_friend_email.clear();
        find_friend_name.clear();
        find_friend_result.clear();
        find_friend_online.clear();
        private_email.clear();

        setContentView(R.layout.find_new_friend);

        EditText input = findViewById(R.id.find_newfriend_input);
        ImageView search = findViewById(R.id.find_newfriend_search);
        ListView result = findViewById(R.id.find_newfriend_result);
        TextView agefilter = findViewById(R.id.find_newfriend_age_filter);
        TextView genderfilter = findViewById(R.id.find_newfriend_gender_filter);
        Button show = findViewById(R.id.find_newfriend_show);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, find_friend_result);
        result.setAdapter(adapter);

        userInstance = User.getInstance();

        search.setOnClickListener(
                v -> {
                    lastInput = currentInput;
                    currentInput = input.getText().toString();
                    if (!lastInput.equals(currentInput)) {
                        search_click = false;
                    }
                    if (input.getText().toString().isEmpty()) {
                        Toast.makeText(this, "Can not do search with empty input", Toast.LENGTH_SHORT).show();
                    } else if (input.getText().toString().endsWith(";") || input.getText().toString().startsWith(";")) {
                        Toast.makeText(this, "Can not do search if input end with ;", Toast.LENGTH_SHORT).show();
                    } else if (search_click) {
                        Toast.makeText(this, "search has already done", Toast.LENGTH_SHORT).show();
                    }
                    // when input is not empty, then search
                    else {
                        show_click = false;
                        search_click = true;
                        if (search_times == 0) {
                            search_times++;
                        } else {
                            ClearFindFriendResult();
                            adapter.notifyDataSetChanged();
                        }
                        // make sure input is not null
                        if (!input.getText().toString().isEmpty()) {
                            String target = input.getText().toString();
                            ParserForFindFriend parser = new ParserForFindFriend(target);
                            FinalExp inputInParser = parser.parse();
                            SearchExe searching = new SearchExe();
                            searching.execute(inputInParser);
                            //private account remove from search result
                            Query query = db.collection(Constants.users);
                            query = query.whereEqualTo("private_account", private_account);
                            query.get().addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        String email = document.getString("email");
                                        Log.d("1", email);
                                        private_email.add(email);
                                    }
                                }
                            });
                            Toast.makeText(this, "search finished", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        // show button
        show.setOnClickListener(
                // show the result
                v -> {
                    if (find_friend_result.isEmpty()) {
                        Toast.makeText(this, "no user found", Toast.LENGTH_SHORT).show();
                    } else if (show_click) {
                        Toast.makeText(this, "result has already showed", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "result showed", Toast.LENGTH_SHORT).show();
                        // reorder the result according to online status
                        for (int i = 0; i < find_friend_online.size(); i++) {
                            String online = find_friend_online.get(i);
                            if (Objects.equals(online, "true")) {
                                find_friend_result.set(i, find_friend_result.get(i) + "  online");
                            } else {
                                find_friend_result.set(i, find_friend_result.get(i) + "  offline");
                            }
                        }
                        int x = 0;
                        for (int i = 0; i < find_friend_online.size(); i++) {
                            String online = find_friend_online.get(i);
                            if (Objects.equals(online, "true") &&
                                    Objects.equals(find_friend_online.get(x), "false")) {
                                Collections.swap(find_friend_result, i, x);
                                Collections.swap(find_friend_email, i, x);
                                Collections.swap(find_friend_name, i, x);
                                Collections.swap(find_friend_online, i, x);
                                x++;
                            }
                        }
                        // remove the private account
                        for (int i = 0; i < find_friend_email.size(); i++) {
                            if (private_email.contains(find_friend_email.get(i))) {
                                find_friend_result.remove(i);
                                find_friend_email.remove(i);
                                find_friend_name.remove(i);
                                find_friend_online.remove(i);
                            }
                        }
                        adapter.notifyDataSetChanged();
                        show_click = true;
                        search_click = false;
                    }
                });


        result.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String friendInformation = (String) parent.getItemAtPosition(position);
                Log.d("FriendName", "Selected friend: " + friendInformation);
                Pattern pattern = Pattern.compile("Account:\\s*([\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6})");
                Matcher matcher = pattern.matcher(friendInformation);
                String email = null;
                if (matcher.find()) {
                    email = matcher.group(1);
                    Log.d("ExtractedEmail", "Email: " + email);
                } else {
                    // if there is nothing matched
                    Log.d("ExtractedEmail", "No email found in friendInformation.");
                }

                String finalEmail = email;
                if (userInstance.getFriendList().contains(finalEmail) || Objects.equals(finalEmail, userInstance.getEmail())) {
                    Toast.makeText(FindNewFriend.this, "you are already friends", Toast.LENGTH_SHORT).show();
                    return;
                }
                new AlertDialog.Builder(FindNewFriend.this)
                        .setTitle("friend application")
                        .setMessage("Are you sure to add this friend？")
                        .setPositiveButton("Yes", (dialog, which) ->
                                sendFriendRequestToDatabase(finalEmail)
                        )
                        .setNegativeButton("Cancel", (dialog, which) ->
                                // user canceled, close the pop window
                                dialog.dismiss()
                        )
                        .show();
            }
        });

        // firer button, witch can select age range
        agefilter.setOnClickListener(
                v -> {
                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
                    LayoutInflater inflater = this.getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.find_new_friend_age_range_filter, null);
                    RadioGroup radioGroup = dialogView.findViewById(R.id.find_new_friend_age_range_filter);
                    builder.setTitle("Select an Option");
                    builder.setView(dialogView);
                    builder.setPositiveButton("YES", (dialog, which) -> {
                        int selectedId = radioGroup.getCheckedRadioButtonId();
                        if (selectedId == R.id.find_new_friend_age_range1) {
                            ParserForFindFriend.ChooseAgeRange(0, 12);
                            Toast.makeText(this, "0-12 years old is the target", Toast.LENGTH_SHORT).show();
                        } else if (selectedId == R.id.find_new_friend_age_range2) {
                            ParserForFindFriend.ChooseAgeRange(13, 18);
                            Toast.makeText(this, "13-18 years old is the target", Toast.LENGTH_SHORT).show();
                        } else if (selectedId == R.id.find_new_friend_age_range3) {
                            ParserForFindFriend.ChooseAgeRange(19, 26);
                            Toast.makeText(this, "19-26 years old is the target", Toast.LENGTH_SHORT).show();
                        } else if (selectedId == R.id.find_new_friend_age_range4) {
                            ParserForFindFriend.ChooseAgeRange(27, 35);
                            Toast.makeText(this, "27-35 years old is the target", Toast.LENGTH_SHORT).show();
                        } else if (selectedId == R.id.find_new_friend_age_range5) {
                            ParserForFindFriend.ChooseAgeRange(36, 48);
                            Toast.makeText(this, "36-48 years old is the target", Toast.LENGTH_SHORT).show();
                        } else if (selectedId == R.id.find_new_friend_age_range6) {
                            ParserForFindFriend.ChooseAgeRange(49, 69);
                            Toast.makeText(this, "49-69 years old is the target", Toast.LENGTH_SHORT).show();
                        } else if (selectedId == R.id.find_new_friend_age_range7) {
                            ParserForFindFriend.ChooseAgeRange(70, 120);
                            Toast.makeText(this, "70-120 years old is the target", Toast.LENGTH_SHORT).show();
                        } else {
                            ClearAgeFilter();
                            Toast.makeText(this, "Cancel your option", Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.setNegativeButton("CANCEL", (dialog, which) ->
                            dialog.dismiss()
                    );
                    androidx.appcompat.app.AlertDialog dialog = builder.create();
                    dialog.show();

                }
        );

        // firer button, witch can select gender
        genderfilter.setOnClickListener(
                v -> {
                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
                    LayoutInflater inflater = this.getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.accountsettings_gender_select, null);
                    RadioGroup radioGroup = dialogView.findViewById(R.id.account_settings_gender_selection);
                    builder.setTitle("Select an Option");
                    builder.setView(dialogView);
                    builder.setPositiveButton("YES", (dialog, which) -> {
                        int selectedId = radioGroup.getCheckedRadioButtonId();
                        if (selectedId == R.id.account_settings_gender_selection_male) {
                            ParserForFindFriend.ChooseGender("Male");
                            Toast.makeText(this, "Male is the target", Toast.LENGTH_SHORT).show();
                        } else if (selectedId == R.id.account_settings_gender_selection_female) {
                            ParserForFindFriend.ChooseGender("Female");
                            Toast.makeText(this, "Female is the target", Toast.LENGTH_SHORT).show();
                        } else if (selectedId == R.id.account_settings_gender_selection_cancel_option) {
                            ClearAgeFilter();
                            Toast.makeText(this, "Cancel your option", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "No option selected", Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.setNegativeButton("CANCEL", (dialog, which) ->
                            dialog.dismiss()
                    );
                    androidx.appcompat.app.AlertDialog dialog = builder.create();
                    dialog.show();

                }
        );

    }

    @Override
    protected void onStart() {
        super.onStart();
        String userEmail = userInstance.getEmail();
        // update friendlist
        db.collection("users").document(userEmail)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            List<String> friendList = (List<String>) document.get("friend_list");
                            if (friendList != null) {
                                Log.d("FriendList", friendList.toString());
                                userInstance.setFriendList(friendList);
                            } else {
                                Log.d("FriendList", "friend_list is null");
                            }
                        } else {
                            Log.d("FriendList", "No such document");
                        }
                    } else {
                        Log.w("FriendList", "Get failed with ", task.getException());
                    }
                });

    }

    protected void onResume() {
        super.onResume();
        //update online status
        if (!User.getInstance().isOnline()) {
            User.getInstance().setOnline(true);
        }
        Log.d("isOnline", "Find new friend onResume " + User.getInstance().isOnline());
    }

    protected void onPause() {
        super.onPause();
        //update online status
        if (User.getInstance().isOnline()) {
            User.getInstance().setOnline(false);
        }
        Log.d("isOnline", "Find new friend onPause " + User.getInstance().isOnline());
    }


    public static void ClearFindFriendResult() {
        // reset the find friend result
        find_friend_result.clear();
        find_friend_email.clear();
        find_friend_name.clear();
        find_friend_online.clear();
    }

    public static void ClearAgeFilter() {
        // reset the age filter
        ParserForFindFriend.ChooseAgeRange(-1, -1);
    }

    public static void ClearGenderFilter() {
        // reset the gender filter
        ParserForFindFriend.ChooseGender(null);
    }

    protected void onDestroy() {
        super.onDestroy();
        //reset all
        ClearFindFriendResult();
        ClearAgeFilter();
        ClearGenderFilter();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent returnIntent = new Intent();
        returnIntent.putExtra("data_key", 0);
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    private void sendFriendRequestToDatabase(String friendEmail) {
        String currentUserEmail = userInstance.getEmail();

        checkExistingFriendRequest(db, currentUserEmail, friendEmail, exists -> {
            if (exists) {
                Toast.makeText(FindNewFriend.this, "Please check request in friend request page", Toast.LENGTH_SHORT).show();
            } else {
                checkSentFriendRequest(db, currentUserEmail, friendEmail, requestExists -> {
                    if (requestExists) {
                        Toast.makeText(FindNewFriend.this, "Friend request already exists.", Toast.LENGTH_SHORT).show();
                    } else {
                        sendFriendRequest(db, currentUserEmail, friendEmail);
                    }
                });
            }
        });
    }

    /**
     * Check if there are pending friend requests from other users.
     * if there exists, add to list view
     */
    private void checkExistingFriendRequest(FirebaseFirestore db, String currentUserEmail, String friendEmail, RequestCallback callback) {
        db.collection(Constants.friendRequest)
                .whereEqualTo("toUser", currentUserEmail)
                .whereEqualTo("status", "pending")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<String> friendResults = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            String fromUser = document.getString("fromUser");
                            if (fromUser != null) {
                                friendResults.add(fromUser);
                            }
                        }
                        callback.onResult(friendResults.contains(friendEmail));
                    } else {
                        Log.w("Firestore", "Error getting documents.", task.getException());
                    }
                });
    }

    /**
     * Check if a friend request has already been sent by the current user.
     */
    private void checkSentFriendRequest(FirebaseFirestore db, String currentUserEmail, String friendEmail, RequestCallback callback) {
        db.collection("friend_requests")
                .whereEqualTo("fromUser", currentUserEmail)
                .whereEqualTo("toUser", friendEmail)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        callback.onResult(true);
                    } else {
                        callback.onResult(false);
                    }
                });
    }

    /**
     * Send the friend request to the Firestore database and notify the friend.
     */
    private void sendFriendRequest(FirebaseFirestore db, String currentUserEmail, String friendEmail) {
        Map<String, Object> request = new HashMap<>();
        request.put("fromUser", currentUserEmail);
        request.put("toUser", friendEmail);
        request.put("status", "pending");

        db.collection("friend_requests")
                .add(request)
                .addOnSuccessListener(documentReference -> {
                    sendNotification(db, currentUserEmail, friendEmail);
                    Toast.makeText(FindNewFriend.this, "Friend request has been sent", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error sending friend request", e);
                    Toast.makeText(FindNewFriend.this, "Failed to send friend request", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Sends a notification about the friend request to the friend.
     */
    private void sendNotification(FirebaseFirestore db, String currentUserEmail, String friendEmail) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("text", "You have a new friend request");
        notification.put("from", currentUserEmail);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        notification.put("time", sdf.format(new Date()));

        Map<String, Object> updateNotifications = new HashMap<>();
        updateNotifications.put("notifications", FieldValue.arrayUnion(notification));

        db.collection("notifications")
                .document(friendEmail)
                .set(updateNotifications, SetOptions.merge())
                .addOnSuccessListener(aVoid -> Log.d("Firebase", "Notification successfully added."))
                .addOnFailureListener(e -> Log.w("Firebase", "Error adding notification", e));
    }

    /**
     * Callback interface for asynchronous operations related to friend requests.
     */
    interface RequestCallback {
        void onResult(boolean exists);
    }

}
