package suncor.com.android.di.modules;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import suncor.com.android.ui.SplashActivity;
import suncor.com.android.ui.enrollement.CardQuestion;
import suncor.com.android.ui.enrollement.EnrollmentActivity;
import suncor.com.android.ui.enrollement.form.EnrollmentFormFragment;
import suncor.com.android.ui.enrollement.form.SecurityQuestionFragment;
import suncor.com.android.ui.home.HomeActivity;
import suncor.com.android.ui.home.common.SessionAwareActivity;
import suncor.com.android.ui.home.dashboard.DashboardFragment;
import suncor.com.android.ui.home.profile.ProfileFragment;
import suncor.com.android.ui.home.stationlocator.StationDetailsDialog;
import suncor.com.android.ui.home.stationlocator.StationsFragment;
import suncor.com.android.ui.home.stationlocator.favourites.FavouritesFragment;
import suncor.com.android.ui.home.stationlocator.search.SearchFragment;
import suncor.com.android.ui.login.LoginActivity;

/**
 * Binds all sub-components within the app.
 */
@Module
public abstract class BuildersModule {
    @ContributesAndroidInjector()
    abstract SessionAwareActivity contributeSessionAwareActivity();

    //Home Activity and its fragments
    @ContributesAndroidInjector()
    abstract HomeActivity contributeHomeActivity();

    @ContributesAndroidInjector()
    abstract StationsFragment contributeStationsFragment();

    @ContributesAndroidInjector
    abstract ProfileFragment contributeProfileFragment();

    @ContributesAndroidInjector
    abstract StationDetailsDialog contributeStationDetailsDialog();

    @ContributesAndroidInjector
    abstract DashboardFragment contributeDashboardFragment();

    @ContributesAndroidInjector
    abstract SearchFragment contributesSearchFragment();

    @ContributesAndroidInjector
    abstract FavouritesFragment contributesFavouritesFragment();


    //Login Activity
    @ContributesAndroidInjector()
    abstract LoginActivity contributeLoginActivity();

    //Enrollment Activity
    @ContributesAndroidInjector
    abstract EnrollmentActivity contributeEnrollmentActivity();

    @ContributesAndroidInjector
    abstract CardQuestion contributeCardQuestion();

    @ContributesAndroidInjector
    abstract EnrollmentFormFragment contributeEnrollmentFormFragment();

    @ContributesAndroidInjector
    abstract SecurityQuestionFragment contributeSecurityQuestionFragment();


    @ContributesAndroidInjector()
    abstract SplashActivity contributeSplashActivity();


}