package suncor.com.android.ui.main.cards.list;

import java.util.ArrayList;
import java.util.Calendar;
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
import suncor.com.android.ui.common.Event;


public class CardsViewModel extends ViewModel {

    private final CardsRepository repository;
    private MediatorLiveData<ViewState> _viewState = new MediatorLiveData<>();
    public LiveData<ViewState> viewState = _viewState;

    private MutableLiveData<CardDetail> petroPointsCard = new MutableLiveData<>();
    private MutableLiveData<List<CardDetail>> petroCanadaCards = new MutableLiveData<>();
    private MutableLiveData<List<CardDetail>> partnerCards = new MutableLiveData<>();

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
        this.cards = cards;
        petroPointsCard.setValue(cards.get(0));

        ArrayList<CardDetail> petroCanadaCardsList = new ArrayList<>();
        for (CardDetail item : cards) {
            if (item.getCardCategory() == CardDetail.CardCategory.PETRO_CANADA) {
                petroCanadaCardsList.add(item);
            }
        }
        petroCanadaCards.setValue(petroCanadaCardsList);

        ArrayList<CardDetail> partnerCardsList = new ArrayList<>();
        for (CardDetail item : cards) {
            if (item.getCardCategory() == CardDetail.CardCategory.PARTNER) {
                partnerCardsList.add(item);
            }
        }
        partnerCards.setValue(partnerCardsList);

        dateOfUpdate.setValue(repository.getTimeOfLastUpdate());
    }

    public LiveData<CardDetail> getPetroPointsCard() {
        return petroPointsCard;
    }

    public LiveData<List<CardDetail>> getPetroCanadaCards() {
        return petroCanadaCards;
    }

    public LiveData<List<CardDetail>> getPartnerCards() {
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

    public int getIndexofCardDetail(CardDetail cardDetail) {
        return cards.indexOf(cardDetail);
    }
}