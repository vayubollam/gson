package suncor.com.android.ui.main.home;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.AutoTransition;
import androidx.transition.Slide;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;
import androidx.transition.TransitionSet;

import com.google.android.gms.maps.model.LatLng;

import javax.inject.Inject;

import suncor.com.android.HomeNavigationDirections;
import suncor.com.android.LocationLiveData;
import suncor.com.android.R;
import suncor.com.android.databinding.FragmentHomeGuestBinding;
import suncor.com.android.databinding.FragmentHomeSignedinBinding;
import suncor.com.android.databinding.HomeNearestCardBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.model.Resource;
import suncor.com.android.model.pap.ActiveSession;
import suncor.com.android.model.pap.FuelUp;
import suncor.com.android.model.station.Station;
import suncor.com.android.ui.common.Alerts;
import suncor.com.android.ui.common.webview.WebDialogFragment;
import suncor.com.android.ui.main.BottomNavigationFragment;
import suncor.com.android.ui.main.MainActivity;
import suncor.com.android.ui.main.MainViewModel;
import suncor.com.android.ui.main.pap.fuelup.FuelUpFragmentDirections;
import suncor.com.android.ui.main.pap.selectpump.SelectPumpFragmentDirections;
import suncor.com.android.ui.main.wallet.cards.CardsLoadType;
import suncor.com.android.ui.main.stationlocator.StationDetailsDialog;
import suncor.com.android.ui.main.stationlocator.StationItem;
import suncor.com.android.utilities.AnalyticsUtils;
import suncor.com.android.utilities.LocationUtils;
import suncor.com.android.utilities.NavigationAppsHelper;
import suncor.com.android.utilities.PermissionManager;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static suncor.com.android.utilities.Constants.*;

public class HomeFragment extends BottomNavigationFragment {

    private static final int REQUEST_CHECK_SETTINGS = 100;
    private static final int PERMISSION_REQUEST_CODE = 1;
    @Inject
    ViewModelFactory viewModelFactory;
    private HomeViewModel mViewModel;
    private MainViewModel mainViewModel;
    private OffersAdapter offersAdapter;
    private LocationLiveData locationLiveData;
    private boolean inAnimationShown;
    @Inject
    PermissionManager permissionManager;
    private HomeNearestCardBinding nearestCard;

    private boolean pingActiveSessionStarted = false;
    private Handler handler = new Handler();
    private FuelUp fuelUp;

    private OnClickListener tryAgainLister = v -> {
        if (mViewModel.getUserLocation() != null) {
            mViewModel.isLoading.set(true);
            mViewModel.setUserLocation(mViewModel.getUserLocation());
        } else {
            mViewModel.setLocationServiceEnabled(LocationUtils.isLocationEnabled(getContext()));
        }
    };

    private OnClickListener showCardDetail = v -> {
        Resource<StationItem> resource = mViewModel.nearestStation.getValue();


        if (resource != null && resource.data != null && !mViewModel.isLoading.get()) {
            resource.data.isFavourite = resource.data.favouriteRepository.isFavourite(resource.data.getStation());
            StationDetailsDialog.showCard(this, resource.data, nearestCard.getRoot(), false);
        }
    };

    private RecyclerView.OnScrollListener offersScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                int position = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
                OfferCard card = offersAdapter.getOffer(position);
                AnalyticsUtils.logPromotionEvent(getContext(), AnalyticsUtils.Event.VIEWITEM , position + "|" + card.getText(),card.getText(),card.getText(),position+"");
            }
        }
    };

    private boolean systemMarginsAlreadyApplied;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationLiveData = new LocationLiveData(getContext().getApplicationContext(), false, true);
        mViewModel = ViewModelProviders.of(this, viewModelFactory).get(HomeViewModel.class);
        mainViewModel = ViewModelProviders.of(getActivity(), viewModelFactory).get(MainViewModel.class);
        mViewModel.locationServiceEnabled.observe(this, (enabled -> {
            if (enabled) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED) {
                    mViewModel.isLoading.set(mViewModel.getUserLocation() == null);
                    locationLiveData.observe(getViewLifecycleOwner(), (location -> mViewModel.setUserLocation(new LatLng(location.getLatitude(), location.getLongitude()))));
                }
            }
        }));

        mViewModel.openNavigationApps.observe(this, event -> {
            Station station = event.getContentIfNotHandled();

            if (fuelUp != null && fuelUp.fuelUpAvailable()) {
                HomeNavigationDirections.ActionToSelectPumpFragment action =
                        HomeNavigationDirections.actionToSelectPumpFragment(
                                station.getId(),
                                getString(R.string.action_location, station.getAddress().getAddressLine())
                        );
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(action);
            } else if (station != null) {
                NavigationAppsHelper.openNavigationApps(getActivity(), station);
            }
        });

        mViewModel.dismissEnrollmentRewardsCardEvent.observe(this, event -> {
            if (event.getContentIfNotHandled() != null) {
                ConstraintLayout mainLayout = getView().findViewById(R.id.main_layout);
                TransitionSet set = new TransitionSet();
                Transition cardSlide = new Slide(Gravity.LEFT);
                cardSlide.addTarget(R.id.enrollment_greetings_card);
                set.addTransition(cardSlide);
                set.addTransition(new AutoTransition());
                TransitionManager.beginDelayedTransition(mainLayout, set);
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(mainLayout);
                constraintSet.setVisibility(R.id.enrollment_greetings_card, ConstraintSet.GONE);
                constraintSet.applyTo(mainLayout);
            }
        });

        mViewModel.navigateToPetroPoints.observe(this, event -> {
            if (event.getContentIfNotHandled() != null) {
                HomeFragmentDirections.ActionHomeTabToCardsDetailsFragment action = HomeFragmentDirections.actionHomeTabToCardsDetailsFragment();
                action.setLoadType(CardsLoadType.PETRO_POINT_ONLY);
                Navigation.findNavController(getView()).navigate(action);
            }
        });

        mViewModel.nearestCarWashStation.observeForever(resource -> {
            if (resource != null) {
                mainViewModel.setNearestStation(resource);
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view;
        if (!mViewModel.isUserLoggedIn()) {
            view = setupGuestLayout(inflater, container);
        } else {
            view = setupSignedInLayout(inflater, container);
        }

        //Setup nearest card click listeners
        nearestCard.getRoot().setOnClickListener(showCardDetail);
        nearestCard.tryAgainButton.setOnClickListener(tryAgainLister);

        nearestCard.settingsButton.setOnClickListener(v -> {
            permissionManager.checkPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION, new PermissionManager.PermissionAskListener() {
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
        });

        mViewModel.isPAPAvailable().observe(getViewLifecycleOwner(), value -> {
            this.fuelUp = value.data;

            nearestCard.mobilePaymentText.setVisibility(value.status == Resource.Status.LOADING ? View.INVISIBLE : View.VISIBLE);
            nearestCard.mobilePaymentProgressBar.setVisibility(value.status != Resource.Status.LOADING ? View.GONE : View.VISIBLE);

            nearestCard.mobilePaymentText.setText(value.data != null && value.data.papAvailable() ? R.string.mobile_payment_accepted : R.string.mobile_payment_not_accepted);
            nearestCard.imgMobilePayment.setImageResource(value.data != null && value.data.papAvailable() ? R.drawable.ic_check : R.drawable.ic_close);

            nearestCard.directionsButton.setText(value.data != null && value.data.fuelUpAvailable() ? R.string.action_fuel_up : R.string.station_directions_button);
        });

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private void startIntentToRevokeAutoPermission(){
        Intent intent = new Intent(Intent.ACTION_AUTO_REVOKE_PERMISSIONS,
                Uri.fromParts("package", requireActivity().getPackageName(), null));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        requireContext().startActivity(intent);
    }

    private View setupSignedInLayout(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        FragmentHomeSignedinBinding binding = FragmentHomeSignedinBinding.inflate(inflater, container, false);
        binding.setVm(mViewModel);
        binding.setLifecycleOwner(this);
        nearestCard = binding.nearestCard;
        nearestCard.getRoot().setOnClickListener((v) -> {
            if (mViewModel.nearestStation.getValue().data != null && !mViewModel.isLoading.get()) {
                if (binding.scrollView.getScrollY() > binding.nearestCard.getRoot().getTop() - 200) {
                    binding.scrollView.smoothScrollTo(0, binding.nearestCard.getRoot().getTop() - 200);
                    binding.scrollView.postDelayed(() -> {
                        StationDetailsDialog.showCard(this, mViewModel.nearestStation.getValue().data, nearestCard.getRoot(), false);
                    }, 100);
                } else {
                    StationDetailsDialog.showCard(this, mViewModel.nearestStation.getValue().data, nearestCard.getRoot(), false);
                }
            }
        });

        mViewModel.getDateDifference();
        offersAdapter = new OffersAdapter((MainActivity) getActivity(), true, mViewModel.isExpired);
        binding.offersRecyclerview.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        PagerSnapHelper helper = new PagerSnapHelper();
        helper.attachToRecyclerView(binding.offersRecyclerview);
        binding.offersRecyclerview.setAdapter(offersAdapter);
        binding.offersRecyclerview.addOnScrollListener(offersScrollListener);
        systemMarginsAlreadyApplied = false;
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            if (!systemMarginsAlreadyApplied) {
                systemMarginsAlreadyApplied = true;
                int systemsTopMargin = insets.getSystemWindowInsetTop();
                ((ViewGroup.MarginLayoutParams) binding.headerGreetings.getLayoutParams()).topMargin += systemsTopMargin;
                binding.headerGreetings.getParent().requestLayout();
            }
            return insets;
        });


        binding.scrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            int totalTranslation = binding.greetingBottomToCards.getHeight();
            View barrierView = binding.enrollmentGreetingsCard.getVisibility() != View.GONE ? binding.enrollmentGreetingsCard : binding.nearestCard.getRoot();
            float parallaxEffectValue = (float) totalTranslation / barrierView.getTop();
            float greetingsTranslation = Math.min(totalTranslation, scrollY * parallaxEffectValue);
            binding.headerGreetings.setTranslationY(greetingsTranslation);
            binding.headerPetropoints.setTranslationY(greetingsTranslation);
            binding.headerImage.setTranslationY((float) (scrollY * 0.5));
        });

        binding.fuellingSessionCard.setOnClickListener((view) -> {
            ActiveSession session = mViewModel.getCachedActiveSession();
            FuelUpFragmentDirections.ActionFuelUpToFuellingFragment action = FuelUpFragmentDirections.actionFuelUpToFuellingFragment(
                    session != null ? session.pumpNumber : "",
                    session != null? session.preAuthPoints: "",
                    session != null ? session.fuelUpAmount: "",
                    mViewModel.getPetroPointsBalance());
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(action);
        });
        binding.fuellingSessionCard.setLoading(true);
        binding.fuellingSessionCard.setRadius(8);
        startFuellingActiveSession();

        return binding.getRoot();
    }

    private View setupGuestLayout(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        FragmentHomeGuestBinding binding = FragmentHomeGuestBinding.inflate(inflater, container, false);
        binding.setVm(mViewModel);
        binding.setLifecycleOwner(this);
        binding.mainLayout.post(() -> {
            ConstraintLayout.LayoutParams privacyButtonParams = (ConstraintLayout.LayoutParams) binding.privacyPolicy.getLayoutParams();
            int _8dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8,    requireContext().getResources().getDisplayMetrics());
            binding.mainLayout.getLayoutParams().height = binding.scrollView.getHeight() + binding.privacyPolicy.getHeight() + privacyButtonParams.bottomMargin + _8dp;
            binding.mainLayout.requestLayout();
        });

        systemMarginsAlreadyApplied = false;
        ViewCompat.setOnApplyWindowInsetsListener(binding.mainLayout, (view, insets) -> {
            if (!systemMarginsAlreadyApplied) {
                systemMarginsAlreadyApplied = true;
                int systemsTopMargin = insets.getSystemWindowInsetTop();
                view.setPadding(view.getPaddingLeft(), view.getPaddingTop() + systemsTopMargin, view.getPaddingRight(), view.getPaddingBottom());
            }
            return insets;
        });

        nearestCard = binding.nearestCard;
        mViewModel.getDateDifference();
        offersAdapter = new OffersAdapter((MainActivity) getActivity(), false, mViewModel.isExpired);
        binding.offersRecyclerview.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        PagerSnapHelper helper = new PagerSnapHelper();
        helper.attachToRecyclerView(binding.offersRecyclerview);
        binding.offersRecyclerview.setAdapter(offersAdapter);
        binding.offersRecyclerview.addOnScrollListener(offersScrollListener);
        binding.privacyPolicy.setOnClickListener(v -> showDialog(getString(R.string.profile_about_privacy_policy_link), getString(R.string.profile_about_legal_header)));
        binding.termsConditions.setOnClickListener(v -> showDialog(getString(R.string.profile_about_legal_link), getString(R.string.profile_about_privacy_policy_header)));

        if (!inAnimationShown) {
            inAnimationShown = true;
            Animation animFromLet = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_right_home);
            animFromLet.setDuration(500);
            Animation animslideUp = AnimationUtils.loadAnimation(getContext(), R.anim.push_up_in);
            animslideUp.setDuration(500);
            binding.offersRecyclerview.startAnimation(animFromLet);
            nearestCard.getRoot().startAnimation(animslideUp);
            binding.termsConditions.startAnimation(animslideUp);
            binding.privacyPolicy.startAnimation(animslideUp);
        }

        return binding.getRoot();
    }

    void showDialog(String url, String header) {
        WebDialogFragment webDialogFragment = WebDialogFragment.newInstance(url, header);
        webDialogFragment.show(getFragmentManager(), WebDialogFragment.TAG);

        AnalyticsUtils.logEvent(getContext(), "intersite", new Pair<>("intersiteURL", url));
    }

    @Override
    protected boolean isFullScreen() {
        return true;
    }

    private void showRequestLocationDialog(boolean previouselyDeniedWithNeverASk) {
        AlertDialog.Builder adb = new AlertDialog.Builder(getContext());
        AnalyticsUtils.logEvent(getActivity().getApplicationContext(), ALERT,
                new Pair<>(ALERT_TITLE, getString(R.string.enable_location_dialog_title)+"("+getString(R.string.enable_location_dialog_message)+")"),
                new Pair<>(FORM_NAME,HOME)
        );
        adb.setTitle(R.string.enable_location_dialog_title);
        adb.setMessage(R.string.enable_location_dialog_message);
        adb.setNegativeButton(R.string.cancel, (dialog, which) -> {
            AnalyticsUtils.logEvent(getActivity().getApplicationContext(), ALERT_INTERACTION,
                    new Pair<>(ALERT_TITLE, getString(R.string.enable_location_dialog_title)+"("+getString(R.string.enable_location_dialog_message)+")"),
                    new Pair<>(ALERT_SELECTION, getString(R.string.cancel)),
                    new Pair<>(ALERT_TITLE, HOME),
                    new Pair<>(FORM_NAME, ALERT_SELECTION)
            );
        });
        adb.setPositiveButton(R.string.ok, (dialog, which) -> {
            AnalyticsUtils.logEvent(getActivity().getApplicationContext(), ALERT_INTERACTION,
                    new Pair<>(ALERT_TITLE, getString(R.string.enable_location_dialog_title)+"("+getString(R.string.enable_location_dialog_message)+")"),
                    new Pair<>(ALERT_SELECTION, getString(R.string.ok)),
                    new Pair<>(FORM_NAME, HOME)
            );
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
    public void onStart() {
        super.onStart();
        checkAndRequestPermission();

        if (mViewModel.isUserLoggedIn()) {
            getView().post(() -> {
                int flags = getActivity().getWindow().getDecorView().getSystemUiVisibility();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    flags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                }
                getActivity().getWindow().getDecorView().setSystemUiVisibility(flags);
            });
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        int flags = getActivity().getWindow().getDecorView().getSystemUiVisibility();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        }
        getActivity().getWindow().getDecorView().setSystemUiVisibility(flags);
    }

    @Override
    protected String getScreenName() {
        return "home";
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PERMISSION_GRANTED) {
                if (LocationUtils.isLocationEnabled(getContext())) {
                    mViewModel.setLocationServiceEnabled(true);
                } else {
                    LocationUtils.openLocationSettings(this, REQUEST_CHECK_SETTINGS);
                }

            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onLoginStatusChanged() {
        if (!this.isDetached()) {
            getFragmentManager().beginTransaction()
                    .detach(this)
                    .attach(this)
                    .commit();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CHECK_SETTINGS && resultCode == Activity.RESULT_OK) {
            mViewModel.setLocationServiceEnabled(true);

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopFuellingActiveSessionObserver();
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
                //do nothing
            }

            @Override
            public void onPermissionGranted() {
                mViewModel.setLocationServiceEnabled(LocationUtils.isLocationEnabled(getContext()));
            }
        });
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            mViewModel.getActiveSession().observe(getViewLifecycleOwner(), result -> {
                if (result.status == Resource.Status.LOADING) {
                } else if (result.status == Resource.Status.ERROR) {
                    AnalyticsUtils.logEvent(getContext(), AnalyticsUtils.Event.error,
                            new Pair<>(AnalyticsUtils.Param.errorMessage, SOMETHING_WRONG),
                            new Pair<>(AnalyticsUtils.Param.FORMNAME, PAY_AT_PUMP));
                    Alerts.prepareGeneralErrorDialog(getContext(), PAY_AT_PUMP).show();
                } else if (result.status == Resource.Status.SUCCESS && result.data != null) {
                    if (result.data.activeSession && result.data.status != null) {
                        if(result.data.status.equalsIgnoreCase(NEW) || result.data.status.equalsIgnoreCase(AUTHORIZED)){
                            AnalyticsUtils.logEvent(getContext(), AnalyticsUtils.Event.FORMSTEP,
                                    new Pair<>(AnalyticsUtils.Param.FORMSELECTION, getString(R.string.fuelling_about_to_begin)),
                                    new Pair<>(AnalyticsUtils.Param.FORMNAME, PAY_AT_PUMP));
                            mViewModel.updateFuellingSession(true, getString(R.string.fuelling_about_to_begin));
                        }
                        //handle processing and session end state
                        /*else if(result.data.status.equals("BeginFueling")){
                            AnalyticsUtils.logEvent(getContext(), AnalyticsUtils.Event.FORMSTEP,
                                    new Pair<>(AnalyticsUtils.Param.FORMSELECTION, getString(R.string.fueling_up)),
                                    new Pair<>(AnalyticsUtils.Param.FORMNAME, "Pay at Pump"));
                            mViewModel.updateFuellingSession(true, getString(R.string.fueling_up));
                        } */
                        else {
                            mViewModel.updateFuellingSession(true, getString(R.string.fueling_up));
                            AnalyticsUtils.logEvent(getContext(), AnalyticsUtils.Event.FORMSTEP,
                                    new Pair<>(AnalyticsUtils.Param.FORMSELECTION, getString(R.string.fueling_up)),
                                    new Pair<>(AnalyticsUtils.Param.FORMNAME, PAY_AT_PUMP));
                        }
                        if(pingActiveSessionStarted) {
                            observerFuellingActiveSession();
                        }
                    } else {
                        if (mViewModel.activeFuellingSession.get()) {
                            AnalyticsUtils.logEvent(getContext(), AnalyticsUtils.Event.FORMCOMPLETE,
                                    new Pair<>(AnalyticsUtils.Param.FORMSELECTION, FUELING_COMPLETE),
                                    new Pair<>(AnalyticsUtils.Param.FORMNAME, PAY_AT_PUMP));
                        }

                        mViewModel.updateFuellingSession(false, "");
                    }
                }
            });

        }
    };

    public void stopFuellingActiveSessionObserver() {
        pingActiveSessionStarted = false;
        handler.removeCallbacks(runnable);
    }

    public void startFuellingActiveSession() {
        pingActiveSessionStarted = true;
        handler.postDelayed(runnable, 0);
    }

    public void observerFuellingActiveSession() {
        pingActiveSessionStarted = true;
        handler.postDelayed(runnable, 5000);
    }

}

