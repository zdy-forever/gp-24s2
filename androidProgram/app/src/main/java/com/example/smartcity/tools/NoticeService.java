package com.example.smartcity.tools;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import androidx.core.app.NotificationCompat;

import com.example.smartcity.NoticePage;
import com.example.smartcity.P2PMessage;
import com.example.smartcity.R;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.SetOptions;

import java.util.List;
import java.util.Map;

/**
 * @author : Lanping Hu
 * UID: u7904927
 */

public class NoticeService extends Service {
    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    private User user;
    private User currentUser=user.getInstance();
    private ListenerRegistration reportListener;
    private Subject subject = new Subject(); 

    private ListenerRegistration allRequestListener;
    private boolean isInitialFetch = true;

    private static final long CHECK_INTERVAL = 60 * 60 * 1000;
    private Handler handler = new Handler();
    private boolean isCheckScheduled = false;

    /**
     * Called when the service is started. Schedules a periodic check for bookings.
     *
     * @param intent The intent that started the service
     * @param flags Additional data about the start request
     * @param startId A unique integer representing the start request
     * @return The return value indicates how the system should behave if the service is killed
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!isCheckScheduled&&currentUser!=null) {
            schedulePeriodicCheck();
        }
        return START_STICKY;
    }

    private void listenForReports() {
        if(currentUser!=null)
        {
            db.collection("reports")
                    .addSnapshotListener((queryDocumentSnapshots, e) -> {
                        if (e != null) {
                            Log.w("NoticeService", "Listen failed.", e);
                            return;
                        }
                        for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.MODIFIED || dc.getType() == DocumentChange.Type.ADDED) {
                                String address = dc.getDocument().getString("address");
                                Boolean isNotified = dc.getDocument().getBoolean("isNotified");
                                if (address != null && (isNotified == null || !isNotified)) {
                                    notifySubscribers(address);
                                    // update isNotified to true
                                    db.collection("reports").document(dc.getDocument().getId())
                                            .update("isNotified", true)
                                            .addOnSuccessListener(aVoid -> Log.d("NoticeService", "isNotified set to true for report: " + dc.getDocument().getId()))
                                            .addOnFailureListener(e2 -> Log.w("NoticeService", "Error updating isNotified for report: " + dc.getDocument().getId(), e2));
                                }
                            }
                        }
                    });
        }
    }

    /**
     * Notifies subscribers of a new report for the given address.
     *
     * @param address The address of the report
     */
    private void notifySubscribers(String address) {
        if(currentUser!=null)
        {
            subject.getSubscribersForAddress(address, new Subject.OnSubscribersFetchedListener() {
                @Override
                public void onSubscribersFetched(List<String> subscribers) {
                    for (String subscriberEmail : subscribers) {
                        subscriberNotification(subscriberEmail, address);
                    }
                }
                @Override
                public void onError(Exception e) {
                    Log.w("NoticeService", "Error fetching subscribers: ", e);
                }
            });
        }
    }

    /**
     * Sends a notification to a subscriber about a new report.
     *
     * @param subscriberEmail The email of the subscriber
     * @param address The address of the report
     */
    private void subscriberNotification(String subscriberEmail, String address) {
        if(currentUser!=null)
        {
            Map<String, Object> notification = new HashMap<>();
            notification.put("text", "Your booking place has a new report");
            notification.put("from", address);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            String currentTime = sdf.format(new Date());
            notification.put("time", currentTime);
            notification.put("isNotified", false); // Set isNotified to false

            // Add the notification to the subscriber's notifications array in Firestore
            db.collection("notifications")
                    .document(subscriberEmail)
                    .set(
                            new HashMap<String, Object>() {{
                                put("notifications", FieldValue.arrayUnion(notification));
                            }},
                            SetOptions.merge()
                    )
                    .addOnSuccessListener(aVoid -> {
                        Log.d("NoticeService", "Notification successfully added for: " + subscriberEmail);
                    })
                    .addOnFailureListener(e -> {
                        Log.w("NoticeService", "Error adding notification for: " + subscriberEmail, e);
                    });
        }
    }
    private void schedulePeriodicCheck() {
        if(currentUser!=null)
        {
            isCheckScheduled = true;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    checkBookings();
                    handler.postDelayed(this, CHECK_INTERVAL);
                }
            });
        }
    }

    private void checkBookings() {
        if(currentUser!=null)
        {
            String userEmail = user.getEmail();

            db.collection("bookings").document(userEmail).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String bookingDateString = documentSnapshot.getString("date");
                            Long startTime = documentSnapshot.getLong("start_time");
                            String address = documentSnapshot.getString("address");
                            Boolean isNotified = documentSnapshot.getBoolean("isNotified");
                            if ((isNotified == null || !isNotified)&&isToday(bookingDateString) && isNextHour(startTime )) {
                                Log.d("NoticeService", "Booking notification triggered");
                                BookingNearNotification(address);
                                updateBookingNotificationStatus(userEmail);
                            }
                        }
                    })
                    .addOnFailureListener(e -> Log.e("NoticeService", "Error checking bookings", e));
        }
    }
    
    /*
     * Sends a notification when the user's booking time is near.
     *
     * @param address The address of the booking
     */
    private void BookingNearNotification(String address) {
        if(currentUser!=null)
        {
            String userEmail = user.getEmail();

            Map<String, Object> notification = new HashMap<>();
            notification.put("text", "Your booking time is near");
            notification.put("from", address);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            String currentTime = sdf.format(new Date());
            notification.put("time", currentTime);

            db.collection("notifications")
                    .document(userEmail)
                    .update("notifications", FieldValue.arrayUnion(notification))
                    .addOnSuccessListener(aVoid -> {
                        Log.d("NoticeService", "Booking notification successfully added.");
                    })
                    .addOnFailureListener(e -> {
                        Log.w("NoticeService", "Error adding booking notification", e);
                    });
        }
    }

    private void updateBookingNotificationStatus(String userEmail) {
        if(currentUser!=null)
        {
            db.collection("bookings").document(userEmail)
                    .update("isNotified", true)
                    .addOnSuccessListener(aVoid -> Log.d("NoticeService", "Booking notification status updated"))
                    .addOnFailureListener(e -> Log.e("NoticeService", "Error updating booking notification status", e));
        }
    }

    private boolean isToday(String bookingDateString) {
        SimpleDateFormat sdf = new SimpleDateFormat("d/M/yyyy", Locale.getDefault());
        String todayFormatted = sdf.format(new Date());
        boolean isToday = bookingDateString.equals(todayFormatted);
        return isToday;
    }

    private boolean isNextHour(Long startTime) {
        Calendar now = Calendar.getInstance();
        int currentMinutes = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE);
        int timeDifference = (int) (startTime - currentMinutes);

        // when cross midnight
        if (timeDifference < 0) {
            timeDifference += 24 * 60; // minutes in a day
        }

        return timeDifference<=60;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        if(currentUser!=null)
        {
            db=FirebaseFirestore.getInstance();
            user=User.getInstance();
            if (user == null || user.getEmail() == null) {
                Log.e("NoticeService", "User not initialized");
                stopSelf();
                return;
            }
            listenForReports();
            listenForMessages();
            listenForAllNotices();
        }
    }

    private void listenForAllNotices(){
        if(currentUser!=null)
        {
            String email = user.getEmail();
            allRequestListener = db.collection("notifications").document(email)
                    .addSnapshotListener((documentSnapshot, e) -> {
                        if (e != null) {
                            Log.w("ListenForAllRequests", "Listen failed.", e);
                            return;
                        }
                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            List<Map<String, Object>> notifications =
                                    (List<Map<String, Object>>) documentSnapshot.get("notifications");
                            if (notifications != null) {
                                if (isInitialFetch) {
                                    isInitialFetch = false;
                                    Log.d("NoticeService", "Initial data fetch, not sending notifications");
                                } else {
                                    sendAllNotices(notifications);
                                }
                            }
                        }
                    });
        }
    }
    private void listenForMessages() {
        if(currentUser!=null)
        {
            String email = user.getEmail();
            db.collection("message")
                    .whereEqualTo("receiver", email)
                    .addSnapshotListener((queryDocumentSnapshots, e) -> {
                        if (e != null) {
                            Log.w("ListenForMessages", "Listen failed.", e);
                            return;
                        }
                        if (queryDocumentSnapshots != null) {
                            for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                                if (dc.getType() == DocumentChange.Type.ADDED) {
                                    String sender = dc.getDocument().getString("sender");

                                    // 检查用户是否在聊天中
                                    if (P2PMessage.isInChat && sender.equals(P2PMessage.p2pmessage_friend_email)) {
                                        Log.d("NoticeService", "User is in chat with this sender, ignoring new message.");
                                        return; // 如果用户在聊天中且消息来自当前聊天对象，则忽略新消息
                                    }

                                    // 发送通知
                                    messageNotification(sender); // 调用通知方法
                                }
                            }
                        }
                    });
        }
    }

    private void messageNotification(String sender) {
        if(currentUser!=null)
        {
            String userEmail = user.getEmail();

            Map<String, Object> notification = new HashMap<>();
            notification.put("text", "You have a new message");
            notification.put("from", sender);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            String currentTime = sdf.format(new Date());
            notification.put("time", currentTime);

            db.collection("notifications")
                    .document(userEmail)
                    .update("notifications", FieldValue.arrayUnion(notification))
                    .addOnSuccessListener(aVoid -> {
                        Log.d("NoticeService", "Message notification successfully added.");
                    })
                    .addOnFailureListener(e -> {
                        Log.w("NoticeService", "Error adding message notification", e);
                    });
        }
    }
    
    private void sendAllNotices(List<Map<String, Object>> notifications) {
        if(currentUser!=null)
        {
            for (Map<String, Object> notification : notifications) {
                String text = (String) notification.get("text");

                switch (text) {
                    case "You have a new friend request":
                        if (user.setting_friend_request)
                            sendOneNotice(text);
                        break;
                    case "You have a new message":
                        if (user.setting_friend_message)
                            sendOneNotice(text);
                        break;
                    case "Your booking is successful":
                        if (user.setting_booking_success)
                            sendOneNotice(text);
                        break;
                    case "Your booking time is near":
                        if (user.setting_booking_near)
                            sendOneNotice(text);
                        break;
                    case "Your report is successful":
                        if (user.setting_report_success)
                            sendOneNotice(text);
                        break;
                    case "You report has a new feedback":
                        if (user.setting_report_feedback)
                            sendOneNotice(text);
                        break;
                    case "Time limit is slow":
                        if (user.setting_low_time_limit)
                            sendOneNotice(text);
                        break;
                    case "Your booking place has a new report":
                        sendOneNotice(text);
                        updateNotificationStatus(notification);
                        break;
                }
            }
        }
    }

    private void updateNotificationStatus(Map<String, Object> notification) {
        if(currentUser!=null)
        {
            String userEmail = user.getEmail();
            db.collection("notifications")
                    .document(userEmail)
                    .update("notifications", FieldValue.arrayRemove(notification)) // Remove the processed notification
                    .addOnSuccessListener(aVoid -> {
                        Log.d("NoticeService", "Notification successfully removed after processing.");
                    })
                    .addOnFailureListener(e -> {
                        Log.w("NoticeService", "Error removing notification for: " + userEmail, e);
                    });
        }
    }

    private void sendOneNotice(String text) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, NoticePage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // 设置标志，以确保新的 Activity 被启动
        //intent.putExtra("requestCount", requestCount);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "comp2100")
                .setSmallIcon(R.drawable.app_logo)
                //.setContentTitle("New Friend Requests")
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent) // 设置 PendingIntent
                .setAutoCancel(true); // 点击通知后自动消失

        notificationManager.notify(1, builder.build());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(currentUser!=null)
        {
            if (allRequestListener != null) {
                allRequestListener.remove(); // 停止监听
                allRequestListener = null;
            }
            handler.removeCallbacksAndMessages(null);
            isCheckScheduled = false;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
