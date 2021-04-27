package suncor.com.android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;

import java.util.concurrent.atomic.AtomicBoolean;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import suncor.com.android.utilities.Timber;

public class LocationLiveData extends LiveData<Location> {
    private final AtomicBoolean mPending = new AtomicBoolean(false);
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationManager locationManager;
    private Observer<? super Location> mCurrentObserver;
    private final Observer<Location> internalObserver = new Observer<Location>() {
        @Override
        public void onChanged(Location location) {
            if (mPending.compareAndSet(true, false)) {
                mCurrentObserver.onChanged(location);
            }
        }
    };
    private boolean useLastKnownLocation = true, liveUpdates = false;
    private LocationCallback locationCallback = new LocationCallback() {

        @Override
        public void onLocationAvailability(LocationAvailability locationAvailability) {
            //TODO do we need to handle errors here
        }

        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult != null) {
                fusedLocationProviderClient.removeLocationUpdates(locationCallback);
                postValue(locationResult.getLastLocation());
            }
        }
    };

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                postValue(location);
            }
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) { }
        @Override
        public void onProviderEnabled(String provider) { }
        @Override
        public void onProviderDisabled(String provider) { }
    };

    public LocationLiveData(Context context, boolean useLastKnownLocation) {
        this(context);
        this.useLastKnownLocation = useLastKnownLocation;
    }

    public LocationLiveData(Context context, boolean useLastKnownLocation, boolean liveUpdates) {
        this(context);
        this.useLastKnownLocation = useLastKnownLocation;
        this.liveUpdates = liveUpdates;
    }


    public LocationLiveData(Context context) {
        fusedLocationProviderClient = new FusedLocationProviderClient(context);
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    protected void setValue(Location value) {
        mPending.set(true);
        super.setValue(value);
    }

    @Override
    public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super Location> observer) {
        if (hasActiveObservers()) {
            Timber.w( "Has previous observers, but only the last one will be used");
        }
        mCurrentObserver = observer;
        super.observe(owner, internalObserver);
    }

    @Override
    public void removeObserver(@NonNull Observer<? super Location> observer) {
        super.removeObserver(internalObserver);
        locationManager.removeUpdates(locationListener);
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onActive() {
        if (hasActiveObservers()) {
            if (useLastKnownLocation) {
                fusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        postValue(task.getResult());
                    } else {
                        requestLocationUpdate();
                    }
                });
            } else {
                requestLocationUpdate();
            }

            if (liveUpdates) {
                requestLiveLocationUpdate();
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void requestLocationUpdate() {
        LocationRequest request = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        fusedLocationProviderClient.requestLocationUpdates(request, locationCallback, null);
    }

    @SuppressLint("MissingPermission")
    private void requestLiveLocationUpdate() {
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ) {
            // Threshold set at every 120 seconds and 250 meters in accordance to RMP-3198
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 120 * 1000, 250, locationListener);
        } else if (locationManager.isProviderEnabled("fused") ) {
            locationManager.requestLocationUpdates("fused", 120 * 1000, 250, locationListener);
        }
    }

}