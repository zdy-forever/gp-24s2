package com.example.smartcity.notifications;

import com.example.smartcity.tools.User;

import java.util.Map;

/**
 * @author : Lanping Hu
 * UID: u7904927
 */
public class FriendRequestHandler implements NotificationHandler {
    private User user;

    public FriendRequestHandler(User user) {
        this.user = user;
    }

    /**
     * Handles the incoming friend request notification.
     * 
     * @param notification A map containing notification details, including time and sender.
     * @return A formatted string message if the user has friend request notifications enabled; otherwise, null.
     */
    @Override
    public String handleNotification(Map<String, Object> notification) {
        if (user.setting_friend_request) {
            String time = (String) notification.get("time");
            String from = (String) notification.get("from");
            return String.format("%s You have a new friend request from %s", time, from);
        }
        return null;
    }
}