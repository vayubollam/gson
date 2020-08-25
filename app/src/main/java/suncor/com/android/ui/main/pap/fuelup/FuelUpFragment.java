package suncor.com.android.ui.main.pap.fuelup;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import suncor.com.android.R;
import suncor.com.android.databinding.FragmentFuelUpBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.model.Resource;
import suncor.com.android.model.SettingsResponse;
import suncor.com.android.model.pap.P97StoreDetailsResponse;
import suncor.com.android.model.payments.PaymentDetail;
import suncor.com.android.ui.common.Alerts;
import suncor.com.android.ui.main.common.MainActivityFragment;
import suncor.com.android.ui.main.pap.selectpump.SelectPumpAdapter;
import suncor.com.android.ui.main.pap.selectpump.SelectPumpFragmentArgs;
import suncor.com.android.ui.main.pap.selectpump.SelectPumpListener;
import suncor.com.android.ui.main.pap.selectpump.SelectPumpViewModel;
import suncor.com.android.ui.main.wallet.payments.list.PaymentListItem;
import suncor.com.android.uicomponents.dropdown.ExpandableViewListener;

public class FuelUpFragment extends MainActivityFragment implements ExpandableViewListener, FuelUpLimitCallbacks, SelectPumpListener {

    private FragmentFuelUpBinding binding;
    private FuelUpViewModel viewModel;
    private SelectPumpViewModel selectPumpViewModel;
    private ObservableBoolean isLoading = new ObservableBoolean(true);
    private Double lastTransactionFuelUpLimit;
    SettingsResponse.Pap mPapData;
    PaymentDropDownAdapter paymentDropDownAdapter;

    private SelectPumpAdapter adapter;

    private String pumpNumber;
    private String storeId;
    private String preAuth;


    @Inject
    ViewModelFactory viewModelFactory;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(FuelUpViewModel.class);
        selectPumpViewModel = ViewModelProviders.of(this, viewModelFactory).get(SelectPumpViewModel.class);
      //  AnalyticsUtils.logEvent(getContext(), AnalyticsUtils.Event.formStart, new Pair<>(AnalyticsUtils.Param.formName, "Select Pump"));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFuelUpBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(this);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        binding.setIsLoading(isLoading);

        binding.appBar.setNavigationOnClickListener(v -> goBack());
        binding.preauthorizeButton.setOnClickListener(v-> {});

        binding.selectPumpLayout.appBar.setVisibility(View.GONE);
        binding.selectPumpLayout.layout.setVisibility(View.GONE);

        binding.pumpLayout.setOnClickListener(v -> {
            binding.selectPumpLayout.layout.setVisibility(binding.selectPumpLayout.layout.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
        });

        paymentDropDownAdapter = new PaymentDropDownAdapter(
                getContext()
        );

        binding.paymentExpandable.setDropDownData(paymentDropDownAdapter);

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

            } else if (result.status == Resource.Status.ERROR) {
                Alerts.prepareGeneralErrorDialog(getContext()).show();
            } else if (result.status == Resource.Status.SUCCESS && result.data != null) {
                lastTransactionFuelUpLimit = result.data.getLastFuelUpAmount();
                initializeFuelUpLimit();
            }
        });

        viewModel.getSettingResponse().observe(getViewLifecycleOwner(), result -> {
            if (result.status == Resource.Status.LOADING) {
                //hideKeyBoard();
            } else if (result.status == Resource.Status.ERROR) {
                Alerts.prepareGeneralErrorDialog(getContext()).show();
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
                Alerts.prepareGeneralErrorDialog(getContext()).show();
            } else if (result.status == Resource.Status.SUCCESS && result.data != null) {
                List<PaymentListItem> payments = result.data;
                paymentDropDownAdapter.addPayments(payments);
            }
        });

        binding.termsAgreement.setOnClickListener((v) -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.privacy_policy_url)));
            startActivity(browserIntent);

        });
        binding.preauthorizeButton.setOnClickListener((v) ->{});

        NavController navController = NavHostFragment.findNavController(this);
        // We use a String here, but any type that can be put in a Bundle is supported
        MutableLiveData<PaymentDetail> liveData = navController.getCurrentBackStackEntry()
                .getSavedStateHandle()
                .getLiveData("tempPayment");

        liveData.observe(getActivity(), paymentDetail -> {
            // Do something with the result.
            paymentDropDownAdapter.addPayment(new PaymentListItem(getContext(), paymentDetail), true);
        });

        // We use a String here, but any type that can be put in a Bundle is supported
        MutableLiveData<String> selectedPaymentLiveData = navController.getCurrentBackStackEntry()
                .getSavedStateHandle()
                .getLiveData("selectedPayment");

        selectedPaymentLiveData.observe(getActivity(), userPaymentSourceId -> {
            // Do something with the result.
            paymentDropDownAdapter.setSelectedPos(userPaymentSourceId);
        });
    }

    private void initializeFuelUpLimit(){
       if (Objects.nonNull(mPapData) && Objects.nonNull(mPapData.getPreAuthLimits())) {
          // binding.totalAmount.setText(String.format("$%s", mPapData.getPreAuthLimits().get("1")));

           FuelLimitDropDownAdapter adapter = new FuelLimitDropDownAdapter(
                   getContext(),
                   mPapData.getPreAuthLimits(),
                   this,
                   mPapData.getOtherAmountHighLimit(),
                   mPapData.getOtherAmountLowLimit()
           );

           if (preAuth != null) {
               adapter.setSelectedPosfromValue(preAuth.replace("$", "").replace(",", ".").replaceAll("\\s",""));
           }

           adapter.findLastFuelUpTransaction(lastTransactionFuelUpLimit);

           binding.fuelUpLimit.setDropDownData(adapter);

       }
    }

    @Override
    public void selectPumpNumber(String pumpNumber) {
        this.pumpNumber = pumpNumber;
        binding.pumpNumberText.setText(pumpNumber);

        new Handler().postDelayed(() -> binding.pumpLayout.callOnClick(), 400);
    }

    @Override
    public void onExpandCollapseListener(boolean isExpand) {
        
    }
    private void goBack() {
        Navigation.findNavController(getView()).popBackStack(R.id.home_navigation, false);
    }

    @Override
    public void onPreAuthChanged(String value) {
        this.preAuth = value;
        binding.totalAmount.setText(value);
    }
}
