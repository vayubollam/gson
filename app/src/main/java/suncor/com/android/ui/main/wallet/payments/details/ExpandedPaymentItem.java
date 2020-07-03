package suncor.com.android.ui.main.wallet.payments.details;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.annotation.Nullable;

import suncor.com.android.R;
import suncor.com.android.model.payments.PaymentDetail;

public class ExpandedPaymentItem {
    private Drawable cardImage;
    private String cardNumber;
    private PaymentDetail paymentDetail;
    private PaymentDetail.PaymentType paymentType;
    private boolean isRemovable = true;

    ExpandedPaymentItem(Context context, PaymentDetail paymentDetail) {
        this.paymentDetail = paymentDetail;
        this.cardNumber = context.getString(R.string.payment_card_number, paymentDetail.getCardNumber().replaceAll("[^\\d]", ""));
        this.paymentType = paymentDetail.getPaymentType();
        this.cardImage = context.getDrawable(paymentDetail.getCardImage());

    }

    public PaymentDetail.PaymentType getPaymentType() {
        return paymentType;
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
