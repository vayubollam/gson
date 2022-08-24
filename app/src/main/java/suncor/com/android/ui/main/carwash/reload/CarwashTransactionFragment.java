package suncor.com.android.ui.main.carwash.reload;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

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
import suncor.com.android.LocationLiveData;
import suncor.com.android.R;
import suncor.com.android.databinding.FragmentCarwashTransactionBinding;
import suncor.com.android.databinding.FragmentFuelUpBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.googlepay.GooglePayUtils;
import suncor.com.android.mfp.ErrorCodes;
import suncor.com.android.model.Resource;
import suncor.com.android.model.SettingsResponse;
import suncor.com.android.model.cards.CardDetail;
import suncor.com.android.model.carwash.reload.TransactionProduct;
import suncor.com.android.model.carwash.reload.TransactionReloadData;
import suncor.com.android.model.pap.P97StoreDetailsResponse;
import suncor.com.android.model.payments.PaymentDetail;
import suncor.com.android.ui.common.Alerts;
import suncor.com.android.ui.common.OnBackPressedListener;
import suncor.com.android.ui.main.carwash.CarWashActivationSecurityFragmentArgs;
import suncor.com.android.ui.main.common.MainActivityFragment;
import suncor.com.android.ui.main.pap.fuelup.FuelLimitDropDownAdapter;
import suncor.com.android.ui.main.pap.fuelup.FuelUpFragmentArgs;
import suncor.com.android.ui.main.pap.fuelup.FuelUpFragmentDirections;
import suncor.com.android.ui.main.pap.fuelup.FuelUpViewModel;
import suncor.com.android.ui.main.pap.fuelup.PaymentDropDownAdapter;
import suncor.com.android.ui.main.pap.fuelup.PaymentDropDownCallbacks;
import suncor.com.android.ui.main.pap.selectpump.SelectPumpAdapter;
import suncor.com.android.ui.main.pap.selectpump.SelectPumpHelpDialogFragment;
import suncor.com.android.ui.main.pap.selectpump.SelectPumpListener;
import suncor.com.android.ui.main.pap.selectpump.SelectPumpViewModel;
import suncor.com.android.ui.main.wallet.cards.details.ExpandedCardItem;
import suncor.com.android.ui.main.wallet.payments.list.PaymentListItem;
import suncor.com.android.uicomponents.dropdown.ExpandableViewListener;
import suncor.com.android.utilities.AnalyticsUtils;
import suncor.com.android.utilities.FingerprintManager;
import suncor.com.android.utilities.Timber;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static suncor.com.android.utilities.Constants.PUMP_PRE_AUTHORIZED;

public class CarwashTransactionFragment extends MainActivityFragment implements ExpandableViewListener,
        PaymentDropDownCallbacks, CardReloadValuesDropDownAdapter.CardReloadValuesCallbacks, CardsDropDownAdapter.CardCallbacks, OnBackPressedListener {


    // Arbitrarily-picked constant integer you define to track a request for payment data activity.
    private static final int LOAD_PAYMENT_DATA_REQUEST_CODE = 991;
    private final NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.getDefault());

    private FragmentCarwashTransactionBinding binding;
    private CarwashTransactionViewModel viewModel;
    private final ObservableBoolean isLoading = new ObservableBoolean(false);
    private Double lastTransactionValue;
    SettingsResponse.Pap papData;
    PaymentDropDownAdapter paymentDropDownAdapter;

    private String cardNumber;
    private String userPaymentId;
    private String cardType;
    private Handler handler;

    // A client for interacting with the Google Pay API.
    private PaymentsClient paymentsClient;

    @Inject
    FingerprintManager fingerPrintManager;

    @Inject
    ViewModelFactory viewModelFactory;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(CarwashTransactionViewModel.class);
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
        binding = FragmentCarwashTransactionBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(this);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        binding.setIsLoading(isLoading);
        if (getArguments() != null) {
            String cardName = CarwashTransactionFragmentArgs.fromBundle(getArguments()).getCardName();
            cardNumber = CarwashTransactionFragmentArgs.fromBundle(getArguments()).getCardNumber();
            cardType = CarwashTransactionFragmentArgs.fromBundle(getArguments()).getCardType();
            viewModel.setCardNumber(cardNumber);
            viewModel.setCardName(cardName);
        }
        binding.setVm(viewModel);
        binding.appBar.setNavigationOnClickListener(v -> goBack());
        binding.preauthorizeButton.setOnClickListener(v-> handleConfirmAndAuthorizedClick());

        paymentDropDownAdapter = new PaymentDropDownAdapter(
                getContext(),
                this
        );

        binding.paymentExpandable.setDropDownData(paymentDropDownAdapter);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (cardNumber == null) {
            cardNumber = FuelUpFragmentArgs.fromBundle(getArguments()).getPumpNumber();
        }

        binding.paymentExpandable.initListener(this);
        initializeCards();
        initializeCardsValues();

        viewModel.getSettings().observe(getViewLifecycleOwner(), result -> {
            if (result.status == Resource.Status.LOADING) {
            } else if (result.status == Resource.Status.ERROR) {
                AnalyticsUtils.logEvent(getContext(), AnalyticsUtils.Event.error,
                        new Pair<>(AnalyticsUtils.Param.errorMessage, result.message));
            } else if (result.status == Resource.Status.SUCCESS && result.data != null) {
                papData = result.data.getSettings().getPap();
            }
        });

        viewModel.getPayments(getContext()).observe(getViewLifecycleOwner(), result -> {
            if (result.status == Resource.Status.LOADING) {
                //hideKeyBoard();
            } else if (result.status == Resource.Status.ERROR) {
                AnalyticsUtils.logEvent(getContext(), AnalyticsUtils.Event.error,
                        new Pair<>(AnalyticsUtils.Param.errorMessage, result.message));
                List<PaymentListItem> payments = result.data;
                payments = new ArrayList<>();
                paymentDropDownAdapter.addPayments(payments);

                if (userPaymentId == null && payments.size() > 0)
                    this.userPaymentId = payments.get(0).getPaymentDetail().getId();

                paymentDropDownAdapter.setSelectedPos(userPaymentId);
                checkForGooglePayOptions();
                Alerts.prepareGeneralErrorDialog(getContext(),PUMP_PRE_AUTHORIZED).show();
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

    private void initializeCardsValues(){
//        if(viewModel.getTransactionReloadData() != null){
//            setCardReloadAdapter();
//        } else {
            viewModel.getTransactionData(cardType).observe(getViewLifecycleOwner(), result -> {
                if (result.status == Resource.Status.LOADING) {
                    //hideKeyBoard();
                } else if (result.status == Resource.Status.ERROR) {
                    AnalyticsUtils.logEvent(getContext(), AnalyticsUtils.Event.error,
                            new Pair<>(AnalyticsUtils.Param.errorMessage, result.message));
                    Alerts.prepareGeneralErrorDialog(getContext(), PUMP_PRE_AUTHORIZED).show();
                } else if (result.status == Resource.Status.SUCCESS && result.data != null) {
                    viewModel.setTransactionReloadData(result.data);
                    viewModel.setLastSelectedValue(cardType.equals("SP") ? "90" : "5");
                    setCardReloadAdapter();
                }
            });
        //}

    }

    private void fetchTaxValues(){
        viewModel.fetchTaxValues(viewModel.getSelectedProduct().getRewardId(), viewModel.getUserProvince()).observe(getViewLifecycleOwner(), result -> {
            if (result.status == Resource.Status.LOADING) {
                binding.loadingProgressBar.setVisibility(View.VISIBLE);
            } else if (result.status == Resource.Status.ERROR) {
                binding.loadingProgressBar.setVisibility(View.GONE);
                Alerts.prepareGeneralErrorDialog(getContext(), "", (dialog, which) -> Navigation.findNavController(getView()).popBackStack()).show();
            } else if (result.status == Resource.Status.SUCCESS && result.data != null) {
                binding.loadingProgressBar.setVisibility(View.GONE);
                viewModel.setTransactionReloadTax(result.data);
                calculateTax();
            }
        });
    }


    private void calculateTax() {
        if (Objects.isNull(viewModel.getTransactionReloadTax()) || Objects.isNull(viewModel.getSelectedProduct())) {
            return;
        }
        Double totalTax = viewModel.getTransactionReloadTax().getTotalTax(viewModel.getSelectedValuesAmount());
        viewModel.setTotalAmount(viewModel.getSelectedValuesAmount() + totalTax);
        binding.taxAmount.setText(formatter.format(totalTax));
        binding.totalAmount.setText(formatter.format(viewModel.getSelectedValuesAmount() + totalTax));
    }
    private void setCardReloadAdapter(){
        try {
            CardReloadValuesDropDownAdapter adapter = new CardReloadValuesDropDownAdapter(
                    getContext(),
                    viewModel.getTransactionReloadData().getProducts(),
                    this, cardType, viewModel.getSelectedProduct().getUnits()
            );
            binding.valuesLayout.setDropDownData(adapter);
            binding.loadingProgressBar.setVisibility(View.GONE);
        }catch (Exception e){
            binding.loadingProgressBar.setVisibility(View.GONE);
            Alerts.prepareGeneralErrorDialog(getContext(), "carwash_transaction_form").show();
            Timber.d("Exception");
        }

    }

    private void initializeCards(){
        viewModel.getcards().observe(getViewLifecycleOwner(), result -> {
            if (result.status == Resource.Status.LOADING) {
            } else if (result.status == Resource.Status.ERROR) {
                AnalyticsUtils.logEvent(getContext(), AnalyticsUtils.Event.error,
                        new Pair<>(AnalyticsUtils.Param.errorMessage, result.message));
            } else if (result.status == Resource.Status.SUCCESS && result.data != null) {
                List<CardDetail> cards = result.data;
                List<ExpandedCardItem> selectCards = new ArrayList<>();
                cards.forEach(card -> {
                    if (card.getCardType().name().equals(cardType)) {
                        selectCards.add(new ExpandedCardItem(getContext(), card));
                    }
                });
                CardsDropDownAdapter adapter = new CardsDropDownAdapter(
                        getContext(),
                        selectCards,
                        this, viewModel.cardNumber
                );
                binding.cardsLayout.setDropDownData(adapter);
                if(selectCards.size() == 1){
                    binding.cardsLayout.hideExpandDropdownButton();
                }
            }
        });
    }

    private void showHelp() {
        DialogFragment fragment = new SelectPumpHelpDialogFragment();
        fragment.show(getFragmentManager(), "dialog");
    }

    @Override
    public void onExpandCollapseListener(boolean isExpand, String cardTitle) {
      //  AnalyticsUtils.logEvent(getContext(), AnalyticsUtils.Event.infoTab,
          //      new Pair<>(AnalyticsUtils.Param.infoText, cardTitle));
    }

    private void goBack() {
        Alerts.prepareCustomDialog(getActivity(), getString(R.string.transacation_back_alert_title),
                getString(R.string.transacation_back_alert_msg), getString(R.string.transacation_back_alert_leave),getString(R.string.transacation_back_alert_cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Navigation.findNavController(requireView()).popBackStack();
                    }
                }, "carwash_transaction_form"
        ).show();
    }

    @Override
    public void onBackPressed() {
        goBack();
    }

    @Override
    public void onPaymentChanged(String userPaymentId) {
        this.userPaymentId = userPaymentId;

        AnalyticsUtils.logEvent(getContext(), AnalyticsUtils.Event.FORMSTEP, new Pair<>(AnalyticsUtils.Param.FORMNAME, PUMP_PRE_AUTHORIZED),
                new Pair<>(AnalyticsUtils.Param.FORMSELECTION,userPaymentId.equals(PaymentDropDownAdapter.PAYMENT_TYPE_GOOGLE_PAY) ? PaymentDropDownAdapter.PAYMENT_TYPE_GOOGLE_PAY : "credit_card"));
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

    private void handleConfirmAndAuthorizedClick(){
        AnalyticsUtils.logEvent(getContext(), AnalyticsUtils.Event.BUTTONTAP, new Pair<>(AnalyticsUtils.Param.buttonText, getString(R.string.confirm_and_authorized).toLowerCase()));
        if(userPaymentId == null || Objects.isNull(viewModel.getTransactionReloadTax())) {
            // select payment type error
            return;
        }
        if(userPaymentId.equals(PaymentDropDownAdapter.PAYMENT_TYPE_GOOGLE_PAY)){
            verifyFingerPrints();
        } else {
            try {
                String kountSessionId = generateKountSessionID();
                Double totalAmount = viewModel.getTotalAmount();
                viewModel.payByWalletRequest(cardType, totalAmount, kountSessionId, Integer.parseInt(userPaymentId)).observe(getViewLifecycleOwner(), result -> {
                    if (result.status == Resource.Status.LOADING) {
                        isLoading.set(true);
                        AnalyticsUtils.setCurrentScreenName(requireActivity(), "pay-at-pump-preauthorize-loading");
                    } else if (result.status == Resource.Status.ERROR) {
                        isLoading.set(false);
                        handleAuthorizationFail(result.message);
                    } else if (result.status == Resource.Status.SUCCESS && result.data != null) {
                        showPaymentSuccessfulDialogAndNavigateToReceipt();
                        AnalyticsUtils.logEvent(getContext(), AnalyticsUtils.Event.paymentPreauthorize,
                                new Pair<>(AnalyticsUtils.Param.paymentMethod, "Credit Card"),
                                new Pair<>(AnalyticsUtils.Param.fuelAmountSelection, String.valueOf(totalAmount)));
                    }
                });
            } catch (Exception ex) {
                Timber.e(Objects.requireNonNull(ex.getMessage()));
            }
        }
    }

    private void showPaymentSuccessfulDialogAndNavigateToReceipt(){
      viewModel.refreshCards();
      binding.authorizedProgressbar.setVisibility(View.GONE);
      binding.progressbarImage.setVisibility(View.GONE);
      binding.paymentSuccessImage.setVisibility(View.VISIBLE);
      binding.progressbarText.setText(R.string.payment_successful);
      if(handler == null){
          handler =  new Handler(Looper.getMainLooper());
      }
      handler.postDelayed(() -> {
          isLoading.set(false);
          Navigation.findNavController(requireView()).popBackStack();
          CarwashTransactionFragmentDirections.ActionTransactionToReceiptFragment action
                  = CarwashTransactionFragmentDirections.actionTransactionToReceiptFragment();
          action.setCardType(cardType);
          action.setTotalAmount(formatter.format(viewModel.getTotalAmount()));
          action.setUserName(viewModel.getUserName());
          Navigation.findNavController(getView()).navigate(action);

      }, 2000);
    }


//    public void requestGooglePaymentTransaction() {
//        try {
//            //change value
//            double preAuthPrices = formatter.parse("1").doubleValue();
//            PaymentDataRequest request = viewModel.createGooglePayInitiationRequest(preAuthPrices,
//                    BuildConfig.GOOGLE_PAY_MERCHANT_GATEWAY, papData.getP97TenantID());
//
//            // Since loadPaymentData may show the UI asking the user to select a payment method, we use
//            // AutoResolveHelper to wait for the user interacting with it. Once completed,
//            // onActivityResult will be called with the result.
//            if (request != null) {
//                AutoResolveHelper.resolveTask(
//                        paymentsClient.loadPaymentData(request),
//                        requireActivity(), LOAD_PAYMENT_DATA_REQUEST_CODE);
//            }
//        }catch (ParseException ex){
//            Timber.e(ex.getMessage());
//        }
//
//    }



    public void requestGooglePaymentTransaction() {
        try {
            //change value
            double preAuthPrices = formatter.parse("1").doubleValue();
            PaymentDataRequest request = viewModel.createGooglePayInitiationRequest(preAuthPrices,
                    BuildConfig.GOOGLE_PAY_MERCHANT_GATEWAY, papData.getP97TenantID());

            // Since loadPaymentData may show the UI asking the user to select a payment method, we use
            // AutoResolveHelper to wait for the user interacting with it. Once completed,
            // onActivityResult will be called with the result.
            if (request != null) {
                AutoResolveHelper.resolveTask(
                        paymentsClient.loadPaymentData(request),
                        requireActivity(), LOAD_PAYMENT_DATA_REQUEST_CODE);
            }
        } catch (Exception ex) {
            Timber.e(ex.getMessage());
        }
    }


        private void handleAuthorizationFail(String errorCode){
        if(errorCode == null){
            return;
        }
        if (ErrorCodes.ERR_TRANSACTION_FAILS_CARWASH.equals(errorCode.toUpperCase())) {
            AnalyticsUtils.logEvent(getContext(), AnalyticsUtils.Event.error,
                    new Pair<>(AnalyticsUtils.Param.errorMessage, "Something went wrong on our side"),
                    new Pair<>(AnalyticsUtils.Param.detailMessage, "Transaction fails, errorCode : " + errorCode),
                    new Pair<>(AnalyticsUtils.Param.FORMNAME, "carwash_transaction_form"));

            transactionFailsAlert(getContext()).show();
        } else {
            AnalyticsUtils.logEvent(getContext(), AnalyticsUtils.Event.error,
                    new Pair<>(AnalyticsUtils.Param.errorMessage, "Something went wrong on our side"),
                    new Pair<>(AnalyticsUtils.Param.detailMessage, "Something went wrong on our side, errorCode : " + errorCode),
                    new Pair<>(AnalyticsUtils.Param.FORMNAME, "carwash_transaction_form"));
            Alerts.prepareGeneralErrorDialog(getContext(), "carwash_transaction_form").show();
        }
    }

    private AlertDialog transactionFailsAlert(Context context) {

        String analyticsName = context.getString(R.string.payment_failed_title)
                + "(" + context.getString(R.string.payment_failed_message) + ")";
        AnalyticsUtils.logEvent(context, AnalyticsUtils.Event._ALERT,
                new Pair<>(AnalyticsUtils.Param.alertTitle, analyticsName),
                new Pair<>(AnalyticsUtils.Param.FORMNAME, "carwash_transaction_form")
        );

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(R.string.payment_failed_title)
                .setMessage(R.string.payment_failed_message)
                .setNegativeButton(R.string.payment_failed_cancel, ((dialog, i) -> {
                    AnalyticsUtils.logEvent(context, AnalyticsUtils.Event.alertInteraction,
                            new Pair<>(AnalyticsUtils.Param.alertTitle, analyticsName),
                            new Pair<>(AnalyticsUtils.Param.alertSelection, context.getString(R.string.payment_failed_cancel)),
                            new Pair<>(AnalyticsUtils.Param.FORMNAME, "carwash_transaction_form")
                    );
                    dialog.dismiss();
                }))

                .setPositiveButton(R.string.try_agian, (dialog, which) -> {
                    handleConfirmAndAuthorizedClick();
                    AnalyticsUtils.logEvent(context, AnalyticsUtils.Event.alertInteraction,
                            new Pair<>(AnalyticsUtils.Param.alertTitle, analyticsName),
                            new Pair<>(AnalyticsUtils.Param.alertSelection, context.getString(R.string.try_agian)),
                            new Pair<>(AnalyticsUtils.Param.FORMNAME,"carwash_transaction_form"));
                    dialog.dismiss();
                });
        return builder.create();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            // value passed in AutoResolveHelper
            case LOAD_PAYMENT_DATA_REQUEST_CODE:
                switch (resultCode) {

                    case Activity.RESULT_OK:
                        PaymentData paymentData = PaymentData.getFromIntent(Objects.requireNonNull(data));
                        String paymentToken =  viewModel.handlePaymentSuccess(paymentData);
                        payByGooglePayRequestToServer(paymentToken);
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user cancelled the payment attempt
                        // The user cancelled the payment attempt
                    /*    AnalyticsUtils.logEvent(getContext(), AnalyticsUtils.Event.error,
                                new Pair<>(AnalyticsUtils.Param.errorMessage, "Google pay transaction cancel by user"),
                                new Pair<>(AnalyticsUtils.Param.formName, "Pump PreAuthorized"));*/
                        break;

                    case AutoResolveHelper.RESULT_ERROR:
                        Status status = AutoResolveHelper.getStatusFromIntent(data);//                       AnalyticsUtils.logEvent(getContext(), AnalyticsUtils.Event.error,
//                                new Pair<>(AnalyticsUtils.Param.errorMessage, "Google Pay error , message" + status.getStatusMessage()),
//                                new Pair<>(AnalyticsUtils.Param.formName, "Pump PreAuthorized"));
//                        Alerts.prepareGeneralErrorDialog(getContext(), "Pump PreAuthorized").show();
                        break;
                }
        }
    }

    private void payByGooglePayRequestToServer(String paymentToken){
        try {
            String kountSessionId = generateKountSessionID();
            Double totalAmount = viewModel.getTotalAmount();
            viewModel.payByGooglePayRequest(cardType, totalAmount, kountSessionId, paymentToken).observe(getViewLifecycleOwner(), result -> {
                if (result.status == Resource.Status.LOADING) {
                    isLoading.set(true);
                    AnalyticsUtils.setCurrentScreenName(requireActivity(), "pay-at-pump-preauthorize-loading");
                } else if (result.status == Resource.Status.ERROR) {
                    isLoading.set(false);
                    handleAuthorizationFail(result.message);
                } else if (result.status == Resource.Status.SUCCESS && result.data != null) {
                    showPaymentSuccessfulDialogAndNavigateToReceipt();
                    AnalyticsUtils.logEvent(getContext(), AnalyticsUtils.Event.paymentPreauthorize,
                            new Pair<>(AnalyticsUtils.Param.paymentMethod, "Google Pay"),
                            new Pair<>(AnalyticsUtils.Param.fuelAmountSelection, String.valueOf(totalAmount)));
                }
            });
        } catch (Exception ex) {
            Timber.e(Objects.requireNonNull(ex.getMessage()));
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
            BiometricPrompt biometricPrompt = new BiometricPrompt(requireActivity(), executor, new BiometricPrompt.AuthenticationCallback() {
                @Override
                public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                    super.onAuthenticationError(errorCode, errString);
                    AnalyticsUtils.logEvent(getContext(), AnalyticsUtils.Event.error,
                            new Pair<>(AnalyticsUtils.Param.errorMessage, "Biometrics fails"),
                            new Pair<>(AnalyticsUtils.Param.FORMNAME, "Pump PreAuthorized"));
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
    }

    @Override
    public void onSelectCardChanged(String cardId, String cardNumber) {
      viewModel.setCardNumber(cardNumber);
    }

    @Override
    public void onValueChanged(Double value, TransactionProduct product) {
        binding.totalAmount.setText(formatter.format(value));
        viewModel.setSelectedProduct(product);
        viewModel.setSelectedValuesAmount(value);
        fetchTaxValues();
    }


}
