package suncor.com.android.model.thirdpartycard;

import java.util.List;

public class ThirdPartyGiftCardCategory {

    public ThirdPartyGiftCardCategory(String categoryName, List<ThirdPartyGiftCardSubCategory> thirdPartyGiftCardSubCategory) {
        this.categoryName = categoryName;
        this.thirdPartyGiftCardSubCategory = thirdPartyGiftCardSubCategory;
    }

    private String categoryName;

    private List<ThirdPartyGiftCardSubCategory> thirdPartyGiftCardSubCategory;

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }


    public List<ThirdPartyGiftCardSubCategory> getThirdPartyGiftCardSubCategory() {
        return thirdPartyGiftCardSubCategory;
    }

    public void setThirdPartyGiftCardSubCategory(List<ThirdPartyGiftCardSubCategory> thirdPartyGiftCardSubCategory) {
        this.thirdPartyGiftCardSubCategory = thirdPartyGiftCardSubCategory;
    }
}


