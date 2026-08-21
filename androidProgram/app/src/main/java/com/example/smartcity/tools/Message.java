package com.example.smartcity.tools;

import java.util.Date;

/**
 * @author :Shangyi Shen
 * UID: u7735222
 */

public class Message {
    public String sender, receiver, message;
    public Status status;
    public long timestamp;
    public Date dateObject;

    public Message() {
    }

    public Message(String sender, String receiver, String message, long timestamp, Status status) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.timestamp = timestamp;
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
