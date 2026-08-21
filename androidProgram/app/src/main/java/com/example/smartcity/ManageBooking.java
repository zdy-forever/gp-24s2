package com.example.smartcity;

import android.annotation.SuppressLint;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartcity.tools.User;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * @author : Daoyan Zhu
 * UID: u7782042
 */
public class ManageBooking extends AppCompatActivity {

    public static String date;
    public static String startTime;
    public static String endTime;
    public static String address;

    @Override
    protected void onResume() {
        super.onResume();
        if (!User.getInstance().isOnline()) {
            User.getInstance().setOnline(true);
        }
        Log.d("isOnline", "Manage Booking onResume " + User.getInstance().isOnline());
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (User.getInstance().isOnline()) {
            User.getInstance().setOnline(false);
        }
        Log.d("isOnline", "Manage Booking onPause " + User.getInstance().isOnline());
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.managebooking);
        TextView textView = findViewById(R.id.managebooking_info);
        Button cancel = findViewById(R.id.managebooking_cancel_booking);
        Button done = findViewById(R.id.managebooking_mark_as_done);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");
        String currentDateString = sdfDate.format(new Date());

        textView.setText("Address: " + address + "\nDate: " + date + "\nStart time: " + startTime + "\nEnd time: " + endTime);

        cancel.setOnClickListener(v -> {
            if (address == null) {
                Toast.makeText(ManageBooking.this, "You have no booking", Toast.LENGTH_SHORT).show();
            } else if (isDatePassed(date, currentDateString)) {
                Toast.makeText(ManageBooking.this, "The date has passed", Toast.LENGTH_SHORT).show();
            } else {
                if (isDateTimePassed(date, endTime)) {
                    Toast.makeText(ManageBooking.this, "The time has passed", Toast.LENGTH_SHORT).show();
                } else if (isDateTimePassed(date, startTime)) {
                    Toast.makeText(ManageBooking.this, "The activity is still running", Toast.LENGTH_SHORT).show();
                } else {
                    db.collection("bookings")
                            .document(User.getInstance().getEmail())
                            .delete()
                            .addOnSuccessListener(aVoid -> {
                                BookingPage.is_booked = false;
                                address = null;
                                date = null;
                                startTime = null;
                                endTime = null;
                                textView.setText("Address: " + address + "\nDate: " + date + "\nStart time: " + startTime + "\nEnd time: " + endTime);
                                Toast.makeText(ManageBooking.this, "Cancel success", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Log.w("Firestore", "Error deleting document", e);
                                Toast.makeText(ManageBooking.this, "Failed to cancel booking", Toast.LENGTH_SHORT).show();
                            });
                }
            }
        });

        done.setOnClickListener(v -> {
            if (address == null) {
                Toast.makeText(ManageBooking.this, "You have no booking", Toast.LENGTH_SHORT).show();
            } else if (isDatePassed(date, currentDateString) || isDateTimePassed(date, endTime)) {
                db.collection("bookings")
                        .document(User.getInstance().getEmail())
                        .delete()
                        .addOnSuccessListener(aVoid -> {
                            BookingPage.is_booked = false;
                            address = null;
                            date = null;
                            startTime = null;
                            endTime = null;
                            textView.setText("Address: " + address + "\nDate: " + date + "\nStart time: " + startTime + "\nEnd time: " + endTime);
                            Toast.makeText(ManageBooking.this, "Mark as done success", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Log.w("Firestore", "Error deleting document", e);
                            Toast.makeText(ManageBooking.this, "Failed to mark booking as done", Toast.LENGTH_SHORT).show();
                        });
            } else if (isDateTimePassed(date, startTime)) {
                Toast.makeText(ManageBooking.this, "The activity is still running", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ManageBooking.this, "The activity has not started yet", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static boolean isDatePassed(String startTime, String currentDateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        try {
            LocalDate targetDate = LocalDate.parse(startTime, formatter);
            LocalDate currentDate = LocalDate.parse(currentDateString, formatter);
            return currentDate.isAfter(targetDate);
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean isDateTimePassed(String dateString, String timeString) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        try {
            String targetDateTimeString = dateString + " " + timeString;
            Date targetDateTime = sdf.parse(targetDateTimeString);
            Date currentDateTime = new Date(); // 当前日期和时间
            if (currentDateTime.after(targetDateTime)) {
                Log.d("ManageBooking", "The target date and time has passed.");
                return true;
            } else {
                Log.d("ManageBooking", "The target date and time has not passed.");
                return false;
            }
        } catch (ParseException e) {
            Log.e("ManageBooking", "Error parsing date and time: " + e.getMessage());
            return false;
        }
    }
}

