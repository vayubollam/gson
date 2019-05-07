package suncor.com.android.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PermissionPrefs {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public PermissionPrefs(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void firstTimeAsking(String permission, boolean isFirstTime) {
        doEdit();
        editor.putBoolean(permission, isFirstTime);
        doCommit();
    }

    public boolean isFirstTimeAsking(String permission) {
        return sharedPreferences.getBoolean(permission, true);
    }

    private void doEdit() {
        if (editor == null) {
            editor = sharedPreferences.edit();
        }
    }

    private void doCommit() {
        if (editor != null) {
            editor.commit();
            editor = null;
        }
    }
}
