package suncor.com.android.ui.home.dashboard;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import suncor.com.android.data.repository.stations.StationsProvider;
import suncor.com.android.mfp.SessionManager;
import suncor.com.android.model.Resource;
import suncor.com.android.model.station.Station;
import suncor.com.android.ui.common.Event;
import suncor.com.android.ui.home.stationlocator.StationItem;
import suncor.com.android.utilities.LocationUtils;

public class DashboardViewModel extends ViewModel {

    public LiveData<Resource<Station>> nearestStation;
    private StationsProvider stationsProvider;
    private SessionManager.AccountState accountState = null;
    private final static int DISTANCE_API = 100000;
    private SessionManager sessionManager;
    public StationItem stationItem;
    public MutableLiveData<Boolean> locationServiceEnabled = new MutableLiveData<>();
    private LatLng userLocation;
    private MutableLiveData<Event<Boolean>> loadNearest = new MutableLiveData<>();


    @Inject
    public DashboardViewModel(SessionManager sessionManager, StationsProvider stationsProvider) {
        this.stationsProvider = stationsProvider;
        this.sessionManager = sessionManager;
        initNearestStation();
        if (sessionManager.isUserLoggedIn()) {
            accountState = sessionManager.getAccountState();
        }
    }


    public void initNearestStation() {


        LiveData<Resource<ArrayList<Station>>> nearestStationLoad = Transformations.switchMap(loadNearest, (event) -> {

            if (event.getContentIfNotHandled() != null && userLocation != null) {
                LatLngBounds bounds = LocationUtils.calculateSquareBounds(userLocation, DISTANCE_API);
                return stationsProvider.getStations(bounds, true);
            } else {
                return new MutableLiveData<>();
            }
        });


        nearestStation = Transformations.map(nearestStationLoad, ((resource) -> {
            switch (resource.status) {
                case LOADING:
                    return Resource.loading(null);
                case ERROR:
                    return Resource.error(resource.message, null);
                default:
                    if (resource.data.isEmpty()) {
                        return Resource.success(null);
                    } else {
                        return Resource.success(resource.data.get(0));
                    }
            }
        }));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (accountState == SessionManager.AccountState.JUST_ENROLLED) {
            sessionManager.setAccountState(SessionManager.AccountState.REGULAR_LOGIN);
        }
    }

    public SessionManager.AccountState getAccountState() {
        return accountState;
    }

    public LatLng getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(LatLng userLocation) {
        loadNearest.setValue(Event.newEvent(true));
        this.userLocation = userLocation;

    }
}
