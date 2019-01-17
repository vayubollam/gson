package suncor.com.android.ui.home.stationlocator;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
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
import java.util.Random;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import suncor.com.android.LocationLiveData;
import suncor.com.android.R;
import suncor.com.android.SuncorApplication;
import suncor.com.android.model.Resource;
import suncor.com.android.ui.home.stationlocator.search.SearchDialog;
import suncor.com.android.utilities.LocationUtils;


public class StationsFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnCameraIdleListener, GoogleMap.OnCameraMoveStartedListener {

    public static final int STATION_DETAILS_REQUEST_CODE = 1;
    private final static int MINIMUM_ZOOM_LEVEL = 10;

    private StationsViewModel mViewModel;
    private GoogleMap mGoogleMap;
    private AppCompatImageButton findMyLocationButton;
    private HashMap<Marker, StationItem> stationsMarkers = new HashMap<>();
    private ProgressBar indeterminateBar;
    private StationAdapter stationAdapter;
    private RecyclerView recyclerView;
    private Marker myLocationMarker;
    private BottomSheetBehavior bottomSheetBehavior;
    private Marker lastSelectedMarker;
    private float screenRatio;
    private AppCompatTextView txtSearchAddress;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StationViewModelFactory factory = new StationViewModelFactory(SuncorApplication.favouriteRepository);
        mViewModel = ViewModelProviders.of(this, factory).get(StationsViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.stations_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        screenRatio = (float) getResources().getDisplayMetrics().heightPixels / (float) getResources().getDisplayMetrics().widthPixels;
        mViewModel.setRegionRatio(screenRatio);
        indeterminateBar = view.findViewById(R.id.indeterminateBar);
        indeterminateBar.setVisibility(View.VISIBLE);
        txtSearchAddress = getView().findViewById(R.id.txt_search_address);
        txtSearchAddress.setOnClickListener(this);

        recyclerView = view.findViewById(R.id.card_recycler);
        bottomSheetBehavior = BottomSheetBehavior.from(recyclerView);
        stationAdapter = new StationAdapter(this, bottomSheetBehavior);
        recyclerView.setAdapter(stationAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayout.HORIZONTAL, false));
        ViewCompat.setNestedScrollingEnabled(recyclerView, false);
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        findMyLocationButton = view.findViewById(R.id.btn_my_location);
        findMyLocationButton.setOnClickListener(this);
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
    }

    @Override
    public void onResume() {
        super.onResume();

        mViewModel.stationsAround.observe(getActivity(), this::UpdateCards);
        mViewModel.selectedStation.observe(this, station -> {
            if (mViewModel.stationsAround.getValue() == null || mViewModel.stationsAround.getValue().data == null) {
                return;
            }
            ArrayList<StationItem> stations = mViewModel.stationsAround.getValue().data;

            if (stations.contains(station)) {
                recyclerView.scrollToPosition(stations.indexOf(station));
                if (lastSelectedMarker != null) {
                    StationItem oldStation = stationsMarkers.get(lastSelectedMarker);
                    lastSelectedMarker.setIcon(getDrawableForMarker(false, oldStation.isFavourite.get()));
                }

                lastSelectedMarker = findMarkerForStation(station);
                lastSelectedMarker.setIcon(getDrawableForMarker(true, station.isFavourite.get()));

            } else if (lastSelectedMarker != null) {
                StationItem oldStation = stationsMarkers.get(lastSelectedMarker);
                lastSelectedMarker.setIcon(getDrawableForMarker(false, oldStation.isFavourite.get()));
            }
        });

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                if (i == BottomSheetBehavior.STATE_EXPANDED) {
                    refreshSelectedStation();
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            new LocationLiveData(getContext())
                    .observe(this, (this::gotoMyLocation));
        } else {
            //TODO remove this
            AlertDialog.Builder adb = new AlertDialog.Builder(getContext());
            adb.setMessage("Location Permission Not Granted");
            adb.setPositiveButton("OK", null);
            adb.show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!isAdded())
            return;
        if (requestCode == STATION_DETAILS_REQUEST_CODE) {
            if (lastSelectedMarker != null) {
                StationItem item = stationsMarkers.get(lastSelectedMarker);
                lastSelectedMarker.setIcon(getDrawableForMarker(true, item.isFavourite.get()));
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //Map is ready
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mGoogleMap = googleMap;
        this.mGoogleMap.setOnCameraIdleListener(this);
        mGoogleMap.getUiSettings().setMapToolbarEnabled(false);
        mGoogleMap.getUiSettings().setCompassEnabled(true);
        mGoogleMap.setMinZoomPreference(MINIMUM_ZOOM_LEVEL);
        mGoogleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.map_style));
        mGoogleMap.setOnMarkerClickListener(this);
        mGoogleMap.setOnCameraMoveStartedListener(this);
        if (!haveNetworkConnection()) {
            indeterminateBar.setVisibility(View.INVISIBLE);
            Toast.makeText(getActivity(), "No Internet access ...", Toast.LENGTH_SHORT).show();
        }
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
    }


    private void gotoMyLocation(Location location) {
        if (mGoogleMap != null && isAdded()) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            mViewModel.setUserLocation(latLng);
            stationAdapter.setUserLocation(latLng);
            myLocationMarker = mGoogleMap.addMarker(new MarkerOptions().position(latLng).icon(getBitmapFromVector(getActivity(), R.drawable.ic_my_location)));
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(LocationUtils.calculateBounds(latLng, StationsViewModel.DEFAULT_MAP_ZOOM, screenRatio), 0);
            mGoogleMap.animateCamera(cameraUpdate);
            if (getView() != null) {
                getView().postDelayed(() -> {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }, 1000);
            }
        }
    }


    @Override
    public void onClick(View v) {
        if (v == findMyLocationButton && isAdded()) {
            if (LocationUtils.isLocationEnabled()) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    new LocationLiveData(getContext())
                            .observe(this, (this::gotoMyLocation));
                }
            } else {
                alertUser();
            }
        }
        if (v == txtSearchAddress) {
            SearchDialog searchFragment = new SearchDialog();
            searchFragment.show(getFragmentManager(), searchFragment.getTag());

        }
    }

    private void alertUser() {
        LocationDialog dialogFragment = new LocationDialog();
        dialogFragment.show(getFragmentManager(), "location dialog");
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
        if (stationsMarkers.containsKey(marker)) {
            mViewModel.selectedStation.setValue(stationsMarkers.get(marker));
            if (getView() != null) {
                getView().postDelayed(() -> {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }, 200);
            }
            return true;
        }
        return false;
    }


    @Override
    public void onCameraIdle() {
        mViewModel.refreshStations(mGoogleMap.getCameraPosition().target, mGoogleMap.getProjection().getVisibleRegion().latLngBounds);
    }

    @Override
    public void onCameraMoveStarted(int i) {
        mViewModel.selectedStation.setValue(null);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    private void UpdateCards(Resource<ArrayList<StationItem>> result) {
        if (result.status == Resource.Status.LOADING) {
            indeterminateBar.setVisibility(View.VISIBLE);
        } else if (result.status == Resource.Status.SUCCESS) {
            if (isAdded() && mGoogleMap != null) {
                ArrayList<StationItem> stations = result.data;
                mGoogleMap.clear();
                stationsMarkers.clear();
                lastSelectedMarker = null;
                if (myLocationMarker != null)
                    myLocationMarker = mGoogleMap.addMarker(new MarkerOptions().position(myLocationMarker.getPosition()).icon(getBitmapFromVector(getContext(), R.drawable.ic_my_location)));

                Random fav = new Random();
                for (StationItem station : stations) {
                    LatLng latLng = new LatLng(station.station.get().getAddress().getLatitude(), station.station.get().getAddress().getLongitude());
                    boolean isFavourite = station.isFavourite.get();
                    boolean isSelected = mViewModel.selectedStation.getValue() != null && mViewModel.selectedStation.getValue() == station;
                    Marker stationMarker = mGoogleMap.addMarker(new MarkerOptions().position(latLng).icon(getDrawableForMarker(isSelected, isFavourite)));
                    if (isSelected) {
                        lastSelectedMarker = stationMarker;
                    }
                    stationsMarkers.put(stationMarker, station);
                }
                stationAdapter.getStations().clear();
                stationAdapter.getStations().addAll(stations);
                stationAdapter.notifyDataSetChanged();
                recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        refreshSelectedStation();
                    }
                });
                indeterminateBar.setVisibility(View.GONE);
            }
        }
    }

    private void refreshSelectedStation() {
        if (mViewModel.stationsAround.getValue() == null) {
            return;
        }
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int vi = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
        if (vi != RecyclerView.NO_POSITION) {
            mViewModel.selectedStation.setValue(mViewModel.stationsAround.getValue().data.get(vi));
        }
    }

    private BitmapDescriptor getDrawableForMarker(boolean isSelected, boolean isFavourite) {
        int drawable;
        if (isSelected) {
            if (isFavourite) {
                drawable = R.drawable.ic_pin_favourite_selected;//TODO set it red
            } else {
                drawable = R.drawable.ic_pin_selected;
            }
        } else {
            if (isFavourite) {
                drawable = R.drawable.ic_pin_favourite;//TODO set it red
            } else {
                drawable = R.drawable.ic_pin;
            }
        }

        return getBitmapFromVector(getActivity(), drawable);
    }

    private Marker findMarkerForStation(StationItem station) {
        for (Marker m : stationsMarkers.keySet()) {
            if (station.equals(stationsMarkers.get(m))) {
                return m;
            }
        }
        return null;
    }

    //convert vector images to bitmap in order to use as lastSelectedMarker icons
    private static BitmapDescriptor getBitmapFromVector(@NonNull Context context,
                                                        @DrawableRes int vectorResourceId) {

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
}

