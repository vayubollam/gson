package suncor.com.android.mfp;

import suncor.com.android.model.Profile;

public interface SessionChangeListener {
    void onLoginSuccess(Profile profile);

    void onLoginRequired(int remainingAttempts);

    void onLoginFailed(String error);
}
