package com.example.smartcity;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.smartcity.tools.User;
import com.example.smartcity.tools.Util;
import com.example.smartcity.tools.NavigationManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

/**
 * @author : Daoyan Zhu
 * UID: u7782042
 * @author Shangyi Shen
 * UID: u7735222
 */

public class MyPage extends AppCompatActivity {
    private User userInstance;
    private TextView userName;
    private Button manage_booking, notification_setting, account_setting,
            friend_request, graphical_report;
    private ActivityOptions options;


    // after log off we need to close this page by broadcast
    private final BroadcastReceiver closeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("close_mypage".equals(intent.getAction())) {
                finish();
            }
        }
    };

    private final BroadcastReceiver changeNameReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("name_change".equals(intent.getAction())) {
                finish();
                Log.d("MyPage", "My page close");
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mypage);

        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter filter = new IntentFilter("close_MyPage");
        localBroadcastManager.registerReceiver(closeReceiver, filter);
        Log.d("MyPage", "Local receiver registered");
        IntentFilter filter1 = new IntentFilter("name_change");
        localBroadcastManager.registerReceiver(changeNameReceiver, filter1);
        Log.d("MyPage", "Local receiver registered");

        initialize();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("bookings")
                .document(userInstance.getEmail())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {

                            ManageBooking.address = document.getString("address");
                            ManageBooking.date = document.getString("date");
                            ManageBooking.startTime = document.getString("start_time_string");
                            ManageBooking.endTime = document.getString("end_time");

                            if (ManageBooking.address != null) {
                                Log.d("ManageBooking111", "Address: " + ManageBooking.address);
                                Log.d("ManageBooking111", "Date: " + ManageBooking.date);
                                Log.d("ManageBooking111", "Start time: " + ManageBooking.startTime);
                                Log.d("ManageBooking111", "End time: " + ManageBooking.endTime);
                            }
                        } else {
                            Log.d("ManageBooking", "No such document found");
                        }
                    } else {
                        Log.e("ManageBooking", "Error getting document: ", task.getException());
                    }
                });


        if (userName.getText().toString().isEmpty()) {
            userName.setText("Username is waiting to be set up.\nGo Account settings to set it up.");
        }


        manage_booking.setOnClickListener(v -> {
            Intent intent = new Intent(this, ManageBooking.class);
            startActivity(intent, options.toBundle());

        });
        notification_setting.setOnClickListener(v -> {
            Intent intent = new Intent(this, NotificationSetting.class);
            startActivity(intent, options.toBundle());

        });
        account_setting.setOnClickListener(v -> {
            Intent intent = new Intent(this, AccountSettings.class);
            startActivity(intent, options.toBundle());
        });

        friend_request.setOnClickListener(v -> {
            Intent intent = new Intent(this, FriendRequest.class);
            startActivity(intent, options.toBundle());
        });

        graphical_report.setOnClickListener(v -> {
            Intent intent = new Intent(this, GraphicalReport.class);
            startActivity(intent, options.toBundle());
        });

    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onResume() {
        super.onResume();
        if (!User.getInstance().isOnline()) {
            User.getInstance().setOnline(true);
        }
        Log.d("isOnline", " " + User.getInstance().isOnline());
        //update all information from user
        TextView username = findViewById(R.id.mypage_username);
        TextView account = findViewById(R.id.mypage_account);
        ImageView gender = findViewById(R.id.mypage_gender);
        TextView age = findViewById(R.id.mypage_age);
        Util.setGenderView(userInstance.getGender(), gender);
        age.setText("Age: " + User.getInstance().getAge());
        account.setText("Email:" + Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail());
        username.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        loadAvatar();
    }

    @SuppressLint("SetTextI18n")
    protected void onPause() {
        super.onPause();
        //update online status
        if (User.getInstance().isOnline()) {
            User.getInstance().setOnline(false);
        }
        Log.d("isOnline", "Mypage on pause " + User.getInstance().isOnline());
    }

    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(closeReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(changeNameReceiver);
        Log.d("MyPage", "Local receiver unregistered");
    }

    private void loadAvatar() {
        if (userInstance.getAvatar() == null || userInstance.getAvatar().isEmpty()) {
            setDefaultAvatar();
            return;
        }
        ImageView avatar = findViewById(R.id.user_avatar);
        String avatarBase64 = userInstance.getAvatar();
        Log.d("Avatar", "get Avatar string");
        try {
            byte[] decodedBytes = Base64.decode(avatarBase64, Base64.DEFAULT);
            Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            avatar.setImageBitmap(decodedBitmap);
        } catch (IllegalArgumentException e) {
            Log.e("AccountActivity", "Invalid Base64 string", e);
            setDefaultAvatar();
        }
    }

    private void setDefaultAvatar() {
        ImageView avatar = findViewById(R.id.user_avatar);
        avatar.setImageResource(R.mipmap.default_avatar);
    }

    @SuppressLint("SetTextI18n")
    private void initialize() {
        userInstance = User.getInstance();
        ImageView search = findViewById(R.id.mypage_search);
        ImageView notice = findViewById(R.id.mypage_notice);
        ImageView message = findViewById(R.id.mypage_message);
        ImageView home = findViewById(R.id.mypage_home);
        ImageView my = findViewById(R.id.mypage_my);
        loadAvatar();
        TextView myText = findViewById(R.id.myText);
        TextView searchText = findViewById(R.id.findText);
        TextView noticeText = findViewById(R.id.NoticeText);
        TextView messageText = findViewById(R.id.messageText);
        TextView homeText = findViewById(R.id.homeText);

        manage_booking = findViewById(R.id.mypage_managebooking);
        notification_setting = findViewById(R.id.mypage_notificationsetting);
        account_setting = findViewById(R.id.mypage_accountsetting);
        friend_request = findViewById(R.id.mypage_friendrequest);
        graphical_report = findViewById(R.id.mypage_graphical_report);
        ImageView gender = findViewById(R.id.mypage_gender);
        Util.setGenderView(userInstance.getGender(), gender);

        userName = findViewById(R.id.mypage_username);
        TextView account = findViewById(R.id.mypage_account);
        TextView age = findViewById(R.id.mypage_age);

        age.setText(String.valueOf(User.getInstance().getAge()));
        account.setText("Email:" + Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail());
        userName.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());

        options = ActivityOptions.makeCustomAnimation(this, R.anim.fade_in, R.anim.fade_out);
        NavigationManager navigationManager = new NavigationManager();
        navigationManager.setupNavigation(this, search, notice, home, message, my, searchText, noticeText, homeText, messageText, myText);
    }

}
