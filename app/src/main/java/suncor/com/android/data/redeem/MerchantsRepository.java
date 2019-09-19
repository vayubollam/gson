package suncor.com.android.data.redeem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.inject.Inject;
import javax.inject.Singleton;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Transformations;
import suncor.com.android.mfp.SessionManager;
import suncor.com.android.model.Resource;
import suncor.com.android.model.merchants.Merchant;
import suncor.com.android.utilities.Timber;

@Singleton
public class MerchantsRepository {
    private MerchantsApi merchantsApi;
    private ArrayList<Merchant> cachedMerchants;
    private MerchantsComparator merchantsComparator = new MerchantsComparator();

    @Inject
    public MerchantsRepository(MerchantsApi merchantsApi, SessionManager sessionManager) {
        this.merchantsApi = merchantsApi;
        sessionManager.getLoginState().observeForever((state) -> {
            if (state == SessionManager.LoginState.LOGGED_OUT && cachedMerchants != null) {
                cachedMerchants.clear();
            }
        });

    }

    public LiveData<Resource<ArrayList<Merchant>>> getMerchants() {
        MediatorLiveData<Resource<ArrayList<Merchant>>> result = new MediatorLiveData<>();
        if (cachedMerchants != null && !cachedMerchants.isEmpty()) {
            result.postValue(Resource.success(cachedMerchants));
            return result;
        }
        return Transformations.map(merchantsApi.retrieveMerchants(), resource -> {
            if (resource.status == Resource.Status.SUCCESS) {
                if (cachedMerchants == null) {
                    Collections.sort(resource.data, merchantsComparator);
                    cachedMerchants = resource.data;
                    Timber.d("merchants retrieved and cached");
                    return resource;
                }
                Collections.sort(cachedMerchants, merchantsComparator);
                return Resource.success(cachedMerchants);

            } else if (resource.status == Resource.Status.ERROR) {
                if (cachedMerchants != null) {
                    cachedMerchants.clear();
                }
                return resource;
            } else {
                return resource;
            }
        });
    }

    private class MerchantsComparator implements Comparator<Merchant> {
        @Override
        public int compare(Merchant merchant1, Merchant merchant2) {
            return merchant1.getDisplayOrder() - merchant2.getDisplayOrder();
        }
    }
}
