package suncor.com.android.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.worklight.wlclient.api.WLClient;

/**
 * Created by bahramhaddadi on 2018-11-27.
 */

public class UserLocalSettings {

    private static SharedPreferences preferences;

    static {
        Context context = WLClient.getInstance().getContext();
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static boolean getBool(String key) {
        return preferences.getBoolean(key, false);
    }

    public static void setBool(String key, boolean value) {
        preferences.edit()
                .putBoolean(key, value)
                .apply();
    }

    public static String getString(String key) {
        return preferences.getString(key, null);
    }

    public static void setString(String key, String value) {
        preferences.edit()
                .putString(key, value)
                .apply();
    }

    public static long getLong(String key) {
        return preferences.getLong(key, 0);
    }

    public static void setLong(String key, long value) {
        preferences.edit()
                .putLong(key, value)
                .apply();
    }

    public static void removeKey(String key) {
        preferences.edit()
                .remove(key)
                .apply();
    }
}
