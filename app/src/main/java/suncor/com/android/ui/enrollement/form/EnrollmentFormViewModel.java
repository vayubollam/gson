package suncor.com.android.ui.enrollement.form;

import java.util.ArrayList;

import androidx.lifecycle.ViewModel;
import suncor.com.android.R;

public class EnrollmentFormViewModel extends ViewModel {
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

    private ArrayList<InputField> requiredFields = new ArrayList<>();

    public EnrollmentFormViewModel() {
        requiredFields.add(firstNameField);
        requiredFields.add(lastNameField);
        requiredFields.add(emailInputField);
        requiredFields.add(passwordField);
        requiredFields.add(streetAddressField);
        requiredFields.add(cityField);
        requiredFields.add(provinceField);
        requiredFields.add(postalCodeField);
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
