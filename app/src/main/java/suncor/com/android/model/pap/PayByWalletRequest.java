package suncor.com.android.model.pap;


public class PayByWalletRequest {

    private String encryptedStoreId;
    private int pumpNumber;
    private double preAuthLimit;
    private int userPaymentSourceId;
    private String paymentProviderName;


    public PayByWalletRequest(String encryptedStoreId, int pumpNumber, double preAuthLimit, int userPaymentSourceId) {
        this.encryptedStoreId = encryptedStoreId;
        this.pumpNumber = pumpNumber;
        this.preAuthLimit = preAuthLimit;
        this.userPaymentSourceId = userPaymentSourceId;
        this.paymentProviderName = "moneris";
    }

}
