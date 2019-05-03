package suncor.com.android.ui.home.common;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;

import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.FragmentManager;
import dagger.android.support.DaggerFragment;
import suncor.com.android.R;

public class BaseFragment extends DaggerFragment {

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

    //A hackish solution to this issue: https://issuetracker.google.com/issues/37036000
    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (!enter) {
            ViewCompat.setTranslationZ(getView(), -100);
        }
        return super.onCreateAnimation(transit, enter, nextAnim);
    }

    protected int getStatusBarColor() {
        return getResources().getColor(R.color.colorPrimaryDark);
    }

    protected boolean isStatusBarTransparent() {
        return false;
    }

    protected void setStatusBarColor() {
        if (isStatusBarTransparent()) {
            getActivity().getWindow().setStatusBarColor(Color.TRANSPARENT);
            getActivity().getWindow()
                    .getDecorView()
                    .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        } else {
            getActivity().getWindow().setStatusBarColor(getStatusBarColor());
            getActivity().getWindow()
                    .getDecorView()
                    .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }
}
