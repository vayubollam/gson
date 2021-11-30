package suncor.com.android.data.cards;

import android.app.Activity;
import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

import suncor.com.android.model.Resource;
import suncor.com.android.model.cards.AddCardRequest;
import suncor.com.android.model.cards.CardDetail;

public class CardsApiMock implements CardsApi {
    ArrayList<CardDetail> cardDetails;
    private String responseJson = getCardsApiResponse(null);

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

    private String getCardsApiResponse(Context context) {
        String json = null;
        try {
            InputStream is = context.getAssets().open("cardsApiResponse.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
