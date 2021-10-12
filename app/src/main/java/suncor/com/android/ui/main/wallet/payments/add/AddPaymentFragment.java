package suncor.com.android.ui.main.wallet.payments.add;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.google.android.gms.maps.model.LatLng;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.inject.Inject;

import suncor.com.android.R;
import suncor.com.android.LocationLiveData;
import suncor.com.android.databinding.FragmentAddPaymentBinding;
import suncor.com.android.databinding.HomeNearestCardBinding;
import suncor.com.android.databinding.LayoutNoLocationBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.model.Resource;
import suncor.com.android.model.payments.PaymentDetail;
import suncor.com.android.ui.common.Alerts;
import suncor.com.android.ui.common.OnBackPressedListener;
import suncor.com.android.ui.main.common.MainActivityFragment;
import suncor.com.android.ui.main.home.HomeViewModel;
import suncor.com.android.utilities.AnalyticsUtils;
import suncor.com.android.utilities.LocationUtils;
import suncor.com.android.utilities.PermissionManager;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class AddPaymentFragment extends MainActivityFragment implements OnBackPressedListener {

    private static final int REQUEST_CHECK_SETTINGS = 100;
    private static final int PERMISSION_REQUEST_CODE = 1;

    private FragmentAddPaymentBinding binding;
    private AddPaymentViewModel viewModel;
    private LayoutNoLocationBinding layoutNoLocationBinding;

    private LocationLiveData locationLiveData;

    private ObservableBoolean isWebViewLoading = new ObservableBoolean();
    private ObservableBoolean isAdding = new ObservableBoolean(false);

    @Inject
    PermissionManager permissionManager;

    @Inject
    ViewModelFactory viewModelFactory;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(AddPaymentViewModel.class);
        AnalyticsUtils.logEvent(getContext(), AnalyticsUtils.Event.formStart, new Pair<>(AnalyticsUtils.Param.formName, "Credit Card Added"));
        locationLiveData = new LocationLiveData(getContext().getApplicationContext());

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddPaymentBinding.inflate(inflater, container, false);
        binding.setVm(viewModel);
        binding.setLifecycleOwner(this);
        layoutNoLocationBinding = binding.noLocationCard;

        binding.appBar.setNavigationOnClickListener(v -> onBackPressed());

        initWebView();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (permissionManager.shouldAskPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
            viewModel.setLocationServiceTitle(getString(R.string.card_enable_gps_title));
            viewModel.setLocationServiceMessage(getString(R.string.card_enable_gps_message));
        } else {
            viewModel.setLocationServiceTitle(getString(R.string.card_enable_location_title));
            viewModel.setLocationServiceMessage(getString(R.string.card_enable_location_message));
        }

        fetchAddPaymentEndpoint();
        layoutNoLocationBinding.settingsButton.setOnClickListener(v -> {
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
    }

    @Override
    public void onStart() {
        super.onStart();
        checkAndRequestPermission();
    }

    private void fetchAddPaymentEndpoint() {
        boolean inTransaction = AddPaymentFragmentArgs.fromBundle(getArguments()).getInTransaction();
        viewModel.locationServiceLiveData.observe(getViewLifecycleOwner(), (enabled -> {
            if (enabled) {
                locationLiveData.observe(getViewLifecycleOwner(), result -> {
                    Log.i(AddPaymentFragment.class.getSimpleName(), "location changes");
                });
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED) {

                    Transformations.switchMap(locationLiveData, result -> {
                        viewModel.setUserLocation(new LatLng(result.getLatitude(), result.getLongitude()));
                        String kountSessionId = generateKountSessionID();
                        return viewModel.getAddPaymentEndpoint(inTransaction, kountSessionId);
                    }).observe(getViewLifecycleOwner(), result -> {
                        if (result.status == Resource.Status.LOADING) {
                            binding.setIsAdding(isAdding);
                            binding.setIsWebviewLoading(isWebViewLoading);
                            //hideKeyBoard();
                        } else if (result.status == Resource.Status.ERROR) {
                            Alerts.prepareGeneralErrorDialog(getContext(), "Credit Card Added").show();
                        } else if (result.status == Resource.Status.SUCCESS && result.data != null) {
                            binding.webView.loadUrl(result.data.toString());
                        }
                    });
                }
            }
        }));
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        AnalyticsUtils.setCurrentScreenName(this.getActivity(), "my-petro-points-wallet-add-credit-card");
        binding.webView.getSettings().setJavaScriptEnabled(true);
        binding.webView.clearCache(true);
        isWebViewLoading.set(true);

        binding.webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                isWebViewLoading.set(false);

                if (url.toLowerCase().contains(viewModel.redirectUrl.toLowerCase())) {
                    isAdding.set(true);
                    Uri uri = Uri.parse(url);

                    String isSingleUse = uri.getQueryParameter("isSingleUse");
                    String userPaymentSourceId = uri.getQueryParameter("userPaymentSourceId");

                    if (isSingleUse != null && isSingleUse.equals("Y")) {
                        String cardName = uri.getQueryParameter("cardName");
                        String lastFour = uri.getQueryParameter("lastFour");
                        String exp = uri.getQueryParameter("expMonth") + "/" + uri.getQueryParameter("expYear");

                        PaymentDetail paymentDetail = new PaymentDetail();
                        paymentDetail.setId(userPaymentSourceId);
                        paymentDetail.setCardNumber(lastFour);
                        paymentDetail.setPaymentType(cardName);

                        SimpleDateFormat format = new SimpleDateFormat("MM/yy", Locale.CANADA);
                        SimpleDateFormat toFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CANADA);

                        try {
                            paymentDetail.setExpDate(toFormat.format(format.parse(exp)));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        Navigation.findNavController(getView()).getPreviousBackStackEntry().getSavedStateHandle().set("tempPayment", paymentDetail);
                    } else {
                        Navigation.findNavController(getView()).getPreviousBackStackEntry().getSavedStateHandle().set("selectedPayment", userPaymentSourceId);
                    }
                    AnalyticsUtils.logEvent(getContext(), AnalyticsUtils.Event.formComplete,
                            new Pair<>(AnalyticsUtils.Param.formSelection, "Credit Card"),
                            new Pair<>(AnalyticsUtils.Param.formName, "Credit Card Added"));


                    new Handler().postDelayed(() -> {
                        goBack();
                    }, 50);
                }
            }
        });
    }

    private void goBack() {
        Navigation.findNavController(getView()).getPreviousBackStackEntry().getSavedStateHandle().set("fromPayment", true);
        Navigation.findNavController(getView()).popBackStack();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CHECK_SETTINGS && resultCode == Activity.RESULT_OK) {
            viewModel.setLocationServiceEnabled(true);
        }
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

    private void checkAndRequestPermission() {
        permissionManager.checkPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION, new PermissionManager.PermissionAskListener() {
            @Override
            public void onNeedPermission() {
                if (!permissionManager.isAlertShown()) {
                    permissionManager.setAlertShown(true);
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
                viewModel.setLocationServiceEnabled(LocationUtils.isLocationEnabled(getActivity()));
            }
        });
    }


    private void showRequestLocationDialog(boolean previouselyDeniedWithNeverASk) {
        AlertDialog.Builder adb = new AlertDialog.Builder(getContext());
        AnalyticsUtils.logEvent(getActivity().getApplicationContext(),AnalyticsUtils.Event.alert,
                new Pair<>(AnalyticsUtils.Param.alertTitle, getString(R.string.enable_location_dialog_title)+"("+getString(R.string.enable_location_dialog_message)+")"),
                new Pair<>(AnalyticsUtils.Param.formName, "Credit Card Added")
        );
        adb.setTitle(R.string.enable_location_dialog_title);
        adb.setMessage(R.string.enable_location_dialog_message);
        adb.setNegativeButton(R.string.cancel, (dialog, which) -> {
            AnalyticsUtils.logEvent(getActivity().getApplicationContext(), AnalyticsUtils.Event.alertInteraction,
                    new Pair<>(AnalyticsUtils.Param.alertTitle, getString(R.string.enable_location_dialog_title)+"("+getString(R.string.enable_location_dialog_message)+")"),
                    new Pair<>(AnalyticsUtils.Param.alertSelection, getString(R.string.cancel)),
                    new Pair<>(AnalyticsUtils.Param.formName, "Credit Card Added")
            );
        });
        adb.setPositiveButton(R.string.ok, (dialog, which) -> {
            AnalyticsUtils.logEvent(getActivity().getApplicationContext(), AnalyticsUtils.Event.alertInteraction,
                    new Pair<>(AnalyticsUtils.Param.alertTitle, getString(R.string.enable_location_dialog_title)+"("+getString(R.string.enable_location_dialog_message)+")"),
                    new Pair<>(AnalyticsUtils.Param.alertSelection, getString(R.string.ok)),
                    new Pair<>(AnalyticsUtils.Param.formName, "Credit Card Added")
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
    public void onBackPressed() {
        goBack();
    }
}
