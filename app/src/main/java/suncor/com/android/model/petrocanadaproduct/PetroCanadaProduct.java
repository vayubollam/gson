package suncor.com.android.model.petrocanadaproduct;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

public class PetroCanadaProduct implements Parcelable {

    @NonNull
    private Category category;
    private SubType subType;
    private String description;
    private boolean productFeature;
    private String sku;
    private String materialCode;
    private String rewardId;
    private String title;
    private float originalPrice;
    private int pointsPrice;
    private int quantity;
    private int units;
    private float discountAmount;
    private float discountPercent;
    private float discountPrice;

    @NonNull
    public Category getCategory() {
        return category;
    }

    public void setCategory(@NonNull Category category) {
        this.category = category;
    }

    @Nullable
    public SubType getSubType() {
        return subType;
    }

    public void setSubType(@Nullable SubType subType) {
        this.subType = subType;
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    public void setDescription(@Nullable String description) {
        this.description = description;
    }

    public boolean isProductFeature() {
        return productFeature;
    }

    public void setProductFeature(boolean productFeature) {
        this.productFeature = productFeature;
    }

    @Nullable
    public String getSku() {
        return sku;
    }

    public void setSku(@Nullable String sku) {
        this.sku = sku;
    }

    @Nullable
    public String getMaterialCode() {
        return materialCode;
    }

    public void setMaterialCode(@Nullable String materialCode) {
        this.materialCode = materialCode;
    }

    @Nullable
    public String getRewardId() {
        return rewardId;
    }

    public void setRewardId(@Nullable String rewardId) {
        this.rewardId = rewardId;
    }

    @Nullable
    public String getTitle() {
        return title;
    }

    public void setTitle(@Nullable String title) {
        this.title = title;
    }

    public float getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(float originalPrice) {
        this.originalPrice = originalPrice;
    }

    public int getPointsPrice() {
        return pointsPrice;
    }

    public void setPointsPrice(int pointsPrice) {
        this.pointsPrice = pointsPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getUnits() {
        return units;
    }

    public void setUnits(int units) {
        this.units = units;
    }

    public float getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(float discountAmount) {
        this.discountAmount = discountAmount;
    }

    public float getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(float discountPercent) {
        this.discountPercent = discountPercent;
    }

    public float getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(float discountPrice) {
        this.discountPrice = discountPrice;
    }

    public static final Creator<PetroCanadaProduct> CREATOR = new Creator<PetroCanadaProduct>() {
        @Override
        public PetroCanadaProduct createFromParcel(Parcel in) {
            return new PetroCanadaProduct(in);
        }

        @Override
        public PetroCanadaProduct[] newArray(int size) {
            return new PetroCanadaProduct[size];
        }
    };

    protected PetroCanadaProduct(Parcel in) {
        category = Category.valueOf(in.readString());
        subType = SubType.valueOf(in.readString());
        productFeature = in.readInt() == 1;
        sku = in.readString();
        materialCode = in.readString();
        rewardId = in.readString();
        title = in.readString();
        originalPrice = in.readFloat();
        pointsPrice = in.readInt();
        quantity = in.readInt();
        units = in.readInt();
        discountAmount = in.readFloat();
        discountPercent = in.readFloat();
        discountPrice = in.readFloat();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(category.name());
        parcel.writeString(subType.name());
        parcel.writeInt(productFeature ? 1 : 0);
        parcel.writeString(sku);
        parcel.writeString(materialCode);
        parcel.writeString(rewardId);
        parcel.writeString(title);
        parcel.writeFloat(originalPrice);
        parcel.writeInt(pointsPrice);
        parcel.writeInt(quantity);
        parcel.writeInt(units);
        parcel.writeFloat(discountAmount);
        parcel.writeFloat(discountPercent);
        parcel.writeFloat(discountPrice);
    }

    public enum Category {
        @SerializedName("wag")
        WAG,
        @SerializedName("sp")
        SP,
        @SerializedName("fsr")
        FSR,
        @SerializedName("ticket")
        ST
    }

    public enum SubType {
        @SerializedName("super")
        SUPER,
        @SerializedName("glide")
        GLIDE,
        @SerializedName("one")
        SINGLE
    }

}
