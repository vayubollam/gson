package suncor.com.android.ui.main.wallet.payments.details;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.annotation.Nullable;

import suncor.com.android.R;
import suncor.com.android.model.payments.PaymentDetail;

public class ExpandedPaymentItem {
    private String cardName;
    private Drawable cardImage;
    private String cardNumber;
    private PaymentDetail paymentDetail;
    private PaymentDetail.PaymentType paymentType;
    private boolean isRemovable = true;

    public ExpandedPaymentItem(Context context, PaymentDetail paymentDetail) {
        this.paymentDetail = paymentDetail;
        this.cardNumber = paymentDetail.getCardNumber();
        this.paymentType = paymentDetail.getPaymentType();

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

    public PaymentDetail.PaymentType getPaymentType() {
        return paymentType;
    }

    public String getCardName() {
        return cardName;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public Drawable getCardImage() {
        return cardImage;
    }

    public PaymentDetail getPaymentDetail() {
        return paymentDetail;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof ExpandedPaymentItem)) {
            return false;
        }
        return ((ExpandedPaymentItem) obj).paymentDetail.equals(getPaymentDetail());
    }

    public boolean isRemovable() {
        return isRemovable;
    }
}
