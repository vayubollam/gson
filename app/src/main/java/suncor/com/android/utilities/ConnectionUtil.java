package suncor.com.android.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import suncor.com.android.SuncorApplication;

public class ConnectionUtil {

    public static boolean haveNetworkConnection() {
        ConnectivityManager cm =
                (ConnectivityManager) SuncorApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }
}
