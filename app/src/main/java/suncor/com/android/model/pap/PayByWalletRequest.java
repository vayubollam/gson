package suncor.com.android.model.pap;


public class PayByWalletRequest {

    private String encryptedStoreId;
    private int pumpNumber;
    private double preAuthLimit;
    private String userPaymentSourceId;
    private String paymentProviderName;


    public PayByWalletRequest(String encryptedStoreId, int pumpNumber, double preAuthLimit, String userPaymentSourceId) {
        this.encryptedStoreId = encryptedStoreId;
        this.pumpNumber = pumpNumber;
        this.preAuthLimit = preAuthLimit;
        this.userPaymentSourceId = userPaymentSourceId;
        this.paymentProviderName = "moneris";
    }

}
