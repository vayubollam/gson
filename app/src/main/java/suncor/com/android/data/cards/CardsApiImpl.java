package suncor.com.android.data.cards;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.kount.api.analytics.AnalyticsCollector;
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
import java.util.Objects;

import suncor.com.android.SuncorApplication;
import suncor.com.android.mfp.ErrorCodes;
import suncor.com.android.model.Resource;
import suncor.com.android.model.cards.AddCardRequest;
import suncor.com.android.model.cards.CardDetail;
import suncor.com.android.model.station.Station;
import suncor.com.android.utilities.Timber;

public class CardsApiImpl implements CardsApi {
    private final static String GET_CARDS_ADAPTER_PATH = "/adapters/suncor/v6/rfmp-secure/cards";
    private final static String ADD_CARDS_ADAPTER_PATH = "/adapters/suncor/v4/rfmp-secure/cards";
    private final static String DELETE_CARDS_ADAPTER_PATH = "/adapters/suncor/v4/rfmp-secure/cards";
    private final static String GET_SP_CARD_DETAIL_ADAPTER_PATH = "/adapters/suncor/v6/rfmp-secure/cards/SP/";
   private final static String GET_STORE_DETAIL_ADAPTER_PATH = "adapters/suncor/v3/rfmp-secure/locationId";

    public final int ADD_CARD_TIMEOUT = 60_000;

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
            URI adapterPath = new URI(GET_CARDS_ADAPTER_PATH);
            WLResourceRequest request = new WLResourceRequest(adapterPath, WLResourceRequest.GET, SuncorApplication.DEFAULT_TIMEOUT, SuncorApplication.PROTECTED_SCOPE);
            request.send(new WLResponseListener() {
                @Override
                public void onSuccess(WLResponse wlResponse) {
                    String jsonText = wlResponse.getResponseText();
                    Timber.d("Cards API success, response:\n" + jsonText);

                    CardDetail[] cards = gson.fromJson(jsonText, CardDetail[].class);
                    if(Objects.nonNull(cards)){
                        result.postValue(Resource.success(new ArrayList<>(Arrays.asList(cards))));
                    } else {
                        result.postValue(Resource.success(new ArrayList<>()));
                    }
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


    @Override
    public LiveData<Resource<CardDetail>> addCard(AddCardRequest cardRequest) {
        Timber.d("Add Card: " + cardRequest.getCardNumber());
        MutableLiveData<Resource<CardDetail>> result = new MutableLiveData<>();
        result.postValue(Resource.loading());
        try {
            URI adapterPath = new URI(ADD_CARDS_ADAPTER_PATH);
            WLResourceRequest request = new WLResourceRequest(adapterPath, WLResourceRequest.POST, ADD_CARD_TIMEOUT, SuncorApplication.PROTECTED_SCOPE);
            JSONObject body = new JSONObject(gson.toJson(cardRequest));
            request.send(body, new WLResponseListener() {
                @Override
                public void onSuccess(WLResponse wlResponse) {
                    String jsonText = wlResponse.getResponseText();
                    Timber.d("Post cards API success, response:\n" + jsonText);

                    CardDetail card = gson.fromJson(jsonText, CardDetail.class);
                    result.postValue(Resource.success(card));
                }

                @Override
                public void onFailure(WLFailResponse wlFailResponse) {
                    Timber.d("Post cards API failed, " + wlFailResponse.toString());
                    Timber.e(wlFailResponse.toString());
                    result.postValue(Resource.error(wlFailResponse.getErrorMsg()));
                }
            });
        } catch (URISyntaxException e) {
            Timber.e(e.toString());
            result.postValue(Resource.error(ErrorCodes.GENERAL_ERROR));
        } catch (JSONException e) {
            e.printStackTrace();
            result.postValue(Resource.error(ErrorCodes.GENERAL_ERROR));
        }

        return result;
    }

    @Override
    public LiveData<Resource<CardDetail>> removeCard(CardDetail cardDetail) {
        Timber.d("Removing card: " + cardDetail.getCardNumber());
        MutableLiveData<Resource<CardDetail>> result = new MutableLiveData<>();
        result.postValue(Resource.loading());
        try {
            URI adapterPath = new URI(DELETE_CARDS_ADAPTER_PATH);
            WLResourceRequest request = new WLResourceRequest(adapterPath, WLResourceRequest.DELETE, SuncorApplication.DEFAULT_TIMEOUT, SuncorApplication.PROTECTED_SCOPE);
            request.addHeader("x-card-number", cardDetail.getCardNumber());
            request.addHeader("x-service-id", cardDetail.getServiceId());
            request.send(new WLResponseListener() {
                @Override
                public void onSuccess(WLResponse wlResponse) {
                    result.postValue(Resource.success(cardDetail));
                    Timber.d("card was deleted: " + cardDetail.getCardNumber());
                }

                @Override
                public void onFailure(WLFailResponse wlFailResponse) {
                    Timber.d("card deletion failed due to: " + wlFailResponse.getErrorMsg());
                    result.postValue(Resource.error(wlFailResponse.getErrorMsg()));
                }
            });
        } catch (URISyntaxException e) {
            e.printStackTrace();
            result.postValue(Resource.error(e.getMessage()));
            Timber.d("card deletion failed due to: " + e.getMessage());
        }

        return result;
    }


    @Override
    public LiveData<Resource<CardDetail>> retrieveSPCardDetail(String cardNumber) {
        Timber.d("fetching SP card details: " + cardNumber);
        MutableLiveData<Resource<CardDetail>> result = new MutableLiveData<>();
        result.postValue(Resource.loading());
        try {
            URI adapterPath = new URI(GET_SP_CARD_DETAIL_ADAPTER_PATH+cardNumber);
            Timber.d("URI PATH : " + adapterPath);
            WLResourceRequest request = new WLResourceRequest(adapterPath, WLResourceRequest.GET, SuncorApplication.DEFAULT_TIMEOUT, SuncorApplication.PROTECTED_SCOPE);
            request.send(new WLResponseListener() {
                @Override
                public void onSuccess(WLResponse wlResponse) {
                    String jsonText = wlResponse.getResponseText();
                    Timber.d("SP Card API success, response:\n" + jsonText);

                    CardDetail cards = gson.fromJson(jsonText, CardDetail.class);
                    if(Objects.nonNull(cards)){
                        result.postValue(Resource.success(cards));
                    }
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

    @Override
    public LiveData<Resource<Station>> retrieveStoreDetails(String storeId) {
        Timber.d("fetching Store details: "+storeId);
        MutableLiveData<Resource<Station>> result = new MutableLiveData<>();
        result.postValue(Resource.loading());
        try {
            URI adapterPath = new URI(GET_STORE_DETAIL_ADAPTER_PATH);
            Timber.d("URI PATH : " + adapterPath);
            WLResourceRequest request = new WLResourceRequest(adapterPath, WLResourceRequest.GET, SuncorApplication.DEFAULT_TIMEOUT, SuncorApplication.PROTECTED_SCOPE);
            request.addHeader("x-store-id",storeId);
            request.send(new WLResponseListener() {
                @Override
                public void onSuccess(WLResponse wlResponse) {
                    String jsonText = wlResponse.getResponseText();
                    Timber.d("Store success, response:\n" + jsonText);

                   Station address = gson.fromJson(jsonText, Station.class);
                    if(Objects.nonNull(address)){
                        result.postValue(Resource.success(address));
                    }
                }

                @Override
                public void onFailure(WLFailResponse wlFailResponse) {
                    Timber.d("Store API failed, " + wlFailResponse.toString());
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
