package suncor.com.android.model.redeem.response;

import android.os.Parcel;
import android.os.Parcelable;

public class Card implements Parcelable {
    private String cardType;
    private String cardNumber;

    protected Card(Parcel in) {
        cardType = in.readString();
        cardNumber = in.readString();
    }

    public String getCardType() {
        return cardType;
    }

    public Card(String cardType, String cardNumber) {
        this.cardType = cardType;
        this.cardNumber = cardNumber;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public static final Creator<Card> CREATOR = new Creator<Card>() {
        @Override
        public Card createFromParcel(Parcel in) {
            return new Card(in);
        }

        @Override
        public Card[] newArray(int size) {
            return new Card[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(cardType);
        parcel.writeString(cardNumber);
    }
}
