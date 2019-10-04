package suncor.com.android.model.redeem.response;

import android.os.Parcel;
import android.os.Parcelable;

public class EGift implements Parcelable {

    private int rewardId;
    private int petroPointsRequired;
    private int merchantId;
    private String description;
    private int id;
    private int value;

    public EGift(int rewardId, int petroPointsRequired, int merchantId, String description, int id, int value) {
        this.rewardId = rewardId;
        this.petroPointsRequired = petroPointsRequired;
        this.merchantId = merchantId;
        this.description = description;
        this.id = id;
        this.value = value;
    }

    public int getRewardId() {
        return rewardId;
    }

    public void setRewardId(int rewardId) {
        this.rewardId = rewardId;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    protected EGift(Parcel in) {
        rewardId = in.readInt();
        petroPointsRequired = in.readInt();
        merchantId = in.readInt();
        description = in.readString();
        id = in.readInt();
        value = in.readInt();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(rewardId);
        parcel.writeInt(petroPointsRequired);
        parcel.writeInt(merchantId);
        parcel.writeString(description);
        parcel.writeInt(id);
        parcel.writeInt(value);
    }
}
