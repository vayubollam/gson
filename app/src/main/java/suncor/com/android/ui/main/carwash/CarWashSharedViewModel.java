package suncor.com.android.ui.main.carwash;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.security.NoSuchAlgorithmException;

import javax.inject.Inject;

import suncor.com.android.data.carwash.CarwashRepository;
import suncor.com.android.mfp.SessionManager;
import suncor.com.android.model.Resource;
import suncor.com.android.model.carwash.ActivateCarwashRequest;
import suncor.com.android.model.carwash.ActivateCarwashResponse;
import suncor.com.android.model.pap.PayByWalletRequest;
import suncor.com.android.model.pap.PayResponse;
import suncor.com.android.ui.common.cards.CardFormatUtils;

public class CarWashSharedViewModel extends ViewModel {
    public String cardNumber;
    public String securityKey;
    public String cardType;
    private MutableLiveData<String> encryptedCarWashCode = new MutableLiveData<>();
    private MutableLiveData<Boolean> reEnter = new MutableLiveData<>();
    private MutableLiveData<Integer> clickedCardIndex = new MutableLiveData<>();
    public MutableLiveData<String> securityPin = new MutableLiveData<>();
    private MutableLiveData<Boolean> isBackFromBarCode = new MutableLiveData<>();
    private CarwashRepository carwashRepository;

    @Inject
    public CarWashSharedViewModel(CarwashRepository carwashRepository) {
        this.carwashRepository = carwashRepository;
    }

    public void getMobileCode() {
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

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public MutableLiveData<String> getEncryptedCarWashCode() {
        return this.encryptedCarWashCode;
    }

    public MutableLiveData<Boolean> getIsBackFromBarCode() {
        return isBackFromBarCode;
    }

    public void setIsBackFromBarCode(Boolean isBackFromBarCode) {
        this.isBackFromBarCode.setValue(isBackFromBarCode);
    }

    /**
     * Activate Carwash
     */
    LiveData<Resource<ActivateCarwashResponse>> activateCarwash(String storeId) {
        ActivateCarwashRequest request = new ActivateCarwashRequest(storeId, securityPin.getValue(), cardNumber);
        return carwashRepository.activateCarwash(request);
    }
}
