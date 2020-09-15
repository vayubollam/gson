package suncor.com.android.ui.main.pap.fuelling;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import javax.inject.Inject;

import suncor.com.android.R;
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

    private boolean pingActiveSessionStarted = false;
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

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pumpNumber = FuellingFragmentArgs.fromBundle(getArguments()).getPumpNumber();

        binding.pumpAuthorizedText.setText(getString(R.string.pump_authorized, pumpNumber));
        binding.pumpNumberText.setText(pumpNumber);

        RotateAnimation rotate = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(6000);
        rotate.setInterpolator(new LinearInterpolator());
        rotate.setRepeatCount(Animation.INFINITE);
        binding.borderImageView.startAnimation(rotate);

        binding.cancelButton.setOnClickListener(button -> {
            if (binding.cancelButton.getText().equals(getString(R.string.hide))) {
                // Navigate to home
                goBack();
            } else {
                // TODO: Handle cancel
                goBack();
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
        observerFuellingActiveSession();
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            viewModel.getActiveSession().observe(getViewLifecycleOwner(), result -> {
                if (result.status == Resource.Status.LOADING) {

                } else if (result.status == Resource.Status.ERROR) {
                    Alerts.prepareGeneralErrorDialog(getContext()).show();
                } else if (result.status == Resource.Status.SUCCESS && result.data != null) {

                    if (result.data.status != null) {
                        binding.pumpAuthorizedText.setText(result.data.status.equals("New") ?
                                getString(R.string.pump_authorized, result.data.pumpNumber) : getString(R.string.fueling_up));
                        binding.pumpAuthorizedSubheader.setText(result.data.status.equals("New") ?
                                R.string.pump_authorized_subheader : R.string.fueling_up_subheader);
                        binding.pumpNumberText.setText(result.data.pumpNumber);
                        binding.pumpNumberText.setVisibility(result.data.status.equals("New") ? View.VISIBLE : View.GONE);

                        binding.cancelButton.setText(result.data.status.equals("New") ? R.string.cancel : R.string.hide);
                        binding.borderImageView.setImageDrawable(getContext().getDrawable(result.data.status.equals("New") ?
                                R.drawable.circle_dash_border : R.drawable.circle_border));

                        if (!result.data.status.equals("New"))
                            binding.borderImageView.clearAnimation();
                    } else {
                        goBack();
                    }
                }
            });

            if(pingActiveSessionStarted) {
                observerFuellingActiveSession();
            }
        }
    };

    public void stopFuellingActiveSessionObserver() {
        pingActiveSessionStarted = false;
        handler.removeCallbacks(runnable);
    }

    public void observerFuellingActiveSession() {
        pingActiveSessionStarted = true;
        handler.postDelayed(runnable, 5000);
    }

    private void goBack() {
        NavController navController = Navigation.findNavController(getView());
        navController.popBackStack();
    }
}
