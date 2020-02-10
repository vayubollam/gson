package suncor.com.android.ui.resetpassword;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import suncor.com.android.R;
import suncor.com.android.data.resetpassword.ForgotPasswordProfileApi;
import suncor.com.android.model.Resource;
import suncor.com.android.model.resetpassword.SecurityQuestion;
import suncor.com.android.ui.common.Event;
import suncor.com.android.ui.common.input.InputField;

public class ResetPasswordSecurityQuestionValidationViewModel extends ViewModel {

    public InputField questionAnswerField = new InputField(R.string.profile_security_question_error);
    public LiveData<Resource<SecurityQuestion>> securityQuestionLiveData;
    public LiveData<Resource<String>> securityAnswerLiveData;
    private MutableLiveData<Event<Boolean>> loadSecurityQuestion = new MutableLiveData<>();
    private MutableLiveData<Event<Boolean>> validateSecurityQuestion = new MutableLiveData<>();
    private String GUID;
    private String questionId;
    private String profileIdEncrypted;

    @Inject
    public ResetPasswordSecurityQuestionValidationViewModel(ForgotPasswordProfileApi api) {
        securityQuestionLiveData = Transformations.switchMap(loadSecurityQuestion, event -> {
                    if (event.getContentIfNotHandled() != null) {
                        return api.getSecurityQuestionToResetPassword(GUID);
                    }
                    return new MutableLiveData<>();
                }
        );

        securityAnswerLiveData = Transformations.switchMap(validateSecurityQuestion, event -> {
            if (event.getContentIfNotHandled() != null) {
                return api.validateSecurityQuestion(questionId, questionAnswerField.getText().trim(),profileIdEncrypted, GUID);
            }
            return new MutableLiveData<>();
        });

        loadSecurityQuestion.postValue(Event.newEvent(true));
    }



    public void loadQuestion() {
        loadSecurityQuestion.postValue(Event.newEvent(true));
    }

    public void setGUID(String appLinkData) {
        this.GUID = appLinkData;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public void setProfileIdEncrypted(String profileIdEncrypted) {
        this.profileIdEncrypted = profileIdEncrypted;
    }

    public void validateAndContinue() {
        if (!questionAnswerField.isValid()) {
            questionAnswerField.setShowError(true);
        } else {
            validateSecurityQuestion.postValue(Event.newEvent(true));
        }
    }
}