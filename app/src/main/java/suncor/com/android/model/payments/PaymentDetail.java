package suncor.com.android.model.payments;


import android.os.Parcelable;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import suncor.com.android.R;

public class PaymentDetail implements Serializable {

    @Expose
    @SerializedName("userPaymentSourceId")
    private String id;

    @Expose
    @SerializedName("cardIssuerId")
    private PaymentType paymentType;

    @Expose
    @SerializedName("lastFour")
    private String cardNumber;

    @Expose
    @SerializedName("expDate")
    private String expDate;

    @DrawableRes
    private int cardImage;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getExpDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CANADA);
        SimpleDateFormat toFormat = new SimpleDateFormat("MM/yyyy", Locale.CANADA);

        try {
            return toFormat.format(format.parse(expDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "";
    }

    public void setExpDate(String expDate) {
        this.expDate = expDate;
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
            return cardDetail.getId().equals(this.getId());
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
