package suncor.com.android.mfp;

import org.json.JSONException;
import org.json.JSONObject;

import suncor.com.android.mfp.challengeHandlers.UserLoginChallengeHandler;
import suncor.com.android.utilities.UserLocalSettings;

public class SessionManager {

    public static final String ACTION_LOGIN_SUCCESS = "suncor.com.android.login.success";
    public static final String ACTION_LOGIN_FAILURE = "suncor.com.android.login.failure";
    public static final String ACTION_LOGIN_REQUIRED = "suncor.com.android.login.required";
    public static final String ACTION_LOGOUT = "suncor.com.android.logout";
    public static final String ACTION_LOGOUT_SUCCESS = "suncor.com.android.logout.success";
    public static final String ACTION_LOGOUT_FAILURE = "suncor.com.android.logout.failure";
    public static final String ACTION_USER_ACCOUNT_BLOCKED = "com.ibm.user.account.blocked";
    private static final String SHARED_PREF_USER = "com.ibm.suncor.user";
    private static final String ACCOUNT_BLOCKED_DATE = "com.ibm.suncor.account.blocked.date";

    public static final int LOCK_TIME_MINUTES = 15;
    public static final int LOGIN_ATTEMPTS = 5;


    private static SessionManager sInstance;
    private String userName;
    private UserLoginChallengeHandler challengeHandler;

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        if (sInstance == null) {
            sInstance = new SessionManager();
            sInstance.userName = UserLocalSettings.getString(SHARED_PREF_USER);
        }
        return sInstance;
    }

    public void logout() {
        if (challengeHandler == null) {
            throw new IllegalStateException("Security Challenge Handler not initialized, did you forget to call setChallengeHandler()");
        }
        challengeHandler.logout();
    }

    public void login(String name, String password) {
        if (challengeHandler == null) {
            throw new IllegalStateException("Security Challenge Handler not initialized, did you forget to call setChallengeHandler()");
        }
        JSONObject credentials = new JSONObject();
        try {
            credentials.put("email", name);
            credentials.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        challengeHandler.login(credentials);
    }

    public void cancelLogin() {
        try {
            challengeHandler.cancel();
        } catch (Exception ignored) {
        }
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
            UserLocalSettings.removeKey(SHARED_PREF_USER);
        } else {
            UserLocalSettings.setString(SHARED_PREF_USER, userName);
        }
    }

    public void markAccountAsBlocked() {
        UserLocalSettings.setLong(ACCOUNT_BLOCKED_DATE, System.currentTimeMillis());
    }

    public boolean isAccountBlocked() {
        return remainingTimeToUnblock() > 0;
    }

    /**
     * @return in milliseconds
     */
    public long remainingTimeToUnblock() {
        long lockTime = UserLocalSettings.getLong(ACCOUNT_BLOCKED_DATE);
        return (lockTime + (LOCK_TIME_MINUTES + 1) * 60 * 1000) - System.currentTimeMillis();
    }

    public void setChallengeHandler(UserLoginChallengeHandler userLoginChallengeHandler) {
        challengeHandler = userLoginChallengeHandler;
    }


}
