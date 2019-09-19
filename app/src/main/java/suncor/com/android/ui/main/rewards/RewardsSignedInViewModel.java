package suncor.com.android.ui.main.rewards;

import java.util.ArrayList;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import suncor.com.android.data.redeem.MerchantsRepository;
import suncor.com.android.mfp.SessionManager;
import suncor.com.android.model.Resource;
import suncor.com.android.model.merchants.Merchant;
import suncor.com.android.ui.common.Event;
import suncor.com.android.ui.common.cards.CardFormatUtils;

public class RewardsSignedInViewModel extends ViewModel {

    private MutableLiveData<Event> _navigateToPetroPoints = new MutableLiveData<>();
    public LiveData<Event> navigateToPetroPoints = _navigateToPetroPoints;

    private MutableLiveData<Event> _navigateToDiscovery = new MutableLiveData<>();
    public LiveData<Event> navigateToDiscovery = _navigateToDiscovery;
    public LiveData<Resource<ArrayList<Merchant>>> merchantsLiveData;


    private Reward[] rewards;
    private SessionManager sessionManager;

    @Inject
    public RewardsSignedInViewModel(SessionManager sessionManager, RewardsReader rewardsReader, MerchantsRepository merchantsRepository) {
        rewards = rewardsReader.getRewards();
        this.sessionManager = sessionManager;
        merchantsLiveData = merchantsRepository.getMerchants();
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
