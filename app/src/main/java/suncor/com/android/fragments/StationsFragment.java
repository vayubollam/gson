package suncor.com.android.fragments;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import suncor.com.android.R;
import suncor.com.android.Workers.GetSatationsWorker;
import suncor.com.android.Workers.GetStationsService;
import suncor.com.android.dataObjects.Station;
import suncor.com.android.dialogs.LocationDialog;

import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
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
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;


public class StationsFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener, GoogleMap.OnMarkerClickListener {

    private StationsViewModel mViewModel;
    private MapFragment mapFragment;
    private GoogleMap mGoogleMap;
    private FusedLocationProviderClient mFusedLocationClient;
    private MaterialButton btn_my_location;
    private int zoomLevel=10;
    private String result="ALL_STATIONS";
   private Marker station_marker;
   private HashMap<String,Marker> stations_markers=new HashMap<>();
   private String marker_id;
   private ProgressBar indeterminateBar;
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
        indeterminateBar=getView().findViewById(R.id.indeterminateBar);
        indeterminateBar.setVisibility(View.VISIBLE);
        mViewModel = ViewModelProviders.of(this).get(StationsViewModel.class);

        final Observer<ArrayList<Station>> stationsObserver=new Observer<ArrayList<Station>>() {
            @Override
            public void onChanged(ArrayList<Station> stations) {
                Random fav=new Random();
                for(Station station : stations)
                {
                    boolean favnor=fav.nextBoolean();
                    if(mGoogleMap!=null){

                        LatLng latLng=new LatLng(station.getAddress().getLatitude(),station.getAddress().getLongitude());
                        if(favnor)
                        {
                            station_marker= mGoogleMap.addMarker(new MarkerOptions().position(latLng).icon(bitmapDescriptorFromVector(getActivity(),R.drawable.ic_add_location_black_24dp)));
                            stations_markers.put(station_marker.getId(),station_marker);
                        }
                        else
                        {
                            station_marker= mGoogleMap.addMarker(new MarkerOptions().position(latLng).icon(bitmapDescriptorFromVector(getActivity(),R.drawable.ic_pin_filled)).alpha(1));
                            stations_markers.put(station_marker.getId(),station_marker);
                        }
                    }
                }
                indeterminateBar.setVisibility(View.INVISIBLE);

            }
        };

        if(haveNetworkConnection())
               mViewModel.getStations().observe(getActivity(),stationsObserver);
        else{
            indeterminateBar.setVisibility(View.INVISIBLE);
            Toast.makeText(getActivity(), "No Internet access ...", Toast.LENGTH_SHORT).show();
        }
           

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btn_my_location=getView().findViewById(R.id.btn_my_location);
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
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
        }


    }

    //Map is ready
    @Override
    public void onMapReady(GoogleMap googleMap) {
       // Toast.makeText(getActivity(), "Map is ready", Toast.LENGTH_SHORT).show();
        this.mGoogleMap = googleMap;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: ask for runtime permission

                return;
            }
        }
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
        mGoogleMap.getUiSettings().setCompassEnabled(true);
        mGoogleMap.getUiSettings().setMapToolbarEnabled(false);

        mGoogleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.map_style));
        mGoogleMap.setOnMarkerClickListener(this);



        gotoMyLocation();


    }


    private void gotoMyLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
        }
        mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

                if (location != null)
                    if (mGoogleMap != null) {
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel);
                        mGoogleMap.animateCamera(cameraUpdate);
                        mGoogleMap.addMarker(new MarkerOptions().position(latLng));
                    }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onClick(View v) {
        if(v==btn_my_location)
        {
            if(isLocationEnabled(getActivity()))
                 gotoMyLocation();
            else
                alertUser();
        }
    }

    private void alertUser() {

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
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
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
        if(marker_id!=null && marker_id!=marker.getId())
        {
            stations_markers.get(marker_id).setIcon(bitmapDescriptorFromVector(getActivity(),R.drawable.ic_pin_filled));
            marker.setIcon(bitmapDescriptorFromVector(getActivity(),R.drawable.ic_place_black_24dp));
            marker_id=marker.getId();
        }
        marker.setIcon(bitmapDescriptorFromVector(getActivity(),R.drawable.ic_place_black_24dp));
        marker_id=marker.getId();


        return true;
    }
}

