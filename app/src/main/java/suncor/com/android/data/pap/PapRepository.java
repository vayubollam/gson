package suncor.com.android.data.pap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Transformations;

import com.google.android.gms.maps.model.LatLng;

import javax.inject.Inject;
import javax.inject.Singleton;

import suncor.com.android.mfp.SessionManager;
import suncor.com.android.model.Resource;
import suncor.com.android.model.pap.ActiveSession;
import suncor.com.android.model.pap.P97StoreDetailsResponse;
import suncor.com.android.model.pap.PayByGooglePayRequest;
import suncor.com.android.model.pap.PayResponse;
import suncor.com.android.model.pap.PayByWalletRequest;
import suncor.com.android.model.pap.transaction.Transaction;

@Singleton
public class PapRepository {

    private PapApi papApi;
    private ActiveSession cachedActiveSession;

    @Inject
    public PapRepository(PapApi papApi, SessionManager sessionManager) {
        this.papApi = papApi;
        sessionManager.getLoginState().observeForever((state) -> {
            if (state == SessionManager.LoginState.LOGGED_OUT && cachedActiveSession != null) {
                cachedActiveSession = null;
            }
        });
    }

    public LiveData<Resource<ActiveSession>> getActiveSession() {
        MediatorLiveData<Resource<ActiveSession>> result = new MediatorLiveData<>();
        return Transformations.map(papApi.activeSession(), resource -> {
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                cachedActiveSession = resource.data;
                return Resource.success(cachedActiveSession);
            } else if (resource.status == Resource.Status.ERROR) {
                if (cachedActiveSession != null) {
                    cachedActiveSession = null;
                }
                return resource;
            } else if (resource.status == Resource.Status.LOADING) {
                return Resource.loading(cachedActiveSession);
            } else {
                return resource;
            }
        });
    }

    public LiveData<Resource<P97StoreDetailsResponse>> getStoreDetails(String storeId) {
        return papApi.storeDetails(storeId);
    }

    public LiveData<Resource<Transaction>> getTransactionDetails(String trasnsactionId) {
       return papApi.getTransactionDetails(trasnsactionId);
    }

    public LiveData<Resource<Boolean>> cancelTransaction(String trasnsactionId) {
       return papApi.cancelTransaction(trasnsactionId);
    }

    public LiveData<Resource<PayResponse>> authorizePaymentByGooglePay(PayByGooglePayRequest payByGooglePayRequest, LatLng userLocation) {
        return papApi.authorizePaymentByGooglePay(payByGooglePayRequest, userLocation);
    }

    public LiveData<Resource<PayResponse>> authorizePaymentByWallet(PayByWalletRequest request, LatLng userLocation) {
        return papApi.authorizePaymentByWallet(request, userLocation);
    }

}
