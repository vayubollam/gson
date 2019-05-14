package suncor.com.android.ui.home.cards;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import suncor.com.android.data.repository.cards.CardsApiMock;
import suncor.com.android.databinding.FragmentCardsDetailsBinding;
import suncor.com.android.model.Resource;
import suncor.com.android.ui.home.common.BaseFragment;

public class CardsDetailsFragment extends BaseFragment {

    private AppCompatImageView barCodeImage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentCardsDetailsBinding binding = FragmentCardsDetailsBinding.inflate(inflater, container, false);
        CardsApiMock mock = new CardsApiMock();
        mock.retrieveCards().observe(this, result -> {
            if (result.status == Resource.Status.SUCCESS) {
                binding.card.setCard(new ExpandedCardItem(getContext(), result.data.get(8)));
                binding.card.executePendingBindings();
            }
        });
        return binding.getRoot();
    }
}
