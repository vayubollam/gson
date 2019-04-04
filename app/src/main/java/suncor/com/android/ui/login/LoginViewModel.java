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
import suncor.com.android.ui.common.input.EmailInputField;
import suncor.com.android.ui.common.input.InputField;


public class LoginViewModel extends ViewModel {

    private InputField passwordInputField;
    private EmailInputField emailInputField;
    private LiveData<Resource<SigninResponse>> loginLiveData;
    private MutableLiveData<Event<Boolean>> loginSuccessEvent = new MutableLiveData<>();
    private MutableLiveData<Event<LoginFailResponse>> loginFailedEvent = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<Event<Boolean>> loginEvent = new MutableLiveData<>();
    private MutableLiveData<Event<Boolean>> navigateToPasswordResetEvent = new MutableLiveData<>();
    private MutableLiveData<Event<Boolean>> callCustomerService = new MutableLiveData<>();

    @Inject
    public LoginViewModel(SessionManager sessionManager) {
        this.passwordInputField = new InputField(R.string.enrollment_password_empty_error);
        this.emailInputField = new EmailInputField(R.string.login_email_field_error, R.string.enrollment_email_format_error);
        loginLiveData = Transformations.switchMap(loginEvent, (event) -> {
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
                        int remainingAttempts = response.getRemainingAttempts();
                        if (remainingAttempts == SessionManager.LOGIN_ATTEMPTS - 1 || remainingAttempts == -1) {
                            loginFailedEvent.postValue(Event.newEvent(new LoginFailResponse(
                                    R.string.login_invalid_credentials_dialog_title,
                                    new ErrorMessage(R.string.login_invalid_credentials_dialog_1st_message)
                            )));
                            passwordInputField.setText("");
                            passwordInputField.notifyPropertyChanged(BR.text);
                        } else {

                            loginFailedEvent.postValue(Event.newEvent(new LoginFailResponse(
                                    R.string.login_invalid_credentials_dialog_title,
                                    new ErrorMessage(R.string.login_invalid_credentials_dialog_2nd_message, remainingAttempts, SessionManager.LOCK_TIME_MINUTES),
                                    R.string.login_invalid_credentials_reset_password,
                                    () -> navigateToPasswordResetEvent.postValue(Event.newEvent(true))
                            )));
                            passwordInputField.setText("");
                            passwordInputField.notifyPropertyChanged(BR.text);
                        }
                        break;
                    case SOFT_LOCKED:
                        loginFailedEvent.postValue(Event.newEvent(new LoginFailResponse(
                                R.string.login_invalid_credentials_dialog_title,
                                new ErrorMessage(R.string.login_invalid_credentials_dialog_1st_message, SessionManager.LOGIN_ATTEMPTS, response.getTimeOut())

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

                }


            } else if (result.status == Resource.Status.ERROR) {
                loginFailedEvent.postValue(Event.newEvent(new LoginFailResponse(
                        R.string.msg_e001_title,
                        new ErrorMessage(R.string.msg_e001_message)
                )));
            }
        });
    }

    public MutableLiveData<Event<Boolean>> getCallCustomerService() {
        return callCustomerService;
    }

    public LiveData<Event<Boolean>> getNavigateToPasswordResetEvent() {
        return navigateToPasswordResetEvent;
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

    public EmailInputField getEmailInputField() {
        return emailInputField;
    }

    public void onClickSignIn() {
        if (this.validateInput()) {
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


