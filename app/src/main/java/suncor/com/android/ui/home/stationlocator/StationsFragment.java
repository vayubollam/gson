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
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.HashMap;

import javax.inject.Inject;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.ViewCompat;
import androidx.databinding.Observable;
import androidx.databinding.ObservableBoolean;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import suncor.com.android.LocationLiveData;
import suncor.com.android.R;
import suncor.com.android.databinding.FragmentStationsBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.mfp.SessionManager;
import suncor.com.android.model.Resource;
import suncor.com.android.model.station.Station;
import suncor.com.android.ui.common.ModalDialog;
import suncor.com.android.ui.enrollment.EnrollmentActivity;
import suncor.com.android.ui.home.common.BaseFragment;
import suncor.com.android.ui.home.stationlocator.favourites.FavouritesFragment;
import suncor.com.android.ui.home.stationlocator.search.SearchFragment;
import suncor.com.android.ui.login.LoginActivity;
import suncor.com.android.utilities.LocationUtils;


public class StationsFragment extends BaseFragment implements GoogleMap.OnMarkerClickListener, GoogleMap.OnCameraIdleListener, GoogleMap.OnCameraMoveStartedListener
        , OnMapReadyCallback {

    public static final String STATIONS_FRAGMENT_TAG = "stations-tag";

    private final static int MINIMUM_ZOOM_LEVEL = 10;

    private StationsViewModel mViewModel;
    private LocationLiveData locationLiveData;
    private GoogleMap mGoogleMap;
    private HashMap<Marker, StationItem> stationsMarkers = new HashMap<>();
    private StationAdapter stationAdapter;
    private Marker myLocationMarker;
    private BottomSheetBehavior bottomSheetBehavior;
    private boolean lockBottomSheet;
    private Marker lastSelectedMarker;
    private float screenRatio;
    private FragmentStationsBinding binding;
    private ObservableBoolean isLoading = new ObservableBoolean(false);
    private ObservableBoolean isErrorCardVisible = new ObservableBoolean(false);

    private boolean userScrolledMap;
    private boolean systemMarginsAlreadyApplied;

    @Inject
    ViewModelFactory viewModelFactory;

    @Inject
    SessionManager sessionManager;

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
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        locationLiveData = new LocationLiveData(getContext(), false);

        mViewModel = ViewModelProviders.of(getActivity(), viewModelFactory).get(StationsViewModel.class);
        isErrorCardVisible.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if (isErrorCardVisible.get()) {
                    lockBottomSheet = true;
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    lockBottomSheet = false;
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentStationsBinding.inflate(inflater, container, false);

        screenRatio = (float) getResources().getDisplayMetrics().heightPixels / (float) getResources().getDisplayMetrics().widthPixels;
        mViewModel.setRegionRatio(screenRatio);

        binding.setVm(mViewModel);
        binding.setEventHandler(this);
        binding.setIsLoading(isLoading);
        binding.setIsErrorCardVisible(isErrorCardVisible);
        binding.setLifecycleOwner(this);

        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet);

        stationAdapter = new StationAdapter(this, bottomSheetBehavior);
        binding.cardRecycler.setAdapter(stationAdapter);
        binding.cardRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayout.HORIZONTAL, false));
        binding.cardRecycler.addOnItemTouchListener(new StationCardTouchListener(this, stationAdapter.getStations(), bottomSheetBehavior));
        binding.cardRecycler.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE && bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    updateSelectedStation();
                }
            }
        });
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(binding.cardRecycler);

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

        //Apply top insets to accomodate for the transparent state
        systemMarginsAlreadyApplied = false;
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            if (!systemMarginsAlreadyApplied) {
                systemMarginsAlreadyApplied = true;
                int systemsTopMargin = insets.getSystemWindowInsetTop();
                ((RelativeLayout.LayoutParams) binding.searchBar.getLayoutParams()).topMargin += systemsTopMargin;
                FrameLayout filtersLayout = binding.filtersLayout;
                filtersLayout.setPadding(filtersLayout.getPaddingLeft(), filtersLayout.getPaddingTop() + systemsTopMargin, filtersLayout.getPaddingRight(), filtersLayout.getPaddingBottom());
            }
            return insets;
        });

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mViewModel.userLocation.getValue() == null || mViewModel.getUserLocationType() == StationsViewModel.UserLocationType.SEARCH) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locateMe(false);
            } else {
                //TODO remove this
                AlertDialog.Builder adb = new AlertDialog.Builder(getContext());
                adb.setMessage("Location Permission Not Granted");
                adb.setPositiveButton("OK", null);
                adb.show();
            }
        }

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                if (lockBottomSheet && i == BottomSheetBehavior.STATE_DRAGGING) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
                if (i == BottomSheetBehavior.STATE_EXPANDED) {
                    updateSelectedStation();
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            getViewLifecycleOwner();
        } catch (Exception e) {
            return;
        }
        this.mGoogleMap = googleMap;
        this.mGoogleMap.setOnCameraIdleListener(this);
        mGoogleMap.getUiSettings().setCompassEnabled(false);
        mGoogleMap.getUiSettings().setMapToolbarEnabled(false);
        mGoogleMap.setMinZoomPreference(MINIMUM_ZOOM_LEVEL);
        mGoogleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.map_style));
        mGoogleMap.setOnMarkerClickListener(this);
        mGoogleMap.setOnCameraMoveStartedListener(this);

        mViewModel.stationsAround.observe(getViewLifecycleOwner(), this::updateCards);
        mViewModel.filters.observe(getViewLifecycleOwner(), this::filtersChanged);
        mViewModel.selectedStation.observe(getViewLifecycleOwner(), station -> {
            if (mViewModel.stationsAround.getValue() == null || mViewModel.stationsAround.getValue().data == null || mViewModel.stationsAround.getValue().data.isEmpty()) {
                return;
            }
            ArrayList<StationItem> stations = mViewModel.stationsAround.getValue().data;

            if (stations.contains(station)) {
                binding.cardRecycler.scrollToPosition(stations.indexOf(station));
                if (lastSelectedMarker != null) {
                    StationItem oldStation = stationsMarkers.get(lastSelectedMarker);
                    lastSelectedMarker.setIcon(getDrawableForMarker(false, oldStation.isFavourite()));
                }

                lastSelectedMarker = findMarkerForStation(station);
                if (lastSelectedMarker != null) {
                    lastSelectedMarker.setIcon(getDrawableForMarker(true, station.isFavourite()));
                }
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            } else {
                if (lastSelectedMarker != null) {
                    StationItem oldStation = stationsMarkers.get(lastSelectedMarker);
                    lastSelectedMarker.setIcon(getDrawableForMarker(false, oldStation.isFavourite()));
                }
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
        mViewModel.userLocation.observe(getViewLifecycleOwner(), (location) -> {
            if (myLocationMarker != null) {
                myLocationMarker.remove();
            }
            if (mViewModel.getUserLocationType() == StationsViewModel.UserLocationType.GPS) {
                myLocationMarker = mGoogleMap.addMarker(new MarkerOptions().position(location).icon(getBitmapFromVector(getActivity(), R.drawable.ic_my_location)));
                stationAdapter.setUserLocation(location);
            } else {
                myLocationMarker = mGoogleMap.addMarker(new MarkerOptions().position(location).icon(getBitmapFromVector(getActivity(), R.drawable.ic_pin_search)));
            }
        });

        mViewModel.mapBounds.observe(getViewLifecycleOwner(), (bounds -> {
            if (!mGoogleMap.getProjection().getVisibleRegion().latLngBounds.equals(bounds)) {
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 0);
                try {
                    mGoogleMap.animateCamera(cameraUpdate);
                } catch (Exception ignored) {
                    //in case the activity is restarted, observers trigger updates before the map is ready
                }
            }
        }));

        mViewModel.queryText.observe(getViewLifecycleOwner(), (text) ->
        {
            binding.addressSearchText.setText(text);
            binding.clearSearchButton.setVisibility(text == null || text.isEmpty() ? View.GONE : View.VISIBLE);
        });
    }

    @Override
    protected boolean isStatusBarTransparent() {
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!isAdded())
            return;
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void gotoMyLocation(Location location) {
        if (mGoogleMap != null && isAdded()) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            mViewModel.setUserLocation(latLng, StationsViewModel.UserLocationType.GPS);
            stationAdapter.setUserLocation(latLng);
            locationLiveData.removeObserver(this::gotoMyLocation);
        }
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
            mViewModel.setSelectedStation(stationsMarkers.get(marker));
            return true;
        }
        return false;
    }

    @Override
    public void onCameraIdle() {
        if (userScrolledMap) {
            LatLngBounds cameraBounds = mGoogleMap.getProjection().getVisibleRegion().latLngBounds;
            mViewModel.setMapBounds(cameraBounds);
            userScrolledMap = false;
        }
    }

    @Override
    public void onCameraMoveStarted(int i) {
        if (i == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
            mViewModel.setSelectedStation(null);
            userScrolledMap = true;
        }
    }

    private void updateCards(Resource<ArrayList<StationItem>> result) {
        binding.bottomSheet.requestLayout();
        if (result.status == Resource.Status.LOADING) {
            isLoading.set(true);
            isErrorCardVisible.set(false);
        } else {
            isLoading.set(false);
            if (result.status == Resource.Status.SUCCESS) {
                if (isAdded() && mGoogleMap != null) {
                    ArrayList<StationItem> stations = result.data;
                    ArrayList<String> currentFilter = mViewModel.filters.getValue();

                    clearMapMarkers();

                    if (stations != null && stations.isEmpty() && currentFilter != null && !currentFilter.isEmpty()) {
                        isErrorCardVisible.set(true);
                    } else {
                        isErrorCardVisible.set(false);
                        for (StationItem station : stations) {
                            LatLng latLng = new LatLng(station.getStation().getAddress().getLatitude(), station.getStation().getAddress().getLongitude());
                            boolean isFavourite = station.isFavourite();
                            boolean isSelected = mViewModel.selectedStation.getValue() != null && mViewModel.selectedStation.getValue().equals(station);
                            Marker stationMarker = mGoogleMap.addMarker(new MarkerOptions().position(latLng).icon(getDrawableForMarker(isSelected, isFavourite)));
                            if (isSelected) {
                                lastSelectedMarker = stationMarker;
                            }
                            stationsMarkers.put(stationMarker, station);
                        }
                        stationAdapter.setStationItems(stations);
                    }
                }
            } else if (result.status == Resource.Status.ERROR) {
                isErrorCardVisible.set(true);
                stationAdapter.getStations().clear();
                clearMapMarkers();
            }
        }
    }

    private void clearMapMarkers() {
        for (Marker marker : stationsMarkers.keySet()) {
            marker.remove();
        }
        stationsMarkers.clear();
        lastSelectedMarker = null;
    }

    private void filtersChanged(ArrayList<String> filterList) {
        if (filterList == null || filterList.isEmpty()) {
            binding.filtersLayout.setVisibility(View.GONE);
        } else {
            binding.filtersLayout.setVisibility(View.VISIBLE);

            binding.filtersChipgroup.removeAllViews();
            for (String amenity : filterList) {
                Chip chip = new Chip(getActivity());
                chip.setText(Station.FULL_AMENITIES.get(amenity));
                chip.setTag(amenity);
                chip.setOnCloseIconClickListener(v -> {
                    ArrayList<String> currentFilters = mViewModel.filters.getValue();
                    currentFilters.remove(v.getTag().toString());
                    mViewModel.setCurrentFilters(currentFilters);
                });
                binding.filtersChipgroup.addView(chip);
            }
            Chip clearFiltersChip = new Chip(getActivity());
            clearFiltersChip.setText(R.string.station_clear_all_filters_chip);
            clearFiltersChip.setTextColor(getResources().getColor(R.color.red));
            clearFiltersChip.setCloseIconVisible(false);
            clearFiltersChip.setOnClickListener((v) -> {
                mViewModel.clearFilters();
            });
            binding.filtersChipgroup.addView(clearFiltersChip);
        }
    }

    private void updateSelectedStation() {
        if (mViewModel.stationsAround.getValue() == null) {
            return;
        }
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) binding.cardRecycler.getLayoutManager();
        int vi = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
        Resource<ArrayList<StationItem>> stationsResource = mViewModel.stationsAround.getValue();
        if (vi != RecyclerView.NO_POSITION && stationsResource.status == Resource.Status.SUCCESS && stationsResource.data.size() > vi) {
            mViewModel.setSelectedStation(mViewModel.stationsAround.getValue().data.get(vi));
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

    public void launchFiltersFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        Fragment filtersFragment = fragmentManager.findFragmentByTag(FiltersFragment.FILTERS_FRAGMENT_TAG);
        if (filtersFragment != null && filtersFragment.isAdded()) {
            return;
        }
        filtersFragment = new FiltersFragment();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.setCustomAnimations(R.anim.slide_up, R.anim.slide_down, R.anim.slide_up, R.anim.slide_down);
        ft.add(android.R.id.content, filtersFragment, FiltersFragment.FILTERS_FRAGMENT_TAG);
        ft.addToBackStack(null);
        ft.commit();
    }

    public void launchSearchFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        Fragment searchFragment = fragmentManager.findFragmentByTag(SearchFragment.SEARCH_FRAGMENT_TAG);
        if (searchFragment != null && searchFragment.isAdded()) {
            return;
        }
        searchFragment = new SearchFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
        ft.add(android.R.id.content, searchFragment, SearchFragment.SEARCH_FRAGMENT_TAG);
        ft.addToBackStack(null);
        ft.commit();
    }

    public void clearSearchText() {
        mViewModel.setTextQuery("");
        locateMe(false);
    }

    //TODO check if we can remove the showDialog parameter
    public void locateMe(boolean showDialog) {
        if (LocationUtils.isLocationEnabled(getContext())) {
            //start by loading only if the current location is not initialized or not GPS
            boolean alreadyHasGPSLocation = mViewModel.userLocation.getValue() != null && mViewModel.getUserLocationType() == StationsViewModel.UserLocationType.GPS;
            isLoading.set(!alreadyHasGPSLocation);
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationLiveData.observe(getViewLifecycleOwner(), this::gotoMyLocation);
            }
        } else {
            AlertDialog.Builder adb = new AlertDialog.Builder(getContext());
            adb.setTitle(R.string.enable_location_dialog_title);
            adb.setMessage(R.string.enable_location_dialog_message);
            adb.setNegativeButton(R.string.cancel, null);
            adb.setPositiveButton(R.string.station_location_alert_settings, (dialog, which) -> {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                dialog.dismiss();
            });
            AlertDialog alertDialog = adb.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        }
    }

    public void showFavourites() {
        if (sessionManager.isUserLoggedIn()) {
            FragmentManager fragmentManager = getFragmentManager();
            Fragment favouritesFragment = fragmentManager.findFragmentByTag(FavouritesFragment.FAVOURITES_FRAGMENT_TAG);
            if (favouritesFragment != null && favouritesFragment.isAdded()) {
                return;
            }
            favouritesFragment = new FavouritesFragment();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.slide_up, R.anim.slide_down, R.anim.slide_up, R.anim.slide_down);
            ft.add(android.R.id.content, favouritesFragment, FavouritesFragment.FAVOURITES_FRAGMENT_TAG);
            ft.addToBackStack(null);
            ft.commit();
        } else {
            ModalDialog dialog = new ModalDialog();
            dialog.setTitle(getString(R.string.login_prompt_title))
                    .setMessage(getString(R.string.login_prompt_message))
                    .setRightButton(getString(R.string.sign_in), (v) -> {
                        startActivity(new Intent(getContext(), LoginActivity.class));
                        dialog.dismiss();
                    })
                    .setCenterButton(getString(R.string.join), (v) -> {
                        startActivity(new Intent(getContext(), EnrollmentActivity.class));
                        dialog.dismiss();
                    })
                    .setLeftButton(getString(R.string.cancel), (v) -> {
                        dialog.dismiss();
                    })
                    .show(getFragmentManager(), ModalDialog.TAG);
        }
    }
}

