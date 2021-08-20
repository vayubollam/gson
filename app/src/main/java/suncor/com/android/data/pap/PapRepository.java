package suncor.com.android.data.pap;

import android.text.format.DateUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Transformations;

import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;
import java.util.Date;

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
import suncor.com.android.utilities.UserLocalSettings;

@Singleton
public class PapRepository {

    private PapApi papApi;
    private ActiveSession cachedActiveSession;
    private SessionManager sessionManager;

    @Inject
    public PapRepository(PapApi papApi, SessionManager sessionManager) {
        this.papApi = papApi;
        this.sessionManager = sessionManager;

        sessionManager.getLoginState().observeForever((state) -> {
            if (state == SessionManager.LoginState.LOGGED_OUT && cachedActiveSession != null) {
                cachedActiveSession = null;
            }
        });
    }

    public LiveData<Resource<ActiveSession>> getActiveSession() {
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

    public LiveData<Resource<Transaction>> getTransactionDetails(String transactionId, boolean isPartnerTransactionId) {
       return papApi.getTransactionDetails(transactionId, isPartnerTransactionId);
    }

    public LiveData<Resource<Boolean>> cancelTransaction(String transactionId) {
       return papApi.cancelTransaction(transactionId);
    }

    public LiveData<Resource<PayResponse>> authorizePaymentByGooglePay(PayByGooglePayRequest payByGooglePayRequest, LatLng userLocation, String kSessionId) {
        return papApi.authorizePaymentByGooglePay(payByGooglePayRequest, userLocation, kSessionId);
    }

    public LiveData<Resource<PayResponse>> authorizePaymentByWallet(PayByWalletRequest request, LatLng userLocation, String kSessionId) {
        return papApi.authorizePaymentByWallet(request, userLocation, kSessionId);
    }

}
