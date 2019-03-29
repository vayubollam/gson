package suncor.com.android;

import com.worklight.wlclient.HttpClientManager;
import com.worklight.wlclient.api.WLAuthorizationManager;
import com.worklight.wlclient.api.WLClient;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DaggerApplication;
import suncor.com.android.data.repository.favourite.FavouriteRepository;
import suncor.com.android.di.DaggerAppComponent;
import suncor.com.android.mfp.MFPRequestInterceptor;
import suncor.com.android.mfp.MfpLogging;
import suncor.com.android.mfp.challengeHandlers.UserLoginChallengeHandler;
import suncor.com.android.model.Station;
import suncor.com.android.utilities.Timber;

public class SuncorApplication extends DaggerApplication {

    public static final int DEFAULT_TIMEOUT = 15_000;


    public static boolean splashShown = false;
    private static SuncorApplication sInstance;

    @Inject
    FavouriteRepository favouriteRepository;

    @Inject
    MFPRequestInterceptor requestInterceptor;

    @Inject
    WLClient wlClient;

    @Inject
    UserLoginChallengeHandler challengeHandler;


    public static SuncorApplication getInstance() {
        return sInstance;
    }

    public void onCreate() {
        super.onCreate();
        sInstance = this;
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        initMFP();

        favouriteRepository.observeSessionChanges();

        Station.initiateAmenities(this);
    }

    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        return DaggerAppComponent.builder()
                .create(this);
    }

    private void initMFP() {
        wlClient.registerChallengeHandler(challengeHandler);
        MFPRequestInterceptor.attachRequestInterceptor(requestInterceptor, HttpClientManager.getInstance());
        MfpLogging.logDeviceInfo(this);
        WLAuthorizationManager.getInstance().setLoginTimeout(DEFAULT_TIMEOUT / 1000);
    }


}
