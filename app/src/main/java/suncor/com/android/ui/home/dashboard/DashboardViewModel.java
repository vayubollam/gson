package suncor.com.android.ui.home.dashboard;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import suncor.com.android.SuncorApplication;
import suncor.com.android.data.repository.stations.StationsProvider;
import suncor.com.android.mfp.SessionManager;
import suncor.com.android.model.Resource;
import suncor.com.android.model.Station;

public class DashboardViewModel extends ViewModel {

    public LiveData<Resource<Station>> nearestStation;
    private StationsProvider stationsProvider;
    private SessionManager.AccountState accountState = null;

    public DashboardViewModel() {
        //TODO move the parameter to constructor
        this.stationsProvider = SuncorApplication.stationsProvider;
        initNearestStation();
        if (SessionManager.getInstance().isUserLoggedIn()) {
            accountState = SessionManager.getInstance().getAccountState();
        }
    }

    public void initNearestStation() {
        LatLngBounds bounds = new LatLngBounds(
                new LatLng(43.468, -79.55637),
                new LatLng(43.841, -79.2456238321887)
        );

        nearestStation = Transformations.map(stationsProvider.getStations(bounds), ((resource) -> {
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
            SessionManager.getInstance().setAccountState(SessionManager.AccountState.REGULAR_LOGIN);
        }
    }

    public SessionManager.AccountState getAccountState() {
        return accountState;
    }
}
