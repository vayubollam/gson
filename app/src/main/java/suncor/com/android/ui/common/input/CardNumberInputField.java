package suncor.com.android.ui.common.input;

import java.util.regex.Pattern;

import androidx.annotation.StringRes;
import androidx.databinding.Bindable;

public class CardNumberInputField extends InputField {

    private static final Pattern CARD_NUMBER_PATTERN = Pattern.compile("^7069[\\d]{12}$");

    private boolean hasFocus;
    @StringRes
    private int formatError;

    public CardNumberInputField(@StringRes int error, @StringRes int formatError) {
        super(error);
        this.formatError = formatError;
    }

    public void setHasFocus(boolean hasFocus) {
        this.hasFocus = hasFocus;
        if (!hasFocus && !isEmpty() && !isFormatValid()) {
            setShowError(true);
        }
    }

    @Bindable
    @Override
    public int getError() {
        if (!getShowError() || isValid()) {
            return -1;
        } else {
            return isEmpty() ? super.getError() : formatError;
        }
    }

    @Override
    public boolean isValid() {
        return !isEmpty() && isFormatValid();
    }

    private boolean isFormatValid() {
        return CARD_NUMBER_PATTERN.matcher(getText().replace(" ", "")).matches();
    }
}
