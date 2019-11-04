package suncor.com.android.di.modules.login;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import suncor.com.android.ui.login.LoginActivity;
import suncor.com.android.ui.resetpassword.ForgotPasswordFragment;

@Module
public abstract class LoginActivityModule {
    @ContributesAndroidInjector(modules = LoginFragmentsModule.class)
    abstract LoginActivity contributeLoginActivity();

    @ContributesAndroidInjector
    abstract ForgotPasswordFragment contributeForgotPasswordFragment();
}
