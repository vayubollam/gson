package suncor.com.android.ui.common.input;

import java.util.regex.Pattern;

import androidx.databinding.Bindable;

import suncor.com.android.BR;


public class PasswordInputField extends InputField {

    private static final Pattern LOWER_CASE_PATTERN = Pattern.compile(".*[a-z]+.*");
    private static final Pattern UPPER_CASE_PATTERN = Pattern.compile(".*[A-Z]+.*");
    private static final Pattern NUMBER_PATTERN = Pattern.compile(".*[0-9]+.*");
    private static final Pattern SPECIAL_CHARS_PATTERN = Pattern.compile(".*[@$!%*#?&~`^_+={},.:;<>|\"\'\\-\\(\\)\\[\\]\\/\\\\]+.*");
    private boolean hasRightLength;
    private boolean hasLowerCase;
    private boolean hasUpperCase;
    private boolean hasNumber;
    private boolean hasSpecialChar;
    private boolean showValidationHint;
    private boolean hasFocus;

    public PasswordInputField(int error) {
        super(error);
    }

    @Override
    @Bindable
    public boolean getShowError() {
        return super.getShowError();
    }

    @Override
    public void setShowError(boolean showError) {
        super.setShowError(showError);
        notifyPropertyChanged(BR.showError);
        if (showError && isEmpty()) {
            //When the user clicks on join, if it's empty show only required error
            setShowValidationHint(false);
        }
    }

    @Override
    @Bindable
    public int getError() {
        if (!super.isValid()) {
            return super.getError();
        } else {
            return -1;
        }
    }

    @Bindable
    public boolean isHasRightLength() {
        return hasRightLength;
    }

    public void setHasRightLength(boolean hasRightLength) {
        if (this.hasRightLength != hasRightLength) {
            this.hasRightLength = hasRightLength;
            notifyPropertyChanged(BR.hasRightLength);
        }
    }

    @Bindable
    public boolean isShowValidationHint() {
        return showValidationHint;
    }

    public void setShowValidationHint(boolean showValidationHint) {
        this.showValidationHint = showValidationHint;
        notifyPropertyChanged(BR.showValidationHint);
    }

    @Bindable
    public boolean isHasLowerCase() {
        return hasLowerCase;
    }

    public void setHasLowerCase(boolean hasLowerCase) {
        if (this.hasLowerCase != hasLowerCase) {
            this.hasLowerCase = hasLowerCase;
            notifyPropertyChanged(BR.hasLowerCase);
        }
    }

    @Bindable
    public boolean isHasUpperCase() {
        return hasUpperCase;
    }

    public void setHasUpperCase(boolean hasUpperCase) {
        if (this.hasUpperCase != hasUpperCase) {
            this.hasUpperCase = hasUpperCase;
            notifyPropertyChanged(BR.hasUpperCase);
        }
    }

    @Bindable
    public boolean isHasNumber() {
        return hasNumber;
    }

    public void setHasNumber(boolean hasNumber) {
        if (this.hasNumber != hasNumber) {
            this.hasNumber = hasNumber;
            notifyPropertyChanged(BR.hasNumber);
        }
    }

    @Bindable
    public boolean isHasSpecialChar() {
        return hasSpecialChar;
    }

    public void setHasSpecialChar(boolean hasSpecialChar) {
        if (this.hasSpecialChar != hasSpecialChar) {
            this.hasSpecialChar = hasSpecialChar;
            notifyPropertyChanged(BR.hasSpecialChar);
        }
    }


    @Bindable
    public boolean isHasFocus() {
        return hasFocus;
    }

    public void setHasFocus(boolean hasFocus) {
        this.hasFocus = hasFocus;
        if (hasFocus && !isValid()) {
            setShowValidationHint(true);
            notifyPropertyChanged(BR.error);
        } else {
            if (!isEmpty() && !isValid()) {
                setShowError(true);
            }
        }
    }

    @Override
    public boolean isValid() {
        return super.isValid()
                && hasRightLength && hasLowerCase && hasUpperCase && hasNumber && hasSpecialChar;
    }

    @Override
    public void setText(String text) {
        super.setText(text);
        validate(text);
        if (hasFocus) {
            setShowValidationHint(!isValid());
        }
    }

    private void validate(String text) {
        setHasRightLength(text.length() >= 8 && text.length() <= 16);
        setHasLowerCase(LOWER_CASE_PATTERN.matcher(text).matches());
        setHasUpperCase(UPPER_CASE_PATTERN.matcher(text).matches());
        setHasNumber(NUMBER_PATTERN.matcher(text).matches());
        setHasSpecialChar(SPECIAL_CHARS_PATTERN.matcher(text).matches());
    }
}
