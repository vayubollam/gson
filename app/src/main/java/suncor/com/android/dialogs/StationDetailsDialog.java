package suncor.com.android.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import suncor.com.android.R;
import suncor.com.android.dataObjects.Station;

public class StationDetailsDialog extends BottomSheetDialogFragment {


    private int intialHeight;
    private int intialPosition;
    private int fullHeight;
    private int fullWidth;

    private CardView cardView;
    private BottomSheetBehavior behavior;
    private View rootView;

    private int cardMarginInPixels;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.StationDetailsDialogStyle);
        cardMarginInPixels = getResources().getDimensionPixelSize(R.dimen.stationDetailsMargin);
    }

    @Override
    public void setupDialog(@NonNull Dialog dialog, int style) {
        rootView = View.inflate(getContext(), R.layout.station_details_dialog, null);
        DisplayMetrics dp = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dp);
        fullHeight = dp.heightPixels - getStatusBarHeight();
        fullWidth = dp.widthPixels;
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, fullHeight);
        rootView.setLayoutParams(params);
        //rootView.getLayoutParams().height = dp.heightPixels;
        cardView = rootView.findViewById(R.id.cardView);
        cardView.getLayoutParams().height = intialHeight;
        dialog.setContentView(rootView);

        behavior = BottomSheetBehavior.from(((View) rootView.getParent()));
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
                        cardView.getLayoutParams().height = (int) (((fullHeight - 2 * cardMarginInPixels) * v) + (1 - v) * intialHeight);
                        //cardView.getLayoutParams().width = (int) (((fullWidth - 2 * cardMarginInPixels) * v) + (1 - v) * getResources().getDimensionPixelSize(R.dimen.stationCardWidth));
                        cardView.requestLayout();
                    } else if (v > 0) {
                        cardView.setVisibility(View.GONE);
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

    public void setStation(Station station) {
    }
}
