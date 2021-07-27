package suncor.com.android.utilities;

import androidx.biometric.BiometricManager;

import javax.inject.Inject;

import suncor.com.android.SuncorApplication;

public class FingerprintManager {

    private final SuncorApplication application;
    private static UserLocalSettings userLocalSettings;
    public final String USE_FINGERPRINT = "use_fingerprint";
    public final String AUTO_LOGIN = "use_auto_login";

    private BiometricManager biometricManager;

    @Inject
    public FingerprintManager(SuncorApplication application, UserLocalSettings userLocalSettings) {
        this.application = application;
        FingerprintManager.userLocalSettings = userLocalSettings;
    }

    /**
     * This method checks if the device can support biometric authentication APIs
     */
    public boolean isFingerPrintExistAndEnrolled(){
        biometricManager = BiometricManager.from(application);
        switch (biometricManager.canAuthenticate()) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                return true;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                return false;
        }

        return false;
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
