package suncor.com.android.di.modules.enrollment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import suncor.com.android.ui.enrollement.CardQuestion;
import suncor.com.android.ui.enrollement.form.EnrollmentFormFragment;
import suncor.com.android.ui.enrollement.form.SecurityQuestionFragment;

@Module
abstract class EnrollmentFragmentsModule {
    @ContributesAndroidInjector
    abstract CardQuestion contributeCardQuestion();

    @ContributesAndroidInjector
    abstract EnrollmentFormFragment contributeEnrollmentFormFragment();

    @ContributesAndroidInjector
    abstract SecurityQuestionFragment contributeSecurityQuestionFragment();
}
