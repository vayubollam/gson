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
    private boolean textOffers;

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhone() {
        return phone;
    }

    public String getPetroPointsNumber() {
        return petroPointsNumber;
    }

    public int getPointsBalance() {
        return pointsBalance;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public String getCity() {
        return city;
    }

    public String getProvince() {
        return province;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public boolean isEmailOffers() {
        return emailOffers;
    }

    public boolean isTextOffers() {
        return textOffers;
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
