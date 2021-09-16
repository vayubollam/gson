package suncor.com.android;

import android.provider.Settings;

import com.google.android.gms.wallet.WalletConstants;
import com.kount.api.analytics.AnalyticsCollector;
import com.mazenrashed.logdnaandroidclient.LogDna;
import com.worklight.common.Logger;
import com.worklight.wlclient.HttpClientManager;
import com.worklight.wlclient.api.WLAuthorizationManager;
import com.worklight.wlclient.api.WLClient;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DaggerApplication;
import suncor.com.android.data.favourite.FavouriteRepository;
import suncor.com.android.di.DaggerAppComponent;
import suncor.com.android.mfp.MFPRequestInterceptor;
import suncor.com.android.mfp.MfpLogging;
import suncor.com.android.mfp.challengeHandlers.UserLoginChallengeHandler;
import suncor.com.android.model.station.Station;
import suncor.com.android.utilities.KountManager;
import suncor.com.android.utilities.Timber;

public class SuncorApplication extends DaggerApplication {

    public static final int DEFAULT_TIMEOUT = 30_000;
    public static final String DEFAULT_PROTECTED_SCOPE = "RegisteredClient";
    public static final String PROTECTED_SCOPE = "LoggedIn";


    private boolean splashShown = false;

    @Inject
    FavouriteRepository favouriteRepository;

    @Inject
    MFPRequestInterceptor requestInterceptor;

    @Inject
    WLClient wlClient;

    @Inject
    UserLoginChallengeHandler challengeHandler;


    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG || !BuildConfig.FLAVOR.equalsIgnoreCase("Prod")) {
            Timber.plant(new Timber.DebugTree(), new Timber.LogDNATree());
        }

        initMFP();

        favouriteRepository.observeSessionChanges();

        Station.initiateAmenities(this);

       KountManager.INSTANCE.collect(BuildConfig.APPLICATION_ID.equalsIgnoreCase("com.petrocanada.my_petro_canada")
                ? AnalyticsCollector.ENVIRONMENT_PRODUCTION : AnalyticsCollector.ENVIRONMENT_TEST);
    }

    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        return DaggerAppComponent.builder()
                .create(this);
    }

    private void initMFP() {
        Logger.setAutoSendLogs(false);
        Logger.setCapture(false);

        wlClient.registerChallengeHandler(challengeHandler);
        MFPRequestInterceptor.attachRequestInterceptor(requestInterceptor, HttpClientManager.getInstance());
        MfpLogging.logDeviceInfo(this);
        WLAuthorizationManager.getInstance().setLoginTimeout(DEFAULT_TIMEOUT / 1000);
        if (BuildConfig.DEBUG) {
            Logger.setLevel(Logger.LEVEL.TRACE);
        }
    }


    public boolean isSplashShown() {
        return splashShown;
    }

    public void setSplashShown(boolean splashShown) {
        this.splashShown = splashShown;
    }
}
