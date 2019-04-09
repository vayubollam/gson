package suncor.com.android.ui.common.input;

import java.util.Arrays;
import java.util.regex.Pattern;

import androidx.annotation.StringRes;
import androidx.databinding.Bindable;

public class PostalCodeField extends InputField {
    private static final Pattern POSTAL_CODE_PATTERN = Pattern.compile("^[A-Za-z]\\d[A-Za-z][ -]?\\d[A-Za-z]\\d$");


    @StringRes
    int formatError;
    int matchingProvinceError = -1;
    private boolean hasFocus;
    private String firstCharacterValidation;

    public PostalCodeField(int error, int formatError, int matchingProvinceError) {
        super(error);
        this.formatError = formatError;
        this.matchingProvinceError = matchingProvinceError;
    }

    public PostalCodeField(int error, int formatError) {
        super(error);
        this.formatError = formatError;
    }

    public void setFirstCharacterValidation(String firstCharacterValidation) {
        this.firstCharacterValidation = firstCharacterValidation;
    }

    @Bindable
    @Override
    public int getError() {
        if (!getShowError() || isValid()) {
            return -1;
        } else {
            return isEmpty() ? super.getError() :
                    !isFormatValid() ? formatError :
                            !isMatchingProvince() ? matchingProvinceError : -1;

    }
    }

    public void setHasFocus(boolean hasFocus) {
        this.hasFocus = hasFocus;
        if (!hasFocus && !isEmpty() && !isValid()) {
            setShowError(true);
        }
    }

    @Override
    public boolean isValid() {
        return !isEmpty() && isFormatValid() && isMatchingProvince();
    }

    private boolean isFormatValid() {
        return POSTAL_CODE_PATTERN.matcher(getText()).matches();
    }

    private boolean isMatchingProvince() {
        if(matchingProvinceError == -1 || firstCharacterValidation == null)
        {
            return true;
        }
        String first = getText().substring(0,1).toUpperCase();
        return firstCharacterValidation.contains(first);
    }

}
