package suncor.com.android.ui.home.cards;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import suncor.com.android.R;
import suncor.com.android.databinding.PetroCanadaSmallCardItemBinding;
import suncor.com.android.model.cards.CardDetail;
import suncor.com.android.utilities.Consumer;
import suncor.com.android.utilities.Timber;

public class CardsListAdapter extends RecyclerView.Adapter<CardsListAdapter.PetroCanadaViewHolder> {

    private Context context;
    private ArrayList<CardItem> cards = new ArrayList<>();
    Consumer<CardDetail> callback;

    public CardsListAdapter(Consumer<CardDetail> callback) {
        this.callback = callback;
    }

    private View.OnTouchListener cardsTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            if (action == MotionEvent.ACTION_DOWN) {
                v.animate().scaleX(0.97f).scaleY(0.97f).setDuration(100).start();
            } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                v.animate().cancel();
                v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
            }

            return v.onTouchEvent(event);
        }
    };

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

        if (cards.get(position).getCardCategory() == CardDetail.CardCategory.PETRO_CANADA) {
            view.setOnTouchListener(cardsTouchListener);
        }

        view.setOnClickListener((v) -> {
            Timber.d("Click still working");
        });

        holder.binding.executePendingBindings();
        holder.binding.getRoot().setOnClickListener(v -> callback.accept(cards.get(position).getCardDetail()));
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
        PetroCanadaSmallCardItemBinding binding;

        public PetroCanadaViewHolder(PetroCanadaSmallCardItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
