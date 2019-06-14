package suncor.com.android.ui.main.cards.details;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import java.util.ArrayList;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import suncor.com.android.databinding.FragmentCardsDetailsBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.model.cards.CardDetail;
import suncor.com.android.ui.main.common.BaseFragment;

public class CardsDetailsFragment extends BaseFragment {
    private FragmentCardsDetailsBinding binding;
    CardDetailsViewModel viewModel;
    private int clickedCardIndex;
    @Inject
    ViewModelFactory viewModelFactory;
    private float previousBrightness;
    private CardsDetailsAdapter cardsDetailsAdapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        clickedCardIndex = CardsDetailsFragmentArgs.fromBundle(getArguments()).getCardIndex();
        boolean loadCardFromProfile = CardsDetailsFragmentArgs.fromBundle(getArguments()).getIsCardFromProfile();
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(CardDetailsViewModel.class);
        viewModel.setCardFromProfile(loadCardFromProfile);
        viewModel.retrieveCards();
        viewModel.cards.observe(this, arrayListResource -> {
            ArrayList<ExpandedCardItem> expandedCardItems = new ArrayList<>();
            for (CardDetail cardDetail : arrayListResource) {
                expandedCardItems.add(new ExpandedCardItem(getContext(), cardDetail));
            }
            if (expandedCardItems.size() > 0) {
                cardsDetailsAdapter.setCardItems(expandedCardItems);
                cardsDetailsAdapter.notifyDataSetChanged();
                binding.cardDetailRecycler.scrollToPosition(clickedCardIndex);
                binding.setNumCards(expandedCardItems.size());
                binding.executePendingBindings();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCardsDetailsBinding.inflate(inflater, container, false);
        binding.cardDetailRecycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(binding.cardDetailRecycler);
        cardsDetailsAdapter = new CardsDetailsAdapter();
        binding.cardDetailRecycler.setAdapter(cardsDetailsAdapter);
        binding.pageIndicator.attachToRecyclerView(binding.cardDetailRecycler, pagerSnapHelper);
        cardsDetailsAdapter.registerAdapterDataObserver(binding.pageIndicator.getAdapterDataObserver());
        binding.buttonClose.setOnClickListener(v -> Navigation.findNavController(getView()).popBackStack());
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        WindowManager.LayoutParams attributes = getActivity().getWindow().getAttributes();
        previousBrightness = attributes.screenBrightness;
        attributes.screenBrightness = 1f;
        getActivity().getWindow().setAttributes(attributes);
    }

    @Override
    public void onStop() {
        super.onStop();
        WindowManager.LayoutParams attributes = getActivity().getWindow().getAttributes();
        attributes.screenBrightness = previousBrightness;
        getActivity().getWindow().setAttributes(attributes);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }
}
