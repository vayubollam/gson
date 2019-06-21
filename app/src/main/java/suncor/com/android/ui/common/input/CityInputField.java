package suncor.com.android.ui.common.input;

import java.util.regex.Pattern;

import androidx.annotation.StringRes;
import androidx.databinding.Bindable;

public class CityInputField extends InputField {
    private static final Pattern CITY_PATTERN = Pattern.compile("^[a-zA-ZÀ-ÿ\\s,'-]+$");
    @StringRes
    private final int formatError;

    public CityInputField(@StringRes int error, @StringRes int formatError) {
        super(error);
        this.formatError = formatError;
    }

    public void setHasFocus(boolean hasFocus) {
        if (!hasFocus && !isEmpty() && !isValid()) {
            setShowError(true);
        }
    }

    @Bindable
    @StringRes
    public int getError() {
        if (!getShowError()) {
            return -1;
        } else {
            return isEmpty() ? super.getError()
                    : !isValid() ? formatError : -1;
        }
    }

    @Override
    public boolean isValid() {
        return super.isValid() && CITY_PATTERN.matcher(getText()).matches();
    }

}
