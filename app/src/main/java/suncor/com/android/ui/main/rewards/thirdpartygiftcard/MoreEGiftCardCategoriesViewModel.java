package suncor.com.android.ui.main.rewards.thirdpartygiftcard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import suncor.com.android.data.redeem.MerchantsRepository;
import suncor.com.android.mfp.SessionManager;
import suncor.com.android.model.Resource;
import suncor.com.android.model.account.Profile;
import suncor.com.android.model.merchants.Merchant;
import suncor.com.android.model.thirdpartycard.ThirdPartyGiftCardCategory;
import suncor.com.android.ui.common.cards.CardFormatUtils;
import suncor.com.android.ui.main.rewards.RewardsReader;

public class MoreEGiftCardCategoriesViewModel extends ViewModel {

    private Profile profile;
    private RewardsReader rewardsReader;
    private SessionManager sessionManager;
    private MerchantsRepository merchantsRepository;
    public LiveData<Resource<ArrayList<Merchant>>> merchantsMutableLiveData;
    private MutableLiveData<ArrayList<Merchant>> _merchantsLiveData = new MutableLiveData<>();
    public LiveData<ArrayList<Merchant>> merchantsLiveData = _merchantsLiveData;

    @Inject
    public MoreEGiftCardCategoriesViewModel(SessionManager sessionManager, RewardsReader rewardsReader, MerchantsRepository merchantsRepository) {

        this.sessionManager = sessionManager;
        this.rewardsReader = rewardsReader;
        this.profile = sessionManager.getProfile();
        this.merchantsRepository = merchantsRepository;
    }

    public void getMerchantsData(){
        merchantsMutableLiveData = merchantsRepository.getMerchantLiveData();
        merchantsMutableLiveData.observeForever(merchantsResource -> {
            if (merchantsResource.status == Resource.Status.SUCCESS) {
                ArrayList<Merchant> validMerchants = merchantsResource.data;
                _merchantsLiveData.postValue(validMerchants);
            }
        });
    }

    public String getPetroPointsBalance() {
        return CardFormatUtils.formatBalance(sessionManager.getProfile().getPointsBalance());
    }

    public List<ThirdPartyGiftCardCategory> getRewards() {
        return new ArrayList<>(Arrays.asList(rewardsReader.getThirdPartyRawFile()));
    }

}
