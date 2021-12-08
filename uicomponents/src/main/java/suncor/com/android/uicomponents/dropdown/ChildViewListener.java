package suncor.com.android.uicomponents.dropdown;

public interface ChildViewListener {
        void onSelectValue(String header, String subheader, boolean isFromRedeemSection, boolean isRedeemSelectionChanged);
        void onAddBonus(String header);
        void onAddDiscount(String header);
        void expandCollapse();
        void onSelectGooglePay(String header);
        void onRedeemSectionChanged(boolean isRedeemChanged);
}
