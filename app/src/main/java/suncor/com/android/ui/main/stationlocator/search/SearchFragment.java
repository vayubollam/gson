package suncor.com.android.ui.main.stationlocator.search;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import suncor.com.android.LocationLiveData;
import suncor.com.android.databinding.FragmentSearchBinding;
import suncor.com.android.databinding.NearbyLayoutBinding;
import suncor.com.android.databinding.RecentlySearchedLayoutBinding;
import suncor.com.android.databinding.SuggestionsLayoutBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.model.Resource;
import suncor.com.android.ui.main.common.MainActivityFragment;
import suncor.com.android.ui.main.stationlocator.StationItem;
import suncor.com.android.ui.main.stationlocator.StationsViewModel;
import suncor.com.android.utilities.LocationUtils;

public class SearchFragment extends MainActivityFragment {
    private SearchViewModel viewModel;
    private StationsViewModel parentViewModel;
    private FragmentSearchBinding binding;
    private NearbyLayoutBinding nearbySearchBinding;
    private LatLng userLocation;
    private LocationLiveData locationLiveData;
    private SearchNearByAdapter nearbyStationsAdapter;
    private SuggestionsAdapter suggestionsAdapter;
    private ObservableBoolean recentSearch = new ObservableBoolean();
    private ArrayList<String> searchedPlaces  = new ArrayList<>();

    @Inject
    ViewModelFactory viewModelFactory;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parentViewModel = ViewModelProviders.of(requireActivity()).get(StationsViewModel.class);

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(SearchViewModel.class);

        locationLiveData = new LocationLiveData(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        nearbySearchBinding = binding.nearbyLayout;
        SuggestionsLayoutBinding suggestionsLayoutBinding = binding.suggestionsLayout;
        RecentlySearchedLayoutBinding recentlySearchedLayoutBinding = binding.recentlySearchedLayout;
        binding.setVm(viewModel);
        binding.setLifecycleOwner(getActivity());
        recentlySearchedLayoutBinding.setLifecycleOwner(getActivity());
        suggestionsLayoutBinding.setLifecycleOwner(getActivity());
        nearbySearchBinding.setLifecycleOwner(getActivity());

        //retrieving views

        //layout manager for recycler views
        suggestionsLayoutBinding.sugestionsRecycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        suggestionsAdapter = new SuggestionsAdapter(this::placeSuggestionClicked);
        suggestionsLayoutBinding.sugestionsRecycler.setAdapter(suggestionsAdapter);

        recentlySearchedLayoutBinding.recentlySearchedRecycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        RecentlySearchedAdapter recentlySearchedAdapter = new RecentlySearchedAdapter(this::RecentSearchClicked, viewModel.getRecentSearches());
        recentlySearchedLayoutBinding.recentlySearchedRecycler.setAdapter(recentlySearchedAdapter);


        nearbySearchBinding.nearbyRecycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

        binding.backButton.setOnClickListener((v) -> {
            goBack();
        });
        binding.clearButton.setOnClickListener((v) -> {
            binding.addressSearchText.getText().clear();
        });

        binding.addressSearchText.requestFocus();
        showKeyBoard();

        recentSearch.set(false);
        binding.setRecentSearch(recentSearch);

        binding.scrollView.addStickyHeader(binding.recentlySearchedLayout.recentSearchHeader);
        binding.scrollView.addStickyHeader(binding.nearbyLayout.nearbyHeader);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (LocationUtils.isLocationEnabled(getContext()) && ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationLiveData.observe(this, location -> {
                userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                viewModel.setUserLocation(userLocation);
            });

            viewModel.nearbyStations.observe(getViewLifecycleOwner(), arrayListResource -> {
                if (arrayListResource.status == Resource.Status.SUCCESS) {
                    ArrayList<StationItem> stationItems = arrayListResource.data;
                    nearbyStationsAdapter = new SearchNearByAdapter(stationItems, (this::nearbyItemClicked));
                    nearbySearchBinding.nearbyRecycler.setAdapter(nearbyStationsAdapter);
                }
            });
        } else {
            nearbySearchBinding.getRoot().setVisibility(View.GONE);
        }
        viewModel.placeSuggestions.observe(getViewLifecycleOwner(), arrayListResource -> {
            if (arrayListResource.status == Resource.Status.SUCCESS) {
                ArrayList<PlaceSuggestion> suggestions = arrayListResource.data;
                suggestionsAdapter.setSuggestions(suggestions);
            } else if (arrayListResource.status == Resource.Status.ERROR) {
                suggestionsAdapter.clearSuggestions();
            }
        });
    }

    @Override
    protected String getScreenName() {
        return "gas-station-locations-search";
    }

    private void placeSuggestionClicked(PlaceSuggestion placeSuggestion) {
        viewModel.getCoordinatesOfPlace(placeSuggestion)
                .observe(SearchFragment.this, (resouce) -> {
                    if (resouce.status == Resource.Status.SUCCESS) {
                        parentViewModel.setUserLocation(resouce.data, StationsViewModel.UserLocationType.SEARCH);
                        parentViewModel.setTextQuery(placeSuggestion.getPrimaryText());
                        viewModel.addToRecentSearched(new RecentSearch(placeSuggestion.getPrimaryText(), placeSuggestion.getSecondaryText(), resouce.data, placeSuggestion.getPlaceId()));
                        goBack();
                    }
                });
    }

    private void RecentSearchClicked(RecentSearch recentSearch) {
        parentViewModel.setUserLocation(recentSearch.getCoordinate(), StationsViewModel.UserLocationType.SEARCH);
        parentViewModel.setTextQuery(recentSearch.getPrimaryText());
        viewModel.addToRecentSearched(new RecentSearch(recentSearch.getPrimaryText(), recentSearch.getSecondaryText(), recentSearch.getCoordinate(), recentSearch.getPlaceId()));

        goBack();
    }

    private void nearbyItemClicked(StationItem station) {
        if (viewModel.nearbyStations.getValue().status != Resource.Status.SUCCESS) {
            throw new IllegalStateException("to handle clicks, nearby stations should be initialized");
        }
        parentViewModel.setSelectedStationFromSearch(userLocation, viewModel.nearbyStations.getValue().data, station);
        goBack();
    }

    public void goBack() {
        try {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        } catch (NullPointerException ignored) {
        }
        Navigation.findNavController(requireView()).popBackStack();
    }

    private void showKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }
}
