package suncor.com.android.model.account;

public class ProfileRequest {
    String email;
    Address address;
    Offers offers;
    String password;
    String securityAnswerEncrypted;

    public String getSecurityAnswerEncrypted() {
        return securityAnswerEncrypted;
    }

    public void setSecurityAnswerEncrypted(String securityAnswerEncrypted) {
        this.securityAnswerEncrypted = securityAnswerEncrypted;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ProfileRequest(Profile profile) {
        email = profile.getEmail();
        address = new Address();
        address.setStreetAddress(profile.getStreetAddress());
        address.setCity(profile.getCity());
        address.setPostalCode(profile.getPostalCode());
        address.setPhone(profile.getPhone());
        address.setProvince(profile.getProvince());
    }

    public void setEmailOffers(boolean emailOffers) {
        if (offers == null) {
            offers = new Offers();
        }
        this.offers.email = emailOffers;
    }

    public void setTextOffers(boolean textOffers) {
        if (offers == null) {
            offers = new Offers();
        }

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
        Boolean email;
        Boolean text;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
