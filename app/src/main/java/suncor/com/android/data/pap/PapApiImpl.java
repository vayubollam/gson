package suncor.com.android.data.pap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.worklight.wlclient.api.WLFailResponse;
import com.worklight.wlclient.api.WLResourceRequest;
import com.worklight.wlclient.api.WLResponse;
import com.worklight.wlclient.api.WLResponseListener;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;

import suncor.com.android.SuncorApplication;
import suncor.com.android.data.payments.PaymentsApi;
import suncor.com.android.mfp.ErrorCodes;
import suncor.com.android.model.Resource;
import suncor.com.android.model.pap.ActiveSession;
import suncor.com.android.model.payments.AddPayment;
import suncor.com.android.model.payments.PaymentDetail;
import suncor.com.android.model.payments.PaymentResponse;
import suncor.com.android.utilities.Timber;

public class PapApiImpl implements PapApi {
    private Gson gson;

    public PapApiImpl(Gson gson) {
        this.gson = gson;
    }

    @Override
    public LiveData<Resource<ActiveSession>> activeSession() {
        Timber.d("retrieve active session from backend");
        MutableLiveData<Resource<ActiveSession>> result = new MutableLiveData<>();
        result.postValue(Resource.loading());
        try {
            URI adapterPath = new URI("/adapters/suncorpayatpump/v1/payatpump/fuelup/session");
            WLResourceRequest request = new WLResourceRequest(adapterPath, WLResourceRequest.GET, SuncorApplication.DEFAULT_TIMEOUT, SuncorApplication.PROTECTED_SCOPE);

            request.send(new WLResponseListener() {
                @Override
                public void onSuccess(WLResponse wlResponse) {
                    String jsonText = wlResponse.getResponseText();
                    Timber.d("PAP Active Session API success, response:\n" + jsonText);

                    ActiveSession activeSession = gson.fromJson(jsonText, ActiveSession.class);
                    result.postValue(Resource.success(activeSession));
                }

                @Override
                public void onFailure(WLFailResponse wlFailResponse) {
                    Timber.d("PAP Active Session API failed, " + wlFailResponse.toString());
                    Timber.e(wlFailResponse.toString());
                    result.postValue(Resource.error(wlFailResponse.getErrorMsg()));
                }
            });
        } catch (URISyntaxException e) {
            Timber.e(e.toString());
            result.postValue(Resource.error(ErrorCodes.GENERAL_ERROR));
        }

        return result;
    }

    @Override
    public LiveData<Resource<ActiveSession>> storeDetails(String storeId) {
        Timber.d("retrieve store details from backend");
        MutableLiveData<Resource<ActiveSession>> result = new MutableLiveData<>();
        result.postValue(Resource.loading());
        try {
            URI adapterPath = new URI("/adapters/suncorpayatpump/v1/payatpump/stores");
            WLResourceRequest request = new WLResourceRequest(adapterPath, WLResourceRequest.GET, SuncorApplication.DEFAULT_TIMEOUT, SuncorApplication.PROTECTED_SCOPE);
            request.addHeader("x-store-id", storeId);

            request.send(new WLResponseListener() {
                @Override
                public void onSuccess(WLResponse wlResponse) {
                    String jsonText = wlResponse.getResponseText();
                    Timber.d("PAP store details API success, response:\n" + jsonText);

                    ActiveSession activeSession = gson.fromJson(jsonText, ActiveSession.class);
                    result.postValue(Resource.success(activeSession));
                }

                @Override
                public void onFailure(WLFailResponse wlFailResponse) {
                    Timber.d("PAP store details API failed, " + wlFailResponse.toString());
                    Timber.e(wlFailResponse.toString());
                    result.postValue(Resource.error(wlFailResponse.getErrorMsg()));
                }
            });
        } catch (URISyntaxException e) {
            Timber.e(e.toString());
            result.postValue(Resource.error(ErrorCodes.GENERAL_ERROR));
        }

        return result;
    }
}
