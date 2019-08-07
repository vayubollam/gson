package suncor.com.android.utilities;

import androidx.core.hardware.fingerprint.FingerprintManagerCompat;

import javax.inject.Inject;

import suncor.com.android.SuncorApplication;

public class FingerPrintManager {

    private final SuncorApplication application;

    @Inject
    public FingerPrintManager(SuncorApplication application) {
        this.application = application;
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
}
