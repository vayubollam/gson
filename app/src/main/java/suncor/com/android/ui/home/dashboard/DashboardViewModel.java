package suncor.com.android.ui.home.dashboard;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
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

    public static final int MIN_Y = -180;
    public static final int MAX_Y = -50;
    public static final int MIN_X = 20;
    public static final int MAX_X = 90;
    private MediatorLiveData<Resource<Station>> _nearestStation = new MediatorLiveData<>();
    public LiveData<Resource<Station>> nearestStation = _nearestStation;

    private StationsProvider stationsProvider;
    private SessionManager.AccountState accountState = null;
    private final static int DISTANCE_API = 25000;
    private SessionManager sessionManager;
    public StationItem stationItem;
    public MutableLiveData<Boolean> locationServiceEnabled = new MutableLiveData<>();
    private LatLng userLocation;
    LatLngBounds canadaUSbounds;
    public MutableLiveData<Event<Boolean>> loadNearest = new MutableLiveData<>();


    @Inject
    public DashboardViewModel(SessionManager sessionManager, StationsProvider stationsProvider) {
        this.stationsProvider = stationsProvider;
        this.sessionManager = sessionManager;
        canadaUSbounds = new LatLngBounds(new LatLng(MIN_X, MIN_Y), new LatLng(MAX_X, MAX_Y));
        if (sessionManager.isUserLoggedIn()) {
            accountState = sessionManager.getAccountState();
        }

        LiveData<Resource<ArrayList<Station>>> nearestStationLoad = Transformations.switchMap(loadNearest, (event) -> {

            if (event.getContentIfNotHandled() != null) {
                LatLngBounds bounds = LocationUtils.calculateSquareBounds(userLocation, DISTANCE_API);
                return stationsProvider.getStations(bounds, true);
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
                        _nearestStation.setValue(Resource.success(resource.data.get(0)));
                    }
                    break;
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
        if (getUserLocation() != null && SphericalUtil.computeDistanceBetween(getUserLocation(), new LatLng(userLocation.latitude, userLocation.longitude)) < 10) {
            this.userLocation = userLocation;
            if (!canadaUSbounds.contains(userLocation)) {
                _nearestStation.setValue(Resource.success(null));
            }
            return;
        }
        this.userLocation = userLocation;
        if (canadaUSbounds.contains(userLocation)) {
            loadNearest.setValue(Event.newEvent(true));
        } else {
            _nearestStation.setValue(Resource.success(null));
        }


    }
}
