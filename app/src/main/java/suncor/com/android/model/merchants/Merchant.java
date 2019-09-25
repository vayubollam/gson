package suncor.com.android.model.merchants;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import androidx.annotation.Nullable;

public class Merchant implements Parcelable {
    private String merchantName;
    private int displayOrder;
    private int merchantId;
    private List<EGift> eGifts;

    public Merchant(String merchantName, int displayOrder, int merchantId, List<EGift> eGifts) {
        this.merchantName = merchantName;
        this.displayOrder = displayOrder;
        this.merchantId = merchantId;
        this.eGifts = eGifts;
    }

    public static final Creator<Merchant> CREATOR = new Creator<Merchant>() {
        @Override
        public Merchant createFromParcel(Parcel in) {
            return new Merchant(in);
        }

        @Override
        public Merchant[] newArray(int size) {
            return new Merchant[size];
        }
    };

    protected Merchant(Parcel in) {
        merchantName = in.readString();
        displayOrder = in.readInt();
        merchantId = in.readInt();
        eGifts = in.createTypedArrayList(EGift.CREATOR);
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    public int getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(int merchantId) {
        this.merchantId = merchantId;
    }

    public List<EGift> geteGifts() {
        return eGifts;
    }

    public void seteGifts(List<EGift> eGifts) {
        this.eGifts = eGifts;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof Merchant)) {
            return false;
        }
        return ((Merchant) obj).getMerchantId() == this.getMerchantId();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(merchantName);
        dest.writeInt(displayOrder);
        dest.writeInt(merchantId);
        dest.writeTypedList(eGifts);
    }
}
