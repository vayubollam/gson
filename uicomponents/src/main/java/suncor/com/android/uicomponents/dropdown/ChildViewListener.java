package suncor.com.android.uicomponents.dropdown;

public interface ChildViewListener {
        void onSelectValue(String header, String subheader, boolean isFromRedeemSection, boolean isRedeemSelectionChanged);
        void expandCollapse();
        void onSelectGooglePay(String header);
        void onRedeemSectionChanged(boolean isRedeemChanged);
}
