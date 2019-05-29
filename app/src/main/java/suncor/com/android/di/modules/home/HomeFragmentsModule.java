package suncor.com.android.di.modules.home;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import suncor.com.android.ui.home.cards.add.AddCardFragment;
import suncor.com.android.ui.home.cards.details.CardsDetailsFragment;
import suncor.com.android.ui.home.cards.list.CardsFragment;
import suncor.com.android.ui.home.dashboard.DashboardFragment;
import suncor.com.android.ui.home.profile.ProfileFragment;
import suncor.com.android.ui.home.profile.help.FAQFragment;
import suncor.com.android.ui.home.profile.help.FAQResponseFragment;
import suncor.com.android.ui.home.profile.info.PersonalInfoFragment;
import suncor.com.android.ui.home.profile.preferences.PreferencesFragment;
import suncor.com.android.ui.home.profile.transcations.TransactionsFragment;
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
    abstract CardsFragment contributesCardsFragment();

    @ContributesAndroidInjector
    abstract CardsDetailsFragment contributesCardsDetailsFragment();

    @ContributesAndroidInjector
    abstract AddCardFragment contributesAddCardFragment();

    @ContributesAndroidInjector
    abstract FAQFragment contributesFAQFragment();

    @ContributesAndroidInjector
    abstract FAQResponseFragment contributesFAQResponse();

    @ContributesAndroidInjector
    abstract PersonalInfoFragment contributesPersonalInfoFragment();

    @ContributesAndroidInjector
    abstract PreferencesFragment contributesPreferencesFragment();


    @ContributesAndroidInjector
    abstract TransactionsFragment contributTransactionFragment();
}
