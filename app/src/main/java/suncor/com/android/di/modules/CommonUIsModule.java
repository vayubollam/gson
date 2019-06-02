package suncor.com.android.di.modules;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import suncor.com.android.ui.SplashActivity;
import suncor.com.android.ui.main.common.SessionAwareActivity;

/**
 * Binds all sub-components within the app.
 */
@Module
public abstract class CommonUIsModule {
    @ContributesAndroidInjector
    abstract SessionAwareActivity contributeSessionAwareActivity();

    @ContributesAndroidInjector
    abstract SplashActivity contributeSplashActivity();
}