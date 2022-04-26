package suncor.com.android.ui.main.pap.fuelling;

import static suncor.com.android.utilities.Constants.AUTHORIZED;
import static suncor.com.android.utilities.Constants.CANCELED;
import static suncor.com.android.utilities.Constants.CANCELLED;
import static suncor.com.android.utilities.Constants.GOOGLE;
import static suncor.com.android.utilities.Constants.NEW;
import static suncor.com.android.utilities.Constants.PAY_AT_PUMP;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import javax.inject.Inject;

import suncor.com.android.R;
import suncor.com.android.analytics.pap.FuellingAnalytics;
import suncor.com.android.databinding.FragmentFuellingBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.model.Resource;
import suncor.com.android.ui.common.Alerts;
import suncor.com.android.ui.main.common.MainActivityFragment;
import suncor.com.android.ui.main.pap.fuelup.FuelUpViewModel;

public class FuellingFragment extends MainActivityFragment {

    private FuelUpViewModel viewModel;
    private FragmentFuellingBinding binding;
    private String pumpNumber;
    private String transactionId;

    private boolean pingActiveSessionStarted = false;
    private ObservableBoolean isLoading = new ObservableBoolean(true);
    private Handler handler = new Handler();

    @Inject
    ViewModelFactory viewModelFactory;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(FuelUpViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFuellingBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(this);
        binding.setIsLoading(isLoading);
        FuellingAnalytics.logPapAuthLoadingScreenName(getActivity());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pumpNumber = FuellingFragmentArgs.fromBundle(getArguments()).getPumpNumber();
        FuellingAnalytics.logPapAuthScreenName(getActivity());
        binding.pumpAuthorizedText.setText(getString(R.string.pump_authorized, pumpNumber));
        binding.pumpNumberText.setText(pumpNumber);

        RequestOptions options = new RequestOptions();
        options = options.fitCenter();
        Glide.with(this).load(Uri.parse("file:///android_asset/fuelling_animation.gif")).apply(options).into(binding.fuelAnimationGif);

        RotateAnimation rotate = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(6000);
        rotate.setInterpolator(new LinearInterpolator());
        rotate.setRepeatCount(Animation.INFINITE);
        binding.borderImageView.startAnimation(rotate);

        binding.cancelButton.setOnClickListener(button -> {
            FuellingAnalytics.logCancelButtonTap(requireContext());
            if (binding.cancelButton.getText().equals(getString(R.string.hide))) {
                // Navigate to home
                goBack();
            } else {
                Alerts.prepareCustomDialog(
                        getContext(),
                        getString(R.string.cancel_alert_title),
                        getString(R.string.cancel_alert_body),
                        getString(R.string.cancel_alert_button),
                        getString(R.string.cards_details_close),
                        (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                            FuellingAnalytics.logStopSessionAlertInteraction(requireContext());
                            viewModel.cancelTransaction(transactionId).observe(getViewLifecycleOwner(), result -> {
                                if (result.status == Resource.Status.LOADING) {
                                    binding.cancelLayout.setVisibility(View.VISIBLE);
                                } else if (result.status == Resource.Status.ERROR) {
                                    binding.cancelLayout.setVisibility(View.GONE);
                                    Alerts.prepareGeneralErrorDialog(getContext(), PAY_AT_PUMP).show();
                                } else if (result.status == Resource.Status.SUCCESS) {
                                    goBack();
                                }
                            });
                        },  PAY_AT_PUMP).show();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        stopFuellingActiveSessionObserver();
    }

    @Override
    public void onResume() {
        super.onResume();
        FuellingAnalytics.logPapWillBeginScreenName(requireActivity());
        startFuellingActiveSession();
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            viewModel.getActiveSession().observe(getViewLifecycleOwner(), result -> {
                if (result.status == Resource.Status.ERROR) {
                    FuellingAnalytics.logSomethingWentWrongMessage(requireContext());
                    Alerts.prepareGeneralErrorDialog(getContext(), PAY_AT_PUMP).show();
                } else if (result.status == Resource.Status.SUCCESS && result.data != null) {
                    if(!result.data.activeSession){
                        if (result.data.lastStatus.equalsIgnoreCase(CANCELLED) ||
                                result.data.lastStatus.equalsIgnoreCase(CANCELED)) {
                            Alerts.prepareCustomDialog(
                                     getString(R.string.cancellation_alert_title),
                                    getString(R.string.cancellation_alert_body),
                                    getContext(),
                                    (dialogInterface, i) -> {

                                         FuellingAnalytics.logPapTransactionCancelScreenName(requireActivity());

                                         FuellingAnalytics.logTransactionCancelAlertInteraction(requireContext());

                                        dialogInterface.dismiss();
                                        goBack();
                                    }, PAY_AT_PUMP).show();
                        } else {
                            observeTransactionData(result.data.lastTransId, result.data.lastPaymentProviderName);
                            FuellingAnalytics.logPapAlmostComplete(getActivity());
                            FuellingAnalytics.logFuellingFormComplete(requireContext());
                        }
                    } else if (result.data.status != null) {
                        transactionId = result.data.transId;
                        binding.cancelButton.setVisibility(View.VISIBLE);
                        FuellingAnalytics.logFuellingUpFormStep(requireContext());


                        if(result.data.status.equalsIgnoreCase(NEW) || result.data.status.equalsIgnoreCase(AUTHORIZED)){
                            FuellingAnalytics.logPapWillBeginScreenName(getActivity());
                        }else{
                            FuellingAnalytics.logPapHasBegunScreenName(getActivity());
                        }


                        binding.pumpAuthorizedText.setText(result.data.status.equalsIgnoreCase(NEW)
                                || result.data.status.equalsIgnoreCase(AUTHORIZED)?
                                getString(R.string.pump_authorized, result.data.pumpNumber) : getString(R.string.fueling_up));
                        binding.pumpAuthorizedSubheader.setText(result.data.status.equalsIgnoreCase(NEW)
                                || result.data.status.equalsIgnoreCase(AUTHORIZED)?
                                R.string.pump_authorized_subheader : R.string.fueling_up_subheader);
                        binding.pumpNumberText.setText(result.data.pumpNumber);

                        binding.cancelButton.setText(result.data.status.equalsIgnoreCase(NEW)
                                || result.data.status.equalsIgnoreCase(AUTHORIZED)? R.string.cancel : R.string.hide);
                        binding.borderImageView.setImageDrawable(getContext().getDrawable(result.data.status.equalsIgnoreCase(NEW)
                                || result.data.status.equalsIgnoreCase(AUTHORIZED)?
                                R.drawable.circle_dash_border : R.drawable.circle_border));

                        isLoading.set(false);

                        if (!result.data.status.equalsIgnoreCase(NEW) && !result.data.status.equalsIgnoreCase(AUTHORIZED)) {
                            binding.borderImageView.clearAnimation();
                            binding.fuelImageContainer.setVisibility(View.GONE);

                            binding.fuelAnimationGif.setVisibility(View.VISIBLE);
                        }

                        if(pingActiveSessionStarted) {
                            observerFuellingActiveSession();
                        }
                    } else {
                        goBack();
                    }
                }
            });
        }
    };

    private void observeTransactionData(String transactionId, String lastPaymentProvider){
        FuellingFragmentDirections.ActionFuellingToReceiptFragment action = FuellingFragmentDirections.actionFuellingToReceiptFragment(transactionId);
        action.setIsGooglePay(lastPaymentProvider.toLowerCase().contains(GOOGLE));
        Navigation.findNavController(requireView()).popBackStack();
        Navigation.findNavController(requireView()).navigate(action);
    }

    public void stopFuellingActiveSessionObserver() {
        pingActiveSessionStarted = false;
        handler.removeCallbacks(runnable);
    }

    public void observerFuellingActiveSession() {
        pingActiveSessionStarted = true;
        handler.postDelayed(runnable, 5000);
    }

    public void startFuellingActiveSession() {
        pingActiveSessionStarted = true;
        handler.postDelayed(runnable, 0);
    }

    private void goBack() {
        NavController navController = Navigation.findNavController(getView());
        navController.popBackStack();
    }
}
