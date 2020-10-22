package suncor.com.android.data.pap;


import androidx.lifecycle.LiveData;

import suncor.com.android.model.Resource;
import suncor.com.android.model.pap.ActiveSession;
import suncor.com.android.model.pap.P97StoreDetailsResponse;
import suncor.com.android.model.pap.PayByGooglePayRequest;
import suncor.com.android.model.pap.PayResponse;
import suncor.com.android.model.pap.PayByWalletRequest;
import suncor.com.android.model.pap.transaction.Transaction;

public interface PapApi {
    LiveData<Resource<ActiveSession>> activeSession();
    LiveData<Resource<P97StoreDetailsResponse>> storeDetails(String storeId);
    LiveData<Resource<PayResponse>> authorizePaymentByGooglePay(PayByGooglePayRequest request);
    LiveData<Resource<PayResponse>> authorizePaymentByWallet(PayByWalletRequest request);
    LiveData<Resource<Transaction>> getTransactionDetails(String transactionId);
    LiveData<Resource<Boolean>> cancelTransaction(String transactionId);
}
