package suncor.com.android.ui.main.home;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.inject.Inject;

import suncor.com.android.R;
import suncor.com.android.data.DistanceApi;
import suncor.com.android.data.favourite.FavouriteRepository;
import suncor.com.android.data.stations.StationsApi;
import suncor.com.android.mfp.SessionManager;
import suncor.com.android.model.DirectionsResult;
import suncor.com.android.model.Resource;
import suncor.com.android.model.station.Station;
import suncor.com.android.ui.common.Event;
import suncor.com.android.ui.common.cards.CardFormatUtils;
import suncor.com.android.ui.main.stationlocator.StationItem;
import suncor.com.android.utilities.LocationUtils;

public class HomeViewModel extends ViewModel {

    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(new LatLng(20, -180), new LatLng(90, -50));
    private final static int DISTANCE_API = 25000;
    private FavouriteRepository favouriteRepository;
    public ObservableBoolean isLoading = new ObservableBoolean(false);
    private MediatorLiveData<Resource<StationItem>> _nearestStation = new MediatorLiveData<>();
    public LiveData<Resource<StationItem>> nearestStation = _nearestStation;
    private MutableLiveData<Boolean> _locationServiceEnabled = new MutableLiveData<>();
    public LiveData<Boolean> locationServiceEnabled = _locationServiceEnabled;
    private MutableLiveData<Event<Boolean>> loadNearest = new MutableLiveData<>();
    private SessionManager sessionManager;
    private LatLng userLocation;

    private MutableLiveData<Event<Station>> _openNavigationApps = new MutableLiveData<>();
    public LiveData<Event<Station>> openNavigationApps = _openNavigationApps;

    private MutableLiveData<Event<Boolean>> _navigateToPetroPoints = new MutableLiveData<>();
    public LiveData<Event<Boolean>> navigateToPetroPoints = _navigateToPetroPoints;

    private MutableLiveData<Event<Boolean>> _dismissEnrollmentRewardsCardEvent = new MutableLiveData<>();
    public LiveData<Event<Boolean>> dismissEnrollmentRewardsCardEvent = _dismissEnrollmentRewardsCardEvent;

    public ObservableInt greetingsMessage = new ObservableInt();
    public ObservableInt headerImage = new ObservableInt();

    @Inject
    public HomeViewModel(SessionManager sessionManager, StationsApi stationsApi, FavouriteRepository favouriteRepository, DistanceApi distanceApi) {
        this.sessionManager = sessionManager;
        this.favouriteRepository = favouriteRepository;
        LiveData<Resource<ArrayList<Station>>> nearestStationLoad = Transformations.switchMap(loadNearest, (event) -> {
            if (event.getContentIfNotHandled() != null) {
                LatLngBounds bounds = LocationUtils.calculateSquareBounds(userLocation, DISTANCE_API);
                return stationsApi.getStations(bounds, true);
            } else {
                return new MutableLiveData<>();
            }
        });

        _nearestStation.addSource(nearestStationLoad, ((resource) -> {
            isLoading.set(resource.status == Resource.Status.LOADING);

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
                        _nearestStation.setValue(Resource.success(new StationItem(favouriteRepository, resource.data.get(0), favouriteRepository.isFavourite(resource.data.get(0)))));
                    }
                    break;
            }
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
        });

        initGreetings();
    }

    private void initGreetings() {
        Calendar now = GregorianCalendar.getInstance();
        Calendar noon = GregorianCalendar.getInstance();
        noon.set(Calendar.HOUR_OF_DAY, 12);
        noon.set(Calendar.MINUTE, 0);
        Calendar evening = GregorianCalendar.getInstance();
        evening.set(Calendar.HOUR_OF_DAY, 17);
        evening.set(Calendar.MINUTE, 0);

        if (now.before(noon)) {
            greetingsMessage.set(R.string.home_signedin_greetings_morning);
            headerImage.set(R.drawable.home_backdrop_morning);
        } else if (now.before(evening)) {
            greetingsMessage.set(R.string.home_signedin_greetings_afternoon);
            headerImage.set(R.drawable.home_backdrop_afternoon);
        } else {
            greetingsMessage.set(R.string.home_signedin_greetings_evening);
            headerImage.set(R.drawable.home_backdrop_evening);
        }
    }


    @Override
    protected void onCleared() {
        super.onCleared();
    }

    public boolean isUserLoggedIn() {
        return sessionManager.isUserLoggedIn();
    }

    public void setLocationServiceEnabled(boolean enabled) {
        _locationServiceEnabled.setValue(enabled);
    }

    public LatLng getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(LatLng userLocation) {
        if (this.userLocation != null && LocationUtils.calculateDistance(this.userLocation, userLocation) < 10) {
            this.userLocation = userLocation;

            if (!LAT_LNG_BOUNDS.contains(userLocation)) {
                _nearestStation.setValue(Resource.success(null));
            } else if (_nearestStation.getValue() != null && _nearestStation.getValue().status != Resource.Status.SUCCESS) {
                loadNearest.setValue(Event.newEvent(true));
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

    public void openNavigationApps() {
        Station nearestStation = _nearestStation.getValue().data.getStation();
        _openNavigationApps.setValue(Event.newEvent(nearestStation));
    }

    public String getUserFirstName() {
        String firstName = sessionManager.getProfile().getFirstName();
        return firstName.substring(0, 1).toUpperCase() + firstName.substring(1);
    }

    public String getPetroPointsBalance() {
        return CardFormatUtils.formatBalance(sessionManager.getProfile().getPointsBalance());
    }

    public int getPetroPointsBalanceConverted() {
        return sessionManager.getProfile().getPointsBalance() / 1000;
    }


    public boolean shouldShowEnrollmentRewardsCard() {
        return sessionManager.getAccountState() == SessionManager.AccountState.JUST_ENROLLED;
    }

    public void dismissEnrollmentRewardsCard() {
        sessionManager.setAccountState(SessionManager.AccountState.REGULAR_LOGIN);
        _dismissEnrollmentRewardsCardEvent.setValue(Event.newEvent(true));
    }

    public void navigateToPetroPoints() {
        _navigateToPetroPoints.setValue(Event.newEvent(true));
    }

    public String getRewardedPoints() {
        return CardFormatUtils.formatBalance(sessionManager.getRewardedPoints());
    }
}