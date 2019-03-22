package suncor.com.android.ui.common.input;

import android.text.Editable;
import android.text.TextWatcher;

public class PostalCodeFormattingTextWatcher implements TextWatcher {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.length() == 4 && s.charAt(s.length() - 1) != ' ') {
            s.insert(3, " ");
        }
    }
}
