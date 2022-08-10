package suncor.com.android.utilities;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import javax.inject.Inject;
import javax.inject.Singleton;

import suncor.com.android.SuncorApplication;

@Singleton
public class UserLocalSettings {
    public static final String RECENTLY_SEARCHED = "recentlySearched";
    public static final String LAST_SUCCESSFUL_PAP_DATE = "com.ibm.suncor.last_pap.date";
    public static final String ENROLLMENT_BONUS = "enrollmentBonus";
    private SharedPreferences preferences;


    @Inject
    public UserLocalSettings(SuncorApplication application) {
        preferences = PreferenceManager.getDefaultSharedPreferences(application);
    }

    public boolean getBool(String key, boolean defValue) {
        return preferences.getBoolean(key, defValue);
    }

    public Boolean getBool(String key, Boolean defValue) {
        return preferences.getBoolean(key, defValue);
    }

    public void setBool(String key, boolean value) {
        preferences.edit()
                .putBoolean(key, value)
                .apply();
    }

    public void setBool(String key, Boolean value) {
        preferences.edit()
                .putBoolean(key, value)
                .apply();
    }

    public String getString(String key) {
        return preferences.getString(key, null);
    }

    public void setString(String key, String value) {
        preferences.edit()
                .putString(key, value)
                .apply();
    }

    public long getLong(String key) {
        return preferences.getLong(key, 0);
    }

    public void setLong(String key, long value) {
        preferences.edit()
                .putLong(key, value)
                .apply();
    }

    public void removeKey(String key) {
        preferences.edit()
                .remove(key)
                .apply();
    }

    public int getInt(String key, int defaultValue) {
        return preferences.getInt(key, defaultValue);
    }

    public void setInt(String key, int value) {
        preferences.edit()
                .putInt(key, value)
                .apply();
    }
}
