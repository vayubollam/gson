package suncor.com.android.ui.main;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;

import suncor.com.android.R;
import suncor.com.android.ui.main.common.MainActivityFragment;

public class BottomNavigationFragment extends MainActivityFragment {

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
        // apply a bottom margin = height of bottom navigation + height of divider
        params.bottomMargin = (int) (getResources().getDimensionPixelSize(R.dimen.suncor_bottom_navigation_height) + getResources().getDisplayMetrics().density);
        view.setLayoutParams(params);
    }

    public void navigateToAccountPage() {
        Navigation.findNavController(getView()).navigate(R.id.action_to_profile_tab);
    }
}
