package suncor.com.android.ui.common.input;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class CardNumberFormattingTextWatcher implements TextWatcher {
    private EditText editText;
    private int firstSpacePosition = 4;
    private int secondSpacePosition = 9;
    private int thirdSpacePosition = 13;

    public CardNumberFormattingTextWatcher(EditText editText) {
        this.editText = editText;
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
        // Remove spacing char
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == ' ' || s.charAt(i) == '-') {
                s.replace(i, i + 1, "");
            }
        }

        if (s.length() > thirdSpacePosition) {
            s.insert(thirdSpacePosition, " ");
        }
        if (s.length() > secondSpacePosition) {
            s.insert(secondSpacePosition, " ");
        }
        if (s.length() > firstSpacePosition) {
            s.insert(firstSpacePosition, " ");
        }

        editText.addTextChangedListener(this);
    }
}
