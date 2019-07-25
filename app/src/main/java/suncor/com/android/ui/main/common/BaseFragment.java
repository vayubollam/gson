package suncor.com.android.ui.main.common;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;

import com.google.firebase.analytics.FirebaseAnalytics;

import dagger.android.support.DaggerFragment;
import suncor.com.android.R;

public class BaseFragment extends DaggerFragment {

    private boolean insetsApplyed;

    public void onLoginStatusChanged() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!isFullScreen()) {
            insetsApplyed = false;
            ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
                if (!insetsApplyed) {
                    insetsApplyed = true;
                    int systemsTopMargin = insets.getSystemWindowInsetTop();
                    view.setPadding(view.getPaddingLeft(), view.getPaddingTop() + systemsTopMargin, view.getPaddingRight(), view.getPaddingBottom());
                }
                return insets;
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
        getView().requestApplyInsets();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!TextUtils.isEmpty(getScreenName())) {
            FirebaseAnalytics.getInstance(getActivity()).setCurrentScreen(getActivity(), getScreenName(), getActivity().getClass().getSimpleName());
        }
    }

    /**
     * Used to track current screen being viewed on Firebase Analytics
     * Subclasses should override it to change the behavior
     * @return the name to send to Firebase Analytics
     */
    protected String getScreenName() {
        return null;
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

    protected boolean isFullScreen() {
        return false;
    }
}
