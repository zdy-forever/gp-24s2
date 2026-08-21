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
public class AccountExp extends Exp {
    private final String account;



    /**
     * Constructor for AccountExp class.
     * Initializes the account field with the provided account string.
     *
     * @param account The account string associated with this expression.
     */
    public AccountExp(String account) {
        this.account = account;
    }


    /**
     * Returns the account as a string.
     *
     * @return The account string.
     */
    @Override
    public String show() {
        return account;
    }


    /**
     * Executes the search for new friends based on the provided account.
     * The method uses Firebase Firestore to perform queries and filters based on account name, gender,
     * and age range. Results are stored in the `find_friend_result`, `find_friend_email`, and `find_friend_name` lists.
     * It also checks if the found users are in the super blacklist, and only adds users who are not blacklisted.
     * Additionally, the user's online status is checked and stored.
     */
    public void execute()
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<Task<Void>> allTasks = new ArrayList<>();

        // clear find_friend_result
        FindNewFriend.ClearFindFriendResult();
        // generate new token
        TokenizerForFindFriend tokenizer = new TokenizerForFindFriend(account);
        TokenForFindFriend token2 = tokenizer.current();


        while (tokenizer.hasNext()) {
            Query query = db.collection("users");

            //  check if it is account
            if (token2.getType().equals(TokenForFindFriend.Type.ACCOUNT)) {
                query = query.whereEqualTo("userName", token2.getToken());
                Log.d("TokenType", token2.getType().toString());
                Log.d("Token", token2.getToken());
                break;
            }

            String gender=ParserForFindFriend.gender;
            int age_limit_min = ParserForFindFriend.age_limit_min;
            int age_limit_max=ParserForFindFriend.age_limit_max;

            if (gender != null) {
                query = query.whereEqualTo("gender", gender);
                Log.d("GenderFilter", gender);
            }
            if (age_limit_min != -1 && age_limit_max != -1) {
                query = query.whereGreaterThanOrEqualTo("age", age_limit_min)
                        .whereLessThanOrEqualTo("age", age_limit_max);
                Log.d("AgeRange", age_limit_min + " " + age_limit_max);
            }

            // do query and add task to task queue
            Task<Void> task = query.get().continueWithTask(queryTask -> {
                if (queryTask.isSuccessful()) {
                    List<Task<Void>> innerTasks = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryTask.getResult()) {
                        String name = document.getString("userName");
                        String account = document.getString("email");
                        if(account!=null){count_this_time++;}
                        String userInfo = "Username: " + name + ", Account: " + account;
                        String isOnline = String.valueOf(document.getBoolean("is_online"));
                        // check super_blacklist
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
                                            // check online statue
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

        Tasks.whenAll(allTasks).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("AllTasks", "All tasks completed successfully.");
                if (count_this_time==0) {
                    // if it is invalid search(search nothing), then use invalidParse()
                    InvalidSearch.invalidParse(token2);
                } else {
                    // if search is valid
                    Log.d("Parse", "Found results: " + find_friend_result.toString());
                }
            } else {
                Log.e("AllTasks", "Some tasks failed.", task.getException());
            }
        });
    }
}




