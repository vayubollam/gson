package suncor.com.android.ui.main.carwash;

import android.graphics.Outline;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import java.util.ArrayList;

import javax.inject.Inject;

import suncor.com.android.R;
import suncor.com.android.databinding.FragmentCarWashBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.model.cards.CardDetail;
import suncor.com.android.ui.common.GenericErrorView;
import suncor.com.android.ui.common.OnBackPressedListener;
import suncor.com.android.ui.main.cards.list.CardItemDecorator;
import suncor.com.android.ui.main.cards.list.CardListItem;
import suncor.com.android.ui.main.cards.list.CardsListAdapter;
import suncor.com.android.ui.main.common.MainActivityFragment;

public class CarWashCardFragment extends MainActivityFragment implements OnBackPressedListener {

    @Inject
    ViewModelFactory viewModelFactory;
    private FragmentCarWashBinding binding;
    private CarWashCardViewModel viewModel;
    private CardsListAdapter petroCanadaCardsAdapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //appBarElevation = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(CarWashCardViewModel.class);
        petroCanadaCardsAdapter = new CardsListAdapter(this::cardClick);

        viewModel.getViewState().observe(this, (result) -> {
            if (result != CarWashCardViewModel.ViewState.LOADING && result != CarWashCardViewModel.ViewState.FAILED
                    && viewModel.getIsCardAvailable().getValue()) {

                ArrayList<CardListItem> petroCanadaCards = new ArrayList<>();
                for (CardDetail cardDetail : viewModel.getPetroCanadaCards().getValue()) {
                    petroCanadaCards.add(new CardListItem(getContext(), cardDetail));
                }
                petroCanadaCardsAdapter.setCards(petroCanadaCards);
            }
        });

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCarWashBinding.inflate(inflater, container, false);
        binding.setVm(viewModel);
        binding.setLifecycleOwner(this);
        binding.errorLayout.setModel(new GenericErrorView(getContext(), R.string.msg_sl005_button, () -> viewModel.loadData()));

        binding.appBar.setNavigationOnClickListener(v -> goBack());

        CardItemDecorator listDecorator = new CardItemDecorator(-getResources().getDimensionPixelSize(R.dimen.petro_canada_cards_padding));

        binding.carWashCardsList.setAdapter(petroCanadaCardsAdapter);
        binding.carWashCardsList.addItemDecoration(listDecorator);
        binding.carWashCardsList.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                Drawable drawable = getActivity().getDrawable(R.drawable.petro_canada_card_background);
                drawable.setBounds(new Rect(0, 0, view.getWidth(), view.getHeight()));
                drawable.getOutline(outline);
            }
        });

        return binding.getRoot();

    }

    @Override
    public void onStart() {
        super.onStart();
        viewModel.onAttached();
    }


    @Override
    public void onBackPressed() {
        goBack();
    }

    private void goBack() {
        Navigation.findNavController(getView()).popBackStack();
    }


    private void cardClick(CardDetail cardDetail) {
        if (viewModel.getViewState().getValue() == CarWashCardViewModel.ViewState.FAILED) {
            //the card was loaded from profile, so the repository is still empty
//            CardsFragmentDirections.ActionCardsTabToCardsDetailsFragment action = CardsFragmentDirections.actionCardsTabToCardsDetailsFragment();
//            action.setIsCardFromProfile(true);
//            Navigation.findNavController(getView()).navigate(action);
        } else {
//            CardsFragmentDirections.ActionCardsTabToCardsDetailsFragment action = CardsFragmentDirections.actionCardsTabToCardsDetailsFragment();
//            action.setCardIndex(viewModel.getIndexofCardDetail(cardDetail));
//            Navigation.findNavController(getView()).navigate(action);
        }
    }


}
