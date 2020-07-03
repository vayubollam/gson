package suncor.com.android.ui.main.wallet.payments.list;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

import androidx.annotation.ColorInt;

import suncor.com.android.R;
import suncor.com.android.model.payments.PaymentDetail;

public class PaymentListItem {
    @ColorInt
    private int backgroundColor;
    @ColorInt
    private int textColor = Color.WHITE;
    private Drawable cardImage;
    private PaymentDetail.PaymentType paymentType;
    private PaymentDetail paymentDetail;
    private String cardNumber;

    PaymentListItem(Context context, PaymentDetail paymentDetail) {
        this.paymentDetail = paymentDetail;
        this.paymentType = paymentDetail.getPaymentType();
        this.cardImage = context.getDrawable(paymentDetail.getCardImage());
        this.cardNumber = context.getString(R.string.payment_card_number, paymentDetail.getCardNumber().replaceAll("[^\\d]", ""));

        backgroundColor = Color.WHITE;
        textColor = Color.parseColor("#CC000000");
    }

    public int getTextColor() {
        return textColor;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public PaymentDetail.PaymentType getPaymentType() {
        return paymentType;
    }

    public PaymentDetail getPaymentDetail() {
        return paymentDetail;
    }

    public Drawable getCardImage() {
        return cardImage;
    }

    public String getCardNumber() {
        return cardNumber;
    }

}
