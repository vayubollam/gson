package suncor.com.android.model.redeem.response;

import android.os.Parcel;
import android.os.Parcelable;

public class TransactionAmount implements Parcelable {

    private int quantity;
    private PetroPoints petroPoints;

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

    public TransactionAmount(int quantity, PetroPoints petroPoints) {
        this.quantity = quantity;
        this.petroPoints = petroPoints;
    }

    protected TransactionAmount(Parcel in) {
        quantity = in.readInt();
        petroPoints = in.readParcelable(PetroPoints.class.getClassLoader());
    }

    public static final Creator<TransactionAmount> CREATOR = new Creator<TransactionAmount>() {
        @Override
        public TransactionAmount createFromParcel(Parcel in) {
            return new TransactionAmount(in);
        }

        @Override
        public TransactionAmount[] newArray(int size) {
            return new TransactionAmount[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(quantity);
        parcel.writeParcelable(petroPoints, i);
    }
}
