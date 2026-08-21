package com.example.smartcity.notifications;

import com.example.smartcity.tools.User;

import java.util.Map;

/**
 * @author : Lanping Hu
 * UID: u7904927
 */

public class BookingSuccessHandler implements NotificationHandler {
    private User user;

    public BookingSuccessHandler(User user) {
        this.user = user;
    }

    /**
     * Handles the notification for booking success.
     *
     * @param notification A map containing notification details, including time and from.
     * @return A formatted success message if the user's booking success setting is enabled; otherwise, null.
     */
    @Override
    public String handleNotification(Map<String, Object> notification) {
        if (user.setting_booking_success) {
            String time = (String) notification.get("time");
            String from = (String) notification.get("from");
            return String.format("%s Your booking is successful for %s", time, from);
        }
        return null;
    }
}