package suncor.com.android;

import android.app.Application;

import com.worklight.wlclient.HttpClientManager;
import com.worklight.wlclient.api.WLClient;

import suncor.com.android.data.repository.favourite.FavouriteRepository;
import suncor.com.android.data.repository.favourite.FavouriteRepositoryImpl;
import suncor.com.android.data.repository.stations.StationsProvider;
import suncor.com.android.data.repository.stations.StationsProviderImpl;
import suncor.com.android.mfp.MFPRequestInterceptor;
import suncor.com.android.mfp.MfpLogging;
import suncor.com.android.mfp.challengeHandlers.UserLoginChallengeHandler;
import suncor.com.android.model.Station;

public class SuncorApplication extends Application {

    public static FavouriteRepository favouriteRepository;
    public static StationsProvider stationsProvider;
    public static boolean splashShown = false;
    private static SuncorApplication sInstance;

    public static SuncorApplication getInstance() {
        return sInstance;
    }

    public void onCreate() {
        super.onCreate();
        sInstance = this;
        initMFP();
        favouriteRepository = new FavouriteRepositoryImpl(this);
        stationsProvider = new StationsProviderImpl();
        Station.initiateAmenities(this);
    }

    private void initMFP() {
        WLClient.createInstance(this);
        UserLoginChallengeHandler.createAndRegister();
        MFPRequestInterceptor.attachInterceptor(HttpClientManager.getInstance());
        MfpLogging.logDeviceInfo(this);
    }


}