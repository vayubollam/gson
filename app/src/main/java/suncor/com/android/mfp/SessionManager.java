package suncor.com.android.mfp;

import suncor.com.android.GeneralConstants;
import suncor.com.android.utilities.UserLocalSettings;

public class SessionManager {

    private static SessionManager sInstance;
    private String userName;

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        if (sInstance == null) {
            sInstance = new SessionManager();
            sInstance.userName = UserLocalSettings.getString(GeneralConstants.SHARED_PREF_USER);
        }
        return sInstance;
    }

    public boolean isUserLoggedIn() {
        return userName != null;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
        if (userName == null) {
            UserLocalSettings.removeKey(GeneralConstants.SHARED_PREF_USER);
        } else {
            UserLocalSettings.setString(GeneralConstants.SHARED_PREF_USER, userName);
        }
    }

    public void markAccountAsBlocked() {
        UserLocalSettings.setLong(GeneralConstants.ACCOUNT_BLOCKED_DATE, System.currentTimeMillis());
    }

    public boolean isAccountBlocked() {
        return remainingTimeToUnblock() < GeneralConstants.ACCOUNT_BLOCKED_TIME * 60 * 1000;
    }

    /**
     * @return in milliseconds
     */
    public long remainingTimeToUnblock() {
        long lockTime = UserLocalSettings.getLong(GeneralConstants.ACCOUNT_BLOCKED_DATE);
        return System.currentTimeMillis() - lockTime;
    }
}
