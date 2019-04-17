package suncor.com.android.ui.home.common;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import dagger.android.support.DaggerFragment;
import suncor.com.android.R;

public class BaseFragment extends DaggerFragment {

    private FragmentManager.OnBackStackChangedListener backStackListener = () -> {
        if (isVisible()) {
            setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
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
        setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        getView().requestApplyInsets();
    }


    protected boolean isStatusBarTransparent() {
        return false;
    }

    protected void setStatusBarColor(int statusBarColor) {
        if (isStatusBarTransparent()) {
            getActivity().getWindow().setStatusBarColor(Color.TRANSPARENT);
            getActivity().getWindow()
                    .getDecorView()
                    .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        } else {
            getActivity().getWindow().setStatusBarColor(statusBarColor);
            getActivity().getWindow()
                    .getDecorView()
                    .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }
}
