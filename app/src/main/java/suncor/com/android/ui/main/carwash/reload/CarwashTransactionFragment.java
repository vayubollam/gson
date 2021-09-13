package suncor.com.android.ui.main.carwash.reload;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.googlepay.GooglePayUtils;
import suncor.com.android.model.Resource;
import suncor.com.android.model.SettingsResponse;
import suncor.com.android.model.cards.CardDetail;
import suncor.com.android.model.carwash.reload.TransactionProduct;
import suncor.com.android.model.carwash.reload.TransactionReloadTaxes;
import suncor.com.android.model.payments.PaymentDetail;
import suncor.com.android.ui.common.Alerts;
import suncor.com.android.ui.common.OnBackPressedListener;
import suncor.com.android.ui.main.common.MainActivityFragment;
import suncor.com.android.ui.main.pap.fuelup.FuelUpFragmentArgs;
import suncor.com.android.ui.main.pap.fuelup.PaymentDropDownAdapter;
import suncor.com.android.ui.main.pap.fuelup.PaymentDropDownCallbacks;
import suncor.com.android.ui.main.pap.selectpump.SelectPumpHelpDialogFragment;
import suncor.com.android.ui.main.wallet.cards.details.ExpandedCardItem;
import suncor.com.android.ui.main.wallet.payments.list.PaymentListItem;
import suncor.com.android.uicomponents.dropdown.ExpandableViewListener;
import suncor.com.android.utilities.AnalyticsUtils;
import suncor.com.android.utilities.FingerprintManager;
import suncor.com.android.utilities.Timber;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class CarwashTransactionFragment extends MainActivityFragment implements ExpandableViewListener,
        PaymentDropDownCallbacks, CardReloadValuesDropDownAdapter.CardReloadValuesCallbacks, CardsDropDownAdapter.CardCallbacks,
        OnBackPressedListener {

    // Arbitrarily-picked constant integer you define to track a request for payment data activity.
    private static final int LOAD_PAYMENT_DATA_REQUEST_CODE = 991;
    private NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.getDefault());

    private FragmentCarwashTransactionBinding binding;
    private CarwashTransactionViewModel viewModel;
    private ObservableBoolean isLoading = new ObservableBoolean(false);
    private Double lastTransactionValue;
    SettingsResponse.Pap papData;
    PaymentDropDownAdapter paymentDropDownAdapter;

    private String cardNumber;
    private String userPaymentId;
    private String cardType;

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
            } else if (result.status == Resource.Status.SUCCESS && result.data != null) {
                papData = result.data.getSettings().getPap();
            }
        });

        viewModel.getPayments(getContext()).observe(getViewLifecycleOwner(), result -> {
            if (result.status == Resource.Status.LOADING) {
            } else if (result.status == Resource.Status.ERROR) {
                List<PaymentListItem> payments = result.data;
                payments = new ArrayList<>();
                paymentDropDownAdapter.addPayments(payments);

                if (userPaymentId == null && payments.size() > 0)
                    this.userPaymentId = payments.get(0).getPaymentDetail().getId();

                paymentDropDownAdapter.setSelectedPos(userPaymentId);
                checkForGooglePayOptions();
                Alerts.prepareGeneralErrorDialog(getContext(),"Pump PreAuthorized").show();
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
        if(viewModel.getTransactionReloadData() != null){
            setCardReloadAdapter();
        } else {
            viewModel.getTransactionData(cardType).observe(getViewLifecycleOwner(), result -> {
                if (result.status == Resource.Status.LOADING) {
                } else if (result.status == Resource.Status.ERROR) {
                    Alerts.prepareGeneralErrorDialog(getContext(), "").show();
                } else if (result.status == Resource.Status.SUCCESS && result.data != null) {
                    viewModel.setTransactionReloadData(result.data);
                    viewModel.setSelectedProduct(result.data.getDefaultSelectProduct(cardType));
                    setCardReloadAdapter();
                }
            });
        }

    }

    private void fetchTaxValues(){
        viewModel.fetchTaxValues(viewModel.getSelectedProduct().getRewardId(), viewModel.getUserProvince()).observe(getViewLifecycleOwner(), result -> {
            if (result.status == Resource.Status.LOADING) {
                binding.loadingProgressBar.setVisibility(View.VISIBLE);
            } else if (result.status == Resource.Status.ERROR) {
                binding.loadingProgressBar.setVisibility(View.GONE);
                Alerts.prepareGeneralErrorDialog(getContext(), "", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Navigation.findNavController(getView()).popBackStack();
                    }
                }).show();
            } else if (result.status == Resource.Status.SUCCESS && result.data != null) {
                binding.loadingProgressBar.setVisibility(View.GONE);
                viewModel.setTransactionReloadTax(result.data);
                calculateTax();
            }
        });
    }

    private void calculateTax(){
        if(Objects.isNull(viewModel.getTransactionReloadTax()) || Objects.isNull(viewModel.getSelectedProduct())) {
           return;
         }
         Double totalTax = viewModel.getTransactionReloadTax().getTotalTax(Double.valueOf(viewModel.getSelectedValuesAmount()));
         binding.taxAmount.setText(formatter.format(totalTax));
         binding.totalAmount.setText(formatter.format(viewModel.getSelectedValuesAmount() + totalTax));
    }

    private void setCardReloadAdapter(){
        CardReloadValuesDropDownAdapter adapter = new CardReloadValuesDropDownAdapter(
                getContext(),
                viewModel.getTransactionReloadData().getProducts(),
                this, cardType, viewModel.getSelectedProduct().getUnits()
        );
        binding.valuesLayout.setDropDownData(adapter);
    }

    private void initializeCards(){
        viewModel.getcards().observe(getViewLifecycleOwner(), result -> {
            if (result.status == Resource.Status.LOADING) {
            } else if (result.status == Resource.Status.ERROR) {
            } else if (result.status == Resource.Status.SUCCESS && result.data != null) {
                List<CardDetail> cards = result.data;
                List<ExpandedCardItem> selectCards = new ArrayList<>();
                cards.forEach(card -> {
                    if(card.getCardType().name().equals(cardType) && !card.isSuspendedCard()){
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
                           Navigation.findNavController(getView()).popBackStack();
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

        AnalyticsUtils.logEvent(getContext(), AnalyticsUtils.Event.formStep, new Pair<>(AnalyticsUtils.Param.formName, "Pump PreAuthorized"),
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

    private void handleConfirmAndAuthorizedClick(){
        AnalyticsUtils.logEvent(getContext(), AnalyticsUtils.Event.buttonTap, new Pair<>(AnalyticsUtils.Param.buttonText, getString(R.string.confirm_and_authorized).toLowerCase()));
        if(userPaymentId == null) {
            // select payment type error
            return;
        }
        if(userPaymentId.equals(PaymentDropDownAdapter.PAYMENT_TYPE_GOOGLE_PAY)){
            verifyFingerPrints();
        } else {

        }
    }


    public void requestGooglePaymentTransaction() {
        try {
            //todo change value
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
        }catch (ParseException ex){
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
                      //  String paymentToken =  viewModel.handlePaymentSuccess(paymentData);


                        break;
                    case Activity.RESULT_CANCELED:
                        // The user cancelled the payment attempt
                      break;

                    case AutoResolveHelper.RESULT_ERROR:
                        Status status = AutoResolveHelper.getStatusFromIntent(data);

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
                            new Pair<>(AnalyticsUtils.Param.errorMessage, "Biometrics fails"),
                            new Pair<>(AnalyticsUtils.Param.formName, "Pump PreAuthorized"));
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
       viewModel.setSelectedProduct(product);
       viewModel.setSelectedValuesAmount(value);
       fetchTaxValues();
    }
}
