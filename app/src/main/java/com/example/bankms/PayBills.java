package com.example.bankms;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PayBills extends AppCompatActivity {

    private Spinner billTypeSpinner;
    private EditText amountEditText;
    private Button payButton;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference userRef;
    private DatabaseReference transactionsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_bills);

        // Initialize Firebase Authentication and Database
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        billTypeSpinner = findViewById(R.id.billTypeSpinner);
        amountEditText = findViewById(R.id.amountEditText);
        payButton = findViewById(R.id.payButton);

        // Populate the Spinner with bill types
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.bill_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        billTypeSpinner.setAdapter(adapter);

        if (currentUser != null) {
            String uid = currentUser.getUid();
            userRef = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
            transactionsRef = FirebaseDatabase.getInstance().getReference().child("transactions");
            // Set the current balance
            updateBalance();

            payButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    withdrawAmount();
                }
            });
        }
    }

    private void updateBalance() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Retrieve the current balance
                    String balance = dataSnapshot.child("balance").getValue(String.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors if any
            }
        });
    }
    private void withdrawAmount() {
        String amountStr = amountEditText.getText().toString().trim();

        if (!amountStr.isEmpty()) {
            double amount = Double.parseDouble(amountStr);

            // Retrieve the current balance
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String balance1 = dataSnapshot.child("balance").getValue(String.class);

                        double currentBalance = Double.parseDouble(balance1);

                        // Check if there is sufficient balance
                        if (amount <= currentBalance) {
                            // Update the balance after withdrawal
                            double newBalance = currentBalance - amount;
                            String stringValue = String.valueOf(newBalance);
                            userRef.child("balance").setValue(stringValue);

                            saveTransaction(amount);

                            // Display success message
                            Toast.makeText(PayBills.this, "Bill payed successful", Toast.LENGTH_SHORT).show();

                            // Update the displayed balance
                            updateBalance();
                        } else {
                            Toast.makeText(PayBills.this, "Insufficient balance", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle errors if any
                }
            });
        } else {
            Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show();
        }
    }
    private void saveTransaction(double amount) {
        // Generate a unique key for the transaction
        String transactionId = transactionsRef.push().getKey();

        // Get the current user's ID
        String uid = firebaseAuth.getCurrentUser().getUid();

        // Create the transaction object
        Transaction1 transaction = new Transaction1(transactionId, uid, "Bill Payed", amount);

        // Save the transaction to the database
        transactionsRef.child(transactionId).setValue(transaction);
    }
}
