package com.example.smartcity.tools;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @author :Hanjian Jin
 * UID: u7905060
 *
 */
public class UserGenerator {
    private FirebaseFirestore db;

    public UserGenerator() {
        // Initialize Firestore instance
        db = FirebaseFirestore.getInstance();
    }

    // Automatically generate 5 users and store them in Firestore
    public void generateRandomUsers() {
        // Generate 5 random user data
        for (int i = 0; i < 5; i++) {
            String randomEmail = generateSakuraEmail(); // Generate email in the format Sakura<number>@goodjob.com
            String randomUserName = "User" + new Random().nextInt(1000);
            String randomGender = new Random().nextBoolean() ? "Male" : "Female";
            int randomAge = new Random().nextInt(60) + 18;

            // Randomly generate an email for the friend list and blacklist
            String randomFriendEmail = generateGoodBuddyEmail();
            String randomBlackListEmail = generateBadBuddyEmail();

            // Create user data
            Map<String, Object> userData = new HashMap<>();
            userData.put("email", randomEmail);
            userData.put("userName", randomUserName);
            userData.put("gender", randomGender);
            userData.put("age", randomAge);
            userData.put("avatar", null);  // Default avatar is null
            userData.put("is_online", false);  // Default is_online is false
            userData.put("friend_list", generateListWithRandomEmail(randomFriendEmail));  // Randomly generate a friend
            userData.put("black_list", generateListWithRandomEmail(randomBlackListEmail));  // Randomly generate a blacklist user

            // Store user data in Firestore's "users" collection, using email as the document ID
            db.collection("users")
                    .document(randomEmail)  // Use email as the document ID
                    .set(userData)          // Use set() method to store data
                    .addOnSuccessListener(aVoid -> {
                        System.out.println("User stored successfully: " + randomEmail);
                    })
                    .addOnFailureListener(e -> {
                        System.out.println("Failed to store user: " + e.getMessage());
                    });
        }
    }

    // Generate a random email in the format GoodBuddy<number>@nicejob.com for the friend list
    private String generateGoodBuddyEmail() {
        return "GoodBuddy" + new Random().nextInt(10000) + "@nicejob.com";
    }

    // Generate a random email in the format BadBuddy<number>@WTFjob.com for the blacklist
    private String generateBadBuddyEmail() {
        return "BadBuddy" + new Random().nextInt(10000) + "@WTFjob.com";
    }

    // Generate a random email for users in the format Sakura<number>@goodjob.com
    private String generateSakuraEmail() {
        return "Sakura" + new Random().nextInt(10000) + "@goodjob.com";
    }

    // Create a list containing a randomly generated email (for friend_list and black_list)
    private List<String> generateListWithRandomEmail(String randomEmail) {
        List<String> emailList = new ArrayList<>();
        emailList.add(randomEmail);
        return emailList;
    }
}