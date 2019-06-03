package suncor.com.android.ui.main.cards.list;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import suncor.com.android.R;
import suncor.com.android.databinding.PetroCanadaSmallCardItemBinding;
import suncor.com.android.model.cards.CardDetail;
import suncor.com.android.utilities.Consumer;

public class CardsListAdapter extends RecyclerView.Adapter<CardsListAdapter.PetroCanadaViewHolder> {

    private Context context;
    private ArrayList<CardListItem> cards = new ArrayList<>();
    Consumer<CardDetail> callback;

    public CardsListAdapter(Consumer<CardDetail> callback) {
        this.callback = callback;
    }

    @NonNull
    @Override
    public PetroCanadaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PetroCanadaSmallCardItemBinding binding = PetroCanadaSmallCardItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new PetroCanadaViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PetroCanadaViewHolder holder, int position) {
        holder.binding.setItem(cards.get(position));
        View view = holder.binding.getRoot();
        int color;
        if (position % 2 == 0) {
            color = Color.parseColor("#FFAB252C");
        } else {
            color = Color.parseColor("#FF6D6E6F");
        }
        view.setBackgroundTintList(ColorStateList.valueOf(color));

        if (position == cards.size() - 1) {
            view.getLayoutParams().height = view.getResources().getDimensionPixelSize(R.dimen.petro_canada_cards_last_height);
            view.setPadding(0, 0, 0, 0);
        } else {
            view.getLayoutParams().height = view.getResources().getDimensionPixelSize(R.dimen.petro_canada_cards_height);
            view.setPadding(0, 0, 0, view.getResources().getDimensionPixelSize(R.dimen.petro_canada_cards_padding));
        }

        view.setOnClickListener(v -> callback.accept(cards.get(position).getCardDetail()));

        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    public void setCards(List<CardListItem> cards) {
        this.cards.clear();
        this.cards.addAll(cards);
        notifyDataSetChanged();
    }

    class PetroCanadaViewHolder extends RecyclerView.ViewHolder {
        PetroCanadaSmallCardItemBinding binding;

        public PetroCanadaViewHolder(PetroCanadaSmallCardItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
