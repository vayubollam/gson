package suncor.com.android.model.redeem.request;

import android.os.Parcel;
import android.os.Parcelable;

public class PetroPoints implements Parcelable {

    private String petroPointsCardNumber;
    private int petroPointsRedeemed;

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

    public PetroPoints(String petroPointsCardNumber, int petroPointsRedeemed) {
        this.petroPointsCardNumber = petroPointsCardNumber;
        this.petroPointsRedeemed = petroPointsRedeemed;
    }

    protected PetroPoints(Parcel in) {
        petroPointsCardNumber = in.readString();
        petroPointsRedeemed = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeString(petroPointsCardNumber);
        dest.writeInt(petroPointsRedeemed);

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
