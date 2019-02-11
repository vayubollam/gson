package suncor.com.android.ui.home.stationlocator.favorites;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.google.android.gms.maps.model.LatLng;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.ObservableBoolean;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import suncor.com.android.LocationLiveData;
import suncor.com.android.R;
import suncor.com.android.SuncorApplication;
import suncor.com.android.databinding.FavouritesFragmentBinding;
import suncor.com.android.utilities.LocationUtils;

public class FavouritesFragment extends Fragment {

    private FavouritesViewModel mViewModel;
    public static String FAVOURITES_FRAGMENT_TAG = "FAVOURITES_FRAGMENT";
    private FavouritesAdapter favouritesAdapter;
    private FavouritesFragmentBinding binding;
    private ObservableBoolean isLoading = new ObservableBoolean(true);
    private ObservableBoolean noResult = new ObservableBoolean(false);

    public static FavouritesFragment newInstance() {
        return new FavouritesFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.black_4));
        binding = FavouritesFragmentBinding.inflate(inflater, container, false);
        binding.setIsLoading(isLoading);
        binding.setNoResult(noResult);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.backButton.setOnClickListener(this::goBack);
        binding.favoriteRecycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        favouritesAdapter = new FavouritesAdapter();
        view.requestApplyInsets();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FavouritesViewModelFactory favouritesViewModelFactory = new FavouritesViewModelFactory(SuncorApplication.favouriteRepository);
        mViewModel = ViewModelProviders.of(this, favouritesViewModelFactory).get(FavouritesViewModel.class);
        mViewModel.stations.observe(this, stations -> {
            isLoading.set(false);
            if (stations.size() == 0) {
                noResult.set(true);

            } else {
                binding.favoriteRecycler.setVisibility(View.VISIBLE);
                favouritesAdapter.setActivity(getActivity());
                favouritesAdapter.setStationItems(stations);
                binding.favoriteRecycler.setAdapter(favouritesAdapter);
            }

        });
        LocationLiveData locationLiveData = new LocationLiveData(getActivity());
        if (LocationUtils.isLocationEnabled() && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Observer<Location> locationObserver = new Observer<Location>() {
                @Override
                public void onChanged(Location location) {
                    mViewModel.setUserLocation(new LatLng(location.getLatitude(), location.getLongitude()));
                    favouritesAdapter.setUserLocation(new LatLng(location.getLatitude(), location.getLongitude()));
                    mViewModel.refreshStations();
                    locationLiveData.removeObserver(this);
                }
            };
            locationLiveData.observe(this, locationObserver);
        } else {
            mViewModel.refreshStations();
        }

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    public void goBack(View view) {
        getFragmentManager().popBackStack();
    }


}
