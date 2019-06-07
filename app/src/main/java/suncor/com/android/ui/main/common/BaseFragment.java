package suncor.com.android.ui.main.common;

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
    //TODO find a better and more general solution for fragments being popped out of backstack
    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (!enter && nextAnim == R.anim.dummy) {
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
            int flags = getActivity().getWindow()
                    .getDecorView().getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            getActivity().getWindow()
                    .getDecorView()
                    .setSystemUiVisibility(flags);
            getActivity().getWindow().setStatusBarColor(Color.TRANSPARENT);

        } else {
            getActivity().getWindow().setStatusBarColor(getStatusBarColor());
            int flags = getActivity().getWindow()
                    .getDecorView().getSystemUiVisibility();
            flags &= ~View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            flags &= ~View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            getActivity().getWindow()
                    .getDecorView()
                    .setSystemUiVisibility(flags);
        }
    }
}
