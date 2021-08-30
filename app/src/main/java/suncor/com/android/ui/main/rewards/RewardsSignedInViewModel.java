package suncor.com.android.ui.main.rewards;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

import javax.inject.Inject;

import suncor.com.android.data.redeem.MerchantsRepository;
import suncor.com.android.mfp.SessionManager;
import suncor.com.android.model.Resource;
import suncor.com.android.model.merchants.Merchant;
import suncor.com.android.ui.common.Event;
import suncor.com.android.ui.common.cards.CardFormatUtils;

public class RewardsSignedInViewModel extends ViewModel {

    private MutableLiveData<Event> _navigateToDiscovery = new MutableLiveData<>();
    public LiveData<Event> navigateToDiscovery = _navigateToDiscovery;

    private MutableLiveData<ArrayList<Merchant>> _merchantsLiveData = new MutableLiveData<>();
    public LiveData<ArrayList<Merchant>> merchantsLiveData = _merchantsLiveData;
    public LiveData<Resource<ArrayList<Merchant>>> merchantsMutableLiveData;


    private Reward[] rewards;
    private SessionManager sessionManager;

    @Inject
    public RewardsSignedInViewModel(SessionManager sessionManager, RewardsReader rewardsReader, MerchantsRepository merchantsRepository) {
        rewards = rewardsReader.getRewards();
        this.sessionManager = sessionManager;
        merchantsMutableLiveData = merchantsRepository.getMerchants();
        merchantsMutableLiveData.observeForever(merchantsResource -> {
            if (merchantsResource.status == Resource.Status.SUCCESS) {
                ArrayList<Merchant> validMerchants = filterValidMerchants(merchantsResource.data);
                _merchantsLiveData.postValue(validMerchants);
            }
        });
    }

    private ArrayList<Merchant> filterValidMerchants(ArrayList<Merchant> merchants) {

        for (Iterator<Merchant> merchantIterator = merchants.iterator(); merchantIterator.hasNext(); ) {
            Merchant m = merchantIterator.next();
            if (Locale.getDefault().getLanguage().equalsIgnoreCase("fr")) {
                if (m.getMerchantId() != MerchantsIds.Cara_FR && m.getMerchantId() != MerchantsIds.Cineplex_FR
                        && m.getMerchantId() != MerchantsIds.Hudson_Bay_FR && m.getMerchantId() != MerchantsIds.WINNERS_HomeSense_Marshalls_FR &&  m.getMerchantId() != MerchantsIds.Petro_Canada_FR) {
                    merchantIterator.remove();
                }
            } else {
                if (m.getMerchantId() != MerchantsIds.Cara_EN && m.getMerchantId() != MerchantsIds.Cineplex_EN
                        && m.getMerchantId() != MerchantsIds.Hudson_Bay_EN && m.getMerchantId() != MerchantsIds.WINNERS_HomeSense_Marshalls_EN && m.getMerchantId() != MerchantsIds.Petro_Canada_EN) {
                    merchantIterator.remove();
                }
            }
        }
        return merchants;
    }

    public Reward[] getRewards() {
        return rewards;
    }

    public String getPetroPoints() {

        if (sessionManager.getProfile() != null)
        {
            return CardFormatUtils.formatBalance(sessionManager.getProfile().getPointsBalance());
        }
        return " ";
    }

    public int getPetroPointsValue() {
        if (sessionManager.getProfile() != null) {
            return sessionManager.getProfile().getPointsBalance() / 1000;
        }
        return 0;
    }

    public void navigateToDiscovery() {
        _navigateToDiscovery.postValue(Event.newEvent(true));
    }
}