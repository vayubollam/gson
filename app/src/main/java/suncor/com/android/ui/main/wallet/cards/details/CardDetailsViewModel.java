package suncor.com.android.ui.main.wallet.cards.details;


import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import suncor.com.android.R;
import suncor.com.android.data.cards.CardsRepository;
import suncor.com.android.data.settings.SettingsApi;
import suncor.com.android.googlepay.passes.LoyalityData;
import suncor.com.android.mfp.SessionManager;
import suncor.com.android.model.Resource;
import suncor.com.android.model.SettingsResponse;
import suncor.com.android.model.account.Profile;
import suncor.com.android.model.cards.CardDetail;
import suncor.com.android.model.cards.CardType;
import suncor.com.android.model.station.Station;
import suncor.com.android.ui.main.wallet.cards.CardsLoadType;
import suncor.com.android.utilities.Timber;

public class CardDetailsViewModel extends ViewModel {

    private final SessionManager sessionManager;
    private final CardsRepository cardsRepository;
    private MediatorLiveData<List<CardDetail>> _cards = new MediatorLiveData<>();
    LiveData<List<CardDetail>> cards = _cards;
    private CardsLoadType loadType;
    private Set<String> redeemedTicketNumbers;
    private MutableLiveData<Boolean> isCarWashBalanceZero = new MutableLiveData<>();
    private MutableLiveData<Boolean> isVacuumProgress = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> isWashProgress = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> isCanVacuum = new MutableLiveData<>();
    private MutableLiveData<Boolean> isCanWash = new MutableLiveData<>();

    protected MutableLiveData<Boolean> isServiceRunning = new MutableLiveData<>();
    private String newlyAddedCardNumber;
    private final SettingsApi settingsApi;
    final int interval = 360000;
    private Timer carousalTimer=new Timer();

    @Inject
    public CardDetailsViewModel(CardsRepository cardsRepository, SessionManager sessionManager, SettingsApi settingsApi) {
        this.cardsRepository = cardsRepository;
        this.sessionManager = sessionManager;
        this.settingsApi = settingsApi;
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
        for (CardDetail card : petroCanadaCards) {
            if (card.getCardType() != CardType.ST && card.getCardNumber().equals(newlyAddedCardNumber))
                return Collections.singletonList(card);
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

    public void updateSPStatus(CardDetail card) {
        Timber.d("SP STATUS CALLED" + card.getCardNumber() + getIsVacuumProgress());
        boolean isVacuumprogress = false;
        boolean isWashProgress = false;
        boolean isCanWash = false;
        boolean isCanVacuum = false;


        if (card.getCardType() == CardType.SP && card.isVacuumInProgress()) {
            isVacuumprogress = true;
        }
        if (card.getCardType() == CardType.SP && card.isWashInProgress()) {
            Timber.d("CHECKED AND CHANGED");
            isWashProgress = true;
        }
        if (card.getCardType() == CardType.SP && card.isCanWash()) {
            Timber.d("CHECKED AND CHANGED");
            isCanWash = true;
        }
        if (card.getCardType() == CardType.SP && card.isCanVacuum()) {
            Timber.d("CHECKED AND CHANGED");
            isCanVacuum = true;
        }


        this.isCanWash.setValue(isCanWash);
        this.isCanVacuum.setValue(isCanVacuum);
        this.isVacuumProgress.setValue(isVacuumprogress);
        this.isWashProgress.setValue(isWashProgress);
    }

    public LiveData<Resource<CardDetail>> getProgressDetails(String cardNum) {
        return cardsRepository.getSPCardDetails(cardNum);
    }


    public LiveData<Resource<Station>> getStoreDetails(String storeId) {
        return cardsRepository.getStoreDetails(storeId);

    }

    public MutableLiveData<Boolean> getIsCarWashBalanceZero() {
        return isCarWashBalanceZero;
    }

    public MutableLiveData<Boolean> getIsServiceRunning() {
        return isServiceRunning;
    }

    public MutableLiveData<Boolean> getIsVacuumProgress() {
        return isVacuumProgress;
    }

    public MutableLiveData<Boolean> getIsWashProgress() {
        return isWashProgress;
    }

    public MutableLiveData<Boolean> getIsCanVacuum() {
        return isCanVacuum;
    }

    public MutableLiveData<Boolean> getIsCanWash() {
        return isCanWash;
    }

    protected Profile getUserProfile() {
        return sessionManager.getProfile();
    }

    protected LoyalityData getLoyalityCardDataForGoogleWallet(Context context, int clickedCardIndex) {
        LoyalityData loyalityData = new LoyalityData();
        loyalityData.setBarcode(cards.getValue().get(clickedCardIndex).getCardNumber().replace(" ", ""));
        ExpandedCardItem expandedCardItem = new ExpandedCardItem(context, cards.getValue().get(clickedCardIndex));
        loyalityData.setBarcodeDisplay(expandedCardItem.getCardNumber());
        loyalityData.setNameLabel(context.getString(R.string.google_passes_name_label));
        loyalityData.setNameLocalizedLabel(context.getString(R.string.google_passes_name_label_fr));
        loyalityData.setNameValue(getUserProfile().getFirstName() + " " + getUserProfile().getLastName());
        loyalityData.setEmailLabel(context.getString(R.string.google_passes_email_label));
        loyalityData.setEmailLocalizedLabel(context.getString(R.string.google_passes_email_label_fr));
        loyalityData.setEmailValue(getUserProfile().getEmail());
        loyalityData.setDetailsLabel(context.getString(R.string.google_passes_detail_label));
        loyalityData.setDetailsLocalizedLabel(context.getString(R.string.google_passes_detail_label_fr));
        loyalityData.setDetailsValue(context.getString(R.string.google_passes_detail_value));
        loyalityData.setDetailsLocalizedValue(context.getString(R.string.google_passes_detail_value_fr));
        loyalityData.setValuesLabel(context.getString(R.string.google_passes_value_label));
        loyalityData.setValuesLocalizedLabel(context.getString(R.string.google_passes_value_label_fr));
        loyalityData.setValuesValue(context.getString(R.string.google_passes_value_value));
        loyalityData.setValuesLocalizedValue(context.getString(R.string.google_passes_value_value_fr));
        loyalityData.setHowToUseLabel(context.getString(R.string.google_passes_howtouse_label));
        loyalityData.setHowToUseLocalizedLabel(context.getString(R.string.google_passes_howtouse_label_fr));
        loyalityData.setHowToUseValue(context.getString(R.string.google_passes_howtouse_value));
        loyalityData.setHowToUseLocalizedValue(context.getString(R.string.google_passes_howtouse_value_fr));
        loyalityData.setTermConditionLabel(context.getString(R.string.google_passes_termcondition_label));
        loyalityData.setTermConditionLocalizedLabel(context.getString(R.string.google_passes_termcondition_label_fr));
        loyalityData.setTermConditionValue(context.getString(R.string.google_passes_termcondition_value));
        loyalityData.setTermConditionLocalizedValue(context.getString(R.string.google_passes_termcondition_value_fr));
        return loyalityData;
    }

    public LiveData<Resource<SettingsResponse>> getSettings() {
        return settingsApi.retrieveSettings();
    }

    public void refreshCards() {
        _cards.addSource(cardsRepository.getCards(true), result -> {
            if (result.status == Resource.Status.SUCCESS) {
                _cards.setValue(result.data);
                updateCarWashBalance(_cards.getValue());
            }
        });
        Timber.d("Refreshed Cards");
    }


    protected void setRecurringService(String cardNumber) {
        carousalTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                getProgressDetails(cardNumber);
                isServiceRunning.postValue(true);
                Timber.d("--SERVICE STARTED--");
            }
        }, interval, interval); // delay
    }

    void stopRecurringService() {
        carousalTimer.cancel();
        Timber.d("--SERVICE STOP--");
        isServiceRunning.postValue(false);
    }


}
