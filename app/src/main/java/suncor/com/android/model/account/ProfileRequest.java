package suncor.com.android.model.account;

public class ProfileRequest {
    String email;
    Address address;
    Offers offers;

    public ProfileRequest(Profile profile) {
        email = profile.getEmail();
        address = new Address();
        address.setStreetAddress(profile.getStreetAddress());
        address.setCity(profile.getCity());
        address.setPostalCode(profile.getPostalCode());
        address.setPhone(profile.getPhone());
        address.setProvince(profile.getProvince());
        offers = new Offers();
        offers.text = profile.isTextOffers();
        offers.email = profile.isEmailOffers();
    }

    public void setEmailOffers(boolean emailOffers) {
        this.offers.email = emailOffers;
    }

    public void setTextOffers(boolean textOffers) {
        this.offers.text = textOffers;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.address.setPhone(phoneNumber);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    static class Offers {
        boolean email;
        boolean text;
    }
}
