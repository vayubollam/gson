package suncor.com.android.data.pap;


import androidx.lifecycle.LiveData;

import suncor.com.android.model.Resource;
import suncor.com.android.model.pap.ActiveSession;
import suncor.com.android.model.pap.P97StoreDetailsResponse;
import suncor.com.android.model.pap.PayByGooglePayRequest;
import suncor.com.android.model.pap.PayByGooglePayResponse;

public interface PapApi {
    LiveData<Resource<ActiveSession>> activeSession();
    LiveData<Resource<P97StoreDetailsResponse>> storeDetails(String storeId);
    LiveData<Resource<PayByGooglePayResponse>> authorizedPaymentByGooglePay(PayByGooglePayRequest request);
}
