package suncor.com.android.mfp;

import android.content.Context;
import android.os.Build;

import com.worklight.wlclient.api.WLClient;

import java.text.Normalizer;
import java.util.Locale;

import suncor.com.android.BuildConfig;

public class MfpLogging {
    public static void logDeviceInfo(Context context) {

        enableCertificatePinning();

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

    }

    private static void enableCertificatePinning() {
        String[] certificates;

        if (BuildConfig.APPLICATION_ID == "com.suncor.android.SuncorApplicationOpsQA") {
            certificates = new String[]{"opsqa_rfmp-mfp_com_p1.der", "opsqa_rfmp-mfp_com_b1.der"};
        } else if (BuildConfig.APPLICATION_ID == "com.petrocanada.my_petro_canada" && !BuildConfig.FLAVOR.equals("UATTestflight")) {
            certificates = new String[]{"P1_mfp_petro-canada_ca.der", "B1_mfp_petro-canada_ca.der", "P1_2021_mfp_petro-canada_ca.der", "B1_2021_mfp_petro-canada_ca.der"};
        } else
            return;

        WLClient.getInstance().pinTrustedCertificatePublicKey(certificates);
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
