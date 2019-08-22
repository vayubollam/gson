package suncor.com.android.ui.common;

import android.text.TextUtils;

import java.util.Timer;
import java.util.TimerTask;

import dagger.android.support.DaggerFragment;
import suncor.com.android.utilities.AnalyticsUtils;

public class BaseFragment extends DaggerFragment {

    private Timer timer;


    @Override
    public void onStart() {
        super.onStart();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                AnalyticsUtils.logEvent(getContext(), "timer30");
            }
        };
        timer = new Timer();
        timer.schedule(timerTask, 30000);
        AnalyticsUtils.logEvent(getContext(), "screen_view");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!TextUtils.isEmpty(getScreenName())) {
            AnalyticsUtils.setCurrentScreenName(getActivity(), getScreenName());
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        timer.cancel();
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
