package com.example.bankms;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class Withdraw extends AppCompatActivity {

    private EditText amountEditText;
    private TextView balanceTextView;
    private Button withdrawButton;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference userRef;
    private DatabaseReference transactionsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);

        // Initialize Firebase Authentication and Database
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            String uid = currentUser.getUid();
            userRef = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
            transactionsRef = FirebaseDatabase.getInstance().getReference().child("transactions");
            // Set the current balance
            updateBalance();

            amountEditText = findViewById(R.id.etWithdrawAmount);
            balanceTextView = findViewById(R.id.tvWithdrawBalance);
            withdrawButton = findViewById(R.id.btnWithdraw);

            withdrawButton.setOnClickListener(new View.OnClickListener() {
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

                    // Check if balance is not null
                    if (balance != null) {
                        balanceTextView.setText("Current Balance: ₹" + balance);
                    } else {
                        // Handle the case where balance is null
                        balanceTextView.setText("Balance not available");
                    }
                } else {
                    // Handle the case where no user with the specified UID is found
                    balanceTextView.setText("User not found");
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
                            Toast.makeText(Withdraw.this, "Withdrawal successful", Toast.LENGTH_SHORT).show();

                            // Update the displayed balance
                            updateBalance();
                        } else {
                            Toast.makeText(Withdraw.this, "Insufficient balance", Toast.LENGTH_SHORT).show();
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
        Transaction1 transaction = new Transaction1(transactionId, uid, "Withdraw", amount);

        // Save the transaction to the database
        transactionsRef.child(transactionId).setValue(transaction);
    }
}
