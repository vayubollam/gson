package suncor.com.android.data.redeem;


import com.google.gson.Gson;
import com.worklight.wlclient.api.WLFailResponse;
import com.worklight.wlclient.api.WLResourceRequest;
import com.worklight.wlclient.api.WLResponse;
import com.worklight.wlclient.api.WLResponseListener;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import suncor.com.android.SuncorApplication;
import suncor.com.android.mfp.ErrorCodes;
import suncor.com.android.model.Resource;
import suncor.com.android.model.merchants.Merchant;
import suncor.com.android.utilities.Timber;

public class MerchantsApiImpl implements MerchantsApi {
    private final static String ADAPTER_PATH = "/adapters/suncorreloadredeem/v1/merchants";
    private Gson gson;

    public MerchantsApiImpl(Gson gson) {
        this.gson = gson;
    }

    @Override
    public LiveData<Resource<ArrayList<Merchant>>> retrieveMerchants() {
        Timber.d("retrieve merchant from backend");
        MutableLiveData<Resource<ArrayList<Merchant>>> result = new MutableLiveData<>();
        result.postValue(Resource.loading());
        try {
            URI adapterPath = new URI(ADAPTER_PATH);
            WLResourceRequest request = new WLResourceRequest(adapterPath, WLResourceRequest.GET, SuncorApplication.DEFAULT_TIMEOUT, SuncorApplication.DEFAULT_PROTECTED_SCOPE);
            if (Locale.getDefault().getLanguage().equalsIgnoreCase("fr")) {
                request.addHeader("Accept-Language", "fr-CA");
            } else {
                request.addHeader("Accept-Language", "en-CA");
            }
            request.send(new WLResponseListener() {
                @Override
                public void onSuccess(WLResponse wlResponse) {
                    String jsonText = wlResponse.getResponseText();
                    Timber.d("Merchants API success, response:\n" + jsonText);

                    Merchant[] merchants = gson.fromJson(jsonText, Merchant[].class);
                    result.postValue(Resource.success(new ArrayList<>(Arrays.asList(merchants))));
                }

                @Override
                public void onFailure(WLFailResponse wlFailResponse) {
                    Timber.d("Merchants API failed, " + wlFailResponse.toString());
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





