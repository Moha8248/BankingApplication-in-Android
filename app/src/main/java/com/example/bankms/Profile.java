package com.example.bankms;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class Profile extends AppCompatActivity {

    private TextView fullNameTextView, emailTextView, balanceTextView,acc,occ,phno,add;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private DatabaseReference databaseReference;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Firebase Authentication and Database
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        user = firebaseAuth.getCurrentUser();

        fullNameTextView = findViewById(R.id.tvFullName);
        emailTextView = findViewById(R.id.tvEmail);
        balanceTextView = findViewById(R.id.tvBalance);
        acc=findViewById(R.id.tvano);
        add=findViewById(R.id.tvadd);
        occ=findViewById(R.id.tvoccu);
        phno=findViewById(R.id.tvphno);




        if (user != null) {
            loadUserProfile();
        }
    }

    private void loadUserProfile() {
        String userId = user.getUid();

        databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String fullName = dataSnapshot.child("fullName").getValue(String.class);
                    String accountno1=dataSnapshot.child("accountNumber").getValue(String.class);
                    String phno1=dataSnapshot.child("phoneNumber").getValue(String.class);
                    String oc1=dataSnapshot.child("occupation").getValue(String.class);
                    String email = dataSnapshot.child("emailAddress").getValue(String.class);
                    String add1=dataSnapshot.child("address").getValue(String.class);
                    String balance = dataSnapshot.child("balance").getValue(String.class);




                    fullNameTextView.setText("Name :"+fullName);
                    emailTextView.setText("Email :"+email);
                    balanceTextView.setText("Balance :"+balance);
                    phno.setText("PhoneNumber :"+phno1);
                    occ.setText("Occupation :"+oc1);
                    add.setText("Address :"+add1);
                    acc.setText("Account Number :"+accountno1);

                } else {
                    // Handle the case when user data is not found
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the database error
            }
        });
    }
}
