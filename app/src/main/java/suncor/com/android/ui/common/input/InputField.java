package suncor.com.android.ui.common.input;

import androidx.annotation.StringRes;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import suncor.com.android.BR;


public class InputField extends BaseObservable {
    private String text;

    @StringRes
    private int error;
    private boolean isRequired;
    private boolean showError = false;

    public InputField(int error) {
        this.error = error;
        isRequired = true;
    }

    public InputField() {
        this.isRequired = false;
    }

    public boolean getShowError() {
        return showError;
    }

    public void setShowError(boolean showError) {
        this.showError = showError;
        notifyPropertyChanged(BR.error);
    }

    @Bindable
    public int getError() {
        return showError && !isValid() ? error : -1;
    }

    public boolean isEmpty() {
        return text == null || text.isEmpty();
    }

    @Bindable
    public String getText() {
        return text;
    }

    public void setText(String text) {
        if (text == null) {
            text = "";
        }
        if (!text.equals(this.text)) {
            this.text = text;
            setShowError(false);
        }
    }

    public boolean isRequired() {
        return isRequired;
    }

    public boolean isValid() {
        return !isEmpty();
    }
}
