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

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ObservableBoolean;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import dagger.android.support.AndroidSupportInjection;
import suncor.com.android.LocationLiveData;
import suncor.com.android.R;
import suncor.com.android.databinding.NearbyLayoutBinding;
import suncor.com.android.databinding.SearchFragmentBinding;
import suncor.com.android.databinding.SuggestionsLayoutBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.model.Resource;
import suncor.com.android.ui.home.stationlocator.StationItem;
import suncor.com.android.ui.home.stationlocator.StationsViewModel;
import suncor.com.android.utilities.LocationUtils;

public class SearchFragment extends Fragment {

    public static final String SEARCH_FRAGMENT_TAG = "Search_Fragment";
    private SearchViewModel viewModel;
    private StationsViewModel parentViewModel;
    private SearchFragmentBinding binding;
    private NearbyLayoutBinding nearbySearchBinding;
    private LatLng userLocation;
    private LocationLiveData locationLiveData;
    private SearchNearByAdapter nearbyStationsAdapter;
    private SuggestionsAdapter suggestionsAdapter;
    private ObservableBoolean recentSearch = new ObservableBoolean();

    @Inject
    ViewModelFactory viewModelFactory;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidSupportInjection.inject(this);
        parentViewModel = ViewModelProviders.of(getActivity()).get(StationsViewModel.class);

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(SearchViewModel.class);

        locationLiveData = new LocationLiveData(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.black_4));

        binding = SearchFragmentBinding.inflate(inflater, container, false);
        nearbySearchBinding = binding.nearbyLayout;
        SuggestionsLayoutBinding suggestionsLayoutBinding = binding.suggestionsLayout;

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

        recentSearch.set(false);
        binding.setRecentSearch(recentSearch);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (LocationUtils.isLocationEnabled()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (getContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
            }
            locationLiveData.observe(this, location -> {
                userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                viewModel.setUserLocation(userLocation);
            });

            viewModel.nearbyStations.observe(this, arrayListResource -> {
                if (arrayListResource.status == Resource.Status.SUCCESS) {
                    ArrayList<StationItem> stationItems = arrayListResource.data;
                    nearbyStationsAdapter = new SearchNearByAdapter(stationItems, (this::nearbyItemClicked));
                    nearbySearchBinding.nearbyRecycler.setAdapter(nearbyStationsAdapter);
                }
            });
        } else {
            nearbySearchBinding.getRoot().setVisibility(View.GONE);
        }
        viewModel.placeSuggestions.observe(this, arrayListResource -> {
            if (arrayListResource.status == Resource.Status.SUCCESS) {
                ArrayList<PlaceSuggestion> suggestions = arrayListResource.data;
                suggestionsAdapter.setSuggestions(suggestions);
            } else if (arrayListResource.status == Resource.Status.ERROR) {
                suggestionsAdapter.clearSuggestions();
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
