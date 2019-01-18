package suncor.com.android;

import android.app.Application;

import com.worklight.wlclient.api.WLClient;

import suncor.com.android.data.repository.FavouriteRepository;
import suncor.com.android.data.repository.FavouriteRepositoryImpl;
import suncor.com.android.mfp.challengeHandlers.UserLoginChallengeHandler;
import suncor.com.android.model.Resource;
import suncor.com.android.model.Station;

public class SuncorApplication extends Application {

    private static SuncorApplication sInstance;
    public static FavouriteRepository favouriteRepository = new FavouriteRepositoryImpl();

    public void onCreate() {
        super.onCreate();
        sInstance = this;
        WLClient client = WLClient.createInstance(this);
        UserLoginChallengeHandler suncorChallengeHandler = new UserLoginChallengeHandler(GeneralConstants.SECURITY_CHECK_NAME_LOGIN);
        client.registerChallengeHandler(suncorChallengeHandler);
        System.out.println("App Created");

//        favouriteRepository.loadFavourites().observeForever((r) -> {
//            if (r.status == Resource.Status.ERROR) {
//                //TODO handle or retry
//            }
//        });
        Station.initiateAmenities(this);
    }

    public static SuncorApplication getInstance() {
        return sInstance;
    }
}
