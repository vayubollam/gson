package suncor.com.android.ui.main.cards.details;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import suncor.com.android.data.cards.CardsRepository;
import suncor.com.android.mfp.SessionManager;
import suncor.com.android.model.Resource;
import suncor.com.android.model.account.Profile;
import suncor.com.android.model.cards.CardDetail;
import suncor.com.android.model.cards.CardType;
import suncor.com.android.ui.main.cards.CardsLoadType;

public class CardDetailsViewModel extends ViewModel {

    private final SessionManager sessionManager;
    private final CardsRepository cardsRepository;
    MediatorLiveData<List<CardDetail>> _cards = new MediatorLiveData<>();
    LiveData<List<CardDetail>> cards = _cards;
    private CardsLoadType loadType;

    @Inject
    public CardDetailsViewModel(CardsRepository cardsRepository, SessionManager sessionManager) {
        this.cardsRepository = cardsRepository;
        this.sessionManager = sessionManager;
    }

    public void retrieveCards() {
        switch (loadType) {
            case PETRO_POINT_ONLY:
                Profile profile = sessionManager.getProfile();
                CardDetail petroPointsCard = new CardDetail(CardType.PPTS, profile.getPetroPointsNumber(), profile.getPointsBalance());
                _cards.setValue(Collections.singletonList(petroPointsCard));
                break;
            case REDEEMED_SINGLE_TICKETS:
            case CAR_WASH_PRODUCTS:
                _cards.addSource(cardsRepository.getCards(false), result -> {
                    if (result.status == Resource.Status.SUCCESS) {
                        _cards.setValue(CardsRepository.filterCarWashCards(result.data));
                    }
                });
                break;
            case ALL:
                _cards.addSource(cardsRepository.getCards(false), result -> {
                    if (result.status == Resource.Status.SUCCESS) {
                        _cards.setValue(result.data);
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

}
