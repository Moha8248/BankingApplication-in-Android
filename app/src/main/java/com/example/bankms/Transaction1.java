package com.example.bankms;

import com.google.firebase.database.IgnoreExtraProperties;
import java.util.Date;


@IgnoreExtraProperties
public class Transaction1 {
    private String transactionId;
    private String sender;
    private String recipient;
    private double amount;
    private long timestamp;

    public Transaction1() {
        // Default constructor required for Firebase
    }

    public Transaction1(String transactionId, String sender, String recipient, double amount) {
        this.transactionId = transactionId;
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
        this.timestamp = new Date().getTime(); // Store current timestamp
    }

    // Getters and setters
    // ...

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
