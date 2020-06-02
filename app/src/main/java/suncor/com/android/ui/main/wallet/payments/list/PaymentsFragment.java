package suncor.com.android.ui.main.wallet.payments.list;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import java.util.ArrayList;

import javax.inject.Inject;

import suncor.com.android.R;
import suncor.com.android.databinding.FragmentPaymentsBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.model.payments.PaymentDetail;
import suncor.com.android.ui.main.MainViewModel;
import suncor.com.android.ui.main.common.MainActivityFragment;
import suncor.com.android.ui.main.wallet.WalletFragment;
import suncor.com.android.ui.main.wallet.WalletFragmentDirections;
import suncor.com.android.ui.main.wallet.cards.CardsLoadType;
import suncor.com.android.ui.main.wallet.cards.list.CardItemDecorator;
import suncor.com.android.ui.main.wallet.cards.list.CardsErrorView;
import suncor.com.android.ui.main.wallet.cards.list.CardsFragmentDirections;
import suncor.com.android.uicomponents.swiperefreshlayout.SwipeRefreshLayout;
import suncor.com.android.utilities.AnalyticsUtils;

public class PaymentsFragment extends MainActivityFragment implements SwipeRefreshLayout.OnRefreshListener {

    @Inject
    ViewModelFactory viewModelFactory;
    private FragmentPaymentsBinding binding;
    private PaymentsViewModel viewModel;
    private MainViewModel mainViewModel;
    private PaymentsListAdapter adapter;
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainViewModel = ViewModelProviders.of(getActivity(), viewModelFactory).get(MainViewModel.class);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(PaymentsViewModel.class);
        adapter = new PaymentsListAdapter(this::cardClick);

        viewModel.viewState.observe(this, (result) -> {
            if (result != PaymentsViewModel.ViewState.REFRESHING) {
                Fragment parent = getParentFragment();
                if (parent instanceof WalletFragment) {
                    ((WalletFragment) parent).stopRefresh();
                }
            }

            if (result != PaymentsViewModel.ViewState.REFRESHING && result != PaymentsViewModel.ViewState.LOADING) {

                if (result != PaymentsViewModel.ViewState.FAILED) {
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
                        ArrayList<PaymentListItem> payments = new ArrayList<>();
                        for (PaymentDetail paymentDetail : viewModel.getPayments().getValue()) {
                            payments.add(new PaymentListItem(getContext(), paymentDetail));
                        }
                        adapter.setPayments(payments);
                    }
                }
            }
        });
    }

    private void cardClick(PaymentDetail paymentDetail) {
        navigateToPaymentDetail(paymentDetail);
    }

    private void navigateToPaymentDetail(PaymentDetail paymentDetail) {
        AnalyticsUtils.setCurrentScreenName(getActivity(), paymentDetail.getFirebaseScreenName());
        WalletFragmentDirections.ActionPaymentsTabToPaymentsDetailsFragment action = WalletFragmentDirections.actionPaymentsTabToPaymentsDetailsFragment();
        action.setCardIndex(viewModel.getIndexofPaymentDetail(paymentDetail));
        Navigation.findNavController(getView()).navigate(action);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPaymentsBinding.inflate(inflater, container, false);
        binding.setVm(viewModel);
        binding.setLifecycleOwner(this);
        binding.errorLayout.setModel(new CardsErrorView(getContext(), () -> viewModel.retryAgain()));
        binding.cardsLayout.post(() -> {
            //give the cards layout a minimum height, to anchor the date to the bottom screen
            //binding.cardsLayout.setMinHeight(binding.scrollView.getHeight() - binding.appBar.getHeight());
        });

        CardItemDecorator listDecorator = new CardItemDecorator(-getResources().getDimensionPixelSize(R.dimen.petro_canada_cards_padding));

        binding.creditCardsList.setAdapter(adapter);
        binding.creditCardsList.addItemDecoration(listDecorator);

        binding.addCardButton.setOnClickListener((v) -> navigateToAddCard());

        return binding.getRoot();
    }

    private void navigateToAddCard() {
        Navigation.findNavController(getView()).navigate(R.id.action_cards_tab_to_addCardFragment);
    }

    @Override
    public void onStart() {
        super.onStart();
        viewModel.onAttached();
    }

    @Override
    public void onRefresh() {
        viewModel.refreshPayments();
    }
}
