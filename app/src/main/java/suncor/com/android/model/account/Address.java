package suncor.com.android.model.account;

import android.os.Parcel;
import android.os.Parcelable;

public class Address implements Parcelable {
    private String streetAddress;
    private String city;
    private String province;
    private String postalCode;
    private String phone;

    public Address() {

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

    @Override
    public int describeContents() {
        return 0;
    }

    private Address(Parcel in) {
        streetAddress = in.readString();
        city = in.readString();
        province = in.readString();
        postalCode = in.readString();
        phone = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(streetAddress);
        dest.writeString(city);
        dest.writeString(province);
        dest.writeString(postalCode);
        dest.writeString(phone);
    }

    public static final Parcelable.Creator<Address> CREATOR = new Parcelable.Creator<Address>() {
        @Override
        public Address createFromParcel(Parcel source) {
            return new Address(source);
        }

        @Override
        public Address[] newArray(int size) {
            return new Address[size];
        }
    };
}
