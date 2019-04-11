package suncor.com.android.model.canadapost;

import com.google.gson.annotations.SerializedName;

public class CanadaPostDetails {
    @SerializedName("Id")
    private String id;
    @SerializedName("Language")
    private String language;
    @SerializedName("LanguageAlternatives")
    private String languageAlternatives;
    @SerializedName("BuildingNumber")
    private String buildingNumber;
    @SerializedName("Line1")
    private String line1;
    @SerializedName("Street")
    private String street;
    @SerializedName("City")
    private String city;
    @SerializedName("ProvinceName")
    private String provinceName;
    @SerializedName("ProvinceCode")
    private String provinceCode;
    @SerializedName("PostalCode")
    private String postalCode;

    public String getLine1() {
        return line1;
    }

    public String getBuildingNumber() {
        return buildingNumber;
    }

    public String getId() {
        return id;
    }

    public String getLanguage() {
        return language;
    }

    public String getLanguageAlternatives() {
        return languageAlternatives;
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public String getPostalCode() {
        return postalCode;
    }
}
