package suncor.com.android.ui.main.rewards;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import suncor.com.android.data.redeem.MerchantsRepository;
import suncor.com.android.mfp.SessionManager;
import suncor.com.android.model.Resource;
import suncor.com.android.model.merchants.Merchant;
import suncor.com.android.ui.common.Event;
import suncor.com.android.ui.common.cards.CardFormatUtils;

public class RewardsSignedInViewModel extends ViewModel {

    public LiveData<Resource<ArrayList<Merchant>>> merchantsMutableLiveData;
    private MutableLiveData<Event> _navigateToDiscovery = new MutableLiveData<>();
    public LiveData<Event> navigateToDiscovery = _navigateToDiscovery;
    private MutableLiveData<ArrayList<Merchant>> _merchantsLiveData = new MutableLiveData<>();
    public LiveData<ArrayList<Merchant>> merchantsLiveData = _merchantsLiveData;
    private Reward[] rewards;
    private List<Merchant> merchantList = new ArrayList<>();
    private SessionManager sessionManager;
    private boolean isFirstTym = true;

    @Inject
    public RewardsSignedInViewModel(SessionManager sessionManager, RewardsReader rewardsReader, MerchantsRepository merchantsRepository) {
        rewards = rewardsReader.getRewards();
        this.sessionManager = sessionManager;
        merchantsMutableLiveData = merchantsRepository.getMerchants();
        merchantsMutableLiveData.observeForever(merchantsResource -> {
            if (merchantsResource.status == Resource.Status.SUCCESS) {
                merchantList = merchantsResource.data;
                assert merchantsResource.data != null;
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
                        && m.getMerchantId() != MerchantsIds.Hudson_Bay_FR && m.getMerchantId() != MerchantsIds.WINNERS_HomeSense_Marshalls_FR && m.getMerchantId() != MerchantsIds.Petro_Canada_FR && m.getMerchantId() != MerchantsIds.Best_Buy_FR && m.getMerchantId() != MerchantsIds.GAP_FR && m.getMerchantId() != MerchantsIds.Walmart_FR && m.getMerchantId() != MerchantsIds.Tim_hortons_FR) {
                    merchantIterator.remove();
                }
            } else {
                if (m.getMerchantId() != MerchantsIds.Cara_EN && m.getMerchantId() != MerchantsIds.Cineplex_EN
                        && m.getMerchantId() != MerchantsIds.Hudson_Bay_EN && m.getMerchantId() != MerchantsIds.WINNERS_HomeSense_Marshalls_EN && m.getMerchantId() != MerchantsIds.Petro_Canada_EN && m.getMerchantId() != MerchantsIds.Best_Buy_EN && m.getMerchantId() != MerchantsIds.GAP_EN && m.getMerchantId() != MerchantsIds.Walmart_EN && m.getMerchantId() != MerchantsIds.Tim_hortons_EN) {
                    merchantIterator.remove();
                }
            }
        }
        return merchants;
    }

    public List<Merchant> getMerchantList() {

        return merchantList;
    }

    public Reward[] getRewards() {
        return rewards;

    }

    public String getPetroPoints() {

        if (sessionManager.getProfile() != null) {
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