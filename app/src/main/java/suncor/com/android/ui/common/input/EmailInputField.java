package suncor.com.android.ui.common.input;

import java.util.regex.Pattern;

import androidx.annotation.StringRes;
import androidx.databinding.Bindable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import suncor.com.android.ui.common.Event;

public class EmailInputField extends InputField {

    private static final Pattern EMAIL_ADDRESS = Pattern.compile("[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,64}");

    @StringRes
    private final int formatError;
    @StringRes
    private final int restrictedError;

    private boolean isRestricted = false;
    private MutableLiveData<Event<Boolean>> hasFocus = new MutableLiveData<>();
    private VerificationState verificationState = VerificationState.UNCHECKED;

    public EmailInputField(int emptyError, @StringRes int formatError, @StringRes int restrictedError) {
        super(emptyError);
        this.formatError = formatError;
        this.restrictedError = restrictedError;
    }

    public void setRestricted(boolean restricted) {
        isRestricted = restricted;
        if (isRestricted) {
            setShowError(true);
        }
    }

    public VerificationState getVerificationState() {
        return verificationState;
    }

    public void setVerificationState(VerificationState state) {
        verificationState = state;
    }

    @Override

    @Bindable
    @StringRes
    public int getError() {
        if (!getShowError()) {
            return -1;
        } else {
            return isRestricted ? restrictedError
                    : isEmpty() ? super.getError()
                    : !formatValid() ? formatError : -1;
        }
    }

    @Override
    public void setText(String text) {
        if (!text.equals(this.getText())) {
            verificationState = VerificationState.UNCHECKED;
            isRestricted = false;
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
        return super.isValid() && formatValid() && !isRestricted;
    }

    private boolean formatValid() {
        return EMAIL_ADDRESS.matcher(getText()).matches();
    }

    public enum VerificationState {
        UNCHECKED, CHECKED
    }
}
