package suncor.com.android.ui.main.cards.add;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import suncor.com.android.R;
import suncor.com.android.data.cards.CardsRepository;
import suncor.com.android.model.Resource;
import suncor.com.android.model.cards.AddCardRequest;
import suncor.com.android.model.cards.CardDetail;
import suncor.com.android.ui.common.Event;
import suncor.com.android.ui.common.cards.CardFormat;
import suncor.com.android.ui.common.cards.CardFormatUtils;

public class AddCardViewModel extends ViewModel {

    public LiveData<Resource<CardDetail>> addCardApiResult;
    private CardNumberInputField cardNumberInputField = new CardNumberInputField(R.string.cards_add_fragment_card_field_empty_error, R.string.cards_add_fragment_card_field_format_error);
    private CVVInputField cvvInputField = new CVVInputField(R.string.cards_add_fragment_cvv_field_empty_error, R.string.cards_add_fragment_cvv_field_format_error);
    private MutableLiveData<Boolean> _showCvvField = new MutableLiveData<>();
    public LiveData<Boolean> showCvvField = _showCvvField;
    private MutableLiveData<CardDetail> _card = new MutableLiveData<>();
    public LiveData<CardDetail> card = _card;
    private MutableLiveData<Event<Boolean>> addCardEvent = new MutableLiveData<>();

    @Inject
    public AddCardViewModel(CardsRepository repository) {
        _showCvvField.setValue(false);
        cardNumberInputField.setChangedListener(() -> _showCvvField.setValue(false));

        addCardApiResult = Transformations.switchMap(addCardEvent, event -> {
            if (event.getContentIfNotHandled() != null) {
                CardFormat format = CardFormatUtils.findCardFormat(cardNumberInputField.getText());
                AddCardRequest.Category category;

                if (format == CardFormatUtils.FSR_FORMAT || format == CardFormatUtils.FSR_SHORT_FORMAT) {
                    category = AddCardRequest.Category.FSR;
                } else if (format == CardFormatUtils.PPC_FORMAT || format == CardFormatUtils.PPC_SHORT_FORMAT) {
                    category = AddCardRequest.Category.PPC;
                } else if (format == CardFormatUtils.WAG_SP_FORMAT || format == CardFormatUtils.WAG_SP_SHORT_FORMAT) {
                    category = AddCardRequest.Category.CARWASH;
                } else {
                    throw new IllegalStateException();
                }

                String cardNumber = cardNumberInputField.getText().replace(" ", "");
                if (cardNumber.length() == 14) {
                    cardNumber = CardFormatUtils.CARDS_PREFIX.concat(cardNumber);
                }

                return repository.addCard(new AddCardRequest(category, cardNumber, cvvInputField.getText()));
            } else {
                return new MutableLiveData<>();
            }
        });

        addCardApiResult.observeForever(result -> {
            if (result.status == Resource.Status.SUCCESS) {
                _card.postValue(result.data);
            }
        });
    }

    public CVVInputField getCvvInputField() {
        return cvvInputField;
    }

    public CardNumberInputField getCardNumberInputField() {
        return cardNumberInputField;
    }

    public void continueButtonClicked() {
        if (!_showCvvField.getValue()) {
            if (!cardNumberInputField.isValid()) {
                cardNumberInputField.setShowError(true);
            } else {
                _showCvvField.setValue(true);
            }
        } else {
            if (!cvvInputField.isValid()) {
                cvvInputField.setShowError(true);
            } else {
                addCardEvent.postValue(Event.newEvent(true));
            }
        }
    }
}
