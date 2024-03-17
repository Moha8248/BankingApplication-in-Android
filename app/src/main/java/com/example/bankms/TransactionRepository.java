package com.example.bankms;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TransactionRepository {

    private DatabaseReference transactionsRef;

    public TransactionRepository() {
        // Initialize Firebase Database reference
        transactionsRef = FirebaseDatabase.getInstance().getReference("transactions");
    }

    public void addTransaction(Transaction1 transaction) {
        // Generate a unique key for the transaction
        String transactionId = transactionsRef.push().getKey();

        // Set the transaction data in the database
        transactionsRef.child(transactionId).setValue(transaction);
    }
}
