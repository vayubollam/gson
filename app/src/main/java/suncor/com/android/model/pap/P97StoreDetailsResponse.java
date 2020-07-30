package suncor.com.android.model.pap;

import com.google.gson.annotations.SerializedName;

import suncor.com.android.model.payments.PaymentDetail;

public class P97StoreDetailsResponse {
    public String storeNumber;

    public MobilePaymentStatus mobilePaymentStatus;

    @SerializedName("fuelService")
    public FuelService fuelService;

    public static class MobilePaymentStatus {
        @SerializedName("allowOutsidePayment")
        boolean papAvailable;

        public boolean getPapAvailable() {
            return papAvailable;
        }
    }

    public static class FuelService {
        @SerializedName("fuelingPoints")
        public PumpStatus[] pumpStatuses;
    }

    public static class PumpStatus {
        public int pumpNumber;
        public String serviceLevel;
        @SerializedName("pumpStatus")
        public String status;
    }
}
