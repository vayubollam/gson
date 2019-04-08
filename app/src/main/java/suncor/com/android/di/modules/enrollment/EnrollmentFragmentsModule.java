package suncor.com.android.di.modules.enrollment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import suncor.com.android.ui.enrollment.CardQuestion;
import suncor.com.android.ui.enrollment.cardform.CardFormFragment;
import suncor.com.android.ui.enrollment.CardQuestionFragment;
import suncor.com.android.ui.enrollment.form.EnrollmentFormFragment;
import suncor.com.android.ui.enrollment.form.SecurityQuestionFragment;

@Module
abstract class EnrollmentFragmentsModule {
    @ContributesAndroidInjector
    abstract CardQuestionFragment contributeCardQuestion();

    @ContributesAndroidInjector
    abstract EnrollmentFormFragment contributeEnrollmentFormFragment();

    @ContributesAndroidInjector
    abstract SecurityQuestionFragment contributeSecurityQuestionFragment();

    @ContributesAndroidInjector
    abstract CardFormFragment contributeCardFormFragment();
}
