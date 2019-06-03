package suncor.com.android.di.modules.home;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import suncor.com.android.ui.main.MainActivity;

@Module
public abstract class MainActivityModule {
    @ContributesAndroidInjector(modules = {MainActivityFragmentsModule.class})
    abstract MainActivity contributeMainActivity();
}
