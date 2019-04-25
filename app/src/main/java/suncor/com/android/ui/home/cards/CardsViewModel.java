package suncor.com.android.ui.home.cards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import suncor.com.android.data.repository.cards.CardsApiMock;
import suncor.com.android.model.Resource;
import suncor.com.android.model.cards.CardDetail;

public class CardsViewModel extends ViewModel {

    public LiveData<Resource<ArrayList<CardDetail>>> cardsLiveData;
    private MutableLiveData<PetroPointsCard> petroPointsCard = new MutableLiveData<>();

    @Inject
    public CardsViewModel() {
        cardsLiveData = new CardsApiMock().retrieveCards();
        cardsLiveData.observeForever((result) -> {
            if (result.status == Resource.Status.SUCCESS) {
                petroPointsCard.postValue(new PetroPointsCard(result.data.get(0)));
            }
        });
    }

    public LiveData<PetroPointsCard> getPetroPointsCard() {
        return petroPointsCard;
    }

    public List<CardItem> getPetroCanadaCards() {
        if (cardsLiveData.getValue() == null || cardsLiveData.getValue().status != Resource.Status.SUCCESS) {
            return Collections.emptyList();
        }
        ArrayList<CardItem> petroCanadaCards = new ArrayList<>();
        for (CardDetail item : cardsLiveData.getValue().data) {
            if (item.getCardCategory() == CardDetail.CardCategory.PETRO_CANADA) {
                petroCanadaCards.add(new CardItem(item));
            }
        }
        return petroCanadaCards;
    }

    public List<CardItem> getPartnerCards() {
        if (cardsLiveData.getValue() == null || cardsLiveData.getValue().status != Resource.Status.SUCCESS) {
            return Collections.emptyList();
        }
        ArrayList<CardItem> partnerCards = new ArrayList<>();
        for (CardDetail item : cardsLiveData.getValue().data) {
            if (item.getCardCategory() == CardDetail.CardCategory.PARTNER) {
                partnerCards.add(new CardItem(item));
            }
        }
        return partnerCards;
    }
}
