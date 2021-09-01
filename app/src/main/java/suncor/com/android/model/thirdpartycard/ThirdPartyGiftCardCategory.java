package suncor.com.android.model.thirdpartycard;

public class ThirdPartyGiftCardCategory {

    private String categoryName;

    private ThirdPartyGiftCardSubCategory thirdPartyGiftCardSubCategory;

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public ThirdPartyGiftCardSubCategory getThirdPartyGiftCardSubCategory() {
        return thirdPartyGiftCardSubCategory;
    }

    public void setThirdPartyGiftCardSubCategory(ThirdPartyGiftCardSubCategory thirdPartyGiftCardSubCategory) {
        this.thirdPartyGiftCardSubCategory = thirdPartyGiftCardSubCategory;
    }

    public static class ThirdPartyGiftCardSubCategory{

        private String SubcategoryName;

        public String getSubcategoryName() {
            return SubcategoryName;
        }

        public void setSubcategoryName(String subcategoryName) {
            SubcategoryName = subcategoryName;
        }
    }
}
