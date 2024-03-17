package com.example.bankms;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import com.google.firebase.auth.FirebaseAuth;
import androidx.annotation.NonNull;
import com.google.firebase.database.Query;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DashboardActivity extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private FirebaseUser user;
    private Button addAccountButton ,profile ,deposite ,withdraw ,transfermoney ,recentTransaction ,payBill ,logoutButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        addAccountButton = findViewById(R.id.addaccount);
        profile=findViewById(R.id.profile);
        deposite=findViewById(R.id.deposite);
        withdraw=findViewById(R.id.withdraw);
        transfermoney=findViewById(R.id.transferMoneyButton);
        recentTransaction=findViewById(R.id.recentTransactionsButton);
        payBill=findViewById(R.id.payBillsButton);
        logoutButton = findViewById(R.id.logout);

        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        user = FirebaseAuth.getInstance().getCurrentUser();

        checkIfEmailExists();

        addAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the button click (navigate to AddAccountActivity or perform other action)
                startActivity(new Intent(DashboardActivity.this,CreateAccountActivity.class));
            }
        });


        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, Profile.class));
            }
        });

        deposite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, Deposite.class));
            }
        });

        withdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, Withdraw.class));
            }
        });

        transfermoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, TransferMoney.class));
            }
        });

        recentTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, TransactionActivity.class ));
            }
        });

        payBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, PayBills.class));
            }
        });


        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutUser();
            }
        });
    }
    private void logoutUser() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void checkIfEmailExists() {
        if (user != null) {
            String userEmail = user.getEmail();

            Query query = databaseReference.orderByChild("emailAddress").equalTo(userEmail);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // User with the logged-in email already exists in the database
                        // Hide the "Add Account" button or perform any other action
                        addAccountButton.setVisibility(View.GONE);
                    } else {
                        // User with the logged-in email doesn't exist in the database
                        // Show the "Add Account" button
                        addAccountButton.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle the error if needed
                }
            });
        }
    }

}
