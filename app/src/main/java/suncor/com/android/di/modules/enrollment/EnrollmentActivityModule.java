package suncor.com.android.di.modules.enrollment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import suncor.com.android.ui.enrollement.EnrollmentActivity;

@Module
public abstract class EnrollmentActivityModule {
    @ContributesAndroidInjector(modules = EnrollmentFragmentsModule.class)
    abstract EnrollmentActivity contributeEnrollmentActivity();
}
