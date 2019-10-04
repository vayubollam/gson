package suncor.com.android.model.redeem.request;

import android.os.Parcel;
import android.os.Parcelable;

import suncor.com.android.model.merchants.EGift;

public class ShoppingCart implements Parcelable {

    private EGift eGift;

    public void seteGift(EGift eGift) {
        this.eGift = eGift;
    }

    public EGift geteGift() {
        return eGift;
    }

    protected ShoppingCart(Parcel in) {
        in.readParcelable(EGift.class.getClassLoader());
    }

    public ShoppingCart(EGift eGift) {
        this.eGift = eGift;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(eGift, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ShoppingCart> CREATOR = new Creator<ShoppingCart>() {
        @Override
        public ShoppingCart createFromParcel(Parcel in) {
            return new ShoppingCart(in);
        }

        @Override
        public ShoppingCart[] newArray(int size) {
            return new ShoppingCart[size];
        }
    };
}


