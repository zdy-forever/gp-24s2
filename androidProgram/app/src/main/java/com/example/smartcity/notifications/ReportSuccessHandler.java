package com.example.smartcity.notifications;

import com.example.smartcity.tools.User;

import java.util.Map;

/**
 * @author : Lanping Hu
 * UID: u7904927
 */
public class ReportSuccessHandler implements NotificationHandler {
    private User user;

    public ReportSuccessHandler(User user) {
        this.user = user;
    }

    /**
     * Handles the notification for report success.
     * If the user's settings allow for report success notifications, formats and returns the success message.
     * Otherwise, returns null.
     *
     * @param notification The notification data containing success details
     * @return The formatted success message or null if not displayed
     */
    @Override
    public String handleNotification(Map<String, Object> notification) {
        if (user.setting_report_success) {
            String time = (String) notification.get("time");
            String from = (String) notification.get("from");
            return String.format("%s Your report is successful for %s", time, from);
        }
        return null;
    }
}