package suncor.com.android.ui.main.wallet.cards.details;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import suncor.com.android.data.cards.CardsRepository;
import suncor.com.android.mfp.SessionManager;
import suncor.com.android.model.Resource;
import suncor.com.android.model.account.Profile;
import suncor.com.android.model.cards.CardDetail;
import suncor.com.android.model.cards.CardType;
import suncor.com.android.ui.main.wallet.cards.CardsLoadType;

public class CardDetailsViewModel extends ViewModel {

    private final SessionManager sessionManager;
    private final CardsRepository cardsRepository;
    private MediatorLiveData<List<CardDetail>> _cards = new MediatorLiveData<>();
    LiveData<List<CardDetail>> cards = _cards;
    private CardsLoadType loadType;
    private Set<String> redeemedTicketNumbers;
    private MutableLiveData<Boolean> isCarWashBalanceZero = new MutableLiveData<>();
    private String newlyAddedCardNumber;

    @Inject
    public CardDetailsViewModel(CardsRepository cardsRepository, SessionManager sessionManager) {
        this.cardsRepository = cardsRepository;
        this.sessionManager = sessionManager;
    }

    public void retrieveCards() {
        switch (loadType) {
            case PETRO_POINT_ONLY:
                Profile profile = sessionManager.getProfile();
                if (profile != null && profile.getPetroPointsNumber() != null) {
                    CardDetail petroPointsCard = new CardDetail(CardType.PPTS, profile.getPetroPointsNumber(), profile.getPointsBalance());
                    _cards.setValue(Collections.singletonList(petroPointsCard));
                }
                break;
            case NEWLY_ADD_CARD:
                _cards.addSource(cardsRepository.getCards(false), result -> {
                    _cards.setValue(findNewlyAddedCard(result.data));
                });
                break;
            case REDEEMED_SINGLE_TICKETS:
                _cards.addSource(cardsRepository.getCards(false), result -> {
                    _cards.setValue(findNewlyRedeemedSingleTickets(result.data));
                });
                break;
            case CAR_WASH_PRODUCTS:
                _cards.addSource(cardsRepository.getCards(false), result -> {
                    if (result.status == Resource.Status.SUCCESS) {
                        _cards.setValue(CardsRepository.filterCarWashCards(result.data));
                        updateCarWashBalance(_cards.getValue());
                    }
                });
                break;
            case ALL:
                _cards.addSource(cardsRepository.getCards(false), result -> {
                    if (result.status == Resource.Status.SUCCESS) {
                        _cards.setValue(result.data);
                        updateCarWashBalance(_cards.getValue());
                    }
                });
                break;
        }

    }


    public LiveData<Resource<CardDetail>> deleteCard(CardDetail cardDetail) {
        return cardsRepository.removeCard(cardDetail);
    }

    public void setLoadType(CardsLoadType loadType) {
        this.loadType = loadType;
    }

    public CardsLoadType getLoadType() {
        return loadType;
    }

    public void setRedeemedTicketNumbers(Set<String> redeemedTicketNumbers) {
        this.redeemedTicketNumbers = redeemedTicketNumbers;
    }

    private List<CardDetail> findNewlyRedeemedSingleTickets(List<CardDetail> petroCanadaCards) {
        if (redeemedTicketNumbers != null && redeemedTicketNumbers.size() > 0) {
            List<CardDetail> newlyRedeemedSingleTickets = new ArrayList<>();
            for (CardDetail card : petroCanadaCards) {
                if (card.getCardType() == CardType.ST && redeemedTicketNumbers.contains(card.getTicketNumber()))
                    newlyRedeemedSingleTickets.add(card);
            }
            return newlyRedeemedSingleTickets;
        }
        return petroCanadaCards;
    }

    private List<CardDetail> findNewlyAddedCard(List<CardDetail> petroCanadaCards) {
        for(CardDetail card : petroCanadaCards){
            if(card.getCardType() != CardType.ST && card.getCardNumber().equals(newlyAddedCardNumber)) return Collections.singletonList(card);
        }
        return petroCanadaCards;
    }

    public void setNewlyAddedCardNumber(String newlyAddedCardNumber) {
        this.newlyAddedCardNumber = newlyAddedCardNumber;
    }

    private void updateCarWashBalance(List<CardDetail> cards) {
        boolean isBalanceZero = true;
        for (CardDetail card : cards) {
            if (card.getCardType() == CardType.ST || ((card.getCardType() == CardType.SP || card.getCardType() == CardType.WAG)
                    && card.getBalance() > 0)) isBalanceZero = false;
        }
        this.isCarWashBalanceZero.setValue(isBalanceZero);
    }

    public MutableLiveData<Boolean> getIsCarWashBalanceZero() {
        return isCarWashBalanceZero;
    }


    protected Profile getUserProfile(){
        return sessionManager.getProfile();
    }
}
