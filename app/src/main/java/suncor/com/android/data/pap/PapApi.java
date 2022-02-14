package suncor.com.android.data.pap;


import androidx.lifecycle.LiveData;

import com.google.android.gms.maps.model.LatLng;

import suncor.com.android.model.Resource;
import suncor.com.android.model.pap.ActiveSession;
import suncor.com.android.model.pap.GetRedeemableFlag;
import suncor.com.android.model.pap.P97StoreDetailsResponse;
import suncor.com.android.model.pap.PayByGooglePayRequest;
import suncor.com.android.model.pap.PayResponse;
import suncor.com.android.model.pap.PayByWalletRequest;
import suncor.com.android.model.pap.transaction.Transaction;

public interface PapApi {
    LiveData<Resource<ActiveSession>> activeSession();
    LiveData<Resource<P97StoreDetailsResponse>> storeDetails(String storeId);
    LiveData<Resource<PayResponse>> authorizePaymentByGooglePay(PayByGooglePayRequest request, LatLng userLocation);
    LiveData<Resource<PayResponse>> authorizePaymentByWallet(PayByWalletRequest request, LatLng userLocation);
    LiveData<Resource<Transaction>> getTransactionDetails(String transactionId, boolean isPartnerTransactionId);
    LiveData<Resource<Boolean>> cancelTransaction(String transactionId);
    LiveData<Resource<GetRedeemableFlag>> getRedeemableFlag(String stateCode);
}
