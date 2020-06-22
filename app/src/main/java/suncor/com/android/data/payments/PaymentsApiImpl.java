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
import suncor.com.android.model.payments.AddPayment;
import suncor.com.android.model.payments.PaymentDetail;
import suncor.com.android.model.payments.PaymentResponse;
import suncor.com.android.utilities.Timber;

public class PaymentsApiImpl implements PaymentsApi {
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
            URI adapterPath = new URI("/adapters/suncorpayatpump/v1/payatpump/wallet/funding");
            WLResourceRequest request = new WLResourceRequest(adapterPath, WLResourceRequest.GET, SuncorApplication.DEFAULT_TIMEOUT, SuncorApplication.PROTECTED_SCOPE);

            request.send(new WLResponseListener() {
                @Override
                public void onSuccess(WLResponse wlResponse) {
                    String jsonText = wlResponse.getResponseText();
                    Timber.d("Payments API success, response:\n" + jsonText);

                    PaymentResponse.WalletResponse[] wallets = gson.fromJson(jsonText, PaymentResponse.class).getWallet();
                    PaymentResponse.WalletResponse wallet = Arrays.stream(wallets).filter(x -> x.getSource().equals("moneris")).findFirst().orElse(null);

                    if (wallet != null) {
                        result.postValue(Resource.success(new ArrayList<>(Arrays.asList(wallet.getPayments()))));
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
    public LiveData<Resource<AddPayment>> addPayment() {
        Timber.d("Retrieve Add Payment info ");
        MutableLiveData<Resource<AddPayment>> result = new MutableLiveData<>();
        result.postValue(Resource.loading());
        try {
            URI adapterPath = new URI("/adapters/suncorpayatpump/v1/payatpump/wallet/iframeurl");
            WLResourceRequest request = new WLResourceRequest(adapterPath, WLResourceRequest.GET, SuncorApplication.DEFAULT_TIMEOUT, SuncorApplication.PROTECTED_SCOPE);

            request.send(new WLResponseListener() {
                @Override
                public void onSuccess(WLResponse wlResponse) {
                    String jsonText = wlResponse.getResponseText();
                    Timber.d("Get Add Payment API success, response:\n" + jsonText);

                    AddPayment addPayment = gson.fromJson(jsonText, AddPayment.class);
                    result.postValue(Resource.success(addPayment));
                }

                @Override
                public void onFailure(WLFailResponse wlFailResponse) {
                    Timber.d("Get Add Payment API failed, " + wlFailResponse.toString());
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
    public LiveData<Resource<ArrayList<PaymentDetail>>> removePayment(PaymentDetail paymentDetail) {
        Timber.d("Removing payment: " + paymentDetail.getCardNumber());
        MutableLiveData<Resource<ArrayList<PaymentDetail>>> result = new MutableLiveData<>();
        result.postValue(Resource.loading());
        try {
            URI adapterPath = new URI("/adapters/suncorpayatpump/v1/payatpump/wallet/funding/" + paymentDetail.getId());
            WLResourceRequest request = new WLResourceRequest(adapterPath, WLResourceRequest.DELETE, SuncorApplication.DEFAULT_TIMEOUT, SuncorApplication.PROTECTED_SCOPE);

            request.send(new WLResponseListener() {
                @Override
                public void onSuccess(WLResponse wlResponse) {
                    String jsonText = wlResponse.getResponseText();
                    Timber.d("Payments deletion API success, response:\n" + jsonText);

                    PaymentResponse.WalletResponse[] wallets = gson.fromJson(jsonText, PaymentResponse.class).getWallet();
                    PaymentResponse.WalletResponse wallet = Arrays.stream(wallets).filter(x -> x.getSource().equals("moneris")).findFirst().orElse(null);

                    if (wallet != null) {
                        result.postValue(Resource.success(new ArrayList<>(Arrays.asList(wallet.getPayments()))));
                    } else {
                        result.postValue(Resource.success(new ArrayList<>()));
                    }
                }

                @Override
                public void onFailure(WLFailResponse wlFailResponse) {
                    Timber.d("Payment deletion failed due to: " + wlFailResponse.getErrorMsg());
                    Timber.e(wlFailResponse.toString());
                    result.postValue(Resource.error(wlFailResponse.getErrorMsg()));
                }
            });
        } catch (URISyntaxException e) {
            e.printStackTrace();
            result.postValue(Resource.error(e.getMessage()));
            Timber.d("Payment deletion failed due to: " + e.getMessage());
        }

        return result;
    }
}
