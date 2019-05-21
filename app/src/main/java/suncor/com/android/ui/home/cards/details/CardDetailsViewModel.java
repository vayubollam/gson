package suncor.com.android.ui.home.cards.details;

import java.util.ArrayList;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import suncor.com.android.data.repository.cards.CardsRepository;
import suncor.com.android.model.Resource;
import suncor.com.android.model.cards.CardDetail;

public class CardDetailsViewModel extends ViewModel {

    CardsRepository cardsRepository;
    LiveData<Resource<ArrayList<CardDetail>>> cards;

    @Inject
    public CardDetailsViewModel(CardsRepository cardsRepository) {
        this.cardsRepository = cardsRepository;
        cards = cardsRepository.getCards(false);
    }


}
