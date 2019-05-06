package suncor.com.android.ui.login;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import suncor.com.android.BR;
import suncor.com.android.R;
import suncor.com.android.mfp.SessionManager;
import suncor.com.android.mfp.SigninResponse;
import suncor.com.android.model.Resource;
import suncor.com.android.ui.common.Event;
import suncor.com.android.ui.common.input.InputField;


public class LoginViewModel extends ViewModel {

    private InputField passwordInputField;
    private InputField emailInputField;
    private MutableLiveData<Event<Boolean>> loginSuccessEvent = new MutableLiveData<>();
    private MutableLiveData<Event<LoginFailResponse>> loginFailedEvent = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<Event<Boolean>> loginEvent = new MutableLiveData<>();
    private MutableLiveData<Event<Boolean>> passwordResetEvent = new MutableLiveData<>();
    private MutableLiveData<Event<Boolean>> callCustomerService = new MutableLiveData<>();
    private MutableLiveData<Event<Boolean>> createPasswordEvent = new MutableLiveData<>();

    @Inject
    public LoginViewModel(SessionManager sessionManager) {
        this.passwordInputField = new InputField(R.string.login_password_field_error);
        this.emailInputField = new InputField(R.string.login_email_field_error);
        LiveData<Resource<SigninResponse>> loginLiveData = Transformations.switchMap(loginEvent, (event) -> {
            if (event.getContentIfNotHandled() != null) {
                return sessionManager.login(emailInputField.getText(), passwordInputField.getText());
            }

            return new MutableLiveData<>();
        });

        loginLiveData.observeForever((result) -> {
            isLoading.postValue(result.status == Resource.Status.LOADING);
            if (result.status == Resource.Status.SUCCESS) {
                SigninResponse response = result.data;
                switch (response.getStatus()) {
                    case SUCCESS:
                        loginSuccessEvent.postValue(Event.newEvent(true));
                        break;
                    case WRONG_CREDENTIALS:
                        passwordInputField.setText("");
                        passwordInputField.notifyPropertyChanged(BR.text);
                        int remainingAttempts = response.getRemainingAttempts();
                        if (remainingAttempts >= SessionManager.LOGIN_ATTEMPTS - 2 || remainingAttempts == -1) {
                            loginFailedEvent.postValue(Event.newEvent(new LoginFailResponse(
                                    R.string.login_invalid_credentials_dialog_title,
                                    new ErrorMessage(R.string.login_invalid_credentials_dialog_1st_message)
                            )));
                        } else {
                            loginFailedEvent.postValue(Event.newEvent(new LoginFailResponse(
                                    R.string.login_invalid_credentials_dialog_title,
                                    new ErrorMessage(R.string.login_invalid_credentials_dialog_2nd_message, remainingAttempts, SessionManager.LOCK_TIME_MINUTES),
                                    R.string.login_invalid_credentials_reset_password,
                                    this::requestResetPassword
                            )));
                        }
                        break;
                    case SOFT_LOCKED:
                        loginFailedEvent.postValue(Event.newEvent(new LoginFailResponse(
                                R.string.login_soft_lock_alert_title,
                                new ErrorMessage(R.string.login_soft_lock_alert_message, SessionManager.LOGIN_ATTEMPTS, response.getTimeOut()),
                                R.string.login_invalid_credentials_reset_password,
                                this::requestResetPassword
                        )));
                        break;
                    case HARD_LOCKED:
                        loginFailedEvent.postValue(Event.newEvent(new LoginFailResponse(
                                R.string.login_hard_lock_alert_title,
                                new ErrorMessage(R.string.login_hard_lock_alert_message),
                                R.string.login_hard_lock_alert_call_button,
                                () -> callCustomerService.postValue(Event.newEvent(true))
                        )));
                        break;
                    case OTHER_FAILURE:
                        loginFailedEvent.postValue(Event.newEvent(new LoginFailResponse(
                                R.string.msg_e001_title,
                                new ErrorMessage(R.string.msg_e001_message)
                        )));
                        break;
                    case PASSWORD_RESET:
                        createPasswordEvent.postValue(Event.newEvent(true));
                        break;
                }


            } else if (result.status == Resource.Status.ERROR) {
                loginFailedEvent.postValue(Event.newEvent(new LoginFailResponse(
                        R.string.msg_e001_title,
                        new ErrorMessage(R.string.msg_e001_message)
                )));
            }
        });
    }

    public MutableLiveData<Event<Boolean>> getCreatePasswordEvent() {
        return createPasswordEvent;
    }

    public MutableLiveData<Event<Boolean>> getCallCustomerService() {
        return callCustomerService;
    }

    public LiveData<Event<Boolean>> getPasswordResetEvent() {
        return passwordResetEvent;
    }

    public LiveData<Event<Boolean>> getLoginSuccessEvent() {
        return loginSuccessEvent;
    }

    public LiveData<Event<LoginFailResponse>> getLoginFailedEvent() {
        return loginFailedEvent;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public InputField getPasswordInputField() {
        return passwordInputField;
    }

    public InputField getEmailInputField() {
        return emailInputField;
    }

    public void onClickSignIn() {
        if (this.validateInput()) {
            loginEvent.postValue(Event.newEvent(true));
        }
    }

    private boolean validateInput() {
        boolean isValid = true;
        if (!emailInputField.isValid()) {
            emailInputField.setShowError(true);
            isValid = false;
        }
        if (!passwordInputField.isValid()) {
            passwordInputField.setShowError(true);
            isValid = false;
        }
        return isValid;
    }

    public void requestResetPassword() {
        passwordResetEvent.postValue(Event.newEvent(true));
    }

    public interface ErrorAlertCallback {
        void call();
    }

    public static class LoginFailResponse {
        int title;
        ErrorMessage message;
        int buttonTitle;
        ErrorAlertCallback callback;

        public LoginFailResponse(int title, ErrorMessage message, int buttonTitle, ErrorAlertCallback callback) {
            this.title = title;
            this.message = message;
            this.callback = callback;
            this.buttonTitle = buttonTitle;
        }

        public LoginFailResponse(int title, ErrorMessage message) {
            this.title = title;
            this.message = message;
        }
    }

    public static class ErrorMessage {
        int content;
        Object[] args;

        public ErrorMessage(int content, Object... args) {
            this.content = content;
            this.args = args;
        }

        public ErrorMessage(int content) {
            this.content = content;
        }
    }
}


