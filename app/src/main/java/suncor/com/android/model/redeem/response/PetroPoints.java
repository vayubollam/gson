package suncor.com.android.model.redeem.response;

import android.os.Parcel;
import android.os.Parcelable;

public class PetroPoints implements Parcelable {

    private String petroPointsCardNumber;
    private int petroPointsRedeemed;
    private int petroPointsRemaining;


    public String getPetroPointsCardNumber() {
        return petroPointsCardNumber;
    }

    public void setPetroPointsCardNumber(String petroPointsCardNumber) {
        this.petroPointsCardNumber = petroPointsCardNumber;
    }

    public int getPetroPointsRedeemed() {
        return petroPointsRedeemed;
    }

    public void setPetroPointsRedeemed(int petroPointsRedeemed) {
        this.petroPointsRedeemed = petroPointsRedeemed;
    }

    public int getPetroPointsRemaining() {
        return petroPointsRemaining;
    }

    public void setPetroPointsRemaining(int petroPointsRemaining) {
        this.petroPointsRemaining = petroPointsRemaining;
    }

    public PetroPoints(String petroPointsCardNumber, int petroPointsRedeemed, int petroPointsRemaining) {
        this.petroPointsCardNumber = petroPointsCardNumber;
        this.petroPointsRedeemed = petroPointsRedeemed;
        this.petroPointsRemaining = petroPointsRemaining;
    }

    protected PetroPoints(Parcel in) {
        petroPointsCardNumber = in.readString();
        petroPointsRedeemed = in.readInt();
        petroPointsRemaining = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(petroPointsCardNumber);
        dest.writeInt(petroPointsRedeemed);
        dest.writeInt(petroPointsRemaining);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PetroPoints> CREATOR = new Creator<PetroPoints>() {
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
