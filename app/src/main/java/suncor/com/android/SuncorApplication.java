package suncor.com.android;

import android.app.Application;
import android.util.Log;

import com.worklight.wlclient.api.WLAccessTokenListener;
import com.worklight.wlclient.api.WLAuthorizationManager;
import com.worklight.wlclient.api.WLClient;
import com.worklight.wlclient.api.WLFailResponse;
import com.worklight.wlclient.auth.AccessToken;

import suncor.com.android.data.repository.FavouriteMock;
import suncor.com.android.data.repository.FavouriteRepository;
import suncor.com.android.data.repository.FavouriteRepositoryImpl;
import suncor.com.android.mfp.SessionManager;
import suncor.com.android.mfp.challengeHandlers.UserLoginChallengeHandler;
import suncor.com.android.model.Station;

public class SuncorApplication extends Application {

    private static SuncorApplication sInstance;
    public static FavouriteRepository favouriteRepository = new FavouriteMock();
    public static boolean splashShown = false;

    public void onCreate() {
        super.onCreate();
        sInstance = this;
        WLClient.createInstance(this);
        UserLoginChallengeHandler.createAndRegister();
        SessionManager sessionManager = SessionManager.getInstance();

        if (sessionManager.getUserName() != null) {
            WLAuthorizationManager.getInstance().obtainAccessToken(GeneralConstants.SECURITY_CHECK_NAME_LOGIN, new WLAccessTokenListener() {
                @Override
                public void onSuccess(AccessToken accessToken) {
                    Log.d(this.getClass().getSimpleName(), "User is logged in");
                }

                @Override
                public void onFailure(WLFailResponse wlFailResponse) {
                    Log.d(this.getClass().getSimpleName(), "User is not logged in");
                }
            });
        }
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
