package suncor.com.android.ui.home.cards;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import suncor.com.android.R;
import suncor.com.android.databinding.PetroCanadaCardItemBinding;
import suncor.com.android.model.cards.CardDetail;
import suncor.com.android.utilities.Timber;

public class CardsListAdapter extends RecyclerView.Adapter<CardsListAdapter.PetroCanadaViewHolder> {

    private Context context;
    private ArrayList<CardItem> cards = new ArrayList<>();
    private View.OnTouchListener cardsTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            if (action == MotionEvent.ACTION_DOWN) {
                v.animate().scaleX(0.97f).start();
                v.animate().scaleY(0.97f).start();
            } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                v.animate().cancel();
                v.animate().scaleX(1f).start();
                v.animate().scaleY(1f).start();
            }

            return v.onTouchEvent(event);
        }
    };

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
        view.setBackgroundTintList(ColorStateList.valueOf(color));

        if (position == cards.size() - 1) {
            view.getLayoutParams().height = view.getResources().getDimensionPixelSize(R.dimen.petro_canada_cards_last_height);
            view.setPadding(0, 0, 0, 0);
        } else {
            view.getLayoutParams().height = view.getResources().getDimensionPixelSize(R.dimen.petro_canada_cards_height);
            view.setPadding(0, 0, 0, view.getResources().getDimensionPixelSize(R.dimen.petro_canada_cards_padding));
        }

        if (cards.get(position).getCardCategory() == CardDetail.CardCategory.PETRO_CANADA) {
            view.setOnTouchListener(cardsTouchListener);
        }

        view.setOnClickListener((v) -> {
            Timber.d("Click still working");
        });

        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    public void setCards(List<CardItem> cards) {
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
