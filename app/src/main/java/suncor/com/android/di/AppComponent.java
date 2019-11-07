package suncor.com.android.di;

import javax.inject.Singleton;

import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;
import suncor.com.android.SuncorApplication;
import suncor.com.android.di.modules.APPModule;
import suncor.com.android.di.modules.CommonUIsModule;
import suncor.com.android.di.modules.DataModule;
import suncor.com.android.di.modules.ViewModelModule;
import suncor.com.android.di.modules.enrollment.EnrollmentActivityModule;
import suncor.com.android.di.modules.home.MainActivityModule;
import suncor.com.android.di.modules.login.LoginActivityModule;
import suncor.com.android.di.modules.resetpassword.ResetPasswordActivityModule;

@Component(modules = {
        APPModule.class,
        DataModule.class,
        ViewModelModule.class,
        CommonUIsModule.class,
        LoginActivityModule.class,
        MainActivityModule.class,
        EnrollmentActivityModule.class,
        AndroidSupportInjectionModule.class,
        ResetPasswordActivityModule.class})

@Singleton
public interface AppComponent extends AndroidInjector<SuncorApplication> {


    /* We will call this builder interface from our custom Application class.
     * This will set our application object to the AppComponent.
     * So inside the AppComponent the application instance is available.
     * So this application instance can be accessed by our modules
     * */
    @Component.Builder
    abstract class Builder extends AndroidInjector.Builder<SuncorApplication> {
        @Override
        abstract public AppComponent build();
    }
}