package suncor.com.android.ui.common.input;

import java.util.regex.Pattern;

import suncor.com.android.BR;


public class PhoneInputField extends InputField {
    public PhoneInputField(int error) {
        super(error);
    }

    private static final Pattern PHONE_NUMBER = Pattern.compile("\\d{3}-\\d{3}-\\d{4}");

    public void setHasFocus(boolean hasFocus) {
        if (!hasFocus && !isValid()) {
            setShowError(true);
        }
    }

    @Override
    public boolean isValid() {
        return isEmpty() || PHONE_NUMBER.matcher(getText()).matches();
    }

    @Override
    public void setText(String text) {
        if (!isSameText(text)) {
            notifyPropertyChanged(BR.text);
        }
        super.setText(text);
    }

    private boolean isSameText(String text) {
        if (text == null) {
            return getText() == null;
        } else {
            if (getText() == null) {
                return false;
            }
            return text.replace("-", "").equals(getText().replace("-", ""));
        }
    }
}
