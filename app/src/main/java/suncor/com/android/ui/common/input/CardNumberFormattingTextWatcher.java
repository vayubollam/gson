package suncor.com.android.ui.common.input;

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.widget.EditText;

import suncor.com.android.ui.common.cards.CardFormat;
import suncor.com.android.ui.common.cards.CardFormatUtils;

public class CardNumberFormattingTextWatcher implements TextWatcher {
    private EditText editText;
    private CardFormat forcedCardFormat;
    private CardFormat detectedCardFormat;

    public CardNumberFormattingTextWatcher(EditText editText) {
        this.editText = editText;
    }

    public CardNumberFormattingTextWatcher(EditText editText, CardFormat cardFormat) {
        this.editText = editText;
        this.forcedCardFormat = cardFormat;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //do nothing
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        //do nothing
    }

    @Override
    public void afterTextChanged(Editable s) {
        editText.removeTextChangedListener(this);
        if (forcedCardFormat != null) {
            CardFormatUtils.formatForViewing(s, forcedCardFormat);
        } else if (s.length() >= 4) {
            {
                CardFormat cardFormat = CardFormatUtils.findCardFormat(s);
                if (cardFormat != null) {
                    CardFormatUtils.formatForViewing(s, cardFormat);
                    if (detectedCardFormat != cardFormat) {
                        detectedCardFormat = cardFormat;
                        //apply maximum length
                        updateEditTextMaxLength(cardFormat);
                    }
                }
            }
        }
        editText.addTextChangedListener(this);
    }

    private void updateEditTextMaxLength(CardFormat cardFormat) {
        //we can here restrict the length to the exact value if the card number is only 14, but to be consistent with iOS will limit to 18 + spaces
        InputFilter.LengthFilter lengthFilter = new InputFilter.LengthFilter(18 + cardFormat.getFormat().length - 1);
        InputFilter[] currentFilters = editText.getFilters();
        if (currentFilters != null) {
            int previousLengthFilterIndex = -1;
            for (int i = 0; i < currentFilters.length; i++) {
                if (currentFilters[i] instanceof InputFilter.LengthFilter) {
                    previousLengthFilterIndex = i;
                    break;
                }
            }

            InputFilter newFilters[] = new InputFilter[currentFilters.length + (previousLengthFilterIndex != -1 ? 0 : 1)];
            System.arraycopy(currentFilters, 0, newFilters, 0, currentFilters.length);
            newFilters[previousLengthFilterIndex != -1 ? previousLengthFilterIndex : currentFilters.length] = lengthFilter;
            editText.setFilters(newFilters);
        } else {
            editText.setFilters(new InputFilter[]{lengthFilter});
        }
    }
}
