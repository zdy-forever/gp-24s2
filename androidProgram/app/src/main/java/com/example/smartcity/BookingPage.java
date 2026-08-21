package com.example.smartcity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.icu.util.TimeZone;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartcity.tools.Subject;
import com.example.smartcity.tools.Timeline;
import com.example.smartcity.tools.User;
import com.example.smartcity.tools.ConvertTime;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : Lanping Hu
 * UID: u7904927
 * @author : Hanjian Jin
 * UID: u7905060
 */

public class BookingPage extends AppCompatActivity {
    private String locationString;
    ;
    private String selectedDate;
    private TimePicker start_time;
    private TimePicker end_time;
    private Timeline timeline;
    private boolean isOverlapping = false;
    private List<Timeline.TimeSlot> existingBookings = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private User user;
    private boolean isDateSelected = false;

    private Subject subject = new Subject();
    static boolean is_booked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = User.getInstance();

        setContentView(R.layout.bookingpage);
        Button date = findViewById(R.id.bookingpage_date_picker);
        TextView acceptable = findViewById(R.id.bookingpage_acceptable);
        Button confirm = findViewById(R.id.bookingpage_confirm);

        start_time = findViewById(R.id.bookingpage_startime);
        start_time.setIs24HourView(true);
        end_time = findViewById(R.id.bookingpage_endtime);
        end_time.setIs24HourView(true);
        timeline = findViewById(R.id.timeline);

        TimeZone tz = TimeZone.getTimeZone("GMT+10:00");
        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.setTimeZone(tz);
        int year = currentCalendar.get(Calendar.YEAR);
        int month = currentCalendar.get(Calendar.MONTH) + 1;
        int day = currentCalendar.get(Calendar.DAY_OF_MONTH);
        int hour = currentCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = currentCalendar.get(Calendar.MINUTE);
        @SuppressLint("DefaultLocale") String currentDateString = String.format("%02d/%02d/%d", day, month, year);

        locationString = getIntent().getStringExtra("location_string");


        date.setOnClickListener(
                v -> {
                    // The selected month is from 0 to 11, and needs to be +1
                    DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {

                        selectedMonth += 1;
                        selectedDate = selectedDay + "/" + selectedMonth + "/" + selectedYear;

                        Calendar selectedCalendar = Calendar.getInstance();
                        //When not outputting a string, month does not need to be +1
                        selectedCalendar.set(selectedYear, selectedMonth, selectedDay);
                        if (selectedCalendar.before(currentCalendar)) {
                            Toast.makeText(this, "Please select a date today or later", Toast.LENGTH_SHORT).show();
                        } else {
                            isDateSelected = true;
                            date.setText(selectedDate);
                            fetchExistingBookings();
                        }
                    }, year, month, day);
                    datePickerDialog.show();
                });

        confirm.setOnClickListener(v -> {
            if (isOverlapping) {
                Toast.makeText(this, "Selected time overlaps with an existing booking", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!isDateSelected) {

                Toast.makeText(this, "Selected a date please", Toast.LENGTH_SHORT).show();
                return;
            }
            if (is_booked) {
                AlertDialog dialog = getAlertDialog();
                dialog.show();
                return;
            }
            if (start_time.getHour() == end_time.getHour() && start_time.getMinute() == end_time.getMinute()) {
                Toast.makeText(this, "Start time and end time must be different", Toast.LENGTH_SHORT).show();
                return;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Twice verification");
            builder.setMessage("Make sure all information is correct");
            builder.setPositiveButton("YES", (dialog, which) -> {
                // update user and subject
                user.setSubscribedAddress(locationString);
                subject.subscribe(locationString, user.getEmail());

                selectedDate = date.getText().toString().trim();
                int startHour = start_time.getHour();
                int startMinute = start_time.getMinute();
                int endHour = end_time.getHour();
                int endMinute = end_time.getMinute();
                String remainingAcceptable = acceptable.getText().toString();

                Calendar startCalendar = Calendar.getInstance();
                startCalendar.set(currentCalendar.get(Calendar.YEAR),
                        currentCalendar.get(Calendar.MONTH),
                        currentCalendar.get(Calendar.DAY_OF_MONTH),
                        startHour, startMinute);

                if (selectedDate.compareTo(currentDateString) == 0 && startCalendar.before(currentCalendar)) {
                    Toast.makeText(this, "The start time must be later than the current time", Toast.LENGTH_SHORT).show();
                    return;
                }

                Calendar endCalendar = Calendar.getInstance();
                endCalendar.set(currentCalendar.get(Calendar.YEAR),
                        currentCalendar.get(Calendar.MONTH),
                        currentCalendar.get(Calendar.DAY_OF_MONTH),
                        endHour, endMinute);

                if (endCalendar.before(startCalendar)) {
                    Toast.makeText(this, "The end time must be later than the start time", Toast.LENGTH_SHORT).show();
                    return;
                }
                saveBookingToFirestore(selectedDate, startHour, startMinute, endHour, endMinute, remainingAcceptable);
                Intent intent = new Intent(BookingPage.this, HomePage.class);
                startActivity(intent);
                finish();
                HomePage.chosen = null;
            });
            builder.setNegativeButton("CANCLE", (dialog, which) -> {
                dialog.dismiss();
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        });

        start_time.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                checkTimeOverlap();
            }
        });

        end_time.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                checkTimeOverlap();
            }
        });
    }

    /**
     * Creates and returns an AlertDialog object where the user can choose to cancel the current appointment or keep an existing appointment
     *
     * @return AlertDialog
     */
    private AlertDialog getAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);


        builder.setTitle("You have one booking already");

        builder.setMessage("Would you like to cancel the current booking and book a new one?");

        builder.setPositiveButton("OK", (dialog, which) -> {
            db.collection("bookings")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String documentId = document.getId();
                                if (documentId.equals(user.getEmail())) {
                                    DocumentReference docRef = db.collection("bookings").document(documentId);
                                    docRef.delete();
                                    is_booked = false;
                                    ManageBooking.address = null;
                                    ManageBooking.date = null;
                                    ManageBooking.startTime = null;
                                    ManageBooking.endTime = null;
                                    break;
                                }
                            }
                        } else {
                            Log.w("Firestore", "Error getting documents: ", task.getException());
                        }
                    });
            Toast.makeText(getApplicationContext(), "You clicked OK", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            Intent intent = new Intent(BookingPage.this, HomePage.class);
            startActivity(intent);
            finish();
            dialog.dismiss();
        });
        return builder.create();
    }

    protected void onResume() {
        super.onResume();
        //update online status
        if (!User.getInstance().isOnline()) {
            User.getInstance().setOnline(true);
        }
        Log.d("isOnline", " " + User.getInstance().isOnline());
    }

    protected void onPause() {
        super.onPause();
        //update online status
        if (User.getInstance().isOnline()) {
            User.getInstance().setOnline(false);
        }
        Log.d("isOnline", "booking page on pause" + User.getInstance().isOnline());
    }

    private void fetchExistingBookings() {
        db.collection("bookings")
                .whereEqualTo("address", locationString)
                .whereEqualTo("date", selectedDate)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        existingBookings.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String startTimeString = document.getString("start_time_string");
                            String endTimeString = document.getString("end_time");

                            float startTime = ConvertTime.convertTimeToFloat(startTimeString);
                            float endTime = ConvertTime.convertTimeToFloat(endTimeString);

                            existingBookings.add(new Timeline.TimeSlot(startTime, endTime, Color.RED)); // 使用红色表示已存在的预订
                        }
                        updateTimeline();
                    } else {
                        Log.w("BookingPage", "Error getting documents.", task.getException());
                    }
                });
    }

    @SuppressLint("DefaultLocale")
    private void saveBookingToFirestore(String date, int startHour, int startMinute, int endHour, int endMinute, String acceptable) {
        Map<String, Object> bookingMap = new HashMap<>();
        bookingMap.put("date", date);
        bookingMap.put("start_time", startHour * 60 + startMinute);
        bookingMap.put("start_time_string", String.format("%02d:%02d", startHour, startMinute));
        bookingMap.put("end_time", String.format("%02d:%02d", endHour, endMinute));
        bookingMap.put("acceptable", acceptable);
        bookingMap.put("address", locationString);
        bookingMap.put("isNotified", false);

        String userEmail = user.getEmail();
        if (userEmail != null) {
            db.collection("bookings").document(userEmail)
                    .set(bookingMap)
                    .addOnSuccessListener(aVoid -> {
                        Log.d("BookingPage", "Booking successfully written for user: " + userEmail);
                        Toast.makeText(this, "Booking saved successfully", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Log.w("BookingPage", "Error adding booking for user: " + userEmail, e);
                        Toast.makeText(this, "Failed to save booking", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "User is not logged in", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateTimeline() {
        int startHour = start_time.getHour();
        int startMinute = start_time.getMinute();
        int endHour = end_time.getHour();
        int endMinute = end_time.getMinute();

        float startTime = (startHour * 60 + startMinute) / (24f * 60);
        float endTime = (endHour * 60 + endMinute) / (24f * 60);

        List<Timeline.TimeSlot> slots = new ArrayList<>(existingBookings);
        slots.add(new Timeline.TimeSlot(startTime, endTime, Color.parseColor("#FFA500")));

        timeline.setTimeSlots(slots);
    }

    @SuppressLint("SetTextI18n")
    private void checkTimeOverlap() {
        int startHour = start_time.getHour();
        int startMinute = start_time.getMinute();
        int endHour = end_time.getHour();
        int endMinute = end_time.getMinute();

        float startTime = ConvertTime.convertIntTimeToFloatTime(startHour, startMinute);
        float endTime = ConvertTime.convertIntTimeToFloatTime(endHour, endMinute);

        isOverlapping = false;
        for (Timeline.TimeSlot slot : existingBookings) {
            if (ConvertTime.isTimeOverlapping(startTime, endTime, slot.startTime, slot.endTime)) {
                isOverlapping = true;
                break;
            }
        }

        TextView acceptableTextView = findViewById(R.id.bookingpage_acceptable);
        if (isOverlapping) {
            acceptableTextView.setText("Already booked");
            acceptableTextView.setTextColor(Color.RED);
        } else {
            acceptableTextView.setText("Available");
            acceptableTextView.setTextColor(Color.GREEN);
        }

        updateTimeline();
    }


}
