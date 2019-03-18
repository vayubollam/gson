package suncor.com.android.ui.enrollement.form;

import java.util.regex.Pattern;

import androidx.databinding.Bindable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import suncor.com.android.ui.common.Event;

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
    private MutableLiveData<Event<Boolean>> hasFocus = new MutableLiveData<>();
    private VerificationState verificationState = VerificationState.UNCHECKED;

    public EmailInputField(int emptyError, int formatError) {
        super(emptyError);
        this.formatError = formatError;
    }

    public VerificationState getVerificationState() {
        return verificationState;
    }

    public void setVerificationState(VerificationState state) {
        verificationState = state;
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

    @Override
    public void setText(String text) {
        if (!text.equals(this.getText())) {
            verificationState = VerificationState.UNCHECKED;
        }
        super.setText(text);
    }

    public LiveData<Event<Boolean>> getHasFocusObservable() {
        return hasFocus;
    }

    public void setHasFocus(boolean hasFocus) {
        this.hasFocus.postValue(Event.newEvent(hasFocus));
        if (!hasFocus && !isEmpty() && !isValid()) {
            setShowError(true);
        }
    }

    @Override
    public boolean isValid() {
        return super.isValid() && formatValid();
    }

    private boolean formatValid() {
        return EMAIL_ADDRESS.matcher(getText()).matches();
    }

    public enum VerificationState {
        UNCHECKED, CHECKED
    }
}
