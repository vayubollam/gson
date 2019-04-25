package suncor.com.android.di.modules.home;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import suncor.com.android.ui.home.dashboard.DashboardFragment;
import suncor.com.android.ui.home.profile.FAQFragment;
import suncor.com.android.ui.home.profile.ProfileFragment;
import suncor.com.android.ui.home.stationlocator.StationDetailsDialog;
import suncor.com.android.ui.home.stationlocator.StationsFragment;
import suncor.com.android.ui.home.stationlocator.favourites.FavouritesFragment;
import suncor.com.android.ui.home.stationlocator.search.SearchFragment;

@Module
abstract class HomeFragmentsModule {
    @ContributesAndroidInjector
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

    @ContributesAndroidInjector
    abstract FAQFragment contributesFAQFragment();
}
