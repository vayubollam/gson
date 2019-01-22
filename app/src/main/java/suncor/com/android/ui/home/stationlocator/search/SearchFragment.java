package suncor.com.android.ui.home.stationlocator.search;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import suncor.com.android.LocationLiveData;
import suncor.com.android.R;
import suncor.com.android.SuncorApplication;
import suncor.com.android.data.repository.PlaceSuggestionsProviderImpl;
import suncor.com.android.databinding.NearbyLayoutBinding;
import suncor.com.android.databinding.SearchFragmentBinding;
import suncor.com.android.databinding.SuggestionsLayoutBinding;
import suncor.com.android.model.Resource;
import suncor.com.android.model.Station;
import suncor.com.android.ui.home.stationlocator.StationViewModelFactory;
import suncor.com.android.ui.home.stationlocator.StationsViewModel;
import suncor.com.android.utilities.LocationUtils;

public class SearchFragment extends Fragment {

    public static final String SEARCH_FRAGMENT_TAG = "Search_Fragment";
    private SearchViewModel viewModel;
    private StationsViewModel parentViewModel;
    private SearchFragmentBinding binding;
    private NearbyLayoutBinding nearbySearchBinding;
    private LatLng userLocation;
    private SearchNearByAdapter nearbyStationsAdapter;
    private SuggestionsAdapter suggestionsAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.black_4));

        binding = SearchFragmentBinding.inflate(inflater, container, false);
        nearbySearchBinding = binding.nearbyLayout;
        SuggestionsLayoutBinding suggestionsLayoutBinding = binding.suggestionsLayout;

        //instantiating
        GeoDataClient geoDataClient = Places.getGeoDataClient(getContext());

        StationViewModelFactory factory = new StationViewModelFactory(SuncorApplication.favouriteRepository);
        parentViewModel = ViewModelProviders.of(getActivity(), factory).get(StationsViewModel.class);

        SearchViewModelFactory viewModelFactory = new SearchViewModelFactory(new PlaceSuggestionsProviderImpl(geoDataClient));
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(SearchViewModel.class);
        binding.setVm(viewModel);
        binding.setLifecycleOwner(getActivity());
        suggestionsLayoutBinding.setLifecycleOwner(getActivity());
        nearbySearchBinding.setLifecycleOwner(getActivity());

        //retrieving views

        //layout manager for recycler views
        suggestionsLayoutBinding.sugestionsRecycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        suggestionsAdapter = new SuggestionsAdapter(this::placeSuggestionClicked);
        suggestionsLayoutBinding.sugestionsRecycler.setAdapter(suggestionsAdapter);

        nearbySearchBinding.nearbyRecycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

        //listener
        binding.backButton.setOnClickListener((v) -> {
            goBack();
        });
        binding.clearButton.setOnClickListener((v) -> {
            binding.addressSearchText.getText().clear();
        });

        binding.addressSearchText.requestFocus();
        showKeyBoard();

        return binding.getRoot();
    }


    @Override
    public void onResume() {
        super.onResume();
        if (LocationUtils.isLocationEnabled()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (getContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
            }
            LocationLiveData locationLiveData = new LocationLiveData(getContext());
            locationLiveData.observe(getActivity(), location -> {
                userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                viewModel.setUserLocation(userLocation);
            });

            viewModel.nearbyStations.observe(getActivity(), arrayListResource -> {
                if (arrayListResource.status == Resource.Status.SUCCESS) {
                    ArrayList<StationNearbyItem> stationItems = arrayListResource.data;
                    nearbyStationsAdapter = new SearchNearByAdapter(stationItems, (this::nearbyItemClicked));
                    nearbySearchBinding.nearbyRecycler.setAdapter(nearbyStationsAdapter);
                }
            });
        } else {
            nearbySearchBinding.getRoot().setVisibility(View.GONE);
        }
        viewModel.placeSuggestions.observe(getActivity(), arrayListResource -> {
            if (arrayListResource.status == Resource.Status.SUCCESS) {
                ArrayList<PlaceSuggestion> suggestions = arrayListResource.data;
                suggestionsAdapter.setSuggestions(suggestions);
            } else if (arrayListResource.status == Resource.Status.ERROR) {
                //TODO : handle error
            }
        });
    }

    private void placeSuggestionClicked(PlaceSuggestion placeSuggestion) {
        viewModel.getCoordinatesOfPlace(placeSuggestion)
                .observe(SearchFragment.this, (resouce) -> {
                    if (resouce.status == Resource.Status.SUCCESS) {
                        parentViewModel.setUserLocation(resouce.data, StationsViewModel.UserLocationType.SEARCH);
                        parentViewModel.setTextQuery(placeSuggestion.getPrimaryText());
                        goBack();
                    }
                });
    }

    private void nearbyItemClicked(Station station) {
        //TODO
//        Observer<Resource<ArrayList<StationItem>>> tempObserver = new Observer<Resource<ArrayList<StationItem>>>() {
//            @Override
//            public void onChanged(Resource<ArrayList<StationItem>> r) {
//                if (r.status != Resource.Status.LOADING) {
//                    if (r.status == Resource.Status.SUCCESS) {
//                        parentViewModel.stationsAround.removeObserver(this);
//                        StationItem selectedStation = null;
//                        for (StationItem item : r.data) {
//                            if (station.getId().equals(item.station.get().getId())) {
//                                selectedStation = item;
//                                break;
//                            }
//                        }
//                        parentViewModel.setSelectedStation(selectedStation);
//                    }
//                    goBack();
//                }
//            }
//        };
//        parentViewModel.setUserLocation(userLocation, StationsViewModel.UserLocationType.GPS);
//        parentViewModel.stationsAround.observe(this, tempObserver);
    }

    public void goBack() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        getFragmentManager().popBackStack();
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.white));

    }

    private void showKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }
}
