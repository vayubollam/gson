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
    public InputField questionAnswerField = new InputField(R.string.profile_security_question_error);
    public LiveData<Resource<SecurityQuestion>> securityQuestionLiveData;
    public LiveData<Resource<String>> securityAnswerLiveData;
    private MutableLiveData<Event<Boolean>> loadSecurityQuestion = new MutableLiveData<>();
    private MutableLiveData<Event<Boolean>> validateSecurityQuestion = new MutableLiveData<>();

    @Inject
    public SecurityQuestionValidationViewModel(ProfilesApi profilesApi) {
        securityQuestionLiveData = Transformations.switchMap(loadSecurityQuestion, event -> {
                    if (event.getContentIfNotHandled() != null) {
                        return profilesApi.getSecurityQuestion();
                    }
                    return new MutableLiveData<>();
                }
        );
        securityAnswerLiveData = Transformations.switchMap(validateSecurityQuestion, event -> {
            if (event.getContentIfNotHandled() != null) {
                return profilesApi.validateSecurityQuestion(questionAnswerField.getText());
            }
            return new MutableLiveData<>();
        });

        loadSecurityQuestion.postValue(Event.newEvent(true));
    }

    public void validateAndContinue() {
        if (!questionAnswerField.isValid()) {
            questionAnswerField.setShowError(true);
        } else {
            validateSecurityQuestion.postValue(Event.newEvent(true));
        }
    }

    public void loadQuestion() {
        loadSecurityQuestion.postValue(Event.newEvent(true));
    }
}
