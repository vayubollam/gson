package suncor.com.android.ui.main.wallet.payments.list;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import suncor.com.android.R;
import suncor.com.android.databinding.PaymentSmallCardItemBinding;
import suncor.com.android.databinding.PetroCanadaSmallCardItemBinding;
import suncor.com.android.model.payments.PaymentDetail;
import suncor.com.android.utilities.Consumer;

public class PaymentsListAdapter extends RecyclerView.Adapter<PaymentsListAdapter.ViewHolder> {

    private Context context;
    private ArrayList<PaymentListItem> payments = new ArrayList<>();
    private Consumer<PaymentDetail> callback;

    PaymentsListAdapter(Consumer<PaymentDetail> callback) {
        this.callback = callback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PaymentSmallCardItemBinding binding = PaymentSmallCardItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.setItem(payments.get(position));
        View view = holder.binding.getRoot();
        int color;
        if (position % 2 == 0) {
            color = Color.parseColor("#FFAB252C");
        } else {
            color = Color.parseColor("#FF6D6E6F");
        }
        view.setBackgroundTintList(ColorStateList.valueOf(color));

        if (position == payments.size() - 1) {
            view.getLayoutParams().height = view.getResources().getDimensionPixelSize(R.dimen.petro_canada_cards_last_height);
            view.setPadding(0, 0, 0, 0);
        } else {
            view.getLayoutParams().height = view.getResources().getDimensionPixelSize(R.dimen.petro_canada_cards_height);
            view.setPadding(0, 0, 0, view.getResources().getDimensionPixelSize(R.dimen.petro_canada_cards_padding));
        }

        view.setOnClickListener(v -> callback.accept(payments.get(position).getPaymentDetail()));

        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return payments.size();
    }

    public void setPayments(List<PaymentListItem> payments) {
        this.payments.clear();
        this.payments.addAll(payments);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        PaymentSmallCardItemBinding binding;

        ViewHolder(PaymentSmallCardItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
