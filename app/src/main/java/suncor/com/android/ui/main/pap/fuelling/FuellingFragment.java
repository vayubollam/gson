package suncor.com.android.ui.main.pap.fuelling;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;

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
import suncor.com.android.databinding.FragmentFuellingBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.model.Resource;
import suncor.com.android.ui.common.Alerts;
import suncor.com.android.ui.main.common.MainActivityFragment;
import suncor.com.android.ui.main.pap.fuelup.FuelUpFragmentDirections;
import suncor.com.android.ui.main.pap.fuelup.FuelUpViewModel;
import suncor.com.android.utilities.AnalyticsUtils;

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
        AnalyticsUtils.setCurrentScreenName(getActivity(), "pay-at-pump-fuelling-authorizing-loading");
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pumpNumber = FuellingFragmentArgs.fromBundle(getArguments()).getPumpNumber();
        AnalyticsUtils.setCurrentScreenName(getActivity(), "pay-at-pump-fuelling-authorizing");
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
            AnalyticsUtils.logEvent(getContext(), AnalyticsUtils.Event.buttonTap, new Pair<>(AnalyticsUtils.Param.buttonText, getString(R.string.cancel)));
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
                            AnalyticsUtils.logEvent(getContext(), AnalyticsUtils.Event.alertInteraction,
                                    new Pair<>(AnalyticsUtils.Param.alertTitle, getString(R.string.cancel_alert_title)+"("+getString(R.string.cancel_alert_body)+")"),
                                    new Pair<>(AnalyticsUtils.Param.alertSelection, getString(R.string.cancel_alert_button)),
                                    new Pair<>(AnalyticsUtils.Param.formName, "Pay at Pump"));
                            viewModel.cancelTransaction(transactionId).observe(getViewLifecycleOwner(), result -> {
                                if (result.status == Resource.Status.LOADING) {
                                    binding.cancelLayout.setVisibility(View.VISIBLE);
                                } else if (result.status == Resource.Status.ERROR) {
                                    binding.cancelLayout.setVisibility(View.GONE);
                                    Alerts.prepareGeneralErrorDialog(getContext(), "Pay at Pump").show();
                                } else if (result.status == Resource.Status.SUCCESS) {
                                    //goBack();
                                }
                            });
                        },  "Pay at Pump").show();
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
        AnalyticsUtils.setCurrentScreenName(getActivity(), "pay-at-pump-fuelling-will-begin");
        startFuellingActiveSession();
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            viewModel.getActiveSession().observe(getViewLifecycleOwner(), result -> {
                if (result.status == Resource.Status.ERROR) {
                    AnalyticsUtils.logEvent(getContext(), AnalyticsUtils.Event.error,
                            new Pair<>(AnalyticsUtils.Param.errorMessage, "Something went wrong on our side" ),
                            new Pair<>(AnalyticsUtils.Param.formName, "Pay at Pump"));
                    Alerts.prepareGeneralErrorDialog(getContext(), "Pay at Pump").show();
                } else if (result.status == Resource.Status.SUCCESS && result.data != null) {
                    if(!result.data.activeSession){
                        if (result.data.lastStatus.equalsIgnoreCase("Cancelled") ||
                                result.data.lastStatus.equalsIgnoreCase("CANCELED")) {
                            Alerts.prepareCustomDialog(
                                     getString(R.string.cancellation_alert_title),
                                    getString(R.string.cancellation_alert_body),
                                    getContext(),
                                    (dialogInterface, i) -> {
                                        AnalyticsUtils.setCurrentScreenName(requireActivity(), "pay-at-pump-fuelling-transaction-cancelled" );
                                        AnalyticsUtils.logEvent(getContext(), AnalyticsUtils.Event.alertInteraction,
                                                new Pair<>(AnalyticsUtils.Param.alertTitle, getString(R.string.cancellation_alert_title)+"("+getString(R.string.cancellation_alert_body)+")"),
                                                new Pair<>(AnalyticsUtils.Param.alertSelection, getString(R.string.cancel)),
                                                new Pair<>(AnalyticsUtils.Param.formName, "Pay at Pump"));
                                        dialogInterface.dismiss();
                                        goBack();
                                    }, "Pay at Pump").show();
                        } else {
                            observeTransactionData(result.data.lastTransId, result.data.lastPaymentProviderName);
                            AnalyticsUtils.setCurrentScreenName(getActivity(), "pay-at-pump-fuelling-almost-complete" );
                            AnalyticsUtils.logEvent(getContext(), AnalyticsUtils.Event.formComplete,
                                    new Pair<>(AnalyticsUtils.Param.formSelection, "Fuelling Complete"),
                                    new Pair<>(AnalyticsUtils.Param.formName, "Pay at Pump"));
                        }
                    } else if (result.data.status != null) {
                        transactionId = result.data.transId;
                        binding.cancelButton.setVisibility(View.VISIBLE);
                        AnalyticsUtils.logEvent(getContext(), AnalyticsUtils.Event.formStep,
                                new Pair<>(AnalyticsUtils.Param.formSelection, getString(R.string.fueling_up)),
                                new Pair<>(AnalyticsUtils.Param.formName, "Pay at Pump"));

                        AnalyticsUtils.setCurrentScreenName(getActivity(), result.data.status.equalsIgnoreCase("New")
                                || result.data.status.equalsIgnoreCase("Authorized") ? "pay-at-pump-fuelling-will-begin" : "pay-at-pump-fuelling-has-begun" );

                        binding.pumpAuthorizedText.setText(result.data.status.equalsIgnoreCase("New")
                                || result.data.status.equalsIgnoreCase("Authorized")?
                                getString(R.string.pump_authorized, result.data.pumpNumber) : getString(R.string.fueling_up));
                        binding.pumpAuthorizedSubheader.setText(result.data.status.equalsIgnoreCase("New")
                                || result.data.status.equalsIgnoreCase("Authorized")?
                                R.string.pump_authorized_subheader : R.string.fueling_up_subheader);
                        binding.pumpNumberText.setText(result.data.pumpNumber);

                        binding.cancelButton.setText(result.data.status.equalsIgnoreCase("New")
                                || result.data.status.equalsIgnoreCase("Authorized")? R.string.cancel : R.string.hide);
                        binding.borderImageView.setImageDrawable(getContext().getDrawable(result.data.status.equalsIgnoreCase("New")
                                || result.data.status.equalsIgnoreCase("Authorized")?
                                R.drawable.circle_dash_border : R.drawable.circle_border));

                        isLoading.set(false);

                        if (!result.data.status.equalsIgnoreCase("New") && !result.data.status.equalsIgnoreCase("Authorized")) {
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
        action.setIsGooglePay(lastPaymentProvider.toLowerCase().contains("google"));
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
