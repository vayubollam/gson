package suncor.com.android.di.modules.resetpassword;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import suncor.com.android.ui.login.LoginFragment;
import suncor.com.android.ui.main.home.HomeFragment;
import suncor.com.android.ui.resetpassword.ResetPasswordFragment;
import suncor.com.android.ui.resetpassword.ResetPasswordSecurityQuestionValidationFragment;

@Module
abstract class ResetPasswordFragmentsModule {
    @ContributesAndroidInjector
    abstract ResetPasswordSecurityQuestionValidationFragment contributeResetPasswordSecurityQuestionValidationFragment();

    @ContributesAndroidInjector
    abstract ResetPasswordFragment contributeResetPasswordFragment();
}
