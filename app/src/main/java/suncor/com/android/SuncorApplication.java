package suncor.com.android;

import android.app.Application;

import com.worklight.wlclient.api.WLClient;

import suncor.com.android.data.repository.FavouriteRepository;
import suncor.com.android.data.repository.FavouriteRepositoryImpl;
import suncor.com.android.mfp.challengeHandlers.UserLoginChallengeHandler;
import suncor.com.android.model.Station;

public class SuncorApplication extends Application {

    private static SuncorApplication sInstance;
    public static FavouriteRepository favouriteRepository;
    public static boolean splashShown = false;

    public void onCreate() {
        super.onCreate();
        sInstance = this;
        favouriteRepository = new FavouriteRepositoryImpl(this);
        WLClient.createInstance(this);
        UserLoginChallengeHandler.createAndRegister();
        Station.initiateAmenities(this);
    }

    public static SuncorApplication getInstance() {
        return sInstance;
    }
}
