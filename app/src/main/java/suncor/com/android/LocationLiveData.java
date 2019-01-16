package suncor.com.android;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.lifecycle.LiveData;

public class LocationLiveData extends LiveData<Location> implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    private GoogleApiClient googleApiClient;


    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    public LocationLiveData(Context context) {
        googleApiClient =
                new GoogleApiClient.Builder(context, this, this)
                        .addApi(LocationServices.API)
                        .build();
    }

    @Override
    protected void onActive() {
        // Wait for the GoogleApiClient to be connected
        googleApiClient.connect();
    }

    @Override
    protected void onInactive() {
        if (googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    googleApiClient, this);
        }
        googleApiClient.disconnect();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onConnected(Bundle connectionHint) {
        // Try to immediately find a location
        Location lastLocation = LocationServices.FusedLocationApi
                .getLastLocation(googleApiClient);
        if (lastLocation != null) {
            setValue(lastLocation);
        }

        // Request updates if there’s someone observing
        if (hasActiveObservers()) {
            LocationRequest request = new LocationRequest()
                    .setNumUpdates(1);

            LocationServices.FusedLocationApi.requestLocationUpdates(
                    googleApiClient, request, this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        // Deliver the location changes
        setValue(location);
    }


    @Override
    public void onConnectionSuspended(int cause) {
        // Cry softly, hope it comes back on its own
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}