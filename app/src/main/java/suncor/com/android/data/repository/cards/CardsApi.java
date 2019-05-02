package suncor.com.android.data.repository.cards;

import java.util.ArrayList;

import androidx.lifecycle.LiveData;
import suncor.com.android.model.Resource;
import suncor.com.android.model.cards.CardDetail;

public interface CardsApi {
    LiveData<Resource<ArrayList<CardDetail>>> retrieveCards();
}
