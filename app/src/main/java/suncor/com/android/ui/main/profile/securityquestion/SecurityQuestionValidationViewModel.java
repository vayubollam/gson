package suncor.com.android.ui.main.profile.securityquestion;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import suncor.com.android.R;
import suncor.com.android.data.profiles.ProfilesApi;
import suncor.com.android.model.Resource;
import suncor.com.android.model.account.SecurityQuestion;
import suncor.com.android.ui.common.Event;
import suncor.com.android.ui.common.input.InputField;

public class SecurityQuestionValidationViewModel extends ViewModel {
    public InputField questionAnswer = new InputField(R.string.profile_security_question_error);
    public LiveData<Resource<SecurityQuestion>> securityQuestion;
    private MutableLiveData<Event<Boolean>> loadSecurityQuestion = new MutableLiveData<>();

    @Inject
    public SecurityQuestionValidationViewModel(ProfilesApi profilesApi) {
        securityQuestion = Transformations.switchMap(loadSecurityQuestion, event -> {
                    if (event.getContentIfNotHandled() != null) {
                        return profilesApi.getSecurityQuestion();
                    }
                    return new MutableLiveData<>();
                }
        );
        loadSecurityQuestion.postValue(Event.newEvent(true));
    }

    public void validateAndContinue() {
        if (!questionAnswer.isValid()) {
            questionAnswer.setShowError(true);
        }
    }

    public void loadQuestion() {
        loadSecurityQuestion.postValue(Event.newEvent(true));
    }
}
