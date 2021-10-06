package suncor.com.android.data.pap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.worklight.wlclient.api.WLFailResponse;
import com.worklight.wlclient.api.WLResourceRequest;
import com.worklight.wlclient.api.WLResponse;
import com.worklight.wlclient.api.WLResponseListener;

import java.net.URI;
import java.net.URISyntaxException;

import suncor.com.android.BuildConfig;
import suncor.com.android.SuncorApplication;
import suncor.com.android.mfp.ErrorCodes;
import suncor.com.android.model.Resource;
import suncor.com.android.model.pap.ActiveSession;
import suncor.com.android.model.pap.P97StoreDetailsResponse;
import suncor.com.android.model.pap.PayByGooglePayRequest;
import suncor.com.android.model.pap.PayResponse;
import suncor.com.android.model.pap.PayByWalletRequest;
import suncor.com.android.model.pap.transaction.CancelTransaction;
import suncor.com.android.model.pap.transaction.Transaction;
import suncor.com.android.utilities.Timber;

public class PapApiImpl implements PapApi {
    public final int PAYMENT_TIMEOUT = 45_000;
    private Gson gson;

    public PapApiImpl(Gson gson) {
        this.gson = new GsonBuilder().disableHtmlEscaping().create();
    }

    @Override
    public LiveData<Resource<ActiveSession>> activeSession() {
        Timber.d("retrieve active session from backend");
        MutableLiveData<Resource<ActiveSession>> result = new MutableLiveData<>();
        result.postValue(Resource.loading());
        try {
            URI adapterPath = new URI("/adapters/suncorpayatpump/v1/rfmp-secure/payatpump/fuelup/session");
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
    public LiveData<Resource<P97StoreDetailsResponse>> storeDetails(String storeId) {
        Timber.d("retrieve store details from backend");
        MutableLiveData<Resource<P97StoreDetailsResponse>> result = new MutableLiveData<>();
        result.postValue(Resource.loading());
        try {
            URI adapterPath = new URI("/adapters/suncorpayatpump/v1/rfmp-secure/payatpump/stores");
            WLResourceRequest request = new WLResourceRequest(adapterPath, WLResourceRequest.GET, SuncorApplication.DEFAULT_TIMEOUT, SuncorApplication.PROTECTED_SCOPE);
            request.addHeader("x-store-id", storeId);

            request.send(new WLResponseListener() {
                @Override
                public void onSuccess(WLResponse wlResponse) {
                    String jsonText = wlResponse.getResponseText();
                    Timber.d("PAP store details API success, response:\n" + jsonText);

                    P97StoreDetailsResponse storeDetailsResponse = gson.fromJson(jsonText, P97StoreDetailsResponse.class);
                    result.postValue(Resource.success(storeDetailsResponse));
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

    @Override
    public LiveData<Resource<PayResponse>> authorizePaymentByGooglePay(PayByGooglePayRequest payByGooglePayRequest, LatLng userLocation) {
        Timber.d("request initiate for authorized google pay payment ");
        MutableLiveData<Resource<PayResponse>> result = new MutableLiveData<>();
        result.postValue(Resource.loading());
        try {
            URI adapterPath = new URI("/adapters/suncorpayatpump/v2/rfmp-secure/payatpump/fuelup/PreAuth/PayByGooglePay");
            WLResourceRequest request = new WLResourceRequest(adapterPath, WLResourceRequest.POST, PAYMENT_TIMEOUT, SuncorApplication.PROTECTED_SCOPE);
            request.addHeader("latitude", Double.toString(userLocation.latitude));
            request.addHeader("longitude", Double.toString(userLocation.longitude));
            request.addHeader("deviceOS", "Android");
            request.addHeader("appBundleId", BuildConfig.APPLICATION_ID);
            request.addHeader("appVersionNumber", BuildConfig.VERSION_NAME);

            String body = gson.toJson(payByGooglePayRequest);
            Timber.i("Send Pay By Google Pay, string: " + payByGooglePayRequest.toString() +  "\nbody: " + body);

            request.send(body,  new WLResponseListener() {
                @Override
                public void onSuccess(WLResponse wlResponse) {
                    String jsonText = wlResponse.getResponseText();
                    Timber.d("Google Pay authorized payment success, response:\n" + jsonText);

                    PayResponse payResponse = gson.fromJson(jsonText, PayResponse.class);
                    result.postValue(Resource.success(payResponse));
                }

                @Override
                public void onFailure(WLFailResponse wlFailResponse) {
                    Timber.d("Google Pay authorized payment API failed, " + wlFailResponse.toString());
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
    public LiveData<Resource<PayResponse>> authorizePaymentByWallet(PayByWalletRequest payByWalletRequest, LatLng userLocation) {
        Timber.d("request initiate for authorized wallet payment ");
        MutableLiveData<Resource<PayResponse>> result = new MutableLiveData<>();
        result.postValue(Resource.loading());
        try {
            URI adapterPath = new URI("/adapters/suncorpayatpump/v2/rfmp-secure/payatpump/fuelup/PreAuth/PayByWallet");
            WLResourceRequest request = new WLResourceRequest(adapterPath, WLResourceRequest.POST, PAYMENT_TIMEOUT, SuncorApplication.PROTECTED_SCOPE);
            request.addHeader("latitude", Double.toString(userLocation.latitude));
            request.addHeader("longitude", Double.toString(userLocation.longitude));
            request.addHeader("deviceOS", "Android");
            request.addHeader("appBundleId", BuildConfig.APPLICATION_ID);
            request.addHeader("appVersionNumber", BuildConfig.VERSION_NAME);

            String body = gson.toJson(payByWalletRequest);
            Timber.i("Send Pay By Wallet, string: " + payByWalletRequest.toString() +  "\nbody: " + body);

            request.send(body,  new WLResponseListener() {
                @Override
                public void onSuccess(WLResponse wlResponse) {
                    String jsonText = wlResponse.getResponseText();
                    Timber.d("Wallet authorized payment success, response:\n" + jsonText);

                    PayResponse payResponse = gson.fromJson(jsonText, PayResponse.class);
                    result.postValue(Resource.success(payResponse));
                }

                @Override
                public void onFailure(WLFailResponse wlFailResponse) {
                    Timber.d("Wallet authorized payment API failed, " + wlFailResponse.toString());
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
    public LiveData<Resource<Transaction>> getTransactionDetails(String transactionId, boolean isPartnerTransactionId) {
        Timber.d("request initiate for fetch transaction details ");
        MutableLiveData<Resource<Transaction>> result = new MutableLiveData<>();
        result.postValue(Resource.loading());
        try {
            URI adapterPath = new URI("/adapters/suncorpayatpump/v1/rfmp-secure/payatpump/fuelup/transactionInfo/" + transactionId + "?isPartnerTransactionId=" + isPartnerTransactionId );
            WLResourceRequest request = new WLResourceRequest(adapterPath, WLResourceRequest.GET, SuncorApplication.DEFAULT_TIMEOUT, SuncorApplication.PROTECTED_SCOPE);

            request.send( new WLResponseListener() {
                @Override
                public void onSuccess(WLResponse wlResponse) {
                    String jsonText = wlResponse.getResponseText();
                    Timber.d("Transaction details fetch success, response:\n" + jsonText);
                    Transaction transaction = gson.fromJson(jsonText, Transaction.class);
                    result.postValue(Resource.success(transaction));
                }

                @Override
                public void onFailure(WLFailResponse wlFailResponse) {
                    Timber.d("Transaction details fetch API failed, " + wlFailResponse.toString());
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
    public LiveData<Resource<Boolean>> cancelTransaction(String transactionId) {
        Timber.d("request initiate for cancel transaction");
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        result.postValue(Resource.loading());
        try {
            URI adapterPath = new URI("/adapters/suncorpayatpump/v1/rfmp-secure/payatpump/fuelup/transaction/cancel/" + transactionId);
            WLResourceRequest request = new WLResourceRequest(adapterPath, WLResourceRequest.GET, SuncorApplication.DEFAULT_TIMEOUT, SuncorApplication.PROTECTED_SCOPE);

            request.send( new WLResponseListener() {
                @Override
                public void onSuccess(WLResponse wlResponse) {
                    String jsonText = wlResponse.getResponseText();
                    Timber.d("Cancel transaction success, response:\n" + jsonText);
                    Boolean transaction = gson.fromJson(jsonText, CancelTransaction.class).result.equals("success");
                    result.postValue(Resource.success(transaction));
                }

                @Override
                public void onFailure(WLFailResponse wlFailResponse) {
                    Timber.d("Cancel transaction API failed, " + wlFailResponse.toString());
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
