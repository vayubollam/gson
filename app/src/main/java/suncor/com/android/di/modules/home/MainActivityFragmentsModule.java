package suncor.com.android.di.modules.home;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import suncor.com.android.ui.main.cards.add.AddCardFragment;
import suncor.com.android.ui.main.cards.details.CardsDetailsFragment;
import suncor.com.android.ui.main.cards.list.CardsFragment;
import suncor.com.android.ui.main.home.HomeFragment;
import suncor.com.android.ui.main.profile.ProfileFragment;
import suncor.com.android.ui.main.profile.help.FAQFragment;
import suncor.com.android.ui.main.profile.help.FAQResponseFragment;
import suncor.com.android.ui.main.profile.info.PersonalInfoFragment;
import suncor.com.android.ui.main.profile.preferences.PreferencesFragment;
import suncor.com.android.ui.main.profile.transcations.TransactionsFragment;
import suncor.com.android.ui.main.stationlocator.StationDetailsDialog;
import suncor.com.android.ui.main.stationlocator.StationsFragment;
import suncor.com.android.ui.main.stationlocator.favourites.FavouritesFragment;
import suncor.com.android.ui.main.stationlocator.search.SearchFragment;

@Module
abstract class MainActivityFragmentsModule {
    @ContributesAndroidInjector
    abstract StationsFragment contributeStationsFragment();

    @ContributesAndroidInjector
    abstract ProfileFragment contributeProfileFragment();

    @ContributesAndroidInjector
    abstract StationDetailsDialog contributeStationDetailsDialog();

    @ContributesAndroidInjector
    abstract HomeFragment contributeHomeFragment();

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