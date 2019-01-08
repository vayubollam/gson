package suncor.com.android;

import android.app.Application;

import com.worklight.wlclient.api.WLClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

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

        Station.SERVICE_AMENITIES = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.station_services)));
        Station.FUEL_AMENITIES = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.station_fuel_options)));
        Station.WASH_AMENITIES = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.station_wash_options)));
    }
}
