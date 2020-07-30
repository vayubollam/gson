package suncor.com.android.ui.main.pap.selectpump;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import suncor.com.android.databinding.PaymentExpandedCardItemBinding;
import suncor.com.android.databinding.SelectPumpCardItemBinding;
import suncor.com.android.ui.main.wallet.payments.details.ExpandedPaymentItem;
import suncor.com.android.utilities.Consumer;

public class SelectPumpAdapter extends RecyclerView.Adapter<SelectPumpAdapter.CardsDetailHolder> {
    private ArrayList<String> pumpNumbers = new ArrayList<>();
    private int selectedPos = RecyclerView.NO_POSITION;
    private Consumer<String> callBack;

    SelectPumpAdapter() {

    }

    @NonNull
    @Override
    public CardsDetailHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SelectPumpCardItemBinding binding = SelectPumpCardItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CardsDetailHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CardsDetailHolder holder, int position) {
        holder.binding.setPumpNumber(pumpNumbers.get(position));
        holder.binding.cardView.setSelected(selectedPos == position);
        holder.binding.pumpNumberSelected.setVisibility(selectedPos == position ? View.VISIBLE : View.INVISIBLE);

        holder.binding.cardView.setOnClickListener(v -> {
            notifyItemChanged(selectedPos);
            selectedPos = position;
            notifyItemChanged(selectedPos);
        });
    }

    @Override
    public int getItemCount() {
        return pumpNumbers.size();
    }

    void setPumpNumbers(ArrayList<String> pumpNumbers) {
        this.pumpNumbers = pumpNumbers;
    }

    static class CardsDetailHolder extends RecyclerView.ViewHolder {
        SelectPumpCardItemBinding binding;

        CardsDetailHolder(@NonNull SelectPumpCardItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
