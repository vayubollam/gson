package suncor.com.android.ui.home.cards;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import javax.inject.Inject;

import suncor.com.android.R;
import suncor.com.android.databinding.FragmentCardsDetailsBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.model.Resource;
import suncor.com.android.model.cards.CardDetail;
import suncor.com.android.ui.home.common.BaseFragment;

public class CardsDetailsFragment extends BaseFragment {
    private FragmentCardsDetailsBinding binding;
    CardDetailsViewModel viewModel;
    private int clickedCardIndex;
    @Inject
    ViewModelFactory viewModelFactory;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCardsDetailsBinding.inflate(inflater, container, false);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(CardDetailsViewModel.class);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        clickedCardIndex = CardsDetailsFragmentArgs.fromBundle(getArguments()).getClickedCardIndex();
    }

    @Override
    protected int getStatusBarColor() {
        return getResources().getColor(R.color.black_4);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        binding.cardDetailRecycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(binding.cardDetailRecycler);
        CardsDetailsAdapter cardsDetailsAdapter = new CardsDetailsAdapter();
        binding.cardDetailRecycler.setAdapter(cardsDetailsAdapter);
        binding.pageIndicator.attachToRecyclerView(binding.cardDetailRecycler, pagerSnapHelper);
        cardsDetailsAdapter.registerAdapterDataObserver(binding.pageIndicator.getAdapterDataObserver());

        viewModel.cards.observe(this, arrayListResource -> {
            if (arrayListResource.status == Resource.Status.SUCCESS) {
                ArrayList<ExpandedCardItem> expandedCardItems = new ArrayList<>();
                for (CardDetail cardDetail : arrayListResource.data) {
                    expandedCardItems.add(new ExpandedCardItem(getContext(), cardDetail));
                }
                if (expandedCardItems.size() > 0) {
                    cardsDetailsAdapter.setCardItems(expandedCardItems);
                    cardsDetailsAdapter.notifyDataSetChanged();
                    binding.cardDetailRecycler.scrollToPosition(clickedCardIndex);
                    binding.setNumCards(expandedCardItems.size());
                    binding.executePendingBindings();
                }

            }
        });
        binding.buttonClose.setOnClickListener(v -> Navigation.findNavController(getView()).popBackStack());

    }
}
