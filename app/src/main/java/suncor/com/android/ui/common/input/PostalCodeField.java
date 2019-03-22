package suncor.com.android.ui.common.input;

import java.util.regex.Pattern;

import androidx.annotation.StringRes;
import androidx.databinding.Bindable;

public class PostalCodeField extends InputField {
    private static final Pattern POSTAL_CODE_PATTERN = Pattern.compile("^[A-Za-z]\\d[A-Za-z][ -]?\\d[A-Za-z]\\d$");


    @StringRes
    int formatError;
    private boolean hasFocus;

    public PostalCodeField(int error, int formatError) {
        super(error);
        this.formatError = formatError;
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

    public void setHasFocus(boolean hasFocus) {
        this.hasFocus = hasFocus;
        if (!hasFocus && !isEmpty() && !isFormatValid()) {
            setShowError(true);
        }
    }

    @Override
    public boolean isValid() {
        return !isEmpty() && isFormatValid();
    }

    private boolean isFormatValid() {
        return POSTAL_CODE_PATTERN.matcher(getText()).matches();
    }
}
