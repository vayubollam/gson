package suncor.com.android.ui.main.home;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.VisibleForTesting;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableChar;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.inject.Inject;
import javax.inject.Singleton;

import suncor.com.android.R;
import suncor.com.android.data.DistanceApi;
import suncor.com.android.data.favourite.FavouriteRepository;
import suncor.com.android.data.pap.PapRepository;
import suncor.com.android.data.settings.SettingsApi;
import suncor.com.android.data.stations.StationsApi;
import suncor.com.android.mfp.SessionManager;
import suncor.com.android.model.DirectionsResult;
import suncor.com.android.model.Resource;
import suncor.com.android.model.pap.ActiveSession;
import suncor.com.android.model.pap.FuelUp;
import suncor.com.android.model.pap.P97StoreDetailsResponse;
import suncor.com.android.model.station.Station;
import suncor.com.android.ui.common.Event;
import suncor.com.android.ui.common.cards.CardFormatUtils;
import suncor.com.android.ui.main.pap.selectpump.SelectPumpViewModel;
import suncor.com.android.ui.main.stationlocator.StationItem;
import suncor.com.android.utilities.Constants;
import suncor.com.android.utilities.DateUtils;
import suncor.com.android.utilities.LocationUtils;
import suncor.com.android.utilities.StationsUtil;
import suncor.com.android.utilities.Timber;

@Singleton
public class HomeViewModel extends ViewModel {

    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(new LatLng(20, -180), new LatLng(90, -50));
    private final static int DISTANCE_API = 25000;

    private PapRepository papRepository;
    private final SettingsApi settingsApi;
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

    public MutableLiveData<Station> nearestCarWashStation = new MutableLiveData<>();

    public ObservableInt greetingsMessage = new ObservableInt();
    public ObservableInt headerImage = new ObservableInt();

    public ObservableBoolean activeFuellingSession = new ObservableBoolean();
    public ObservableField<String> fuellingStateMessage = new ObservableField<>();

    public ObservableBoolean isExpired = new ObservableBoolean(false);

    @Inject
    public HomeViewModel(SessionManager sessionManager, StationsApi stationsApi,
                         FavouriteRepository favouriteRepository, DistanceApi distanceApi,
                         PapRepository papRepository, SettingsApi settingsApi) {
        this.sessionManager = sessionManager;
        this.papRepository = papRepository;
        this.settingsApi = settingsApi;
        fuellingStateMessage.set("fuellingStateMessage");
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
                        nearestCarWashStation.setValue(StationsUtil.filterNearestCarWashStation(resource.data));
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
        getDateDifference();
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

    public void getDateDifference(){

        long diff = DateUtils.getDateTimeDifference(DateUtils.getCurrentDateInEST(), Constants.IMAGE_EXPIRY_DATE, false);

        if(diff < 0){
            isExpired.set(true);
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
                new Handler().postDelayed(() -> {
                    isLoading.set(false);
                }, 1000);
            } else if (_nearestStation.getValue() != null && _nearestStation.getValue().status != Resource.Status.SUCCESS) {
                loadNearest.setValue(Event.newEvent(true));
            } else {
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

    public void openNavigationForNearestStation() {
        if(_nearestStation.getValue().data != null){
            Station nearestStation = _nearestStation.getValue().data.getStation();
            _openNavigationApps.setValue(Event.newEvent(nearestStation));
        }
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

    public void navigateToPetroPointsScreen() {
        _navigateToPetroPoints.setValue(Event.newEvent(true));
    }

    public String getRewardedPoints() {
        return CardFormatUtils.formatBalance(sessionManager.getRewardedPoints());
    }

    public LiveData<Resource<P97StoreDetailsResponse>> getStoreDetails(String storeId) {
        return papRepository.getStoreDetails(storeId);
    }

public LiveData<Resource<FuelUp>> isPAPAvailable() {
    return Transformations.switchMap(nearestStation, stationItemResource -> {
        if (stationItemResource.status == Resource.Status.SUCCESS && stationItemResource.data != null &&
                stationItemResource.data.getStation() != null) {
            return Transformations.switchMap(getGeoFenceLiveData(), limit -> {
                if (limit.status == Resource.Status.SUCCESS && limit.data != null) {
                    return Transformations.map(getStoreDetails(stationItemResource.data.getStation().getId()), result -> {

                        return new Resource<>(result.status,
                                new FuelUp(stationItemResource, limit, result), result.message);

                    });
                } else {
                    return new MutableLiveData<>(new Resource<>(limit.status, new FuelUp(), limit.message));
                }
            });
        } else {
            return new MutableLiveData<>(new Resource<>(stationItemResource.status, new FuelUp(), stationItemResource.message));
        }
    });
}

    public LiveData<Resource<FuelUp>> isPAPAvailable(StationItem stationItem) {
        Resource<StationItem> nearestStation = new Resource<>(Resource.Status.SUCCESS, stationItem, null);

        return Transformations.switchMap(getGeoFenceLiveData(), limit -> {
            if (limit.status == Resource.Status.SUCCESS && limit.data != null) {
                return Transformations.switchMap(getActiveSession(), activeSessionResource -> {
                    if (activeSessionResource.status == Resource.Status.SUCCESS && activeSessionResource.data != null) {
                        return Transformations.map(getStoreDetails(stationItem.getStation().getId()), result -> {
                            return new Resource<>(result.status, new FuelUp(nearestStation, limit, activeSessionResource, result), result.message);
                        });
                    } else {
                        return new MutableLiveData<>(new Resource<>(activeSessionResource.status, new FuelUp(), activeSessionResource.message));
                    }
                });
            } else {
                return new MutableLiveData<>(new Resource<>(limit.status, new FuelUp(), limit.message));
            }
        });
    }

    //Call this method when fuelling state change
    protected void updateFuellingSession(boolean isActiveFuelingSession, String stateMessage){
        activeFuellingSession.set(isActiveFuelingSession);
        fuellingStateMessage.set(stateMessage);
    }

    public LiveData<Resource<ActiveSession>> getActiveSession() {
        return  papRepository.getActiveSession();
    }

    public LiveData<Resource<Integer>> getGeoFenceLiveData() {
        return Transformations.map(settingsApi.retrieveSettings(), result ->
                new Resource<>(result.status, result.data != null ? result.data.getSettings().getPap().getGeofenceDistanceMeters() : 0, result.message));
    }

    @VisibleForTesting
    public long getDateTimeDifference(String startDate, String endDate){
        return DateUtils.getDateTimeDifference(startDate, endDate, false);
    }

}
