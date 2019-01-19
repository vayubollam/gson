package suncor.com.android.ui.home.stationlocator.search;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import suncor.com.android.LocationLiveData;
import suncor.com.android.R;
import suncor.com.android.data.repository.PlaceSuggestionsProviderImpl;
import suncor.com.android.databinding.NearbyLayoutBinding;
import suncor.com.android.databinding.SearchFragmentBinding;
import suncor.com.android.databinding.SuggestionsLayoutBinding;
import suncor.com.android.model.Resource;
import suncor.com.android.ui.common.FullScreenDialog;
import suncor.com.android.utilities.LocationUtils;

public class SearchDialog extends FullScreenDialog {
    private SearchViewModel viewModel;
    private SearchFragmentBinding binding;
    private NearbyLayoutBinding nearbySearchBinding;
    private SuggestionsLayoutBinding suggestionsLayoutBinding;
    private LatLng userLocation;
    private SearchNearByAdapter nearbyStationsAdapter;
    private SuggestionsAdapter suggestionsAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().getAttributes().windowAnimations = R.style.SearchDialogAnimation;
        getDialog().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getDialog().getWindow().setStatusBarColor(getResources().getColor(R.color.light_gray));

        binding = SearchFragmentBinding.inflate(inflater, container, false);
        nearbySearchBinding = binding.nearbyLayout;
        suggestionsLayoutBinding = binding.suggestionsLayout;

        //instantiating
        GeoDataClient geoDataClient = Places.getGeoDataClient(getContext());
        SearchViewModelFactory viewModelFactory = new SearchViewModelFactory(new PlaceSuggestionsProviderImpl(geoDataClient));
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(SearchViewModel.class);
        binding.setVm(viewModel);
        binding.setLifecycleOwner(this);
        suggestionsLayoutBinding.setLifecycleOwner(this);
        nearbySearchBinding.setLifecycleOwner(this);

        //retrieving views

        //layout manager for recycler views
        suggestionsLayoutBinding.sugestionsRecycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        suggestionsAdapter = new SuggestionsAdapter();
        suggestionsLayoutBinding.sugestionsRecycler.setAdapter(suggestionsAdapter);

        nearbySearchBinding.nearbyRecycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

        //listener
        binding.backButton.setOnClickListener((v) -> {
            dismiss();
        });
        binding.clearButton.setOnClickListener((v) -> {
            binding.addressSearchText.getText().clear();
        });

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
            locationLiveData.observe(this, location -> {
                userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                viewModel.setUserLocation(userLocation);
            });

            viewModel.nearbyStations.observe(this, arrayListResource -> {
                if (arrayListResource.status == Resource.Status.SUCCESS) {
                    ArrayList<StationNearbyItem> stationItems = arrayListResource.data;
                    nearbyStationsAdapter = new SearchNearByAdapter(stationItems, userLocation, getActivity());
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
                //TODO : handle error
            }
        });
    }
}
