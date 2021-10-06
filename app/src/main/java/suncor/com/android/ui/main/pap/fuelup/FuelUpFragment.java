package suncor.com.android.ui.main.pap.fuelup;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateUtils;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.databinding.ObservableBoolean;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;


import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.PaymentDataRequest;
import com.google.android.gms.wallet.PaymentsClient;
import com.kount.api.analytics.AnalyticsCollector;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import suncor.com.android.BuildConfig;
import suncor.com.android.HomeNavigationDirections;
import suncor.com.android.LocationLiveData;
import suncor.com.android.R;
import suncor.com.android.databinding.FragmentFuelUpBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.mfp.ErrorCodes;
import suncor.com.android.model.Resource;
import suncor.com.android.model.SettingsResponse;
import suncor.com.android.model.pap.P97StoreDetailsResponse;
import suncor.com.android.model.payments.PaymentDetail;
import suncor.com.android.ui.common.Alerts;
import suncor.com.android.ui.main.common.MainActivityFragment;
import suncor.com.android.googlepay.GooglePayUtils;
import suncor.com.android.ui.main.home.HomeViewModel;
import suncor.com.android.ui.main.pap.selectpump.SelectPumpAdapter;
import suncor.com.android.ui.main.pap.selectpump.SelectPumpHelpDialogFragment;
import suncor.com.android.ui.main.pap.selectpump.SelectPumpListener;
import suncor.com.android.ui.main.pap.selectpump.SelectPumpViewModel;
import suncor.com.android.ui.main.wallet.payments.list.PaymentListItem;
import suncor.com.android.uicomponents.dropdown.ExpandableViewListener;
import suncor.com.android.utilities.AnalyticsUtils;
import suncor.com.android.utilities.FingerprintManager;
import suncor.com.android.utilities.Timber;
import suncor.com.android.utilities.UserLocalSettings;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class FuelUpFragment extends MainActivityFragment implements ExpandableViewListener,
        FuelUpLimitCallbacks, SelectPumpListener, PaymentDropDownCallbacks, RedeemPointsDropDownAdapter.RedeemPointsCallback,ShowWarningPopupListener{

    // Arbitrarily-picked constant integer you define to track a request for payment data activity.
    private static final int LOAD_PAYMENT_DATA_REQUEST_CODE = 991;
    private NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.getDefault());

    private FragmentFuelUpBinding binding;
    private FuelUpViewModel viewModel;
    private SelectPumpViewModel selectPumpViewModel;
    private ObservableBoolean isLoading = new ObservableBoolean(false);
    private Double lastTransactionFuelUpLimit;
    SettingsResponse.Pap mPapData;
    PaymentDropDownAdapter paymentDropDownAdapter;
    RedeemPointsDropDownAdapter redeemPointsDropDownAdapter;
    private FuelLimitDropDownAdapter fuelLimitDropDownAdapter;
    private int isPreAuthChanges = 0;

    private SelectPumpAdapter adapter;

    private String pumpNumber;
    private String storeId;
    private String preAuth;
    private String userPaymentId;
    private String preAuthRedeemPoints = "0";
    private String selectedRadioButton = "No Redemption";
    private boolean isRedemptionChanges;

    // A client for interacting with the Google Pay API.
    private PaymentsClient paymentsClient;

    @Inject
    FingerprintManager fingerPrintManager;

    @Inject
    ViewModelFactory viewModelFactory;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(FuelUpViewModel.class);
        selectPumpViewModel = ViewModelProviders.of(this, viewModelFactory).get(SelectPumpViewModel.class);
        paymentsClient = GooglePayUtils.createPaymentsClient(getContext());

        LocationLiveData locationLiveData = new LocationLiveData(getContext().getApplicationContext());
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED) {
            locationLiveData.observe(this, result -> {
                viewModel.setUserLocation(new LatLng(result.getLatitude(), result.getLongitude()));
            });
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFuelUpBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(this);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        binding.setIsLoading(isLoading);
        binding.setVm(viewModel);

        binding.appBar.setNavigationOnClickListener(v -> goBack());
        binding.preauthorizeButton.setOnClickListener(v-> handleConfirmAndAuthorizedClick());

        binding.selectPumpLayout.appBar.setVisibility(View.GONE);
        binding.selectPumpLayout.layout.setVisibility(View.GONE);

        binding.pumpLayout.setOnClickListener(v -> {
            binding.selectPumpLayout.layout.setVisibility(binding.selectPumpLayout.layout.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
            if(binding.selectPumpLayout.layout.getVisibility() == View.VISIBLE) {
                binding.paymentExpandable.collapseExpanded();
                binding.redeemPointsExpandable.collapseExpanded();
                binding.fuelUpLimit.collapseExpanded();
            }
            else {
                binding.selectPumpLayout.layout.setVisibility(View.GONE);
            }
        });

        paymentDropDownAdapter = new PaymentDropDownAdapter(
                getContext(),
                this
        );

        binding.paymentExpandable.setDropDownData(paymentDropDownAdapter, false);


        HashMap<String, String> redeemPointsData = new HashMap<>();
        redeemPointsData.put("1", getString(R.string.no_redemption));
        redeemPointsData.put("2", getString(R.string.redeem_x_points));
        redeemPointsData.put("3", getString(R.string.other_amount));


        redeemPointsDropDownAdapter = new RedeemPointsDropDownAdapter(
                getContext(),
                redeemPointsData,
                viewModel.getPetroPoints(),
                this
        );

        binding.redeemPointsExpandable.setDropDownData(redeemPointsDropDownAdapter, true);

        adapter = new SelectPumpAdapter(this);
        binding.selectPumpLayout.pumpRecyclerView.setAdapter(adapter);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        storeId = FuelUpFragmentArgs.fromBundle(getArguments()).getStoreId();

        if (pumpNumber == null) {
            pumpNumber = FuelUpFragmentArgs.fromBundle(getArguments()).getPumpNumber();
        }

        binding.pumpNumberText.setText(pumpNumber);

        binding.fuelUpLimit.initListener(this);
        binding.paymentExpandable.initListener(this);
        binding.redeemPointsExpandable.initListener(this);


        binding.selectPumpLayout.helpButton.setOnClickListener(v -> {
            AnalyticsUtils.logEvent(getContext(), AnalyticsUtils.Event.infoTap,
                    new Pair<>(AnalyticsUtils.Param.infoText, "select pump number info"));
            showHelp();
        });

        selectPumpViewModel.getStoreDetails(storeId).observe(getViewLifecycleOwner(), storeDetailsResponseResource -> {
            if (storeDetailsResponseResource.status == Resource.Status.SUCCESS && storeDetailsResponseResource.data != null) {
                P97StoreDetailsResponse storeDetailsResponse = storeDetailsResponseResource.data;

                ArrayList<String> pumpNumbers = new ArrayList<String>();

                int index = 0;

                for (P97StoreDetailsResponse.PumpStatus pumpStatus: storeDetailsResponse.fuelService.pumpStatuses) {

                    if (pumpStatus.status.equals("Available")) {
                        pumpNumbers.add(String.valueOf(pumpStatus.pumpNumber));

                        if (String.valueOf(pumpStatus.pumpNumber).equals(pumpNumber)) {
                            adapter.setSelectedPos(index);
                        }
                        index++;
                    }
                }

                adapter.setPumpNumbers(pumpNumbers);
            }
        });

        viewModel.getActiveSession().observe(getViewLifecycleOwner(), result->{
            if (result.status == Resource.Status.LOADING) {
                AnalyticsUtils.setCurrentScreenName(getActivity(), "pay-at-pump-preauthorize-loading");
            } else if (result.status == Resource.Status.ERROR) {
                Alerts.prepareGeneralErrorDialog(getContext(), "Pay at Pump").show();
            } else if (result.status == Resource.Status.SUCCESS && result.data != null) {
                lastTransactionFuelUpLimit = result.data.lastFuelUpAmount;
                initializeFuelUpLimit();
                viewModel.saveLastStatusUpdate(result.data);
            }
        });

        viewModel.getSettingResponse().observe(getViewLifecycleOwner(), result -> {
            if (result.status == Resource.Status.LOADING) {
                //hideKeyBoard();
            } else if (result.status == Resource.Status.ERROR) {
                Alerts.prepareGeneralErrorDialog(getContext(), "Pay at Pump").show();
            } else if (result.status == Resource.Status.SUCCESS && result.data != null) {
                mPapData = result.data.getSettings().getPap();
                mPapData.getPreAuthLimits().put(String.valueOf(mPapData.getPreAuthLimits().size() + 1), getString(R.string.other_amount));
                initializeFuelUpLimit();
            }
        });

        viewModel.getPayments(getContext()).observe(getViewLifecycleOwner(), result -> {
            if (result.status == Resource.Status.LOADING) {
                //hideKeyBoard();
            } else if (result.status == Resource.Status.ERROR) {
                List<PaymentListItem> payments = new ArrayList<>();
                paymentDropDownAdapter.addPayments(payments);

                if (userPaymentId == null && payments.size() > 0)
                    this.userPaymentId = payments.get(0).getPaymentDetail().getId();

                paymentDropDownAdapter.setSelectedPos(userPaymentId);
                checkForGooglePayOptions();
                Alerts.prepareGeneralErrorDialog(getContext(),"Pay at Pump").show();
            } else if (result.status == Resource.Status.SUCCESS && result.data != null) {
                List<PaymentListItem> payments = result.data;
                paymentDropDownAdapter.addPayments(payments);

                if (userPaymentId == null && payments.size() > 0)
                    this.userPaymentId = payments.get(0).getPaymentDetail().getId();

                paymentDropDownAdapter.setSelectedPos(userPaymentId);
                checkForGooglePayOptions();

            }
        });

        binding.termsAgreement.setOnClickListener((v) -> {
            AnalyticsUtils.logEvent(getContext(), AnalyticsUtils.Event.intersite,
                    new Pair<>(AnalyticsUtils.Param.intersiteURL, getString(R.string.privacy_policy_url)));
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.privacy_policy_url)));
            startActivity(browserIntent);

        });

        NavController navController = NavHostFragment.findNavController(this);
        // We use a String here, but any type that can be put in a Bundle is supported
        MutableLiveData<PaymentDetail> liveData = navController.getCurrentBackStackEntry()
                .getSavedStateHandle()
                .getLiveData("tempPayment");

        liveData.observe(getActivity(), paymentDetail -> {
            // Do something with the result.
            paymentDropDownAdapter.addPayment(new PaymentListItem(getContext(), paymentDetail), true);
            this.userPaymentId = paymentDetail.getId();
        });

        // We use a String here, but any type that can be put in a Bundle is supported
        MutableLiveData<String> selectedPaymentLiveData = navController.getCurrentBackStackEntry()
                .getSavedStateHandle()
                .getLiveData("selectedPayment");

        selectedPaymentLiveData.observe(getActivity(), userPaymentSourceId -> {
            this.userPaymentId = userPaymentSourceId;

            // Do something with the result.
            paymentDropDownAdapter.setSelectedPos(userPaymentSourceId);
        });
    }

    private void initializeFuelUpLimit(){
       if (Objects.nonNull(mPapData) && Objects.nonNull(mPapData.getPreAuthLimits())) {
          // binding.totalAmount.setText(String.format("$%s", mPapData.getPreAuthLimits().get("1")));

            fuelLimitDropDownAdapter = new FuelLimitDropDownAdapter(
                   getContext(),
                   mPapData.getPreAuthLimits(),
                   this,
                   mPapData.getOtherAmountHighLimit(),
                   mPapData.getOtherAmountLowLimit(),
                    this
           );

           if (preAuth != null) {
               try {
                   fuelLimitDropDownAdapter.setSelectedPosfromValue(formatter.parse(preAuth).doubleValue());
               }catch (ParseException ex){
                   Timber.e(ex.getMessage());
               }
           }

           fuelLimitDropDownAdapter.findLastFuelUpTransaction(lastTransactionFuelUpLimit);

           binding.fuelUpLimit.setDropDownData(fuelLimitDropDownAdapter, false);

       }
    }

    private void showHelp() {
        DialogFragment fragment = new SelectPumpHelpDialogFragment();
        fragment.show(getFragmentManager(), "dialog");
    }

    @Override
    public void selectPumpNumber(String pumpNumber) {
        this.pumpNumber = pumpNumber;
        binding.pumpNumberText.setText(pumpNumber);
        AnalyticsUtils.logEvent(getContext(), AnalyticsUtils.Event.formStep, new Pair<>(AnalyticsUtils.Param.formName, "Pay at Pump"),
                new Pair<>(AnalyticsUtils.Param.formSelection, pumpNumber));
        new Handler().postDelayed(() -> binding.pumpLayout.callOnClick(), 400);
    }

    @Override
    public void onExpandCollapseListener(boolean isExpand, String cardTitle) {

    }

    @Override
    public void collapseManage(boolean isExpand, String cardTitle) {
        if (isExpand) {
            switch (cardTitle) {
                case "Fuel up to":
                case "Montant maximal du carburanto":
                    binding.paymentExpandable.collapseExpanded();
                    binding.redeemPointsExpandable.collapseExpanded();
                    binding.selectPumpLayout.layout.setVisibility(View.GONE);
                    break;
                case "Redeem Petro-Points":
                case "Échangez vos Petro-Points":
                    binding.paymentExpandable.collapseExpanded();
                    binding.fuelUpLimit.collapseExpanded();
                    binding.selectPumpLayout.layout.setVisibility(View.GONE);
                    break;
                case "Select payment method":
                case "Sélectionnez un mode de paiement":
                    binding.fuelUpLimit.collapseExpanded();
                    binding.redeemPointsExpandable.collapseExpanded();
                    binding.selectPumpLayout.layout.setVisibility(View.GONE);
                    break;
            }
        }
    }

    private void goBack() {
        Navigation.findNavController(getView()).popBackStack();
    }

    @Override
    public void onPreAuthChanged(String value) {
        if(value.equals(this.preAuth)) {
            return;
        }
        this.preAuth = value;
        binding.totalAmount.setText(value);
        redeemPointsDropDownAdapter.setPreAuthValue(preAuth);
        if( ++ isPreAuthChanges >1 && fuelLimitDropDownAdapter.isEditableValueChange()){
            redeemPointsDropDownAdapter.collapseIfPreAuthChanges(0);
            fuelLimitDropDownAdapter.setIsRedeem(false);
        preAuthRedeemPoints = "0";
        selectedRadioButton = "No Redemption";
        redeemPointsDropDownAdapter.notifyDataSetChanged();
        }
        AnalyticsUtils.logEvent(getContext(), AnalyticsUtils.Event.formStep, new Pair<>(AnalyticsUtils.Param.formName, "Pay at Pump"),
                new Pair<>(AnalyticsUtils.Param.formSelection, value));
    }

    @Override
    public void onPaymentChanged(String userPaymentId) {
        this.userPaymentId = userPaymentId;

        AnalyticsUtils.logEvent(getContext(), AnalyticsUtils.Event.formStep, new Pair<>(AnalyticsUtils.Param.formName, "Pay at Pump"),
                new Pair<>(AnalyticsUtils.Param.formSelection,userPaymentId.equals(PaymentDropDownAdapter.PAYMENT_TYPE_GOOGLE_PAY) ? PaymentDropDownAdapter.PAYMENT_TYPE_GOOGLE_PAY : "credit_card"));
    }


    private void checkForGooglePayOptions(){
        IsReadyToPayRequest request = viewModel.IsReadyToPayRequestForGooglePay();
        if(Objects.isNull(request )){
            return;
        }
        Task<Boolean> task = paymentsClient.isReadyToPay(request);
        // The call to isReadyToPay is asynchronous and returns a Task. We need to provide an
        // OnCompleteListener to be triggered when the result of the call is known.
        task.addOnCompleteListener(requireActivity(), (data) -> {
            if (data.isSuccessful() && data.getResult() != null && data.getResult()){
                paymentDropDownAdapter.addGooglePayOption(userPaymentId);
            } else {
                Log.w("isReadyToPay failed", task.getException());
            }
        });
    }

    private void handleConfirmAndAuthorizedClick() {
        AnalyticsUtils.logEvent(getContext(), AnalyticsUtils.Event.buttonTap, new Pair<>(AnalyticsUtils.Param.buttonText, getString(R.string.confirm_and_authorized).toLowerCase()));
        if (userPaymentId == null) {
            // select payment type error
            return;
        }
        if (userPaymentId.equals(PaymentDropDownAdapter.PAYMENT_TYPE_GOOGLE_PAY)) {
            verifyFingerPrints();
        } else if (preAuth != null) {
            try {
                String kountSessionId = generateKountSessionID();
                double preAuthPrices = formatter.parse(preAuth).doubleValue();
                viewModel.payByWalletRequest(storeId, Integer.parseInt(pumpNumber), preAuthPrices,Integer.parseInt(preAuthRedeemPoints), Integer.parseInt(userPaymentId), kountSessionId).observe(getViewLifecycleOwner(), result -> {
                    if (result.status == Resource.Status.LOADING) {
                        isLoading.set(true);
                        AnalyticsUtils.setCurrentScreenName(getActivity(), "pay-at-pump-preauthorize-loading");
                    } else if (result.status == Resource.Status.ERROR) {
                        isLoading.set(false);
                        handleAuthorizationFail(result.message);
                    } else if (result.status == Resource.Status.SUCCESS && result.data != null) {
                        isLoading.set(false);
                        AnalyticsUtils.logEvent(getContext(), AnalyticsUtils.Event.paymentPreauthorize,
                                new Pair<>(AnalyticsUtils.Param.paymentMethod, "Credit Card"),
                                new Pair<>(AnalyticsUtils.Param.checkboxInput, selectedRadioButton),
                                new Pair<>(AnalyticsUtils.Param.fuelAmountSelection, String.valueOf(preAuthPrices)));

                        FuelUpFragmentDirections.ActionFuelUpToFuellingFragment action = FuelUpFragmentDirections.actionFuelUpToFuellingFragment(pumpNumber, preAuthRedeemPoints);
                        Navigation.findNavController(getView()).popBackStack();
                        Navigation.findNavController(getView()).navigate(action);
                    }
                });
            } catch (ParseException ex) {
                Timber.e(ex.getMessage());
            }
        }
    }


    public void requestGooglePaymentTransaction() {
        try {
            double preAuthPrices = formatter.parse(preAuth).doubleValue();
            PaymentDataRequest request = viewModel.createGooglePayInitiationRequest(preAuthPrices,
                    BuildConfig.GOOGLE_PAY_MERCHANT_GATEWAY, mPapData.getP97TenantID());

            // Since loadPaymentData may show the UI asking the user to select a payment method, we use
            // AutoResolveHelper to wait for the user interacting with it. Once completed,
            // onActivityResult will be called with the result.
            if (request != null) {
                AutoResolveHelper.resolveTask(
                        paymentsClient.loadPaymentData(request),
                        requireActivity(), LOAD_PAYMENT_DATA_REQUEST_CODE);
            }
        } catch (ParseException ex){
            Timber.e(ex.getMessage());
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            // value passed in AutoResolveHelper
            case LOAD_PAYMENT_DATA_REQUEST_CODE:
                switch (resultCode) {

                    case Activity.RESULT_OK:
                        PaymentData paymentData = PaymentData.getFromIntent(data);
                        String paymentToken =  viewModel.handlePaymentSuccess(paymentData);
                        try {
                            requestPayByGooglePay(paymentToken);
                        }catch (ParseException ex){
                            Timber.e(ex.getMessage());
                        }

                        break;
                    case Activity.RESULT_CANCELED:
                        // The user cancelled the payment attempt
                        AnalyticsUtils.logEvent(getContext(), AnalyticsUtils.Event.error,
                                new Pair<>(AnalyticsUtils.Param.errorMessage, "Something went wrong on our side"),
                                new Pair<>(AnalyticsUtils.Param.detailMessage, "Google pay transaction cancel by user"),
                                new Pair<>(AnalyticsUtils.Param.formName, "Pay at Pump"));
                        break;

                    case AutoResolveHelper.RESULT_ERROR:
                        Status status = AutoResolveHelper.getStatusFromIntent(data);
                        AnalyticsUtils.logEvent(getContext(), AnalyticsUtils.Event.error,
                                new Pair<>(AnalyticsUtils.Param.errorMessage, "Something went wrong on our side"),
                                new Pair<>(AnalyticsUtils.Param.detailMessage, "Google Pay error , message" + status.getStatusMessage()),
                                new Pair<>(AnalyticsUtils.Param.formName, "Pay at Pump"));
                        Alerts.prepareGeneralErrorDialog(getContext(), "Pay at Pump").show();
                        break;
                }
        }
    }

    private void verifyFingerPrints(){
        if (fingerPrintManager.isFingerPrintExistAndEnrolled()) {
            BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                    .setTitle(getString(R.string.payment))
                    .setSubtitle(getString(R.string.google_pay))
                    .setDescription(getResources().getString(R.string.login_fingerprint_alert_desc))
                    .setNegativeButtonText(getResources().getString(R.string.login_fingerprint_alert_negative_button)).build();
            Executor executor = Executors.newSingleThreadExecutor();
            BiometricPrompt biometricPrompt = new BiometricPrompt(getActivity(), executor, new BiometricPrompt.AuthenticationCallback() {
                @Override
                public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                    super.onAuthenticationError(errorCode, errString);
                    AnalyticsUtils.logEvent(getContext(), AnalyticsUtils.Event.error,
                            new Pair<>(AnalyticsUtils.Param.errorMessage, "Something went wrong on our side"),
                            new Pair<>(AnalyticsUtils.Param.detailMessage, "Biometrics fails"),
                            new Pair<>(AnalyticsUtils.Param.formName, "Pay at Pump"));
                }

                @Override
                public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                    super.onAuthenticationSucceeded(result);
                    requestGooglePaymentTransaction();
                }

                @Override
                public void onAuthenticationFailed() {
                    super.onAuthenticationFailed();
                }
            });
            biometricPrompt.authenticate(promptInfo);
        } else {
            requestGooglePaymentTransaction();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        AnalyticsUtils.setCurrentScreenName(getActivity(), "pay-at-pump-preauthorize");
    }

    private void requestPayByGooglePay(String paymentToken) throws ParseException {
        double preAuthPrices = formatter.parse(preAuth).doubleValue();
        String kountSessionId = generateKountSessionID();
        viewModel.payByGooglePayRequest(storeId, Integer.parseInt(pumpNumber), preAuthPrices,Integer.parseInt(preAuthRedeemPoints), paymentToken, kountSessionId).observe(getViewLifecycleOwner(), result -> {
            if (result.status == Resource.Status.LOADING) {
                isLoading.set(true);
                AnalyticsUtils.setCurrentScreenName(getActivity(), "pay-at-pump-preauthorize-loading");
            } else if (result.status == Resource.Status.ERROR) {
                isLoading.set(false);
                handleAuthorizationFail(result.message);
            } else if (result.status == Resource.Status.SUCCESS && result.data != null) {
                isLoading.set(false);
                AnalyticsUtils.logEvent(getContext(), AnalyticsUtils.Event.paymentPreauthorize,
                        new Pair<>(AnalyticsUtils.Param.paymentMethod, "Google Pay"),
                        new Pair<>(AnalyticsUtils.Param.checkboxInput, selectedRadioButton),
                        new Pair<>(AnalyticsUtils.Param.fuelAmountSelection, String.valueOf(preAuthPrices)));

                FuelUpFragmentDirections.ActionFuelUpToFuellingFragment action = FuelUpFragmentDirections.actionFuelUpToFuellingFragment(pumpNumber, preAuthRedeemPoints);
                Navigation.findNavController(requireView()).popBackStack();
                Navigation.findNavController(requireView()).navigate(action);
            }
        });
    }

    private void handleAuthorizationFail(String errorCode){
        if(errorCode == null){
            return;
        }
        switch (errorCode.toUpperCase()){
            case ErrorCodes.ERR_TRANSACTION_FAILS:
                AnalyticsUtils.logEvent(getContext(), AnalyticsUtils.Event.error,
                        new Pair<>(AnalyticsUtils.Param.errorMessage, "Something went wrong on our side"),
                        new Pair<>(AnalyticsUtils.Param.detailMessage, "Transaction fails, errorCode : " + errorCode),
                        new Pair<>(AnalyticsUtils.Param.formName, "Pay at Pump"));

                 transactionFailsAlert(getContext()).show();
                break;
            case ErrorCodes.ERR_PUMP_RESERVATION_FAILS:
                AnalyticsUtils.logEvent(getContext(), AnalyticsUtils.Event.error,
                        new Pair<>(AnalyticsUtils.Param.errorMessage, "Something went wrong on our side"),
                        new Pair<>(AnalyticsUtils.Param.detailMessage, "Pump Registration fails, errorCode : " + errorCode),
                        new Pair<>(AnalyticsUtils.Param.formName, "Pay at Pump"));
                pumpReservationFailsAlert(getContext()).show();
                break;
            default:
                AnalyticsUtils.logEvent(getContext(), AnalyticsUtils.Event.error,
                        new Pair<>(AnalyticsUtils.Param.errorMessage, "Something went wrong on our side"),
                        new Pair<>(AnalyticsUtils.Param.detailMessage, "Something went wrong on our side, errorCode : " + errorCode),
                        new Pair<>(AnalyticsUtils.Param.formName, "Pay at Pump"));
                Alerts.prepareGeneralErrorDialog(getContext(), "Pay at Pump").show();
                break;
        }
    }

    public void showUpdatePopup(){
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity())
                .setTitle(R.string.fuel_up_alert_heading )
                .setMessage( R.string.fuel_up_alert_description)
                .setNegativeButton(R.string.payment_failed_cancel, ((dialog, i) -> {
                    dialog.dismiss();
                }))

                .setPositiveButton(R.string.fuel_up_alert_update_button, (dialog, which) -> {
                    isRedemptionChanges = false;
                    fuelLimitDropDownAdapter.onRedeemChanged(false);
                    binding.fuelUpLimit.expandCollapse();
                    dialog.dismiss();
                });

        builder.show();
    }

    private  AlertDialog transactionFailsAlert(Context context) {

        String analyticsName = context.getString( R.string.payment_failed_title)
                + "(" + context.getString(R.string.payment_failed_message) + ")";
        AnalyticsUtils.logEvent(context, AnalyticsUtils.Event.alert,
                new Pair<>(AnalyticsUtils.Param.alertTitle, analyticsName),
                new Pair<>(AnalyticsUtils.Param.formName, "Pay at Pump")
        );

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(R.string.payment_failed_title )
                .setMessage( R.string.payment_failed_message)
                .setNegativeButton(R.string.payment_failed_cancel, ((dialog, i) -> {
                    AnalyticsUtils.logEvent(context, AnalyticsUtils.Event.alertInteraction,
                            new Pair<>(AnalyticsUtils.Param.alertTitle, analyticsName),
                            new Pair<>(AnalyticsUtils.Param.alertSelection, context.getString(R.string.payment_failed_cancel)),
                            new Pair<>(AnalyticsUtils.Param.formName, "Pay at Pump")
                    );
                    dialog.dismiss();
                }))

                .setPositiveButton(R.string.try_agian, (dialog, which) -> {
                    verifyFingerPrints();
                    AnalyticsUtils.logEvent(context, AnalyticsUtils.Event.alertInteraction,
                            new Pair<>(AnalyticsUtils.Param.alertTitle, analyticsName),
                            new Pair<>(AnalyticsUtils.Param.alertSelection, context.getString(R.string.try_agian)),
                            new Pair<>(AnalyticsUtils.Param.formName, "Pay at Pump"));
                    dialog.dismiss();
                });
        return builder.create();
    }


    private  AlertDialog pumpReservationFailsAlert(Context context) {
        String analyticsName = context.getString( R.string.pump_unavailable_title)
                + "(" + context.getString(R.string.pump_unavailable_message) + ")";
        AnalyticsUtils.logEvent(context, AnalyticsUtils.Event.alert,
                new Pair<>(AnalyticsUtils.Param.alertTitle, analyticsName),
                new Pair<>(AnalyticsUtils.Param.formName, "Pay at Pump")
        );

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(R.string.pump_unavailable_title )
                .setMessage(  R.string.pump_unavailable_message)
                .setPositiveButton(R.string.ok, ((dialog, i) -> {
                    dialog.dismiss();
                    binding.selectPumpLayout.layout.setVisibility(binding.selectPumpLayout.layout.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
                    AnalyticsUtils.logEvent(context, AnalyticsUtils.Event.alertInteraction,
                            new Pair<>(AnalyticsUtils.Param.alertTitle, analyticsName),
                            new Pair<>(AnalyticsUtils.Param.alertSelection, context.getString(R.string.ok)),
                            new Pair<>(AnalyticsUtils.Param.formName, "Pay at Pump"));

                }));
        return builder.create();
    }

    @Override
    public void onRedeemPointsChanged(String redeemPoints, String selectedRadioButton, boolean isRedemptionChanged) {
        preAuthRedeemPoints = redeemPoints;
        this.selectedRadioButton = selectedRadioButton;
        this.isRedemptionChanges = isRedemptionChanged;
        fuelLimitDropDownAdapter.onRedeemChanged(isRedemptionChanges);
        fuelLimitDropDownAdapter.isWarningAlertVisible(isRedemptionChanged);
    }

    @Override
    public void onRedeemSelectionChanged() {
        showUpdatePopup();
    }
}
