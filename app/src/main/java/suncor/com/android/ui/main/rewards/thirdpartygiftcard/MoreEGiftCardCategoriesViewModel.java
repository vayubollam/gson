package suncor.com.android.ui.main.rewards.thirdpartygiftcard;

import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;

import javax.inject.Inject;

import suncor.com.android.data.pap.PapRepository;
import suncor.com.android.data.payments.PaymentsRepository;
import suncor.com.android.data.settings.SettingsApi;
import suncor.com.android.mfp.SessionManager;
import suncor.com.android.model.account.Profile;
import suncor.com.android.model.thirdpartycard.ThirdPartyGiftCardCategory;
import suncor.com.android.ui.common.cards.CardFormatUtils;
import suncor.com.android.ui.main.rewards.Reward;
import suncor.com.android.ui.main.rewards.RewardsReader;

public class MoreEGiftCardCategoriesViewModel extends ViewModel {

    private Profile profile;
    private SessionManager sessionManager;
    private ThirdPartyGiftCardCategory[] thirdPartyGiftCardCategory;

    @Inject
    public MoreEGiftCardCategoriesViewModel(SessionManager sessionManager, RewardsReader rewardsReader){

        this.sessionManager = sessionManager;
        this.profile = sessionManager.getProfile();
        thirdPartyGiftCardCategory = rewardsReader.getThirdPartyRawFile();
    }

    public String getPetroPointsBalance() {
        return CardFormatUtils.formatBalance(sessionManager.getProfile().getPointsBalance());
    }

    public ThirdPartyGiftCardCategory[] getRewards() {
        return thirdPartyGiftCardCategory;
    }

}
