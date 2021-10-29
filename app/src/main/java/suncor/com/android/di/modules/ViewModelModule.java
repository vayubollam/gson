package suncor.com.android.di.modules;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.di.viewmodel.ViewModelKey;
import suncor.com.android.ui.enrollment.cardform.CardFormViewModel;
import suncor.com.android.ui.enrollment.form.EnrollmentFormViewModel;
import suncor.com.android.ui.enrollment.form.SecurityQuestionViewModel;
import suncor.com.android.ui.login.CreatePasswordViewModel;
import suncor.com.android.ui.login.LoginViewModel;
import suncor.com.android.ui.main.MainViewModel;
import suncor.com.android.ui.main.actionmenu.ActionMenuViewModel;
import suncor.com.android.ui.main.carwash.reload.CarwashTransactionViewModel;
import suncor.com.android.ui.main.pap.fuelup.FuelUpViewModel;
import suncor.com.android.ui.main.pap.receipt.ReceiptViewModel;
import suncor.com.android.ui.main.pap.selectpump.SelectPumpViewModel;
import suncor.com.android.ui.main.wallet.cards.add.AddCardViewModel;
import suncor.com.android.ui.main.wallet.cards.details.CardDetailsViewModel;
import suncor.com.android.ui.main.wallet.cards.list.CardsViewModel;
import suncor.com.android.ui.main.carwash.CarWashCardViewModel;
import suncor.com.android.ui.main.carwash.CarWashSharedViewModel;
import suncor.com.android.ui.main.carwash.singleticket.SingleTicketViewModel;
import suncor.com.android.ui.main.home.HomeViewModel;
import suncor.com.android.ui.main.profile.address.AddressViewModel;
import suncor.com.android.ui.main.profile.help.FAQViewModel;
import suncor.com.android.ui.main.profile.info.PersonalInfoViewModel;
import suncor.com.android.ui.main.profile.preferences.PreferencesViewModel;
import suncor.com.android.ui.main.profile.securityquestion.SecurityQuestionValidationViewModel;
import suncor.com.android.ui.main.profile.transcations.TransactionsViewModel;
import suncor.com.android.ui.main.rewards.RewardsSignedInViewModel;
import suncor.com.android.ui.main.rewards.redeem.GiftCardValueConfirmationViewModel;
import suncor.com.android.ui.main.stationlocator.StationsViewModel;
import suncor.com.android.ui.main.stationlocator.favourites.FavouritesViewModel;
import suncor.com.android.ui.main.stationlocator.search.SearchViewModel;
import suncor.com.android.ui.main.wallet.payments.add.AddPaymentViewModel;
import suncor.com.android.ui.main.wallet.payments.details.PaymentDetailsViewModel;
import suncor.com.android.ui.main.wallet.payments.list.PaymentsViewModel;
import suncor.com.android.ui.resetpassword.ForgotPasswordViewModel;
import suncor.com.android.ui.resetpassword.ResetPasswordSecurityQuestionValidationViewModel;
import suncor.com.android.ui.resetpassword.ResetPasswordViewModel;

@Module
public abstract class ViewModelModule {

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(ViewModelFactory factory);


    /*
     * This method basically says
     * inject this object into a Map using the @IntoMap annotation,
     * with the  class as key,
     * and a Provider that will build a MovieListViewModel
     * object.
     *
     * */

    //MainActivity ViewModels
    @Binds
    @IntoMap
    @ViewModelKey(StationsViewModel.class)
    protected abstract ViewModel stationsViewModel(StationsViewModel stationsViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel.class)
    protected abstract ViewModel homeViewModel(HomeViewModel homeViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ActionMenuViewModel.class)
    protected abstract ViewModel actionMenuViewModel(ActionMenuViewModel actionMenuViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(SearchViewModel.class)
    protected abstract ViewModel searchViewModel(SearchViewModel searchViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(FavouritesViewModel.class)
    protected abstract ViewModel favouritesViewModel(FavouritesViewModel favouritesViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(CardsViewModel.class)
    protected abstract ViewModel cardsViewModel(CardsViewModel cardsViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(AddCardViewModel.class)
    protected abstract ViewModel addCardViewModel(AddCardViewModel addCardViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(PaymentsViewModel.class)
    protected abstract ViewModel paymentsViewModel(PaymentsViewModel paymentsViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(PaymentDetailsViewModel.class)
    protected abstract ViewModel paymentDetailsViewModel(PaymentDetailsViewModel paymentDetailsViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(AddPaymentViewModel.class)
    protected abstract ViewModel addPaymentViewModel(AddPaymentViewModel addPaymentViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(SelectPumpViewModel.class)
    protected abstract ViewModel selectPumpViewModel(SelectPumpViewModel selectPumpViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(PersonalInfoViewModel.class)
    protected abstract ViewModel addPersonalInfoViewModel(PersonalInfoViewModel personalInfoViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(PreferencesViewModel.class)
    protected abstract ViewModel addPreferencesViewModel(PreferencesViewModel preferencesViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(FAQViewModel.class)
    protected abstract ViewModel faqViewModel(FAQViewModel faqViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(CardDetailsViewModel.class)
    protected abstract ViewModel cardDetailViewModel(CardDetailsViewModel cardDetailsViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(TransactionsViewModel.class)
    protected abstract ViewModel transactionsViewModel(TransactionsViewModel transactionsViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(RewardsSignedInViewModel.class)
    protected abstract ViewModel reedeemViewModel(RewardsSignedInViewModel rewardsSignedInViewModel);

    //EnrollmentActivity ViewModels
    @Binds
    @IntoMap
    @ViewModelKey(EnrollmentFormViewModel.class)
    protected abstract ViewModel enrollmentViewModel(EnrollmentFormViewModel enrollmentFormViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(SecurityQuestionViewModel.class)
    protected abstract ViewModel securityQuestionViewModel(SecurityQuestionViewModel securityQuestionViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(CardFormViewModel.class)
    protected abstract ViewModel cardformViewModel(CardFormViewModel cardFormViewModel);


    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel.class)
    protected abstract ViewModel loginViewModel(LoginViewModel loginViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(CreatePasswordViewModel.class)
    protected abstract ViewModel createPasswordViewModel(CreatePasswordViewModel createPasswordViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(SecurityQuestionValidationViewModel.class)
    protected abstract ViewModel createSecurityQuestionValidationViewModel(SecurityQuestionValidationViewModel securityQuestionValidationViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(AddressViewModel.class)
    protected abstract ViewModel createAddressViewModel(AddressViewModel addressViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel.class)
    protected abstract ViewModel MerchantViewModel(MainViewModel MerchantViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(GiftCardValueConfirmationViewModel.class)
    protected abstract ViewModel GiftCardConfirmationViewModel(GiftCardValueConfirmationViewModel giftCardValueConfirmationViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(CarWashCardViewModel.class)
    protected abstract ViewModel CarWashCardViewModel(CarWashCardViewModel carWashCardViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(CarWashSharedViewModel.class)
    protected abstract ViewModel CarWashSharedViewModel(CarWashSharedViewModel carWashSharedViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ForgotPasswordViewModel.class)
    protected abstract ViewModel ForgotPasswordViewModel(ForgotPasswordViewModel forgotPasswordViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ResetPasswordSecurityQuestionValidationViewModel.class)
    protected abstract ViewModel ResetPasswordSecurityQuestionValidationViewModel(ResetPasswordSecurityQuestionValidationViewModel resetPasswordSecurityQuestionValidationViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ResetPasswordViewModel.class)
    protected abstract ViewModel ResetPasswordViewModel(ResetPasswordViewModel resetPasswordViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(SingleTicketViewModel.class)
    protected abstract ViewModel SingleTicketViewModel(SingleTicketViewModel singleTicketViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(FuelUpViewModel.class)
    protected abstract ViewModel fuelUpViewModel(FuelUpViewModel fuelUpViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ReceiptViewModel.class)
    protected abstract ViewModel receiptViewModel(ReceiptViewModel receiptViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(CarwashTransactionViewModel.class)
    protected abstract ViewModel carwashTransactionViewModel(CarwashTransactionViewModel carwashTransactionViewModel);

}