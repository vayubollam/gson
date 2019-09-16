package suncor.com.android.ui.main.rewards;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import suncor.com.android.mfp.SessionManager;
import suncor.com.android.ui.common.Event;
import suncor.com.android.ui.common.cards.CardFormatUtils;

public class RewardsSignedInViewModel extends ViewModel {

    private MutableLiveData<Event> _navigateToPetroPoints = new MutableLiveData<>();
    public LiveData<Event> navigateToPetroPoints = _navigateToPetroPoints;

    private MutableLiveData<Event> _navigateToDiscovery = new MutableLiveData<>();
    public LiveData<Event> navigateToDiscovery = _navigateToDiscovery;

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
        _navigateToPetroPoints.postValue(Event.newEvent(true));
    }

    public void navigateToDiscovery() {
        _navigateToDiscovery.postValue(Event.newEvent(true));
    }
}
