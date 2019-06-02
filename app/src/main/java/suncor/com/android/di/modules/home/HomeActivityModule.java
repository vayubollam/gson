package suncor.com.android.di.modules.home;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import suncor.com.android.ui.main.MainActivity;

@Module
public abstract class HomeActivityModule {
    @ContributesAndroidInjector(modules = {HomeFragmentsModule.class})
    abstract MainActivity contributeHomeActivity();
}
