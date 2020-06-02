package suncor.com.android.data.payments;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.worklight.wlclient.api.WLFailResponse;
import com.worklight.wlclient.api.WLResourceRequest;
import com.worklight.wlclient.api.WLResponse;
import com.worklight.wlclient.api.WLResponseListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;

import suncor.com.android.SuncorApplication;
import suncor.com.android.data.cards.CardsApi;
import suncor.com.android.mfp.ErrorCodes;
import suncor.com.android.model.Resource;
import suncor.com.android.model.cards.AddCardRequest;
import suncor.com.android.model.cards.CardDetail;
import suncor.com.android.model.payments.PaymentDetail;
import suncor.com.android.model.payments.PaymentResponse;
import suncor.com.android.utilities.Timber;

public class PaymentsApiImpl implements PaymentsApi {
    private final static String ADAPTER_PATH = "/adapters/suncorpayatpump/v1/payatpump/wallet";
    private Gson gson;

    public PaymentsApiImpl(Gson gson) {
        this.gson = gson;
    }

    @Override
    public LiveData<Resource<ArrayList<PaymentDetail>>> retrievePayments() {
        Timber.d("retrieve payments from backend");
        MutableLiveData<Resource<ArrayList<PaymentDetail>>> result = new MutableLiveData<>();
        result.postValue(Resource.loading());
        try {
            URI adapterPath = new URI(ADAPTER_PATH);
            WLResourceRequest request = new WLResourceRequest(adapterPath, WLResourceRequest.GET, SuncorApplication.DEFAULT_TIMEOUT, SuncorApplication.PROTECTED_SCOPE);

            // TODO: Remove, just for mocking
            request.addHeader("X-Mock-Variant", "/v1/payatpump/wallet:success");

            request.send(new WLResponseListener() {
                @Override
                public void onSuccess(WLResponse wlResponse) {
                    String jsonText = wlResponse.getResponseText();
                    Timber.d("Payments API success, response:\n" + jsonText);

                    PaymentResponse.WalletResponse[] wallet = gson.fromJson(jsonText, PaymentResponse.class).getWallet();

                    if (wallet.length == 1) {
                        PaymentDetail[] payments = wallet[0].getPayments();
                        result.postValue(Resource.success(new ArrayList<>(Arrays.asList(payments))));
                    } else {
                        result.postValue(Resource.success(new ArrayList<>()));
                    }
                }

                @Override
                public void onFailure(WLFailResponse wlFailResponse) {
                    Timber.d("Payments API failed, " + wlFailResponse.toString());
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
    public LiveData<Resource<PaymentDetail>> addPayment() {
        // TODO: Implement
        return null;
    }

    @Override
    public LiveData<Resource<PaymentDetail>> removePayment(PaymentDetail paymentDetail) {
        // TODO: Implement
        return null;
    }
}
