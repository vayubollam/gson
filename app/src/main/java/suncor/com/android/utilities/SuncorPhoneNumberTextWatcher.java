package suncor.com.android.utilities;

import android.text.Editable;
import android.text.TextWatcher;

public class SuncorPhoneNumberTextWatcher implements TextWatcher {
    private boolean isBeingUpdatedByTextWatcher = false;

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //Do nothing
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        //Do nothing
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (isBeingUpdatedByTextWatcher)
            return;
        isBeingUpdatedByTextWatcher = true;
        if (s.length() >= 4) {
            for (int i = 0; i < s.length(); i++) {
                if (s.charAt(i) == '-') {
                    s.replace(i, i + 1, "");
                }
            }
            if (s.length() >= 7) {
                s.insert(6, "-");
            }
            s.insert(3, "-");
        }
        s.replace(0, s.length(), s.toString().toUpperCase());
        isBeingUpdatedByTextWatcher = false;
    }
}