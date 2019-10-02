package suncor.com.android.ui.main.rewards.redeem;

import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import suncor.com.android.mfp.SessionManager;
import suncor.com.android.ui.main.rewards.MerchantItem;

public class GiftCardValueConfirmationViewModel extends ViewModel {
    private MerchantItem merchantItem;
    private SessionManager sessionManager;

    @Inject
    public GiftCardValueConfirmationViewModel(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public MerchantItem getMerchantItem() {
        return merchantItem;
    }

    public void setMerchantItem(MerchantItem merchantItem) {
        this.merchantItem = merchantItem;
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }
}
