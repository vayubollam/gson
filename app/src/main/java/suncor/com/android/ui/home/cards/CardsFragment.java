package suncor.com.android.ui.home.cards;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;
import dagger.android.support.DaggerFragment;
import suncor.com.android.R;
import suncor.com.android.databinding.FragmentCardsBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;

public class CardsFragment extends DaggerFragment {

    private FragmentCardsBinding binding;
    private CardsViewModel viewModel;

    @Inject
    ViewModelFactory viewModelFactory;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(CardsViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCardsBinding.inflate(inflater, container, false);


        PetroCanadaCardsAdapter adapter = new PetroCanadaCardsAdapter();
        binding.petroCanadaCardsList.setAdapter(adapter);
        ItemDecorator decorator = new ItemDecorator(-getResources().getDimensionPixelSize(R.dimen.petro_canada_cards_padding));
        binding.petroCanadaCardsList.addItemDecoration(decorator);


        ArrayList<CardItem> cards = new ArrayList<>();
        cards.add(new CardItem("Fuel Savings Reward", "100 Litres"));
        cards.add(new CardItem("Fuel Savings Reward", "100 Litres"));
        cards.add(new CardItem("Fuel Savings Reward", "100 Litres"));
        cards.add(new CardItem("Fuel Savings Reward", "100 Litres"));
        cards.add(new CardItem("Fuel Savings Reward", "100 Litres"));
        adapter.setCards(cards);

        return binding.getRoot();
    }

    private class ItemDecorator extends RecyclerView.ItemDecoration {
        private final int mSpace;

        public ItemDecorator(int space) {
            this.mSpace = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            if (position != 0)
                outRect.top = mSpace;
        }
    }
}
