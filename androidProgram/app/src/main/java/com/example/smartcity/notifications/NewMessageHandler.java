package com.example.smartcity.notifications;

import com.example.smartcity.tools.User;

import java.util.Map;

/**
 * @author : Lanping Hu
 * UID: u7904927
 */
public class NewMessageHandler implements NotificationHandler {
    private User user;

    public NewMessageHandler(User user) {
        this.user = user;
    }

    /**
     * Handles the notification for a new message.
     * If the user's settings allow for friend messages, formats and returns the message.
     * Otherwise, returns null.
     *
     * @param notification The notification data containing message details
     * @return The formatted message or null if not displayed
     */
    @Override
    public String handleNotification(Map<String, Object> notification) {
        if (user.setting_friend_message) {
            String time = (String) notification.get("time");
            String from = (String) notification.get("from");
            return String.format("%s You have a new message from %s", time, from);
        }
        return null;
    }
}