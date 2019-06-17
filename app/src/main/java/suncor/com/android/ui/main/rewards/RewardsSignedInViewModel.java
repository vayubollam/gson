package suncor.com.android.ui.main.rewards;

import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import suncor.com.android.mfp.SessionManager;
import suncor.com.android.ui.common.cards.CardFormatUtils;

public class RewardsSignedInViewModel extends ViewModel {

    private Reward[] rewards;
    private SessionManager sessionManager;

    @Inject
    public RewardsSignedInViewModel(SessionManager sessionManager, RewardsReader rewardsReader) {
        rewards = rewardsReader.getRewards();
        this.sessionManager = sessionManager;
    }

    public Reward[] getRewards() {
        return rewards;
    }

    public String getPetroPoints() {
        return CardFormatUtils.formatBalance(sessionManager.getProfile().getPointsBalance());
    }

    public int getPetroPointsValue() {
        return sessionManager.getProfile().getPointsBalance() / 1000;
    }

    public void navigateToPetroPoints() {

    }
}
