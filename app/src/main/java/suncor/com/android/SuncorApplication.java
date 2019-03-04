package suncor.com.android;

import android.app.Application;

import com.worklight.wlclient.HttpClientManager;
import com.worklight.wlclient.api.WLClient;

import suncor.com.android.data.repository.FavouriteRepository;
import suncor.com.android.data.repository.FavouriteRepositoryImpl;
import suncor.com.android.mfp.MFPRequestInterceptor;
import suncor.com.android.mfp.challengeHandlers.UserLoginChallengeHandler;
import suncor.com.android.model.Station;

public class SuncorApplication extends Application {

    private static SuncorApplication sInstance;
    public static FavouriteRepository favouriteRepository;
    public static boolean splashShown = false;

    public void onCreate() {
        super.onCreate();
        sInstance = this;
        WLClient.createInstance(this);
        UserLoginChallengeHandler.createAndRegister();
        MFPRequestInterceptor.attachInterceptor(HttpClientManager.getInstance());
        favouriteRepository = new FavouriteRepositoryImpl(this);
        Station.initiateAmenities(this);
    }


    public static SuncorApplication getInstance() {
        return sInstance;
    }
}
