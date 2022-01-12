package suncor.com.android.model.account;

public class ProfileRequest {
    String email;
    Address address;
    Offers offers;
    String password;
    String securityAnswerEncrypted;
    String petroPointsCardNumber;

    public String getSecurityAnswerEncrypted() {
        return securityAnswerEncrypted;
    }

    public void setSecurityAnswerEncrypted(String securityAnswerEncrypted) {
        this.securityAnswerEncrypted = securityAnswerEncrypted;
    }

    public ProfileRequest() {
        //do nothing
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
        if (this.address == null) {
            this.address = new Address();
        }
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

    public String getPetroPointsCardNumber() {
        return petroPointsCardNumber;
    }

    public void setPetroPointsCardNumber(String petroPointsCardNumber) {
        this.petroPointsCardNumber = petroPointsCardNumber;
    }

    public Address getAddress() {
        if (this.address == null) {
            this.address = new Address();
        }
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
