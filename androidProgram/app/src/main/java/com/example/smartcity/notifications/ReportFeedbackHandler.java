package com.example.smartcity.notifications;

import com.example.smartcity.tools.User;

import java.util.Map;

/**
 * @author : Lanping Hu
 * UID: u7904927
 */
public class ReportFeedbackHandler implements NotificationHandler {
    private User user;

    public ReportFeedbackHandler(User user) {
        this.user = user;
    }

    /**
     * Handles the notification for report feedback.
     * If the user's settings allow for report feedback notifications, formats and returns the feedback message.
     * Otherwise, returns null.
     *
     * @param notification The notification data containing feedback details
     * @return The formatted feedback message or null if not displayed
     */
    @Override
    public String handleNotification(Map<String, Object> notification) {
        if (user.setting_report_feedback) {
            String time = (String) notification.get("time");
            String from = (String) notification.get("from");
            return String.format("%s Your report has a new feedback from %s", time, from);
        }
        return null;
    }
}