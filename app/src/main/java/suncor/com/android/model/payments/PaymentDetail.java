package suncor.com.android.model.payments;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import suncor.com.android.model.cards.CardType;

public class PaymentDetail {

    @SerializedName("cardIssuer")
    private PaymentType paymentType;

    @SerializedName("mainDisplayText")
    private String cardNumber;

    public PaymentDetail(PaymentType paymentType, String cardNumber) {
        this.paymentType = paymentType;
        this.cardNumber = cardNumber;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof PaymentDetail)) {
            return false;
        } else {
            PaymentDetail cardDetail = (PaymentDetail) obj;
            return cardDetail.getCardNumber().equals(this.getCardNumber());
        }
    }

    public String getFirebaseScreenName() {
        return "my-wallet-view-"+getPaymentType().name();
    }

    public enum PaymentType {
        @SerializedName("Visa")
        VISA,
        @SerializedName("Mastercard")
        MASTERCARD,
        @SerializedName("Amex")
        AMEX
    }
}
