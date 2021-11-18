package suncor.com.android.uicomponents.dropdown;

public interface ChildViewListener {
        void onSelectValue(String header, String subheader);
        void onAddBonus(String header);
        void onAddDiscount(String header);
        void expandCollapse();
        void onSelectGooglePay(String header);
}
