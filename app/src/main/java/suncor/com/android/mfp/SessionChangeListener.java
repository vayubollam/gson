package suncor.com.android.mfp;

public interface SessionChangeListener {
    void onLoginSuccess(String userName);

    void onLoginRequired(int remainingAttempts);

    void onLoginFailed(String error);
}
