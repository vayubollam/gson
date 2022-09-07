package suncor.com.android.ui.main.carwash;

import android.graphics.Outline;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import java.util.ArrayList;
import suncor.com.android.R;
import suncor.com.android.databinding.CarwashNearestCardBinding;
import suncor.com.android.databinding.FragmentCarWashBinding;
import suncor.com.android.model.Resource;
import suncor.com.android.model.cards.CardDetail;
import suncor.com.android.model.cards.CardType;
import suncor.com.android.model.station.Station;
import suncor.com.android.ui.common.Alerts;
import suncor.com.android.ui.common.GenericErrorView;
import suncor.com.android.ui.common.OnBackPressedListener;
import suncor.com.android.ui.main.wallet.cards.CardsLoadType;
import suncor.com.android.ui.main.wallet.cards.list.CardItemDecorator;
import suncor.com.android.ui.main.wallet.cards.list.CardListItem;
import suncor.com.android.ui.main.wallet.cards.list.CardsListAdapter;
import suncor.com.android.ui.main.stationlocator.StationDetailsDialog;
import suncor.com.android.ui.main.stationlocator.StationItem;
import suncor.com.android.uicomponents.swiperefreshlayout.SwipeRefreshLayout;
import suncor.com.android.utilities.AnalyticsUtils;
import suncor.com.android.utilities.CardsUtil;
import suncor.com.android.utilities.NavigationAppsHelper;
import suncor.com.android.utilities.StationsUtil;


public class CarWashCardFragment extends CarwashLocation implements OnBackPressedListener,
        SwipeRefreshLayout.OnRefreshListener {

    private FragmentCarWashBinding binding;
    private CardsListAdapter petroCanadaCardsAdapter;
    private float appBarElevation;

    private CarwashNearestCardBinding nearestCardBinding;
    private boolean isFirstTime = true;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appBarElevation = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
        petroCanadaCardsAdapter = new CardsListAdapter(this::cardClick, this::reloadButtonClick);
        petroCanadaCardsAdapter.setIsCarWashReload(carWashCardViewModel.getCarWashToggleStatus());
        carWashCardViewModel.getViewState().observe(this, (result) -> {
            if (result != CarWashCardViewModel.ViewState.REFRESHING) {
                binding.refreshLayout.setRefreshing(false);
            }

            if (result != CarWashCardViewModel.ViewState.REFRESHING && result != CarWashCardViewModel.ViewState.LOADING
                    && result != CarWashCardViewModel.ViewState.FAILED && carWashCardViewModel.getIsCardAvailable().getValue()) {

                if (mainViewModel.isLinkedToAccount()) {
                    AnalyticsUtils.setCurrentScreenName(getActivity(), mainViewModel.getNewAddedCard().getFirebaseCarwashScreenName());
                    CarWashCardFragmentDirections.ActionCarWashCardFragmentToCardsDetailsFragment action = CarWashCardFragmentDirections.actionCarWashCardFragmentToCardsDetailsFragment();
                    action.setLoadType(CardsLoadType.REDEEMED_SINGLE_TICKETS);
                    Navigation.findNavController(getView()).navigate((NavDirections) action);
                } else if (mainViewModel.isNewCardAdded() && (mainViewModel.getNewAddedCard().getCardType() == CardType.WAG || mainViewModel.getNewAddedCard().getCardType() == CardType.SP)) {
                    AnalyticsUtils.setCurrentScreenName(getActivity(), mainViewModel.getNewAddedCard().getFirebaseCarwashScreenName());
                    CarWashCardFragmentDirections.ActionCarWashCardFragmentToCardsDetailsFragment action = CarWashCardFragmentDirections.actionCarWashCardFragmentToCardsDetailsFragment();
                    action.setLoadType(CardsLoadType.NEWLY_ADD_CARD);
                    Navigation.findNavController(getView()).navigate((NavDirections) action);
                } else {
                    mainViewModel.setNewCardAdded(false);
                    ArrayList<CardListItem> petroCanadaCards = new ArrayList<>();
                    for (CardDetail cardDetail : carWashCardViewModel.getPetroCanadaCards().getValue()) {
                        petroCanadaCards.add(new CardListItem(getContext(), cardDetail));
                    }
                    petroCanadaCardsAdapter.setCards(petroCanadaCards);
                }
            }
        });

        carWashCardViewModel.getCardTypeStatus().observe(this, cardTypeStatus -> {
            String content;
            switch (cardTypeStatus) {
                case CARD_ONLY:
                    content = getString(R.string.carwash_getwash_message_card_only);
                    break;
                case TICKET_ONLY:
                    content = getString(R.string.carwash_getwash_message_ticket_only);
                    break;
                default:
                    content = getString(R.string.carwash_getwash_message_card_and_ticket);
                    break;
            }
            binding.carwashWashCards.descriptionCardContent.setText(content);
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        binding = FragmentCarWashBinding.inflate(inflater, container, false);
        binding.setVm(carWashCardViewModel);
        binding.setLifecycleOwner(this);
        binding.errorLayout.setModel(new GenericErrorView(getContext(), R.string.ok,
                () -> {
                    carWashCardViewModel.loadData(CarWashCardViewModel.ViewState.LOADING);
                    Alerts.prepareGeneralErrorDialog(getContext(), String.valueOf(R.string.carwash_cards)).show();
                }));

        binding.scrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (!isFirstTime) {
                int[] headerLocation = new int[2];
                int[] appBarLocation = new int[2];

                binding.carWashWelcomeMessage.getLocationInWindow(headerLocation);
                binding.appBar.getLocationInWindow(appBarLocation);
                int appBarBottom = appBarLocation[1] + binding.appBar.getMeasuredHeight();
                int headerBottom = headerLocation[1] +
                        binding.carWashWelcomeMessage.getMeasuredHeight()
                        - binding.carWashWelcomeMessage.getPaddingBottom();

                if (headerBottom <= appBarBottom) {
                    binding.appBar.setTitle(binding.carWashWelcomeMessage.getText());
                    ViewCompat.setElevation(binding.appBar, appBarElevation);
                    binding.appBar.findViewById(R.id.collapsed_title).setAlpha(
                            Math.min(1, (float) (appBarBottom - headerBottom) / 100));
                } else {
                    binding.appBar.setTitle("");
                    ViewCompat.setElevation(binding.appBar, 0);
                }
            } else {
                isFirstTime = false;
                binding.scrollView.scrollTo(0, 0);
            }
        });

        binding.appBar.setNavigationOnClickListener(v -> goBack());
        binding.refreshLayout.setColorSchemeResources(R.color.red);
        binding.refreshLayout.setOnRefreshListener(this);

        CardItemDecorator listDecorator = new CardItemDecorator(-getResources().getDimensionPixelSize(R.dimen.petro_canada_cards_padding));
        //UNCOMMENT THIS ONCE SINGLE TICKET IS IN SCOPE
        //binding.carwashWashCards.carWashGetMoreCard.setOnClickListener(buyTicketListener);
        binding.carwashWashCards.carWashCardsList.setAdapter(petroCanadaCardsAdapter);
        binding.carwashWashCards.carWashCardsList.addItemDecoration(listDecorator);
        binding.carwashWashCards.carWashCardsList.setNestedScrollingEnabled(false);
        binding.carwashWashCards.carWashCardsList.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                Drawable drawable = getActivity().getDrawable(R.drawable.petro_canada_card_background);
                drawable.setBounds(new Rect(0, 0, view.getWidth(), view.getHeight()));
                drawable.getOutline(outline);
            }
        });

        //setup no card click listener
        //binding.carwashNoCard.buyTicketButton.setOnClickListener(buyTicketListener);
        binding.carwashNoCard.buyTicketButton.setOnClickListener(addNewCardListener);

        //Setup nearest card click listeners
        nearestCardBinding = binding.carwashNearestCards;
        nearestCardBinding.tryAgainButton.setOnClickListener(getTryAgainLister());
        nearestCardBinding.directionsButton.setOnClickListener(openNavigationListener);
        nearestCardBinding.settingsButton.setOnClickListener(getOpenSettingListener());
        nearestCardBinding.getRoot().setOnClickListener(showCardDetail);
        return binding.getRoot();

    }

    @Override
    public void onStart() {
        super.onStart();
        isFirstTime = true;
    }

    /**
     * Called when a swipe gesture triggers a refresh.
     */
    @Override
    public void onRefresh() {
        carWashCardViewModel.loadData(CarWashCardViewModel.ViewState.REFRESHING);
    }

    @Override
    public void onResume() {
        super.onResume();
        AnalyticsUtils.setCurrentScreenName(getActivity(), String.valueOf(R.string.car_wash_card_list));
    }

    @Override
    public void onBackPressed() {
        goBack();
    }

    private void goBack() {
        Navigation.findNavController(getView()).popBackStack();
    }


    private void cardClick(CardDetail cardDetail) {
        if (carWashCardViewModel.getIsNearestStationIndependent().getValue() != null
                && carWashCardViewModel.getIsNearestStationIndependent().getValue()) {
            StationsUtil.showIndependentStationAlert(getContext());
        } else {
            navigateToCardDetail(cardDetail);
        }

    }

    private void reloadButtonClick(CardDetail cardDetail) {
        if (carWashCardViewModel.getIsNearestStationIndependent().getValue() != null
                && carWashCardViewModel.getIsNearestStationIndependent().getValue()) {
            StationsUtil.showIndependentStationAlert(getContext());
        }else if (cardDetail.isSuspendedCard()) {
             CardsUtil.showSuspendedCardAlert(getContext());
        }
        else if (carWashCardViewModel.getIsBalanceZero().getValue() != null &&
                carWashCardViewModel.getIsBalanceZero().getValue()) {
            navigateToReloadTransaction(cardDetail);
        } else if (cardDetail.getBalance() <= 0 || cardDetail.isExpiredCard()) {
            navigateToReloadTransaction(cardDetail);
        }

    }

    private void navigateToCardDetail(CardDetail cardDetail) {
        if (getView() == null) return;

        AnalyticsUtils.setCurrentScreenName(getActivity(), cardDetail.getFirebaseCarwashScreenName());
        CarWashCardFragmentDirections.ActionCarWashCardFragmentToCardsDetailsFragment action = CarWashCardFragmentDirections.actionCarWashCardFragmentToCardsDetailsFragment();
        action.setCardIndex(carWashCardViewModel.getIndexofCardDetail(cardDetail));
        action.setLoadType(CardsLoadType.CAR_WASH_PRODUCTS);
        NavController controller = Navigation.findNavController(getView());
        if (controller.getCurrentDestination() != null
                && controller.getCurrentDestination().getAction(action.getActionId()) != null) {
            controller.navigate((NavDirections) action);
        }
    }

    private void navigateToReloadTransaction(CardDetail cardDetail) {
        if (getView() == null) return;

        if (cardDetail.isSuspendedCard()) {
            CardsUtil.showSuspendedCardAlert(getContext());
        } else {
            //open Reload Transaction form
            CarWashCardFragmentDirections.ActionCarWashCardFragmentToCarWashTransactionFragment
                    action = CarWashCardFragmentDirections.actionCarWashCardFragmentToCarWashTransactionFragment();
            action.setCardNumber(cardDetail.getCardNumber());
            action.setCardName(cardDetail.getLongName());
            action.setCardType(cardDetail.getCardType().name());
            Navigation.findNavController(getView()).navigate(action);
        }
    }

    //UNCOMMENT THIS WHEN REDDEM/BUY SINGLE TICKET IS IN THE SCOPE
//    private View.OnClickListener buyTicketListener = v -> {
//        Navigation.findNavController(getView()).navigate(R.id.action_carWashCardFragment_to_carWashPurchaseFragment);
//    };

    private View.OnClickListener addNewCardListener = v -> {
        Navigation.findNavController(getView()).navigate(R.id.action_carWashCardFragment_to_addCardFragment);
    };

    private View.OnClickListener openNavigationListener = v -> {
        if (carWashCardViewModel.getNearestStation().getValue() != null) {
            Station station = carWashCardViewModel.getNearestStation().getValue().data.getStation();
            if (station != null) {
                NavigationAppsHelper.openNavigationApps(getActivity(), station);
            }
        }
    };

    private View.OnClickListener showCardDetail = v -> {
        Resource<StationItem> resource = carWashCardViewModel.getNearestStation().getValue();
        if (resource != null && resource.data != null && !carWashCardViewModel.getIsLoading().get()) {
            StationDetailsDialog.showCard(this, resource.data, nearestCardBinding.getRoot(), false);
        } else {
            AnalyticsUtils.logEvent(this.getContext(), AnalyticsUtils.Event.error,
                    new Pair<>(AnalyticsUtils.Param.errorMessage, resource.message));
        }
    };

}
