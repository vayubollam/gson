package suncor.com.android.ui.login;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import suncor.com.android.R;
import suncor.com.android.mfp.SessionManager;
import suncor.com.android.mfp.SigninResponse;
import suncor.com.android.model.Resource;
import suncor.com.android.ui.common.Event;
import suncor.com.android.ui.common.input.EmailInputField;
import suncor.com.android.ui.common.input.InputField;


import javax.inject.Inject;


public class LoginViewModel extends ViewModel {

    private  SessionManager sessionManager;
    private InputField passwordInputField;
    private EmailInputField emailInputField;

    public LiveData<Resource<SigninResponse>> loginLiveData;

    private MutableLiveData<Event<Boolean>> loginEvent = new MutableLiveData<>();

    @Inject
    public LoginViewModel(SessionManager sessionManager) {
        this.passwordInputField  = new InputField(R.string.enrollment_password_empty_error);
        this.emailInputField = new EmailInputField(R.string.login_email_field_error,R.string.enrollment_email_format_error);
        this.sessionManager = sessionManager;
        loginLiveData = Transformations.switchMap(loginEvent, (event) -> {
            if(event.getContentIfNotHandled() != null){
                return sessionManager.login(emailInputField.getText(), passwordInputField.getText());
            }

            return new MutableLiveData<>();
        });
    }

    public InputField getPasswordInputField() {
        return passwordInputField;
    }

    public EmailInputField getEmailInputField() {
        return emailInputField;
    }

    public void onClickSignIn() {
        if (this.validateInput()){
            loginEvent.postValue(Event.newEvent(true));
        }


    }

    private boolean validateInput() {
         Boolean errorUnfounded = true;
        if (this.getEmailInputField().isEmpty()) {
            this.getEmailInputField().setShowError(true);
            errorUnfounded = false;
        }
        if (this.getPasswordInputField().isEmpty()) {
            this.getPasswordInputField().setShowError(true);
            errorUnfounded = false;
        }
        return errorUnfounded;


}
}


