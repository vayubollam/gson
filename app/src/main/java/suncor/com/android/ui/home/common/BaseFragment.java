package suncor.com.android.ui.home.common;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import suncor.com.android.R;

public class BaseFragment extends Fragment {

    private FragmentManager.OnBackStackChangedListener backStackListener = () -> {
        if (isVisible()) {
            setStatusBarColor();
        }
    };

    public void onLoginStatusChanged() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().addOnBackStackChangedListener(backStackListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getFragmentManager().removeOnBackStackChangedListener(backStackListener);
    }

    @Override
    public void onStart() {
        super.onStart();
        setStatusBarColor();
        getView().requestApplyInsets();
    }


    protected boolean isStatusBarTransparent() {
        return false;
    }

    private void setStatusBarColor() {
        if (isStatusBarTransparent()) {
            getActivity().getWindow().setStatusBarColor(Color.TRANSPARENT);
            getActivity().getWindow()
                    .getDecorView()
                    .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        } else {
            getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
            getActivity().getWindow()
                    .getDecorView()
                    .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }
}
