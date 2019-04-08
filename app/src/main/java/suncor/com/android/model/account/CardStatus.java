package suncor.com.android.model.account;

import android.os.Parcel;
import android.os.Parcelable;

public class CardStatus implements Parcelable {
    NewEnrollment.EnrollmentType cardType;
    UserInfo userInfo;
    Address address;

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
        dest.writeString(cardType.name());
    }

    public CardStatus(Parcel in) {
        userInfo = in.readParcelable(UserInfo.class.getClassLoader());
        address = in.readParcelable(Address.class.getClassLoader());
        cardType = NewEnrollment.EnrollmentType.valueOf(in.readString());
    }
}
