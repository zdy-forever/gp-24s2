package com.example.smartcity;

import android.Manifest;
import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.smartcity.tools.User;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : Daoyan Zhu
 * UID: u7782042
 */


public class Start extends AppCompatActivity {
    private ActivityOptions options;
    private static final int REQUEST_CODE = 2100;
    private final BroadcastReceiver closeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("close_first_activity".equals(intent.getAction())) {
                finish();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_start);
        checkAndRequestPermissions();
        options = ActivityOptions.makeCustomAnimation(this, R.anim.fade_in, R.anim.fade_out);
        //start offline cache
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)  // enable offline data persistent
                .build();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.setFirestoreSettings(settings);
        IntentFilter filter = new IntentFilter("close_first_activity");
        registerReceiver(closeReceiver, filter, Context.RECEIVER_NOT_EXPORTED);

        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        Intent intent1 = new Intent("close_mypage");
        localBroadcastManager.sendBroadcast(intent1);
        Log.d("LogIn", "Sending local broadcast to close MyPage");

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String email = currentUser.getEmail();
            updateUI(email);
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_start), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button start_log_in = findViewById(R.id.start_log_in);
        Button start_sign_up = findViewById(R.id.start_sign_up);
        // choose log in or sign up
        start_log_in.setOnClickListener(v ->
                {
                    Intent intent = new Intent(this, LogIn.class);
                    startActivity(intent, options.toBundle());
                    finish();
                }
        );
        start_sign_up.setOnClickListener(v -> {
            Intent intent = new Intent(this, SignUp.class);
            startActivity(intent, options.toBundle());
        });

    }

    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(closeReceiver);
    }

    public void updateUI(String email) {
        // get user data
        User.getInstance(email, user -> {
            if (user != null) {
                Intent intent = new Intent(Start.this, HomePage.class);
                startActivity(intent, options.toBundle());
                finish();
            } else {
                // Deal with loading failure
                Toast.makeText(Start.this, "Fail to enter home page, please log in again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkAndRequestPermissions() {
        List<String> permissionsNeeded = new ArrayList<>();

        // check permission of notification
        if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.POST_NOTIFICATIONS);
            Log.d("Permissions", "Requesting POST_NOTIFICATIONS permission");
        }

        // check permission of location
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
            Log.d("Permissions", "Requesting ACCESS_FINE_LOCATION permission");
        }

        // if not permitted, ask
        if (!permissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsNeeded.toArray(new String[0]), REQUEST_CODE);
        }
    }

}