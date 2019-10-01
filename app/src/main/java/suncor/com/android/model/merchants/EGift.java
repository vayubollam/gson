package suncor.com.android.model.merchants;

import android.os.Parcel;
import android.os.Parcelable;

public class EGift implements Parcelable {
    private int id;
    private String description;
    private int value;
    private int petroPointsRequired;
    private int merchantId;

    public EGift(int id, String description, int value, int petroPointsRequired, int merchantId) {
        this.id = id;
        this.description = description;
        this.value = value;
        this.petroPointsRequired = petroPointsRequired;
        this.merchantId = merchantId;
    }

    public static final Creator<EGift> CREATOR = new Creator<EGift>() {
        @Override
        public EGift createFromParcel(Parcel in) {
            return new EGift(in);
        }

        @Override
        public EGift[] newArray(int size) {
            return new EGift[size];
        }
    };

    protected EGift(Parcel in) {
        id = in.readInt();
        description = in.readString();
        value = in.readInt();
        petroPointsRequired = in.readInt();
        merchantId = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(description);
        dest.writeInt(value);
        dest.writeInt(petroPointsRequired);
        dest.writeInt(merchantId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getPetroPointsRequired() {
        return petroPointsRequired;
    }

    public void setPetroPointsRequired(int petroPointsRequired) {
        this.petroPointsRequired = petroPointsRequired;
    }

    public int getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(int merchantId) {
        this.merchantId = merchantId;
    }
}
