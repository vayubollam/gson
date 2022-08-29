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
    private String howToUse;
    private String howToRedeem;
    private String largeImage;
    private String smallImage;
    private String shortName;
    private String screenName;
    private String group;
    private String notEnoughPointsErrorMsg;

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    private boolean isDataDynamic;
    private boolean isMoreGIftCard;

    public boolean isMoreGIftCard() {
        return isMoreGIftCard;
    }

    public void setMoreGIftCard(boolean moreGIftCard) {
        isMoreGIftCard = moreGIftCard;
    }

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

    public GenericEGiftCard() {

    }

    protected GenericEGiftCard(Parcel in) {
        name = in.readString();
        points = in.readString();
        title = in.readString();
        subtitle = in.readString();
        howToUse = in.readString();
        howToRedeem = in.readString();
        largeImage = in.readString();
        smallImage = in.readString();
        notEnoughPointsErrorMsg = in.readString();
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

    public String getHowToUse() {
        return howToUse;
    }

    public void setHowToUse(String howToUse) {
        this.howToUse = howToUse;
    }

    public String getHowToRedeem() {
        return howToRedeem;
    }

    public void setHowToRedeem(String howToRedeem) {
        this.howToRedeem = howToRedeem;
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

    public String getNotEnoughPointsErrorMsg() {
        return notEnoughPointsErrorMsg;
    }

    public void setNotEnoughPointsErrorMsg(String notEnoughPointsErrorMsg) {
        this.notEnoughPointsErrorMsg = notEnoughPointsErrorMsg;
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
        dest.writeString(howToRedeem);
        dest.writeString(howToUse);
        dest.writeString(largeImage);
        dest.writeString(smallImage);
        dest.writeString(notEnoughPointsErrorMsg);
        dest.writeByte((byte) (isDataDynamic ? 1 : 0));
    }
}