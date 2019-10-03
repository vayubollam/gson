package suncor.com.android.model.redeem.request;

import android.os.Parcel;
import android.os.Parcelable;

public class RedeemTransactionAmount implements Parcelable {

    private int quantity;
    private PetroPoints petroPoints;

    public static final Creator<RedeemTransactionAmount> CREATOR = new Creator<RedeemTransactionAmount>() {
        @Override
        public RedeemTransactionAmount createFromParcel(Parcel in) {
            return new RedeemTransactionAmount(in);
        }

        @Override
        public RedeemTransactionAmount[] newArray(int size) {
            return new RedeemTransactionAmount[size];
        }
    };

    public RedeemTransactionAmount(int quantity, PetroPoints petroPoints) {
        this.quantity = quantity;
        this.petroPoints = petroPoints;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flag) {
        dest.writeInt(quantity);
        dest.writeParcelable(petroPoints, flag);
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public PetroPoints getPetroPoints() {
        return petroPoints;
    }

    public void setPetroPoints(PetroPoints petroPoints) {
        this.petroPoints = petroPoints;
    }

    protected RedeemTransactionAmount(Parcel in) {
        quantity = in.readInt();
        petroPoints = in.readParcelable(PetroPoints.class.getClassLoader());
    }
}
