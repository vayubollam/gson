package suncor.com.android.ui.login;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import suncor.com.android.BR;
import suncor.com.android.R;
import suncor.com.android.data.users.UsersApi;
import suncor.com.android.mfp.SessionManager;
import suncor.com.android.mfp.SigninResponse;
import suncor.com.android.model.Resource;
import suncor.com.android.ui.common.Event;
import suncor.com.android.ui.common.input.PasswordInputField;
import suncor.com.android.utilities.Timber;

public class CreatePasswordViewModel extends ViewModel {

    public LiveData<Resource<Boolean>> api;
    private PasswordInputField passwordField = new PasswordInputField(R.string.login_create_password_empty_error);
    private String email;
    private String emailEncrypted;
    private MutableLiveData<Event<Boolean>> buttonClickEvent = new MutableLiveData<>();

    @Inject
    public CreatePasswordViewModel(SessionManager sessionManager, UsersApi usersApi) {
        LiveData<Resource<Boolean>> passwordsApiCall = Transformations.switchMap(buttonClickEvent, (event) -> {
            if (event.getContentIfNotHandled() != null) {
                return usersApi.createPassword(email, passwordField.getText(), emailEncrypted);
            } else {
                return new MutableLiveData<>();
            }
        });

        api = Transformations.switchMap(passwordsApiCall, (result) -> {
            if (result.status == Resource.Status.SUCCESS) {
                //login the user
                Timber.d("password created, start user auto login");
                return Transformations.map(sessionManager.login(email, passwordField.getText()), (r) -> {
                    switch (r.status) {
                        case SUCCESS:
                            if (r.data.getStatus() == SigninResponse.Status.SUCCESS) {
                                Timber.d("Login succeeded");
                                return Resource.success(true);
                            } else {
                                Timber.d("Login failed, status: " + r.data.getStatus());
                                return Resource.error(r.data.getStatus().toString());
                            }
                        case ERROR:
                            Timber.d("Login failed");
                            return Resource.error(r.message);
                        default:
                            return Resource.loading();
                    }
                });
            } else {
                MutableLiveData<Resource<Boolean>> intermediateLivedata = new MutableLiveData<>();
                intermediateLivedata.setValue(result);
                return intermediateLivedata;
            }
        });
    }

    public void setEmailEncrypted(String emailEncrypted) {
        this.emailEncrypted = emailEncrypted;
    }

    public PasswordInputField getPasswordField() {
        return passwordField;
    }

    public void validateAndContinue() {
        passwordField.setHasFocus(false);
        passwordField.notifyPropertyChanged(BR.hasFocus);
        if (passwordField.isValid()) {
            buttonClickEvent.setValue(Event.newEvent(true));
        } else {
            passwordField.setShowError(true);
        }
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
