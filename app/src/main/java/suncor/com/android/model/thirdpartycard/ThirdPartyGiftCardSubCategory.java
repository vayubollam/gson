package suncor.com.android.model.thirdpartycard;

public  class ThirdPartyGiftCardSubCategory{

    private String SubcategoryName;
    private String smallIcon;
    private String largeIcon;

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
}
