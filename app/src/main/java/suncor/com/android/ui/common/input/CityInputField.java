package suncor.com.android.ui.common.input;

import java.util.regex.Pattern;

import androidx.annotation.StringRes;

public class CityInputField extends InputField {
    private static final Pattern CITY_PATTERN = Pattern.compile("^[.0-9a-zA-Z\\s-]+$");

    public CityInputField(@StringRes int error) {
        super(error);
    }

    @Override
    public boolean isValid() {
        return super.isValid() && CITY_PATTERN.matcher(getText()).matches();
    }

}
