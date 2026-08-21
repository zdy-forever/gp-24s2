package com.example.smartcity;

import static com.example.smartcity.FindNewFriend.find_friend_email;
import static com.example.smartcity.FindNewFriend.find_friend_name;
import static com.example.smartcity.FindNewFriend.find_friend_online;
import static com.example.smartcity.FindNewFriend.find_friend_result;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.smartcity.tools.Constants;
import com.example.smartcity.tools.User;
import com.example.smartcity.tools.Util;
import com.example.smartcity.tree.AVLTree;
import com.example.smartcity.tools.NavigationManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author : Daoyan Zhu
 * UID: u7782042
 * @author :Shangyi Shen
 * UID: u7735222
 */
public class MessagePage extends AppCompatActivity {
    private ImageView add;
    private ImageView friendGenderShow;
    private ImageView ahead;
    private TextView friendNameShow;
    private TextView friendEmailShow;
    private TextView friendAgeShow;
    private Button startChat;
    private ListView result;

    private static final int add_friend_back = 1;
    private static final int p2p_back = 1;
    private String avatarUri;
    private static String friendEmail1;
    String friendName;
    String friendGender;
    int friendAge;
    private FirebaseFirestore db;
    private String userEmail;
    public static ArrayAdapter<String> adapter;
    public static AVLTree<String> friendlist_tree = new AVLTree<>();
    private int enterTimes = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("Messagepage111", "Oncreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messagepage);
        initialize();

        add.setOnClickListener(v -> {
            Intent intent = new Intent(this, FindNewFriend.class);
            startActivityForResult(intent, add_friend_back);
            Toast.makeText(this, "Find new friend", Toast.LENGTH_SHORT).show();
        });

        result.setOnItemClickListener((parent, view, position, id) -> {
            String friendInformation = (String) parent.getItemAtPosition(position);
            Log.d("Friend_list", friendlist_tree.inOrder().toString());
            Log.d("FriendName", "Selected friend: " + friendInformation);
            String friendEmail = extractEmail(friendInformation);
            if (friendEmail != null) {
                fetchAndDisplayFriendData(friendEmail);
            } else {
                Log.d("ExtractedEmail", "No email found in the message.");
            }
        });


        startChat.setOnClickListener(v -> {
            if (friendEmailShow.getText().toString().isEmpty()) {
                Toast.makeText(this, "Please select a friend", Toast.LENGTH_SHORT).show();
            } else {
                P2PMessage.p2pmessage_friend_email = friendEmail1;
                if (friendEmail1 == null || friendEmail1.isEmpty()) {
                    Toast.makeText(this, "Please select a friend", Toast.LENGTH_SHORT).show();
                    return;
                }
                int orientation = getResources().getConfiguration().orientation;
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    P2PMessage.isPortrait = false;
                    Log.d("ScreenOrientation", "Landscape");
                } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                    P2PMessage.isPortrait = true;
                    Log.d("ScreenOrientation", "Portrait");
                }
                Intent intent = new Intent(MessagePage.this, P2PMessage.class);
                intent.putExtra("friendName", friendName);
                intent.putExtra("friendEmail", friendEmail1);
                saveFriendAvatar();
                startActivityForResult(intent, p2p_back);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

    }

    protected void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void updateFriendList() {
        if (enterTimes == 0) {
            adapter.clear();
            fetchFriendListFromFirestore();
            adapter.notifyDataSetChanged();
        } else {
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        //update online status and adapter
        adapter.notifyDataSetChanged();
        updateFriendList();
        enterTimes++;
        if (!User.getInstance().isOnline()) {
            User.getInstance().setOnline(true);
        }
        Log.d("isOnline", "select friend on resume " + User.getInstance().isOnline());
        clearShow();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    protected void onPause() {
        super.onPause();
        //update online status
        if (User.getInstance().isOnline()) {
            User.getInstance().setOnline(false);
        }
        Log.d("isOnline", "select friend on pause " + User.getInstance().isOnline());
    }

    protected void onDestroy() {
        super.onDestroy();
        //reset enterTimes
        enterTimes = 0;
        find_friend_result.clear();
        find_friend_email.clear();
        find_friend_name.clear();
        find_friend_online.clear();
        adapter.clear();
    }


    /**
     * Clears the TextViews for displaying friend data.
     */
    private void clearShow() {
        // clear the show
        ImageView avatar = findViewById(R.id.select_friend_avatar);
        TextView friendNameShow = findViewById(R.id.select_friend_username);
        TextView friendEmailShow = findViewById(R.id.select_friend_email);
        ahead.setVisibility(View.GONE);
        avatar.setImageDrawable(null);
        friendNameShow.setText("");
        friendEmailShow.setText("");
        friendAgeShow.setText("");
        friendGenderShow.setImageDrawable(null);
    }

    /**
     * Extracts the email from a given string using regex.
     *
     * @param friendInformation the string that contains the friend's information
     * @return the extracted email or null if no email is found
     */
    private String extractEmail(String friendInformation) {
        Pattern pattern = Pattern.compile("[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}");
        Matcher matcher = pattern.matcher(friendInformation);
        if (matcher.find()) {
            String friendEmail = matcher.group();
            Log.d("ExtractedEmail", "Email: " + friendEmail);
            return friendEmail;
        }
        return null;
    }

    /**
     * Loads the avatar from the given URI safely.
     *
     * @param avatarUri the URI of the avatar to load
     */
    private void loadAvatarSafely(String avatarUri) {
        try {
            loadAvatar(avatarUri);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == p2p_back && resultCode == RESULT_OK && data != null) {
            // get time from data
            enterTimes = data.getIntExtra("data_key", 0);
            // deal with the time
            Log.d("MessagePage", "Received data: " + enterTimes);

        }
    }

    /**
     * Displays friend data on the UI.
     *
     * @param document the Firestore document containing friend information
     */
    @SuppressLint("SetTextI18n")
    private void displayFriendData(DocumentSnapshot document) {
        // get and set Email
        String friendEmail = document.getString("email");
        Log.d("ExtractedEmail", "Email: " + friendEmail);
        friendEmailShow.setText("Email: " + friendEmail);
        friendEmail1 = friendEmail;

        // get and set UserName
        friendName = document.getString("userName");
        Log.d("ExtractedName", "Name: " + friendName);
        friendNameShow.setText("Name: " + friendName);

        // get and set Age
        friendAge = Objects.requireNonNull(document.getLong("age")).intValue();
        friendAgeShow.setText("Age: " + friendAge);

        // get and set Gender
        friendGender = document.getString("gender");
        if (friendGender != null && !friendGender.isEmpty()) {
            Util.setGenderView(friendGender, friendGenderShow);
        }
        ahead.setVisibility(View.VISIBLE);
        // get and set Avatar
        avatarUri = document.getString("avatar");
        if (avatarUri != null && !avatarUri.isEmpty()) {
            loadAvatarSafely(avatarUri);
        } else {
            Log.d("Firestore", "No avatar found");
        }
    }

    /**
     * Fetches and displays friend data from Firestore using the provided email.
     *
     * @param friendEmail the email of the friend to search for
     */
    private void fetchAndDisplayFriendData(String friendEmail) {
        db.collection(Constants.users)
                .whereEqualTo(Constants.Email, friendEmail)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {
                                DocumentSnapshot document = task.getResult().getDocuments().get(0);
                                clearShow();
                                displayFriendData(document);
                            } else {
                                Log.d("Firestore", "No user found with the specified email.");
                            }
                        } else {
                            Log.d("Firestore", "Error fetching user data: ", task.getException());
                        }
                    }
                });
    }

    private void saveFriendAvatar() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("avatarUri", avatarUri);
        editor.apply();
    }

    /**
     * Loads an avatar image into an ImageView from either a URL or a Base64 string.
     * If the input is a URL, it loads the image using Glide.
     * If the input is a Base64 string, it decodes it into a Bitmap and displays it.
     * If neither is valid, a default avatar image is shown.
     *
     * @param urlOrBase64 The URL or Base64 string representing the avatar image.
     * @throws FileNotFoundException if the file is not found when loading the avatar.
     */
    private void loadAvatar(String urlOrBase64) throws FileNotFoundException {
        ImageView avatar = findViewById(R.id.select_friend_avatar);
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

    // fetch friend information from firestore
    private void fetchFriendListFromFirestore() {
        db.collection(Constants.users).document(userEmail).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                List<String> friendList = (List<String>) task.getResult().get("friend_list");
                friendList = friendList != null ? friendList : new ArrayList<>();
                insertFriendsToAVLTree(friendList); // insert into friend list tree
                updateAdapterWithFriendInfo(friendlist_tree.inOrder()); // update ada[ter
            } else {
                Log.d("FriendList", task.isSuccessful() ? "No such document" : "Task failed", task.getException());
            }
        });
    }

    private void insertFriendsToAVLTree(List<String> friendList) {
        friendList.forEach(email -> friendlist_tree = friendlist_tree.insert(email));
    }

    private void updateAdapterWithFriendInfo(List<String> sortedFriendEmails) {
        sortedFriendEmails.forEach(email -> {
            db.collection(Constants.users).whereEqualTo(Constants.Email, email).get().addOnCompleteListener(friendTask -> {
                if (friendTask.isSuccessful() && !friendTask.getResult().isEmpty()) {
                    DocumentSnapshot friendDoc = friendTask.getResult().getDocuments().get(0);
                    addFriendToAdapter(friendDoc, email);
                } else {
                    Log.d("Friend Info", "No such friend found for email: " + email);
                }
            });
        });
    }

    private void addFriendToAdapter(DocumentSnapshot friendDoc, String email) {
        String friendName = friendDoc.getString(Constants.userName);
        String friendAvatarUrl = friendDoc.getString("avatarUrl");
        List<String> superBlackList = (List<String>) friendDoc.get("super_blacklist");
        if (superBlackList == null || !superBlackList.contains(User.getInstance().getEmail())) {
            adapter.add("Name: " + friendName + " Email: " + email);
            find_friend_email.add(email);
            find_friend_name.add(friendName);
            find_friend_result.add("Name: " + friendName + " Email: " + email);
            Log.d("Friend Info", "Name: " + friendName + ", Avatar: " + friendAvatarUrl);
        } else {
            Log.d("Super Blacklist",
                    "You are in your friend's super blacklist. Name: " + friendName + ", email: " + email);
        }
    }

    /**
     * Initializes various components and UI elements of the activity.
     * Sets up Firebase Firestore, UI elements such as buttons, text views, and list adapters,
     * and loads data like the user's email and friend list for display.
     */
    private void initialize() {
        db = FirebaseFirestore.getInstance();
        ahead = findViewById(R.id.goAhead);
        ImageView search = findViewById(R.id.messagepage_search);
        ImageView notice = findViewById(R.id.messagepage_notice);
        ImageView home = findViewById(R.id.messagepage_home);
        ImageView my = findViewById(R.id.messagepage_my);
        add = findViewById(R.id.messagepage_newmessage);
        TextView searchText = findViewById(R.id.findText);
        TextView noticeText = findViewById(R.id.NoticeText);
        TextView homeText = findViewById(R.id.homeText);
        TextView myText = findViewById(R.id.myText);
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        User userInstance = User.getInstance();
        userEmail = userInstance.getEmail();
        friendNameShow = findViewById(R.id.select_friend_username);
        friendEmailShow = findViewById(R.id.select_friend_email);
        friendAgeShow = findViewById(R.id.select_friend_age);
        friendGenderShow = findViewById(R.id.messagepage_gender);
        startChat = findViewById(R.id.select_friend_button);
        result = findViewById(R.id.select_friend_result);
        clearShow();

        Log.d("AVL", friendlist_tree.inOrder().toString());

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, friendlist_tree.inOrder());
        result.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        Log.d("friendList", userInstance.getFriendList().toString());
        Log.d("Friend_tree", friendlist_tree.toString());
        NavigationManager navigationManager = new NavigationManager();
        navigationManager.setupNavigationMessage(this, search, notice, home, my, searchText, noticeText, homeText, myText);
    }
}

