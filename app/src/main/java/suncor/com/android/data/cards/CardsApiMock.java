package suncor.com.android.data.cards;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;

import suncor.com.android.model.Resource;
import suncor.com.android.model.cards.AddCardRequest;
import suncor.com.android.model.cards.CardDetail;
import suncor.com.android.model.station.Station;

import static suncor.com.android.utilities.CommonUtils.getMockResponse;

public class CardsApiMock implements CardsApi {
    ArrayList<CardDetail> cardDetails;

    private String responseJson = getMockResponse(null, "cardsApiResponse.json");
    private String responseWag = getMockResponse(null, "wagDetails.json");
    private String responseSp = getMockResponse(null, "spDetails.json");
    private String locationResponse = getMockResponse(null, "storeDetails.json");

    @Override
    public LiveData<Resource<ArrayList<CardDetail>>> retrieveCards() {
        MutableLiveData<Resource<ArrayList<CardDetail>>> result = new MutableLiveData<>();
        result.postValue(Resource.loading());
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(2000);
                ArrayList<CardDetail> cards = new ArrayList<>();
                Gson gson = new Gson();
                cards.addAll(Arrays.asList(gson.fromJson(responseJson, CardDetail[].class)));
                cardDetails = cards;
                result.postValue(Resource.success(cards));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        thread.start();
        return result;
    }

    @Override
    public LiveData<Resource<CardDetail>> addCard(AddCardRequest request) {
        MutableLiveData<Resource<CardDetail>> result = new MutableLiveData<>();
        result.postValue(Resource.loading());
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(2000);
                ArrayList<CardDetail> cards = new ArrayList<>();
                Gson gson = new Gson();
                cards.addAll(Arrays.asList(gson.fromJson(responseJson, CardDetail[].class)));
                result.postValue(Resource.success(cards.get(1)));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        thread.start();
        return result;
    }

    @Override
    public LiveData<Resource<CardDetail>> removeCard(CardDetail cardDetail) {
        MutableLiveData<Resource<CardDetail>> result = new MutableLiveData<>();
        result.postValue(Resource.loading());

        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(2000);
                cardDetails.remove(cardDetail);
                result.postValue(Resource.success(cardDetail));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        thread.start();
        return result;
    }

    @Override
    public LiveData<Resource<CardDetail>> retrieveSPCardDetail(String cardNumber) {
        MutableLiveData<Resource<CardDetail>> result = new MutableLiveData<>();
        result.postValue(Resource.loading());

        Gson gson = new Gson();
        CardDetail carddetail=gson.fromJson(responseSp, CardDetail.class);
        result.postValue(Resource.success(carddetail));
        return result;
    }

    @Override
    public LiveData<Resource<CardDetail>> retrieveWAGCardDetail(String cardNumber) {
        MutableLiveData<Resource<CardDetail>> result = new MutableLiveData<>();
        result.postValue(Resource.loading());

        Gson gson = new Gson();
        CardDetail carddetail=gson.fromJson(responseWag, CardDetail.class);
        result.postValue(Resource.success(carddetail));
        return result;
    }

    @Override
    public LiveData<Resource<Station>> retrieveStoreDetails(String storeId) {
        MutableLiveData<Resource<Station>> result = new MutableLiveData<>();
        result.postValue(Resource.loading());

        Gson gson = new Gson();
        Station location=gson.fromJson(locationResponse, Station.class);
        result.postValue(Resource.success(location));
        return result;
    }
}
