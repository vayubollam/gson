package suncor.com.android.ui.common.input;

import android.text.Editable;
import android.text.TextWatcher;

public class PostalCodeFormattingTextWatcher implements TextWatcher {

    private boolean isBeingUpdatedByTextWatcher = false;

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
        if (isBeingUpdatedByTextWatcher)
            return;
        isBeingUpdatedByTextWatcher = true;
        if (s.length() >= 4) {
            for (int i = 0; i < s.length(); i++) {
                if (s.charAt(i) == ' ') {
                    s.replace(i, i + 1, "");
                }
            }
            s.insert(3, " ");
        }
        s.replace(0, s.length(), s.toString().toUpperCase());
        isBeingUpdatedByTextWatcher = false;
    }
}
