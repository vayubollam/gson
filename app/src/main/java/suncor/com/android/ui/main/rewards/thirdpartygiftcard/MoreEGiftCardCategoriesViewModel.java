package suncor.com.android.ui.main.rewards.thirdpartygiftcard;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import suncor.com.android.mfp.SessionManager;
import suncor.com.android.model.account.Profile;
import suncor.com.android.model.thirdpartycard.ThirdPartyGiftCardCategory;
import suncor.com.android.ui.common.cards.CardFormatUtils;
import suncor.com.android.ui.main.rewards.RewardsReader;

public class MoreEGiftCardCategoriesViewModel extends ViewModel {

    private Profile profile;
    private RewardsReader rewardsReader;
    private SessionManager sessionManager;

    @Inject
    public MoreEGiftCardCategoriesViewModel(SessionManager sessionManager, RewardsReader rewardsReader) {

        this.sessionManager = sessionManager;
        this.rewardsReader = rewardsReader;
        this.profile = sessionManager.getProfile();

    }

    public String getPetroPointsBalance() {
        return CardFormatUtils.formatBalance(sessionManager.getProfile().getPointsBalance());
    }

    public List<ThirdPartyGiftCardCategory> getRewards() {
        return new ArrayList<>(Arrays.asList(rewardsReader.getThirdPartyRawFile()));
    }

}
