package suncor.com.android.model.thirdpartycard;

import java.util.List;

import suncor.com.android.model.merchants.EGift;

public  class ThirdPartyGiftCardSubCategory{

    private String subCategoryName;
    private String smallIcon;
    private String largeIcon;
    private String howToUse;
    private String howToRedeem;
    private String merchantId;
    private String shortName;
    private String screenName;
    private List<EGift> eGiftList;

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getSubcategoryName() {
        return subCategoryName;
    }

    public void setSubcategoryName(String subcategoryName) {
        subCategoryName = subcategoryName;
    }

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

    public String getSmallIcon() {
        return smallIcon;
    }

    public String getLargeIcon() {
        return largeIcon;
    }

    public ThirdPartyGiftCardSubCategory(String subcategoryName) {
        subCategoryName = subcategoryName;
    }

    public List<EGift> geteGiftList() {
        return eGiftList;
    }

    public void seteGiftList(List<EGift> eGiftList) {
        this.eGiftList = eGiftList;
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
}
