package com.example.smartcity;

import static com.example.smartcity.FindNewFriend.find_friend_email;
import static com.example.smartcity.FindNewFriend.find_friend_name;
import static com.example.smartcity.FindNewFriend.find_friend_result;

import android.app.ActivityOptions;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartcity.tools.Constants;
import com.example.smartcity.tools.Message;
import com.example.smartcity.tools.MessageAdapter;
import com.example.smartcity.tools.User;
import com.example.smartcity.tools.NavigationManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * @author : Daoyan Zhu
 * UID: u7782042
 * @author : Shangyi Shen
 * UID: u7735222
 */
public class P2PMessage extends AppCompatActivity {
    private NavigationManager navigationManager;
    ImageView more;
    EditText input;
    TextView p2pmessage_username;
    ImageView send;
    RecyclerView chatMessagesView;
    public static boolean isPortrait = true;
    public static String p2pmessage_friend_email;
    public static boolean isInChat = false;
    private User currentUser;
    private FirebaseFirestore db;
    private boolean isInBlackList = false;
    private boolean isInSuperBlackList = false;
    private List<Message> chatMessages;
    private MessageAdapter messageAdapter;
    private String friendAvatar;
    private ActivityOptions options;
    String friendName;
    String messageContent;


    protected void onResume() {
        super.onResume();
        if (!currentUser.isOnline()) {
            currentUser.setOnline(true);
        }
        Log.d("isOnline", "P2PMessage on resume " + User.getInstance().isOnline());
    }

    protected void onPause() {
        super.onPause();
        if (currentUser.isOnline()) {
            currentUser.setOnline(false);
        }
        Log.d("isOnline", "P2PMessage on pause " + User.getInstance().isOnline());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isInChat = false; // user leave the chat page
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isPortrait) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        setContentView(R.layout.p2pmessage);
        currentUser = User.getInstance();
        isInChat = true; // user access chat page
        loadReceivedDetails();
        initialize();
        listenMessage();
        deleteFriendNotifications();

        Query queryInitialize = db.collection("users").whereEqualTo("email", FirebaseAuth.getInstance().getCurrentUser().getEmail());
        queryInitialize.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    List<String> blackList = (List<String>) document.get("black_list");
                    List<String> superBlackList = (List<String>) document.get("super_blacklist");
                    if (blackList != null) {
                        Log.d("BlackList", blackList.toString());
                        isInBlackList = blackList.contains(p2pmessage_friend_email);
                    }
                    if (superBlackList != null) {
                        Log.d("SuperBlackList", superBlackList.toString());
                        isInSuperBlackList = superBlackList.contains(p2pmessage_friend_email);
                    }
                }
            }
        });

        //TODO: simplify
        more.setOnClickListener(v -> {
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.p2pmessage_more_click, null);
            SwitchCompat block = dialogView.findViewById(R.id.p2pmessage_more_click_bolck);
            SwitchCompat delete = dialogView.findViewById(R.id.p2pmessage_more_click_delete);
            SwitchCompat superblock = dialogView.findViewById(R.id.p2pmessage_more_click_super_block);

            block.setChecked(isInBlackList);
            superblock.setChecked(isInSuperBlackList);
            delete.setChecked(false);
            Log.d("P2P", isInBlackList + "");
            Log.d("P2P", isInSuperBlackList + "");
            builder.setTitle("Select an Option");
            builder.setView(dialogView);
            builder.setPositiveButton("YES", (dialog, which) -> {

                if (block.isChecked() && !isInBlackList) {
                    Query query = db.collection(Constants.users).whereEqualTo(Constants.email, FirebaseAuth.getInstance().getCurrentUser().getEmail());
                    query.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                List<String> blackList = (List<String>) document.get("black_list");
                                if (blackList == null) {
                                    blackList = new ArrayList<>();
                                }
                                blackList.add(p2pmessage_friend_email);
                                currentUser.setBlackList(blackList);
                                currentUser.saveUserToFirestore();
                                Log.d("BlackListAlter", blackList.toString());
                            }
                        }
                    });
                    Intent intent = new Intent(P2PMessage.this, MessagePage.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    finish();
                    Toast.makeText(this, "Block", Toast.LENGTH_SHORT).show();
                } else if (!block.isChecked() && isInBlackList) {
                    Query query = db.collection(Constants.users).whereEqualTo(Constants.email, FirebaseAuth.getInstance().getCurrentUser().getEmail());
                    query.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                List<String> blackList = (List<String>) document.get("black_list");
                                assert blackList != null;
                                Log.d("BlackList", blackList.toString());
                                if (blackList.contains(p2pmessage_friend_email)) {
                                    blackList.remove(p2pmessage_friend_email);
                                    currentUser.setBlackList(blackList);
                                    currentUser.saveUserToFirestore();
                                }
                                Log.d("BlackListAlter", blackList.toString());
                            }
                        }
                    });
                    Intent intent = new Intent(P2PMessage.this, MessagePage.class);
                    startActivity(intent, options.toBundle());
                    finish();
                    Toast.makeText(this, "Unblock", Toast.LENGTH_SHORT).show();
                }


                if (delete.isChecked()) {
                    Query query = db.collection(Constants.users).whereEqualTo(Constants.email, FirebaseAuth.getInstance().getCurrentUser().getEmail());
                    query.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                List<String> friendList = (List<String>) document.get("friend_list");
                                assert friendList != null;
                                Log.d("FriendList", friendList.toString());
                                friendList.remove(p2pmessage_friend_email);
                                currentUser.setFriendList(friendList);
                                currentUser.saveUserToFirestore();
                                Log.d("FriendListAlter", friendList.toString());
                            }
                        }
                    });
                    Query query1 = db.collection(Constants.users).whereEqualTo("email", p2pmessage_friend_email);
                    query1.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                List<String> friendList = (List<String>) document.get("friend_list");
                                assert friendList != null;
                                Log.d("FriendList", friendList.toString());
                                friendList.remove(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                                Map<String, Object> updates = new HashMap<>();
                                updates.put(Constants.friendList, friendList);
                                db.collection("users").document(p2pmessage_friend_email).update(updates);
                                Log.d("FriendListAlter", friendList.toString());
                            }
                        }
                    });
                    MessagePage.friendlist_tree = MessagePage.friendlist_tree.delete(p2pmessage_friend_email);
                    for (int i = 0; i < find_friend_email.size(); i++) {
                        if (find_friend_email.get(i).equals(p2pmessage_friend_email)) {
                            String s = find_friend_result.get(i);
                            find_friend_email.remove(i);
                            find_friend_name.remove(i);
                            find_friend_result.remove(i);
                            MessagePage.adapter.remove(s);
                        }
                    }

                    Log.d("AVL", p2pmessage_friend_email);
                    Log.d("AVLDelete", MessagePage.friendlist_tree.inOrder().toString());
                    Toast.makeText(this, "Delete", Toast.LENGTH_SHORT).show();
                    super.onBackPressed();
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("data_key", 1);
                    setResult(RESULT_OK, returnIntent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    finish();
                }

                if (superblock.isChecked() && !isInSuperBlackList) {
                    Log.d("superBlackList", superblock.isChecked() + " " + isInSuperBlackList);
                    Query query = db.collection(Constants.users).whereEqualTo(Constants.Email, FirebaseAuth.getInstance().getCurrentUser().getEmail());
                    query.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                List<String> superBlackList = (List<String>) document.get("super_blacklist");
                                if (superBlackList == null) {
                                    superBlackList = new ArrayList<>();
                                }
                                superBlackList.add(p2pmessage_friend_email);
                                currentUser.setSuperBlacklist(superBlackList);
                                currentUser.saveUserToFirestore();
                                Log.d("superBlackListAlter", superBlackList.toString());
                            }
                        }
                    });
                    Intent intent = new Intent(P2PMessage.this, MessagePage.class);
                    startActivity(intent, options.toBundle());
                    finish();
                    Toast.makeText(this, "superBlock", Toast.LENGTH_SHORT).show();
                } else if (!superblock.isChecked() && isInSuperBlackList) {
                    Query query = db.collection(Constants.users).whereEqualTo("email", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                    query.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                List<String> superBlackList = (List<String>) document.get("super_blacklist");
                                if (superBlackList == null) {
                                    superBlackList = new ArrayList<>();
                                }
                                Log.d("superBlackList", superBlackList.toString());
                                if (superBlackList.contains(p2pmessage_friend_email)) {
                                    superBlackList.remove(p2pmessage_friend_email);
                                    currentUser.setSuperBlacklist(superBlackList);
                                    currentUser.saveUserToFirestore();
                                }
                                Log.d("BlackListAlter", superBlackList.toString());
                            }
                        }
                    });
                    Intent intent = new Intent(P2PMessage.this, MessagePage.class);
                    startActivity(intent, options.toBundle());
                    finish();
                    Toast.makeText(this, "UnSuperblock", Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNegativeButton("CANCEL", (dialog, which) ->
                    dialog.dismiss()
            );
            androidx.appcompat.app.AlertDialog dialog = builder.create();
            dialog.show();
        });


        send.setOnClickListener(v -> {
            Log.d("send", "button was clicked");
            messageContent = input.getText().toString();

            if (messageContent.isEmpty()) {
                Toast.makeText(P2PMessage.this, "Message cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            // get current user's friend list and friend email
            if (currentUser == null) {
                Toast.makeText(P2PMessage.this, "User not logged in", Toast.LENGTH_SHORT).show();
                return;
            }

            // check if current user is in the friend black list
            db.collection("users").whereEqualTo("email", p2pmessage_friend_email).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                List<String> blackList = (List<String>) document.get("black_list");
                                if (blackList != null && blackList.contains(currentUser.getEmail())) {
                                    Toast.makeText(P2PMessage.this, "You have been blocked by this user,message send failed.", Toast.LENGTH_SHORT).show();
                                    input.setText("");
                                    return;
                                }
                            }
                            // if not,send message
                            sendMessage();
                        } else {
                            Log.e(Constants.p2pMessage, "Failed to check blacklist", task.getException());
                            Toast.makeText(P2PMessage.this, "Failed to check blacklist.", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    /**
     * Listens for messages between the current user and the selected friend.
     * Sets up two listeners on the "message" collection in Firestore:
     * - One for messages sent by the current user to the friend.
     * - One for messages sent by the friend to the current user.
     */
    private void listenMessage() {
        if (currentUser == null || currentUser.getEmail() == null || p2pmessage_friend_email == null) {
            Log.e(Constants.p2pMessage, "User or friend's email is null");
            return;
        }

        db.collection(Constants.message)
                .whereEqualTo(Constants.sender, currentUser.getEmail())
                .whereEqualTo(Constants.receiver, p2pmessage_friend_email)
                .addSnapshotListener(eventListener);

        db.collection(Constants.message)
                .whereEqualTo(Constants.sender, p2pmessage_friend_email)
                .whereEqualTo(Constants.receiver, currentUser.getEmail())
                .addSnapshotListener(eventListener);
    }

    /**
     * EventListener for real-time updates from Firestore's "message" collection.
     * It listens for newly added messages, retrieves the sender, receiver, content, and timestamp,
     * and adds them to the chatMessages list.
     * Messages are sorted by timestamp, and the chat adapter is updated:
     * either fully refreshed or updated incrementally depending on the number of new messages.
     * Errors and null values are logged, and the chat view is scrolled to the latest message if necessary.
     */
    @SuppressLint("NotifyDataSetChanged")
    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if (error != null) {
            Log.e(Constants.p2pMessage, "Listen failed", error);
            return;
        }
        if (value != null) {
            int count = chatMessages.size();
            for (DocumentChange documentChange : value.getDocumentChanges()) {
                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                    Message message = new Message();
                    message.sender = documentChange.getDocument().getString("sender");
                    message.receiver = documentChange.getDocument().getString("receiver");
                    message.message = documentChange.getDocument().getString("message");
                    // get timestamp
                    String timestampString = documentChange.getDocument().getString("timestamp");
                    if (timestampString != null) {
                        try {
                            // remove UTC to make timestamp readable
                            String fixedTimestampString = timestampString.replace("UTC", "").trim();
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy 'at' HH:mm:ss XXX", Locale.ENGLISH);
                            Date date = dateFormat.parse(fixedTimestampString);
                            assert date != null;
                            message.timestamp = date.getTime();
                            message.dateObject = date;
                        } catch (ParseException e) {
                            Log.e(Constants.p2pMessage, "Failed to parse timestamp: " + timestampString, e);
                        }
                    } else {
                        Log.e(Constants.p2pMessage, "Timestamp is null");
                    }
                    chatMessages.add(message);
                }
            }
            if (messageAdapter == null || chatMessages == null) {
                Log.e(Constants.p2pMessage, "MessageAdapter or chatMessages is null" + chatMessages.isEmpty());
                return;
            }
            chatMessages.sort(Comparator.comparingLong(obj -> obj.timestamp));
            // if initial message is 0 or size is not matched，use notifyDataSetChanged();
            if (count == 0 || value.getDocumentChanges().size() > 1) {
                messageAdapter.notifyDataSetChanged();
            } else {
                messageAdapter.notifyItemInserted(chatMessages.size() - 1);
                chatMessagesView.smoothScrollToPosition(chatMessages.size() - 1);
            }
            chatMessagesView.setVisibility(View.VISIBLE);
        } else {
            Log.e("P2PMessage", "QuerySnapshot is null");
        }
    };

    /**
     * Loads the details of the friend with whom the user is having a peer-to-peer message conversation.
     * Retrieves friend information from the intent(name,email) and shared preferences(avatar), and initializes UI elements.
     */
    private void loadReceivedDetails() {
        friendName = getIntent().getStringExtra("friendName");
        p2pmessage_friend_email = getIntent().getStringExtra("friendEmail");
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        friendAvatar = sharedPreferences.getString("avatarUri", null);
        p2pmessage_username = findViewById(R.id.p2pmessage_username);

        // check if data is valid
        if (friendName == null) {
            p2pmessage_username.setText("");
        }
        p2pmessage_username.setText(friendName);
        more = findViewById(R.id.p2pmessage_more);
        input = findViewById(R.id.p2pmessage_input);
        send = findViewById(R.id.p2pmessage_send);
        chatMessagesView = findViewById(R.id.p2pmessage_chat);
        chatMessagesView.setLayoutManager(new LinearLayoutManager(this));

    }

    private void initialize() {
        chatMessages = new ArrayList<>();
        messageAdapter = new MessageAdapter(transferStringToBitmap(friendAvatar), chatMessages, currentUser.getEmail());
        chatMessagesView.setAdapter(messageAdapter);
        db = FirebaseFirestore.getInstance();
        options = ActivityOptions.makeCustomAnimation(this, R.anim.fade_in, R.anim.fade_out);

    }

    /**
     * Converts a Base64-encoded string into a Bitmap image.
     * If the input string is null or empty, a default avatar image is returned.
     *
     * @param imageString The Base64-encoded string representing the image.
     * @return A Bitmap decoded from the Base64 string, or a default avatar if the string is invalid.
     */
    private Bitmap transferStringToBitmap(String imageString) {
        if (imageString == null || imageString.isEmpty()) {
            return BitmapFactory.decodeResource(getResources(), R.mipmap.default_avatar);
        }
        byte[] bytes = Base64.decode(imageString, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    /**
     * Sends a message from the current user to the selected friend.
     * Gathers necessary data such as sender, receiver, message status, and timestamp,
     * then stores the message in the Firestore "message" collection.
     */
    private void sendMessage() {
        // Get the data
        String senderEmail = currentUser.getEmail();
        String receiverEmail = p2pmessage_friend_email;
        // Use the helper method to create the message data
        Map<String, Object> messageData = createMessageData(senderEmail, receiverEmail, messageContent);
        input.setText("");
        Log.d("messageData", messageData.toString());
        //save message to firestore
        db.collection(Constants.message)
                .add(messageData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(P2PMessage.this, "Message sent successfully", Toast.LENGTH_SHORT).show();
                    Log.d(Constants.p2pMessage, "send message content: " + messageContent);
                })
                .addOnFailureListener(e -> {
                    Log.e(Constants.p2pMessage, "Error sending message", e);
                    Toast.makeText(P2PMessage.this, "Failed to send message", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Helper method to construct message data.
     */
    public Map<String, Object> createMessageData(String senderEmail, String receiverEmail, String messageContent) {
        String statue = "sent";
        long timestamp = System.currentTimeMillis();
        String formattedTimestamp = MessageAdapter.formatTimestamp(timestamp);

        Map<String, Object> messageData = new HashMap<>();
        messageData.put(Constants.message, messageContent);
        messageData.put(Constants.receiver, receiverEmail);
        messageData.put(Constants.sender, senderEmail);
        messageData.put("statue", statue);
        messageData.put("timestamp", formattedTimestamp);

        return messageData;
    }

    /**
     * Deletes notifications from a specific friend in the current user's notifications.
     * It retrieves the user's notifications, filters out the ones sent by the friend,
     * and updates the Firestore document accordingly.
     */
    private void deleteFriendNotifications() {
        String userEmail = currentUser.getEmail();
        // get current user's notification
        db.collection("notifications")
                .document(userEmail)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<Map<String, Object>> notifications = (List<Map<String, Object>>) documentSnapshot.get("notifications");
                        if (notifications != null) {
                            // create a new list to except notice from friends
                            List<Map<String, Object>> updatedNotifications = new ArrayList<>();
                            for (Map<String, Object> notification : notifications) {
                                String from = (String) notification.get("from");
                                //check if from is null
                                if (from != null && !from.equals(p2pmessage_friend_email)) {
                                    updatedNotifications.add(notification);
                                }
                            }
                            // update notification in firestore
                            db.collection(Constants.notifications)
                                    .document(userEmail)
                                    .update(Constants.notifications, updatedNotifications)
                                    .addOnSuccessListener(aVoid ->
                                            Log.d(Constants.p2pMessage, "Friend notifications successfully removed.")
                                    )
                                    .addOnFailureListener(e ->
                                            Log.w(Constants.p2pMessage, "Error removing friend notifications", e)
                                    );
                        }
                    }
                })
                .addOnFailureListener(e ->
                        Log.w(Constants.p2pMessage, "Error getting notifications", e)
                );
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent returnIntent = new Intent();
        returnIntent.putExtra("data_key", 0);
        setResult(RESULT_OK, returnIntent);
        finish();
    }


}

