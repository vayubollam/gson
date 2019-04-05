package suncor.com.android.di.modules.login;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import suncor.com.android.ui.login.CreatePasswordFragment;
import suncor.com.android.ui.login.LoginFragment;

@Module
abstract class LoginFragmentsModule {
    @ContributesAndroidInjector
    abstract LoginFragment contributesLoginFragment();

    @ContributesAndroidInjector
    abstract CreatePasswordFragment contributesCreatePasswordFragment();
}
