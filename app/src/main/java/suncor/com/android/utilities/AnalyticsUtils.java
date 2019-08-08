package suncor.com.android.utilities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Pair;

import com.google.firebase.analytics.FirebaseAnalytics;

public class AnalyticsUtils {

    @SafeVarargs
    public static void logEvent(Context context, String eventName, String screenName, Pair<String, String>... variables) {
        Bundle bundle = new Bundle();
        for (Pair<String, String> variable : variables) {
            bundle.putString(variable.first, variable.second);
        }

        FirebaseAnalytics.getInstance(context).logEvent(eventName, bundle);
    }

    public static void setCurrentScreenName(Activity activity, String screenName) {
        FirebaseAnalytics.getInstance(activity).setCurrentScreen(activity, screenName, activity.getClass().getSimpleName());
    }
}
