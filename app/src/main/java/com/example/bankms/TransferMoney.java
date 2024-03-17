package com.example.bankms;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class TransferMoney extends AppCompatActivity {

    private EditText recipientIdentifierEditText, transferAmountEditText;
    private Button transferButton;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_money);

        // Initialize Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        recipientIdentifierEditText = findViewById(R.id.etRecipientIdentifier);
        transferAmountEditText = findViewById(R.id.etTransferAmount);
        transferButton = findViewById(R.id.btnTransfer);

        transferButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transferMoney();
            }
        });
    }

    private void transferMoney() {
        String recipientIdentifier = recipientIdentifierEditText.getText().toString().trim();
        String transferAmountStr = transferAmountEditText.getText().toString().trim();

        // Validate recipient identifier and transfer amount
        if (recipientIdentifier.isEmpty() || transferAmountStr.isEmpty()) {
            Toast.makeText(this, "Please enter both recipient identifier and transfer amount", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert transfer amount to double
        double transferAmount = Double.parseDouble(transferAmountStr);

        // Call the method to transfer money to the recipient
        transferMoneyToRecipient(recipientIdentifier, transferAmount);
    }

    private void transferMoneyToRecipient(String recipientIdentifier, double transferAmount) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String senderUID = currentUser.getUid();

            checkBalance(senderUID, transferAmount, new OnBalanceCheckListener() {
                @Override
                public void onBalanceCheck(boolean isSufficient, double currentBalance) {
                    if (isSufficient) {
                        updateBalance(senderUID, currentBalance - transferAmount);

                        findRecipient(recipientIdentifier, new OnRecipientUIDListener() {
                            @Override
                            public void onRecipientUID(String recipientUID) {
                                if (recipientUID != null) {
                                    checkRecipientBalance(recipientUID, new OnRecipientBalanceListener() {
                                        @Override
                                        public void onRecipientBalance(double recipientCurrentBalance) {
                                            updateBalance(recipientUID, recipientCurrentBalance + transferAmount);

                                            Toast.makeText(TransferMoney.this,
                                                    "Money transferred successfully", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    });
                                } else {
                                    Toast.makeText(TransferMoney.this, "Recipient not found", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(TransferMoney.this, "Insufficient balance", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void findRecipient(String recipientIdentifier, OnRecipientUIDListener listener) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

        Query query;
        if (recipientIdentifier.matches("\\d{10}")) {
            // Check if the identifier is a 10-digit number (assuming it's a phone number)
            query = usersRef.orderByChild("phoneNumber").equalTo(recipientIdentifier);
        } else {
            // Otherwise, assume it's an account number
            query = usersRef.orderByChild("accountNumber").equalTo(recipientIdentifier);
        }

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String recipientUID = dataSnapshot.getChildren().iterator().next().getKey();
                    listener.onRecipientUID(recipientUID);
                } else {
                    listener.onRecipientUID(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onRecipientUID(null);
            }
        });
    }

    private void checkRecipientBalance(String recipientUID, OnRecipientBalanceListener listener) {
        DatabaseReference recipientRef = FirebaseDatabase.getInstance().getReference("users").child(recipientUID);

        recipientRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String recurrentbalance=dataSnapshot.child("balance").getValue(String.class);
                   double recipientCurrentBalance =Double.parseDouble(recurrentbalance);
                    listener.onRecipientBalance(recipientCurrentBalance);
                } else {
                    listener.onRecipientBalance(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onRecipientBalance(0);
            }
        });
    }

    // Interface to handle recipient UID retrieval
    interface OnRecipientUIDListener {
        void onRecipientUID(String recipientUID);
    }

    // Interface to handle recipient balance retrieval
    interface OnRecipientBalanceListener {
        void onRecipientBalance(double recipientCurrentBalance);
    }

    // Interface to handle balance check
    interface OnBalanceCheckListener {
        void onBalanceCheck(boolean isSufficient, double currentBalance);
    }

    private void checkBalance(String senderUID, double transferAmount, OnBalanceCheckListener listener) {
        DatabaseReference senderRef = FirebaseDatabase.getInstance().getReference("users").child(senderUID);

        senderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String currentbalance1=dataSnapshot.child("balance").getValue(String.class);
                    double currentBalance =Double.parseDouble(currentbalance1);
                    boolean isSufficient = currentBalance >= transferAmount;
                    listener.onBalanceCheck(isSufficient, currentBalance);
                } else {
                    listener.onBalanceCheck(false, 0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onBalanceCheck(false, 0);
            }
        });
    }

    private void updateBalance(String uid, double newBalance) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
        String newbalance1=String.valueOf(newBalance);
        userRef.child("balance").setValue(newbalance1);
    }
}
