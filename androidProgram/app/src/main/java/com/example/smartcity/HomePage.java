package com.example.smartcity;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartcity.tools.NavigationManager;
import com.example.smartcity.tools.NoticeService;
import com.example.smartcity.tools.User;
import com.example.smartcity.tools.mapsforge.CustomMapHandler;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.MapsInitializer;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * @author : Jiahe Qian
 * UID: u7403710
 */

public class HomePage extends AppCompatActivity {
    private ActivityOptions options;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private boolean locationPermissionGranted;
    String selectedLocationString;


    private static final int REQUEST_CODE = 2100;
    private User user;
    public static String chosen;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);
        options = ActivityOptions.makeCustomAnimation(this, R.anim.fade_in, R.anim.fade_out);

        //initialize map
//        MapsInitializer.initialize(this);
//        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        // initialize CustomMapHandler，loading and adding the map into the mapContainer
        ViewGroup mapContainer = findViewById(R.id.osmap_container);

        CustomMapHandler customMapHandler = new CustomMapHandler(HomePage.this, this);//initialize mapHandler
        customMapHandler.loadMap(mapContainer);//initialize map
        customMapHandler.delayedInitialization();

        //initialize notifications
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        user = User.getInstance();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel("comp2100", "Smart City friend request", NotificationManager.IMPORTANCE_DEFAULT);
        notificationManager.createNotificationChannel(channel);

        if (user.getEmail() != null) {
            db.collection("bookings")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String documentId = document.getId();
                                if (documentId.equals(user.getEmail())) {
                                    BookingPage.is_booked = true;
                                    break;
                                }
                            }
                        } else {
                            Log.w("Firestore", "Error getting documents: ", task.getException());
                        }
                    });
        }
        if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_CODE);
            Log.d("Permissions", "Requesting POST_NOTIFICATIONS permission");
        }

        boolean closeFirstActivity = getIntent().getBooleanExtra("close_first_activity", false);
        if (closeFirstActivity) {
            Intent intent = new Intent();
            intent.setAction("close_first_activity");
            sendBroadcast(intent);
        }

        //select map and show
        Intent intentClick = getIntent();
        if (customMapHandler.handleSelectedLocation(intentClick, true, 17)) {
            selectedLocationString = intentClick.getStringExtra("location_name");
            chosen = selectedLocationString;
        }

        ImageView search = findViewById(R.id.homepage_search);
        ImageView notice = findViewById(R.id.homepage_notice);
        ImageView message = findViewById(R.id.homepage_message);
        ImageView my = findViewById(R.id.homepage_my);
        ImageView home = findViewById(R.id.homepage_home);
        TextView myText = findViewById(R.id.myText);
        TextView searchText = findViewById(R.id.findText);
        TextView noticeText = findViewById(R.id.NoticeText);
        TextView messageText = findViewById(R.id.messageText);
        TextView homeText = findViewById(R.id.homeText);

        TextView homepage_chosen_place = findViewById(R.id.homepage_chosen_place);
        Button report = findViewById(R.id.homepage_report);
        Button book = findViewById(R.id.homepage_book);
        NavigationManager navigationManager = new NavigationManager();
        navigationManager.setupNavigation(this, search, notice, home, message, my, searchText, noticeText, homeText, messageText, myText);

        // set chosen place
        if (chosen != null) {
            homepage_chosen_place.setText("Now your chosen place is:\n" + chosen);
        } else {
            homepage_chosen_place.setText("Please go to Find Page to select a place!");
        }

        book.setOnClickListener(
                v -> {
                    if (chosen == null) {
                        Toast.makeText(this, "Please choose a place first in Find page", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(this, BookingPage.class);
                        if (selectedLocationString == null)
                            getDeviceLocation();
                        intent.putExtra("location_string", selectedLocationString);
                        startActivity(intent, options.toBundle());
                    }
                }
        );
        report.setOnClickListener(
                v -> {
                    if (chosen == null) {
                        Toast.makeText(this, "Please choose a place first in Find page", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(this, Report1.class);
                        if (selectedLocationString == null)
                            getDeviceLocation();
                        intent.putExtra("location_string", selectedLocationString);
                        startActivity(intent, options.toBundle());
                    }
                }
        );

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Permissions", "POST_NOTIFICATIONS permission granted");
            } else {
                Log.d("Permissions", "POST_NOTIFICATIONS permission denied");
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent serviceIntent = new Intent(this, NoticeService.class);
        startService(serviceIntent);//running in background of Android system
    }


    private String getAddressFromLatLng(LatLng latLng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                return address.getAddressLine(0);  // return complete address string
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Location not available";
    }


    private void getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        Location lastKnownLocation = task.getResult();

                        LatLng currentLocation = new LatLng(
                                lastKnownLocation.getLatitude(),
                                lastKnownLocation.getLongitude());
                        selectedLocationString = getAddressFromLatLng(currentLocation);

                    }
                });
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        //update online status
        if (user.isOnline()) {
            User.getInstance().setOnline(true);
        }
        Log.d("isOnline", "homepage on resume " + User.getInstance().isOnline());
    }

    @Override
    protected void onPause() {
        super.onPause();
        //update online status
        if (user.isOnline()) {
            User.getInstance().setOnline(false);
        }
        Log.d("isOnline", "homepage on pause " + User.getInstance().isOnline());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}

