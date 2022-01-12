package suncor.com.android.ui.main.wallet.payments.details;

import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;

import javax.inject.Inject;

import suncor.com.android.HomeNavigationDirections;
import suncor.com.android.LocationLiveData;
import suncor.com.android.R;
import suncor.com.android.databinding.FragmentCardsDetailsBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.model.Resource;
import suncor.com.android.model.payments.PaymentDetail;
import suncor.com.android.ui.common.Alerts;
import suncor.com.android.ui.main.MainViewModel;
import suncor.com.android.ui.main.common.MainActivityFragment;
import suncor.com.android.ui.main.wallet.cards.CardsLoadType;
import suncor.com.android.ui.main.wallet.cards.details.CardsDetailsFragmentArgs;
import suncor.com.android.utilities.AnalyticsUtils;

public class PaymentsDetailsFragment extends MainActivityFragment {
    private FragmentCardsDetailsBinding binding;
    PaymentDetailsViewModel viewModel;
    private MainViewModel mainViewModel;
    private int clickedCardIndex;
    @Inject
    ViewModelFactory viewModelFactory;
    private float previousBrightness;
    private PaymentsDetailsAdapter adapter;
    private ObservableBoolean isRemoving = new ObservableBoolean(false);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainViewModel = ViewModelProviders.of(getActivity(), viewModelFactory).get(MainViewModel.class);
        mainViewModel.setLinkedToAccount(false);
        mainViewModel.setNewCardAdded(false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        clickedCardIndex = CardsDetailsFragmentArgs.fromBundle(getArguments()).getCardIndex();
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(PaymentDetailsViewModel.class);

        viewModel.retrieveCards();
        viewModel.payments.observe(getViewLifecycleOwner(), arrayListResource -> {
            // For payments we only want to display the selected card.
            // In order to avoid changing the pager we will create an array with only the selected payment
            ArrayList<ExpandedPaymentItem> expandedPaymentItems = new ArrayList<>();
            expandedPaymentItems.add(new ExpandedPaymentItem(getContext(), arrayListResource.get(clickedCardIndex)));

            if (expandedPaymentItems.size() > 0) {
                adapter.setCardItems(expandedPaymentItems);
                adapter.notifyDataSetChanged();
                binding.setNumCards(expandedPaymentItems.size());
                binding.executePendingBindings();

                //track screen name
              /*  String screenName;
                if (clickedCardIndex == 0) {
                    screenName = "my-petro-points-wallet-view-card";
                } else {
                    screenName = "my-petro-points-wallet-view-" + viewModel.payments.getValue().get(clickedCardIndex).getPaymentType().name();
                }
                AnalyticsUtils.setCurrentScreenName(getActivity(), screenName);*/
            }
        });
        binding = FragmentCardsDetailsBinding.inflate(inflater, container, false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        binding.cardDetailRecycler.setLayoutManager(linearLayoutManager);
        binding.cardDetailRecycler.setItemAnimator(new Animator());
        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(binding.cardDetailRecycler);
        adapter = new PaymentsDetailsAdapter(this::cardViewMoreHandler);
        binding.cardDetailRecycler.setAdapter(adapter);
        binding.cardDetailRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    clickedCardIndex = linearLayoutManager.findFirstVisibleItemPosition();
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                clickedCardIndex = linearLayoutManager.findFirstVisibleItemPosition();
            }
        });
        binding.pageIndicator.attachToRecyclerView(binding.cardDetailRecycler, pagerSnapHelper);
        adapter.registerAdapterDataObserver(binding.pageIndicator.getAdapterDataObserver());
        binding.buttonClose.setOnClickListener(v -> goBack());
        binding.setIsRemoving(isRemoving);
        binding.setLifecycleOwner(this);
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

    private void cardViewMoreHandler(ExpandedPaymentItem expandedCardItem) {
        RemovePaymentBottomSheet removePaymentBottomSheet = new RemovePaymentBottomSheet();
        removePaymentBottomSheet.setClickListener(v -> {
            AnalyticsUtils.logEvent(getContext(), "menu_tap", new Pair<>("menuSelection", getString(R.string.payment_remove_bottom_sheet_title)));
            removePaymentBottomSheet.dismiss();
            showConfirmationAlert(expandedCardItem);
        });
        removePaymentBottomSheet.show(getFragmentManager(), RemovePaymentBottomSheet.TAG);
    }

    private void showConfirmationAlert(ExpandedPaymentItem expandedCardItem) {
        String analyticsName = getResources().getString(R.string.payments_remove_card_alert_title) + "("+getResources().getString(R.string.payments_remove_card_alert_message)+")";
        AnalyticsUtils.logEvent(getContext(), AnalyticsUtils.Event._ALERT,
                new Pair<>(AnalyticsUtils.Param.alertTitle, analyticsName),
                new Pair<>(AnalyticsUtils.Param.FORMNAME,AnalyticsUtils.getCardFormName())
        );
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext()).setTitle(getResources().getString(R.string.payments_remove_card_alert_title)).setMessage(getResources().getString(R.string.payments_remove_card_alert_message))
                .setPositiveButton(getResources().getString(R.string.payments_remove_card_alert_remove), (dialog, which) -> {
                    viewModel.deletePayment(expandedCardItem.getPaymentDetail()).observe(this, paymentDetailResource -> {
                        AnalyticsUtils.logEvent(getContext(), AnalyticsUtils.Event.alertInteraction,
                                new Pair<>(AnalyticsUtils.Param.alertTitle, analyticsName),
                                new Pair<>(AnalyticsUtils.Param.alertSelection, getResources().getString(R.string.payments_remove_card_alert_remove)),
                                new Pair<>(AnalyticsUtils.Param.FORMNAME,AnalyticsUtils.getCardFormName())
                        );
                        if (paymentDetailResource.status == Resource.Status.ERROR) {
                            isRemoving.set(false);
                            Alerts.prepareGeneralErrorDialog(getContext(), AnalyticsUtils.getCardFormName()).show();
                        } else if (paymentDetailResource.status == Resource.Status.SUCCESS) {
                            isRemoving.set(false);
                            new Handler().postDelayed(() -> {
                                adapter.removeCard(new ExpandedPaymentItem(getContext(), paymentDetailResource.data));
                                if (adapter.getCardItems().size() == 0) {
                                    goBack();
                                }
                            }, 200);
                            AnalyticsUtils.logEvent(getContext(), "card_remove", new Pair<>("cardType", "Credit Card"));
                           // AnalyticsUtils.logEvent(getContext(), "payment_remove", new Pair<>("cardNumber", paymentDetailResource.data.getCardNumber()));
                        } else if (paymentDetailResource.status == Resource.Status.LOADING) {
                            isRemoving.set(true);
                        }
                    });

                }).setNegativeButton(getResources().getString(R.string.payments_remove_card_alert_cancel), (dialog, which) -> {
                    AnalyticsUtils.logEvent(getContext(), AnalyticsUtils.Event.alertInteraction,
                            new Pair<>(AnalyticsUtils.Param.alertTitle, getString(R.string.payments_remove_card_alert_title)),
                            new Pair<>(AnalyticsUtils.Param.alertSelection,getString(R.string.payments_remove_card_alert_cancel)),
                            new Pair<>(AnalyticsUtils.Param.FORMNAME,AnalyticsUtils.getCardFormName())
                    );
                });
        builder.show();
    }

    public class Animator extends DefaultItemAnimator {
        @Override
        public boolean animateRemove(RecyclerView.ViewHolder holder) {
            View view = holder.itemView;
            ViewPropertyAnimator propertyAnimator = view.animate();
            propertyAnimator.setDuration(300).setInterpolator(new AccelerateDecelerateInterpolator()).scaleX(0).scaleY(0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(android.animation.Animator animation) {
                    propertyAnimator.setListener(null);
                    view.setScaleX(1);
                    view.setScaleY(1);
                    dispatchRemoveFinished(holder);
                }

                @Override
                public void onAnimationStart(android.animation.Animator animation) {
                    dispatchRemoveStarting(holder);
                }
            }).start();
            return true;
        }
    }

    void goBack() {
        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).getPreviousBackStackEntry().getSavedStateHandle().set("fromPayment", true);

        PaymentsDetailsFragmentDirections.ActionDetailsToWalletFragment action = PaymentsDetailsFragmentDirections.actionDetailsToWalletFragment();
        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(action);

    }
}
