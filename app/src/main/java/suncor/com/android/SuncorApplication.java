package suncor.com.android;

import android.app.Application;

import com.worklight.wlclient.api.WLClient;

import suncor.com.android.challengeHandlers.UserLoginChallengeHandler;
import suncor.com.android.constants.GeneralConstants;
import suncor.com.android.dataObjects.Station;

public class SuncorApplication extends Application {
    public void onCreate() {
        super.onCreate();
        WLClient client = WLClient.createInstance(this);
        UserLoginChallengeHandler suncorChallengeHandler = new UserLoginChallengeHandler(GeneralConstants.SECURITY_CHECK_NAME_LOGIN);
        client.registerChallengeHandler(suncorChallengeHandler);
        System.out.println("App Created");

        Station.initiateAmenities(this);

    }
}
