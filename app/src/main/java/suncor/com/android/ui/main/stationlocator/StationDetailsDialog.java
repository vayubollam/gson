package suncor.com.android.ui.main.stationlocator;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Objects;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import suncor.com.android.HomeNavigationDirections;
import suncor.com.android.R;
import suncor.com.android.SuncorApplication;
import suncor.com.android.analytics.BaseAnalytics;
import suncor.com.android.analytics.stationlocator.StationDetailsAnalytics;
import suncor.com.android.databinding.CardStationItemBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.mfp.SessionManager;
import suncor.com.android.model.Resource;
import suncor.com.android.model.pap.FuelUp;
import suncor.com.android.model.station.Station;
import suncor.com.android.ui.common.ModalDialog;
import suncor.com.android.ui.common.SuncorToast;
import suncor.com.android.ui.enrollment.EnrollmentActivity;
import suncor.com.android.ui.login.LoginActivity;
import suncor.com.android.ui.main.home.HomeViewModel;
import suncor.com.android.utilities.NavigationAppsHelper;
import suncor.com.android.utilities.Timber;

public class StationDetailsDialog extends BottomSheetDialogFragment {


    public static final String TAG = StationDetailsDialog.class.getSimpleName();

    private final static float DIM_AMOUNT = 0.6f;
    private int initialHeight;
    private int initialPosition;
    private int fullHeight;

    private BottomSheetBehavior behavior;

    private int verticalPadding;
    private CardStationItemBinding binding;
    private StationItem stationItem;
    private int initialAddressLayoutHeight;
    private int initialAddressLayoutBottomMargin;
    private boolean shouldShowCardHandler;

    @Inject
    ViewModelFactory viewModelFactory;
    private HomeViewModel mViewModel;
    private FuelUp fuelUp;

    @Inject
    SessionManager sessionManager;

    @Inject
    SuncorApplication application;

    public static void showCard(Fragment fragment, StationItem stationItem, View originalView, boolean shouldShowCardHandler) {
        StationDetailsDialog dialog = new StationDetailsDialog();
        dialog.setInitialHeight(originalView.getHeight());
        int[] position = new int[2];
        originalView.getLocationOnScreen(position);
        dialog.setInitialPosition(position[1]);
        dialog.setStationItem(stationItem);
        if (fragment.getFragmentManager().findFragmentByTag(StationDetailsDialog.TAG) != null) {
            return;
        }
        dialog.show(fragment.getFragmentManager(), StationDetailsDialog.TAG);
        dialog.shouldShowCardHandler = shouldShowCardHandler;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.StationDetailsDialogStyle);
        AndroidSupportInjection.inject(this);

        mViewModel = ViewModelProviders.of(this, viewModelFactory).get(HomeViewModel.class);
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = Objects.requireNonNull(getDialog()).getWindow();
        WindowManager.LayoutParams windowParams = window.getAttributes();
        windowParams.dimAmount = 0f;
        windowParams.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(windowParams);

        StationDetailsAnalytics.logScreenName(requireActivity());

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mViewModel.isPAPAvailable(stationItem).observe(this, value -> {
            this.fuelUp = value.data;

            binding.mobilePaymentLayout.setVisibility(View.VISIBLE);
            binding.mobilePaymentText.setVisibility(value.status == Resource.Status.LOADING ? View.INVISIBLE : View.VISIBLE);
            binding.mobilePaymentProgressBar.setVisibility(value.status != Resource.Status.LOADING ? View.GONE : View.VISIBLE);

            binding.mobilePaymentText.setText(value.data != null && value.data.papAvailable() ? R.string.mobile_payment_accepted : R.string.mobile_payment_not_accepted);
            binding.imgMobilePayment.setImageResource(value.data != null && value.data.papAvailable() ? R.drawable.ic_check : R.drawable.ic_close);

            binding.directionsButton.setText(value.data != null && value.data.fuelUpAvailable() ? R.string.action_fuel_up : R.string.station_directions_button);
        });
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void setupDialog(@NonNull Dialog dialog, int style) {
        try{
        binding = CardStationItemBinding.inflate(LayoutInflater.from(getContext()));
        binding.setVm(stationItem);
        int padding = getResources().getDimensionPixelOffset(R.dimen.cards_padding_expanded);
        verticalPadding = 2 * padding;
        binding.getRoot().setPadding(padding, padding, padding, padding);
        DisplayMetrics dp = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getRealMetrics(dp);
        WindowInsets insets = requireActivity().getWindow().getDecorView().getRootWindowInsets();
        if (insets !=null) {
            fullHeight = dp.heightPixels - insets.getSystemWindowInsetTop() - insets.getStableInsetBottom();
        } else{
            fullHeight = dp.heightPixels - getStatusBarHeight();
        }


        binding.getRoot().setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, fullHeight));
        binding.cardView.getLayoutParams().height = initialHeight;

        if (!shouldShowCardHandler) {
            binding.imgBottomSheet.setVisibility(View.INVISIBLE);
        }

        dialog.setContentView(binding.getRoot());

        binding.closeButton.setOnClickListener((v) -> {
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        });

        binding.callButton.setOnClickListener((v) -> {
            callStation(stationItem.getStation());
        });

        binding.directionsButton.setOnClickListener((v) -> {
            if (fuelUp != null && fuelUp.fuelUpAvailable()) {
                HomeNavigationDirections.ActionToSelectPumpFragment action =
                        HomeNavigationDirections.actionToSelectPumpFragment(
                                stationItem.getStation().getId(),
                                getString(R.string.action_location, stationItem.getStation().getAddress().getAddressLine())
                        );
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(action);
                dismissAllowingStateLoss();
            } else {
                NavigationAppsHelper.openNavigationApps(requireActivity(), stationItem.getStation());
            }
        });

        binding.favouriteButton.setOnClickListener((v) -> {
            toggleFavourite();
        });

        binding.detailsLayout.setVisibility(View.VISIBLE);
        binding.addressLayout.setVisibility(View.VISIBLE);
        initialAddressLayoutHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics()); //16dp
        initialAddressLayoutBottomMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()); //10dp

        behavior = BottomSheetBehavior.from(((View) binding.getRoot().getParent()));
        if (behavior != null) {
            behavior.setPeekHeight(fullHeight - (initialPosition - getStatusBarHeight()) + padding);
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {

                @Override
                public void onStateChanged(@NonNull View view, int i) {
                    //Do nothing
                }

                @Override
                public void onSlide(@NonNull View view, float v) {
                    if (v > 0.005) {
                        dialog.getWindow().setDimAmount(v * DIM_AMOUNT);

                        //title text size
                        float titleTextSize = v > 0.7 ? 22 : v * 4 + 18;
                        if (Math.abs(titleTextSize - binding.stationTitleText.getTextSize()) > 10) {
                            ObjectAnimator.ofFloat(binding.stationTitleText, "textSize", titleTextSize).setDuration(0).start();
                        }

                        //changing address layout height and alpha
                        binding.addressLayout.animate().alpha(v).setDuration(0).start();
                        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) binding.addressLayout.getLayoutParams();
                        layoutParams.height = (int) (v * initialAddressLayoutHeight);
                        layoutParams.bottomMargin = (int) (v * initialAddressLayoutBottomMargin);
                        binding.addressLayout.requestLayout();

                        //close and handle buttons
                        binding.closeButton.setVisibility(v > 0.1 ? View.VISIBLE : View.GONE);
                        if (shouldShowCardHandler) {
                            binding.imgBottomSheet.setVisibility(v > 0.1 ? View.INVISIBLE : View.VISIBLE);
                        } else {
                            binding.imgBottomSheet.setVisibility(View.INVISIBLE);
                        }

                        //card height
                        binding.cardView.getLayoutParams().height = (int) (((fullHeight - verticalPadding) * v) + (1 - v) * initialHeight);
                        binding.cardView.requestLayout();
                    } else {
                        dismiss();
                    }
                }
            });
        }
        dialog.setOnShowListener((dialog1 -> {
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }));

        }catch (Exception e){
            Timber.d( Objects.requireNonNull(e.getLocalizedMessage()));
        }
    }

    private void toggleFavourite() {
        if (!sessionManager.isUserLoggedIn()) {
            ModalDialog dialog = new ModalDialog();
            dialog.setTitle(getString(R.string.login_prompt_title))
                    .setMessage(getString(R.string.login_prompt_message))
                    .setRightButton(getString(R.string.sign_in), (v) -> {
                        startActivity(new Intent(getContext(), LoginActivity.class));
                        dialog.dismiss();
                    })
                    .setCenterButton(getString(R.string.join), (v) -> {
                        startActivity(new Intent(getContext(), EnrollmentActivity.class));
                        dialog.dismiss();
                    })
                    .setLeftButton(getString(R.string.cancel), (v) -> {
                        dialog.dismiss();
                    })
                    .show(getFragmentManager(), ModalDialog.TAG);
        } else {
            binding.setFavouriteBusy(true);
            stationItem.toggleFavourite().observe(this, (r) -> {
                binding.setFavouriteBusy(r.status == Resource.Status.LOADING);
                if (r.status == Resource.Status.ERROR) {
                    if (stationItem.isFavourite()) {
                        SuncorToast.makeText(application, R.string.msg_sl007, Toast.LENGTH_SHORT).show();
                    } else {
                        SuncorToast.makeText(application, R.string.msg_sl006, Toast.LENGTH_SHORT).show();
                    }
                } else if (r.status == Resource.Status.SUCCESS) {
                    if (stationItem.isFavourite()) {
                        StationDetailsAnalytics.logAddToFav(requireContext(),stationItem.getStation().getAddress().getAddressLine());
                    }
                }
            });
        }
    }

    private void callStation(Station station) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + station.getAddress().getPhone()));
        startActivity(intent);
        BaseAnalytics.logTapToCall(requireContext(),station.getAddress().getPhone());
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.setIsLoggedIn(sessionManager.isUserLoggedIn());
        if (behavior != null && behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            getDialog().getWindow().setDimAmount(DIM_AMOUNT);
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    public void setInitialHeight(int initialHeight) {
        this.initialHeight = initialHeight;
    }

    public void setInitialPosition(int initialPosition) {
        this.initialPosition = initialPosition;
    }

    public void setStationItem(StationItem stationItem) {
        this.stationItem = stationItem;
    }
}
