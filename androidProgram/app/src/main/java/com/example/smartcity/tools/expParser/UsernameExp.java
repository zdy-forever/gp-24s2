package com.example.smartcity.tools.expParser;

import static com.example.smartcity.FindNewFriend.find_friend_email;
import static com.example.smartcity.FindNewFriend.find_friend_name;
import static com.example.smartcity.FindNewFriend.find_friend_online;
import static com.example.smartcity.FindNewFriend.find_friend_result;
import static com.example.smartcity.tools.expParser.ParserForFindFriend.count_this_time;


import android.util.Log;

import com.example.smartcity.FindNewFriend;
import com.example.smartcity.tools.User;
import com.example.smartcity.tools.tokenTokenizer.TokenForFindFriend;
import com.example.smartcity.tools.tokenTokenizer.TokenizerForFindFriend;
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
public class UsernameExp extends Exp {
    private final String username;


    /**
     * Constructor for the UsernameExp class.
     * Initializes the UsernameExp with the provided username.
     *
     * @param username The username string to be used in the expression.
     */
    public UsernameExp(String username) {
        this.username = username;
    }

    /**
     * Returns the string representation of the username.
     *
     * @return The username string.
     */
    @Override
    public String show() {
        return username;
    }

    /**
     * Executes the search operation based on the username.
     * The method performs a Firestore query to find users based on the username and applies additional filters
     * such as gender and age. It checks if the users are in the super blacklist and processes the results accordingly.
     */
    public void execute()
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<Task<Void>> allTasks = new ArrayList<>();

        // clear
        FindNewFriend.ClearFindFriendResult();
        TokenizerForFindFriend tokenizer = new TokenizerForFindFriend(username);
        TokenForFindFriend token1 = tokenizer.current();
        // generate token
        while (tokenizer.hasNext()) {
            Query query = db.collection("users");

            // Token type check
            if (token1.getType().equals(TokenForFindFriend.Type.USERNAME)) {
                query = query.whereEqualTo("userName", token1.getToken());
                Log.d("TokenType", token1.getType().toString());
                Log.d("Token", token1.getToken());
                break;
            }

            String gender=ParserForFindFriend.gender;
            int age_limit_min = ParserForFindFriend.age_limit_min;
            int age_limit_max=ParserForFindFriend.age_limit_max;
            // use age and gender filter
            if (gender != null) {
                query = query.whereEqualTo("gender", gender);
                Log.d("GenderFilter", gender);
            }
            if (age_limit_min != -1 && age_limit_max != -1) {
                query = query.whereGreaterThanOrEqualTo("age", age_limit_min)
                        .whereLessThanOrEqualTo("age", age_limit_max);
                Log.d("AgeRange", age_limit_min + " " + age_limit_max);
            }

            // do query
            Task<Void> task = query.get().continueWithTask(queryTask -> {
                if (queryTask.isSuccessful()) {
                    List<Task<Void>> innerTasks = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryTask.getResult()) {
                        String name = document.getString("userName");
                        String account = document.getString("email");
                        if(account!=null){count_this_time++;}
                        String userInfo = "Username: " + name + ", Account: " + account;
                        String isOnline = String.valueOf(document.getBoolean("is_online"));

                        // 检查 super_blacklist
                        Query query_SBL = db.collection("users").whereEqualTo("email", account);
                        Task<Void> innerTask = query_SBL.get().continueWith(innerQueryTask -> {
                            if (innerQueryTask.isSuccessful()) {
                                for (QueryDocumentSnapshot document2 : innerQueryTask.getResult()) {
                                    List<String> superBlackList = (List<String>) document2.get("super_blacklist");
                                    if (superBlackList == null || !superBlackList.contains(User.getInstance().getEmail())) {
                                        synchronized (find_friend_result) {
                                            find_friend_result.add(userInfo);
                                            find_friend_email.add(account);
                                            find_friend_name.add(name);
                                            if(isOnline.isEmpty())
                                            {
                                                find_friend_online.add("false");
                                            }
                                            else
                                            {
                                                find_friend_online.add(isOnline);
                                            }

                                        }
                                        Log.d("FindFriendResult", find_friend_result.toString());
                                    } else {
                                        Log.d("FindFriendResult", User.getInstance().getEmail() + " is in superBlackList");
                                    }
                                }
                                Log.d("isOnline",find_friend_online.toString());
                            }
                            return null;
                        });
                        innerTasks.add(innerTask);
                    }
                    return Tasks.whenAll(innerTasks);
                } else {
                    Log.e("ParseError", "Error getting documents", queryTask.getException());
                    return null;
                }
            });
            allTasks.add(task);

            tokenizer.next();

        }

        // wait for all tasks to complete
        Tasks.whenAll(allTasks).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("AllTasks", "All tasks completed successfully.");
                if (count_this_time==0) {
                    InvalidSearch.invalidParse(token1);
                } else {
                    Log.d("Parse", "Found results: " + find_friend_result.toString());
                }
            } else {
                Log.e("AllTasks", "Some tasks failed.", task.getException());
            }
        });
    }
}


