package com.example.smartcity.notifications;

import com.example.smartcity.tools.User;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;

/**
 * @author : Lanping Hu
 * UID: u7904927
 */
public class BookingTimeNearHandler implements NotificationHandler {
    private static final String TAG = "BookingTimeNearHandler";
    private User user;

    public BookingTimeNearHandler(User user) {
        this.user = user;
    }

    /**
     * Handles the notification for booking time reminders.
     * Checks if the user's booking setting is enabled and if the 
     * notification time is within 2 hours from the current time.
     * 
     * @param notification A map containing notification details, 
     *                     including the time and the source of the booking.
     * @return A formatted string message if the booking time is near, 
     *         or null if it is not.
     */
    @Override
    public String handleNotification(Map<String, Object> notification) {
        if (user.setting_booking_near) {
            String time = (String) notification.get("time");
            String from = (String) notification.get("from");
            String notificationTimeStr = time;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            try {
                long notificationTimestamp = sdf.parse(notificationTimeStr).getTime();
                long currentTime = System.currentTimeMillis();
                // check whether within 2 hours
                if ((currentTime - notificationTimestamp) <= 2 * 60 * 60 * 1000) {
                    return String.format("%s Your booking time is near for %s", time, from);
                }
            } catch (ParseException e) {
                Log.e(TAG, "Error parsing date", e);
            }
        }
        return null;
    }
}