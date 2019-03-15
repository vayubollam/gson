package suncor.com.android.ui.enrollement.form;

import java.util.regex.Pattern;

import androidx.databinding.Bindable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class EmailInputField extends InputField {

    private static final Pattern EMAIL_ADDRESS
            = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
    );
    private final int formatError;

    private MutableLiveData<Boolean> hasFocus = new MutableLiveData<>();

    public EmailInputField(int emptyError, int formatError) {
        super(emptyError);
        this.formatError = formatError;
    }

    @Override
    @Bindable
    public int getError() {
        if (!getShowError()) {
            return -1;
        } else {
            return !super.isValid() ? super.getError() :
                    !formatValid() ? formatError : -1;
        }
    }

    public LiveData<Boolean> getHasFocusObservable() {
        return hasFocus;
    }

    public void setHasFocus(boolean hasFocus) {
        this.hasFocus.postValue(hasFocus);
    }

    @Override
    public boolean isValid() {
        return super.isValid() && formatValid();
    }

    private boolean formatValid() {
        return EMAIL_ADDRESS.matcher(getText()).matches();
    }
}
