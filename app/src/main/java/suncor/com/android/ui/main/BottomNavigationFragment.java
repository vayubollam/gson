package suncor.com.android.ui.main;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import suncor.com.android.R;
import suncor.com.android.ui.main.common.BaseFragment;

public class BottomNavigationFragment extends BaseFragment {

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
        // apply a bottom margin = height of bottom navigation + height of divider
        params.bottomMargin = (int) (getResources().getDimensionPixelSize(R.dimen.suncor_bottom_navigation_height) + getResources().getDisplayMetrics().density);
        view.setLayoutParams(params);
    }
}
