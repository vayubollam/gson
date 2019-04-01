package suncor.com.android.di.modules.home;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import suncor.com.android.ui.home.HomeActivity;

@Module
public abstract class HomeActivityModule {
    @ContributesAndroidInjector(modules = {HomeFragmentsModule.class})
    abstract HomeActivity contributeHomeActivity();
}
