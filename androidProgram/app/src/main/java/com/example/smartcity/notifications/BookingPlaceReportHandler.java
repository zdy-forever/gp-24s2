package com.example.smartcity.notifications;

import com.example.smartcity.tools.User;

import java.util.Map;

/**
 * @author : Lanping Hu
 * UID: u7904927
 */

public class BookingPlaceReportHandler implements NotificationHandler {
    private User user;

    public BookingPlaceReportHandler(User user) {
        this.user = user;
    }

    /**
     * Handles the incoming notification and formats a response message.
     *
     * @param notification A map containing notification details such as time and sender.
     * @return A formatted string response indicating the booking place report.
     */
    @Override
    public String handleNotification(Map<String, Object> notification) {
        String time = (String) notification.get("time");
        String from = (String) notification.get("from");
        return String.format("%s Your booking place has a new report for %s", time, from);
    }
}