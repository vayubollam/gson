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

    public PaymentListItem(Context context, PaymentDetail paymentDetail) {
        this.paymentDetail = paymentDetail;
        this.paymentType = paymentDetail.getPaymentType();

        backgroundColor = Color.WHITE;
        textColor = Color.parseColor("#CC000000");

        switch (paymentType) {
            case VISA:
                cardImage = context.getDrawable(R.drawable.visa);
                break;
            case MASTERCARD:
                cardImage = context.getDrawable(R.drawable.mastercard);
                break;
            case AMEX:
                cardImage = context.getDrawable(R.drawable.amex);
                break;
        }
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

}
