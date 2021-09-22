package suncor.com.android.ui.main.rewards.redeem;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import suncor.com.android.model.merchants.EGift;

public class GenericEGiftCard implements Parcelable {

    private String name;
    private String points;
    private String title;
    private String subtitle;
    private String description;
    private String largeImage;
    private String smallImage;
    private String shortName;
    private String screenName;
    private boolean isDataDynamic;
    private List<EGift> eGifts;

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public List<EGift> geteGifts() {
        return eGifts;
    }

    public void seteGifts(List<EGift> eGifts) {
        this.eGifts = eGifts;
    }

    public static Creator<GenericEGiftCard> getCREATOR() {
        return CREATOR;
    }

    public GenericEGiftCard(){

    }

    protected GenericEGiftCard(Parcel in) {
        name = in.readString();
        points = in.readString();
        title = in.readString();
        subtitle = in.readString();
        description = in.readString();
        largeImage = in.readString();
        smallImage = in.readString();
        isDataDynamic = in.readByte() != 0;
    }

    public static final Creator<GenericEGiftCard> CREATOR = new Creator<GenericEGiftCard>() {
        @Override
        public GenericEGiftCard createFromParcel(Parcel in) {
            return new GenericEGiftCard(in);
        }

        @Override
        public GenericEGiftCard[] newArray(int size) {
            return new GenericEGiftCard[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLargeImage() {
        return largeImage;
    }

    public void setLargeImage(String largeImage) {
        this.largeImage = largeImage;
    }

    public String getSmallImage() {
        return smallImage;
    }

    public void setSmallImage(String smallImage) {
        this.smallImage = smallImage;
    }

    public boolean isDataDynamic() {
        return isDataDynamic;
    }

    public void setDataDynamic(boolean dataDynamic) {
        isDataDynamic = dataDynamic;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(points);
        dest.writeString(title);
        dest.writeString(subtitle);
        dest.writeString(description);
        dest.writeString(largeImage);
        dest.writeString(smallImage);
        dest.writeByte((byte) (isDataDynamic ? 1 : 0));
    }
}
