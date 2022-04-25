package suncor.com.android.ui.main.wallet.cards.details;

import android.Manifest;
import android.animation.AnimatorListenerAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
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
import androidx.core.content.ContextCompat;
import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import javax.inject.Inject;

import suncor.com.android.LocationLiveData;
import suncor.com.android.R;
import suncor.com.android.databinding.FragmentCardsDetailsBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.googleapis.passes.GooglePassesApiGateway;
import suncor.com.android.googleapis.passes.GooglePassesConfig;
import suncor.com.android.googlepay.passes.LoyalityData;
import suncor.com.android.model.DirectionsResult;
import suncor.com.android.model.Resource;
import suncor.com.android.model.SettingsResponse;
import suncor.com.android.model.cards.CardDetail;
import suncor.com.android.model.cards.CardType;
import suncor.com.android.model.station.Station;
import suncor.com.android.ui.common.Alerts;
import suncor.com.android.ui.main.MainViewModel;
import suncor.com.android.ui.main.wallet.cards.CardsLoadType;
import suncor.com.android.ui.main.common.MainActivityFragment;
import suncor.com.android.utilities.AnalyticsUtils;
import suncor.com.android.utilities.CardsUtil;
import suncor.com.android.utilities.LocationUtils;
import suncor.com.android.utilities.StationsUtil;
import suncor.com.android.utilities.Timber;

public class CardsDetailsFragment extends MainActivityFragment {
    private FragmentCardsDetailsBinding binding;
    CardDetailsViewModel viewModel;
    private MainViewModel mainViewModel;
    private int clickedCardIndex;
    private CardsLoadType loadType;
    @Inject
    ViewModelFactory viewModelFactory;
    private float previousBrightness;
    private CardsDetailsAdapter cardsDetailsAdapter;
    private ObservableBoolean isRemoving = new ObservableBoolean(false);

    private LocationLiveData locationLiveData;
    private LatLng currentLocation;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainViewModel = ViewModelProviders.of(getActivity(), viewModelFactory).get(MainViewModel.class);
        mainViewModel.setLinkedToAccount(false);
        mainViewModel.setNewCardAdded(false);
        locationLiveData = new LocationLiveData(getContext().getApplicationContext());
        locationLiveData.observe(this, location -> {
            currentLocation = (new LatLng(location.getLatitude(), location.getLongitude()));
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        clickedCardIndex = CardsDetailsFragmentArgs.fromBundle(getArguments()).getCardIndex();
        loadType = CardsDetailsFragmentArgs.fromBundle(getArguments()).getLoadType();
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(CardDetailsViewModel.class);
        viewModel.setLoadType(loadType);
        if (loadType == CardsLoadType.REDEEMED_SINGLE_TICKETS)
            viewModel.setRedeemedTicketNumbers(mainViewModel.getSingleTicketNumber());
        if (loadType == CardsLoadType.NEWLY_ADD_CARD)
            viewModel.setNewlyAddedCardNumber(mainViewModel.getNewAddedCard().getCardNumber());
        viewModel.retrieveCards();
        viewModel.cards.observe(getViewLifecycleOwner(), arrayListResource -> {
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

                //track screen name
                String screenName;
                if (clickedCardIndex <= 0 || clickedCardIndex > viewModel.cards.getValue().size()) {
                    //need to check
                    screenName = "my-petro-points-wallet-view-petro-card";
                } else {
                    screenName = "my-petro-points-wallet-view-" + viewModel.cards.getValue().get(clickedCardIndex).getCardName();
                }
                AnalyticsUtils.setCurrentScreenName(getActivity(), screenName);
            }
        });
        binding = FragmentCardsDetailsBinding.inflate(inflater, container, false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        binding.cardDetailRecycler.setLayoutManager(linearLayoutManager);
        binding.cardDetailRecycler.setItemAnimator(new Animator());
        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(binding.cardDetailRecycler);
        cardsDetailsAdapter = new CardsDetailsAdapter(this::cardViewMoreHandler, activeCarWashListener, cardReloadListener, gpaySaveToWalletListener);
        binding.cardDetailRecycler.setAdapter(cardsDetailsAdapter);
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
        cardsDetailsAdapter.registerAdapterDataObserver(binding.pageIndicator.getAdapterDataObserver());
        binding.buttonClose.setOnClickListener(v -> {
            Navigation.findNavController(getView()).getPreviousBackStackEntry().getSavedStateHandle().set("fromPayment", false);
            Navigation.findNavController(getView()).popBackStack();
        });
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

    void cardViewMoreHandler(ExpandedCardItem expandedCardItem) {
        RemoveCardBottomSheet removeCardBottomSheet = new RemoveCardBottomSheet();
        removeCardBottomSheet.setClickListener(v -> {
            AnalyticsUtils.logEvent(getContext(), "menu_tap", new Pair<>("menuSelection", getString(R.string.card_remove_bottom_sheet_title)));
            removeCardBottomSheet.dismiss();
            showConfirmationAlert(expandedCardItem);
        });
        removeCardBottomSheet.show(getFragmentManager(), RemoveCardBottomSheet.TAG);
    }

    private DialogInterface.OnClickListener buySingleTicketListener = (dialogInterface, i) -> {
        CardsDetailsFragmentDirections.ActionCardsDetailsFragmentToCarWashPurchaseFragment action =
                CardsDetailsFragmentDirections.actionCardsDetailsFragmentToCarWashPurchaseFragment(viewModel.getLoadType() == CardsLoadType.ALL);
        Navigation.findNavController(getView()).navigate(action);
    };

    private View.OnClickListener activeCarWashListener = view -> {
        if (isUserAtIndependentStation()) {
            StationsUtil.showIndependentStationAlert(getContext());
        } else {
            if (viewModel.cards.getValue().get(clickedCardIndex).getCardType() == CardType.ST) {
                CardsDetailsFragmentDirections.ActionCardsDetailsFragmentToCarWashBarCodeFragment
                        action = CardsDetailsFragmentDirections.actionCardsDetailsFragmentToCarWashBarCodeFragment(
                        loadType == CardsLoadType.REDEEMED_SINGLE_TICKETS ||
                                loadType == CardsLoadType.CAR_WASH_PRODUCTS
                );
                action.setSingleTicketNumber(viewModel.cards.getValue().get(clickedCardIndex).getTicketNumber());
                Navigation.findNavController(getView()).navigate(action);
            } else {
                CardDetail cardDetail = viewModel.cards.getValue().get(clickedCardIndex);
                if (viewModel.getIsCarWashBalanceZero().getValue() != null &&
                        viewModel.getIsCarWashBalanceZero().getValue()) {
                    CardsUtil.showZeroBalanceAlert(getActivity(), buySingleTicketListener, null);
                } else if (cardDetail.getBalance() <= 0) {
                    CardsUtil.showOtherCardAvailableAlert(getContext());
                } else if (cardDetail.isSuspendedCard()) {
                    CardsUtil.ShowSuspendedCardAlertForActivateWash(getContext());
                } else {
                    AnalyticsUtils.logCarwashActivationEvent(getContext(), AnalyticsUtils.Event.FORMSTEP, "Enter 3 digits", cardDetail.getCardType());
                    CardsDetailsFragmentDirections.ActionCardsDetailsFragmentToCarWashActivationSecurityFragment action
                            = CardsDetailsFragmentDirections.actionCardsDetailsFragmentToCarWashActivationSecurityFragment();
                    action.setCardNumber(viewModel.cards.getValue().get(clickedCardIndex).getCardNumber());
                    action.setCardIndex(clickedCardIndex);
                    action.setCardType(viewModel.cards.getValue().get(clickedCardIndex).getCardType().name());
                    action.setIsCardFromCarWash(loadType == CardsLoadType.CAR_WASH_PRODUCTS);
                    Navigation.findNavController(getView()).navigate(action);
                }
            }
        }

    };

    private View.OnClickListener cardReloadListener = view -> {
        CardDetail cardDetail = viewModel.cards.getValue().get(clickedCardIndex);
        if (cardDetail.isSuspendedCard()) {
            CardsUtil.showSuspendedCardAlert(getContext());
        } else {
            ExpandedCardItem cardItem = new ExpandedCardItem(getContext(), cardDetail);
            //open Reload Transaction form
            CardsDetailsFragmentDirections.ActionCardsDetailsFragmentToCarWashTransactionFragment
                    action = CardsDetailsFragmentDirections.actionCardsDetailsFragmentToCarWashTransactionFragment();
            action.setCardNumber(cardItem.getCardNumber());
            action.setCardName(cardItem.getCardName());
            action.setCardType(cardItem.getCardType().name());
            action.setCardIndex(clickedCardIndex);
            action.setIsCardFromCarWash(loadType == CardsLoadType.CAR_WASH_PRODUCTS);
            Navigation.findNavController(getView()).navigate(action);
        }
    };


    private View.OnClickListener gpaySaveToWalletListener = view -> {
        showAddCardProgress();
        viewModel.getSettings().observe(getViewLifecycleOwner(), result -> {
            if (result.status == Resource.Status.LOADING) {
            } else if (result.status == Resource.Status.ERROR) {
                hideAddCardProgress();
                Alerts.prepareGeneralErrorDialog(getContext(), AnalyticsUtils.getCardFormName()).show();
            } else if (result.status == Resource.Status.SUCCESS && result.data != null) {
                SettingsResponse.GooglePassConfig googlePassesConfig = result.data.getSettings().getGooglePass();
                LoyalityData loyalityData = viewModel.getLoyalityCardDataForGoogleWallet(getContext(), clickedCardIndex);

                new Thread(() -> {
                    GooglePassesApiGateway gateway = new GooglePassesApiGateway();
                    String cardAuthToken = gateway.insertLoyalityCard(getContext(), loyalityData, googlePassesConfig);
                    Timber.i("GOOGLE PASSES: card Token " + cardAuthToken);
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            hideAddCardProgress();
                            if (cardAuthToken == null) {
                                Alerts.prepareGeneralErrorDialog(getContext(), AnalyticsUtils.getCardFormName()).show();
                                return;
                            }
                            getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(cardAuthToken)));

                        });
                    }
                }).start();
            }
        });

    };

    private void showAddCardProgress() {
        binding.loadingProgressBar.setVisibility(View.VISIBLE);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void hideAddCardProgress() {
        binding.loadingProgressBar.setVisibility(View.GONE);
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        hideAddCardProgress();
    }

    private void showConfirmationAlert(ExpandedCardItem expandedCardItem) {
        String analyticsName = getResources().getString(R.string.cards_remove_card_alert_title) + "(" + getResources().getString(R.string.cards_remove_card_alert_message) + ")";
        AnalyticsUtils.logEvent(getContext(), AnalyticsUtils.Event._ALERT,
                new Pair<>(AnalyticsUtils.Param.alertTitle, analyticsName),
                new Pair<>(AnalyticsUtils.Param.FORMNAME, AnalyticsUtils.getCardFormName())
        );
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext()).setTitle(getResources().getString(R.string.cards_remove_card_alert_title)).setMessage(getResources().getString(R.string.cards_remove_card_alert_message))
                .setPositiveButton(getResources().getString(R.string.cards_remove_card_alert_remove), (dialog, which) -> {
                    viewModel.deleteCard(expandedCardItem.getCardDetail()).observe(this, cardDetailResource -> {
                        AnalyticsUtils.logEvent(getContext(), AnalyticsUtils.Event.alertInteraction,
                                new Pair<>(AnalyticsUtils.Param.alertTitle, analyticsName),
                                new Pair<>(AnalyticsUtils.Param.alertSelection, getResources().getString(R.string.cards_remove_card_alert_remove)),
                                new Pair<>(AnalyticsUtils.Param.FORMNAME, AnalyticsUtils.getCardFormName())
                        );
                        if (cardDetailResource.status == Resource.Status.ERROR) {
                            isRemoving.set(false);
                            Alerts.prepareGeneralErrorDialog(getContext(), AnalyticsUtils.getCardFormName()).show();
                        } else if (cardDetailResource.status == Resource.Status.SUCCESS) {
                            isRemoving.set(false);
                            new Handler().postDelayed(() -> {
                                cardsDetailsAdapter.removeCard(new ExpandedCardItem(getContext(), cardDetailResource.data));
                                if (cardsDetailsAdapter.getCardItems().size() == 0)
                                    Navigation.findNavController(getView()).popBackStack();
                            }, 200);

                            AnalyticsUtils.logEvent(getContext(), "card_remove", new Pair<>("cardType", "Credit Card"));
                        } else if (cardDetailResource.status == Resource.Status.LOADING) {
                            isRemoving.set(true);
                        }
                    });
                }).setNegativeButton(getResources().getString(R.string.cards_remove_card_alert_cancel), (dialog, which) -> {
                    AnalyticsUtils.logEvent(getContext(), AnalyticsUtils.Event.alertInteraction,
                            new Pair<>(AnalyticsUtils.Param.alertTitle, getString(R.string.cards_remove_card_alert_title)),
                            new Pair<>(AnalyticsUtils.Param.alertSelection, getString(R.string.cards_remove_card_alert_cancel)),
                            new Pair<>(AnalyticsUtils.Param.FORMNAME, AnalyticsUtils.getCardFormName()));
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

    private boolean isUserAtIndependentStation() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Station station = mainViewModel.getNearestStation().getValue();
            if (station != null) {
                LatLng dest = new LatLng(station.getAddress().getLatitude(), station.getAddress().getLongitude());
                if (currentLocation != null) {
                    LatLng origin = new LatLng(currentLocation.latitude, currentLocation.longitude);
                    if (station.isStationIndependentDealer() && LocationUtils.calculateDistance(dest, origin) < DirectionsResult.ONSITE_THRESHOLD) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


}
