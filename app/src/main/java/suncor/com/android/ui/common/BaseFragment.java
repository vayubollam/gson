package suncor.com.android.ui.common;

import android.text.TextUtils;

import dagger.android.support.DaggerFragment;
import suncor.com.android.utilities.AnalyticsUtils;

public class BaseFragment extends DaggerFragment {

    @Override
    public void onResume() {
        super.onResume();
        if (!TextUtils.isEmpty(getScreenName())) {
            AnalyticsUtils.setCurrentScreenName(getActivity(), getScreenName());
        }
    }

    /**
     * Used to track current screen being viewed on Firebase Analytics
     * Subclasses should override it to change the behavior
     *
     * @return the name to send to Firebase Analytics
     */
    protected String getScreenName() {
        return null;
    }
}
