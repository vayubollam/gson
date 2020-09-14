package suncor.com.android.utilities;

import androidx.core.hardware.fingerprint.FingerprintManagerCompat;

import javax.inject.Inject;

import suncor.com.android.SuncorApplication;

public class FingerprintManager {

    private final SuncorApplication application;
    private static UserLocalSettings userLocalSettings;
    public final String USE_FINGERPRINT = "use_fingerprint";
    public final String AUTO_LOGIN = "use_auto_login";

    @Inject
    public FingerprintManager(SuncorApplication application, UserLocalSettings userLocalSettings) {
        this.application = application;
        FingerprintManager.userLocalSettings = userLocalSettings;
    }

    public boolean isFingerPrintExistAndEnrolled() {
        boolean allGood;
        FingerprintManagerCompat fingerprintManagerCompat = FingerprintManagerCompat.from(application);
        if (!fingerprintManagerCompat.isHardwareDetected()) {
            allGood = false;
        } else allGood = fingerprintManagerCompat.hasEnrolledFingerprints();

        return allGood;
    }

    public void activateFingerprint() {
        userLocalSettings.setBool(USE_FINGERPRINT, true);
    }

    public void deactivateFingerprint() {
        userLocalSettings.setBool(USE_FINGERPRINT, false);
    }

    public boolean isFingerprintActivated() {
        return userLocalSettings.getBool(USE_FINGERPRINT, false);
    }

    public void activateAutoLogin() {
        userLocalSettings.setBool(AUTO_LOGIN, true);
    }

    public void deactivateAutoLogin() {
        userLocalSettings.setBool(AUTO_LOGIN, false);
    }

    public boolean isAutoLoginActivated() {
        return userLocalSettings.getBool(AUTO_LOGIN, false);
    }


}
