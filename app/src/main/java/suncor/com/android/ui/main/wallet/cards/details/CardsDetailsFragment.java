package suncor.com.android.ui.main.wallet.cards.details;

import android.Manifest;
import android.animation.AnimatorListenerAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
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
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import javax.inject.Inject;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import suncor.com.android.LocationLiveData;
import suncor.com.android.R;
import suncor.com.android.databinding.FragmentCardsDetailsBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.googleapis.passes.GooglePassesApiGateway;
import suncor.com.android.googlepay.passes.LoyalityData;
import suncor.com.android.model.DirectionsResult;
import suncor.com.android.model.Resource;
import suncor.com.android.model.SettingsResponse;
import suncor.com.android.model.cards.CardDetail;
import suncor.com.android.model.cards.CardType;
import suncor.com.android.model.redeem.response.Card;
import suncor.com.android.model.station.Station;
import suncor.com.android.ui.common.Alerts;
import suncor.com.android.ui.main.MainViewModel;
import suncor.com.android.ui.main.wallet.cards.CardsLoadType;
import suncor.com.android.ui.main.common.MainActivityFragment;
import suncor.com.android.utilities.AnalyticsUtils;
import suncor.com.android.utilities.CardsUtil;
import suncor.com.android.utilities.ConnectionUtil;
import suncor.com.android.utilities.Constants;
import suncor.com.android.utilities.DateUtils;
import suncor.com.android.utilities.LocationUtils;
import suncor.com.android.utilities.StationsUtil;
import suncor.com.android.utilities.Timber;

@RequiresApi(api = Build.VERSION_CODES.O)
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
    private boolean vacuumToggle = false;
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
        NavController navController = NavHostFragment.findNavController(this);
        MutableLiveData<Integer> liveData = navController.getCurrentBackStackEntry().getSavedStateHandle().getLiveData(Constants.CLICKED_CARD_INDEX);
        liveData.observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer s) {
                clickedCardIndex = s;
            }
        });
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
                if (clickedCardIndex <= 0 || clickedCardIndex > viewModel.cards.getValue().size() ) {
                    //need to check
                    screenName = "my-petro-points-wallet-view-petro-card";
                } else {
                    screenName = "my-petro-points-wallet-view-" + viewModel.cards.getValue().get(clickedCardIndex).getCardName();
                }
                AnalyticsUtils.setCurrentScreenName(getActivity(), screenName);
            }
        });
        if (cardsDetailsAdapter != null) {
            viewModel.getProgressDetails(cardsDetailsAdapter.getCardItems().get(clickedCardIndex).getCardDetail().getCardNumber(), cardsDetailsAdapter.getCardItems().get(clickedCardIndex).getCardDetail().getCardType()).observe(getViewLifecycleOwner(), result -> {
                Timber.d("UPDATE-CARD-CALLED-OnCreateView");
                if (result.status == Resource.Status.LOADING) {
                    showAddCardProgress();
                } else if (result.status == Resource.Status.ERROR) {
                    hideAddCardProgress();
                    Alerts.prepareGeneralErrorDialog(getContext(), AnalyticsUtils.getCardFormName()).show();
                } else if (result.status == Resource.Status.SUCCESS && result.data != null) {
                    hideAddCardProgress();

                    if (result.data.isWashInProgress() || result.data.isVacuumInProgress() || !result.data.isCanWash() || !result.data.isCanVacuum()) {
                        ExpandedCardItem updatedItem = new ExpandedCardItem(getContext(), result.data);
                        CardDetail currentDetail = cardsDetailsAdapter.getCardItems().get(clickedCardIndex).getCardDetail();
                        CardDetail newCardDetail = result.data;
                        if ((currentDetail.isCanVacuum() != newCardDetail.isCanVacuum()) || (currentDetail.isCanWash() != newCardDetail.isCanWash()) || (currentDetail.isVacuumInProgress() != newCardDetail.isVacuumInProgress()) || (currentDetail.isWashInProgress() != newCardDetail.isWashInProgress())) {
                            cardsDetailsAdapter.updateCardItems(updatedItem, clickedCardIndex);
                            if (cardsDetailsAdapter.getCardItems().get(clickedCardIndex).getCardDetail().isVacuumInProgress() || cardsDetailsAdapter.getCardItems().get(clickedCardIndex).isWashInProgress() || !cardsDetailsAdapter.getCardItems().get(clickedCardIndex).isCanVacuum() || !cardsDetailsAdapter.getCardItems().get(clickedCardIndex).isCanWash()) {
                                viewModel.setRecurringService(cardsDetailsAdapter.getCardItems().get(clickedCardIndex).getCardNumber(), cardsDetailsAdapter.getCardItems().get(clickedCardIndex).getCardType());
                                cardsDetailsAdapter.getCardItems().get(clickedCardIndex).setTimer(true);
                            } else {
                                cardsDetailsAdapter.getCardItems().get(clickedCardIndex).setTimer(false);
                                viewModel.stopRecurringService();
                            }
                        }
                    }
                }
            });
        }
        viewModel.getSettings().observe(getViewLifecycleOwner(), result -> {
            if (result.status == Resource.Status.LOADING) {
            } else if (result.status == Resource.Status.ERROR) {
                Alerts.prepareGeneralErrorDialog(getContext(), AnalyticsUtils.getCardFormName()).show();
            } else if (result.status == Resource.Status.SUCCESS && result.data != null) {
                vacuumToggle = result.data.getSettings().toggleFeature.isVacuumScanBarcode();
                if (cardsDetailsAdapter != null) {
                    cardsDetailsAdapter.updateVacuumToggle(vacuumToggle);
                }
            }
        });
        binding = FragmentCardsDetailsBinding.inflate(inflater, container, false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        binding.cardDetailRecycler.setLayoutManager(linearLayoutManager);
        binding.cardDetailRecycler.setItemAnimator(new Animator());
        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(binding.cardDetailRecycler);
        cardsDetailsAdapter = new CardsDetailsAdapter( this::cardViewMoreHandler, activeCarWashListener, cardReloadListener, gpaySaveToWalletListener, vacuumListener, vacuumToggle);
        binding.cardDetailRecycler.setAdapter(cardsDetailsAdapter);
        binding.cardDetailRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    clickedCardIndex = linearLayoutManager.findFirstVisibleItemPosition();
                    Timber.d("VISIBLECARD-: " + viewModel.cards.getValue().get(clickedCardIndex).getCardNumber());
                    CardDetail cardDetail = cardsDetailsAdapter.getCardItems().get(clickedCardIndex).getCardDetail();
                    if (cardDetail.getCardType() == CardType.SP || cardDetail.getCardType() == CardType.WAG) {
                        if (cardDetail.isVacuumInProgress() || cardDetail.isWashInProgress() || !cardDetail.isCanVacuum() || !cardDetail.isCanWash()) {
                            viewModel.getProgressDetails(cardsDetailsAdapter.getCardItems().get(clickedCardIndex).getCardDetail().getCardNumber(), cardsDetailsAdapter.getCardItems().get(clickedCardIndex).getCardDetail().getCardType()).observe(getViewLifecycleOwner(), result -> {
                                Timber.d("UPDATE-CARD-CALLED-Scroll-State");
                                if (result.status == Resource.Status.LOADING) {
                                    showAddCardProgress();
                                } else if (result.status == Resource.Status.ERROR) {
                                    hideAddCardProgress();
                                    Alerts.prepareGeneralErrorDialog(getContext(), AnalyticsUtils.getCardFormName()).show();
                                } else if (result.status == Resource.Status.SUCCESS && result.data != null) {
                                    hideAddCardProgress();
                                    if (cardsDetailsAdapter != null) {
                                        ExpandedCardItem updatedItem = new ExpandedCardItem(getContext(), result.data);
                                        CardDetail currentDetail = cardsDetailsAdapter.getCardItems().get(clickedCardIndex).getCardDetail();
                                        CardDetail newCardDetail = result.data;

                                        if (currentDetail.isCanVacuum() != newCardDetail.isCanVacuum() || currentDetail.isCanWash() != newCardDetail.isCanWash() || currentDetail.isVacuumInProgress() != newCardDetail.isVacuumInProgress() || currentDetail.isWashInProgress() != newCardDetail.isWashInProgress()) {
                                            Timber.d("UPDATE-CARD-CALLED-Scroll-State-Item Updated");
                                            cardsDetailsAdapter.updateCardItems(updatedItem, clickedCardIndex);
                                        }
                                        CardDetail updatedCardDetail = cardsDetailsAdapter.getCardItems().get(clickedCardIndex).getCardDetail();
                                        if (updatedCardDetail.isVacuumInProgress() || updatedCardDetail.isWashInProgress() || !updatedCardDetail.isCanVacuum() || !updatedCardDetail.isCanWash()) {
                                            viewModel.setRecurringService(updatedCardDetail.getCardNumber(), updatedCardDetail.getCardType());
                                            cardsDetailsAdapter.getCardItems().get(clickedCardIndex).setTimer(true);
                                        } else {
                                            cardsDetailsAdapter.getCardItems().get(clickedCardIndex).setTimer(false);
                                            try {
                                                viewModel.stopRecurringService();
                                            } catch (Exception e) {
                                                Timber.d("Handler not available !");
                                            }
                                        }
                                    }
                                }
                            });


                        } else {
                            Timber.d("TIMER-BLOCK-else stop called");
                            cardsDetailsAdapter.getCardItems().get(clickedCardIndex).setTimer(false);
                            viewModel.stopRecurringService();
                            viewModel.cardDetail.removeObservers(getViewLifecycleOwner());
                        }
                    }

                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                hideAddCardProgress();
            }
        });

        viewModel.cardDetail.observeForever(result -> {
            Timber.d("Observe for ever");
            if (result != null && cardsDetailsAdapter != null) {
                try {
                    if (cardsDetailsAdapter.getCardItems().get(clickedCardIndex).getCardDetail() != null) {
                        CardDetail currentDetail = cardsDetailsAdapter.getCardItems().get(clickedCardIndex).getCardDetail();
                        CardDetail newCardDetail = result;
                        if ((currentDetail.isCanVacuum() != newCardDetail.isCanVacuum()) || (currentDetail.isCanWash() != newCardDetail.isCanWash()) || (currentDetail.isVacuumInProgress() != newCardDetail.isVacuumInProgress()) || (currentDetail.isWashInProgress() != newCardDetail.isWashInProgress())) {
                            Timber.d("Observe for ever-Item Updated");
                            ExpandedCardItem updatedItem = new ExpandedCardItem(getContext(), result);
                            cardsDetailsAdapter.updateCardItems(updatedItem, clickedCardIndex);
                            CardDetail updatedCardDetail = cardsDetailsAdapter.getCardItems().get(clickedCardIndex).getCardDetail();
                            if (!updatedCardDetail.isVacuumInProgress() || !updatedCardDetail.isWashInProgress() || updatedCardDetail.isCanVacuum() || updatedCardDetail.isCanWash()) {
                                cardsDetailsAdapter.getCardItems().get(clickedCardIndex).setTimer(false);
                                try {
                                    viewModel.stopRecurringService();
                                } catch (Exception e) {
                                    Timber.d("Handler not available !");
                                }
                            }
                        }

                    }

                } catch (Exception e) {

                }
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
    public void onPause() {
        super.onPause();
        if (cardsDetailsAdapter.getCardItems() != null && cardsDetailsAdapter.getCardItems().get(clickedCardIndex).isTimer()) {
            cardsDetailsAdapter.getCardItems().get(clickedCardIndex).setTimer(false);
            viewModel.stopRecurringService();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        WindowManager.LayoutParams attributes = getActivity().getWindow().getAttributes();
        attributes.screenBrightness = previousBrightness;
        getActivity().getWindow().setAttributes(attributes);
        if (cardsDetailsAdapter.getCardItems() != null && cardsDetailsAdapter.getCardItems().get(clickedCardIndex).isTimer()) {
            cardsDetailsAdapter.getCardItems().get(clickedCardIndex).setTimer(false);
            viewModel.stopRecurringService();
        }

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
            showConfirmationAlert();
        });
        removeCardBottomSheet.show(getFragmentManager(), RemoveCardBottomSheet.TAG);
    }

    private DialogInterface.OnClickListener buySingleTicketListener = (dialogInterface, i) -> {
        CardsDetailsFragmentDirections.ActionCardsDetailsFragmentToCarWashPurchaseFragment action =
                CardsDetailsFragmentDirections.actionCardsDetailsFragmentToCarWashPurchaseFragment(viewModel.getLoadType() == CardsLoadType.ALL);
        Navigation.findNavController(getView()).navigate(action);
    };

    private View.OnClickListener activeCarWashListener = view -> {
        boolean hasInternetConnection= ConnectionUtil.haveNetworkConnection(getContext());
        if (!hasInternetConnection) {
            Alerts.prepareGeneralErrorDialog(getContext(), AnalyticsUtils.getCardFormName()).show();
        } else {
            CardDetail cardDetail = cardsDetailsAdapter.getCardItems().get(clickedCardIndex).getCardDetail();
            AnalyticsUtils.logEvent(getContext(), AnalyticsUtils.Event.activateCarWashClick,
                    new Pair<>(AnalyticsUtils.Param.carWashCardType, viewModel.cards.getValue().get(clickedCardIndex).getLongName())
            );

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
                    if (viewModel.getIsCarWashBalanceZero().getValue() != null &&
                            viewModel.getIsCarWashBalanceZero().getValue()) {
                        CardsUtil.showZeroBalanceAlert(getActivity(), buySingleTicketListener, null);
                    } else if (cardDetail.getBalance() <= 0) {
                        CardsUtil.showOtherCardAvailableAlert(getContext());
                    } else if (cardDetail.isSuspendedCard()) {
                        CardsUtil.ShowSuspendedCardAlertForActivateWash(getContext());
                    } else if (cardDetail.isWashInProgress()) {
                        CardsUtil.showWashInprogressAlert(getContext());
                    } else if (!cardDetail.isCanWash() && cardDetail.getCardType() == CardType.SP) {
                        if (cardDetail.getLastWashStoreId() != null && cardDetail.getCardType() == CardType.SP) {
                            showStoreAddressAlert(cardDetail.getLastWashStoreId(), Constants.TYPE_WASH, cardDetail.getLastWashDt());
                        }
                    } else {
                        AnalyticsUtils.logCarwashActivationEvent(getContext(), AnalyticsUtils.Event.FORMSTEP, "Enter 3 digits", cardDetail.getCardType());
                        CardsDetailsFragmentDirections.ActionCardsDetailsFragmentToCarWashActivationSecurityFragment action
                                = CardsDetailsFragmentDirections.actionCardsDetailsFragmentToCarWashActivationSecurityFragment();
                        action.setCardNumber(viewModel.cards.getValue().get(clickedCardIndex).getCardNumber());
                        action.setCardIndex(clickedCardIndex);
                        action.setCardType(viewModel.cards.getValue().get(clickedCardIndex).getLongName());
                        action.setIsCardFromCarWash(loadType == CardsLoadType.CAR_WASH_PRODUCTS);
                        Navigation.findNavController(getView()).navigate(action);
                    }
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

    private View.OnClickListener vacuumListener = view -> {
        boolean hasInternetConnection= ConnectionUtil.haveNetworkConnection(getContext());
        if (!hasInternetConnection) {
            Alerts.prepareGeneralErrorDialog(getContext(), AnalyticsUtils.getCardFormName()).show();
        } else {
            CardDetail cardDetail = cardsDetailsAdapter.getCardItems().get(clickedCardIndex).getCardDetail();
            ExpandedCardItem cardItem = new ExpandedCardItem(getContext(), cardDetail);
            if (!cardDetail.isCanVacuum() && cardDetail.getCardType() == CardType.SP) {
                if (cardDetail.getLastVacuumSiteId() != null) {
                    showStoreAddressAlert(cardDetail.getLastVacuumSiteId(), Constants.TYPE_VACUUM, cardDetail.getLastVacuumDt());
                }
            } else if (!cardDetail.isCanVacuum() && cardDetail.getCardType() == CardType.WAG) {
                // Do nothing for WAG
            } else if (cardDetail.isVacuumInProgress()) {
                CardsUtil.showVacuumInprogressAlert(getContext());
            } else {
                CardsDetailsFragmentDirections.ActionCardsDetailsFragmentToVacuumBarcodeFragment
                        action = CardsDetailsFragmentDirections.actionCardsDetailsFragmentToVacuumBarcodeFragment();
                action.setCardNumber(cardItem.getCardNumber());
                Navigation.findNavController(getView()).navigate(action);
            }
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
                    String cardAuthToken = gateway.insertLoyalityCard(getContext(), loyalityData, googlePassesConfig, new Function1<Exception, Unit>() {
                        @Override
                        public Unit invoke(Exception e) {
                            AnalyticsUtils.logEvent(getContext(), AnalyticsUtils.Event.ADDPPTSTOWALLETERROR.toString(), new Pair<>(AnalyticsUtils.Param.WALLETTYPE.toString(), Constants.GPAY_ANALYTICS),
                                    new Pair<>(AnalyticsUtils.Param.errorMessage.toString(), Constants.SOMETHING_WRONG));
                            return null;
                        }
                    });
                    Timber.i("GOOGLE PASSES: card Token " + cardAuthToken);
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            hideAddCardProgress();
                            if (cardAuthToken == null) {
                                Alerts.prepareGeneralErrorDialog(getContext(), AnalyticsUtils.getCardFormName()).show();
                                return;
                            }
                            AnalyticsUtils.logEvent(getContext(), AnalyticsUtils.Event.ADDPPTSTOWALLET.toString(), new Pair<>(AnalyticsUtils.Param.WALLETTYPE.toString(), Constants.GPAY_ANALYTICS));
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

    private void showConfirmationAlert() {
        String analyticsName = getResources().getString(R.string.cards_remove_card_alert_title) + "(" + getResources().getString(R.string.cards_remove_card_alert_message) + ")";
        AnalyticsUtils.logEvent(getContext(), AnalyticsUtils.Event._ALERT,
                new Pair<>(AnalyticsUtils.Param.alertTitle, analyticsName),
                new Pair<>(AnalyticsUtils.Param.FORMNAME, AnalyticsUtils.getCardFormName())
        );
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext()).setTitle(getResources().getString(R.string.cards_remove_card_alert_title)).setMessage(getResources().getString(R.string.cards_remove_card_alert_message))
                .setPositiveButton(getResources().getString(R.string.cards_remove_card_alert_remove), (dialog, which) -> {
                    viewModel.deleteCard(viewModel.cards.getValue().get(clickedCardIndex)).observe(this, cardDetailResource -> {
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showStoreAddressAlert(String storId, String type, String date) {
        viewModel.getStoreDetails(storId).observe(getViewLifecycleOwner(), result -> {
            if (result.status == Resource.Status.LOADING) {
                showAddCardProgress();
            } else if (result.status == Resource.Status.ERROR) {
                hideAddCardProgress();
                Alerts.prepareGeneralErrorDialog(getContext(), AnalyticsUtils.getCardFormName()).show();
            } else if (result.status == Resource.Status.SUCCESS && result.data != null) {
                hideAddCardProgress();
                if (type.equals(Constants.TYPE_WASH)) {
                    CardsUtil.showDailyWashUsedAlert(getContext(), DateUtils.getInputFormattedTime(date), result.data.getAddress().getAddressLine() + "," + result.data.getAddress().getPrimaryCity());
                } else if (type.equals(Constants.TYPE_VACUUM)) {
                    CardsUtil.showDailyVacuumUsedAlert(getContext(), DateUtils.getInputFormattedTime(date), result.data.getAddress().getAddressLine() + "," + result.data.getAddress().getPrimaryCity());
                }
            }
        });
    }
}
