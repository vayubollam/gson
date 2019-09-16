package suncor.com.android.di.modules;

import com.worklight.wlclient.api.WLAuthorizationManager;
import com.worklight.wlclient.api.WLClient;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import suncor.com.android.SuncorApplication;

@Module
public class APPModule {

    @Provides
    @Singleton
    WLClient providesWLClient(SuncorApplication application) {
        WLClient client = WLClient.createInstance(application);
        return client;
    }

    //WLClient is passed as a parameter to ensure that WLAuthorizationManager is instantiated
    @Provides
    @Singleton
    WLAuthorizationManager providesAuthorizationManager() {
        return WLAuthorizationManager.getInstance();
    }

}
