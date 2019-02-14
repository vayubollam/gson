package suncor.com.android.ui.home.stationlocator;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import suncor.com.android.R;
import suncor.com.android.databinding.CardStationItemBinding;
import suncor.com.android.mfp.SessionManager;
import suncor.com.android.model.Resource;
import suncor.com.android.model.Station;
import suncor.com.android.utilities.NavigationAppsHelper;

public class StationDetailsDialog extends BottomSheetDialogFragment {


    public static final String TAG = StationDetailsDialog.class.getSimpleName();

    private final static float DIM_AMOUNT = 0.6f;
    private int initialHeight;
    private int initialPosition;
    private int fullHeight;

    private SessionManager sessionManager;

    private BottomSheetBehavior behavior;

    private int verticalPadding;
    private CardStationItemBinding binding;
    private StationItem stationItem;
    private int initialAddressLayoutHeight;
    private int initialAddressLayoutBottomMargin;


    public static void showCard(Fragment fragment, StationItem stationItem, View originalView) {
        StationDetailsDialog dialog = new StationDetailsDialog();
        dialog.setInitialHeight(originalView.getHeight());
        int[] position = new int[2];
        originalView.getLocationOnScreen(position);
        dialog.setInitialPosition(position[1]);
        dialog.setStationItem(stationItem);
        dialog.setTargetFragment(fragment, StationsFragment.STATION_DETAILS_REQUEST_CODE);
        dialog.show(fragment.getFragmentManager(), StationDetailsDialog.TAG);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.StationDetailsDialogStyle);
        sessionManager = SessionManager.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams windowParams = window.getAttributes();
        windowParams.dimAmount = 0f;
        windowParams.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(windowParams);
    }

    @Override
    public void setupDialog(@NonNull Dialog dialog, int style) {
        binding = CardStationItemBinding.inflate(LayoutInflater.from(getContext()));
        binding.setVm(stationItem);
        int padding = getResources().getDimensionPixelOffset(R.dimen.cards_padding_expanded);
        verticalPadding = 2 * padding;
        binding.getRoot().setPadding(padding, padding, padding, padding);

        DisplayMetrics dp = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getRealMetrics(dp);
        fullHeight = dp.heightPixels - getStatusBarHeight() - getNavBarHeight();

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, fullHeight);
        binding.getRoot().setLayoutParams(params);
        binding.cardView.getLayoutParams().height = initialHeight;
        dialog.setContentView(binding.getRoot());

        binding.closeButton.setOnClickListener((v) -> {
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        });

        binding.callButton.setOnClickListener((v) -> {
            callStation(stationItem.getStation());
        });

        binding.directionsButton.setOnClickListener((v) -> {
            NavigationAppsHelper.openNavigationApps(getActivity(), stationItem.getStation());
        });

        binding.favouriteButton.setOnClickListener((v) -> {
            toggleFavourite();
        });

        binding.detailsLayout.setVisibility(View.VISIBLE);
        binding.addressLayout.setVisibility(View.VISIBLE);
        binding.addressLayout.post(new Runnable() {
            @Override
            public void run() {
                initialAddressLayoutHeight = binding.addressLayout.getMeasuredHeight();
                initialAddressLayoutBottomMargin = ((LinearLayout.LayoutParams) binding.addressLayout.getLayoutParams()).bottomMargin;
            }
        });

        behavior = BottomSheetBehavior.from(((View) binding.getRoot().getParent()));
        if (behavior != null) {
            behavior.setPeekHeight(fullHeight - (initialPosition - getStatusBarHeight()) + padding);
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {

                @Override
                public void onStateChanged(@NonNull View view, int i) {

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
                        binding.imgBottomSheet.setVisibility(v > 0.1 ? View.INVISIBLE : View.VISIBLE);

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
    }

    private void toggleFavourite() {
        if (!sessionManager.isUserLoggedIn()) {
            PromptLoginDialog promptLoginDialog = new PromptLoginDialog();
            promptLoginDialog.show(getFragmentManager(), PromptLoginDialog.TAG);
        } else {
            binding.setFavouriteBusy(true);
            stationItem.toggleFavourite().observe(this, (r) -> {
                if (r.status != Resource.Status.LOADING) {
                    binding.setFavouriteBusy(false);
                }
                if (r.status == Resource.Status.ERROR) {
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void callStation(Station station) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + station.getAddress().getPhone()));
        startActivity(intent);
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private int getNavBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.setIsLoggedIn(sessionManager.isUserLoggedIn());
        if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            getDialog().getWindow().setDimAmount(DIM_AMOUNT);
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, null);
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
