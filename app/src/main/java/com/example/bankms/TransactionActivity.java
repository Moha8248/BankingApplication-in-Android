package com.example.bankms;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.*;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class TransactionActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TransactionAdapter adapter;
    private List<Transaction1> transactionList;
    private DatabaseReference transactionsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.transactionRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Firebase Database reference
        transactionsRef = FirebaseDatabase.getInstance().getReference("transactions");

        // Initialize transaction list
        transactionList = new ArrayList<>();

        // Initialize adapter
        adapter = new TransactionAdapter(this, transactionList);

        // Set adapter to RecyclerView
        recyclerView.setAdapter(adapter);

        // Load transaction data from Firebase
        loadTransactions();
    }

    private void loadTransactions() {
        // Attach a listener to read the data at our transactions reference
        transactionsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Clear the list before adding new data
                transactionList.clear();

                // Iterate through each transaction data
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Get transaction object and add to the list
                    Transaction1 transaction = snapshot.getValue(Transaction1.class);
                    transactionList.add(transaction);
                }

                // Notify the adapter of the changes
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
                Toast.makeText(TransactionActivity.this, "Failed to load transactions.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
