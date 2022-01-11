package suncor.com.android.ui.main.wallet.payments.list;

import android.content.Context;
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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import java.util.ArrayList;

import javax.inject.Inject;

import suncor.com.android.R;
import suncor.com.android.SuncorApplication;
import suncor.com.android.databinding.FragmentPaymentsBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.model.payments.PaymentDetail;
import suncor.com.android.ui.main.MainViewModel;
import suncor.com.android.ui.main.common.MainActivityFragment;
import suncor.com.android.ui.main.wallet.WalletFragment;
import suncor.com.android.ui.main.wallet.WalletFragmentDirections;
import suncor.com.android.ui.main.wallet.WalletTabInterface;
import suncor.com.android.ui.main.wallet.cards.CardsLoadType;
import suncor.com.android.ui.main.wallet.cards.list.CardItemDecorator;
import suncor.com.android.ui.main.wallet.cards.list.CardsErrorView;
import suncor.com.android.ui.main.wallet.cards.list.CardsFragmentDirections;
import suncor.com.android.utilities.AnalyticsUtils;

public class PaymentsFragment extends MainActivityFragment implements WalletTabInterface {

    @Inject
    ViewModelFactory viewModelFactory;
    private FragmentPaymentsBinding binding;
    private PaymentsViewModel viewModel;
    private MainViewModel mainViewModel;
    private PaymentsListAdapter adapter;

    @Override
    public String getTabName(Context context) {
        return context.getString(R.string.wallet_payment_tab);
    }

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
                        if(Navigation.findNavController(requireView()).getCurrentDestination().getId() == R.id.addPaymentFragment) {
                            Navigation.findNavController(getView()).navigate(action);
                        }
                    } else if (mainViewModel.isNewCardAdded()) {
                        AnalyticsUtils.setCurrentScreenName(getActivity(), mainViewModel.getNewAddedCard().getFirebaseScreenName());
                        CardsFragmentDirections.ActionCardsTabToCardsDetailsFragment action = CardsFragmentDirections.actionCardsTabToCardsDetailsFragment();
                        action.setLoadType(CardsLoadType.NEWLY_ADD_CARD);
                        if(Navigation.findNavController(requireView()).getCurrentDestination().getId() == R.id.addPaymentFragment) {
                            Navigation.findNavController(requireView()).navigate(action);
                        }
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
        if (getView() == null) return;

        AnalyticsUtils.setCurrentScreenName(getActivity(), paymentDetail.getFirebaseScreenName());
        WalletFragmentDirections.ActionPaymentsTabToPaymentsDetailsFragment action = WalletFragmentDirections.actionPaymentsTabToPaymentsDetailsFragment();
        action.setCardIndex(viewModel.getIndexofPaymentDetail(paymentDetail));

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
        binding = FragmentPaymentsBinding.inflate(inflater, container, false);
        binding.setVm(viewModel);
        binding.setLifecycleOwner(this);
        binding.errorLayout.setModel(new CardsErrorView(getContext(), () -> viewModel.retryAgain()));

        CardItemDecorator listDecorator = new CardItemDecorator(-getResources().getDimensionPixelSize(R.dimen.petro_canada_cards_padding));

        binding.creditCardsList.setAdapter(adapter);
        binding.creditCardsList.addItemDecoration(listDecorator);

        binding.addCardButton.setOnClickListener((v) -> navigateToAddCard());

        return binding.getRoot();
    }

    @Override
    public void navigateToAddCard() {
        if (getView() == null) return;

        NavController controller = Navigation.findNavController(getView());
        if (controller.getCurrentDestination() != null
                && controller.getCurrentDestination().getAction(R.id.action_payments_tab_to_addPaymentFragment) != null ) {
            controller.navigate(R.id.action_payments_tab_to_addPaymentFragment);
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
            viewModel.refreshPayments();
    }
}
