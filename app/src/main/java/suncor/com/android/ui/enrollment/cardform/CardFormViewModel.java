package suncor.com.android.ui.enrollment.cardform;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import java.text.Normalizer;

import javax.inject.Inject;

import suncor.com.android.R;
import suncor.com.android.data.account.EnrollmentsApi;
import suncor.com.android.model.Resource;
import suncor.com.android.model.account.Address;
import suncor.com.android.model.account.CardStatus;
import suncor.com.android.model.account.NewEnrollment;
import suncor.com.android.model.account.UserInfo;
import suncor.com.android.ui.common.Event;
import suncor.com.android.ui.common.input.CardNumberInputField;
import suncor.com.android.ui.common.input.InputField;
import suncor.com.android.ui.common.input.PostalCodeField;


public class CardFormViewModel extends ViewModel {

    private EnrollmentsApi enrollmentsApi;
    private MutableLiveData<Event<Boolean>> verify = new MutableLiveData<>();
    private CardNumberInputField cardNumberField = new CardNumberInputField(R.string.enrollment_cardform_card_error, R.string.enrollment_cardform_card_format_error);
    private PostalCodeField postalCodeField = new PostalCodeField(R.string.enrollment_cardform_postalcode_error, R.string.enrollment_cardform_postalcode_format_error);
    private InputField lastNameField = new InputField(R.string.enrollment_cardform_lastname_error);
    public LiveData<Resource<CardStatus>> verifyCard;

    @Inject
    public CardFormViewModel(EnrollmentsApi enrollmentsApi) {
        this.enrollmentsApi = enrollmentsApi;

        LiveData<Resource<CardStatus>> verifyCardApi = Transformations.switchMap(verify, (event) -> {
            if (event.getContentIfNotHandled() != null) {
                return enrollmentsApi.checkCardStatus(
                        cardNumberField.getText().replace(" ", ""),
                        postalCodeField.getText().replace(" ", ""),
                        // converting  Accent character to normal ASCII character
                        Normalizer.normalize(lastNameField.getText(), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", ""));

            } else {
                return new MutableLiveData<>();
            }
        });
        verifyCard = Transformations.map(verifyCardApi, (result) -> {
            if (result.status == Resource.Status.SUCCESS) {
                CardStatus cardStatus = result.data;
                if (cardStatus.getCardType() == NewEnrollment.EnrollmentType.GHOST) {
                    UserInfo userInfo = new UserInfo();
                    userInfo.setLastName(Normalizer.normalize(lastNameField.getText(), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", ""));
                    cardStatus.setUserInfo(userInfo);
                    Address address = new Address();
                    address.setPostalCode(postalCodeField.getText().replace(" ", ""));
                    cardStatus.setAddress(address);
                    cardStatus.setCardNumber(cardNumberField.getText().replace(" ", ""));
                } else {
                    cardStatus.setCardNumber(cardNumberField.getText().replace(" ", ""));
                }
                return Resource.success(cardStatus);
            }
            return result;
        });
    }

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
        boolean allGood = true;
        if (!cardNumberField.isValid()) {
            cardNumberField.setShowError(true);
            allGood = false;
        }

        if (!postalCodeField.isValid()) {
            postalCodeField.setShowError(true);
            allGood = false;
        }

        if (!lastNameField.isValid()) {
            lastNameField.setShowError(true);
            allGood = false;
        }

        if (allGood) {
            verify.postValue(Event.newEvent(true));
        }
    }
}
