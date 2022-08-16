package suncor.com.android.data.cards;

import androidx.lifecycle.LiveData;

import java.util.ArrayList;

import suncor.com.android.model.Resource;
import suncor.com.android.model.cards.AddCardRequest;
import suncor.com.android.model.cards.CardDetail;
import suncor.com.android.model.station.Station;

public interface CardsApi {
    LiveData<Resource<ArrayList<CardDetail>>> retrieveCards();

    LiveData<Resource<CardDetail>> addCard(AddCardRequest request);

    LiveData<Resource<CardDetail>> removeCard(CardDetail cardDetail);

    LiveData<Resource<CardDetail>> retrieveSPCardDetail(String cardNumber);

    LiveData<Resource<CardDetail>> retrieveWAGCardDetail(String cardNumber);

    LiveData<Resource<Station>> retrieveStoreDetails(String storeId);


}
