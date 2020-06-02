package suncor.com.android.ui.main.wallet.payments.details;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import suncor.com.android.databinding.PaymentExpandedCardItemBinding;
import suncor.com.android.utilities.Consumer;

public class PaymentsDetailsAdapter extends RecyclerView.Adapter<PaymentsDetailsAdapter.CardsDetailHolder> {
    private ArrayList<ExpandedPaymentItem> cardItems = new ArrayList<>();
    private Consumer<ExpandedPaymentItem> callBack;

    PaymentsDetailsAdapter(Consumer<ExpandedPaymentItem> callBack) {
        this.callBack = callBack;
    }

    @NonNull
    @Override
    public CardsDetailHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PaymentExpandedCardItemBinding binding = PaymentExpandedCardItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CardsDetailHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CardsDetailHolder holder, int position) {
        holder.binding.setCard(cardItems.get(position));
        holder.binding.moreButton.setOnClickListener(v -> callBack.accept(cardItems.get(position)));
    }

    public void removeCard(ExpandedPaymentItem expandedCardItem) {
        int position = cardItems.indexOf(expandedCardItem);
        cardItems.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, cardItems.size());
    }

    @Override
    public int getItemCount() {
        return cardItems.size();
    }

    public ArrayList<ExpandedPaymentItem> getCardItems() {
        return cardItems;
    }

    void setCardItems(ArrayList<ExpandedPaymentItem> cardItems) {
        this.cardItems = cardItems;
    }

    static class CardsDetailHolder extends RecyclerView.ViewHolder {
        PaymentExpandedCardItemBinding binding;

        CardsDetailHolder(@NonNull PaymentExpandedCardItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
