package suncor.com.android.ui.main.carwash;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Outline;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import javax.inject.Inject;

import suncor.com.android.LocationLiveData;
import suncor.com.android.R;
import suncor.com.android.databinding.CarwashNearestCardBinding;
import suncor.com.android.databinding.FragmentCarWashBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.model.Resource;
import suncor.com.android.model.cards.CardDetail;
import suncor.com.android.model.station.Station;
import suncor.com.android.ui.common.GenericErrorView;
import suncor.com.android.ui.common.OnBackPressedListener;
import suncor.com.android.ui.main.MainViewModel;
import suncor.com.android.ui.main.cards.list.CardItemDecorator;
import suncor.com.android.ui.main.cards.list.CardListItem;
import suncor.com.android.ui.main.cards.list.CardsListAdapter;
import suncor.com.android.ui.main.common.MainActivityFragment;
import suncor.com.android.ui.main.stationlocator.StationDetailsDialog;
import suncor.com.android.ui.main.stationlocator.StationItem;
import suncor.com.android.uicomponents.swiperefreshlayout.SwipeRefreshLayout;
import suncor.com.android.utilities.LocationUtils;
import suncor.com.android.utilities.NavigationAppsHelper;
import suncor.com.android.utilities.PermissionManager;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class CarWashCardFragment extends MainActivityFragment implements OnBackPressedListener,
        SwipeRefreshLayout.OnRefreshListener {

    private static final int REQUEST_CHECK_SETTINGS = 100;
    private static final int PERMISSION_REQUEST_CODE = 1;

    @Inject
    ViewModelFactory viewModelFactory;
    private FragmentCarWashBinding binding;
    private CarWashCardViewModel viewModel;
    private MainViewModel mainViewModel;
    private CardsListAdapter petroCanadaCardsAdapter;
    private float appBarElevation;

    private LocationLiveData locationLiveData;
    private CarwashNearestCardBinding nearestCardBinding;
    @Inject
    PermissionManager permissionManager;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appBarElevation = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
        mainViewModel = ViewModelProviders.of(getActivity(), viewModelFactory).get(MainViewModel.class);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(CarWashCardViewModel.class);
        petroCanadaCardsAdapter = new CardsListAdapter(this::cardClick);

        viewModel.getViewState().observe(this, (result) -> {
            if (mainViewModel.isLinkedToAccount()) {
                Log.i("TTT", "i am here");
            } else {
                Log.i("TTT", "i am here2");
            }
            if (result != CarWashCardViewModel.ViewState.REFRESHING) {
                binding.refreshLayout.setRefreshing(false);
            }

            if (result != CarWashCardViewModel.ViewState.REFRESHING && result != CarWashCardViewModel.ViewState.LOADING
                    && result != CarWashCardViewModel.ViewState.FAILED && viewModel.getIsCardAvailable().getValue()) {

                ArrayList<CardListItem> petroCanadaCards = new ArrayList<>();
                for (CardDetail cardDetail : viewModel.getPetroCanadaCards().getValue()) {
                    petroCanadaCards.add(new CardListItem(getContext(), cardDetail));
                }
                petroCanadaCardsAdapter.setCards(petroCanadaCards);
//                if(isLinkToAccount) {
//                    Log.i("TTT", "i am here");
//                    ((MainActivity)getActivity()).setLinkedToAccount(false);
//                    CarWashCardFragmentDirections.ActionCarWashCardFragmentToCardsDetailsFragment action = CarWashCardFragmentDirections.actionCarWashCardFragmentToCardsDetailsFragment();
//                    action.setCardIndex(0);
//                    action.setIsCardFromCarWash(true);
//                    Navigation.findNavController(getView()).navigate(action);
//                }
            }
        });

        locationLiveData = new LocationLiveData(getContext().getApplicationContext());
        viewModel.getLocationServiceEnabled().observe(this, (enabled -> {
            if (enabled) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED) {
                    viewModel.getIsLoading().set(viewModel.getUserLocation() == null);
                    locationLiveData.observe(getViewLifecycleOwner(), (location -> viewModel.setUserLocation(new LatLng(location.getLatitude(), location.getLongitude()))));
                }
            }
        }));

        viewModel.getRefreshLocationCard().observe(this, v -> {
            checkAndRequestPermission();
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCarWashBinding.inflate(inflater, container, false);
        binding.setVm(viewModel);
        binding.setLifecycleOwner(this);
        binding.errorLayout.setModel(new GenericErrorView(getContext(), R.string.msg_sl005_button,
                () -> viewModel.loadData(CarWashCardViewModel.ViewState.LOADING)));

        binding.scrollView.setOnScrollChangeListener(
                (NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
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
                });

        binding.appBar.setNavigationOnClickListener(v -> goBack());
        binding.refreshLayout.setColorSchemeResources(R.color.red);
        binding.refreshLayout.setOnRefreshListener(this);

        CardItemDecorator listDecorator = new CardItemDecorator(-getResources().getDimensionPixelSize(R.dimen.petro_canada_cards_padding));
        binding.carwashWashCards.carWashGetMoreCard.setOnClickListener(buyTicketListener);
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
        binding.carwashNoCard.buyTicketButton.setOnClickListener(buyTicketListener);

        //Setup nearest card click listeners
        nearestCardBinding = binding.carwashNearestCards;
        nearestCardBinding.tryAgainButton.setOnClickListener(tryAgainLister);
        nearestCardBinding.directionsButton.setOnClickListener(openNavigationListener);
        nearestCardBinding.settingsButton.setOnClickListener(openSettingListener);
        nearestCardBinding.getRoot().setOnClickListener(showCardDetail);

        return binding.getRoot();

    }

    @Override
    public void onStart() {
        super.onStart();
        viewModel.onAttached();
        checkAndRequestPermission();
    }

    /**
     * Called when a swipe gesture triggers a refresh.
     */
    @Override
    public void onRefresh() {
        viewModel.loadData(CarWashCardViewModel.ViewState.REFRESHING);
    }

    @Override
    public void onBackPressed() {
        goBack();
    }

    private void goBack() {
        Navigation.findNavController(getView()).popBackStack();
    }


    private void cardClick(CardDetail cardDetail) {
        CarWashCardFragmentDirections.ActionCarWashCardFragmentToCardsDetailsFragment action = CarWashCardFragmentDirections.actionCarWashCardFragmentToCardsDetailsFragment();
        action.setCardIndex(viewModel.getIndexofCardDetail(cardDetail));
        action.setIsCardFromCarWash(true);
        Navigation.findNavController(getView()).navigate(action);
    }

    private View.OnClickListener buyTicketListener = v -> {
        Navigation.findNavController(getView()).navigate(R.id.action_carWashCardFragment_to_carWashPurchaseFragment);
    };

    private View.OnClickListener tryAgainLister = v -> {
        if (viewModel.getUserLocation() != null) {
            viewModel.setUserLocation(viewModel.getUserLocation());
        } else {
            viewModel.setLocationServiceEnabled(LocationUtils.isLocationEnabled(getContext()));
        }
    };

    private View.OnClickListener openNavigationListener = v -> {
        if (viewModel.getNearestStation().getValue() != null) {
            Station station = viewModel.getNearestStation().getValue().data.getStation();
            if (station != null) {
                NavigationAppsHelper.openNavigationApps(getActivity(), station);
            }
        }
    };

    private View.OnClickListener showCardDetail = v -> {
        Resource<StationItem> resource = viewModel.getNearestStation().getValue();
        if (resource != null && resource.data != null && !viewModel.getIsLoading().get()) {
            StationDetailsDialog.showCard(this, resource.data, nearestCardBinding.getRoot(), false);
        }
    };

    private View.OnClickListener openSettingListener = v -> permissionManager.checkPermission(getContext(),
            Manifest.permission.ACCESS_FINE_LOCATION, new PermissionManager.PermissionAskListener() {
                @Override
                public void onNeedPermission() {
                    showRequestLocationDialog(false);
                }

                @Override
                public void onPermissionPreviouslyDenied() {
                    //in case in the future we would show any rational
                    showRequestLocationDialog(false);
                }

                @Override
                public void onPermissionPreviouslyDeniedWithNeverAskAgain() {
                    showRequestLocationDialog(true);
                }

                @Override
                public void onPermissionGranted() {
                    showRequestLocationDialog(false);
                }
            });

    private void showRequestLocationDialog(boolean previouselyDeniedWithNeverASk) {
        AlertDialog.Builder adb = new AlertDialog.Builder(getContext());
        adb.setTitle(R.string.enable_location_dialog_title);
        adb.setMessage(R.string.enable_location_dialog_message);
        adb.setNegativeButton(R.string.cancel, null);
        adb.setPositiveButton(R.string.ok, (dialog, which) -> {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PERMISSION_GRANTED && !LocationUtils.isLocationEnabled(getContext())) {
                LocationUtils.openLocationSettings(this, REQUEST_CHECK_SETTINGS);
                return;
            }

            permissionManager.setFirstTimeAsking(Manifest.permission.ACCESS_FINE_LOCATION, false);
            if (previouselyDeniedWithNeverASk) {
                PermissionManager.openAppSettings(getActivity());
            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_CODE);
            }
            dialog.dismiss();
        });
        AlertDialog alertDialog = adb.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PERMISSION_GRANTED) {
                if (LocationUtils.isLocationEnabled(getContext())) {
                    viewModel.setLocationServiceEnabled(true);
                } else {
                    LocationUtils.openLocationSettings(this, REQUEST_CHECK_SETTINGS);
                }

            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CHECK_SETTINGS && resultCode == Activity.RESULT_OK) {
            viewModel.setLocationServiceEnabled(true);

        }
    }

    private void checkAndRequestPermission() {
        permissionManager.checkPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION, new PermissionManager.PermissionAskListener() {
            @Override
            public void onNeedPermission() {
                if (!permissionManager.isAlertShown()) {
                    permissionManager.setAlertShown(true);
                    showRequestLocationDialog(false);
                }
            }

            @Override
            public void onPermissionPreviouslyDenied() {
                //in case in the future we would show any rational
            }

            @Override
            public void onPermissionPreviouslyDeniedWithNeverAskAgain() {
            }

            @Override
            public void onPermissionGranted() {
                viewModel.setLocationServiceEnabled(LocationUtils.isLocationEnabled(getContext()));
            }
        });
    }
}
