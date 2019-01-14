package suncor.com.android;

import android.app.Application;

import com.worklight.wlclient.api.WLClient;

import suncor.com.android.mfp.challengeHandlers.UserLoginChallengeHandler;
import suncor.com.android.model.Station;

public class SuncorApplication extends Application {

    private static SuncorApplication sInstance;

    public void onCreate() {
        super.onCreate();
        sInstance = this;
        WLClient client = WLClient.createInstance(this);
        UserLoginChallengeHandler suncorChallengeHandler = new UserLoginChallengeHandler(GeneralConstants.SECURITY_CHECK_NAME_LOGIN);
        client.registerChallengeHandler(suncorChallengeHandler);
        System.out.println("App Created");

        Station.initiateAmenities(this);
    }

    public static SuncorApplication getInstance() {
        return sInstance;
    }
}
