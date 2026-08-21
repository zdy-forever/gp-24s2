package com.example.smartcity.tools;

import android.annotation.SuppressLint;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Source;

import androidx.annotation.NonNull;

/**
 * @author : Shangyi Shen
 * UID: u7735222
 * @author :Lanping Hu
 * UID: u7904927
 * @author :Hanjian Jin
 * UID: u7905060
 *
 */

public class User implements Serializable {
    private static User instance;

    private int age;
    private String userName, email, gender;
    private HashMap<String, Map<String, Object>> request = new HashMap<>();
    private List<String> friendList;
    private List<String> blackList;
    private List<String> superBlacklist;
    private String token;
    private List<String> keywords;
    private static final String TAG = "User";
    private String avatar;
    private boolean private_account = false;
    private boolean is_online = true;
    private static boolean private_information = false;

    public boolean setting_friend_request;
    public boolean setting_friend_message;
    public boolean setting_booking_success;
    public boolean setting_booking_near;
    public boolean setting_report_success;
    public boolean setting_report_feedback;
    public boolean setting_low_time_limit;

    private String subscribedAddress;

    @SuppressLint("StaticFieldLeak")
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();

    public User() {
        private_information = false;
        is_online = true;
        setting_friend_request=false;
        setting_friend_message=false;
        setting_booking_success=false;
        setting_booking_near=false;
        setting_report_success=false;
        setting_report_feedback=false;
        setting_low_time_limit=false;
    }


    private User(String email) {
        this.age = 20;
        this.email = email;
        private_information = false;
    }

    public User(int age, String userName, String email, String gender, String avatar) {
        this.age = age;
        this.userName = userName;
        this.email = email;
        this.gender = gender;
        this.avatar = avatar;
        this.private_information = false;
        this.is_online = true;

        setting_friend_request = false;
        setting_friend_message = false;
        setting_booking_success = false;
        setting_booking_near = false;
        setting_report_success = false;
        setting_report_feedback = false;
        setting_low_time_limit = false;
    }



    private User(int age, String userName, String email, String gender, String avatar, List<String> friendList, List<String> blackList, List<String> superBlacklist, boolean private_account) {
        this.age = age;
        this.userName = userName;
        this.email = email;
        this.gender = gender;
        this.avatar = avatar;
        this.friendList = friendList;
        this.blackList = blackList;
        this.superBlacklist = superBlacklist;
        this.private_account = private_account;
        this.private_information = false;

        setting_friend_request = false;
        setting_friend_message = false;
        setting_booking_success = false;
        setting_booking_near = false;
        setting_report_success = false;
        setting_report_feedback = false;
        setting_low_time_limit = false;
    }


    // Get the existing instance; if the instance has not been created, throw an exception
    public static synchronized User getInstance() {
        if (instance == null) {
            throw new IllegalStateException("User not initialized. Please login first.");
        }
        return instance;
    }

    // During registration, no need for asynchronous operation, just write the email
    public static synchronized User getInstance(String email) {
        if (instance == null) {
            instance = new User(email);
            instance.saveUserToFirestore( ) ;
            Log.d(TAG, "successfully save for sign up ");
            return instance;
        }
        Log.d(TAG, "return instance: " + instance);
        return instance;
    }

    // When re-entering start, asynchronously fetch user data
    public static synchronized void getInstance(String email, UserCallback callback) {
        if (instance == null) {
            fetchUserData(email, callback); // Asynchronously fetch user data
        } else {
            callback.onUserLoaded(instance);  // If the instance exists, return immediately
        }
    }

    // Method to asynchronously fetch user data
    private static void fetchUserData(String email, UserCallback callback) {
        // try to fetch with cache first
        db.collection("users").document(email)
                .get(Source.CACHE)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            handleUserData(document, email, callback);
                        } else {
                            // if there is no data in cache, get data from server
                            fetchUserDataFromServer(email, callback);
                        }
                    } else {
                        //if fail to get data from cache, get data from server
                        fetchUserDataFromServer(email, callback);
                    }
                });
    }

    private static void fetchUserDataFromServer(String email, UserCallback callback) {
        db.collection("users").document(email).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                handleUserData(document, email, callback);
                            } else {
                                callback.onUserLoaded(null);
                            }
                        } else {
                            callback.onUserLoaded(null);
                        }
                    }
                });
    }

    private static void handleUserData(DocumentSnapshot document, String email, UserCallback callback) {
        Long ageLong = document.getLong("age");
        int age = (ageLong != null) ? ageLong.intValue() : 20;
        String username = document.getString("userName");
        String gender = document.getString("gender");
        String avatar = document.getString("avatar");
        List<String> friendList = (List<String>) document.get("friend_list");
        List<String> blackList = (List<String>) document.get("black_list");
        boolean private_account = document.getBoolean("private_account") != null && document.getBoolean("private_account");
        Object requestField = document.get("request");
        List<String> superBlacklist = (List<String>) document.get("super_blacklist");
        boolean is_online = document.getBoolean("is_online") != null ? document.getBoolean("is_online") : true;
        instance = new User(age, username, email, gender, avatar, friendList, blackList, superBlacklist, private_account);
        instance.setPrivateInformation(private_information);
        instance.is_online = is_online;


       // Since settings are uploaded together, for cases where settings are not uploaded, just check if one of the settings is empty
        instance.setting_friend_request=Boolean.TRUE.equals(document.getBoolean("setting_friend_request"));
        instance.setting_friend_message=Boolean.TRUE.equals(document.getBoolean("setting_friend_message"));
        instance.setting_booking_success=Boolean.TRUE.equals(document.getBoolean("setting_booking_success"));
        instance.setting_booking_near= Boolean.TRUE.equals(document.getBoolean("setting_booking_near"));
        instance.setting_report_success=Boolean.TRUE.equals(document.getBoolean("setting_report_success"));
        instance.setting_report_feedback=Boolean.TRUE.equals(document.getBoolean("setting_report_feedback"));
        instance.setting_low_time_limit=Boolean.TRUE.equals(document.getBoolean("setting_low_time_limit"));

        instance.subscribedAddress=document.getString("subscribedAddress");

        Log.d(TAG, "fetchUserData " + instance);
        callback.onUserLoaded(instance);
    }

    public static synchronized void deleteInstance() {
        instance = null;
    }



    public void addFriend(String friendEmail) {
        if (!friendList.contains(friendEmail)) {
            friendList.add(friendEmail);
            saveUserToFirestore();
        }
    }

    public void blockUser(String emailToBlock) {
        if (!blackList.contains(emailToBlock)) {
            blackList.add(emailToBlock);
            saveUserToFirestore();
        }
    }

    public void saveUserToFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<String> keywordList = new ArrayList<>();

        //if username is null, just generate keyword with email
        if (userName == null || userName.isEmpty()) {
            keywordList.addAll(KeywordGenerator.generateKeywords(email));
        } else {
            // if user is not null, generate keyword with user name
            keywords = KeywordGenerator.generateKeywords(userName);
            keywordList.addAll(KeywordGenerator.generateKeywords(email));
            keywordList.addAll(keywords);
        }
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("age", age);
        //userMap.put("name", name);
        userMap.put("userName", userName);
        userMap.put("email", email);
        userMap.put("gender", gender);
        userMap.put("friend_list", friendList);
        userMap.put("black_list", blackList);
        userMap.put("super_blacklist", superBlacklist);
        userMap.put("private_account", private_account);
        userMap.put("is_online", is_online);
        userMap.put("request", request);
        userMap.put("keywords", keywordList);
        userMap.put("avatar", avatar);
        userMap.put("private_information", private_information);

        // Since the content of notification setting is also bound to the user,
        // SetOptions is needed here to avoid overwriting the settings saved elsewhere
        db.collection("users").document(email).set(userMap, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "User successfully written for ID: " + email);
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error writing user data", e);
                });
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        if (age < 0) {
            Log.d(TAG, "Invalid age: " + age);
            throw new IllegalArgumentException("Age cannot be negative");
        }
        this.age = age;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getSubscribedAddress() {
        return subscribedAddress;
    }
    public void setSubscribedAddress(String address) {
        this.subscribedAddress = address;
        String userEmail = getEmail();
        if (userEmail != null) {
            Log.d(TAG, "Updating subscribedAddress for user: " + userEmail + " to: " + address);
            db.collection("users").document(userEmail)
                    .update("subscribedAddress", address)
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Successfully updated subscribedAddress for user: " + userEmail);
                    })
                    .addOnFailureListener(e -> {
                        Log.w(TAG, "Error updating subscribedAddress for user: " + userEmail, e);
                    });
        } else {
            Log.w(TAG, "User email is null, cannot update subscribedAddress.");
        }
    }

    public List<String> getFriendList() {
        if (friendList == null) {
            friendList = new ArrayList<>();
        }
        return friendList;
    }

    public void setFriendList(List<String> friendList) {
        this.friendList = friendList != null ? friendList : new ArrayList<>();
    }

    public List<String> getBlackList() {
        if (blackList == null) {
            blackList = new ArrayList<>();
        }
        return blackList;
    }

    public void setBlackList(List<String> blackList) {
        this.blackList = blackList != null ? blackList : new ArrayList<>();
    }

    public HashMap<String, Map<String, Object>> getRequest() {
        return request;
    }

    public void setRequest(HashMap<String, Map<String, Object>> request) {
        this.request = request;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public boolean isPrivateAccount() {
        return private_account;
    }

    public void setPrivateAccount(boolean private_account) {
        this.private_account = private_account;
        saveUserToFirestore();
    }

    public boolean isPrivateInformation() {
        return private_information;
    }

    public void setPrivateInformation(boolean private_information) {
        this.private_information = private_information;
        saveUserToFirestore();
    }

    public List<String> getSuperBlacklist() {
        if (superBlacklist == null) {
            superBlacklist = new ArrayList<>();
        }
        return superBlacklist;
    }

    public void setSuperBlacklist(List<String> superBlacklist) {
        this.superBlacklist = superBlacklist != null ? superBlacklist : new ArrayList<>();
    }

    public boolean isOnline() {
        return is_online;
    }

    public void setOnline(boolean is_online) {
        this.is_online = is_online;
        db.collection("users").document(email)
                .update("is_online", is_online)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "User online status updated to: " + is_online))
                .addOnFailureListener(e -> Log.w(TAG, "Error updating online status", e));
    }


    @NonNull
    public String toString() {
        return "User{age = " + age + ", userName = " + userName + ", gender = " + gender + ", email = " + email +
                ", friend_list = " + friendList + ", black_list = " + blackList + "}";
    }
}

