package suncor.com.android.model.pap;


import java.util.Locale;

public class PayByWalletRequest {

    private String encryptedStoreId;
    private int pumpNumber;
    private double preAuthLimit;
    private int userPaymentSourceId;
    private String paymentProviderName;
    private String preferredCulture;
    private String petroPointsNumber;
    private boolean rbcLinked;


    public PayByWalletRequest(String encryptedStoreId, int pumpNumber, double preAuthLimit,
                              int userPaymentSourceId, String petroPointsNumber, boolean rbcLinked) {
        this.encryptedStoreId = encryptedStoreId;
        this.pumpNumber = pumpNumber;
        this.preAuthLimit = preAuthLimit;
        this.userPaymentSourceId = userPaymentSourceId;
        this.petroPointsNumber = petroPointsNumber;
        this.rbcLinked = rbcLinked;
        this.paymentProviderName = "moneris";
        this.preferredCulture = Locale.getDefault().getLanguage().equalsIgnoreCase("fr") ? "fr-CA" : "en-CA";
    }

    @Override
    public String toString() {
        return "PayByWalletRequest{" +
                "encryptedStoreId='" + encryptedStoreId + '\'' +
                ", pumpNumber=" + pumpNumber +
                ", preAuthLimit=" + preAuthLimit +
                ", userPaymentSourceId=" + userPaymentSourceId +
                ", paymentProviderName='" + paymentProviderName + '\'' +
                ", preferredCulture='" + preferredCulture + '\'' +
                ", petroPointsNumber='" + petroPointsNumber + '\'' +
                ", rbcLinked=" + rbcLinked +
                '}';
    }
}
