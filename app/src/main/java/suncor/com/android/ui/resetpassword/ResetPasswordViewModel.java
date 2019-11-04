package suncor.com.android.ui.resetpassword;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import suncor.com.android.R;
import suncor.com.android.data.resetpassword.ForgotPasswordProfileApi;
import suncor.com.android.model.Resource;
import suncor.com.android.model.resetpassword.ResetPasswordRequest;
import suncor.com.android.ui.common.Event;
import suncor.com.android.ui.common.input.PasswordInputField;

public class ResetPasswordViewModel extends ViewModel {

    private PasswordInputField passwordField = new PasswordInputField(R.string.enrollment_password_empty_error);
    private MutableLiveData<Event> resetPasswordEvent = new MutableLiveData<>();
    public LiveData<Resource<Boolean>>  resetPasswordLiveData;
    private ResetPasswordRequest request;

    @Inject
    public ResetPasswordViewModel(ForgotPasswordProfileApi api) {

        resetPasswordLiveData = Transformations.switchMap(resetPasswordEvent, event -> {
            if (event.getContentIfNotHandled() != null) {
                request.setPassword(passwordField.getText());
                return api.resetPassword(request.getProfileIdEncrypted(), request.getGUID(), request);
            } else {
                return new MutableLiveData<>();
            }
        });

    }

    public PasswordInputField getPasswordField() {
        return passwordField;
    }

    public void reset() {
        if (!passwordField.isValid()) {
            passwordField.setShowError(true);
        } else {
            resetPasswordEvent.setValue(Event.newEvent(true));
        }
    }

    public void setRequest(ResetPasswordRequest resetPasswordRequest) {
        this.request = resetPasswordRequest;
    }
}