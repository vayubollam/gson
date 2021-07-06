package suncor.com.android.ui.main.carwash;

import android.os.Handler;
import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import android.os.Handler;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import suncor.com.android.data.DistanceApi;
import suncor.com.android.data.cards.CardsRepository;
import suncor.com.android.data.favourite.FavouriteRepository;
import suncor.com.android.data.stations.StationsApi;
import suncor.com.android.model.DirectionsResult;
import suncor.com.android.model.Resource;
import suncor.com.android.model.cards.CardDetail;
import suncor.com.android.model.cards.CardType;
import suncor.com.android.model.station.Station;
import suncor.com.android.ui.common.Event;
import suncor.com.android.ui.main.stationlocator.StationItem;
import suncor.com.android.utilities.LocationUtils;
import suncor.com.android.utilities.StationsUtil;

@Singleton
public class CarWashCardViewModel extends ViewModel {

    private final CardsRepository repository;
    private FavouriteRepository favouriteRepository;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(new LatLng(20, -180), new LatLng(90, -50));
    private final static int DISTANCE_API = 25000;

    private MutableLiveData<ViewState> viewState = new MutableLiveData<>();
    private MutableLiveData<Boolean> isBalanceZero = new MutableLiveData<>();
    private MutableLiveData<Boolean> isCardAvailable = new MutableLiveData<>();
    private MutableLiveData<Event<Boolean>> retrieveCardsEvent = new MutableLiveData<>();
    private MutableLiveData<List<CardDetail>> petroCanadaCards = new MutableLiveData<>();
    private MutableLiveData<Event<Boolean>> refreshCardsEvent = new MutableLiveData<>();

    public ObservableBoolean isLoading = new ObservableBoolean(false);
    private MediatorLiveData<Resource<StationItem>> _nearestStation = new MediatorLiveData<>();
    private LiveData<Resource<StationItem>> nearestStation = _nearestStation;


    private MutableLiveData<Boolean> _locationServiceEnabled = new MutableLiveData<>();
    private LiveData<Boolean> locationServiceEnabled = _locationServiceEnabled;
    private MutableLiveData<Event<Boolean>> loadNearest = new MutableLiveData<>();
    private MutableLiveData<Event<Boolean>> refreshLocationCard = new MutableLiveData<>();

    private MutableLiveData<Boolean> isNearestStationIndependent = new MutableLiveData<>();

    private LatLng userLocation;
    private MutableLiveData<CardTypeStatus> cardTypeStatus = new MutableLiveData<>();


    @Inject
    public CarWashCardViewModel(CardsRepository repository, StationsApi stationsApi, FavouriteRepository favouriteRepository, DistanceApi distanceApi) {
        this.repository = repository;
        this.favouriteRepository = favouriteRepository;

        MediatorLiveData<Resource<ArrayList<CardDetail>>> apiCall = new MediatorLiveData<>();
        //load wash cards
        LiveData<Resource<ArrayList<CardDetail>>> retrieveCall = Transformations.switchMap(retrieveCardsEvent, event -> {
            if (event.getContentIfNotHandled() != null) {
                return repository.getCards(true);
            }
            return new MutableLiveData<>();
        });

        //refresh wash cards
        LiveData<Resource<ArrayList<CardDetail>>> refreshCall = Transformations.switchMap(refreshCardsEvent, event -> {
            if (event.getContentIfNotHandled() != null) {
                return repository.getCards(true);
            }
            return new MutableLiveData<>();
        });

        apiCall.addSource(retrieveCall, apiCall::setValue);
        apiCall.addSource(refreshCall, apiCall::setValue);

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

        //load near stations
        LiveData<Resource<ArrayList<Station>>> nearestStationLoad = Transformations.switchMap(loadNearest, event -> {
            if (event.getContentIfNotHandled() != null) {
                LatLngBounds bounds = LocationUtils.calculateSquareBounds(userLocation, DISTANCE_API);
                return stationsApi.getStations(bounds, true);
            } else {
                return new MutableLiveData<>();
            }
        });

        _nearestStation.addSource(nearestStationLoad, ((resource) -> {
            switch (resource.status) {
                case LOADING:
                    _nearestStation.setValue(Resource.loading());
                    break;
                case ERROR:
                    _nearestStation.setValue(Resource.error(resource.message));
                    break;
                case SUCCESS:
                    if (resource.data == null || resource.data.isEmpty()) {
                        _nearestStation.setValue(Resource.success(null));
                    } else {
                        Station station = StationsUtil.filterNearestCarWashStation(resource.data);
                        if (station == null) {
                            _nearestStation.setValue(Resource.success(null));
                        } else {
                            _nearestStation.setValue(Resource.success(new StationItem(favouriteRepository, station, favouriteRepository.isFavourite(station))));
                        }
                    }
                    break;
            }
            new Handler().postDelayed(() ->
            {
                isLoading.set(resource.status == Resource.Status.LOADING);
            }, 1000);
        }));

        LiveData<Resource<DirectionsResult>> directionsResultLiveData = Transformations.switchMap(_nearestStation, resource -> {
            if (resource.status == Resource.Status.SUCCESS && resource.data != null && resource.data.getDistanceDuration() == null) {
                LatLng dest = new LatLng(resource.data.getStation().getAddress().getLatitude(), resource.data.getStation().getAddress().getLongitude());
                LatLng origin = new LatLng(userLocation.latitude, userLocation.longitude);
                return distanceApi.enqueuJob(origin, dest);
            } else {
                return new MutableLiveData<>();
            }
        });

        directionsResultLiveData.observeForever(resource -> {
            if (resource.status == Resource.Status.LOADING) {
                return;
            }
            StationItem item = nearestStation.getValue().data;
            if (resource.status == Resource.Status.SUCCESS) {
                item.setDistanceDuration(resource.data);
            } else if (resource.status == Resource.Status.ERROR) {
                item.setDistanceDuration(DirectionsResult.INVALID);
            }
            _nearestStation.setValue(Resource.success(item));
            isNearestStationIndependent.setValue(validateIndependentStation(item.getStation()));
        });

    }

    public void onAttached() {
        loadData(ViewState.LOADING);
    }

    public void loadData(ViewState state) {
        viewState.setValue(state);
        if (state == ViewState.REFRESHING) {
            refreshCardsEvent.setValue(Event.newEvent(true));
            refreshLocationCard.setValue(Event.newEvent(true));
        } else {
            retrieveCardsEvent.setValue(Event.newEvent(true));
        }
    }

    public MutableLiveData<ViewState> getViewState() {
        return viewState;
    }

    private void saveCards(List<CardDetail> cards) {
        List<CardDetail> carWashCards = CardsRepository.filterCarWashCards(cards);
        if (carWashCards.size() == 0) {
            isCardAvailable.setValue(false);
        } else {
            setUpCardTypeStatusAndIsBalanceZero(carWashCards);
            isCardAvailable.setValue(true);
            petroCanadaCards.setValue(carWashCards);
        }
    }

    private void setUpCardTypeStatusAndIsBalanceZero(List<CardDetail> cards) {
        boolean isAllBalanceZero = true;
        int cardQuantity = 0;
        int ticketQuantity = 0;
        for (CardDetail card : cards) {
            if (card.getCardType().equals(CardType.SP) || card.getCardType().equals(CardType.WAG)) {
                if (card.getBalance() > 0) {
                    isAllBalanceZero = false;
                }
                cardQuantity++;
            } else if (card.getCardType().equals(CardType.ST)) {
                ticketQuantity++;
                //isAllBalanceZero = false; //TODO: uncomment this once single ticket back end is ready
            }
        }
        if (cardQuantity == 0 && ticketQuantity != 0) {
            this.cardTypeStatus.setValue(CardTypeStatus.TICKET_ONLY);
        } else if (cardQuantity != 0 && ticketQuantity == 0) {
            this.cardTypeStatus.setValue(CardTypeStatus.CARD_ONLY);
        } else {
            this.cardTypeStatus.setValue(CardTypeStatus.CARD_AND_TICKET);
        }
        isBalanceZero.setValue(isAllBalanceZero);
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

    public ObservableBoolean getIsLoading() {
        return isLoading;
    }

    public void setLocationServiceEnabled(boolean enabled) {
        _locationServiceEnabled.setValue(enabled);
    }

    public LatLng getUserLocation() {
        return userLocation;
    }

    public LiveData<Boolean> getLocationServiceEnabled() {
        return locationServiceEnabled;
    }

    public LiveData<CardTypeStatus> getCardTypeStatus() {
        return cardTypeStatus;
    }

    public void setUserLocation(LatLng userLocation) {
        if (this.userLocation != null && LocationUtils.calculateDistance(this.userLocation, userLocation) < 10) {
            this.userLocation = userLocation;

            if (!LAT_LNG_BOUNDS.contains(userLocation)) {
                _nearestStation.setValue(Resource.success(null));
                new Handler().postDelayed(() -> {
                    isLoading.set(false);
                }, 1000);
            } else if (_nearestStation.getValue() != null && _nearestStation.getValue().status != Resource.Status.SUCCESS) {
                loadNearest.setValue(Event.newEvent(true));
            }else {
                new Handler().postDelayed(() -> {
                    isLoading.set(false);
                }, 1000);
            }
        } else {
            this.userLocation = userLocation;
            if (LAT_LNG_BOUNDS.contains(userLocation)) {
                loadNearest.setValue(Event.newEvent(true));
            } else {
                _nearestStation.setValue(Resource.success(null));
                isLoading.set(false);
            }
        }
    }

    public LiveData<Resource<StationItem>> getNearestStation() {
        return nearestStation;
    }

    public MutableLiveData<Event<Boolean>> getRefreshLocationCard() {
        return refreshLocationCard;
    }

    public int getIndexofCardDetail(CardDetail cardDetail) {
        if (petroCanadaCards.getValue() != null) {
            return petroCanadaCards.getValue().indexOf(cardDetail);
        } else {
            return 0;
        }
    }

    public MutableLiveData<Boolean> getIsNearestStationIndependent() {
        return isNearestStationIndependent;
    }

    private boolean validateIndependentStation(Station station) {
        LatLng dest = new LatLng(station.getAddress().getLatitude(), station.getAddress().getLongitude());
        LatLng origin = new LatLng(userLocation.latitude, userLocation.longitude);
        return LocationUtils.calculateDistance(dest, origin) < DirectionsResult.ONSITE_THRESHOLD
                && station.isStationIndependentDealer();
    }

    public enum ViewState {
        LOADING, FAILED, SUCCESS, REFRESHING
    }

    public enum CardTypeStatus {
        CARD_ONLY,
        TICKET_ONLY,
        CARD_AND_TICKET
    }
}
