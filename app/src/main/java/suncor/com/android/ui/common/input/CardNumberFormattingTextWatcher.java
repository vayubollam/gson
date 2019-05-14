package suncor.com.android.ui.common.input;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import suncor.com.android.ui.common.cards.CardFormat;
import suncor.com.android.ui.common.cards.CardFormatUtils;

public class CardNumberFormattingTextWatcher implements TextWatcher {
    private EditText editText;
    private CardFormat cardFormat;

    public CardNumberFormattingTextWatcher(EditText editText) {
        this.editText = editText;
    }

    public CardNumberFormattingTextWatcher(EditText editText, CardFormat cardFormat) {
        this.editText = editText;
        this.cardFormat = cardFormat;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        editText.removeTextChangedListener(this);
        if (cardFormat != null) {
            CardFormatUtils.formatForViewing(s, cardFormat);
        }
        editText.addTextChangedListener(this);
    }
}
