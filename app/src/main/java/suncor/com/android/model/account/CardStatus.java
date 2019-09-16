package suncor.com.android.model.account;

import android.os.Parcel;
import android.os.Parcelable;

public class CardStatus implements Parcelable {
    public static final Creator<CardStatus> CREATOR = new Creator<CardStatus>() {
        @Override
        public CardStatus createFromParcel(Parcel in) {

            return new CardStatus(in);
        }

        @Override
        public CardStatus[] newArray(int size) {

            return new CardStatus[size];
        }
    };
    private NewEnrollment.EnrollmentType cardType;
    private String cardNumber;
    private UserInfo userInfo;
    private Address address;

    public CardStatus(Parcel in) {
        userInfo = in.readParcelable(UserInfo.class.getClassLoader());
        address = in.readParcelable(Address.class.getClassLoader());
        cardNumber = in.readString();
        cardType = NewEnrollment.EnrollmentType.valueOf(in.readString());
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public NewEnrollment.EnrollmentType getCardType() {
        return cardType;
    }

    public void setCardType(NewEnrollment.EnrollmentType cardType) {
        this.cardType = cardType;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(userInfo, flags);
        dest.writeParcelable(address, flags);
        dest.writeString(cardNumber);
        dest.writeString(cardType.name());
    }
}
