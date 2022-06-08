package suncor.com.android.ui.main.wallet.cards.details;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableBoolean;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import suncor.com.android.R;
import suncor.com.android.databinding.PetroCanadaExpandedCardItemBinding;
import suncor.com.android.utilities.Consumer;

public class CardsDetailsAdapter extends RecyclerView.Adapter<CardsDetailsAdapter.CardsDetailHolder> {
    private ArrayList<ExpandedCardItem> cardItems = new ArrayList<>();
    private Consumer<ExpandedCardItem> callBack;
    private View.OnClickListener activeWashListener;

    private View.OnClickListener cardReloadListener;
    private View.OnClickListener gpaySaveToWalletListener;
    private View.OnClickListener vacuumListener;
    private ObservableBoolean isVacuumEnabled = new ObservableBoolean(false);


    public CardsDetailsAdapter(Consumer<ExpandedCardItem> callBack, View.OnClickListener activeWashListener,
                               View.OnClickListener cardReloadListener, View.OnClickListener gpaySaveToWalletListener,
                               View.OnClickListener vacuumListener, boolean isVacuumEnabled) {
        this.callBack = callBack;
        this.activeWashListener = activeWashListener;
        this.cardReloadListener = cardReloadListener;
        this.gpaySaveToWalletListener = gpaySaveToWalletListener;
        this.vacuumListener = vacuumListener;
        this.isVacuumEnabled.set(isVacuumEnabled);
    }


    @NonNull
    @Override
    public CardsDetailHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PetroCanadaExpandedCardItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.petro_canada_expanded_card_item, parent, false);
        return new CardsDetailHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CardsDetailHolder holder, int position) {
        holder.binding.setCard(cardItems.get(position));
        holder.binding.setIsVacuumEnable(isVacuumEnabled);
        holder.binding.moreButton.setOnClickListener(v -> callBack.accept(cardItems.get(position)));
        if(holder.binding.cardReloadButton.isShown()) {
            holder.binding.cardReloadButton.setOnClickListener(cardReloadListener);
        }
        holder.binding.savetowallet.setOnClickListener(gpaySaveToWalletListener);
        holder.binding.vacuumButton.setOnClickListener(vacuumListener);
    }

    public void removeCard(ExpandedCardItem expandedCardItem) {
        int position = cardItems.indexOf(expandedCardItem);
        cardItems.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, cardItems.size());
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

    public void updateVacuumToggle(boolean vacuumToggle){
      isVacuumEnabled.set(vacuumToggle);
    }

    public void updateCardItems(ExpandedCardItem card, final int pos) {
        cardItems.remove(pos);
        cardItems.add(pos, card);
        notifyItemChanged(pos);
        notifyDataSetChanged();
    }

    public class CardsDetailHolder extends RecyclerView.ViewHolder {
        PetroCanadaExpandedCardItemBinding binding;

        public CardsDetailHolder(@NonNull PetroCanadaExpandedCardItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.activeWashButton.setOnClickListener(activeWashListener);
        }
    }
}
