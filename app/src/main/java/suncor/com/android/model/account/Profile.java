package suncor.com.android.model.account;

import androidx.annotation.Nullable;

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

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof Profile)) {
            return false;
        }
        Profile profile = (Profile) obj;
        return email.equals(profile.email) && firstName.equals(profile.firstName) && lastName.equals(profile.lastName);
    }
}
