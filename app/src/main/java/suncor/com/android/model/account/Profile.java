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

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof Profile)) {
            return false;
        }
        Profile profile = (Profile) obj;
        return email.equals(profile.email) && firstName.equals(profile.firstName) && lastName.equals(profile.lastName);
    }
}
