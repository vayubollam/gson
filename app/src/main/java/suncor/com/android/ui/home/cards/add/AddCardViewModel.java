package suncor.com.android.ui.home.cards.add;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import suncor.com.android.R;

public class AddCardViewModel extends ViewModel {

    private CardNumberInputField cardNumberInputField = new CardNumberInputField(R.string.cards_add_fragment_card_field_empty_error, R.string.cards_add_fragment_card_field_format_error);
    private MutableLiveData<Boolean> _showCvvField = new MutableLiveData<>();
    public LiveData<Boolean> showCvvField = _showCvvField;

    @Inject
    public AddCardViewModel() {
        _showCvvField.setValue(false);
        cardNumberInputField.setChangedListener(() -> _showCvvField.setValue(false));
    }

    public CardNumberInputField getCardNumberInputField() {
        return cardNumberInputField;
    }

    public void continueButtonClicked() {
        if (!cardNumberInputField.isValid()) {
            cardNumberInputField.setShowError(true);
        } else {
            _showCvvField.setValue(true);
        }
    }
}
