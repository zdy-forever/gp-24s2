package com.example.smartcity.notifications;

import java.util.Map;

/**
 * @author : Lanping Hu
 * UID: u7904927
 */
public interface NotificationHandler {
    /**
     * Handles the notification and returns the formatted text. 
     * If the notification does not need to be displayed, returns null.
     *
     * @param notification The notification data
     * @return The formatted notification text or null
     */
    String handleNotification(Map<String, Object> notification);
}