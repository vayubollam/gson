package suncor.com.android.ui.main.carwash;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import suncor.com.android.data.cards.CardsRepository;
import suncor.com.android.model.Resource;
import suncor.com.android.model.cards.CardDetail;
import suncor.com.android.model.cards.CardType;
import suncor.com.android.ui.common.Event;

public class CarWashCardViewModel extends ViewModel {

    private final CardsRepository repository;

    private MutableLiveData<ViewState> viewState = new MutableLiveData<>();
    private MutableLiveData<Boolean> isBalanceZero = new MutableLiveData<>();
    private MutableLiveData<Boolean> isCardAvailable = new MutableLiveData<>();
    private MutableLiveData<Event<Boolean>> retrieveCardsEvent = new MutableLiveData<>();
    private MutableLiveData<List<CardDetail>> petroCanadaCards = new MutableLiveData<>();

    @Inject
    public CarWashCardViewModel(CardsRepository repository) {
        this.repository = repository;

        MediatorLiveData<Resource<ArrayList<CardDetail>>> apiCall = new MediatorLiveData<>(); //for future single ticket
        LiveData<Resource<ArrayList<CardDetail>>> retrieveCall = Transformations.switchMap(retrieveCardsEvent, event -> {
            if (event.getContentIfNotHandled() != null) {
                return repository.getCards(true);
            }
            return new MutableLiveData<>();
        });

        apiCall.addSource(retrieveCall, apiCall::setValue);

        apiCall.observeForever((result) -> {
            if (result.status != Resource.Status.LOADING) {
                if (result.data != null) {
                    saveCards(result.data);
                    viewState.setValue(ViewState.SUCCESS);

                } else {
                    viewState.setValue(ViewState.FAILED);
                }
            }
        });

    }

    public void onAttached() {
        loadData();
    }

    public void loadData() {
        viewState.setValue(ViewState.LOADING);
        retrieveCardsEvent.setValue(Event.newEvent(true));
    }

    public MutableLiveData<ViewState> getViewState() {
        return viewState;
    }

    private void saveCards(List<CardDetail> cards) {
        List<CardDetail> carWashCards = filterCarWashCards(cards);
        if (carWashCards.size() == 0) {
            isCardAvailable.setValue(false);
        } else {
            isCardAvailable.setValue(true);
            petroCanadaCards.setValue(carWashCards);
        }
    }

    private List<CardDetail> filterCarWashCards(List<CardDetail> cards) {
        List<CardDetail> carWashCards = new ArrayList<>();
        for (CardDetail card : cards) {
            isBalanceZero.setValue(false);
            if (card.getCardType().equals(CardType.SP)) {
                carWashCards.add(card);
            } else if (card.getCardType().equals(CardType.WAG)) {
                carWashCards.add(card);
                if (card.getBalance() == 0) {
                    isBalanceZero.setValue(true);
                }
            }
        }
        return carWashCards;
    }

    public LiveData<List<CardDetail>> getPetroCanadaCards() {
        return petroCanadaCards;
    }

    public MutableLiveData<Boolean> getIsCardAvailable() {
        return isCardAvailable;
    }

    public MutableLiveData<Boolean> getIsBalanceZero() {
        return isBalanceZero;
    }

    public enum ViewState {
        LOADING, FAILED, SUCCESS
    }
}
