package suncor.com.android.ui.resetpassword;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import suncor.com.android.R;
import suncor.com.android.data.resetpassword.ForgotPasswordProfileApi;
import suncor.com.android.model.Resource;
import suncor.com.android.ui.common.Event;
import suncor.com.android.ui.common.input.EmailInputField;

public class ForgotPasswordViewModel extends ViewModel {

    public LiveData<Resource<Boolean>> sendEmailApiCall;
    private MutableLiveData<Event<Boolean>> updateEvent = new MutableLiveData<>();
    private EmailInputField emailInputField = new EmailInputField(R.string.enrollment_email_empty_error, R.string.enrollment_email_format_error, R.string.enrollment_email_restricted_error);

    @Inject
    public ForgotPasswordViewModel(ForgotPasswordProfileApi api) {

        sendEmailApiCall = Transformations.switchMap(updateEvent, event -> {
            if (event.getContentIfNotHandled() != null) {
                return api.generateResetPasswordEmail(emailInputField.getText());
            } else {
                return new MutableLiveData<>();
            }
        });
    }

    public void generateForgotPasswordLink() {
        if (!emailInputField.isValid()) {
            emailInputField.setShowError(true);
        } else {
            updateEvent.postValue(Event.newEvent(true));
        }
    }

    public EmailInputField getEmailInputField() {
        return emailInputField;
    }
}