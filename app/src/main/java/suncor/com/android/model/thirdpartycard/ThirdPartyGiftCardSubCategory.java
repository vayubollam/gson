package suncor.com.android.model.thirdpartycard;

public  class ThirdPartyGiftCardSubCategory{

    private String SubcategoryName;
    private String smallIcon;
    private String largeIcon;
    private String howToUse;
    private String howToRedeem;
    private String merchantId;

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getSubcategoryName() {
        return SubcategoryName;
    }

    public void setSubcategoryName(String subcategoryName) {
        SubcategoryName = subcategoryName;
    }


    public String getSmallIcon() {
        return smallIcon;
    }

    public String getLargeIcon() {
        return largeIcon;
    }

    public ThirdPartyGiftCardSubCategory(String subcategoryName) {
        SubcategoryName = subcategoryName;
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
