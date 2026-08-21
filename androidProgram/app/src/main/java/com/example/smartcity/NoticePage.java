package com.example.smartcity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartcity.notifications.NotificationHandler;
import com.example.smartcity.notifications.NotificationHandlerFactory;
import com.example.smartcity.tools.Constants;
import com.example.smartcity.tools.NavigationManager;
import com.example.smartcity.tools.User;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

/**
 * @author : Daoyan Zhu
 * UID: u7782042
 * @author : Lanping Hu
 * UID: u7904927
 */

public class NoticePage extends AppCompatActivity {
    private FirebaseFirestore db;
    private static User user;
    private static final String TAG = "NoticePage";
    private ListView notice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.noticepage);
        user = User.getInstance();

        db = FirebaseFirestore.getInstance();
        ImageView search = findViewById(R.id.noticepage_search);
        ImageView home = findViewById(R.id.noticepage_home);
        ImageView message = findViewById(R.id.noticepage_message);
        ImageView noticeImage = findViewById(R.id.noticepage_notice);
        ImageView my = findViewById(R.id.noticepage_my);

        TextView searchText = findViewById(R.id.findText);
        TextView messageText = findViewById(R.id.messageText);
        TextView homeText = findViewById(R.id.homeText);
        TextView noticeText = findViewById(R.id.NoticeText);
        TextView myText = findViewById(R.id.myText);
        NavigationManager navigationManager = new NavigationManager();
        navigationManager.setupNavigation(this, search, noticeImage, home, message, my, searchText, noticeText, homeText, messageText, myText);
        notice = findViewById(R.id.noticepage_nodification);

    }

    @Override
    protected void onStart() {
        super.onStart();
        fetchNotificationsFromNetwork();
    }

    protected void onResume() {
        super.onResume();
        //update online status
        if (!User.getInstance().isOnline()) {
            User.getInstance().setOnline(true);
        }
        Log.d("isOnline", "Notice page on resume " + User.getInstance().isOnline());
    }

    protected void onPause() {
        super.onPause();
        if (User.getInstance().isOnline()) {
            User.getInstance().setOnline(false);
        }
        Log.d("isOnline", "Notice page on pause" + User.getInstance().isOnline());
    }

    private void fetchNotifications() {
        String email = user.getEmail();
        // Try to get data complexity from cache
        db.collection(Constants.notifications).document(email)
                .get(Source.CACHE)
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<Map<String, Object>> notifications = (List<Map<String, Object>>) documentSnapshot.get("notifications");
                        if (notifications != null) {
                            updateListView(notifications);
                        } else {
                            fetchNotificationsFromNetwork();
                        }
                    } else {
                        fetchNotificationsFromNetwork();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error getting documents from cache.", e);
                    fetchNotificationsFromNetwork();
                });
    }

    private void fetchNotificationsFromNetwork() {
        String email = user.getEmail();
        db.collection(Constants.notifications).document(email)
                .get(Source.SERVER)
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<Map<String, Object>> notifications = (List<Map<String, Object>>) documentSnapshot.get("notifications");
                        if (notifications != null) {
                            Collections.reverse(notifications);
                            updateListView(notifications);
                        }
                    }
                })
                .addOnFailureListener(e ->
                        Log.w(TAG, "Error getting documents from server.", e)
                );
    }

    private void updateListView(List<Map<String, Object>> notifications) {
        List<String> notificationTexts = new ArrayList<>();
        NotificationHandlerFactory factory = new NotificationHandlerFactory(user);

        for (Map<String, Object> notification : notifications) {
            String text = (String) notification.get("text");
            NotificationHandler handler = factory.getHandler(text);
            if (handler != null) {
                String processedText = handler.handleNotification(notification);
                if (processedText != null) {
                    notificationTexts.add(processedText);
                }
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, notificationTexts);
        notice.setAdapter(adapter);
    }

}