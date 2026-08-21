package com.example.smartcity.notifications;

import com.example.smartcity.tools.User;

/**
 * @author : Lanping Hu
 * UID: u7904927
 */
public class NotificationHandlerFactory {
    private User user;

    public NotificationHandlerFactory(User user) {
        this.user = user;
    }

    /**
     * Returns the appropriate NotificationHandler based on the provided text.
     *
     * @param text The notification text to determine the handler
     * @return The corresponding NotificationHandler or null if no match is found
     */
    public NotificationHandler getHandler(String text) {
        switch (text) {
            case "You have a new friend request":
                return new FriendRequestHandler(user);
            case "You have a new message":
                return new NewMessageHandler(user);
            case "Your booking time is near":
                return new BookingTimeNearHandler(user);
            case "Your booking is successful":
                return new BookingSuccessHandler(user);
            case "Your report is successful":
                return new ReportSuccessHandler(user);
            case "You report has a new feedback":
                return new ReportFeedbackHandler(user);
            case "Time limit is slow":
                return new LowTimeLimitHandler(user);
            case "Your booking place has a new report":
                return new BookingPlaceReportHandler(user);
            default:
                return null;
        }
    }
}