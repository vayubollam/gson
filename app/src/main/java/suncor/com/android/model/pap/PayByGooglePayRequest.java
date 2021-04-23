package suncor.com.android.model.pap;


import java.util.Locale;

public class PayByGooglePayRequest {

    private String encryptedStoreId;
    private int pumpNumber;
    private double preAuthLimit;
    private FundingPayload fundingPayload;
    private String preferredCulture;
    private String petroPointsNumber;
    private int preAuthRedeemPoints;
    private boolean rbcLinked;


    public PayByGooglePayRequest(String encryptedStoreId, int pumpNumber, double preAuthLimit,int preAuthRedeemPoints,
                                 FundingPayload fundingPayload, String petroPointsNumber, boolean rbcLinked) {
        this.encryptedStoreId = encryptedStoreId;
        this.pumpNumber = pumpNumber;
        this.preAuthLimit = preAuthLimit;
        this.preAuthRedeemPoints = preAuthRedeemPoints;
        this.fundingPayload = fundingPayload;
        this.petroPointsNumber = petroPointsNumber;
        this.rbcLinked = rbcLinked;
        this.preferredCulture = Locale.getDefault().getLanguage().equalsIgnoreCase("fr") ? "fr-CA" : "en-CA";
    }

    public void setEncryptedStoreId(String encryptedStoreId) {
        this.encryptedStoreId = encryptedStoreId;
    }

    public void setPumpNumber(int pumpNumber) {
        this.pumpNumber = pumpNumber;
    }

    public void setPreAuthLimit(double preAuthLimit) {
        this.preAuthLimit = preAuthLimit;
    }

    public void setFundingPayload(FundingPayload fundingPayload) {
        this.fundingPayload = fundingPayload;
    }

    public static class FundingPayload{
        private String token;

        public FundingPayload(String token) {
            this.token = token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        @Override
        public String toString() {
            return "FundingPayload{" +
                    "token='" + token + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "PayByGooglePayRequest{" +
                "encryptedStoreId='" + encryptedStoreId + '\'' +
                ", pumpNumber=" + pumpNumber +
                ", preAuthLimit=" + preAuthLimit +
                ", preAuthRedeemPoints=" + preAuthRedeemPoints +
                ", fundingPayload=" + fundingPayload.toString() +
                ", preferredCulture='" + preferredCulture + '\'' +
                ", petroPointsNumber='" + petroPointsNumber + '\'' +
                ", rbcLinked=" + rbcLinked +
                '}';
    }
}
