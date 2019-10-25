package suncor.com.android.ui.main.carwash;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CarWashSharedViewModel extends ViewModel {
    private MutableLiveData<Boolean> reEnter = new MutableLiveData<>();
    private MutableLiveData<Boolean> isFromCarWash = new MutableLiveData<>();
    private MutableLiveData<Integer> clickedCardIndex = new MutableLiveData<>();
    private MutableLiveData<String> securityPin = new MutableLiveData<>();

    public MutableLiveData<Boolean> getReEnter() {
        return reEnter;
    }

    public void setReEnter(Boolean reEnter) {
        this.reEnter.setValue(reEnter);
    }

    public MutableLiveData<Boolean> getIsFromCarWash() {
        return isFromCarWash;
    }

    public void setIsFromCarWash(Boolean isFromCarWash) {
        this.isFromCarWash.setValue(isFromCarWash);
    }

    public MutableLiveData<Integer> getClickedCardIndex() {
        return clickedCardIndex;
    }

    public void setClickedCardIndex(Integer clickedCardIndex) {
        this.clickedCardIndex.setValue(clickedCardIndex);
    }

    public MutableLiveData<String> getSecurityPin() {
        return securityPin;
    }

    public void setSecurityPin(String securityPin) {
        this.securityPin.setValue(securityPin);
    }
}
