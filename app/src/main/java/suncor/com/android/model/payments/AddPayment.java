package suncor.com.android.model.payments;

import android.net.Uri;

import com.google.gson.annotations.SerializedName;
import com.kount.api.analytics.AnalyticsCollector;

import java.util.Locale;

import suncor.com.android.BuildConfig;

public class AddPayment {
    @SerializedName("Authorization")
    String token;

    @SerializedName("RedirectURL")
    String redirectUrl;

    @SerializedName("IFrameURL")
    String url;

    @SerializedName("x-p97-deviceid")
    String deviceId;

    public String getToken() {
        return token;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public String getUrl() {
        return url;
    }

    public Uri getP97Url() {
        return Uri.parse(url)
                .buildUpon()
                .appendQueryParameter("token", token)
                .appendQueryParameter("redirectUrl", redirectUrl)
                .appendQueryParameter("deviceId", deviceId)
                .appendQueryParameter("appVersion", BuildConfig.VERSION_NAME)
                .appendQueryParameter("appBundleId", BuildConfig.APPLICATION_ID)
                .appendQueryParameter("os", "Android")
                .appendQueryParameter("language", Locale.getDefault().getLanguage().equalsIgnoreCase("fr") ? "fr" : "en")
                .build();
    }
}
