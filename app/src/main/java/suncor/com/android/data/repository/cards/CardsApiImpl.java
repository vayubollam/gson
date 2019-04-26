package suncor.com.android.data.repository.cards;

import com.google.gson.Gson;
import com.worklight.wlclient.api.WLFailResponse;
import com.worklight.wlclient.api.WLResourceRequest;
import com.worklight.wlclient.api.WLResponse;
import com.worklight.wlclient.api.WLResponseListener;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import suncor.com.android.SuncorApplication;
import suncor.com.android.mfp.ErrorCodes;
import suncor.com.android.model.Resource;
import suncor.com.android.model.cards.CardDetail;
import suncor.com.android.utilities.Timber;

public class CardsApiImpl implements CardsApi {
    private final static String ADAPTER_PATH = "/adapters/suncor/v1/cards";
    private Gson gson;

    public CardsApiImpl(Gson gson) {
        this.gson = gson;
    }

    @Override
    public LiveData<Resource<ArrayList<CardDetail>>> retrieveCards() {
        Timber.d("retrieve cards from backend");
        MutableLiveData<Resource<ArrayList<CardDetail>>> result = new MutableLiveData<>();
        result.postValue(Resource.loading());
        try {
            URI adapterPath = new URI(ADAPTER_PATH);
            WLResourceRequest request = new WLResourceRequest(adapterPath, WLResourceRequest.GET, SuncorApplication.DEFAULT_TIMEOUT);
            request.send(new WLResponseListener() {
                @Override
                public void onSuccess(WLResponse wlResponse) {
                    String jsonText = wlResponse.getResponseText();
                    Timber.d("Cards API success, response:\n" + jsonText);

                    CardDetail[] cards = gson.fromJson(jsonText, CardDetail[].class);
                    result.postValue(Resource.success(new ArrayList<>(Arrays.asList(cards))));
                }

                @Override
                public void onFailure(WLFailResponse wlFailResponse) {
                    Timber.d("Cards API failed, " + wlFailResponse.toString());
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
