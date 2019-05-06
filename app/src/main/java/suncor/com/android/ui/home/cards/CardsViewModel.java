package suncor.com.android.ui.home.cards;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import suncor.com.android.data.repository.cards.CardsRepository;
import suncor.com.android.model.Resource;
import suncor.com.android.model.cards.CardDetail;
import suncor.com.android.model.cards.CardType;
import suncor.com.android.ui.common.Event;


public class CardsViewModel extends ViewModel {

    private final CardsComparator comparator = new CardsComparator();
    private final CardsRepository repository;
    private MediatorLiveData<ViewState> _viewState = new MediatorLiveData<>();
    public LiveData<ViewState> viewState = _viewState;

    private MutableLiveData<PetroPointsCard> petroPointsCard = new MutableLiveData<>();
    private MutableLiveData<List<CardItem>> petroCanadaCards = new MutableLiveData<>();
    private MutableLiveData<List<CardItem>> partnerCards = new MutableLiveData<>();

    private MutableLiveData<Event<Boolean>> retrieveCardsEvent = new MutableLiveData<>();
    private MutableLiveData<Event<Boolean>> refreshCardsEvent = new MutableLiveData<>();
    private MutableLiveData<Calendar> dateOfUpdate = new MutableLiveData<>();
    private ArrayList<CardDetail> cards;

    @Inject
    public CardsViewModel(CardsRepository repository) {
        this.repository = repository;
        _viewState.setValue(ViewState.LOADING);
        MediatorLiveData<Resource<ArrayList<CardDetail>>> apiCall = new MediatorLiveData<>();
        LiveData<Resource<ArrayList<CardDetail>>> retrieveCall = Transformations.switchMap(retrieveCardsEvent, event -> {
            if (event.getContentIfNotHandled() != null) {
                return repository.getCards(false);
            }
            return new MutableLiveData<>();
        });

        LiveData<Resource<ArrayList<CardDetail>>> refreshCall = Transformations.switchMap(refreshCardsEvent, event -> {
            if (event.getContentIfNotHandled() != null) {
                return repository.getCards(true);
            }
            return new MutableLiveData<>();
        });

        apiCall.addSource(retrieveCall, apiCall::setValue);
        apiCall.addSource(refreshCall, apiCall::setValue);

        apiCall.observeForever((result) -> {
            //even in error state, we may get some data
            if (result.data != null) {
                saveCards(result.data);
            }
        });

        _viewState.addSource(apiCall, result -> {
            switch (result.status) {
                case SUCCESS:
                    _viewState.setValue(ViewState.SUCCESS);
                    break;
                case LOADING:
                    if (cards != null && !cards.isEmpty()) {
                        //Which means pull to refresh
                        _viewState.setValue(ViewState.REFRESHING);
                    } else {
                        _viewState.setValue(ViewState.LOADING);
                    }
                    break;
                case ERROR:
                    if (result.data == null) {
                        _viewState.setValue(ViewState.FAILED);
                    } else {
                        _viewState.setValue(ViewState.BALANCE_FAILED);
                    }
                    break;
            }
        });
    }

    public void onAttached() {
        retrieveCardsEvent.setValue(Event.newEvent(true));
    }

    public MutableLiveData<Calendar> getDateOfUpdate() {
        return dateOfUpdate;
    }

    private void saveCards(ArrayList<CardDetail> cards) {
        Collections.sort(cards, comparator);
        this.cards = cards;
        petroPointsCard.postValue(new PetroPointsCard(cards.get(0)));

        ArrayList<CardItem> petroCanadaCardsList = new ArrayList<>();
        for (CardDetail item : cards) {
            if (item.getCardCategory() == CardDetail.CardCategory.PETRO_CANADA) {
                petroCanadaCardsList.add(new CardItem(item));
            }
        }
        petroCanadaCards.setValue(petroCanadaCardsList);

        ArrayList<CardItem> partnerCardsList = new ArrayList<>();
        for (CardDetail item : cards) {
            if (item.getCardCategory() == CardDetail.CardCategory.PARTNER) {
                partnerCardsList.add(new CardItem(item));
            }
        }
        partnerCards.setValue(partnerCardsList);

        dateOfUpdate.setValue(repository.getTimeOfLastUpdate());
    }

    public LiveData<PetroPointsCard> getPetroPointsCard() {
        return petroPointsCard;
    }

    public LiveData<List<CardItem>> getPetroCanadaCards() {
        return petroCanadaCards;
    }

    public LiveData<List<CardItem>> getPartnerCards() {
        return partnerCards;
    }

    public void retryAgain() {
        retrieveCardsEvent.setValue(Event.newEvent(true));
    }

    public void refreshBalance() {
        refreshCardsEvent.setValue(Event.newEvent(true));
    }

    public enum ViewState {
        LOADING, FAILED, SUCCESS, REFRESHING, BALANCE_FAILED
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
            } else if (card1.getCardCategory() == CardDetail.CardCategory.PETRO_CANADA) {
                if (card1.getCardType() != card2.getCardType()) {
                    switch (card1.getCardType()) {
                        case FSR:
                            return -1;
                        case WAG:
                            if (card2.getCardType() == CardType.FSR) {
                                return 1;
                            } else {
                                return -1;
                            }
                        case SP:
                            if (card2.getCardType() == CardType.FSR || card2.getCardType() == CardType.WAG) {
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
}
