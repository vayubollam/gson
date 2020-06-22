package suncor.com.android.model.payments;

import android.net.Uri;

import com.google.gson.annotations.SerializedName;

import java.util.Locale;

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

    public Uri getEndpointUrl() {
        return Uri.parse("https://h5-suncor-ca-fuel-uat.petrozone.com/card-on-file")
                .buildUpon()
                .appendQueryParameter("token", token.replaceAll("Bearer ", ""))
                .appendQueryParameter("redirectURL", redirectUrl)
                .appendQueryParameter("deviceId", deviceId)
                .appendQueryParameter("language", Locale.getDefault().getLanguage().equalsIgnoreCase("fr") ? "fr" : "en")
                .build();
    }
}
