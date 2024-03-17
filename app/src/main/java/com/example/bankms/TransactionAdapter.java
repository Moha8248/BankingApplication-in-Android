package  com.example.bankms;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private List<Transaction1> transactionList;
    private Context context;

    public TransactionAdapter(Context context, List<Transaction1> transactionList) {
        this.context = context;
        this.transactionList = transactionList;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_transaction_adapter, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction1 transaction = transactionList.get(position);
        holder.bind(transaction);
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public static class TransactionViewHolder extends RecyclerView.ViewHolder {

        TextView senderTextView;
        TextView recipientTextView;
        TextView amountTextView;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            senderTextView = itemView.findViewById(R.id.senderTextView);
            recipientTextView = itemView.findViewById(R.id.recipientTextView);
            amountTextView = itemView.findViewById(R.id.amountTextView);
        }

        public void bind(Transaction1 transaction) {
            senderTextView.setText("Sender: " + transaction.getSender());
            recipientTextView.setText("Recipient: " + transaction.getRecipient());
            amountTextView.setText("Amount: ₹" + transaction.getAmount());
        }
    }
}
