package suncor.com.android.ui.home.stationlocator.favorites;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import suncor.com.android.LocationLiveData;
import suncor.com.android.R;
import suncor.com.android.SuncorApplication;
import suncor.com.android.model.Station;
import suncor.com.android.ui.home.stationlocator.StationItem;

public class FavouritesFragment extends Fragment {

    private FavouritesViewModel mViewModel;
    private RecyclerView favouritesRecycler;
    public static String FAVOURITES_FRAGMENT_TAG = "FAVOURITES_FRAGMENT";
    FavouritesAdapter favouritesAdapter;
    AppCompatImageView goBackButton;
    LinearLayout noFavoriteLayout;

    public static FavouritesFragment newInstance() {
        return new FavouritesFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.black_4));
        return inflater.inflate(R.layout.fravorites_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        favouritesRecycler = getView().findViewById(R.id.favoriteRecycler);
        goBackButton = getView().findViewById(R.id.goBackButton);
        noFavoriteLayout = getView().findViewById(R.id.noFavorites);
        goBackButton.setOnClickListener(this::goBack);
        favouritesRecycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        LocationLiveData locationLiveData = new LocationLiveData(getActivity());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
        favouritesAdapter = new FavouritesAdapter();
        locationLiveData.observe(this, new Observer<Location>() {
            @Override
            public void onChanged(Location location) {
                favouritesAdapter.setUserLocation(new LatLng(location.getLatitude(), location.getLongitude()));
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FavouritesViewModelFactory favouritesViewModelFactory = new FavouritesViewModelFactory(SuncorApplication.favouriteRepository);
        mViewModel = ViewModelProviders.of(this, favouritesViewModelFactory).get(FavouritesViewModel.class);
    }

    @Override
    public void onResume() {
        super.onResume();
        mViewModel.stations.observe(this, stations -> {
            stations.clear();
            if (stations.size() == 0) {
                noFavoriteLayout.setVisibility(View.VISIBLE);
                favouritesRecycler.setVisibility(View.GONE);
            } else {


                ArrayList<StationItem> stationItems = new ArrayList<>();
                for (Station station : stations) {
                    StationItem stationItem = new StationItem(station, null);
                    stationItems.add(stationItem);
                }
                favouritesAdapter.setActivity(getActivity());
                favouritesAdapter.setStationItems(stationItems);
                favouritesRecycler.setAdapter(favouritesAdapter);
            }

        });
    }

    public void goBack(View view) {
        getFragmentManager().popBackStack();
    }


}
