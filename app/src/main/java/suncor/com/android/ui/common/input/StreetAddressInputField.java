package suncor.com.android.ui.common.input;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class StreetAddressInputField extends InputField {

    private boolean hasFocus;
    private MutableLiveData<String> textLiveData = new MutableLiveData<>();

    public StreetAddressInputField(int error) {
        super(error);
    }

    public boolean hasFocus() {
        return hasFocus;
    }

    public void setHasFocus(boolean hasFocus) {
        this.hasFocus = hasFocus;
    }

    public LiveData<String> getTextLiveData() {
        return textLiveData;
    }

    @Override
    public void setText(String text) {
        if (!text.equals(this.getText())) {
            textLiveData.postValue(text);
        }
        super.setText(text);
    }

    public void setTextSilent(String text) {
        super.setText(text);
    }
}
