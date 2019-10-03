package suncor.com.android.model.redeem.request;

import android.os.Parcel;
import android.os.Parcelable;

import suncor.com.android.model.redeem.request.PetroPoints;

public class RedeemCard implements Parcelable {

    private String cardType;
    private String cardNumber;

    public String getCardType() {
        return cardType;
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

    public RedeemCard(String cardType, String cardNumber) {
        this.cardType = cardType;
        this.cardNumber = cardNumber;
    }

    protected RedeemCard(Parcel in) {
        cardType = in.readString();
        cardNumber = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeString(cardType);
        dest.writeString(cardNumber);
    }

    public static final Parcelable.Creator<PetroPoints> CREATOR = new Parcelable.Creator<PetroPoints>() {
        @Override
        public PetroPoints createFromParcel(Parcel in) {
            return new PetroPoints(in);
        }

        @Override
        public PetroPoints[] newArray(int size) {
            return new PetroPoints[size];
        }
    };
}
