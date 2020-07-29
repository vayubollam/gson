package suncor.com.android.mfp;

import android.content.Context;
import android.os.Build;

import com.worklight.wlclient.api.WLClient;

import java.text.Normalizer;
import java.util.Locale;

import suncor.com.android.BuildConfig;

public class MfpLogging {
    public static void logDeviceInfo(Context context) {
        StringBuilder deviceInfo = new StringBuilder();
        deviceInfo.append(BuildConfig.VERSION_NAME)
                .append(";")
                .append(BuildConfig.VERSION_CODE)
                .append(";")
                .append(Locale.getDefault())
                .append(";")
                .append(Locale.getDefault().getDisplayLanguage())
                .append(";")
                //device name is blank for android
                .append(";")
                .append("android")
                .append(";")
                .append(Build.VERSION.RELEASE)
                .append(";")
                .append(Build.MODEL)
                .append(";")
                .append(Build.BRAND);
        String deviceonfoNormalized = Normalizer.normalize((deviceInfo.toString()), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");

        WLClient.getInstance().addGlobalHeader("X-Device-Info", deviceonfoNormalized);

        WLClient.getInstance().pinTrustedCertificatePublicKey("sni-cloudflaressl-com.der");


    }

    private static String flattenToAscii(String string) {
        char[] out = new char[string.length()];
        string = Normalizer.normalize(string, Normalizer.Form.NFD);
        int j = 0;
        for (int i = 0, n = string.length(); i < n; ++i) {
            char c = string.charAt(i);
            if (c <= '\u007F') out[j++] = c;
        }
        return new String(out);
    }
}
