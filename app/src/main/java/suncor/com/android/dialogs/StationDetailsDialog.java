package suncor.com.android.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import suncor.com.android.R;
import suncor.com.android.databinding.CardStationItemBinding;
import suncor.com.android.fragments.StationViewModel;
import suncor.com.android.utilities.NavigationAppsHelper;

public class StationDetailsDialog extends BottomSheetDialogFragment {


    public static final String TAG = StationDetailsDialog.class.getSimpleName();
    private int intialHeight;
    private int intialPosition;
    private int fullHeight;

    private BottomSheetBehavior behavior;

    private int cardMarginInPixels;
    private CardStationItemBinding binding;
    private StationViewModel stationViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.StationDetailsDialogStyle);
        cardMarginInPixels = getResources().getDimensionPixelSize(R.dimen.stationCardMargin);
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
        binding.setVm(stationViewModel);
        binding.getRoot().setPadding(0, getResources().getDimensionPixelOffset(R.dimen.stationCardMargin), 0, 0);
        DisplayMetrics dp = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dp);
        fullHeight = dp.heightPixels - getStatusBarHeight();

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, fullHeight);
        binding.getRoot().setLayoutParams(params);

        binding.cardView.getLayoutParams().height = intialHeight;
        dialog.setContentView(binding.getRoot());

        binding.closeBtn.setOnClickListener((v) -> {
            dismiss();
        });

        binding.btnCardDirections.setOnClickListener((v)->{
            NavigationAppsHelper navigationAppsHelper = new NavigationAppsHelper(getActivity());
            navigationAppsHelper.openNavigationApps(stationViewModel.station.get());
        });

        behavior = BottomSheetBehavior.from(((View) binding.getRoot().getParent()));
        if (behavior != null) {
            behavior.setPeekHeight(fullHeight - intialPosition + 2 * cardMarginInPixels);
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View view, int i) {

                }

                @Override
                public void onSlide(@NonNull View view, float v) {
                    if (v > 0.01) {
                        binding.closeBtn.setVisibility(v > 0.1 ? View.VISIBLE : View.GONE);
                        binding.imgBottomSheet.setVisibility(v > 0.1 ? View.GONE : View.VISIBLE);
                        dialog.getWindow().setDimAmount(v * 0.6f);
                        binding.addressLayout.setVisibility(v > 0.1 ? View.VISIBLE : View.GONE);
                        binding.detailsLayout.setVisibility(v > 0.2 ? View.VISIBLE : View.GONE);
                        binding.cardView.getLayoutParams().height = (int) (((fullHeight - 2 * cardMarginInPixels) * v) + (1 - v) * intialHeight);
                        binding.cardView.requestLayout();
                    } else if (v > 0) {
                        binding.cardView.setVisibility(View.GONE);
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

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private int dpToPixels(int dimension) {
        float density = getResources().getDisplayMetrics().density;
        float pixel = dimension * density;
        return (int) pixel;
    }

    public void setIntialHeight(int intialHeight) {
        this.intialHeight = intialHeight;
    }

    public void setIntialPosition(int intialPosition) {
        this.intialPosition = intialPosition;
    }

    public void setStationViewModel(StationViewModel stationViewModel) {
        this.stationViewModel = stationViewModel;
    }
}
