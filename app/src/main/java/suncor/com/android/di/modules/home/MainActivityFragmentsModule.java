package suncor.com.android.di.modules.home;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import suncor.com.android.ui.main.actionmenu.ActionMenuFragment;
import suncor.com.android.ui.main.carwash.activate.CarwashActivatedFragment;
import suncor.com.android.ui.main.pap.fuelling.FuellingFragment;
import suncor.com.android.ui.main.pap.fuelup.FuelUpFragment;
import suncor.com.android.ui.main.pap.receipt.ReceiptFragment;
import suncor.com.android.ui.main.pap.selectpump.SelectPumpFragment;
import suncor.com.android.ui.main.rewards.thirdpartygiftcard.MoreEGiftCardCategoriesFragment;
import suncor.com.android.ui.main.stationlocator.NearestStationFragment;
import suncor.com.android.ui.main.wallet.WalletFragment;
import suncor.com.android.ui.main.wallet.cards.add.AddCardFragment;
import suncor.com.android.ui.main.wallet.cards.details.CardsDetailsFragment;
import suncor.com.android.ui.main.wallet.cards.list.CardsFragment;
import suncor.com.android.ui.main.carwash.CarWashActivationSecurityFragment;
import suncor.com.android.ui.main.carwash.CarWashBarCodeFragment;
import suncor.com.android.ui.main.carwash.CarWashCardFragment;
import suncor.com.android.ui.main.carwash.singleticket.SingleTicketFragment;
import suncor.com.android.ui.main.home.HomeFragment;
import suncor.com.android.ui.main.profile.ProfileFragment;
import suncor.com.android.ui.main.profile.about.AboutFragment;
import suncor.com.android.ui.main.profile.address.AddressFragment;
import suncor.com.android.ui.main.profile.address.ProvinceProfileFragment;
import suncor.com.android.ui.main.profile.help.FAQFragment;
import suncor.com.android.ui.main.profile.help.FAQResponseFragment;
import suncor.com.android.ui.main.profile.info.PersonalInfoFragment;
import suncor.com.android.ui.main.profile.preferences.PreferencesFragment;
import suncor.com.android.ui.main.profile.securityquestion.SecurityQuestionValidationFragment;
import suncor.com.android.ui.main.profile.transcations.TransactionDetailFragment;
import suncor.com.android.ui.main.profile.transcations.TransactionsFragment;
import suncor.com.android.ui.main.rewards.MerchantDetailsFragment;
import suncor.com.android.ui.main.rewards.RedeemReceiptFragment;
import suncor.com.android.ui.main.rewards.RewardsDetailsFragment;
import suncor.com.android.ui.main.rewards.RewardsDiscoveryFragment;
import suncor.com.android.ui.main.rewards.RewardsGuestFragment;
import suncor.com.android.ui.main.rewards.RewardsSignedInFragment;
import suncor.com.android.ui.main.rewards.redeem.GiftCardValueConfirmationFragment;
import suncor.com.android.ui.main.stationlocator.FiltersFragment;
import suncor.com.android.ui.main.stationlocator.StationDetailsDialog;
import suncor.com.android.ui.main.stationlocator.StationsFragment;
import suncor.com.android.ui.main.stationlocator.favourites.FavouritesFragment;
import suncor.com.android.ui.main.stationlocator.search.SearchFragment;
import suncor.com.android.ui.main.wallet.payments.add.AddPaymentFragment;
import suncor.com.android.ui.main.wallet.payments.details.PaymentsDetailsFragment;
import suncor.com.android.ui.main.wallet.payments.list.PaymentsFragment;
import suncor.com.android.ui.tutorial.TutorialFragment;

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
    abstract ActionMenuFragment contributeActionMenuFragment();

    @ContributesAndroidInjector
    abstract FiltersFragment contributesFiltersFragment();

    @ContributesAndroidInjector
    abstract SearchFragment contributesSearchFragment();

    @ContributesAndroidInjector
    abstract FavouritesFragment contributesFavouritesFragment();

    @ContributesAndroidInjector
    abstract WalletFragment contributesWalletFragment();

    @ContributesAndroidInjector
    abstract CardsFragment contributesCardsFragment();

    @ContributesAndroidInjector
    abstract CardsDetailsFragment contributesCardsDetailsFragment();

    @ContributesAndroidInjector
    abstract AddCardFragment contributesAddCardFragment();

    @ContributesAndroidInjector
    abstract PaymentsFragment contributesPaymentsFragment();

    @ContributesAndroidInjector
    abstract PaymentsDetailsFragment contributesPaymentsDetailsFragment();

    @ContributesAndroidInjector
    abstract AddPaymentFragment contributesAddPaymentFragment();

    @ContributesAndroidInjector
    abstract FAQFragment contributesFAQFragment();

    @ContributesAndroidInjector
    abstract FAQResponseFragment contributesFAQResponse();

    @ContributesAndroidInjector
    abstract PersonalInfoFragment contributesPersonalInfoFragment();

    @ContributesAndroidInjector
    abstract PreferencesFragment contributesPreferencesFragment();

    @ContributesAndroidInjector
    abstract TransactionsFragment contributesTransactionFragment();

    @ContributesAndroidInjector
    abstract TransactionDetailFragment contributesTransactionDetailsFragment();

    @ContributesAndroidInjector
    abstract AboutFragment contributesAboutFragment();

    @ContributesAndroidInjector
    abstract RewardsGuestFragment contributesRewardsFragment();

    @ContributesAndroidInjector
    abstract RewardsSignedInFragment contributesRedeemFragment();

    @ContributesAndroidInjector
    abstract RewardsDetailsFragment contributesRewardsDetailsFragment();

    @ContributesAndroidInjector
    abstract RewardsDiscoveryFragment contributesRewardsDiscoveryFragment();

    @ContributesAndroidInjector
    abstract SecurityQuestionValidationFragment contributeSecurityQuestionValidationFragment();

    @ContributesAndroidInjector
    abstract AddressFragment contributesAddressFragment();

    @ContributesAndroidInjector
    abstract ProvinceProfileFragment contributeProvinceProfileFragment();

    @ContributesAndroidInjector
    abstract MerchantDetailsFragment contributesMerchantDetailsFragment();

    @ContributesAndroidInjector
    abstract RedeemReceiptFragment contributesRedeemReceiptFragment();

    @ContributesAndroidInjector
    abstract GiftCardValueConfirmationFragment contributeCardValueConfirmationFragment();

    @ContributesAndroidInjector
    abstract CarWashCardFragment contributeCarWashCardFragment();

    @ContributesAndroidInjector
    abstract SingleTicketFragment contributeSingleTicketFragment();

    @ContributesAndroidInjector
    abstract CarWashActivationSecurityFragment contributeCarWashActivationSecurityFragment();

    @ContributesAndroidInjector
    abstract CarWashBarCodeFragment contributeCarWashBarCodeFragment();

    @ContributesAndroidInjector
    abstract NearestStationFragment contributeNearestStationFragment();

    @ContributesAndroidInjector
    abstract SelectPumpFragment contributesSelectPumpFragment();

    @ContributesAndroidInjector
    abstract FuelUpFragment contributeFuelUpFragment();

    @ContributesAndroidInjector
    abstract FuellingFragment contributeFuellingFragment();

    @ContributesAndroidInjector
    abstract ReceiptFragment contributeReceiptFragment();

    @ContributesAndroidInjector
    abstract CarwashActivatedFragment contributeCarwashActivatedFragment();

    @ContributesAndroidInjector
    abstract MoreEGiftCardCategoriesFragment contributeCMoreEGiftCardCategoriesFragment();

}
