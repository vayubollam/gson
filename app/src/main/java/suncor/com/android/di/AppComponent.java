package suncor.com.android.di;

import javax.inject.Singleton;

import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;
import suncor.com.android.SuncorApplication;
import suncor.com.android.di.modules.APPModule;
import suncor.com.android.di.modules.BuildersModule;
import suncor.com.android.di.modules.DataModule;
import suncor.com.android.di.modules.ViewModelModule;

@Component(modules = {
        APPModule.class,
        ViewModelModule.class,
        BuildersModule.class,
        DataModule.class,
        AndroidSupportInjectionModule.class})

@Singleton
public interface AppComponent extends AndroidInjector<SuncorApplication> {


    /* We will call this builder interface from our custom Application class.
     * This will set our application object to the AppComponent.
     * So inside the AppComponent the application instance is available.
     * So this application instance can be accessed by our modules
     * such as ApiModule when needed
     * */
    @Component.Builder
    abstract class Builder extends AndroidInjector.Builder<SuncorApplication> {
        @Override
        abstract public AppComponent build();
    }


    /*
     * This is our custom Application class
     * */
    void inject(SuncorApplication suncorApplication);
}