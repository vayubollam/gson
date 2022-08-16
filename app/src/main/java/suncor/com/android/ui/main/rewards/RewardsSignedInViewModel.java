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
import suncor.com.android.model.redeem.response.MemberEligibilityResponse;
import suncor.com.android.ui.common.Event;

public class RewardsSignedInViewModel extends ViewModel {

    private final MerchantsRepository repository;
    public LiveData<Resource<ArrayList<Merchant>>> merchantsMutableLiveData;
    private MutableLiveData<Event> _navigateToDiscovery = new MutableLiveData<>();
    public LiveData<Event> navigateToDiscovery = _navigateToDiscovery;
    private MutableLiveData<ArrayList<Merchant>> _merchantsLiveData = new MutableLiveData<>();
    public LiveData<ArrayList<Merchant>> merchantsLiveData = _merchantsLiveData;
    private Reward[] rewards;
    private List<Merchant> merchantList = new ArrayList<>();
    private SessionManager sessionManager;
    private boolean isFirstTym = true;

    public LiveData<Resource<MemberEligibilityResponse>> memberEligibilityResponse;
    private MutableLiveData<MemberEligibilityResponse> _eligibilityLiveData = new MutableLiveData<>();


    @Inject
    public RewardsSignedInViewModel(SessionManager sessionManager, RewardsReader rewardsReader, MerchantsRepository merchantsRepository) {
        rewards = rewardsReader.getRewards();
        this.sessionManager = sessionManager;
        this.repository = merchantsRepository;
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
                if (m.getMerchantId() != MerchantsIds.CARA_FR && m.getMerchantId() != MerchantsIds.CINEPLEX_FR
                        && m.getMerchantId() != MerchantsIds.HUDSON_BAY_FR && m.getMerchantId() != MerchantsIds.WINNERS_HOMESENSE_MARSHALLS_FR && m.getMerchantId() != MerchantsIds.PETRO_CANADA_FR && m.getMerchantId() != MerchantsIds.BEST_BUY_FR && m.getMerchantId() != MerchantsIds.GAP_FR && m.getMerchantId() != MerchantsIds.WALMART_FR && m.getMerchantId() != MerchantsIds.TIM_HORTONS_FR) {
                    merchantIterator.remove();
                }
            } else {
                if (m.getMerchantId() != MerchantsIds.CARA_EN && m.getMerchantId() != MerchantsIds.CINEPLEX_EN
                        && m.getMerchantId() != MerchantsIds.HUDSON_BAY_EN && m.getMerchantId() != MerchantsIds.WINNERS_HOMESENSE_MARSHALLS_EN && m.getMerchantId() != MerchantsIds.PETRO_CANADA_EN && m.getMerchantId() != MerchantsIds.BEST_BUY_EN && m.getMerchantId() != MerchantsIds.GAP_EN && m.getMerchantId() != MerchantsIds.WALMART_EN && m.getMerchantId() != MerchantsIds.TIM_HORTONS_EN) {
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

    public SessionManager getSessionManager() {
        return sessionManager;
    }

    public void navigateToDiscoveryScreen() {
        _navigateToDiscovery.postValue(Event.newEvent(true));
    }

    public LiveData<Resource<MemberEligibilityResponse>> getMemberEligibility() {
        memberEligibilityResponse = repository.getMemberEligibility();
        return memberEligibilityResponse;
    }

    public void updatePetroPoints(int pointsBalance) {
        try {
            sessionManager.getProfile().setPointsBalance(pointsBalance);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public int getPetroPointsBalance() {
        try {
            return sessionManager.getProfile().getPointsBalance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean isDonateEnabled() {
        Boolean donateToggle = sessionManager.getDonateToggle();
        if (donateToggle != null) return donateToggle;
        else return false;
    }
}