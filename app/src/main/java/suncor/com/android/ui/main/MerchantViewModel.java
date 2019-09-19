package suncor.com.android.ui.main;

import javax.inject.Inject;

import androidx.lifecycle.ViewModel;
import suncor.com.android.data.redeem.MerchantsRepository;
import suncor.com.android.mfp.SessionManager;
import suncor.com.android.utilities.Timber;

public class MerchantViewModel extends ViewModel {
    @Inject
    public MerchantViewModel(MerchantsRepository merchantsRepository, SessionManager sessionManager) {
        sessionManager.getLoginState().observeForever((loginState -> {
            if (loginState == SessionManager.LoginState.LOGGED_IN) {
                Timber.d("start retrieving merchants");
                merchantsRepository.getMerchants().observeForever(arrayListResource -> {

                });
            }
        }));
    }
}
