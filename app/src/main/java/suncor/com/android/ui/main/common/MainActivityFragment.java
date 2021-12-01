package suncor.com.android.ui.main.common;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;

import com.kount.api.analytics.AnalyticsCollector;

import suncor.com.android.BuildConfig;
import suncor.com.android.R;
import suncor.com.android.ui.common.BaseFragment;
import suncor.com.android.utilities.KountManager;

public class MainActivityFragment extends BaseFragment {

    private boolean insetsApplyed;

    public void onLoginStatusChanged() {
        //do nothing
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

    protected String generateKountSessionID() {
        KountManager.INSTANCE.collect(BuildConfig.APPLICATION_ID.equalsIgnoreCase("com.petrocanada.my_petro_canada")
                ? AnalyticsCollector.ENVIRONMENT_PRODUCTION : AnalyticsCollector.ENVIRONMENT_TEST, getContext());
        return KountManager.INSTANCE.getCurrentSessionId();
    }
}
