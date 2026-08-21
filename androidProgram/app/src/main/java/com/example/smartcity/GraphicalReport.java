package com.example.smartcity;

import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartcity.tools.Timeline;
import com.example.smartcity.tools.User;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author : Lanping Hu
 * UID: u7904927
 */

public class GraphicalReport extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private User user;
    private String locationString;
    private Timeline[] timelines = new Timeline[5];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.graphical_report);
        TextView bookingPlaceTextView = findViewById(R.id.textView_booking_place);
        timelines[0] = findViewById(R.id.timeline_day_1);
        timelines[1] = findViewById(R.id.timeline_day_2);
        timelines[2] = findViewById(R.id.timeline_day_3);
        timelines[3] = findViewById(R.id.timeline_day_4);
        timelines[4] = findViewById(R.id.timeline_day_5);

        user = User.getInstance();

        fetchUserAddress(bookingPlaceTextView);
    }

    private void fetchUserAddress(TextView bookingPlaceTextView) {
        db.collection("bookings")
                .document(user.getEmail())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                        locationString = task.getResult().getString("address");
                        bookingPlaceTextView.setText("Your booking place is: " + locationString);
                        fetchExistingBookings();
                    } else {
                        bookingPlaceTextView.setText("You have not booked any location yet.");
                    }
                });
    }

    private void fetchExistingBookings() {
        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < 5; i++) {
            // Calculate future dates
            int dayOffset = i;
            calendar.add(Calendar.DAY_OF_MONTH, dayOffset);
            String date = String.format("%d/%d/%d", calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR));

            // Query Firestore for reservations
            int finalI = i;
            db.collection("bookings")
                    .whereEqualTo("address", locationString)
                    .whereEqualTo("date", date)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            List<Timeline.TimeSlot> existingBookings = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String documentId = document.getId();
                                String startTimeString = document.getString("start_time_string");
                                String endTimeString = document.getString("end_time");

                                float startTime = convertTimeToFloat(startTimeString);
                                float endTime = convertTimeToFloat(endTimeString);

                                int color = documentId.equals(user.getEmail()) ? Color.YELLOW : Color.RED;
                                existingBookings.add(new Timeline.TimeSlot(startTime, endTime, color));
                            }
                            timelines[finalI].setTimeSlots(existingBookings);
                        } else {
                            Log.w("GraphicalReport", "Error getting documents.", task.getException());
                        }
                    });
            calendar.add(Calendar.DAY_OF_MONTH, -dayOffset); // Reset date
        }
    }

    private float convertTimeToFloat(String timeString) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date date = sdf.parse(timeString);
            if (date != null) {
                int hours = date.getHours();
                int minutes = date.getMinutes();
                return (hours * 60 + minutes) / (24f * 60);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
}