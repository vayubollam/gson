package suncor.com.android.model.payments;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PaymentResponse {
    @SerializedName("wallets")
    WalletResponse[] wallet;

    public PaymentResponse(WalletResponse[] wallet) {
        this.wallet = wallet;
    }

    public WalletResponse[] getWallet() {
        return wallet;
    }

    public static class WalletResponse {
        @SerializedName("wallets")
        PaymentDetail[] payments;

        public WalletResponse(PaymentDetail[] payments) {
            this.payments = payments;
        }

        public PaymentDetail[] getPayments() {
            return payments;
        }
    }
}
