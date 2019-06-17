package suncor.com.android.ui.main.cards.details;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import suncor.com.android.data.repository.cards.CardsApi;
import suncor.com.android.data.repository.cards.CardsRepository;
import suncor.com.android.mfp.SessionManager;
import suncor.com.android.model.Resource;
import suncor.com.android.model.account.Profile;
import suncor.com.android.model.cards.CardDetail;
import suncor.com.android.model.cards.CardType;

public class CardDetailsViewModel extends ViewModel {

    private final SessionManager sessionManager;
    private final CardsRepository cardsRepository;
    MediatorLiveData<List<CardDetail>> _cards = new MediatorLiveData<>();
    LiveData<List<CardDetail>> cards = _cards;
    private boolean isForScan;

    @Inject
    public CardDetailsViewModel(CardsRepository cardsRepository, SessionManager sessionManager, CardsApi cardsApi) {
        this.cardsRepository = cardsRepository;
        this.sessionManager = sessionManager;
    }

    public void setForScan(boolean forScan) {
        isForScan = forScan;
    }

    public void retrieveCards() {
        if (isForScan) {
            Profile profile = sessionManager.getProfile();
            CardDetail petroPointsCard = new CardDetail(CardType.PPTS, profile.getPetroPointsNumber(), profile.getPointsBalance());
            _cards.setValue(Collections.singletonList(petroPointsCard));
        } else {
            _cards.addSource(cardsRepository.getCards(false), result -> {
                if (result.status == Resource.Status.SUCCESS) {
                    _cards.setValue(result.data);
                }
            });
        }
    }

    public LiveData<Resource<CardDetail>> deleteCard(CardDetail cardDetail) {
        return cardsRepository.removeCard(cardDetail);
    }
}
