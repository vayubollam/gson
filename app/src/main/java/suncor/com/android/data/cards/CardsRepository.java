package suncor.com.android.data.cards;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Transformations;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import suncor.com.android.mfp.SessionManager;
import suncor.com.android.model.Resource;
import suncor.com.android.model.cards.AddCardRequest;
import suncor.com.android.model.cards.CardDetail;
import suncor.com.android.model.cards.CardType;

@Singleton
public class CardsRepository {

    public static final String BALANCE_UPDATE_FAILED = "balance_update_failed";

    private CardsApi cardsApi;
    private ArrayList<CardDetail> cachedCards;
    private Calendar timeOfLastUpdate;

    private CardsComparator cardsComparator = new CardsComparator();

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
                if (cachedCards == null || cachedCards.size() == 0) {
                    Collections.sort(resource.data, cardsComparator);
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

                Collections.sort(cachedCards, cardsComparator);

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
                Collections.sort(cachedCards, cardsComparator);
            }
            return result;
        });
    }

    public LiveData<Resource<CardDetail>> removeCard(CardDetail cardDetail) {
        return Transformations.map(cardsApi.removeCard(cardDetail), result -> {
            if (result.status == Resource.Status.SUCCESS) {
                cachedCards.remove(result.data);
            }
            return result;
        });
    }

    public LiveData<Resource<CardDetail>> getSPCardDetails(String cardNumber){
        return Transformations.map(cardsApi.retrieveSPCardDetail(cardNumber), result -> {
            if (result.status == Resource.Status.SUCCESS) {
                for (int i = cachedCards.size() - 1; i >= 0; i--) {
                    if (result.data.getCardNumber()==cachedCards.get(i).getCardNumber()) {
                         cachedCards.add(result.data);
                    }
                }
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
            }else if(card.getTicketNumber() != null && card.getTicketNumber().equals(otherCard.getTicketNumber())){
                return card;
            } else if (card.getCardNumber() == null && otherCard.getCardNumber() == null && card.getCardType() == otherCard.getCardType()) {
                return card;
            }
        }

        return null;
    }

    private class CardsComparator implements Comparator<CardDetail> {

        @Override
        public int compare(CardDetail card1, CardDetail card2) {
            if (card1.getCardCategory() != card2.getCardCategory()) {
                switch (card1.getCardCategory()) {
                    case PPTS:
                        return -1;
                    case PETRO_CANADA:
                        if (card2.getCardCategory() == CardDetail.CardCategory.PPTS) {
                            return 1;
                        } else {
                            return -1;
                        }
                    case PARTNER:
                        return 1;

                }
            } else if (card1.getCardType() == CardType.FSR && card2.getCardType() == CardType.FSR) {
                if (card1.getCpl() == card2.getCpl()) {
                    return ((card2.getBalance() - card1.getBalance()) * 10);
                } else {
                    return (int) ((card1.getCpl() - card2.getCpl()) * 10);
                }
            }else if (card1.getCardType() == CardType.ST && card2.getCardType() == CardType.ST){
                return 0;
            } else if (card1.getCardType() == CardType.SP && card2.getCardType() == CardType.SP) {
                return ((card2.getBalance() - card1.getBalance()) * 10);
            } else if (card1.getCardType() == CardType.WAG && card2.getCardType() == CardType.WAG) {
                return ((card2.getBalance() - card1.getBalance()) * 10);
            } else if (card1.getCardType() == CardType.PPC && card2.getCardType() == CardType.PPC) {
                return ((card2.getBalance() - card1.getBalance()) * 10);
            } else if (card1.getCardCategory() == CardDetail.CardCategory.PETRO_CANADA) {
                if (card1.getCardType() != card2.getCardType()) {
                    switch (card1.getCardType()) {
                        case FSR:
                            return -1;
                        case ST:
                            if (card2.getCardType() == CardType.FSR) {
                                return 1;
                            } else {
                                return -1;
                            }
                        case WAG:
                            if (card2.getCardType() == CardType.FSR || card2.getCardType() == CardType.ST) {
                                return 1;
                            } else {
                                return -1;
                            }
                        case SP:
                            if (card2.getCardType() == CardType.FSR || card2.getCardType() == CardType.WAG || card2.getCardType() == CardType.ST) {
                                return 1;
                            } else {
                                return -1;
                            }
                        case PPC:
                            return 1;

                    }
                } else {
                    if (card1.getCardType() == CardType.FSR || card1.getCardType() == CardType.PPC) {
                        return (int) ((card1.getCpl() - card2.getCpl()) * 10);
                    }
                }
            } else if (card1.getCardCategory() == CardDetail.CardCategory.PARTNER) {
                if (card1.getCardType() != card2.getCardType()) {
                    CardType card2Type = card2.getCardType();
                    switch (card1.getCardType()) {
                        case RBC:
                            return -1;
                        case MORE:
                            return card2Type == CardType.RBC ? 1 : -1;
                        case HBC:
                            return card2Type == CardType.RBC || card2Type == CardType.MORE ? 1 : -1;
                        case CAA:
                        case BCAA:
                            return 1;
                    }
                } else {
                    return 0;
                }
            }

            //which means card1==card2 and is ppts
            return 0;
        }
    }

    public static List<CardDetail> filterCarWashCards(List<CardDetail> cards) {
        ArrayList<CardDetail> carWashCards = new ArrayList<>();
        for (CardDetail card : cards) {
            if (card.getCardType().equals(CardType.SP) || card.getCardType().equals(CardType.WAG) || card.getCardType().equals(CardType.ST)) {
                carWashCards.add(card);
            }
        }
        return carWashCards;
    }
}
