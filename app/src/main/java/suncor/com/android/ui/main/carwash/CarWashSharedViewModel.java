package suncor.com.android.ui.main.carwash;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.security.NoSuchAlgorithmException;

import javax.inject.Inject;

import suncor.com.android.mfp.SessionManager;
import suncor.com.android.ui.common.cards.CardFormatUtils;

public class CarWashSharedViewModel extends ViewModel {
    private String cardNumber;
    private String securityKey;
    private MutableLiveData<String> encryptedCarWashCode = new MutableLiveData<>();
    private MutableLiveData<Boolean> reEnter = new MutableLiveData<>();
    private MutableLiveData<Boolean> isFromCarWash = new MutableLiveData<>();
    private MutableLiveData<Integer> clickedCardIndex = new MutableLiveData<>();
    private MutableLiveData<String> securityPin = new MutableLiveData<>();

    @Inject
    public CarWashSharedViewModel(SessionManager sessionManager) {
        securityKey = sessionManager.getCarWashKey();
        this.securityPin.observeForever(s -> {
            try {
                String code = CardFormatUtils.getMobileScancode(cardNumber, securityKey, Integer.parseInt(s));
                encryptedCarWashCode.setValue(code);
            } catch (NoSuchAlgorithmException ex) {

            }
        });

    }

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

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public MutableLiveData<String> getEncryptedCarWashCode() {
        return this.encryptedCarWashCode;
    }
}
