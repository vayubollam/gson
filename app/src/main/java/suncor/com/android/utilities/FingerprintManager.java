package suncor.com.android.utilities;

import androidx.core.hardware.fingerprint.FingerprintManagerCompat;

import javax.inject.Inject;

import suncor.com.android.SuncorApplication;

public class FingerprintManager {

    private final SuncorApplication application;
    private static UserLocalSettings userLocalSettings;
    public final String USE_FINGERPRINT = "use_fingerprint";

    @Inject
    public FingerprintManager(SuncorApplication application, UserLocalSettings userLocalSettings) {
        this.application = application;
        this.userLocalSettings = userLocalSettings;
    }

    public boolean isFingerPrintExistAndEnrolled() {
        boolean allGood;
        FingerprintManagerCompat fingerprintManagerCompat = FingerprintManagerCompat.from(application);
        if (!fingerprintManagerCompat.isHardwareDetected()) {
            allGood = false;
        } else if (!fingerprintManagerCompat.hasEnrolledFingerprints()) {
            allGood = false;
        } else {
            allGood = true;
        }
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


}
