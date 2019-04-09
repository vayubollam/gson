package suncor.com.android.ui.common.input;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class StreetAddressInputField extends InputField {

    public StreetAddressInputField(int error) {
        super(error);
    }

    private MutableLiveData<String> textLiveData = new MutableLiveData<>();

    public LiveData<String> getTextLiveData() {
        return textLiveData;
    }

    @Override
    public void setText(String text) {
        super.setText(text);
        textLiveData.postValue(text);
    }
}
