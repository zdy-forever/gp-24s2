package com.example.smartcity.tools.expParser;

import static com.example.smartcity.FindNewFriend.find_friend_email;
import static com.example.smartcity.FindNewFriend.find_friend_name;
import static com.example.smartcity.FindNewFriend.find_friend_online;
import static com.example.smartcity.FindNewFriend.find_friend_result;
import static com.example.smartcity.tools.expParser.ParserForFindFriend.count_this_time;

import android.util.Log;

import com.example.smartcity.tools.User;
import com.example.smartcity.tools.tokenTokenizer.TokenForFindFriend;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
/**
 * @author : Daoyan Zhu
 * UID: u7782042
 */
public class InvalidSearch {

    private static final String gender=ParserForFindFriend.gender;
    private static final int age_limit_min=ParserForFindFriend.age_limit_min;
    private static final int age_limit_max=ParserForFindFriend.age_limit_max;
    private final String unknow;

    /**
     * Constructor for InvalidSearch class.
     * Initializes the unknown string that represents an invalid or unknown search input.
     *
     * @param s The invalid or unknown search input string.
     */
    public InvalidSearch(String s)
    {
        this.unknow=s;
    }
    /**
     * Returns the unknown string as a string.
     * @return The unknown string.
     */
    public String show()
    {
        return unknow;
    }


    /**
     * Executes the invalid parse operation based on the provided token.
     * @param token
     */
    public static void invalidParse(TokenForFindFriend token) {
        count_this_time=0;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final Query[] invalidQuery = {db.collection("users")};
        // use gender and age filter
        if (gender != null) {
            invalidQuery[0] = invalidQuery[0].whereEqualTo("gender", gender);
            Log.d("GenderFilter", gender);
        }
        if (age_limit_min != -1 && age_limit_max != -1) {
            invalidQuery[0] = invalidQuery[0].whereGreaterThanOrEqualTo("age", age_limit_min)
                    .whereLessThanOrEqualTo("age", age_limit_max);
            Log.d("AgeRange", age_limit_min + " " + age_limit_max);
        }

        // query from keyword
        invalidQuery[0] = invalidQuery[0].whereArrayContains("keywords", token.getToken());
        Log.d("Parser2", "It is ok till now");
        invalidQuery[0].get().addOnCompleteListener(task1 -> {
            if (task1.isSuccessful()) {
                List<Task<Void>> innerTasks = new ArrayList<>();
                for (QueryDocumentSnapshot document1 : task1.getResult()) {
                    String name1 = document1.getString("userName");
                    String account1 = document1.getString("email");
                    String userInfo1 = "Username: " + name1 + ", Account: " + account1;
                    String isOnline1 = String.valueOf(document1.getBoolean("is_online"));
                    // check super_blacklist
                    Query query_SBL = db.collection("users").whereEqualTo("email", account1);
                    Task<Void> innerTask = query_SBL.get().continueWith(innerQueryTask -> {
                        if (innerQueryTask.isSuccessful()) {
                            for (QueryDocumentSnapshot document2 : innerQueryTask.getResult()) {
                                List<String> superBlackList = (List<String>) document2.get("super_blacklist");
                                if (superBlackList == null || !superBlackList.contains(User.getInstance().getEmail())) {
                                    synchronized (find_friend_result) {
                                        find_friend_result.add(userInfo1);
                                        find_friend_email.add(account1);
                                        find_friend_name.add(name1);
                                        if(isOnline1.isEmpty())
                                        {
                                            find_friend_online.add("false");
                                        }
                                        else
                                        {
                                            find_friend_online.add(isOnline1);
                                        }
                                    }
                                    Log.d("FindFriendResult", find_friend_result.toString());
                                } else {
                                    Log.d("FindFriendResult", User.getInstance().getEmail() + " is in superBlackList");
                                }
                            }
                        }
                        return null;
                    });
                    innerTasks.add(innerTask);
                }
                // wait for all inner tasks to complete
                Tasks.whenAll(innerTasks).addOnCompleteListener(innerTaskResult -> {
                    if (innerTaskResult.isSuccessful()) {
                        Log.d("InvalidParse", "All inner tasks completed successfully.");
                        if (find_friend_result.isEmpty()) {
                            Log.d("InvalidParse", "No results found after invalid parse.");
                        } else {
                            Log.d("InvalidParse", "Found results: " + find_friend_result.toString());
                        }
                    } else {
                        Log.e("InvalidParse", "Some inner tasks failed.", innerTaskResult.getException());
                    }
                });
            } else {
            }
        });



    }
}
