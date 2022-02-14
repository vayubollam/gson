package suncor.com.android.model.pap;

import com.google.gson.annotations.SerializedName;

import suncor.com.android.model.payments.PaymentDetail;

public class P97StoreDetailsResponse {
    public String storeNumber;

    public MobilePaymentStatus mobilePaymentStatus;

    public Address address;

    @SerializedName("fuelService")
    public FuelService fuelService;

    public static class MobilePaymentStatus {
        @SerializedName("allowOutsidePayment")
        boolean papAvailable;

        public boolean getPapAvailable() {
            return papAvailable;
        }
    }

    public static class Address{

        String streetAddress;
        String city;
        String countryIsoCode;
        String postalCode;
        String stateCode;

        public String getStateCode() {
            return stateCode;
        }

        public void setStateCode(String stateCode) {
            this.stateCode = stateCode;
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
