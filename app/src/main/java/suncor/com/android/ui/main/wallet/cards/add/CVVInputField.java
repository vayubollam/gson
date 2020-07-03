package suncor.com.android.ui.main.wallet.cards.add;

import java.util.regex.Pattern;

import androidx.annotation.StringRes;
import androidx.databinding.Bindable;
import suncor.com.android.BR;
import suncor.com.android.ui.common.input.InputField;

public class CVVInputField extends InputField {

    private static final Pattern CVV_PATTERN = Pattern.compile("^[\\d]{3}$");

    @StringRes
    private final int formatError;

    public CVVInputField(@StringRes int emptyError, @StringRes int formatError) {
        super(emptyError);
        this.formatError = formatError;
    }

    @Bindable
    @Override
    public boolean getShowError() {
        return super.getShowError();
    }

    @Override
    public void setShowError(boolean showError) {
        super.setShowError(showError);
        notifyPropertyChanged(BR.showError);
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
        return CVV_PATTERN.matcher(getText()).matches();
    }
}
