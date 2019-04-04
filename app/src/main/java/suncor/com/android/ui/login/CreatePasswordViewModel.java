package suncor.com.android.ui.login;

import javax.inject.Inject;

import androidx.lifecycle.ViewModel;
import suncor.com.android.BR;
import suncor.com.android.R;
import suncor.com.android.ui.common.input.PasswordInputField;

public class CreatePasswordViewModel extends ViewModel {

    private PasswordInputField passwordField = new PasswordInputField(R.string.login_create_password_empty_error);

    @Inject
    public CreatePasswordViewModel() {

    }

    public PasswordInputField getPasswordField() {
        return passwordField;
    }

    public void validateAndContinue() {
        passwordField.setHasFocus(false);
        passwordField.notifyPropertyChanged(BR.hasFocus);
        if (passwordField.isValid()) {

        } else {
            passwordField.setShowError(true);
        }
    }
}
