package suncor.com.android.data.repository.cards;

import java.util.ArrayList;
import java.util.Calendar;

import javax.inject.Inject;
import javax.inject.Singleton;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Transformations;
import suncor.com.android.mfp.SessionManager;
import suncor.com.android.model.Resource;
import suncor.com.android.model.cards.AddCardRequest;
import suncor.com.android.model.cards.CardDetail;

@Singleton
public class CardsRepository {

    public static final String BALANCE_UPDATE_FAILED = "balance_update_failed";

    private CardsApi cardsApi;
    private ArrayList<CardDetail> cachedCards;
    private Calendar timeOfLastUpdate;

    @Inject
    public CardsRepository(CardsApi cardsApi, SessionManager sessionManager) {
        this.cardsApi = cardsApi;
        sessionManager.getLoginState().observeForever((state) -> {
            if (state == SessionManager.LoginState.LOGGED_OUT && cachedCards != null) {
                cachedCards.clear();
            }
        });
    }

    public LiveData<Resource<ArrayList<CardDetail>>> getCards(boolean forceRefresh) {
        MediatorLiveData<Resource<ArrayList<CardDetail>>> result = new MediatorLiveData<>();
        if (!forceRefresh && cachedCards != null && !cachedCards.isEmpty()) {
            result.postValue(Resource.success(cachedCards));
            return result;
        }
        return Transformations.map(cardsApi.retrieveCards(), resource -> {
            if (resource.status == Resource.Status.SUCCESS) {
                if (cachedCards == null) {
                    cachedCards = resource.data;
                    timeOfLastUpdate = Calendar.getInstance();
                    return resource;
                }

                boolean balanceUpdated = false;
                boolean hasPetroCanadaCards = false;
                for (CardDetail newCard : resource.data) {
                    CardDetail oldCard = findCardIn(cachedCards, newCard);
                    if (newCard.getCardCategory() != CardDetail.CardCategory.PETRO_CANADA) {
                        //the balance update is only for petro-canada cards
                        cachedCards.remove(oldCard);
                        cachedCards.add(newCard);
                    } else {
                        hasPetroCanadaCards = true;
                        if (newCard.getBalance() != CardDetail.INVALID_BALANCE) {
                            balanceUpdated = true;
                            cachedCards.remove(oldCard);
                            cachedCards.add(newCard);
                        } else if (oldCard == null) {
                            //if the card has just been added, we will add it even if has no balance
                            cachedCards.add(newCard);
                        }
                    }
                }
                //clearing old cards
                for (int i = cachedCards.size() - 1; i >= 0; i--) {
                    if (findCardIn(resource.data, cachedCards.get(i)) == null) {
                        cachedCards.remove(i);
                    }
                }
                timeOfLastUpdate = Calendar.getInstance();
                if (hasPetroCanadaCards && !balanceUpdated) {
                    return Resource.error(BALANCE_UPDATE_FAILED, cachedCards);
                } else {
                    return Resource.success(cachedCards);
                }
            } else if (resource.status == Resource.Status.ERROR) {
                if (cachedCards != null) {
                    cachedCards.clear();
                }
                return resource;
            } else {
                return resource;
            }
        });
    }

    public LiveData<Resource<CardDetail>> addCard(AddCardRequest cardRequest) {
        return Transformations.map(cardsApi.addCard(cardRequest), result -> {
            if (result.status == Resource.Status.SUCCESS) {
                cachedCards.add(result.data);
            }
            return result;
        });
    }

    public Calendar getTimeOfLastUpdate() {
        return timeOfLastUpdate;
    }

    private static CardDetail findCardIn(ArrayList<CardDetail> cards, CardDetail otherCard) {
        for (CardDetail card : cards) {
            if (card.getCardNumber() != null && card.getCardNumber().equals(otherCard.getCardNumber())) {
                return card;
            } else if (card.getCardNumber() == null && otherCard.getCardNumber() == null && card.getCardType() == otherCard.getCardType()) {
                return card;
            }
        }

        return null;
    }
}
