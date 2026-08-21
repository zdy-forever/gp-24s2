package com.example.smartcity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartcity.tools.User;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : Lanping Hu
 * UID: u7904927
 */

public class NotificationSetting extends AppCompatActivity {
    private static User user;
    private static final String TAG = "NotificationSetting";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notificationsetting);
        user = User.getInstance();

        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch friend_request
                = findViewById(R.id.notificationsetting_friend_request);
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch friend_message
                = findViewById(R.id.notificationsetting_friend_message);
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch booking_success
                = findViewById(R.id.notificationsetting_booking_success);
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch booking_near
                = findViewById(R.id.notificationsetting_booking_near);
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch report_success
                = findViewById(R.id.notificationsetting_report_seccess);
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch report_feedback
                = findViewById(R.id.notificationsetting_report_feedback);
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch low_time_limit
                = findViewById(R.id.notificationsetting_low_time_limit);

        friend_request.setChecked(user.setting_friend_request);
        friend_message.setChecked(user.setting_friend_message);
        booking_success.setChecked(user.setting_booking_success);
        booking_near.setChecked(user.setting_booking_near);
        report_success.setChecked(user.setting_report_success);
        report_feedback.setChecked(user.setting_report_feedback);
        low_time_limit.setChecked(user.setting_low_time_limit);

        friend_request.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                user.setting_friend_request = isChecked;
            }
        });

        friend_message.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                user.setting_friend_message = isChecked;
            }
        });

        booking_success.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                user.setting_booking_success = isChecked;
            }
        });

        booking_near.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                user.setting_booking_near = isChecked;
            }
        });

        report_success.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                user.setting_report_success = isChecked;
            }
        });

        report_feedback.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                user.setting_report_feedback = isChecked;

            }
        });

        low_time_limit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                user.setting_low_time_limit = isChecked;
            }
        });
    }


    protected void onResume() {
        super.onResume();
        //update online status
        if (!User.getInstance().isOnline()) {
            User.getInstance().setOnline(true);
        }
        Log.d("isOnline", " " + User.getInstance().isOnline());
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (User.getInstance().isOnline()) {
            User.getInstance().setOnline(false);
        }
        Log.d("isOnline", " " + User.getInstance().isOnline());
        // When the user leaves the interface, save to Firebase once
        saveNotificationSettingToFirestore();
    }

    public void saveNotificationSettingToFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String email = user.getEmail();
        Map<String, Object> settings = new HashMap<>();
        settings.put("setting_friend_request", user.setting_friend_request);
        settings.put("setting_friend_message", user.setting_friend_message);
        settings.put("setting_booking_success", user.setting_booking_success);
        settings.put("setting_booking_near", user.setting_booking_near);
        settings.put("setting_report_success", user.setting_report_success);
        settings.put("setting_report_feedback", user.setting_report_feedback);
        settings.put("setting_low_time_limit", user.setting_low_time_limit);

        db.collection("users").document(email)
                .update(settings)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Settings successfully updated in Firebase.");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error updating settings in Firebase", e);
                });
    }
}
