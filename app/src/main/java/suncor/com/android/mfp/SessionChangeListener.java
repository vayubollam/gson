package suncor.com.android.mfp;

import suncor.com.android.model.account.Profile;

public interface SessionChangeListener {
    void onLoginSuccess(Profile profile);

    void onLoginFailed(SigninResponse response);
}
