package suncor.com.android.ui.main.cards.details;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import suncor.com.android.R;
import suncor.com.android.databinding.PetroCanadaExpandedCardItemBinding;

public class CardsDetailsAdapter extends RecyclerView.Adapter<CardsDetailsAdapter.CardsDetailHolder> {
    ArrayList<ExpandedCardItem> cardItems = new ArrayList<>();


    @NonNull
    @Override
    public CardsDetailHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PetroCanadaExpandedCardItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.petro_canada_expanded_card_item, parent, false);
        return new CardsDetailHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CardsDetailHolder holder, int position) {
        holder.binding.setCard(cardItems.get(position));
    }

    @Override
    public int getItemCount() {
        return cardItems.size();
    }

    public ArrayList<ExpandedCardItem> getCardItems() {
        return cardItems;
    }

    public void setCardItems(ArrayList<ExpandedCardItem> cardItems) {
        this.cardItems = cardItems;
    }

    public class CardsDetailHolder extends RecyclerView.ViewHolder {
        PetroCanadaExpandedCardItemBinding binding;

        public CardsDetailHolder(@NonNull PetroCanadaExpandedCardItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
