package suncor.com.android.ui.enrollement.cardform;

import androidx.lifecycle.ViewModel;
import suncor.com.android.R;
import suncor.com.android.ui.common.input.CardNumberInputField;
import suncor.com.android.ui.common.input.InputField;
import suncor.com.android.ui.common.input.PostalCodeField;

public class CardFormViewModel extends ViewModel {

    private CardNumberInputField cardNumberField = new CardNumberInputField(R.string.enrollment_cardform_card_error, R.string.enrollment_cardform_card_format_error);
    private PostalCodeField postalCodeField = new PostalCodeField(R.string.enrollment_cardform_postalcode_error, R.string.enrollment_cardform_postalcode_format_error);
    private InputField lastNameField = new InputField(R.string.enrollment_cardform_lastname_error);

    public CardNumberInputField getCardNumberField() {
        return cardNumberField;
    }

    public PostalCodeField getPostalCodeField() {
        return postalCodeField;
    }

    public InputField getLastNameField() {
        return lastNameField;
    }

    public void validateAndContinue() {
        if (!cardNumberField.isValid()) {
            cardNumberField.setShowError(true);
        }

        if (!postalCodeField.isValid()) {
            postalCodeField.setShowError(true);
        }

        if (!lastNameField.isValid()) {
            lastNameField.setShowError(true);
        }
    }
}
