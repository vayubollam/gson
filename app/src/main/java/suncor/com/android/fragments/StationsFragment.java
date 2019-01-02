package suncor.com.android.fragments;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import suncor.com.android.R;
import suncor.com.android.adapters.StationAdapter;
import suncor.com.android.dataObjects.Station;
import suncor.com.android.dialogs.LocationDialog;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;


public class StationsFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnCameraMoveListener, GoogleMap.OnCameraIdleListener {

    private StationsViewModel mViewModel;
    private GoogleMap mGoogleMap;
    private MaterialButton btn_my_location;
    private int zoomLevel = 10;
    private Marker station_marker;
    private HashMap<Marker, Station> stations_markers = new HashMap<>();
    private Marker last_marker;
    private ProgressBar indeterminateBar;
    private Observer<ArrayList<Station>> stationsObserver;
    private LatLngBounds last_bounds;
    private boolean location_permission_is_granted;
    private StationAdapter stationAdapter;
    private RecyclerView recyclerView;
    private Marker myLocationMarker;
    private ArrayList<Station> allStations;
    private LocationListener locationListener;
    private LocationManager locationManager;
    private BottomSheetBehavior bottomSheetBehavior;
private NestedScrollView stations_bottom_sheet;
    public static StationsFragment newInstance() {
        return new StationsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.stations_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        indeterminateBar = getView().findViewById(R.id.indeterminateBar);
        indeterminateBar.setVisibility(View.VISIBLE);
        mViewModel = ViewModelProviders.of(this).get(StationsViewModel.class);
        stations_bottom_sheet=getView().findViewById(R.id.stations_bottom_sheet);
        bottomSheetBehavior=BottomSheetBehavior.from(stations_bottom_sheet);

        stationsObserver = new Observer<ArrayList<Station>>(
        ) {
            @Override
            public void onChanged(ArrayList<Station> stations) {
                allStations = stations;
                if (isAdded() && mGoogleMap!=null) {

                    mGoogleMap.clear();
                    if(myLocationMarker!=null)
                        myLocationMarker=mGoogleMap.addMarker(new MarkerOptions().position(myLocationMarker.getPosition()).icon(getBitmapFromVector(getContext(),R.drawable.ic_my_location,getResources().getColor(R.color.red_location))));

                    Random fav = new Random();
                    for (Station station : stations) {
                        boolean favnor = fav.nextBoolean();
                            LatLng latLng = new LatLng(station.getAddress().getLatitude(), station.getAddress().getLongitude());
                            if (favnor) {
                                station_marker = mGoogleMap.addMarker(new MarkerOptions().position(latLng).icon(getBitmapFromVector(getActivity(), R.drawable.ic_pin_favourite, getResources().getColor(R.color.black_100))));
                                stations_markers.put(station_marker, station);
                            } else {
                                station_marker = mGoogleMap.addMarker(new MarkerOptions().position(latLng).icon(getBitmapFromVector(getActivity(), R.drawable.ic_pin_filled, getResources().getColor(R.color.black_80))).alpha(1));
                                stations_markers.put(station_marker, station);
                            }

                    }
                    if (recyclerView != null && myLocationMarker != null) {
                        stationAdapter = new StationAdapter(stations, getContext(), myLocationMarker.getPosition(), getActivity(),bottomSheetBehavior);
                        recyclerView.setAdapter(stationAdapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayout.HORIZONTAL, false));
                        ViewCompat.setNestedScrollingEnabled(recyclerView, false);
                    }

                    indeterminateBar.setVisibility(View.INVISIBLE);

                }
            }
        };

        recyclerView = getView().findViewById(R.id.card_recycler);


    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btn_my_location = getView().findViewById(R.id.btn_my_location);
        btn_my_location.setOnClickListener(this);
        FragmentManager fm = getChildFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) fm.findFragmentByTag("mapFragment");
        if (mapFragment == null) {
            mapFragment = new SupportMapFragment();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.mapFragmentContainer, mapFragment, "mapFragment");
            ft.commit();
            fm.executePendingTransactions();
        }
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                gotoMyLocation(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);


    }

    //Map is ready
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mGoogleMap = googleMap;
        this.mGoogleMap.setOnCameraMoveListener(this);
        this.mGoogleMap.setOnCameraIdleListener(this);
        mGoogleMap.getUiSettings().setMapToolbarEnabled(false);
        mGoogleMap.getUiSettings().setCompassEnabled(true);
        mGoogleMap.setMinZoomPreference(zoomLevel);
        mGoogleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.map_style));
        mGoogleMap.setOnMarkerClickListener(this);

        if (haveNetworkConnection()) {
        } else {
            indeterminateBar.setVisibility(View.INVISIBLE);
            Toast.makeText(getActivity(), "No Internet access ...", Toast.LENGTH_SHORT).show();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: ask for runtime permission
                location_permission_is_granted = false;
                return;
            } else {
                location_permission_is_granted = true;
            }
        }
        mGoogleMap.setMyLocationEnabled(false);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);

        //gotoMyLocation();

    }


    private void gotoMyLocation(Location location) {
        if (location != null)
            if (mGoogleMap != null && isAdded()) {

                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                myLocationMarker = mGoogleMap.addMarker(new MarkerOptions().position(latLng).icon(getBitmapFromVector(getActivity(), R.drawable.ic_my_location, getResources().getColor(R.color.red_location))));
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel);
                mGoogleMap.animateCamera(cameraUpdate);
                locationManager.removeUpdates(locationListener);

            }
          /*  }
        });*/
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }


    @Override
    public void onClick(View v) {
        if (v == btn_my_location && isAdded()) {
            if (isLocationEnabled(getActivity()))
            { if (locationManager != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                            return;
                        }
                    }
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
                 }
            }

            if(!isLocationEnabled(getContext()))
            {
                alertUser();
            }
        }
    }

    private void alertUser() {
     if(isAdded())
          startActivity(new Intent(getActivity(),LocationDialog.class));

    }
//checking weather the user's gps is enabled or not
    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;




    }
//convert vector images to bitmap in order to use as marker icons
public static BitmapDescriptor getBitmapFromVector(@NonNull Context context,
                                                   @DrawableRes int vectorResourceId,
                                                   @ColorInt int tintColor) {

    Drawable vectorDrawable = ResourcesCompat.getDrawable(
            context.getResources(), vectorResourceId, null);
    if (vectorDrawable == null) {
        return BitmapDescriptorFactory.defaultMarker();
    }
    Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
            vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(bitmap);
    vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
   // DrawableCompat.setTint(vectorDrawable);
    vectorDrawable.draw(canvas);
    return BitmapDescriptorFactory.fromBitmap(bitmap);
}
//checking the user connectivity
    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }


    @Override
    public boolean onMarkerClick(Marker marker) {

        if(!marker.equals(myLocationMarker) && isAdded())
        {

            if(last_marker==null)
            {
                marker.setIcon(getBitmapFromVector(getContext(),R.drawable.ic_place_black_24dp,getResources().getColor(R.color.red_location)));
                last_marker=marker;
                Station station=stations_markers.get(marker);
                int current_station=allStations.indexOf(station);
                if(recyclerView!=null){
                    recyclerView.scrollToPosition(current_station);
            }


            }
            else{
                last_marker.setIcon(getBitmapFromVector(getContext(),R.drawable.ic_pin_filled,getResources().getColor(R.color.black_100)));
                marker.setIcon(getBitmapFromVector(getContext(),R.drawable.ic_place_black_24dp,getResources().getColor(R.color.red_location)));
                last_marker=marker;
                Station station=stations_markers.get(marker);
                int current_station=allStations.indexOf(station);
                if(recyclerView!=null){
                    recyclerView.scrollToPosition(current_station);
                }



            }


        }



        return true;
    }



    @Override
    public void onCameraMove() {

    }

    @Override
    public void onCameraIdle() {



        if(!StillInRegion(getRegion()) && isAdded() && haveNetworkConnection())
        {
            indeterminateBar.setVisibility(View.VISIBLE);
           last_marker=null;
          mViewModel.getStations(getRegion().southwest.latitude,getRegion().southwest.longitude,getRegion().northeast.latitude,getRegion().northeast.longitude).observe(getActivity(),stationsObserver);
        }

    }


    public LatLngBounds getRegion(){
        VisibleRegion vr = mGoogleMap.getProjection().getVisibleRegion();
        double left = vr.latLngBounds.southwest.longitude;
        double top = vr.latLngBounds.northeast.latitude;
        double right = vr.latLngBounds.northeast.longitude;
        double bottom = vr.latLngBounds.southwest.latitude;
        return new LatLngBounds(new LatLng(bottom,left),new LatLng(top,right));
    }
    public boolean StillInRegion(LatLngBounds currentBounds)
    {
        if(last_bounds==null){
            last_bounds=currentBounds;
            return false;

        }else {
            if(last_bounds.contains(currentBounds.northeast) && last_bounds.contains(currentBounds.southwest)){
                return true;
            }else{
                last_bounds=currentBounds;
                return false;
            }
        }
    }




}

