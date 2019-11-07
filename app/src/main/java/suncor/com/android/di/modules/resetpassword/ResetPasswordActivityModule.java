package suncor.com.android.di.modules.resetpassword;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import suncor.com.android.ui.resetpassword.ResetPasswordActivity;

@Module
public abstract class ResetPasswordActivityModule {
    @ContributesAndroidInjector(modules = ResetPasswordFragmentsModule.class)
    abstract ResetPasswordActivity contributeResetPasswordActivity();
}
