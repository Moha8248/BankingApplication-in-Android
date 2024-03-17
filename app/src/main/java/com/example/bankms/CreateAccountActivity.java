package com.example.bankms;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.HashMap;
import java.util.Map;

public class CreateAccountActivity extends AppCompatActivity {

    private EditText fullNameEditText, dobEditText, addressEditText, phoneNumberEditText, emailAddressEditText,
            occupationEditText, pancardEditText, aadharEditText, balanceEditText;
    private Button createAccountButton;

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        // Initialize Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        fullNameEditText = findViewById(R.id.etFullName);
        dobEditText = findViewById(R.id.etDateOfBirth);
        addressEditText = findViewById(R.id.etAddress);
        phoneNumberEditText = findViewById(R.id.etPhoneNumber);
        emailAddressEditText = findViewById(R.id.etEmailAddress);
        occupationEditText = findViewById(R.id.etOccupation);
        pancardEditText = findViewById(R.id.etPancard);
        aadharEditText = findViewById(R.id.etAadharNo);
        balanceEditText = findViewById(R.id.etInitialAmount);

        createAccountButton = findViewById(R.id.btnCreateAccount);

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount();
            }
        });
    }

    private void createAccount() {
        String fullName = fullNameEditText.getText().toString().trim();
        String dob = dobEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();
        String phoneNumber = phoneNumberEditText.getText().toString().trim();
        String emailAddress = emailAddressEditText.getText().toString().trim();
        String occupation = occupationEditText.getText().toString().trim();
        String pancard = pancardEditText.getText().toString().trim();
        String aadhar = aadharEditText.getText().toString().trim();
        String balance = balanceEditText.getText().toString().trim();

        // Validate required fields
        if (fullName.isEmpty() || dob.isEmpty() || address.isEmpty() || phoneNumber.isEmpty() ||
                emailAddress.isEmpty() || occupation.isEmpty() || pancard.isEmpty() || aadhar.isEmpty() || balance.isEmpty()) {
            Toast.makeText(this, "Please fill in all the details", Toast.LENGTH_SHORT).show();
            return;
        }

        // Use the current user's UID as the user ID
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Generate unique account number
        generateAccountNumber(new OnAccountNumberGeneratedListener() {
            @Override
            public void onAccountNumberGenerated(String accountNumber) {
                if (accountNumber != null) {
                    // Save user details to the database
                    saveUserDetails(userID, fullName, dob, address, phoneNumber, emailAddress,
                            occupation, pancard, aadhar, balance, accountNumber);

                    // Account creation success
                    Toast.makeText(CreateAccountActivity.this, "Account created successfully", Toast.LENGTH_SHORT).show();
                } else {
                    // Handle error in account number generation
                    Toast.makeText(CreateAccountActivity.this, "Error generating account number", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void generateAccountNumber(OnAccountNumberGeneratedListener listener) {
        DatabaseReference countersRef = databaseReference.child("counters");
        DatabaseReference accountNumberCounterRef = countersRef.child("accountNumberCounter");

        accountNumberCounterRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                Long currentAccountNumber = mutableData.getValue(Long.class);

                // Check for null and initialize to a default value if it is
                if (currentAccountNumber == null) {
                    currentAccountNumber = 240300L;  // Initialize with a default value
                }

                mutableData.setValue(currentAccountNumber + 1);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@NonNull DatabaseError databaseError, boolean b, @NonNull DataSnapshot dataSnapshot) {
                if (databaseError == null) {
                    Long newAccountNumber = dataSnapshot.getValue(Long.class);

                    // Notify the listener with the new account number
                    listener.onAccountNumberGenerated(String.valueOf(newAccountNumber));
                } else {
                    listener.onAccountNumberGenerated(null);
                }
            }
        });
    }

    interface OnAccountNumberGeneratedListener {
        void onAccountNumberGenerated(String accountNumber);
    }


    private void saveUserDetails(String userID, String fullName, String dob, String address,
                                 String phoneNumber, String emailAddress, String occupation,
                                 String pancard, String aadhar, String balance, String accountNumber) {
        DatabaseReference userRef = databaseReference.child(userID);

        // Create a map to represent the user details
        Map<String, Object> userDetails = new HashMap<>();
        userDetails.put("fullName", fullName);
        userDetails.put("dob", dob);
        userDetails.put("address", address);
        userDetails.put("phoneNumber", phoneNumber);
        userDetails.put("emailAddress", emailAddress);
        userDetails.put("occupation", occupation);
        userDetails.put("pancard", pancard);
        userDetails.put("aadhar", aadhar);
        userDetails.put("balance", balance);
        userDetails.put("accountNumber", accountNumber);

        // Save the user details in the database
        userRef.setValue(userDetails);
    }

    interface OnIDGeneratedListener {
        void onIDsGenerated(String userID, String accountNumber);
    }
}
