package suncor.com.android.ui.home.cards;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import suncor.com.android.R;
import suncor.com.android.databinding.PetroCanadaCardItemBinding;

public class PetroCanadaCardsAdapter extends RecyclerView.Adapter<PetroCanadaCardsAdapter.PetroCanadaViewHolder> {

    private ArrayList<CardItem> cards = new ArrayList<>();

    @NonNull
    @Override
    public PetroCanadaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PetroCanadaCardItemBinding binding = PetroCanadaCardItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
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
        GradientDrawable drawable = (GradientDrawable) holder.binding.getRoot().getResources().getDrawable(R.drawable.petro_canada_card_background);
        drawable.setColor(color);
        view.setBackground(drawable);

        if (position == cards.size() - 1) {
            view.getLayoutParams().height = view.getResources().getDimensionPixelSize(R.dimen.petro_canada_cards_last_height);
            view.setPadding(0, 0, 0, 0);
        } else {
            view.getLayoutParams().height = view.getResources().getDimensionPixelSize(R.dimen.petro_canada_cards_height);
            view.setPadding(0, 0, 0, view.getResources().getDimensionPixelSize(R.dimen.petro_canada_cards_padding));
        }

        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    public void setCards(ArrayList<CardItem> cards) {
        this.cards.clear();
        this.cards.addAll(cards);
        notifyDataSetChanged();
    }

    class PetroCanadaViewHolder extends RecyclerView.ViewHolder {
        PetroCanadaCardItemBinding binding;

        public PetroCanadaViewHolder(PetroCanadaCardItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
