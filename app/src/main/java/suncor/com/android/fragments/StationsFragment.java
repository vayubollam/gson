package suncor.com.android.fragments;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.content.Context;
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
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import suncor.com.android.R;
import suncor.com.android.adapters.StationAdapter;
import suncor.com.android.dataObjects.Station;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;


public class StationsFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnCameraIdleListener {

    private StationsViewModel mViewModel;
    private GoogleMap mGoogleMap;
    private MaterialButton btn_my_location;
    private int zoomLevel = 10;
    private HashMap<Marker, Station> stationsMarkers = new HashMap<>();
    private ProgressBar indeterminateBar;
    private StationAdapter stationAdapter;
    private RecyclerView recyclerView;
    private Marker myLocationMarker;
    private LocationListener locationListener;
    private LocationManager locationManager;
    private BottomSheetBehavior bottomSheetBehavior;
    private LinearLayout stations_bottom_sheet;


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
        indeterminateBar = Objects.requireNonNull(getView()).findViewById(R.id.indeterminateBar);
        indeterminateBar.setVisibility(View.VISIBLE);
        mViewModel = ViewModelProviders.of(this).get(StationsViewModel.class);
        stations_bottom_sheet=getView().findViewById(R.id.stations_bottom_sheet);
        stations_bottom_sheet.setOnClickListener(this);
        bottomSheetBehavior=BottomSheetBehavior.from(stations_bottom_sheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        recyclerView = getView().findViewById(R.id.card_recycler);
        LinearSnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

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

    @Override
    public void onResume() {
        super.onResume();
        mViewModel.shouldHideCards.setValue(false);
        mViewModel.stationsAround.observe(getActivity(), this::UpdateCards);
        mViewModel.stillInRegion.observe(getActivity(), aBoolean -> {
            if(!aBoolean){
                indeterminateBar.setVisibility(View.VISIBLE);
                mViewModel.refreshStations(mGoogleMap);
            }
        });

        mViewModel.shouldHideCards.observe(getActivity(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean)
                {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }
        });
        mViewModel.selectedMarker.observe(getActivity(), marker -> {
            if(marker!=null)
            {
                try{
                    marker.setIcon(getBitmapFromVector(getContext(),R.drawable.ic_place_black_24dp,getResources().getColor(R.color.red_location)));


                }catch (Exception ex){
                  ex.printStackTrace();
                }
                 }
        });
        mViewModel.lastMarker.observe(getActivity(), marker -> {
          if(marker!=null)
          {
              try{
                  marker.setIcon(getBitmapFromVector(getContext(),R.drawable.ic_pin_filled,getResources().getColor(R.color.black_100)));
              }catch (Exception ex){
                  ex.printStackTrace();
              }
          }
        });

        mViewModel.stationPosition.observe(getActivity(), position -> {
            if(position!=null)
                recyclerView.scrollToPosition(position);
        });
    }

    //Map is ready
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mGoogleMap = googleMap;
        this.mGoogleMap.setOnCameraIdleListener(this);
        mGoogleMap.getUiSettings().setMapToolbarEnabled(false);
        mGoogleMap.getUiSettings().setCompassEnabled(true);
        mGoogleMap.setMinZoomPreference(zoomLevel);
        mGoogleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.map_style));
        mGoogleMap.setOnMarkerClickListener(this);

        if (!haveNetworkConnection()) {

            indeterminateBar.setVisibility(View.INVISIBLE);
            Toast.makeText(getActivity(), "No Internet access ...", Toast.LENGTH_SHORT).show();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
        }
        mGoogleMap.setMyLocationEnabled(false);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);



    }


    private void gotoMyLocation(Location location) {
        if (location != null)
            if (mGoogleMap != null && isAdded()) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                myLocationMarker = mGoogleMap.addMarker(new MarkerOptions().position(latLng).icon(getBitmapFromVector(getActivity(), R.drawable.ic_my_location, getResources().getColor(R.color.red_location))));
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel);
                mGoogleMap.animateCamera(cameraUpdate);
                locationManager.removeUpdates(locationListener);
                mViewModel.animatingToUserLocation=true;

            }
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
        if(v==stations_bottom_sheet){
           // mViewModel.shouldHideCards.setValue(false);
        }
    }

    private void alertUser() {
     mViewModel.alertUser(getFragmentManager());

    }
//checking weather the user's gps is enabled or not
    public static boolean isLocationEnabled(Context context) {
        int locationMode;
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;




    }
//convert vector images to bitmap in order to use as marker icons
private static BitmapDescriptor getBitmapFromVector(@NonNull Context context,
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
       mViewModel.CheckMarker(marker);
       mViewModel.shouldHideCards.setValue(false);
        return true;
    }



    @Override
    public void onCameraIdle() {
        mViewModel.checkRegion(mGoogleMap);
    }


    public static Marker getMarkerByStation(HashMap<Marker,Station> sa, Station station) {
        for (Marker m : sa.keySet()) {
            if (sa.get(m).equals(station)) {
                return m;
            }
        }
        return null;
    }

    private void UpdateCards(ArrayList<Station> stations) {
        if (isAdded() && mGoogleMap!=null) {

            mGoogleMap.clear();
            stationsMarkers.clear();
            if(myLocationMarker!=null)
                myLocationMarker=mGoogleMap.addMarker(new MarkerOptions().position(myLocationMarker.getPosition()).icon(getBitmapFromVector(getContext(),R.drawable.ic_my_location,getResources().getColor(R.color.red_location))));

            Random fav = new Random();
            for (Station station : stations) {
                boolean favnor = fav.nextBoolean();
                LatLng latLng = new LatLng(station.getAddress().getLatitude(), station.getAddress().getLongitude());
                Marker stationMarker;
                if (favnor) {
                    stationMarker = mGoogleMap.addMarker(new MarkerOptions().position(latLng).icon(getBitmapFromVector(getActivity(), R.drawable.ic_pin_favourite, getResources().getColor(R.color.black_100))));
                    stationsMarkers.put(stationMarker, station);
                } else {
                    stationMarker = mGoogleMap.addMarker(new MarkerOptions().position(latLng).icon(getBitmapFromVector(getActivity(), R.drawable.ic_pin_filled, getResources().getColor(R.color.black_80))).alpha(1));
                    stationsMarkers.put(stationMarker, station);
                }

            }
            mViewModel.stationMarkers.setValue(stationsMarkers);
            if (recyclerView != null && myLocationMarker != null) {
                stationAdapter = new StationAdapter(stations, getContext(),myLocationMarker.getPosition(), getActivity(),bottomSheetBehavior);
                recyclerView.setAdapter(stationAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayout.HORIZONTAL, false));
                ViewCompat.setNestedScrollingEnabled(recyclerView, false);
                recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        LinearLayoutManager linearLayoutManager= (LinearLayoutManager) recyclerView.getLayoutManager();
                        int vi=linearLayoutManager.findFirstCompletelyVisibleItemPosition();
                        if(vi!=RecyclerView.NO_POSITION){
                            Marker marker=getMarkerByStation(stationsMarkers,stations.get(vi));
                            mViewModel.CheckMarker(marker);
                        }


                    }
                });
            }

            indeterminateBar.setVisibility(View.INVISIBLE);

        }
    }





}

