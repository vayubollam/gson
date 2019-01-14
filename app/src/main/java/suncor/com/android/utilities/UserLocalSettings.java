package suncor.com.android.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import com.worklight.wlclient.api.WLClient;

import java.util.Date;

import suncor.com.android.GeneralConstants;

/**
 * Created by bahramhaddadi on 2018-11-27.
 */

public class UserLocalSettings {
    public static boolean getBool(String key) {
        Context context = WLClient.getInstance().getContext();
        SharedPreferences preferences = context.getSharedPreferences(GeneralConstants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return preferences.getBoolean(key, false);
    }

    public static void setBool(String key, boolean value) {
        Context context = WLClient.getInstance().getContext();
        SharedPreferences preferences = context.getSharedPreferences(GeneralConstants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static String getString(String key) {
        Context context = WLClient.getInstance().getContext();
        SharedPreferences preferences = context.getSharedPreferences(GeneralConstants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return preferences.getString(key, null);
    }

    public static void setString(String key, String value) {
        Context context = WLClient.getInstance().getContext();
        SharedPreferences preferences = context.getSharedPreferences(GeneralConstants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static Date getDate(String key) {
        Context context = WLClient.getInstance().getContext();
        SharedPreferences preferences = context.getSharedPreferences(GeneralConstants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new Date(preferences.getLong(key, 0));
    }

    public static void setDate(String key, Date value) {
        Context context = WLClient.getInstance().getContext();
        SharedPreferences preferences = context.getSharedPreferences(GeneralConstants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(key, value.getTime());
        editor.commit();
    }

    public static void removeKey(String key) {
        Context context = WLClient.getInstance().getContext();
        SharedPreferences preferences = context.getSharedPreferences(GeneralConstants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(key);
        editor.commit();
    }

}
