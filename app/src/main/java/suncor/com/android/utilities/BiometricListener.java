package suncor.com.android.utilities;

import androidx.biometric.BiometricPrompt;

public interface BiometricListener {
    void onSuccess(String result);
    void onFailed();
}
