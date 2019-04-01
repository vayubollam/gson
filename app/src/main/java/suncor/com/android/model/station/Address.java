package suncor.com.android.model.station;

public class Address {

    private String subdivision;
    private String phone;
    private String countryRegion;
    private String postalCode;
    private Double latitude;
    private Double longitude;
    private String addressLine;
    private String crossStreet;
    private String primaryCity;

    public Address(String subdivision, String phone, String countryRegion, String postalCode, Double latitude, Double longitude, String addressLine, String crossStreet, String primaryCity) {

        this.subdivision = subdivision;
        this.phone = phone;
        this.countryRegion = countryRegion;
        this.postalCode = postalCode;
        this.latitude = latitude;
        this.longitude = longitude;
        this.addressLine = addressLine;
        this.crossStreet = crossStreet;
        this.primaryCity = primaryCity;
    }

    public String getSubdivision() {
        return subdivision;
    }

    public void setSubdivision(String subdivision) {
        this.subdivision = subdivision;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCountryRegion() {
        return countryRegion;
    }

    public void setCountryRegion(String countryRegion) {
        this.countryRegion = countryRegion;
    }

    public String getpostalCode() {
        return postalCode;
    }

    public Double getLatitude() {

        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getAddressLine() {
        return addressLine;
    }

    public void setAddressLine(String addressLine) {
        this.addressLine = addressLine;
    }

    public String getCrossStreet() {
        return crossStreet;
    }

    public void setCrossStreet(String crossStreet) {
        this.crossStreet = crossStreet;
    }

    public String getPrimaryCity() {
        return primaryCity;
    }

    public void setPrimaryCity(String primaryCity) {
        this.primaryCity = primaryCity;
    }

    public void setpostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
}
