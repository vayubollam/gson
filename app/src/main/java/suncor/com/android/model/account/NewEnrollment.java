package suncor.com.android.model.account;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;

import suncor.com.android.utilities.EmptyStringTypeAdapter;

public class NewEnrollment {

    public enum EnrollmentType {
        @SerializedName("new") NEW,
        @SerializedName("ghost") GHOST,
        @SerializedName("existing") EXISTING
    }

    private EnrollmentType type;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String streetAddress;
    private String city;
    private String province;
    private String postalCode;

    @JsonAdapter(EmptyStringTypeAdapter.class)
    private String phone;

    private boolean recieveOffers;
    private String securityQuestionId;
    private String securityAnswer;

    public NewEnrollment(EnrollmentType type, String firstName, String lastName, String email, String password, String streetAddress, String city, String province, String postalCode, String phone, boolean recieveOffers, String securityQuestionId, String securityAnswer) {
        this.type = type;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.streetAddress = streetAddress;
        this.city = city;
        this.province = province;
        this.postalCode = postalCode;
        this.phone = phone;
        this.recieveOffers = recieveOffers;
        this.securityQuestionId = securityQuestionId;
        this.securityAnswer = securityAnswer;
    }

    public EnrollmentType getType() {
        return type;
    }

    public void setType(EnrollmentType type) {
        this.type = type;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isRecieveOffers() {
        return recieveOffers;
    }

    public void setRecieveOffers(boolean recieveOffers) {
        this.recieveOffers = recieveOffers;
    }

    public String getSecurityQuestionId() {
        return securityQuestionId;
    }

    public void setSecurityQuestionId(String securityQuestionId) {
        this.securityQuestionId = securityQuestionId;
    }

    public String getSecurityAnswer() {
        return securityAnswer;
    }

    public void setSecurityAnswer(String securityAnswer) {
        this.securityAnswer = securityAnswer;
    }
}
