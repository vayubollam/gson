package suncor.com.android.model.payments;

import com.google.gson.annotations.SerializedName;

public class PaymentResponse {
    @SerializedName("wallets")
    private WalletResponse[] wallet;

    public PaymentResponse(WalletResponse[] wallet) {
        this.wallet = wallet;
    }

    public WalletResponse[] getWallet() {
        return wallet;
    }

    public static class WalletResponse {
        @SerializedName("wallets")
        PaymentDetail[] payments;

        @SerializedName("fundingProviderName")
        String source;

        public PaymentDetail[] getPayments() {
            return payments;
        }

        public String getSource() {
            return source;
        }
    }
}
