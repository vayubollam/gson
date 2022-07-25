package suncor.com.android.model.account;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.text.ParseException;

import suncor.com.android.BuildConfig;
import suncor.com.android.model.SettingsResponse;
import suncor.com.android.utilities.DateUtils;
import suncor.com.android.utilities.Timber;

public class Profile {
    private String email;
    private String firstName;
    private String lastName;
    private String petroPointsNumber;
    private int pointsBalance;
    private String streetAddress;
    private String city;
    private String province;
    private String postalCode;
    private String phone;
    private boolean emailOffers;
    private boolean doNotEmail;
    private boolean textOffers;
    private String retailId;
    private boolean rbcLinked;
    private String accountDeleteDateTime;
    @SerializedName("toggleFeature")
    public ToggleFeature toggleFeature;

    public String getRetailId() {
        return retailId;
    }

    public void setRetailId(String retailId) {
        this.retailId = retailId;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPetroPointsNumber() {
        return petroPointsNumber;
    }

    public void setPetroPointsNumber(String petroPointsNumber) {
        this.petroPointsNumber = petroPointsNumber;
    }

    public ToggleFeature getToggleFeature() {
        return toggleFeature;
    }

    public void setToggleFeature(ToggleFeature toggleFeature) {
        this.toggleFeature = toggleFeature;
    }

    public int getPointsBalance() {
        return pointsBalance;
    }

    public void setPointsBalance(int pointsBalance) {
        this.pointsBalance = pointsBalance;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public boolean isEmailOffers() {
        return emailOffers;
    }

    public void setEmailOffers(boolean emailOffers) {
        this.emailOffers = emailOffers;
    }

    public boolean isTextOffers() {
        return textOffers;
    }

    public void setTextOffers(boolean textOffers) {
        this.textOffers = textOffers;
    }

    public boolean isDoNotEmail() {
        return doNotEmail;
    }

    public void setDoNotEmail(boolean doNotEmail) {
        this.doNotEmail = doNotEmail;
    }

    public boolean isRbcLinked() {
        return rbcLinked;
    }

    public void setRbcLinked(boolean rbcLinked) {
        this.rbcLinked = rbcLinked;
    }

    public String getAccountDeleteDateTime() {
        return accountDeleteDateTime;
    }

    public void setAccountDeleteDateTime(String accountDeleteDateTime) {
        this.accountDeleteDateTime = accountDeleteDateTime;
    }
    public long getAccountDeleteDaysLeft()  {
        try {
            int accountDeletionDays = Integer.parseInt(BuildConfig.ACCOUNT_DELETION_PERIOD_IN_DAYS);
           return accountDeletionDays - DateUtils.getDateTimeDifference( accountDeleteDateTime, DateUtils.getTodayFormattedDate(), true);
        }catch (Exception ex){
            Timber.e("Error on parse date", ex.getMessage());
        }
        return 0;
    }


    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof Profile)) {
            return false;
        }
        Profile profile = (Profile) obj;
        return email.equals(profile.email) && firstName.equals(profile.firstName) && lastName.equals(profile.lastName);
    }

    public String getFormattedAddress(){
        StringBuilder sb = new StringBuilder(streetAddress);
        sb.append(",").append(city).append(",").append(province).append(",").append(postalCode);
        return sb.toString();
    }

    public static class ToggleFeature {
        @SerializedName("VACUUM_SCAN_BARCODE")
        private boolean vacuumScanBarcode;

        @SerializedName("CARWASH_RELOAD")
        private boolean carWashReload;

        @SerializedName("DONATE_PETRO_POINTS")
        private boolean donatePetroPoints;

        public boolean isVacuumScanBarcode() {
            return vacuumScanBarcode;
        }

        public boolean isDonatePetroPoints() {
            return donatePetroPoints;
        }

        public boolean isCarWashReload() {
            return carWashReload;
        }

        public void setVacuumScanBarcode(boolean vacuumScanBarcode, boolean donatePetroPoints, boolean carWashReload) {
            this.vacuumScanBarcode = vacuumScanBarcode;
            this.donatePetroPoints = donatePetroPoints;
            this.carWashReload = carWashReload;
        }
    }
}
