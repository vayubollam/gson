package suncor.com.android.ui.main.profile.transcations;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import suncor.com.android.R;
import suncor.com.android.databinding.TransactionItemBinding;
import suncor.com.android.model.cards.Transaction;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {
    private final ArrayList<Transaction> transactions;

    public TransactionAdapter(ArrayList<Transaction> transactions) {
        this.transactions = transactions;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TransactionViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.transaction_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        holder.transactionItemBinding.setTransaction(transactions.get(position));

    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public class TransactionViewHolder extends RecyclerView.ViewHolder {
        final TransactionItemBinding transactionItemBinding;

        TransactionViewHolder(@NonNull TransactionItemBinding transactionItemBinding) {
            super(transactionItemBinding.getRoot());
            this.transactionItemBinding = transactionItemBinding;
        }
    }
}
