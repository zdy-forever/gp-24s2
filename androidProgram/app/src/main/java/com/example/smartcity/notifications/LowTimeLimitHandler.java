package com.example.smartcity.notifications;

import com.example.smartcity.tools.User;

import java.util.Map;

/**
 * @author : Lanping Hu
 * UID: u7904927
 */
public class LowTimeLimitHandler implements NotificationHandler {
    private User user;

    public LowTimeLimitHandler(User user) {
        this.user = user;
    }
    
    /**
     * Handles the notification by checking the user's low time limit setting.
     * If the setting is enabled, it formats a message indicating that the time limit is slow.
     *
     * @param notification A map containing notification details, including "time" and "from".
     * @return A formatted string message if the low time limit setting is enabled, otherwise null.
     */
    @Override
    public String handleNotification(Map<String, Object> notification) {
        if (user.setting_low_time_limit) {
            String time = (String) notification.get("time");
            String from = (String) notification.get("from");
            return String.format("%s Time limit is slow for %s", time, from);
        }
        return null;
    }
}