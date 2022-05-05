package suncor.com.android.ui.main.stationlocator.favourites;

import android.Manifest;
import android.animation.AnimatorListenerAdapter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;

import javax.inject.Inject;

import suncor.com.android.LocationLiveData;
import suncor.com.android.R;
import suncor.com.android.SuncorApplication;
import suncor.com.android.analytics.stationlocator.FavouritesAnalytics;
import suncor.com.android.data.DistanceApi;
import suncor.com.android.databinding.FragmentFavouritesBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.model.Resource;
import suncor.com.android.ui.common.SuncorToast;
import suncor.com.android.ui.main.common.MainActivityFragment;
import suncor.com.android.ui.main.stationlocator.StationItem;
import suncor.com.android.utilities.LocationUtils;
import suncor.com.android.utilities.UserLocalSettings;

public class FavouritesFragment extends MainActivityFragment {

    private static final String SHOW_FAVS_HINT = "show_favs_hint";
    private static final String SCREEN_CLASS_NAME = "FavouritesFragment";
    private FavouritesViewModel mViewModel;
    private FavouritesAdapter favouritesAdapter;
    private FragmentFavouritesBinding binding;
    private ObservableBoolean isLoading = new ObservableBoolean(false);
    private LocationLiveData locationLiveData;

    @Inject
    ViewModelFactory viewModelFactory;

    @Inject
    SuncorApplication application;

    @Inject
    UserLocalSettings userLocalSettings;

    @Inject
    DistanceApi distanceApi;

    public static FavouritesFragment newInstance() {
        return new FavouritesFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(this, viewModelFactory).get(FavouritesViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentFavouritesBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(this);
        binding.setEventHandler(this);
        binding.setVm(mViewModel);
        binding.setIsLoading(isLoading);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.appBar.setNavigationOnClickListener((v) -> goBack());
        binding.favouriteRecycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        favouritesAdapter = new FavouritesAdapter(this, distanceApi);
        binding.favouriteRecycler.setAdapter(favouritesAdapter);
        binding.favouriteRecycler.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    favouritesAdapter.closeAllItems();
                }
            }
        });

        locationLiveData = new LocationLiveData(getContext());
        view.requestApplyInsets();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel.stations.observe(this, stations -> {
            isLoading.set(stations.status == Resource.Status.LOADING);
            if(stations.status == Resource.Status.LOADING){

                FavouritesAnalytics.logScreenNameClass(requireContext(),FavouritesAnalytics.SCREEN_NAME_FAV_LOADING,SCREEN_CLASS_NAME);
            }
            if (stations.status == Resource.Status.SUCCESS && !stations.data.isEmpty()) {
                favouritesAdapter.setStationItems(stations.data);

                if (userLocalSettings.getBool(SHOW_FAVS_HINT, true)) {
                    binding.favouriteRecycler.post(() -> {
                        FavouritesAdapter.FavoriteHolder viewHolder = (FavouritesAdapter.FavoriteHolder) binding.favouriteRecycler.findViewHolderForAdapterPosition(0);
                        viewHolder.binding.swipeableLayout.showHint(getResources().getDimensionPixelSize(R.dimen.station_remove_hint_width));
                    });
                    userLocalSettings.setBool(SHOW_FAVS_HINT, false);
                }
            } else if(stations.status == Resource.Status.ERROR) {
                FavouritesAnalytics.logFormError(requireContext(),stations.message  + ", Error on fetch favorite Gas Station");
            }
        });
        if (LocationUtils.isLocationEnabled(getContext())
                && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            isLoading.set(true);
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
    protected String getScreenName() {
        return "my-petro-points-gas-station-locations-favourites";
    }

    public void goBack() {
        Navigation.findNavController(getView()).popBackStack();
    }


    public void removeFavourite(StationItem stationItem) {
        //override recyclerview animation after remove to avoid overriding first loading animation
        binding.favouriteRecycler.setItemAnimator(new Animator());
        mViewModel.removeStation(stationItem).observe(getActivity(), (r) -> {
            if (r.status == Resource.Status.SUCCESS) {
                SuncorToast.makeText(application, R.string.favourites_remove_station_message, Toast.LENGTH_SHORT).show();
            } else if (r.status == Resource.Status.ERROR) {
                SuncorToast.makeText(application, R.string.msg_sl007, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class Animator extends DefaultItemAnimator {

        @Override
        public boolean animateAdd(RecyclerView.ViewHolder holder) {
            final View view = holder.itemView;
            view.setTranslationX(-holder.itemView.getRootView().getWidth());
            final ViewPropertyAnimator animation = view.animate();
            animation.setDuration(getRemoveDuration()).translationX(0).setListener(
                    new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(android.animation.Animator animator) {
                            dispatchAddStarting(holder);
                        }

                        @Override
                        public void onAnimationEnd(android.animation.Animator animator) {
                            animation.setListener(null);
                            view.setTranslationX(0);
                            dispatchAddFinished(holder);
                        }
                    }).start();
            return true;
        }

        @Override
        public boolean animateRemove(RecyclerView.ViewHolder holder) {
            final View view = holder.itemView;
            final ViewPropertyAnimator animation = view.animate();
            animation.setDuration(getRemoveDuration()).translationX(-holder.itemView.getRootView().getWidth()).setListener(
                    new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(android.animation.Animator animator) {
                            dispatchRemoveStarting(holder);
                        }

                        @Override
                        public void onAnimationEnd(android.animation.Animator animator) {
                            animation.setListener(null);
                            view.setTranslationX(0);
                            dispatchRemoveFinished(holder);
                        }
                    }).start();
            return true;
        }
    }
}
