package suncor.com.android.ui.enrollement.form;

import java.util.ArrayList;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import suncor.com.android.R;
import suncor.com.android.data.repository.account.EmailCheckApi;
import suncor.com.android.model.Resource;

public class EnrollmentFormViewModel extends ViewModel {

    private EmailCheckApi emailCheckApi;

    public LiveData<Resource<EmailCheckApi.EmailState>> emailCheckLiveData;

    private InputField firstNameField = new InputField(R.string.enrollment_first_name_error);
    private InputField lastNameField = new InputField(R.string.enrollment_last_name_error);
    private EmailInputField emailInputField = new EmailInputField(R.string.enrollment_email_empty_error, R.string.enrollment_email_format_error);
    private PasswordInputField passwordField = new PasswordInputField(R.string.enrollment_password_empty_error);
    private InputField securityQuestionField = new InputField();
    private InputField securityAnswerField = new InputField();
    private InputField streetAddressField = new InputField(R.string.enrollment_street_address_error);
    private InputField cityField = new InputField(R.string.enrollment_city_error);
    private InputField provinceField = new InputField(R.string.enrollment_province_error);
    private InputField postalCodeField = new InputField(R.string.enrollment_postalcode_error);
    private InputField phoneField = new InputField();
    public MutableLiveData<Integer> selectedProvince = new MutableLiveData<>();

    private ArrayList<InputField> requiredFields = new ArrayList<>();

    public EnrollmentFormViewModel(EmailCheckApi emailCheckApi) {
        this.emailCheckApi = emailCheckApi;
        requiredFields.add(firstNameField);
        requiredFields.add(lastNameField);
        requiredFields.add(emailInputField);
        requiredFields.add(passwordField);
        requiredFields.add(streetAddressField);
        requiredFields.add(cityField);
        requiredFields.add(provinceField);
        requiredFields.add(postalCodeField);
        selectedProvince.setValue(-1);

        emailCheckLiveData = Transformations.switchMap(emailInputField.getHasFocusObservable(), (event) -> {
            Boolean hasFocus = event.getContentIfNotHandled();
            //If it's focused, or has already been checked, or email is invalid, return empty livedata
            if (hasFocus == null || hasFocus
                    || emailInputField.getVerificationState() != EmailInputField.VerificationState.UNCHECKED
                    || !emailInputField.isValid()) {
                MutableLiveData<Resource<EmailCheckApi.EmailState>> temp = new MutableLiveData<>();
                temp.setValue(Resource.success(EmailCheckApi.EmailState.UNCHECKED));
                return temp;
            } else {
                return Transformations.map(emailCheckApi.checkEmail(emailInputField.getText()), (r) -> {
                    //to avoid further checks, save the state to the email field
                    if (r.status != Resource.Status.LOADING) {
                        emailInputField.setVerificationState(EmailInputField.VerificationState.CHECKED);
                    }
                    return r;
                });
            }
        });
    }

    /**
     * retrun index of first invalid item to focus on, -1 if all items valid
     */
    public int canJoin() {
        boolean firstItemFocused = false;
        int firstItemWithError = -1;
        for (int i = 0; i < requiredFields.size(); i++) {
            InputField field = requiredFields.get(i);
            if (!field.isValid()) {
                field.setShowError(true);
                if (!firstItemFocused) {
                    firstItemWithError = i;
                    firstItemFocused = true;
                }
            }
        }
        return firstItemWithError;
    }

    public InputField getFirstNameField() {

        return firstNameField;
    }

    public InputField getLastNameField() {

        return lastNameField;
    }

    public EmailInputField getEmailInputField() {

        return emailInputField;
    }

    public PasswordInputField getPasswordField() {

        return passwordField;
    }

    public InputField getSecurityQuestionField() {

        return securityQuestionField;
    }

    public InputField getSecurityAnswerField() {
        return securityAnswerField;
    }

    public InputField getStreetAddressField() {
        return streetAddressField;
    }

    public InputField getCityField() {
        return cityField;
    }

    public InputField getProvinceField() {
        return provinceField;
    }

    public InputField getPostalCodeField() {
        return postalCodeField;
    }

    public InputField getPhoneField() {
        return phoneField;
    }

    public boolean oneItemFilled() {
        for (InputField input : requiredFields) {
            if (!input.isEmpty()) {
                return true;
            }
        }
        return !phoneField.isEmpty()
                || !securityQuestionField.isEmpty()
                || !securityAnswerField.isEmpty();
    }

    ArrayList<InputField> getRequiredFields() {
        return requiredFields;
    }
}
