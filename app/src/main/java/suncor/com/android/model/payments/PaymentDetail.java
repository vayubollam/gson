package suncor.com.android.model.payments;


import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import suncor.com.android.R;

public class PaymentDetail {

    @Expose
    @SerializedName("cardIssuer")
    private PaymentType paymentType;

    @Expose
    @SerializedName("mainDisplayText")
    private String cardNumber;

    @DrawableRes
    private int cardImage;

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public int getCardImage() {

        if (cardImage == 0) {
            switch (paymentType) {
                case VISA:
                    cardImage = R.drawable.visa;
                    break;
                case MASTERCARD:
                    cardImage = R.drawable.mastercard;
                    break;
                case AMEX:
                    cardImage = R.drawable.amex;
                    break;
            }
        }

        return cardImage;
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
