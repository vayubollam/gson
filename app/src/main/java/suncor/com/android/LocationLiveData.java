package suncor.com.android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.util.Log;

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

public class LocationLiveData extends LiveData<Location> {
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Observer<? super Location> mCurrentObserver;
    private final Observer<Location> internalObserver = new Observer<Location>() {
        @Override
        public void onChanged(Location location) {
            if (mPending.compareAndSet(true, false)) {
                mCurrentObserver.onChanged(location);
            }
        }
    };
    private boolean useLastKnownLocation = true;
    private final AtomicBoolean mPending = new AtomicBoolean(false);


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

    public LocationLiveData(Context context, boolean useLastKnownLocation) {
        this(context);
        this.useLastKnownLocation = useLastKnownLocation;
    }


    public LocationLiveData(Context context) {
        fusedLocationProviderClient = new FusedLocationProviderClient(context);
    }

    @Override
    protected void setValue(Location value) {
        mPending.set(true);
        super.setValue(value);
    }

    @Override
    public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super Location> observer) {
        if (hasActiveObservers()) {
            Log.w(LocationLiveData.class.getSimpleName(), "Has previous observers, but only the last one will be used");
        }
        mCurrentObserver = observer;
        super.observe(owner, internalObserver);
    }

    @Override
    public void removeObserver(@NonNull Observer<? super Location> observer) {
        super.removeObserver(internalObserver);
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
        }
    }

    @SuppressLint("MissingPermission")
    private void requestLocationUpdate() {
        LocationRequest request = LocationRequest.create();
        fusedLocationProviderClient.requestLocationUpdates(request, locationCallback, null);
    }
}