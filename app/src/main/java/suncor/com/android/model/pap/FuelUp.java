package suncor.com.android.model.pap;

import suncor.com.android.model.Resource;
import suncor.com.android.ui.main.stationlocator.StationItem;

public class FuelUp {

    public Resource<StationItem> nearestStation;
    public Resource<Integer> geoFenceLimit;
    public Resource<ActiveSession> activeSession;
    public Resource<P97StoreDetailsResponse> storeDetailsResponse;

    public FuelUp() { }

    public FuelUp(Resource<StationItem> nearestStation, Resource<Integer> geoFenceLimit, Resource<ActiveSession> activeSession,
                  Resource<P97StoreDetailsResponse> storeDetailsResponse) {
        this.nearestStation = nearestStation;
        this.geoFenceLimit = geoFenceLimit;
        this.activeSession = activeSession;
        this.storeDetailsResponse = storeDetailsResponse;
    }

    public boolean papAvailable() {
        return storeDetailsResponse != null && storeDetailsResponse.data != null && storeDetailsResponse.data.mobilePaymentStatus.getPapAvailable();
    }

    public boolean fuelUpAvailable() {
        return activeSession != null
                && activeSession.data != null
                && !activeSession.data.activeSession
                && nearestStation != null
                && nearestStation.data != null
                && geoFenceLimit != null
                && geoFenceLimit.data != null
                && nearestStation.data.getDistanceDuration() != null
                && nearestStation.data.getDistanceDuration().getDistance() < geoFenceLimit.data
                && papAvailable();
    }
}
