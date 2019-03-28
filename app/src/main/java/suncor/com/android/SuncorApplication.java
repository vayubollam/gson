package suncor.com.android;

import android.app.Application;

import com.worklight.wlclient.HttpClientManager;
import com.worklight.wlclient.api.WLClient;

import suncor.com.android.data.repository.account.EmailCheckApi;
import suncor.com.android.data.repository.account.EmailCheckApiImpl;
import suncor.com.android.data.repository.account.EnrollmentsApi;
import suncor.com.android.data.repository.account.EnrollmentsApiImpl;
import suncor.com.android.data.repository.account.FetchSecurityQuestionApi;
import suncor.com.android.data.repository.account.FetchSecurityQuestionApiImpl;
import suncor.com.android.data.repository.favourite.FavouriteRepository;
import suncor.com.android.data.repository.favourite.FavouriteRepositoryImpl;
import suncor.com.android.data.repository.stations.StationsProvider;
import suncor.com.android.data.repository.stations.StationsProviderImpl;
import suncor.com.android.mfp.MFPRequestInterceptor;
import suncor.com.android.mfp.MfpLogging;
import suncor.com.android.mfp.challengeHandlers.UserLoginChallengeHandler;
import suncor.com.android.model.Station;
import suncor.com.android.utilities.Timber;

public class SuncorApplication extends Application {

    public static final int DEFAULT_TIMEOUT = 15;

    public static FavouriteRepository favouriteRepository;
    public static StationsProvider stationsProvider;
    public static EmailCheckApi emailCheckApi;
    public static FetchSecurityQuestionApi fetchSecurityQuestionApi;
    public static EnrollmentsApi enrollmentsApi;

    public static boolean splashShown = false;
    private static SuncorApplication sInstance;

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
        favouriteRepository = new FavouriteRepositoryImpl(this);
        stationsProvider = new StationsProviderImpl();
        emailCheckApi = new EmailCheckApiImpl();
        enrollmentsApi = new EnrollmentsApiImpl();
        fetchSecurityQuestionApi = new FetchSecurityQuestionApiImpl();
        Station.initiateAmenities(this);
    }

    private void initMFP() {
        WLClient.createInstance(this);
        UserLoginChallengeHandler.createAndRegister();
        MFPRequestInterceptor.attachInterceptor(HttpClientManager.getInstance());
        MfpLogging.logDeviceInfo(this);
    }


}
