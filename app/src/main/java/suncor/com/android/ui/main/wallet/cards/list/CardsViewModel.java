package suncor.com.android.ui.main.wallet.cards.list;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import suncor.com.android.data.cards.CardsRepository;
import suncor.com.android.mfp.SessionManager;
import suncor.com.android.model.Resource;
import suncor.com.android.model.account.Profile;
import suncor.com.android.model.cards.CardDetail;
import suncor.com.android.model.cards.CardType;
import suncor.com.android.ui.common.Event;


public class CardsViewModel extends ViewModel {

    private final CardsRepository repository;
    private final Profile profile;
    private MediatorLiveData<ViewState> _viewState = new MediatorLiveData<>();
    public LiveData<ViewState> viewState = _viewState;

    private ViewState pendingViewState;

    private MutableLiveData<CardDetail> petroPointsCard = new MutableLiveData<>();
    private MutableLiveData<List<CardDetail>> petroCanadaCards = new MutableLiveData<>();
    private MutableLiveData<List<CardDetail>> partnerCards = new MutableLiveData<>();

    private MutableLiveData<Event<Boolean>> retrieveCardsEvent = new MutableLiveData<>();
    private MutableLiveData<Event<Boolean>> refreshCardsEvent = new MutableLiveData<>();
    private MutableLiveData<Calendar> dateOfUpdate = new MutableLiveData<>();
    private ArrayList<CardDetail> cards;

    private MutableLiveData<Boolean> isBalanceZero = new MutableLiveData<>();
    private boolean carWashreloadStatus;

    @Inject
    public CardsViewModel(CardsRepository repository, SessionManager sessionManager) {
        this.repository = repository;

        profile = sessionManager.getProfile();
        cards = new ArrayList<>();
        carWashreloadStatus = sessionManager.getCarWashToggle() == null ? false : sessionManager.getCarWashToggle();

        MediatorLiveData<Resource<ArrayList<CardDetail>>> apiCall = new MediatorLiveData<>();
        LiveData<Resource<ArrayList<CardDetail>>> retrieveCall = Transformations.switchMap(retrieveCardsEvent, event -> {
            if (event.getContentIfNotHandled() != null) {
                pendingViewState = ViewState.LOADING;
                return repository.getCards(true);
            }
            return new MutableLiveData<>();
        });

        LiveData<Resource<ArrayList<CardDetail>>> refreshCall = Transformations.switchMap(refreshCardsEvent, event -> {
            if (event.getContentIfNotHandled() != null) {
                pendingViewState = ViewState.REFRESHING;
                return repository.getCards(true);
            }
            return new MutableLiveData<>();
        });

        apiCall.addSource(retrieveCall, apiCall::setValue);
        apiCall.addSource(refreshCall, apiCall::setValue);

        apiCall.observeForever((result) -> {
            if (result.status != Resource.Status.LOADING) {
                //even in error state, we may get some data
                if (result.data != null) {
                    for (CardDetail newCard : result.data) {
                        if (newCard.getCardType() == CardType.PPTS && profile != null) {
                            profile.setPointsBalance(newCard.getBalance());
                        }
                    }
                    saveCards(result.data);
                } else {
                    loadBalanceFromProfile();
                }
            }
        });

        _viewState.addSource(apiCall, result -> {
            switch (result.status) {
                case SUCCESS:
                    _viewState.setValue(ViewState.SUCCESS);
                    break;
                case LOADING:
                    _viewState.setValue(pendingViewState);
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

    private void loadBalanceFromProfile() {
        if (profile != null) {
            if (profile.getPetroPointsNumber() != null) {
                CardDetail petroPointsCard = new CardDetail(CardType.PPTS, profile.getPetroPointsNumber(), profile.getPointsBalance());
                saveCards(Collections.singletonList(petroPointsCard));
            }
        }
    }

    public boolean getCarWashToggleStatus() {
        return carWashreloadStatus;
    }

    public void onAttached() {
        retrieveCardsEvent.setValue(Event.newEvent(true));
    }

    public MutableLiveData<Calendar> getDateOfUpdate() {
        return dateOfUpdate;
    }

    private void saveCards(List<CardDetail> cards) {
        this.cards.clear();
        this.cards.addAll(cards);

        petroPointsCard.setValue(cards.get(0));

        ArrayList<CardDetail> petroCanadaCardsList = new ArrayList<>();
        boolean isCarWashBalanceZero = true;
        for (CardDetail item : cards) {
            if (item.getCardCategory() == CardDetail.CardCategory.PETRO_CANADA) {
                petroCanadaCardsList.add(item);
                if (item.getCardType() == CardType.ST || ((item.getCardType() == CardType.SP || item.getCardType() == CardType.WAG)
                        && item.getBalance() > 0)) isCarWashBalanceZero = false;
            }
        }
        isBalanceZero.setValue(isCarWashBalanceZero);
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

    public MutableLiveData<Boolean> getIsBalanceZero() {
        return isBalanceZero;
    }

    public void setIsBalanceZero(MutableLiveData<Boolean> isBalanceZero) {
        this.isBalanceZero = isBalanceZero;
    }
}
