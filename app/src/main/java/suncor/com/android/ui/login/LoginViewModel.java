package suncor.com.android.ui.login;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import suncor.com.android.BR;
import suncor.com.android.BuildConfig;
import suncor.com.android.R;
import suncor.com.android.data.settings.SettingsApi;
import suncor.com.android.mfp.SessionManager;
import suncor.com.android.mfp.SigninResponse;
import suncor.com.android.model.Resource;
import suncor.com.android.model.SettingsResponse;
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
    private MutableLiveData<Event<String>> createPasswordEvent = new MutableLiveData<>();
    private MutableLiveData<Event<Boolean>> navigateToHomeEvent = new MutableLiveData<>();
    private boolean isLoginFromEnrollment;


    private SettingsApi settingsApi;

    @Inject
    public LoginViewModel(SessionManager sessionManager, SettingsApi settingsApi) {
        this.settingsApi = settingsApi;
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
                        sessionManager.setAccountState(isLoginFromEnrollment ? SessionManager.AccountState.JUST_ENROLLED : SessionManager.AccountState.REGULAR_LOGIN);
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
                    case UNEXPECTED_FAILURE:
                        LoginFailResponse failResponse = new LoginFailResponse(
                                R.string.clear_cache_dialog_title,
                                new ErrorMessage(R.string.clear_cache_dialog_message),
                                R.string.settings_failure_dialog_button
                        );
                        failResponse.positiveButtonCallback = () -> deleteAppData();
                        loginFailedEvent.postValue(Event.newEvent(failResponse));
                        break;

                    case PASSWORD_RESET:
                        createPasswordEvent.postValue(Event.newEvent(response.getAdditionalData()));
                        break;
                    case SERVER_FAILURE:
                        LoginFailResponse serverResponse = new LoginFailResponse(
                                R.string.suncor039_error_title,
                                new ErrorMessage(R.string.suncor039_error_message),
                                R.string.ok
                        );
                        loginFailedEvent.postValue(Event.newEvent(serverResponse));
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

    public void deleteAppData() {
        try {
            String packageName = BuildConfig.APPLICATION_ID;
            Runtime runtime = Runtime.getRuntime();
            runtime.exec("pm clear " + packageName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void setLoginFromEnrollment(boolean loginFromEnrollment) {
        isLoginFromEnrollment = loginFromEnrollment;
    }

    public LiveData<Event<String>> getCreatePasswordEvent() {
        return createPasswordEvent;
    }

    public LiveData<Event<Boolean>> getCallCustomerService() {
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

    public LiveData<Event<Boolean>> getNavigateToHomeEvent() {
        return navigateToHomeEvent;
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

    public LiveData<SettingsResponse.Settings> retrieveSettings() {
        return Transformations.map(settingsApi.retrieveSettings(), result -> {
            if (result.status == Resource.Status.SUCCESS) {
                return result.data.getSettings();
            }
            return null;
        });
    }

    public void onClickSignIn() {
        if (this.validateInput()) {
            loginEvent.postValue(Event.newEvent(true));
        }
    }

    public void fingerPrintConfirmed(String email, String password) {
        emailInputField.setText(email);
        passwordInputField.setText(password);
        loginEvent.postValue(Event.newEvent(true));


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
        int negativeButtonTitle;
        ErrorAlertCallback negativeButtonCallBack;
        int positiveButtonTitle = R.string.ok;
        ErrorAlertCallback positiveButtonCallback;

        public LoginFailResponse(int title, ErrorMessage message, int negativeButtonTitle, ErrorAlertCallback negativeButtonCallBack) {
            this.title = title;
            this.message = message;
            this.negativeButtonCallBack = negativeButtonCallBack;
            this.negativeButtonTitle = negativeButtonTitle;
        }

        public LoginFailResponse(int title, ErrorMessage message) {
            this.title = title;
            this.message = message;
        }

        public LoginFailResponse(int title, ErrorMessage message, int customActionTitle) {
            this.title = title;
            this.message = message;
            positiveButtonTitle= customActionTitle;
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


