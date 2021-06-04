package suncor.com.android.data.transcations;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.worklight.wlclient.api.WLFailResponse;
import com.worklight.wlclient.api.WLResourceRequest;
import com.worklight.wlclient.api.WLResponse;
import com.worklight.wlclient.api.WLResponseListener;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;

import suncor.com.android.SuncorApplication;
import suncor.com.android.mfp.ErrorCodes;
import suncor.com.android.model.Resource;
import suncor.com.android.model.cards.Transaction;
import suncor.com.android.utilities.Timber;

public class TransactionApiImpl implements TransactionApi {
    private final static String GET_TRANSACTIONS_ADAPTER_PATH = "/adapters/suncor/v3/rfmp-secure/transactions";
    private Gson gson;

    public TransactionApiImpl(Gson gson) {
        this.gson = gson;
    }

    @Override
    public LiveData<Resource<ArrayList<Transaction>>> getTransactions(String cardId, int startMonth, int monthsBack) {
        Timber.d("Retrieving transactions for card :" + cardId);
        MutableLiveData<Resource<ArrayList<Transaction>>> result = new MutableLiveData<>();
        result.postValue(Resource.loading());

        try {
            URI adapterUri = new URI(GET_TRANSACTIONS_ADAPTER_PATH + "?startMonth=" + startMonth + "&monthsBack=" + monthsBack);
            WLResourceRequest request = new WLResourceRequest(adapterUri, WLResourceRequest.GET, SuncorApplication.DEFAULT_TIMEOUT, SuncorApplication.PROTECTED_SCOPE);
            request.addHeader("x-petro-card-id", cardId);
            request.send(new WLResponseListener() {
                @Override
                public void onSuccess(WLResponse wlResponse) {
                    String jsonText = wlResponse.getResponseText();
                    Timber.d("Transactions Api response :" + jsonText);
                    try {
                        Transaction[] transactions = gson.fromJson(jsonText, Transaction[].class);
                        result.postValue(Resource.success(new ArrayList<>(Arrays.asList(transactions))));
                    } catch (JsonSyntaxException e) {
                        result.postValue(Resource.error(ErrorCodes.GENERAL_ERROR));
                        Timber.d("Transactions Api failed due to :" + e.toString());
                    }
                }

                @Override
                public void onFailure(WLFailResponse wlFailResponse) {
                    result.postValue(Resource.error(wlFailResponse.getErrorMsg()));
                    Timber.d("Transactions Api failed due to :" + wlFailResponse.toString());
                }
            });

        } catch (URISyntaxException e) {
            e.printStackTrace();
            result.postValue(Resource.error(e.getMessage()));
        }

        return result;
    }
}
