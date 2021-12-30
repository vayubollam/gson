package suncor.com.android.ui.main.wallet.cards.list;

import android.content.Context;
import android.graphics.Outline;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import java.util.ArrayList;

import javax.inject.Inject;

import suncor.com.android.R;
import suncor.com.android.databinding.FragmentCardsBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.model.cards.CardDetail;
import suncor.com.android.model.cards.CardType;
import suncor.com.android.ui.common.SuncorToast;
import suncor.com.android.ui.main.MainViewModel;
import suncor.com.android.ui.main.common.MainActivityFragment;
import suncor.com.android.ui.main.wallet.WalletFragment;
import suncor.com.android.ui.main.wallet.WalletTabInterface;
import suncor.com.android.ui.main.wallet.cards.CardsLoadType;
import suncor.com.android.uicomponents.swiperefreshlayout.SwipeRefreshLayout;
import suncor.com.android.utilities.AnalyticsUtils;
import suncor.com.android.utilities.CardsUtil;

public class CardsFragment extends MainActivityFragment implements SwipeRefreshLayout.OnRefreshListener, WalletTabInterface {

    @Inject
    ViewModelFactory viewModelFactory;
    private FragmentCardsBinding binding;
    private CardsViewModel viewModel;
    private MainViewModel mainViewModel;
    private CardsListAdapter petroCanadaCardsAdapter;
    private CardsListAdapter partnerCardsAdapter;


    @Override
    public String getTabName(Context context) {
        return context.getString(R.string.wallet_card_tab);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainViewModel = ViewModelProviders.of(getActivity(), viewModelFactory).get(MainViewModel.class);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(CardsViewModel.class);
        petroCanadaCardsAdapter = new CardsListAdapter(this::cardClick);
        partnerCardsAdapter = new CardsListAdapter(this::cardClick);

        viewModel.viewState.observe(this, (result) -> {
            if (result != CardsViewModel.ViewState.REFRESHING) {
                Fragment parent = getParentFragment();
                if (parent instanceof WalletFragment) {
                    ((WalletFragment) parent).stopRefresh();
                }
            }

            if (result != CardsViewModel.ViewState.REFRESHING && result != CardsViewModel.ViewState.LOADING) {

                binding.setPptsCard(new PetroPointsCard(getContext(), viewModel.getPetroPointsCard().getValue()));

                if (result != CardsViewModel.ViewState.FAILED) {
                    if (mainViewModel.isLinkedToAccount()) {
                        AnalyticsUtils.setCurrentScreenName(getActivity(), mainViewModel.getNewAddedCard().getFirebaseScreenName());
                        CardsFragmentDirections.ActionCardsTabToCardsDetailsFragment action = CardsFragmentDirections.actionCardsTabToCardsDetailsFragment();
                        action.setLoadType(CardsLoadType.REDEEMED_SINGLE_TICKETS);
                        Navigation.findNavController(getView()).navigate(action);
                    } else if (mainViewModel.isNewCardAdded()) {
                        AnalyticsUtils.setCurrentScreenName(getActivity(), mainViewModel.getNewAddedCard().getFirebaseScreenName());
                        CardsFragmentDirections.ActionCardsTabToCardsDetailsFragment action = CardsFragmentDirections.actionCardsTabToCardsDetailsFragment();
                        action.setLoadType(CardsLoadType.NEWLY_ADD_CARD);
                        Navigation.findNavController(getView()).navigate(action);
                    } else {
                        ArrayList<CardListItem> petroCanadaCards = new ArrayList<>();
                        for (CardDetail cardDetail : viewModel.getPetroCanadaCards().getValue()) {
                            petroCanadaCards.add(new CardListItem(getContext(), cardDetail));
                        }
                        petroCanadaCardsAdapter.setCards(petroCanadaCards);

                        ArrayList<CardListItem> partnerCards = new ArrayList<>();
                        for (CardDetail cardDetail : viewModel.getPartnerCards().getValue()) {
                            partnerCards.add(new CardListItem(getContext(), cardDetail));
                        }
                        partnerCardsAdapter.setCards(partnerCards);
                    }
                }

                if (result == CardsViewModel.ViewState.BALANCE_FAILED) {
                    SuncorToast.makeText(getContext(), R.string.msg_cm003, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void cardClick(CardDetail cardDetail) {
        if (viewModel.viewState.getValue() == CardsViewModel.ViewState.FAILED) {
            //the card was loaded from profile, so the repository is still empty
            if(viewModel.getPetroPointsCard().getValue() != null) {
                AnalyticsUtils.setCurrentScreenName(getActivity(), viewModel.getPetroPointsCard().getValue().getFirebaseScreenName());
            }
            CardsFragmentDirections.ActionCardsTabToCardsDetailsFragment action = CardsFragmentDirections.actionCardsTabToCardsDetailsFragment();
            action.setLoadType(CardsLoadType.PETRO_POINT_ONLY);
            Navigation.findNavController(getView()).navigate(action);
        } else {
            if(cardDetail == null)
                return;
            if (viewModel.getIsBalanceZero().getValue() != null &&
                    viewModel.getIsBalanceZero().getValue() && (cardDetail.getCardType() == CardType.SP || cardDetail.getCardType() == CardType.WAG)) {
                CardsUtil.showZeroBalanceAlert(getContext(),
                        (dialog, v) -> Navigation.findNavController(getView()).navigate(R.id.action_cards_tab_to_carWashPurchaseFragment),
                        (dialog, v) -> navigateToCardDetail(cardDetail));
            } else if (cardDetail.getBalance() <= 0 && (cardDetail.getCardType() == CardType.SP || cardDetail.getCardType() == CardType.WAG)) {
                CardsUtil.showOtherCardAvailableAlert(getContext());
            } else {
                navigateToCardDetail(cardDetail);

            }
        }
    }

    private void navigateToCardDetail(CardDetail cardDetail) {
        if (getView() == null) return;

        AnalyticsUtils.setCurrentScreenName(getActivity(), cardDetail.getFirebaseScreenName());
        CardsFragmentDirections.ActionCardsTabToCardsDetailsFragment action = CardsFragmentDirections.actionCardsTabToCardsDetailsFragment();
        action.setCardIndex(viewModel.getIndexofCardDetail(cardDetail));

        NavController controller = Navigation.findNavController(getView());
        if (controller.getCurrentDestination() != null
                && controller.getCurrentDestination().getAction(action.getActionId()) != null ) {
            controller.navigate(action);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCardsBinding.inflate(inflater, container, false);
        binding.setVm(viewModel);
        binding.setLifecycleOwner(this);
        binding.errorLayout.setModel(new CardsErrorView(getContext(), () -> viewModel.retryAgain()));
        binding.cardsLayout.post(() -> {
            //give the cards layout a minimum height, to anchor the date to the bottom screen
            //binding.cardsLayout.setMinHeight(binding.scrollView.getHeight() - binding.appBar.getHeight());
        });

        CardItemDecorator listDecorator = new CardItemDecorator(-getResources().getDimensionPixelSize(R.dimen.petro_canada_cards_padding));

        binding.petroCanadaCardsList.setAdapter(petroCanadaCardsAdapter);
        binding.petroCanadaCardsList.addItemDecoration(listDecorator);
        binding.petroCanadaCardsList.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                Drawable drawable = getActivity().getDrawable(R.drawable.petro_canada_card_background);
                drawable.setBounds(new Rect(0, 0, view.getWidth(), view.getHeight()));
                drawable.getOutline(outline);
            }
        });

        binding.partnerCardsList.setAdapter(partnerCardsAdapter);
        binding.partnerCardsList.addItemDecoration(listDecorator);

        binding.addCardButton.setOnClickListener((v) -> navigateToAddCard());

        binding.petroPointsCard.setOnClickListener((v) -> cardClick(viewModel.getPetroPointsCard().getValue()));

        return binding.getRoot();
    }

    @Override
    public void navigateToAddCard() {
        if (getView() == null) return;

        NavController controller = Navigation.findNavController(getView());
        if (controller.getCurrentDestination() != null
                && controller.getCurrentDestination().getAction(R.id.action_cards_tab_to_addCardFragment) != null ) {
            controller.navigate(R.id.action_cards_tab_to_addCardFragment);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        viewModel.onAttached();
    }

    @Override
    public void onRefresh() {
        if (viewModel != null)
            viewModel.refreshBalance();
    }

    @Override
    public void onResume() {
        super.onResume();
        AnalyticsUtils.setCurrentScreenName(getActivity(), "my-petro-points-credit-card-wallet-list");
    }
}
