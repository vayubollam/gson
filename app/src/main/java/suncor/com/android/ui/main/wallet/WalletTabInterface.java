package suncor.com.android.ui.main.wallet;

import android.content.Context;

import suncor.com.android.uicomponents.swiperefreshlayout.SwipeRefreshLayout;

public interface WalletTabInterface extends SwipeRefreshLayout.OnRefreshListener {
    String getTabName(Context context);
    void navigateToAddCard();
}
