package com.example.bankms;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private Context context;
    private List<User> userList;

    public UserAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_details_item, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);

        holder.fullNameTextView.setText(user.getFullName());
        holder.dobTextView.setText(user.getDob());
        holder.addressTextView.setText(user.getAddress());
        holder.phoneNumberTextView.setText(user.getPhoneNumber());
        holder.emailAddressTextView.setText(user.getEmailAddress());
        holder.occupationTextView.setText(user.getOccupation());
        holder.pancardTextView.setText(user.getPancard());
        holder.aadharTextView.setText(user.getAadhar());
        holder.balanceTextView.setText(user.getBalance());
        holder.accountNumberTextView.setText(user.getAccountNumber());
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {

        TextView fullNameTextView, dobTextView, addressTextView, phoneNumberTextView,
                emailAddressTextView, occupationTextView, pancardTextView,
                aadharTextView, balanceTextView, accountNumberTextView;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            fullNameTextView = itemView.findViewById(R.id.fullNameTextView);
            dobTextView = itemView.findViewById(R.id.dobTextView);
            addressTextView = itemView.findViewById(R.id.addressTextView);
            phoneNumberTextView = itemView.findViewById(R.id.phoneNumberTextView);
            emailAddressTextView = itemView.findViewById(R.id.emailAddressTextView);
            occupationTextView = itemView.findViewById(R.id.occupationTextView);
            pancardTextView = itemView.findViewById(R.id.pancardTextView);
            aadharTextView = itemView.findViewById(R.id.aadharTextView);
            balanceTextView = itemView.findViewById(R.id.balanceTextView);
            accountNumberTextView = itemView.findViewById(R.id.accountNumberTextView);
        }
    }
}
