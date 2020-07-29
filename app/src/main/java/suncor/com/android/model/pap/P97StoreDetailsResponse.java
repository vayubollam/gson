package suncor.com.android.model.pap;

import com.google.gson.annotations.SerializedName;

import suncor.com.android.model.payments.PaymentDetail;

public class P97StoreDetailsResponse {
    public String storeNumber;

    public MobilePaymentStatus mobilePaymentStatus;

    public static class MobilePaymentStatus {
        @SerializedName("allowOutsidePayment")
        boolean papAvailable;

        public boolean getPapAvailable() {
            return papAvailable;
        }
    }
}
