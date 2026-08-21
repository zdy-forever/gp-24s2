package com.example.smartcity.tools;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Subject {
    private FirebaseFirestore db = FirebaseFirestore.getInstance(); // Firestore instance
    private ListenerRegistration listenerRegistration; // Listener for Firestore updates

    // Subscribe a user to notifications for a specific address
    public void subscribe(String address, String email) {
        addUserToAddressInFirebase(address, email);
    }

    // Unsubscribe a user from notifications for a specific address
    public void unsubscribe(String address, String email) {
        removeUserFromAddressInFirebase(address, email);

    }

    // Add the user's email to the address document in Firestore
    private void addUserToAddressInFirebase(String address, String userEmail) {
        db.collection("subjects").document(address)
                .set(
                    new HashMap<String, Object>() {{
                        put("users", FieldValue.arrayUnion(userEmail));
                    }},
                    SetOptions.merge() // Use merge to create the document if it doesn't exist
                )
                .addOnSuccessListener(aVoid -> {
                    // Successfully updated Firestore
                })
                .addOnFailureListener(e -> {
                    // Handle the error
                });
    }

    // Remove the user's email from the address document in Firestore
    private void removeUserFromAddressInFirebase(String address, String userEmail) {
        db.collection("subjects").document(address)
                .update("users", FieldValue.arrayRemove(userEmail))
                .addOnSuccessListener(aVoid -> {
                    // Successfully updated Firestore
                })
                .addOnFailureListener(e -> {
                    // Handle the error
                });
    }

    // Get subscribers for a specific address
    public void getSubscribersForAddress(String address, OnSubscribersFetchedListener listener) {
        db.collection("subjects").document(address)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> subscribers = (List<String>) documentSnapshot.get("users");
                        listener.onSubscribersFetched(subscribers != null ? subscribers : new ArrayList<>());
                    } else {
                        listener.onSubscribersFetched(new ArrayList<>()); // No subscribers found
                    }
                })
                .addOnFailureListener(e -> {
                    listener.onError(e); // Handle the error
                });
    }
    // Listener interface for fetching subscribers
    public interface OnSubscribersFetchedListener {
        void onSubscribersFetched(List<String> subscribers);
        void onError(Exception e);
    }
}